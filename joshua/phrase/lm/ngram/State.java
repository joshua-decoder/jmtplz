package joshua.phrase.lm.ngram;

public interface State {
  byte getLength();

  long identify(byte index);
}
