package joshua.phrase.decode;

import joshua.phrase.search.Note;

public class Header {
  private float score;
  private int arity;
  private Note note;
  
  public boolean Valid() {
    //return base_;
    return false;
  }
  
  public float GetScore() {
    return score;
  }
  
  public void SetScore(float score) {
    this.score = score;
  }
  
  public boolean lessThan(Header other) {
    return this.GetScore() < other.GetScore();
  }
  
  public boolean greaterThan(Header other) {
    return this.GetScore() > other.GetScore();
  }
  
  public int GetArity() { return arity; }
  
  public Note GetNote() { return note; }
  
  public void SetNote(Note note) { this.note = note; }
  
  protected Header() {
    score = 0.0f;
    arity = 0;
    note = null;
  }
  
  protected Header(Header other) {
    this.score = other.GetScore();
    this.arity = other.GetArity();
    this.note = other.GetNote();
  }
  
  protected Header(int arity) {
    this.score = 0.0f;
    this.arity = arity;
    this.note = null;
  }
}
