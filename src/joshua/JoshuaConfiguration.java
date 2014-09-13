package joshua;

/**
 * Configuration file for Joshua decoder.
 * 
 * When adding new features to Joshua, any new configurable parameters should be
 * added to this class.
 * 
 * @author Zhifei Li, <zhifei.work@gmail.com>
 * @author Matt Post <post@cs.jhu.edu>
 */
public class JoshuaConfiguration {

  public int reordering_limit = 5;
  private int pop_limit = 10;
  private float lm_weight = 1.0f;

  public JoshuaConfiguration() {
  }

  public void add_option(String option, String comment) {
  }

  public void add_option(String option, boolean required, String comment) {
  }

  public int PopLimit() {
    return pop_limit;
  }

  public float LMWeight() {
    // TODO Auto-generated method stub
    return 0;
  }
}
