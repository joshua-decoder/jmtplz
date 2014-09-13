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
    return root.Bound();
  }
  
  public VertexNode getRoot() {
    return root;
  }

  public VertexNode Root() {
    return root;
  }
}
