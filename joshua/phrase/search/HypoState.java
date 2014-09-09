package joshua.phrase.search;

import joshua.phrase.lm.ngram.ChartState;

public class HypoState {
  public Note history;
  public ChartState state;
  public float score; // typedef float Score
}
