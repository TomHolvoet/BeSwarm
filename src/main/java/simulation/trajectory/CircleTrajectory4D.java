package simulation.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class CircleTrajectory4D implements Trajectory4d {
    private Point4D location;
    private CircleTrajectory2D xycircle;
    private double planeAngle;
    private double scalefactor;

    public CircleTrajectory4D(Point4D location, double radius, double frequency,
            double planeAngle) {
        this.location = location;
        this.planeAngle = planeAngle;
        this.scalefactor = StrictMath.tan(planeAngle);
        this.xycircle = new CircleTrajectory2D(radius, frequency, location);
    }

    @Override public Trajectory1d getTrajectoryLinearX() {
        return xycircle.getTrajectoryLinearAbscissa();
    }

    @Override public Trajectory1d getTrajectoryLinearY() {
        return xycircle.getTrajectoryLinearOrdinate();
    }

    @Override public Trajectory1d getTrajectoryLinearZ() {
        return new Trajectory1d() {
            @Override public double getDesiredPosition(double timeInSeconds) {
                return scalefactor * xycircle
                        .getTrajectoryLinearOrdinate()
                        .getDesiredPosition(timeInSeconds) - location.getY()
                        + location.getZ();
            }

            @Override public double getDesiredVelocity(double timeInSeconds) {
                return scalefactor * xycircle
                        .getTrajectoryLinearOrdinate()
                        .getDesiredVelocity(timeInSeconds);
            }
        };
    }

    @Override public Trajectory1d getTrajectoryAngularZ() {
        return null;
    }
}
