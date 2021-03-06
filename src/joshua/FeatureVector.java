package joshua;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import joshua.FeatureVector;
import joshua.util.io.LineReader;

/**
 * An implementation of a sparse feature vector, using for representing both weights and feature
 * values.
 * 
 * @author Matt Post <post@cs.jhu.edu>
 */

public class FeatureVector {
  private HashMap<String, Float> features;

  public FeatureVector() {
    features = new HashMap<String, Float>();
  }

  public FeatureVector(String feature, float value) {
    features = new HashMap<String, Float>();
    features.put(feature, value);
  }

  /**
   * This version of the constructor takes an uninitialized feature with potentially intermingled
   * labeled and unlabeled feature values, of the format:
   * 
   * [feature1=]value [feature2=]value
   * 
   * It produces a Feature Vector where all unlabeled features have been labeled by appending the
   * unlabeled feature index (starting at 0) to the defaultPrefix value.
   * 
   * @param featureString, the string of labeled and unlabeled features (probably straight from the
   *          grammar text file)
   * @param prefix, the prefix to use for unlabeled features (probably "tm_OWNER_")
   */
  public FeatureVector(String featureString, String prefix) {

    /*
     * Read through the features on this rule, adding them to the feature vector. Unlabeled features
     * are converted to a canonical form.
     * 
     * Note that it's bad form to mix unlabeled features and the named feature index they are mapped
     * to, but we are being liberal in what we accept.
     */
    features = new HashMap<String, Float>();
    int denseFeatureIndex = 0;

    if (!featureString.trim().equals("")) {
      for (String token : featureString.split("\\s+")) {
        if (token.indexOf('=') == -1) {
          features.put(String.format("%s%d", prefix, denseFeatureIndex), -Float.parseFloat(token));
          denseFeatureIndex++;
        } else {
          int splitPoint = token.indexOf('=');
          features.put(token.substring(0, splitPoint),
              Float.parseFloat(token.substring(splitPoint + 1)));
        }
      }
    }
  }

  public Set<String> keySet() {
    return features.keySet();
  }

  public int size() {
    return features.size();
  }

  public FeatureVector clone() {
    FeatureVector newOne = new FeatureVector();
    for (String key : this.features.keySet())
      newOne.put(key, this.features.get(key));
    return newOne;
  }

  /**
   * Subtracts the weights in the other feature vector from this one. Note that this is not set
   * subtraction; keys found in the other FeatureVector but not in this one will be initialized with
   * a value of 0.0f before subtraction.
   */
  public void subtract(FeatureVector other) {
    for (String key : other.keySet()) {
      float oldValue = (features.containsKey(key)) ? features.get(key) : 0.0f;
      features.put(key, oldValue - other.get(key));
    }
  }

  /**
   * Adds the weights in the other feature vector to this one. This is set union, with values shared
   * between the two being summed.
   */
  public void add(FeatureVector other) {
    for (String key : other.keySet()) {
      if (!features.containsKey(key))
        features.put(key, other.get(key));
      else
        features.put(key, features.get(key) + other.get(key));
    }
  }

  public boolean containsKey(final String feature) {
    return features.containsKey(feature);
  }

  /**
   * This method returns the weight of a feature if it exists and otherwise throws a runtime error.
   * It is the duty of the programmer to check using the method containsKey if a feature with a
   * certain name exists. Previously this method would return 0 if the key did not exists, but this
   * lead to bugs in other parts of the code because Feature Names are often specified in capitals
   * but then lowercased, but in using the get method the lowercase form is not used consistently.
   * It is therefore good defensive programming to just throw an error when someone tries to get a
   * feature that does not exist - this will automatically eliminate such hard to debug errors. This
   * is what is now implemented.
   * 
   * @param feature
   * @return
   */
  public float get(String feature) {
    if (features.containsKey(feature))
      return features.get(feature);

    throw new RuntimeException(String.format("ERROR: unknown feature '%s'", feature));
  }

  public void put(String feature, float value) {
    features.put(feature, value);
  }

  public Map<String, Float> getMap() {
    return features;
  }

  /**
   * Computes the inner product between this feature vector and another one.
   */
  public float innerProduct(FeatureVector other) {
    float cost = 0.0f;
    for (String key : features.keySet())
      if (other.containsKey(key))
        cost += features.get(key) * other.get(key);

    return cost;
  }

  public void times(float value) {
    for (String key : features.keySet())
      features.put(key, features.get(key) * value);
  }

  /***
   * Outputs a list of feature names. All dense features are printed. Feature names are printed
   * in the order they were read in.
   */
  public String toString() {
    String outputString = "";

    for (String key: features.keySet()) {
      float value = features.containsKey(key) ? features.get(key) : 0.0f;
      outputString += String.format("%s%s=%.3f", (outputString.length() > 0) ? " " : "", key, value);
    }
    
    return outputString;
  }

  public static boolean isDense(String feature) {
    return feature.startsWith("tm_") || feature.startsWith("lm_") || feature.equals("WordPenalty");
  }
  
  public void readFromFile(String fileName) {

    if (fileName.equals(""))
      return;

    try {
      LineReader lineReader = new LineReader(fileName);

      for (String line : lineReader) {
        line = line.replaceAll("\\s+", " ");

        if (line.equals("") || line.startsWith("#") || line.startsWith("//")
            || line.indexOf(' ') == -1)
          continue;

        String tokens[] = line.split("\\s+");
        String feature = tokens[0];
        Float value = Float.parseFloat(tokens[1]);

        features.put(feature, value);
      }
    } catch (FileNotFoundException ioe) {
      System.err.println("* FATAL: Can't find weights-file '" + fileName + "'");
      System.exit(1);
    } catch (IOException ioe) {
      System.err.println("* FATAL: Can't read weights-file '" + fileName + "'");
      ioe.printStackTrace();
      System.exit(1);
    }
  }
}
