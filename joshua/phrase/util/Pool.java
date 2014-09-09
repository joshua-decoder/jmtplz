package joshua.phrase.util;

import java.util.Vector;

public class Pool<T> {
  private Vector<T> pool;
  
  public Pool() {
    pool = new Vector<T>();
  }
  
  public Pointer Allocate(int size) {
    int offset = pool.size();
//    for (int i = 0; i < size; i++) {
//      pool.add(new T());
//    }
    
    return new Pointer(this, offset);
  }
  
  public Pointer Continue(int start, int size) {
//    while (pool.size() < start + size)
//      pool.add((Object)new java.lang.Object());
    
    return new Pointer(this, pool.size());
  }
  
  public Object Get(int offset) {
    return pool.get(offset);
  }
  
  public class Pointer {
    public Pool pool;
    public int offset;
    
    public Pointer(Pool pool, int offset) {
      this.pool = pool;
      this.offset = offset;
    }
    
    public Object Star() {
      return pool.Get(offset);
    }
  }

}
