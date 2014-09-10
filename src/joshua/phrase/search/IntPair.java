package joshua.phrase.search;

// PORT: done

/***
 * Represents a range of the input sentence. 
 */
public class IntPair {
  public int first;
  public int second;
  
  public IntPair(int begin, int end) {
    this.first = begin;
    this.second = end;
  }
}
