package joshua.phrase.lm.ngram;

public class Right implements State {
	
	public int[] words; // c++ typedef uint WordIndex, KENLM_MAX_ORDER - 1
	public float[] backoff; // KENLM_MAX_ORDER - 1
	public byte length;
	
	public byte getLength() {
		return length;
	}
	
	public long identify(byte index) {
		return words[index];
	}

}
