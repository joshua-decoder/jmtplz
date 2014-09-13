package joshua.phrase.search;

import java.util.PriorityQueue;

import joshua.decoder.ff.lm.kenlm.jni.KenLM;
import joshua.phrase.lm.ngram.ChartState;
import joshua.phrase.search.SearchContext;
import joshua.phrase.decode.Output;
import joshua.phrase.decode.PartialEdge;

public class EdgeGenerator<Model> {

  private PriorityQueue<PartialEdge> generate;

  public EdgeGenerator() {
    // TODO: does the comparator need to be reversed to put highest-scoring items at the top?
    generate = new PriorityQueue<PartialEdge>(1);
  }

  public void AddEdge(PartialEdge edge) {
    generate.add(edge);
  }

  public boolean Empty() {
    return generate.isEmpty();
  }

  // Pop. If there's a complete hypothesis, return it. Otherwise return an
  // invalid PartialEdge.
  public PartialEdge Pop(SearchContext<Model> context) {
    System.err.println("Pop()");
    assert !generate.isEmpty();
    PartialEdge top = generate.poll();
    PartialVertex[] top_nt = top.NT();
    int arity = top.GetArity();

    int victim = 0;
    int victim_completed = 0;
    int incomplete;
    byte lowest_niceness = (byte) 255;
    // Select victim or return if complete.
    {
      int completed = 0;
      for (int i = 0; i != arity; ++i) {
        if (top_nt[i].Complete()) {
          ++completed;
        } else if (top_nt[i].Niceness() < lowest_niceness) {
          lowest_niceness = top_nt[i].Niceness();
          victim = i;
          victim_completed = completed;
        }
      }
      if (lowest_niceness == 255) {
        return top;
      }
      incomplete = arity - completed;
    }

    PartialVertex old_value = new PartialVertex(top_nt[victim]);
    PartialVertex alternate_changed = new PartialVertex();

    // This is where the lazy trie evaluation happens
    if (top_nt[victim].Split(alternate_changed)) {
      PartialEdge alternate = new PartialEdge(arity, incomplete + 1);
      alternate.SetScore(top.GetScore() + alternate_changed.Bound() - old_value.Bound());

      alternate.SetNote(top.GetNote());

      PartialVertex[] alternate_nt = alternate.NT();
      for (int i = 0; i < victim; ++i)
        alternate_nt[i] = top_nt[i];
      alternate_nt[victim] = alternate_changed;
      for (int i = victim + 1; i < arity; ++i)
        alternate_nt[i] = top_nt[i];

      for (int i = 0; i <= incomplete + 1; i++)
        // memcpy(alternate.Between(), top.Between(),
        // sizeof(lm::ngram::ChartState) * (incomplete + 1));
        top.Between()[i] = alternate.Between()[i];

      generate.add(alternate);
    }

    float before = top.GetScore();

    // top is now the continuation.
    FastScore(context, victim, victim - victim_completed, incomplete, old_value, top);

    // TODO: dedupe?
    generate.add(top);
    assert lowest_niceness != 254 || top.GetScore() == before;

    return new PartialEdge();
  }

  public void Search(SearchContext<Model> context, Output output) {
    int to_pop = context.PopLimit();
    System.err.println("Search(" + to_pop + " " + generate.isEmpty() + ")");
    while (to_pop > 0 && !generate.isEmpty()) {
      PartialEdge got = Pop(context);
      if (got.Valid()) {
        output.NewHypothesis(got);
        --to_pop;
      }
    }
    output.FinishedSearch();
  }

  private void FastScore(SearchContext<Model> context, int victim, int before_idx, int incomplete,
      PartialVertex previous_vertex, PartialEdge update) {
    System.err.println("FastScore()");  

    ChartState[] between = update.Between();
    ChartState before = between[before_idx];
    ChartState after = between[before_idx + 1];

    float adjustment = 0.0f;
    ChartState previous_reveal = previous_vertex.State();
    PartialVertex update_nt = update.NT()[victim];
    ChartState update_reveal = update_nt.State();

    /*
     * // TODO: JNI cut
     * 
     * if ((update_reveal.left.length > previous_reveal.left.length) ||
     * (update_reveal.left.full && !previous_reveal.left.full)) { adjustment +=
     * RevealAfter(context.LanguageModel(), before.left, before.right,
     * update_reveal.left, previous_reveal.left.length); } if
     * ((update_reveal.right.length > previous_reveal.right.length) ||
     * (update_nt.RightFull() && !previous_vertex.RightFull())) { adjustment +=
     * lm::ngram::RevealBefore(context.LanguageModel(), update_reveal.right,
     * previous_reveal.right.length, update_nt.RightFull(), after->left,
     * after->right); } if (update_nt.Complete()) { if (update_reveal.left.full)
     * { before.left.full = true; } else { assert(update_reveal.left.length ==
     * update_reveal.right.length); adjustment +=
     * lm::ngram::Subsume(context.LanguageModel(), before.left, before.right,
     * after.left, after.right, update_reveal.left.length); } before.right =
     * after.right; // Shift the others shifted one down, covering after. for
     * (lm::ngram::ChartState *cover = after; cover < between + incomplete;
     * ++cover) {cover = *(cover + 1); } }
     */
    update.SetScore(update.GetScore() + adjustment * context.LMWeight());
  }
}
