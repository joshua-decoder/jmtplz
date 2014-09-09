package joshua.phrase.decode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import joshua.Vocabulary;
import joshua.phrase.util.SlowHashMapVocabulary;

public class Decode {

  public Decode(Context context, PhraseTable table, String in, HashMap<String, ScoreHistory> history_map, boolean verbose, OutputStream out) {

  }

  

  public static void main(String[] args) throws IOException {
	  Vocabulary vocab = new SlowHashMapVocabulary();
	  Scorer scorer = new Scorer();
	  PhraseTable ptable = new PhraseTable("phrases.en-it.txt", vocab, scorer);
	  System.out.println("longest source phrase: " + ptable.getMaxSourcePhraseLength());
  }
}
