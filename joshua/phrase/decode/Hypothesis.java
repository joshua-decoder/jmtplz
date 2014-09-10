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
  
  public Hypothesis(Right begin_sentence, float score) {
    
  }

  public Hypothesis(Right right, float getScore, Hypothesis cvp, int first,
      int second, Phrase phrase) {
    // TODO Auto-generated constructor stub
  }

  public Coverage GetCoverage() {
    // TODO Auto-generated method stub
    return null;
  }

  public float Score() {
    // TODO Auto-generated method stub
    return 0;
  }

  public Right State() {
    // TODO Auto-generated method stub
    return null;
  }

  public int lastSourceIndex() {
    // TODO Auto-generated method stub
    return 0;
  }

}
