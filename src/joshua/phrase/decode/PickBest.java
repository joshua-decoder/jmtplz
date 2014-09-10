package joshua.phrase.decode;

// complete

public class PickBest {
  
  private Stack stack;
  private PartialEdge best;

  public PickBest(Stack stack) {
    this.stack = stack;
    this.stack.clear();
    this.stack.reserve(1);
    this.best = new PartialEdge();
  }
  
  public void NewHypothesis(PartialEdge complete) {
    if (!best.Valid() || complete.compareTo(best) > 0) {
      best = complete;
    }
  }
  
  public void FinishedSearch() {
    if (best.Valid())
      Stacks.AppendToStack(best, stack);
  }

}
