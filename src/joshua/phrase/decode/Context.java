package joshua.phrase.decode;

// PORT: done

import joshua.JoshuaConfiguration;

import joshua.decoder.ff.lm.kenlm.jni.KenLM;
import joshua.phrase.search.SearchContext;

public class Context {

  private Scorer scorer;
  private JoshuaConfiguration config;
  private SearchContext<KenLM> searchContext;

  public Context(String lm, String weights_file, JoshuaConfiguration config) throws Exception {
    this.scorer = new Scorer(lm, weights_file);
    this.config = config;
    this.searchContext = new SearchContext<KenLM>(config, scorer.LanguageModel());
  }

  public Scorer GetScorer() {
    return scorer;
  }

  public JoshuaConfiguration GetConfig() {
    return config;
  }
  
  public SearchContext<KenLM> SearchContext() {
    return searchContext;
  }
}
