package joshua.phrase.decode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import joshua.Vocabulary;
import joshua.phrase.search.HypoState;
import joshua.phrase.search.kPolicy;

public class PhraseTable {

  private int maxSourcePhraseLength;
  private Map<ArrayList<Integer>, TargetPhrases> map; // unoptimized

  public PhraseTable(String file, Scorer scorer) throws IOException {
    maxSourcePhraseLength = 0;
    map = new HashMap<ArrayList<Integer>, TargetPhrases>();
    
    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    long previous_text_hash = 0;
    TargetPhrases entry = null;
    ArrayList<Integer> source = new ArrayList<Integer>();
    while ((line = br.readLine()) != null) {
      String[] pipes = line.split("\\s*\\|\\|\\|\\s*"); // split ||| and trim
      long source_text_hash = pipes[0].hashCode(); // todo: murmur hash
      if (source_text_hash != previous_text_hash) {
        if (entry != null)
          entry.getVertex().getRoot().finishRoot(kPolicy.Left);
        source.clear();
        for (String word : pipes[0].split(" ")) {
          source.add(Vocabulary.id(word));
        }
        maxSourcePhraseLength = Math.max(maxSourcePhraseLength, source.size());
        if (!map.containsKey(source))
          map.put(source, new TargetPhrases());
        entry = map.get(source);

        entry.getVertex().getRoot().initRoot();
        previous_text_hash = source_text_hash;
      }
      Phrase target = new Phrase(pipes[1].split(" "));
      HypoState hypo = new HypoState();
      hypo.history.set(target);
      
//      System.err.println(String.format("Read phrase '%s'", target));

      float parsed_score = scorer.parse(pipes[2]);
      hypo.score = parsed_score 
          + scorer.LM(target.getWords(), hypo.state) // c++: target.begin(), target.end()
          + scorer.TargetWordCount(target.size());

      entry.getVertex().getRoot().appendHypothesis(hypo);
    }
    if (entry != null)
      entry.getVertex().getRoot().finishRoot(kPolicy.Left);
    br.close();
  }

  public int getMaxSourcePhraseLength() {
    return maxSourcePhraseLength;
  }
  
  public TargetPhrases Phrases(Long long1, long l) {
    return null; // todo
  }
}
