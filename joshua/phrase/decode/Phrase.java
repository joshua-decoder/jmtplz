package joshua.phrase.decode;

import joshua.Vocabulary;

// currently this is an unoptimized implementation
public class Phrase {
	
	// word ids not strings
	private long[] words;

	public Phrase(Vocabulary vocabulary, String[] words) {
		// converts words to word ids
		this.words = new long[words.length];
		for (int i = 0; i < words.length; i++) {
			this.words[i] = vocabulary.findOrInsert(words[i]);
		}
	}
	
	public long[] getWords() {
		return words;
	}
	
	public int size() {
		return words.length;
	}
	
}
