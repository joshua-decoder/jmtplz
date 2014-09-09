package joshua.phrase.decode;

import java.util.List;

import joshua.phrase.search.HypoState;
import joshua.phrase.search.Note;
import joshua.phrase.search.Vertex;
import joshua.phrase.search.kPolicy;
import joshua.phrase.util.Pool;

// complete except for resize todo

public class Stacks {
	
  private List<Stack> stacks;
  private Pool eos_phrase_pool;
  private Hypothesis end;

  public Stacks(Context context, Chart chart) {
    Future future = new Future(chart);
    // Reservation is critical because pointers to Hypothesis objects are retained as history.
    //stacks.reserve(chart.SentenceLength() + 2 /* begin/end of sentence */);
    stacks.clear();
    stacks.add(new Stack());
    // Initialize root hypothesis with <s> context and future cost for everything.
    stacks.get(0).add(new Hypothesis(context.GetScorer().LanguageModel().BeginSentenceState(), future.Full()));
    // Decode with increasing numbers of source words.
    for (int source_words = 1; source_words <= chart.SentenceLength(); ++source_words) {
      Vertices vertices = new Vertices();
      // Iterate over stacks to continue from.
      for (int from = source_words - Math.min(source_words, chart.MaxSourcePhraseLength());
           from < source_words;
           ++from) {
        int phrase_length = source_words - from;
        // Iterate over antecedents in this stack.
        for (Hypothesis ant : stacks.get(from)) {
          Coverage coverage = ant.GetCoverage();
          int begin = coverage.FirstZero();
          int last_end = Math.min(coverage.FirstZero() + context.GetConfig().reordering_limit, chart.SentenceLength());
          int last_begin = (last_end > phrase_length) ? (last_end - phrase_length) : 0;
          // We can always go from first_zero because it doesn't create a reordering gap.
          do {
            TargetPhrases phrases = chart.Range(begin, begin + phrase_length);
            if (phrases == null || !coverage.Compatible(begin, begin + phrase_length)) continue;
            // distortion etc.
            float score_delta = context.GetScorer().Transition(ant, phrases, begin, begin + phrase_length);
            // Future costs: remove span to be filled.
            score_delta += future.Change(coverage, begin, begin + phrase_length);
            vertices.Add(ant, begin, begin + phrase_length, score_delta);
          // Enforce the reordering limit on later iterations.
          } while (++begin <= last_begin);
        }
      }
      EdgeGenerator gen = new EdgeGenerator();
      vertices.Apply(chart, gen);
      //stacks.resize(stacks_.size() + 1); // todo
      //stacks.back().reserve(context.SearchContext().PopLimit()); todo
      EdgeOutput output = new EdgeOutput(stacks.get(stacks.size()-1));
      gen.Search(context.SearchContext(), output);
    }
    PopulateLastStack(context, chart);
  }
  
  private void PopulateLastStack(Context context, Chart chart) {
    // First, make Vertex of all hypotheses
    Vertex all_hyps = new Vertex();
    for (Hypothesis ant : stacks.get(chart.SentenceLength())) {
      // TODO: the zero in the following line assumes that EOS is not scored for distortion. 
      // This assumption might need to be revisited.
      StacksShared.AddHypothesisToVertex(ant, 0, all_hyps);
    }
    
    // Next, make Vertex which consists of a single EOS phrase.
    // The seach algorithm will attempt to find the best hypotheses in the "cross product" of these two sets.
    // TODO: Maybe this should belong to the phrase table.  It's constant.
    Vertex eos_vertex = new Vertex();
    HypoState eos_hypo = new HypoState();
    Phrase eos_phrase = new Phrase(eos_phrase_pool, context.GetVocab(), "</s>");

    eos_hypo.history.cvp = eos_phrase.Base();
    eos_hypo.score = context.GetScorer().LM(eos_phrase.getWords(), eos_hypo.state);
    eos_vertex.Root().appendHypothesis(eos_hypo);
    eos_vertex.Root().finishRoot(kPolicy.Left);

    // Add edge that tacks </s> on
    EdgeGenerator gen = new EdgeGenerator();
    Note note = new Note();
    note.ints.first = chart.SentenceLength();
    note.ints.second = chart.SentenceLength();
    StacksShared.AddEdge(all_hyps, eos_vertex, note, gen);

    //stacks.resize(stacks_.size() + 1); // todo
    PickBest output = new PickBest(stacks.get(stacks.size()-1));
    gen.Search(context.SearchContext(), output);

    end = stacks.get(stacks.size()-1).isEmpty() ? null : stacks.get(stacks.size()-1).get(0);  
  }

  public Hypothesis End() {
    return end;
  }

}
