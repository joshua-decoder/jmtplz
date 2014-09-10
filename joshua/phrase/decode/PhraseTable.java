package joshua.phrase.decode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import joshua.Vocabulary;
import joshua.phrase.search.HypoState;
import joshua.phrase.search.kPolicy;

public class PhraseTable {

  private int maxSourcePhraseLength;
  private Map<Long, TargetPhrases> map; // unoptimized

  public PhraseTable(String file, Vocabulary vocab, Scorer scorer) throws Exception {
    maxSourcePhraseLength = 0;
    map = new HashMap<Long, TargetPhrases>();

    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    long previous_text_hash = 0;
    TargetPhrases entry = null;
    ArrayList<Long> source = new ArrayList<Long>();
    while ((line = br.readLine()) != null) {
      String[] pipes = line.split("\\s*\\|\\|\\|\\s*"); // split ||| and trim
      long source_text_hash = pipes[0].hashCode(); // todo: murmur hash
      if (source_text_hash != previous_text_hash) {
        if (entry != null)
          entry.getVertex().getRoot().finishRoot(kPolicy.Left);
        source.clear();
        for (String word : pipes[0].split(" ")) {
          source.add(vocab.findOrInsert(word));
        }
        maxSourcePhraseLength = Math.max(maxSourcePhraseLength, source.size());
        long hash = hashWords(source);
        if (!map.containsKey(hash)) {
          entry = new TargetPhrases();
          map.put(hash, entry);
        } else {
          entry = map.get(hash);
        }
        entry.getVertex().getRoot().initRoot();
        previous_text_hash = source_text_hash;
      }
      Phrase target = new Phrase(vocab, pipes[1].split(" "));
      HypoState hypo = new HypoState();
      hypo.history.cvp = target.getWords(); // c++: target.Base()

      float parsed_score = scorer.parse(pipes[2]);
      hypo.score = parsed_score + scorer.LM(target.getWords(), hypo.state) // c++:
                                                                           // target.begin(),
                                                                           // target.end()
          + scorer.TargetWordCount(target.size());

      entry.getVertex().getRoot().appendHypothesis(hypo);
    }
    if (entry != null)
      entry.getVertex().getRoot().finishRoot(kPolicy.Left);
    br.close();
  }

  private long hashWords(Iterable<Long> words) { // todo: use murmur hash
    long hash = 17;
    for (long word : words) {
      hash = 31 * word;
    }
    return hash;
  }

  public int getMaxSourcePhraseLength() {
    return maxSourcePhraseLength;
  }
  
  public TargetPhrases Phrases(Long long1, long l) {
    return null; // todo
  }
}
