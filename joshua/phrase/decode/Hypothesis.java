package joshua.phrase.decode;

import joshua.phrase.lm.ngram.Right;

public class Hypothesis {

  private float score;
  private Right state;
  private Hypothesis pre;
  private Phrase target;
  private Coverage coverage;
  
  public Hypothesis() {

  }

  public int lastSourceIndex() {
    // TODO Auto-generated method stub
    return 0;
  }

}
