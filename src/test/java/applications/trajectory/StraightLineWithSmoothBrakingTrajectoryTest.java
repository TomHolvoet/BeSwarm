package applications.trajectory;

import org.junit.Test;

import static applications.trajectory.TestUtils.EPSILON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class StraightLineWithSmoothBrakingTrajectoryTest extends StraightLineTrajectory4DTest {

    private double brakingMark = 0.8d;

    @Override
    protected void createTargets() {
        target = Trajectories
                .newStraightLineWithSmoothBrakingTrajectory(before, after, speed, brakingMark);
        target2 = Trajectories
                .newStraightLineWithSmoothBrakingTrajectory(before, afterNotOrigin, speed,
                        brakingMark);
    }

    @Test
    public void testTargetVelocityCutoffPoint() {
        double brakePoint = after.getX() * brakingMark;
        target.getDesiredVelocityX(brakePoint - 0.5);
        assertNotEquals(0, target.getDesiredVelocityX(brakePoint - 0.5));
        target.getDesiredVelocityX(brakePoint + 0.5);
        assertEquals(0, target.getDesiredVelocityX(brakePoint + 0.5), EPSILON);
    }
}