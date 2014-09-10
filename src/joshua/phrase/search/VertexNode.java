package joshua.phrase.search;

// PORT: in progress

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import joshua.phrase.lm.ngram.ChartState;
import joshua.phrase.lm.ngram.Left;
import joshua.phrase.lm.ngram.Right;

public class VertexNode {

  private List<HypoState> hypos;
  private List<VertexNode> extend;
  private ChartState state;
  private boolean rightFull;
  private byte niceness;
  private kPolicy policy;
  private float bound; // c++: type is score which is typedef floats

  public VertexNode() {
    hypos = new ArrayList<HypoState>();
    extend = new ArrayList<VertexNode>();
    state = new ChartState();
  }

  public void initRoot() {
    hypos.clear();
  }

  public void AppendHypothesis(NBestComplete best) {
    assert hypos.isEmpty() || ! (hypos.get(0).state.equals(best.state));
    HypoState hypo = new HypoState();
    hypo.history = best.history;
    hypo.state = best.state;
    hypo.score = best.score;
    hypos.add(hypo);
  }
  
  public void appendHypothesis(HypoState hypo) {
    hypos.add(hypo);
  }

  public void finishRoot(kPolicy policy) {
    Collections.sort(hypos, new Comparator<HypoState>() {
      public int compare(HypoState a, HypoState b) {
        return a.score > b.score ? 1 : 0; // todo: check correctness
      }
    });

    extend.clear();

    // c++: HACK: extend to one hypo so that root can be blank.
    state.left.full = false;
    state.left.length = 0;
    state.right.length = 0;

    this.rightFull = false;
    this.niceness = 0;
    this.policy = policy;

    if (hypos.size() == 1) {
      extend.add(new VertexNode()); // extend.resize(1);
      extend.get(0).appendHypothesis(hypos.get(0));
      extend.get(0).finishedAppending((byte) 0, (byte) 0, policy);
    }
    if (hypos.isEmpty()) {
      bound = Float.NEGATIVE_INFINITY;
    } else {
      bound = hypos.get(0).score;
    }
  }

  public void finishedAppending(byte common_left, byte common_right, kPolicy policy) {
    bound = hypos.get(0).score;
    state = hypos.get(0).state;
    boolean all_full = state.left.full;
    boolean all_non_full = !state.left.full;

    DetermineSame<Left> left = new DetermineSame<Left>(state.left, common_left);
    DetermineSame<Right> right = new DetermineSame<Right>(state.right, common_right);
    for (int hi = 1; hi < hypos.size(); hi++) {
      HypoState i = hypos.get(hi);
      all_full &= i.state.left.full;
      all_non_full &= !i.state.left.full;
      left.consider(i.state.left);
      right.consider(i.state.right);
    }

    state.left.full = all_full && left.isComplete();
    rightFull = all_full && right.isComplete();
    state.left.length = left.getShared();
    state.right.length = right.getShared();

    if ((all_full || all_non_full)
        && ((policy == kPolicy.Left && left.isComplete()) || (policy == kPolicy.Right && right
            .isComplete()))) {
      this.policy = kPolicy.All;
      // Prioritize revealing the other conctituent.
      this.niceness = (byte) 254;
    } else {
      this.policy = policy;
      this.niceness = (policy == kPolicy.Left) ? state.left.length : state.right.length;
    }
  }
  
  public void BuildExtend() {
    if (! extend.isEmpty()) return;
    if (hypos.size() <= 1) return;
    if (policy == kPolicy.Left) {
      Split(new DivideLeft(state.left.length), hypos, extend);
    } else if (policy == kPolicy.Right){
      Split(new DivideRight(state.right.length), hypos, extend);
    } else {
      assert policy == kPolicy.All;
      extend.clear();
      for (int i = 0; i < hypos.size(); i++) {
        VertexNode newNode = new VertexNode();
        newNode.appendHypothesis(hypos.get(i));
        extend.add(newNode);
      }
    }
    for (VertexNode node: extend) {
      node.finishedAppending(state.left.length, state.right.length, policy);
    }
  }

  public boolean complete() {
    return hypos.size() == 1 && extend.isEmpty();
  }

  public List<HypoState> getHypos() {
    return hypos;
  }

  public int size() {
    return extend.size();
  }

  public float getBound() { // c++: typedef Score
    return bound;
  }

  public boolean Empty() {
    return hypos.isEmpty() && extend.isEmpty();
  }

  public ChartState State() {
    return state;
  }

  public boolean RightFull() {
    return rightFull;
  }
  
  public VertexNode get(int index) {
    return extend.get(index);
  }

  public byte Niceness() {
    return niceness;
  }


  public Note End() {
    // TODO Auto-generated method stub
    return null;
  }

  private static long kCompleteAdd = -1;
  
  private interface Divider {
    public long divide(ChartState state);
  }
  
  private class DivideLeft implements Divider {                                                                                   
    private byte index;
    
    public DivideLeft(byte index) {
      this.index = index;
    }

    public long divide(ChartState state) {
      // TODO: figure out bit manipulations
      return 0;
      
//      return (index < state.left.length) ?       
//          (state.left.pointers[index]) :     
//            (kCompleteAdd - state.left.full); 
    }
  }                                                                                                   
                                                                                                       
  private class DivideRight implements Divider {
    private byte index;

    public DivideRight(byte index) {
      this.index = index;
    }

    public long divide(ChartState state) {
      // TODO: figure out bit manipulations
      return 0;
      
//      return (index < state.right.length) ?
//          state.right.words[index] :
//            (kCompleteAdd - state.left.full);
    }                                                                                                
  } 

  /** TODO: I'm not sure that this is right **/
  private static void Split(Divider divider, List<HypoState> hypos, List<VertexNode> extend) {
    // Map from divider to index in extend.                                                            
    HashMap<Long, Integer> lookup = new HashMap<Long, Integer>();
    for (HypoState node: hypos) {
      long key = divider.divide(node.state);
      lookup.put(key, extend.size());
      if (extend.size() > 0) {
        extend.get(extend.size() - 1).appendHypothesis(node);
      } else {
        // extend[res.first->second].AppendHypothesis(*i);
      }                                                                                                     }
  }
}
