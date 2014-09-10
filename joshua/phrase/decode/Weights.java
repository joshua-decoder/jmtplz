package joshua.phrase.decode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// complete

public class Weights {
  
  private List<Float> phrase_table_weights;
  private float lm_weight;
  private float distortion_weight;
  private float target_word_insertion_weight;
  private Map<String, List<Float>> weights_map;
  
  public Weights() {
    phrase_table_weights = new ArrayList<Float>();
    weights_map = new HashMap<String, List<Float>>();
  }
  
  public void ReadFromFile(String file_name) throws Exception {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file_name));
      String line;
      while ((line = br.readLine()) != null) {
        String[] token = line.split(" ");
        String name = token[0];
        List<Float> weights = new ArrayList<Float>();
        for (int i = 1; i < token.length; i++) {
          float weight = kConverter(token[i]);
          if (Float.isNaN(weight)) {
            throw new Exception("Bad feature weight: " + token[i]);
          }
          weights.add(weight);
        }
        if (weights.isEmpty()) {
          throw new Exception("No weights found for weight type " + name);
        }
        weights_map.put(name, weights);
      }
      br.close();
    } finally {
      if (br != null) br.close();
    }
    
    PopulateWeights();
  }
  
  private static float kConverter(String s) {
    if (s.equalsIgnoreCase("inf")) return Float.POSITIVE_INFINITY;
    if (s.equalsIgnoreCase("nan")) return Float.NaN;
    return Float.parseFloat(s);
  }
  
  public List<Float> PhraseTableWeights() {
    return phrase_table_weights;
  }
  
  public float LMWeight() {
    return lm_weight;
  }
  
  public float DistortionWeight() {
    return distortion_weight;
  }
  
  public float TargetWordInsertionWeight() {
    return target_word_insertion_weight;
  }
  
  private void PopulateWeights() {
    phrase_table_weights = GetWeights("phrase_table");
    lm_weight = GetSingleWeight("lm");
    distortion_weight = GetSingleWeight("distortion");
    target_word_insertion_weight = GetSingleWeight("target_word_insertion");
  }
  
  public List<Float> GetWeights(String name) {
    return weights_map.get(name);
  }
  
  public float GetSingleWeight(String name) {
    return GetWeights(name).get(0);
  }

}
