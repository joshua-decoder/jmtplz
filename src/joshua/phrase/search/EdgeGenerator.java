package joshua.phrase.search;

import java.util.PriorityQueue;

import joshua.phrase.decode.EdgeOutput;
import joshua.phrase.decode.PartialEdge;
import joshua.phrase.decode.PickBest;

public class EdgeGenerator {

  private PriorityQueue<PartialEdge> generate;
  
  
  public EdgeGenerator() {
    // TODO: add comparator
    generate = new PriorityQueue<PartialEdge>(1);
  }
  
  public void AddEdge(PartialEdge edge) {
    generate.add(edge);
  }

  
  
  public void Search(Object searchContext, EdgeOutput output) {
    // TODO Auto-generated method stub
    
  }

  public void Search(Object searchContext, PickBest output) {
    // TODO Auto-generated method stub
    
  }

}
