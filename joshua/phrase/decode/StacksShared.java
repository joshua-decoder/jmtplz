package joshua.phrase.decode;

import joshua.phrase.search.HypoState;
import joshua.phrase.search.IntPair;
import joshua.phrase.search.Note;
import joshua.phrase.search.Vertex;
import joshua.phrase.search.kPolicy;

public class StacksShared {
  
  public static void AddHypothesisToVertex(Hypothesis hypothesis, float score_delta, Vertex vertex) {
    HypoState add = new HypoState();
    add.history.cvp = hypothesis;
    add.state.right = hypothesis.State();
    add.state.left.length = 0;
    add.state.left.full = true;
    add.score = hypothesis.Score() + score_delta;
    vertex.Root().appendHypothesis(add);  
  }

  public static void AddEdge(Vertex hypos, Vertex extensions, Note note, EdgeGenerator out) {
    hypos.Root().finishRoot(kPolicy.Right);
    if (hypos.Empty()) return;
    PartialEdge edge = new PartialEdge(out.AllocateEdge(2));
    // Empty LM state before/between/after
    for (int j = 0; j < 3; ++j) {
      edge.Between()[j].left.length = 0;
      edge.Between()[j].left.full = false;
      edge.Between()[j].right.length = 0;
    }
    edge.SetNote(note);
    // Include top scores.
    edge.SetScore(hypos.Bound() + extensions.Bound());
    edge.NT()[0] = hypos.RootAlternate();
    edge.NT()[1] = extensions.RootAlternate();
    out.AddEdge(edge);
  }

  public static void AppendToStack(PartialEdge complete, Stack out) {
    IntPair source_range = complete.GetNote().ints;
    // The note for the first NT is the hypothesis.  The note for the second
    // NT is the target phrase.
    out.add(new Hypothesis(complete.CompletedState().right,
          complete.GetScore(), // TODO: call scorer to adjust for last of lexro?
          (Hypothesis)complete.NT()[0].End().cvp,
          source_range.first,
          source_range.second,
          new Phrase(complete.NT()[1].End().cvp)));
  }

}
