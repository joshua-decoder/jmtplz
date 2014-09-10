package joshua;

public interface Vocabulary {

  long find(String word);
  long findOrInsert(String word);
  String String(long id);
  int Size();

}
