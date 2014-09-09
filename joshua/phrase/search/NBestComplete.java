package joshua.phrase.search;

public class NBestComplete {
  Note history;
  long state; // lm::ngram::ChartState* pointer
  float score;
  
  public NBestComplete(Note in_history, long in_state, float in_score) {
  }
}
