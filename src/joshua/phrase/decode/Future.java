package joshua.phrase.decode;

// PORT: done

import java.util.ArrayList;
import java.util.List;

// complete

public class Future {
  
  // sentence_length is a valid value of end.
  private int sentence_length_plus_1;
  // Square matrix with half the values ignored.  TODO: waste less memory.
  private List<Float> entries;
  
  public Future(Chart chart) {
    
    entries = new ArrayList<Float>();
    
    sentence_length_plus_1 = chart.SentenceLength() + 1;
    for (int i = 0; i < sentence_length_plus_1 * sentence_length_plus_1; i++) {
      entries.add(Float.NEGATIVE_INFINITY);
    }
    
    for (int begin = 0; begin <= chart.SentenceLength(); begin++) {
      // Nothing is nothing (this is a useful concept when two phrases abut)
      SetEntry(begin, begin, 0.0f);
      // Insert phrases
      int max_end = Math.min(begin + chart.MaxSourcePhraseLength(), chart.SentenceLength());
      for (int end = begin + 1; end <= max_end; end++) {
        TargetPhrases phrases = chart.Range(begin, end);
        if (phrases != null) {
          SetEntry(begin, end, phrases.getVertex().Bound());
        }
      }
    }
    
    // All the phrases are in, now do minimum dynamic programming.  Lengths 0 and 1 were already handled above.
    for (int length = 2; length <= chart.SentenceLength(); length++) {
      for (int begin = 0; begin <= chart.SentenceLength() - length; begin++) {
        for (int division = begin + 1; division < begin + length; division++) {
          SetEntry(begin, begin + length, Math.max(Entry(begin, begin + length), Entry(begin, division) + Entry(division, begin + length)));
        }
      }
    }
  }
  
  public float Full() {
    return Entry(0, sentence_length_plus_1 - 1);
  }

  // Calculate change in rest cost when the given coverage is to be covered.                       
  public float Change(Coverage coverage, int begin, int end) {
    int left = coverage.LeftOpen(begin);
    int right = coverage.RightOpen(end, sentence_length_plus_1 - 1);
    return Entry(left, begin) + Entry(end, right) - Entry(left, right);
  }
  
  private float Entry(int begin, int end) {
    assert end >= begin;
    assert end < sentence_length_plus_1;
    return entries.get(begin * sentence_length_plus_1 + end);
  }
  
  private void SetEntry(int begin, int end, float value) { // &float Entry(begin, end)
    assert end >= begin;
    assert end < sentence_length_plus_1;
    entries.set(begin * sentence_length_plus_1 + end, value);
  }

}
