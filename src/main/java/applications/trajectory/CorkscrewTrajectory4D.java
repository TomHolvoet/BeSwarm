package applications.trajectory;

import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
import com.google.auto.value.AutoValue;
import control.FiniteTrajectory4d;
import control.Trajectory2d;
import utils.math.Transformations;

/**
 * Corkscrew motion around a straight line trajectory defined by an origin and destination point,
 * a radius as perpendicular distance to the straight line (origin-destination) and a frequency
 * to specify the number of revolutions.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class CorkscrewTrajectory4D extends PeriodicTrajectory implements FiniteTrajectory4d {

    private static final double EPSILON = 0.00000001d;
    private final FiniteTrajectory4d unitTrajectory;
    private final double aroundX;
    private final double aroundY;
    private Point4DCache cache;
    private final Point4D origin;

    CorkscrewTrajectory4D(Point4D origin, Point4D destination, double speed, double radius,
            double frequency, double phase) {
        this.origin = origin;
        double distance = Point4D.distance(origin, destination);
        unitTrajectory = new UntransformedUsage(
                CircleTrajectory2D.builder().setRadius(radius).setFrequency(frequency)
                        .setPhase(phase).build(), speed, distance);

        //translate origin to get angles.
        Point4D translated = destination.minus(origin);

        //find angles to unit trajectory
        double x = translated.getX();
        double y = translated.getY();
        double z = translated.getZ();

        this.aroundX = (Math.PI / 2) - Math.asin(z / Math.sqrt(Math.pow(y, 2) + Math.pow(z, 2)));
        this.aroundY = Math.acos(x / Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)));

        //set initial cache
        this.cache = newCache(Point4D.origin(), Point4D.origin(), -1);
    }

    static Point4DCache newCache(Point4D point, Point4D velocity, double timeMark) {
        return new AutoValue_CorkscrewTrajectory4D_Point4DCache(point, velocity, timeMark);
    }

    private static boolean isEqual(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    private void refreshCache(double time) {
        if (!isEqual(cache.getTimeMark(), time)) {
            Point4D beforeTransPoint = Point4D
                    .create(unitTrajectory.getDesiredPositionX(time),
                            unitTrajectory.getDesiredPositionY(time),
                            unitTrajectory.getDesiredPositionZ(time),
                            unitTrajectory.getDesiredAngleZ(time));
            Point4D beforeTransVelocity = Point4D
                    .create(unitTrajectory.getDesiredVelocityX(time),
                            unitTrajectory.getDesiredVelocityY(time),
                            unitTrajectory.getDesiredVelocityZ(time),
                            unitTrajectory.getDesiredAngularVelocityZ(time));
            setCache(beforeTransPoint, beforeTransVelocity, time);
        }
    }

    private void setCache(Point4D beforeTransPoint, Point4D beforeTransVelocity, double time) {
        this.cache = newCache(beforeTransPoint, beforeTransVelocity, time);
    }

    private Point4D getCachePoint() {
        return this.cache.getDestinationPoint();
    }

    private Point4D getCacheVelocity() {
        return this.cache.getVelocityPoint();
    }

    private Point4D transformToRealPosition(Point4D toTrans) {
        return transformToRealVelocity(toTrans).plus(origin);
    }

    private Point4D transformToRealVelocity(Point4D toTrans) {
        Point4D rotated = Point4D.from(Transformations
                .reverseRotationXYZ(Point3D.project(toTrans), aroundX, aroundY, 0), 0);
        return rotated;
    }

    @Override
    public double getTrajectoryDuration() {
        return unitTrajectory.getTrajectoryDuration();
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealPosition(getCachePoint()).getX();
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealVelocity(getCacheVelocity()).getX();
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealPosition(getCachePoint()).getY();
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealVelocity(getCacheVelocity()).getY();
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealPosition(getCachePoint()).getZ();
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealVelocity(getCacheVelocity()).getZ();
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealPosition(getCachePoint()).getAngle();
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToRealVelocity(getCacheVelocity()).getAngle();
    }

    private final class UntransformedUsage implements FiniteTrajectory4d {
        private Trajectory2d circlePlane;
        private final LinearTrajectory1D linear;
        private final double endPoint;
        private final double speed;

        private boolean atEnd;

        private UntransformedUsage(Trajectory2d circlePlane, double speed, double endPoint) {
            this.linear = new LinearTrajectory1D(0, speed);
            this.circlePlane = circlePlane;
            this.endPoint = endPoint;
            this.atEnd = false;
            this.speed = speed;
        }

        @Override
        public double getDesiredPositionX(double timeInSeconds) {
            if (atEnd) {
                return endPoint;
            }
            if (linear.getDesiredPosition(timeInSeconds) >= endPoint) {
                markEnd();
                return endPoint;
            }
            return linear.getDesiredPosition(timeInSeconds);
        }

        private void markEnd() {
            this.circlePlane = new Trajectory2d() {
                @Override
                public double getDesiredPositionAbscissa(double timeInSeconds) {
                    return 0;
                }

                @Override
                public double getDesiredVelocityAbscissa(double timeInSeconds) {
                    return 0;
                }

                @Override
                public double getDesiredPositionOrdinate(double timeInSeconds) {
                    return 0;
                }

                @Override
                public double getDesiredVelocityOrdinate(double timeInSeconds) {
                    return 0;
                }
            };
        }

        @Override
        public double getDesiredVelocityX(double timeInSeconds) {
            if (atEnd) {
                return 0;
            }
            return linear.getDesiredVelocity(timeInSeconds);
        }

        @Override
        public double getDesiredPositionY(double timeInSeconds) {
            return circlePlane.getDesiredPositionOrdinate(timeInSeconds);
        }

        @Override
        public double getDesiredVelocityY(double timeInSeconds) {
            return circlePlane.getDesiredVelocityOrdinate(timeInSeconds);
        }

        @Override
        public double getDesiredPositionZ(double timeInSeconds) {
            return circlePlane.getDesiredPositionAbscissa(timeInSeconds);
        }

        @Override
        public double getDesiredVelocityZ(double timeInSeconds) {
            return circlePlane.getDesiredVelocityAbscissa(timeInSeconds);
        }

        @Override
        public double getDesiredAngleZ(double timeInSeconds) {
            return 0;
        }

        @Override
        public double getDesiredAngularVelocityZ(double timeInSeconds) {
            return 0;
        }

        @Override
        public double getTrajectoryDuration() {
            return endPoint / speed;
        }

    }

    @AutoValue
    static abstract class Point4DCache {
        public abstract Point4D getDestinationPoint();

        public abstract Point4D getVelocityPoint();

        public abstract double getTimeMark();
    }

}
