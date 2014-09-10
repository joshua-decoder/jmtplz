package joshua.phrase.decode;

// c++: TODO n-best lists.
public class EdgeOutput {
  
  private Stack stack;

  public EdgeOutput(Stack stack) {
    this.stack = stack;
  }
  
  public void NewHypothesis(PartialEdge complete) {
    StacksShared.AppendToStack(complete, stack);
    
    // todo
  }
  
  public void FinishedSearch() {
    // this is empty
  }

}
