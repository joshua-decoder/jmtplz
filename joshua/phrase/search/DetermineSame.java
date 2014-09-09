package joshua.phrase.search;

import joshua.phrase.lm.ngram.State;

public class DetermineSame<TSide extends State> {

  private TSide side;
  private byte guaranteed;
  private byte shared;
  private boolean complete;

  public DetermineSame(TSide side, byte guaranteed) {
    this.side = side;
    this.guaranteed = guaranteed;
    this.shared = side.getLength();
    this.complete = true;
  }

  public byte getShared() {
    return shared;
  }

  public boolean isComplete() {
    return complete;
  }

  public void consider(TSide other) {
    if (shared != other.getLength()) {
      complete = false;
      if (shared > other.getLength())
        shared = other.getLength();
    }
    for (byte i = guaranteed; i < shared; i++) {
      if (side.identify(i) != other.identify(i)) {
        shared = i;
        complete = false;
        return;
      }
    }
  }

}
