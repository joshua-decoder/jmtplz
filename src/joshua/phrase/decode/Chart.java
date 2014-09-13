package joshua.phrase.decode;

import java.util.ArrayList;
import java.util.List;

import joshua.Vocabulary;

// Target phrases that correspond to each source span
public class Chart {

  private int sentence_length;
  private int max_source_phrase_length;
  
  // Banded array: different source lengths are next to each other.
  private List<TargetPhrases> entries;
  
  /* Don't call this */
  private Chart() {
  }
  
  public Chart(PhraseTable table, String source, Scorer scorer) {
    max_source_phrase_length = table.getMaxSourcePhraseLength();
    List<Integer> words = new ArrayList<Integer>();

    for (String word : source.split("\\s+")) {
      words.add(Vocabulary.id(word));
    }
    sentence_length = words.size();

    System.err.println(String.format("Initializing chart of size %d max %d from %s", words.size(), max_source_phrase_length, source));
    
    entries = new ArrayList<TargetPhrases>();
    for (int i = 0; i < sentence_length * max_source_phrase_length; i++)
      entries.add(null);
    
    // There's some unreachable ranges off the edge.  Meh.
    //entries_.resize(sentence_length_ * max_source_phrase_length_);
    for (int begin = 0; begin != words.size(); ++begin) {
      for (int end = begin + 1; (end != words.size() + 1) && (end <= begin + max_source_phrase_length); ++end) {
        SetRange(begin, end, table.Phrases(words.subList(begin, end)));
      }
      if (Range(begin, begin + 1) == null) {
        // Add passthrough for words not known to the phrase table.
        TargetPhrases passThrough = new TargetPhrases();
        passThrough.MakePassThrough(scorer, words.get(begin));
        SetRange(begin, begin + 1, passThrough);
      }
    }
  }

  public int SentenceLength() {
    return sentence_length;
  }

  // c++: TODO: make this reflect the longest source phrase for this sentence.
  public int MaxSourcePhraseLength() {
    return max_source_phrase_length;
  }

  public TargetPhrases Range(int begin, int end) {
    int index = begin * max_source_phrase_length + end - begin - 1;
    if (index < 0 || index >= entries.size())
      return null;
    return entries.get(index);
  }

  private void SetRange(int begin, int end, TargetPhrases to) {
    try {
      entries.set(begin * max_source_phrase_length + end - begin - 1, to);
    } catch (java.lang.IndexOutOfBoundsException e) {
      System.err.println(String.format("Whoops! %s [%d-%d] = %d too long (%d)", to, begin, end,
          begin * max_source_phrase_length + end - begin - 1, entries.size()));
    }
  }

}
