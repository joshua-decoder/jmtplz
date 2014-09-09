package joshua.phrase.lm.ngram;

public class ChartState {

  public Left left;
  public Right right;

  public ChartState() {
    left = new Left();
    right = new Right();
  }

}
