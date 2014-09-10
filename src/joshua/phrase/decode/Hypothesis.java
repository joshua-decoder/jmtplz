package joshua.phrase.decode;

import joshua.phrase.lm.ngram.Right;

// complete

public class Hypothesis {
  
  private float score;
  private Right state;
  private Hypothesis pre;
  private int last_source_index;
  private Phrase target;
  private Coverage coverage;
  
  public Hypothesis() {
    this.score = 0;
    this.state = new Right();
    this.last_source_index = 0;
    this.target = null;
    this.coverage = new Coverage();
  }
  
  // Initialize root hypothesis.  Provide the LM's BeginSentence.
  public Hypothesis(Right begin_sentence, float score) {
    this.score = score;
    this.state = begin_sentence;
    this.pre = null;
    this.last_source_index = 0;
    this.target = null;
    this.coverage = new Coverage();
  }

  // Extend a previous hypothesis.
  public Hypothesis(Right state, float score, Hypothesis previous, int source_begin, int source_end, Phrase target) {
    this.score = score;
    this.state = state;
    this.pre = previous;
    this.last_source_index = source_end;
    this.target = target;
    this.coverage = previous.coverage;
    this.coverage.Set(source_begin, source_end);
  }

  public Coverage GetCoverage() {
    return coverage;
  }

  public float Score() {
    return score;
  }

  public Right State() {
    return state;
  }
  
  public int LastSourceIndex() {
    return last_source_index;
  }
  
  public Hypothesis Previous() {
    return pre;
  }
  
  public Phrase Target() {
    return target;
  }

  public int lastSourceIndex() {
    // TODO Auto-generated method stub
    return 0;
  }

}
