package joshua.phrase.decode;

import java.util.ArrayList;
import java.util.List;

import joshua.decoder.ff.lm.kenlm.jni.KenLM;
import joshua.phrase.search.EdgeGenerator;
import joshua.phrase.search.HypoState;
import joshua.phrase.search.IntPair;
import joshua.phrase.search.Note;
import joshua.phrase.search.Vertex;
import joshua.phrase.search.kPolicy;

// PORT: done (except for resize TODO)

public class Stacks {

  private List<Stack> stacks;
  private Hypothesis end;

  public Stacks(Context context, Chart chart) {
    Future future = new Future(chart);
    stacks = new ArrayList<Stack>();
    // Reservation is critical because pointers to Hypothesis objects are retained as history.
    //stacks.reserve(chart.SentenceLength() + 2 /* begin/end of sentence */);
    stacks.clear();
    for (int i = 0; i < chart.SentenceLength() + 2; i++)
      stacks.add(new Stack());
    // Initialize root hypothesis with <s> context and future cost for everything.
    stacks.get(0).add(new Hypothesis(context.GetScorer().LanguageModel().BeginSentenceState(), future.Full()));
    // Decode with increasing numbers of source words.
    for (int source_words = 1; source_words <= chart.SentenceLength(); ++source_words) {
      Vertices vertices = new Vertices();
      // Iterate over stacks to continue from.
      for (int from = source_words - Math.min(source_words, chart.MaxSourcePhraseLength());
           from < source_words;
           ++from) {
        int phrase_length = source_words - from;
        // Iterate over antecedents in this stack.
        for (Hypothesis ant : stacks.get(from)) {
          System.err.println(ant);
          Coverage coverage = ant.GetCoverage();
          int begin = coverage.firstZero();
          int last_end = Math.min(coverage.firstZero() + context.GetConfig().reordering_limit, chart.SentenceLength());
          int last_begin = (last_end > phrase_length) ? (last_end - phrase_length) : 0;
          // We can always go from first_zero because it doesn't create a reordering gap.
          do {
            TargetPhrases phrases = chart.Range(begin, begin + phrase_length);
            System.err.println(String.format("  Applying target phrases over [%d,%d] = %s", begin, begin + phrase_length, phrases));
            if (phrases == null || !coverage.compatible(begin, begin + phrase_length)) {
              System.err.println(String.format("  - no phrases (%s) or incompatible coverage (%s)", phrases, coverage.compatible(begin, begin+ phrase_length)));
              continue;
            }
            // distortion etc.
            float score_delta = context.GetScorer().transition(ant, phrases, begin, begin + phrase_length);
            // Future costs: remove span to be filled.
            score_delta += future.Change(coverage, begin, begin + phrase_length);
            vertices.Add(ant, begin, begin + phrase_length, score_delta);
          // Enforce the reordering limit on later iterations.
          } while (++begin <= last_begin);
        }
      }
      EdgeGenerator<KenLM> gen = new EdgeGenerator<KenLM>();
      vertices.Apply(chart, gen);
      //stacks.resize(stacks_.size() + 1); // todo
      //stacks.back().reserve(context.SearchContext().PopLimit()); todo
      EdgeOutput output = new EdgeOutput(stacks.get(stacks.size()-1));
      gen.Search(context.SearchContext(), output);
    }
    PopulateLastStack(context, chart);
  }
  
  private void PopulateLastStack(Context context, Chart chart) {
    // First, make Vertex of all hypotheses
    Vertex all_hyps = new Vertex();
    for (Hypothesis ant : stacks.get(chart.SentenceLength())) {
      // TODO: the zero in the following line assumes that EOS is not scored for distortion. 
      // This assumption might need to be revisited.
      Stacks.AddHypothesisToVertex(ant, 0, all_hyps);
    }
    
    // Next, make Vertex which consists of a single EOS phrase.
    // The search algorithm will attempt to find the best hypotheses in the "cross product" of these two sets.
    // TODO: Maybe this should belong to the phrase table.  It's constant.
    Vertex eos_vertex = new Vertex();
    HypoState eos_hypo = new HypoState();
    Phrase eos_phrase = new Phrase("</s>");

    eos_hypo.history.set(eos_phrase);
    eos_hypo.score = context.GetScorer().LM(eos_phrase.getWords(), eos_hypo.state);
    eos_vertex.Root().AppendHypothesis(eos_hypo);
    eos_vertex.Root().FinishRoot(kPolicy.Left);

    // Add edge that tacks </s> on
    EdgeGenerator<KenLM> gen = new EdgeGenerator<KenLM>();
    Note note = new Note(new IntPair(chart.SentenceLength(), chart.SentenceLength()));
    Stacks.AddEdge(all_hyps, eos_vertex, note, gen);

    //stacks.resize(stacks_.size() + 1); // todo
    PickBest output = new PickBest(stacks.get(stacks.size()-1));
    gen.Search(context.SearchContext(), output);

    end = stacks.get(stacks.size()-1).isEmpty() ? null : stacks.get(stacks.size()-1).get(0);  
  }

  public Hypothesis End() {
    return end;
  }
  
  public static void AddHypothesisToVertex(Hypothesis hypothesis, float score_delta, Vertex vertex) {
    HypoState add = new HypoState();
    add.history.set(hypothesis);
    add.state.right = hypothesis.State();
    add.state.left.length = 0;
    add.state.left.full = true;
    add.score = hypothesis.Score() + score_delta;
    vertex.Root().AppendHypothesis(add);  
  }

  public static void AddEdge(Vertex hypos, Vertex extensions, Note note, EdgeGenerator out) {
    hypos.Root().FinishRoot(kPolicy.Right);
    if (hypos.Empty()) return;
    PartialEdge edge = new PartialEdge();
    // Empty LM state before/between/after
    for (int j = 0; j < 3; ++j) {
      edge.Between()[j].left.length = 0;
      edge.Between()[j].left.full = false;
      edge.Between()[j].right.length = 0;
    }
    edge.SetNote(note);
    // Include top scores.
    edge.SetScore((hypos.Bound() + extensions.Bound()));
    edge.NT()[0] = hypos.RootAlternate();
    edge.NT()[1] = extensions.RootAlternate();
    out.AddEdge(edge);
  }

  public static void AppendToStack(PartialEdge complete, Stack out) {
    IntPair source_range = (IntPair)complete.GetNote().get();
    // The note for the first NT is the hypothesis.  The note for the second
    // NT is the target phrase.
    out.add(new Hypothesis(complete.CompletedState().right,
          complete.GetScore(), // TODO: call scorer to adjust for last of lexro?
          (Hypothesis)(complete.NT()[0].End().get()),
          source_range.first,
          source_range.second,
          (Phrase)complete.NT()[1].End().get()));
  }
}
