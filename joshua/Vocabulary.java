package joshua;

public interface Vocabulary {

  int find(String word);
  int findOrInsert(String word);

}
