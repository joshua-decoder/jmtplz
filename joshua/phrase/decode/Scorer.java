package joshua.phrase.decode;

import joshua.phrase.lm.ngram.ChartState;
import joshua.phrase.lm.ngram.Model;

public class Scorer {

  public Scorer() {
  }
  
  public Model LanguageModel() {
    return null;
  }

  public float parse(String[] features) {
    return 0; // todo
  }

  public float LM(long[] words, ChartState state) {
    return 0; // todo
  }

  public float targetWordCount(int num_words) {
    return 0; // todo
  }

  public float Transition(Hypothesis ant, TargetPhrases phrases, int begin, int i) {
    // TODO Auto-generated method stub
    return 0;
  }

}
