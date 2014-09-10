package joshua.phrase.decode;

import joshua.FeatureVector;

import joshua.decoder.ff.lm.kenlm.jni.KenLM;
import joshua.phrase.lm.ngram.ChartState;

public class Scorer {
  
  private FeatureVector weights;
  private KenLM model;
  
  public Scorer(String lm_file, String weights_file) {
    weights = new FeatureVector();
    weights.readFromFile(weights_file);
    
    this.model = new KenLM(3, lm_file, true);
  }
  
  public float parse(String features) {
    int index = 0;
    float sum = 0.0f;
    for (String valuestr: features.split(" ")) {
      float value = Float.parseFloat(valuestr);
      sum += value * weights.get(String.format("tm_%d", index));
      index++;
    }
    
    return sum;
  }

  public FeatureVector getWeights() {
    return weights;
  }
  
  public KenLM languageModel() {
    return model;
  }
  
  public float LM(int is, ChartState state) {
    return LM(new int[] { is }, state);
  }
  
  public float LM(int[] is, ChartState state) {
    return model.prob(is);
  }

  public float TargetWordCount(int num_words) {
    return weights.get("target_word_insertion");
  }
  
  public float transition(Hypothesis hypothesis, TargetPhrases phrases, int source_begin, int source_end) {
    int jump_size = Math.abs(hypothesis.lastSourceIndex() - source_begin);
    return (jump_size * weights.get("distortion"));
  }

  public float passThrough() {
    return -100.0f;
  }
}
