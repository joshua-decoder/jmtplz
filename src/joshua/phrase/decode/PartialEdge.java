package joshua.phrase.decode;

// PORT: done

import joshua.phrase.lm.ngram.ChartState;
import joshua.phrase.search.Note;
import joshua.phrase.search.PartialVertex;

public class PartialEdge extends Header {

  private PartialVertex[] vertex;
  private ChartState[] between;
  
  public PartialEdge() {
    super();
  }
  
  public PartialEdge(int num_vertices) {
    super(num_vertices);
    
    vertex = new PartialVertex[num_vertices];
  }
  
  public PartialEdge(int num_vertices, int num_states) {
    super(num_vertices);

    vertex = new PartialVertex[num_vertices];
    
    
    between = new ChartState[num_states];
  }

  public PartialVertex[] NT() {
    return vertex;
  }

  public ChartState CompletedState() {
    return between[0];
  }
  
  public ChartState[] Between() {
    return between;
  }

  public Note End() {
    // TODO Auto-generated method stub
    return null;
  }
}
