package joshua;

public interface Vocabulary {

  long find(String word);
  long findOrInsert(String word);

}
