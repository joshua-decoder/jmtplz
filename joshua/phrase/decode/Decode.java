package joshua.phrase.decode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import joshua.Vocabulary;

public class Decode {

  public Decode(Context context, PhraseTable table, String in,
      HashMap<String, ScoreHistory> history_map, boolean verbose, OutputStream out) {

  }

  public static void main(String[] args) throws IOException {
    Vocabulary vocab;
    Scorer scorer = new Scorer(null, null);
    PhraseTable ptable = new PhraseTable("phrases.en-it.txt", scorer);
    System.out.println("longest source phrase: " + ptable.getMaxSourcePhraseLength());
  }
}
