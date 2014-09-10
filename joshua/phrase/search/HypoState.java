package joshua.phrase.search;

import joshua.phrase.decode.Phrase;
import joshua.phrase.lm.ngram.ChartState;

public class HypoState {
  public Note history = null;
  public ChartState state = null; // lm::ngram::ChartState* pointer
  public float score = 0.0f; // typedef float Score
}
