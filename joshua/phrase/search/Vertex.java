package joshua.phrase.search;

import joshua.phrase.decode.PartialEdge;

public class Vertex {

  private VertexNode root;

  public Vertex() {
    root = new VertexNode();
  }

  public VertexNode getRoot() {
    return root;
  }

  public VertexNode Root() {
    return root;
  }

  public boolean Empty() {
    // TODO Auto-generated method stub
    return false;
  }

  public PartialEdge RootAlternate() {
    // TODO Auto-generated method stub
    return null;
  }

  public int Bound() {
    // TODO Auto-generated method stub
    return 0;
  }

}
