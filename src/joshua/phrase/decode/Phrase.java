package joshua.phrase.decode;

import joshua.Vocabulary;

// currently this is an unoptimized implementation
public class Phrase {

  // word ids not strings
  private int[] words; // ID* pointer

  public Phrase(int word) {
    this.words = new int[] { word };
  }
  
  public Phrase(String word) {
    this.words = new int[] { Vocabulary.id(word) };
  }
  
  public Phrase(String[] words) {
    // converts words to word ids
    this.words = new int[words.length];
    for (int i = 0; i < words.length; i++) {
      this.words[i] = Vocabulary.id(words[i]);
    }
  }

  public int[] getWords() {
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
