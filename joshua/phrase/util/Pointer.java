package joshua.phrase.util;

public class Pointer {
  public Pool pool;
  public int offset;
  
  public Pointer(Pool pool, int offset) {
    this.pool = pool;
    this.offset = offset;
  }
}
