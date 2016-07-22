package applications.trajectory;

import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
import com.google.auto.value.AutoValue;
import control.FiniteTrajectory4d;
import control.Trajectory2d;
import utils.math.Transformations;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TwirlTrajectory4D extends PeriodicTrajectory implements FiniteTrajectory4d {

    private static final double EPSILON = 0.00000001d;
    private FiniteTrajectory4d unitTrajectory;
    //    private final FiniteTrajectory4d trajectory;
    private final double aroundX;
    private final double aroundY;
    private Point4DCache cache;
    private final Point4D origin;

    public TwirlTrajectory4D(Point4D origin, Point4D destination, double speed, double radius,
            double frequency, double phase) {
        this.origin = origin;
        double distance = Point4D.distance(origin, destination);
        unitTrajectory = new UntransformedUsage(
                CircleTrajectory2D.builder().setRadius(radius).setFrequency(frequency)
                        .setPhase(phase).build(), speed, distance);

        //translate origin
        Point4D translated = destination.minus(origin);

        //find rotation and set UnitTrajectory
        double x = translated.getX();
        double y = translated.getY();
        double z = translated.getZ();

        double alpha = (Math.PI / 2) - Math.asin(z / Math.sqrt(Math.pow(y, 2) + Math.pow(z, 2)));
        double beta = (Math.PI * 2) - Math.asin(z / Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)));

        aroundX = -alpha;
        aroundY = -beta;

        //translate origin back and rote back decorator.
        this.cache = newCache(Point4D.origin(), -1);

    }

    private static Point4DCache newCache(Point4D point, double timeMark) {
        return new AutoValue_TwirlTrajectory4D_Point4DCache(point, timeMark);
    }

    private static boolean isEqual(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    private void refreshCache(double time) {
        if (!isEqual(cache.getTimeMark(), time)) {
            Point4D beforeTrans = Point4D
                    .create(unitTrajectory.getDesiredPositionX(time),
                            unitTrajectory.getDesiredPositionY(time),
                            unitTrajectory.getDesiredPositionZ(time),
                            unitTrajectory.getDesiredAngleZ(time));
            setCache(beforeTrans, time);
        }
    }

    private void setCache(Point4D beforeTrans, double time) {
        this.cache = newCache(beforeTrans, time);
    }

    private Point4D getCachePoint() {
        return this.cache.getPoint();
    }

    private Point4D transformToReal(Point4D toTrans) {
        Point4D rotated = Point4D
                .from(Transformations.rotate(Point3D.project(toTrans), aroundX, aroundY, 0), 0);
        return rotated.plus(origin);
    }

    @Override
    public double getTrajectoryDuration() {
        return 0;
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToReal(getCachePoint()).getX();
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        return 0;
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToReal(getCachePoint()).getY();
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        return 0;
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToReal(getCachePoint()).getZ();
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        return 0;
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        refreshCache(timeInSeconds);
        return transformToReal(getCachePoint()).getAngle();
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        return 0;
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
        public abstract Point4D getPoint();

        public abstract double getTimeMark();
    }

}
