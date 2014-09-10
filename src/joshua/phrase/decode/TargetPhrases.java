package joshua.phrase.decode;

import joshua.phrase.search.HypoState;
import joshua.phrase.search.Vertex;

public class TargetPhrases {

  private Vertex vertex;

  public TargetPhrases() {
    vertex = new Vertex();
  }

  public Vertex getVertex() {
    return vertex;
  }

  public void MakePassThrough(Scorer scorer, int word) {
    Phrase target = new Phrase(word);
    HypoState hypo = new HypoState();
    hypo.history.set(target);
    hypo.score = scorer.passThrough()
        + scorer.LM(word, hypo.state) 
        + scorer.TargetWordCount(1);
    
  }
  
}
