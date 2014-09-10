package joshua.phrase.search;

// PORT: done

import joshua.phrase.lm.ngram.ChartState;

public class PartialVertex {

  VertexNode back;
  int index;

  public PartialVertex() {}
  
  public PartialVertex(VertexNode back) {
    this.back = back;
    index = 0;
  }
  
  public PartialVertex(PartialVertex other) {
    this.back = other.back;
    this.index = other.index;
  }

  public boolean Empty() {
    return back.Empty();
  }
  
  public boolean Complete() {
    return back.complete();
  }

  public ChartState State() {
    return back.State();
  }

  public boolean RightFull() { 
    return back.RightFull(); 
  }

  public float Bound() {
    return index > 0 ? back.get(index).getBound() : back.getBound();
  }

  public byte Niceness() { 
    return back.Niceness(); 
  }

      // Split into continuation and alternative, rendering this the continuation.
  public boolean Split(PartialVertex alternative) {
    assert(!Empty() && !Complete());
    back.BuildExtend();
    boolean ret;
    if (index + 1 < back.size()) {
      alternative.index = index + 1;
      alternative.back = back;
      ret = true;
    } else {
      ret = false;
    }
    back = back.get(index);
    index = 0;
    return ret;
  }

  public Note End() {
    return back.End();
  }
}


