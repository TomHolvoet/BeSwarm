package applications.trajectory.geom;

import applications.trajectory.geom.point.Point3D;
import com.google.auto.value.AutoValue;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
@AutoValue
public abstract class LineSegment {

  public abstract Point3D getStartPoint();

  public abstract Point3D getEndPoint();

  public Point3D getSlope() {
    return Point3D.minus(getEndPoint(), getStartPoint());
  }

  public static LineSegment create(Point3D start, Point3D end) {
    return new AutoValue_LineSegment(start, end);
  }
}
