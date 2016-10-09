package geom;

import com.google.auto.value.AutoValue;

/**
 * Represents a plane as: ax + by + cz + d = 0
 *
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class PlaneScalarCoeffs {

  PlaneScalarCoeffs() {}

  public static PlaneScalarCoeffs create(double a, double b, double c, double d) {
    return new AutoValue_PlaneScalarCoeffs(a, b, c, d);
  }

  public abstract double a();

  public abstract double b();

  public abstract double c();

  public abstract double d();
}
