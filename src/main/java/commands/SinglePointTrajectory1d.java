package commands;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import control.Trajectory1d;

/**
 * @author Hoang Tung Dinh
 */
public final class SinglePointTrajectory1d implements Trajectory1d {
    private final double desiredPosition;
    private final double desiredVelocity;

    private SinglePointTrajectory1d(double desiredPosition, double desiredVelocity) {
        this.desiredPosition = desiredPosition;
        this.desiredVelocity = desiredVelocity;
    }

    public static SinglePointTrajectory1d create(double desiredPosition,
            double desiredVelocity) {return new SinglePointTrajectory1d(desiredPosition, desiredVelocity);}

    @Override
    public double getDesiredPosition(double timeInSeconds) {
        return desiredPosition;
    }

    @Override
    public double getDesiredVelocity(double timeInSeconds) {
        return desiredVelocity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SinglePointTrajectory1d that = (SinglePointTrajectory1d) o;
        return Double.compare(that.desiredPosition, desiredPosition) == 0 && Double.compare(that.desiredVelocity,
                desiredVelocity) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(desiredPosition, desiredVelocity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("desiredPosition", desiredPosition)
                .add("desiredVelocity", desiredVelocity)
                .toString();
    }
}
