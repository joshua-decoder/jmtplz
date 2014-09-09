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

  public static final int KENLM_MAX_ORDER = 3;
  
  public int reordering_limit = 3;

  public JoshuaConfiguration() {
  }

  public void add_option(String option, String comment) {
  }

  public void add_option(String option, boolean required, String comment) {
  }
}
