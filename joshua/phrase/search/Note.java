package joshua.phrase.search;

import joshua.phrase.decode.Hypothesis;

public class Note {
  public Object value;
  
  public Note() {
  }
  
  public Note(Object value) {
    this.value = value;
  }
  
  public Object get() {
    return value;
  }

  public void set(Object object) {
    this.value = object;
  }
}
