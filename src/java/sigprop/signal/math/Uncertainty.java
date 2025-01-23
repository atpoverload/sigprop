package sigprop.signal.math;

/**
 * A class for the uncertainty (https://en.wikipedia.org/wiki/Propagation_of_uncertainty) of a
 * collection of measurements.
 */
public final class Uncertainty {
  private final int n;
  private final double mu;
  private final double sigma;

  public Uncertainty(int n, double mu, double sigma) {
    this.n = n;
    this.mu = mu;
    this.sigma = sigma;
  }

  @Override
  public String toString() {
    return String.format("%4.4f+%4.4f (%d)", mu, sigma, n);
  }
}
