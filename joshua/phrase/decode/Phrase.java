package joshua.phrase.decode;

import joshua.Vocabulary;
import joshua.phrase.util.Pool;

// currently this is an unoptimized implementation
public class Phrase {

  // word ids not strings
  private long[] words;
  
  public Phrase(Object pointer) {
    
  }

  public Phrase(Vocabulary vocabulary, String[] words) {
    // converts words to word ids
    this.words = new long[words.length];
    for (int i = 0; i < words.length; i++) {
      this.words[i] = vocabulary.findOrInsert(words[i]);
    }
  }

  public Phrase(Pool eos_phrase_pool, Object getVocab, String string) {
    // TODO Auto-generated constructor stub
  }

  public long[] getWords() {
    return words;
  }

  public int size() {
    return words.length;
  }

  public long[] Base() {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean Valid() {
    // TODO Auto-generated method stub
    return false;
  }

}
