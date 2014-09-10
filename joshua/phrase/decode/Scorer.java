package joshua.phrase.decode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joshua.Vocabulary;
import joshua.phrase.lm.ngram.ChartState;
import joshua.phrase.lm.ngram.Model;

public class Scorer {
  
  private Weights weights;
  private Model model;
  private Map<Long, Integer> vocab_mapping;
  private Vocabulary vocab;

  public Scorer(String lm, String weights_file, Vocabulary vocab) throws Exception {
    this.model = new Model(lm);
    this.vocab = vocab;
    this.weights.ReadFromFile(weights_file);
    this.vocab_mapping = new HashMap<Long, Integer>();
  }

  public Model LanguageModel() {
    return model;
  }

  public float parse(String features) throws Exception {
    List<Float> phrase_weights = weights.PhraseTableWeights();
    float ret = 0;
    int w = 0;
    for (String token : features.split(" ")) {
      float val = Weights.kConverter(token);
      if (Float.isNaN(val)) {
        throw new Exception("Bad score: " + token);
      }
      if (w == phrase_weights.size()) {
        throw new Exception("Have " + phrase_weights.size() + " features but was given feature vecotr " + features);
      }
      ret += w * val; 
      w++;
    }
    if (w != phrase_weights.size()) {
      throw new Exception("Expected " + phrase_weights.size() + " features, but got " + features);
    }
    return ret;
  }

  public float LM(long[] words, ChartState state) {
    //RuleScore<Model> scorer = new Scorer(model, state);
    for (long id : words) {
      Convert(id);
      //scorer.Terminal(Convert(id));
    }
    //return weights.LMWeight() * scorer.Finish();
    return 0; // todo
  }

  public float TargetWordCount(int num_words) {
    return weights.TargetWordInsertionWeight() * num_words;
  }

  public float Transition(Hypothesis hypothesis, TargetPhrases phrases, int source_begin, int source_end) {
    int jump_size = Math.abs(hypothesis.LastSourceIndex() - source_begin);
    return jump_size * weights.DistortionWeight();
  }
  
  // c++: TODO: configurable
  public float Passthrough() {
    return -100;
  }
  
  private int Convert(long from) {
    if (from >= vocab_mapping.size()) {
      while (vocab_mapping.size() < vocab.Size()) {
        // todo
        //vocab_mapping.add(model.GetVocabulary().Index(vocab.String(vocab_mapping.size())))
      }
    }
    return vocab_mapping.get(from);
  }

}
