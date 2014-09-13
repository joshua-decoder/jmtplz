package joshua.phrase.decode;

import java.util.ArrayList;
import java.util.List;

import joshua.Vocabulary;

// Target phrases that correspond to each source span
public class Chart {

  private int sentence_length;
  private int max_source_phrase_length;
  private List<TargetPhrases> entries; // Banded array: different source lengths
                                       // are next to each other.

  public Chart(PhraseTable table, String input, Scorer scorer) {
    max_source_phrase_length = table.getMaxSourcePhraseLength();
    List<Integer> words = new ArrayList<Integer>();
    for (String word : input.split(" ")) {
      words.add(Vocabulary.id(word));
    }
    sentence_length = words.size();
    
    entries = new ArrayList<TargetPhrases>();
    
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

  // c++: TODO: make this reflect the longent source phrase for this sentence.
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
    entries.set(begin * max_source_phrase_length + end - begin - 1, to);
  }

}
