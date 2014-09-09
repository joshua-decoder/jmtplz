package joshua.phrase.decode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import joshua.Vocabulary;
import joshua.phrase.search.HypoState;
import joshua.phrase.search.kPolicy;

public class PhraseTable {

  private int maxSourcePhraseLength;
  private Map<Vector<Integer>, Entry> map; // unoptimized

  public PhraseTable(String file, Scorer scorer) throws IOException {
    maxSourcePhraseLength = 0;
    map = new HashMap<Vector<Integer>, Entry>();

    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    long previous_text_hash = 0;
    Entry entry = null;
    Vector<Integer> source = new Vector<Integer>();
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
          map.put(source, new Entry());
        entry = map.get(source);
        entry.getVertex().getRoot().initRoot();
        previous_text_hash = source_text_hash;
      }
      Phrase target = new Phrase(pipes[1].split(" "));
      HypoState<Phrase> hypo = new HypoState<Phrase>();
      hypo.history = target;

      float parsed_score = scorer.parse(pipes[2]);
      hypo.score = parsed_score 
          + scorer.LM(target.getWords(), hypo.state) // c++: target.begin(), target.end()
          + scorer.targetWordCount(target.size());

      entry.getVertex().getRoot().appendHypothesis(hypo);
    }
    if (entry != null)
      entry.getVertex().getRoot().finishRoot(kPolicy.Left);
    br.close();
  }

  public int getMaxSourcePhraseLength() {
    return maxSourcePhraseLength;
  }

  private class Entry extends TargetPhrases { // typedef lol
  }
}
