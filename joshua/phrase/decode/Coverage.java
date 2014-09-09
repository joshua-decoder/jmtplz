package joshua.phrase.decode;

public class Coverage {
  private int firstZero;
  private long bits;
  
  public Coverage() {
    firstZero = 0;
    bits = 0;
  }
  
  public void set(int begin, int end) {
    assert compatible(begin, end);
    if (begin == firstZero) {
      firstZero = end;
      bits >>= (end - begin);
      while ((bits & 1) != 0) {
        ++firstZero;
        bits >>= 1;
      }
    } else {
      bits |= pattern(begin, end);
    }
    
  }
  
  public boolean compatible(int begin, int end) {
    return (begin >= firstZero) && ((pattern(begin, end) & bits) == 0L);
  }

  public int firstZero() {
    return firstZero;
  }
  
  public int leftOpen(int begin) {
    for (int i = begin - firstZero; i > 0; --i) {
      if (((bits & (1L << i)) != 0)) {
        assert compatible(i + firstZero + 1, begin);
        assert ! compatible(i + firstZero, begin);
        return i + firstZero + 1;
      }
    }
    
    assert compatible(firstZero, begin);
    return firstZero;
  }
  
  public int rightOpen(int end, int sentenceLength) {
    for (int i = end - firstZero; i < Math.min(64, sentenceLength - firstZero); i++) {
      if ( (bits & (1L << i)) != 0) {
        return i + firstZero;
      }
    }
    return sentenceLength;
  }
  
  public long pattern(int begin, int end) {
    assert begin >= firstZero;
    assert end - firstZero < 64;
    return (1L << (end - firstZero)) - (1L << (begin - firstZero));
  }
  
}
