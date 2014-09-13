package joshua.phrase.decode;

import java.util.HashMap;
import java.util.Map;

import joshua.phrase.search.EdgeGenerator;
import joshua.phrase.search.IntPair;
import joshua.phrase.search.Note;
import joshua.phrase.search.Vertex;

// PORT: done

public class Vertices {
  
  // c++: TODO: dense as 2D array?
  // c++: Key is start and end
  private Map<IntPair, Vertex> map = new HashMap<IntPair, Vertex>(); // unoptimized

  public void Add(Hypothesis hypothesis, int source_begin, int source_end, float score_delta) {
    IntPair key = new IntPair(source_begin, source_end);
    Stacks.AddHypothesisToVertex(hypothesis, score_delta, map.get(key));
    
    System.err.println(String.format("Adding to vertex %s: %s (%.5f)", key, hypothesis, score_delta));
  }

  public void Apply(Chart chart, EdgeGenerator out) {
    for (IntPair pair : map.keySet()) {
      Note note = new Note(pair);
      Stacks.AddEdge(map.get(pair), chart.Range(pair.first, pair.second).getVertex(), note, out);
    }
  }
}
