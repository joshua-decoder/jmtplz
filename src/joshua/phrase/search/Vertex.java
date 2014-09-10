package joshua.phrase.search;

// PORT: done

public class Vertex {

  private VertexNode root;

  public Vertex() {
    root = new VertexNode();
  }
  
  public PartialVertex RootAlternate() {
    return new PartialVertex(root);
  }

  public boolean Empty() {
    return root.Empty();
  }
  
  public float Bound() {
    return root.getBound();
  }
  
  public Note BestChild() {
    PartialVertex top = new PartialVertex(RootAlternate());
    if (top.Empty()) {
      return new Note();
    } else {
      PartialVertex continuation = new PartialVertex();
      while (! top.Complete()) {
        top.Split(continuation);
      }
      return top.End();
    }
  }
  
  public VertexNode getRoot() {
    return root;
  }

  public VertexNode Root() {
    return root;
  }
}
