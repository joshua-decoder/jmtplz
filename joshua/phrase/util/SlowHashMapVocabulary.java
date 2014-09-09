package joshua.phrase.util;

import java.util.HashMap;
import java.util.Map;

import joshua.Vocabulary;

// currently an unoptimized implementation of MutableVocab
public class SlowHashMapVocabulary implements Vocabulary {
	
	private Map<String, Integer> map = new HashMap<String, Integer>();

	@Override
	public int find(String word) {
		return map.get(word);
	}

	@Override
	public int findOrInsert(String word) {
		if (!map.containsKey(word)) {
			map.put(word, map.size());
		}
		return map.get(word);
	}

}