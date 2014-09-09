package joshua.phrase.util;

import java.util.HashMap;
import java.util.Map;

import joshua.Vocabulary;

// currently an unoptimized implementation of MutableVocab
public class SlowHashMapVocabulary implements Vocabulary {

  private Map<String, Long> map = new HashMap<String, Long>();

  @Override
  public long find(String word) {
    return map.get(word);
  }

  @Override
  public long findOrInsert(String word) {
    if (!map.containsKey(word)) {
      map.put(word, (long) map.size());
    }
    return map.get(word);
  }

}