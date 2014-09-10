package joshua.phrase.decode;

import joshua.JoshuaConfiguration;
import joshua.Vocabulary;
import joshua.phrase.util.SlowHashMapVocabulary;

public class Context {
  
  private Vocabulary vocab;
  private Scorer scorer;
  private JoshuaConfiguration config;
  private Object search_context; // search::Context<Sorer::Model>

  public Context(String lm, String weights_file, JoshuaConfiguration config) {
    this.vocab = new SlowHashMapVocabulary();
    this.scorer = new Scorer(lm, weights_file, this.vocab);
    this.config = new JoshuaConfiguration();
    //this.search_context = new SearchContext(); search_context_(search::Config(scorer_.GetWeights().LMWeight(), config.pop_limit, search::NBestConfig(1)), scorer_.LanguageModel()) {}
  }
  
  public Scorer GetScorer() {
    return scorer;
  }

  public JoshuaConfiguration GetConfig() {
    return config;
  }

  public Object SearchContext() {
    return search_context;
  }

  public Vocabulary GetVocab() {
    return vocab;
  }

}
