package joshua.phrase.search;

import joshua.phrase.lm.ngram.ChartState;

// PORT: done

public class NBestComplete {
  Note history;
  ChartState state; // lm::ngram::ChartState* pointer
  float score;
  
  public NBestComplete(Note in_history, ChartState in_state, float in_score) {
  }
}
