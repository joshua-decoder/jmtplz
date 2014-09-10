package joshua.phrase.decode;

import joshua.JoshuaConfiguration;

public class Context {

  private Scorer scorer;
  private JoshuaConfiguration config;
  private Object search_context; // search::Context<Sorer::Model>

  public Context(String lm, String weights_file, JoshuaConfiguration config) throws Exception {
    this.scorer = new Scorer(lm, weights_file);
    this.config = new JoshuaConfiguration();
    // this.search_context = new SearchContext();
    // search_context_(search::Config(scorer_.GetWeights().LMWeight(),
    // config.pop_limit, search::NBestConfig(1)), scorer_.LanguageModel()) {}
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

}
