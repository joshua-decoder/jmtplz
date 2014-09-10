package joshua.phrase.search;

// PORT: done

import joshua.phrase.lm.ngram.ChartState;

public class HypoState {

  public HypoState() {
    history = new Note();
    state = new ChartState();
    score = 0.0f;
  }
    
  // A bit of info for the search to keep track of
  public Note history = null;
  
  // lm::ngram::ChartState* pointer
  public ChartState state = null;

  // typedef float Score
  public float score = 0.0f;
}
