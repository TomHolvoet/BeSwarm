package applications.trajectory;

import applications.trajectory.points.Point4D;
import com.google.common.collect.Lists;
import control.Trajectory4d;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class ZDropTrajectoryTest {

    private Point4D before;
    private Point4D after;
    private Trajectory4d target;
    private double negDrop = 2;
    private double freq = 3;
    private double speed = 1;
    private double bounddelta = 0.23;

    @Before
    public void setup() {
        before = Point4D.create(0, 0, 0, 0);
        after = Point4D.create(10, 0, 0, Math.PI / 2);
        target = Trajectories.newZDropLineTrajectory(before, after, speed, freq, negDrop);
        init();
    }

    @Test
    public void testDropRateAfter() {
        before = Point4D.create(0, 0, 10, 0);
        after = Point4D.create(10, 0, 10, Math.PI / 2);
        target = Trajectories.newZDropLineTrajectory(before, after, speed, freq, negDrop);
        init();
        target.getDesiredPositionZ(10);
        testParamDropRate(11, 20, 0);
    }

    @Test
    public void testDropRateHighZ() {
        before = Point4D.create(0, 0, 10, 0);
        after = Point4D.create(10, 0, 10, Math.PI / 2);
        target = Trajectories.newZDropLineTrajectory(before, after, speed, freq, negDrop);
        init();
        testParamDropRate(0, 10, freq);
    }

    @Test
    public void testComplexCase() {
        before = Point4D.create(0, 0, 10, 0);
        after = Point4D.create(0, 15, 10, 0);
        speed = 0.5d;
        freq = 4;
        negDrop = 2;
        target = Trajectories.newZDropLineTrajectory(before, after, speed, freq, negDrop);
        init();
        testParamDropRate(0, 60, freq);
    }

    private void testParamDropRate(double start, double duration, double expected) {
        List<Double> zresults = Lists.newArrayList();
        for (double i = start; i < duration; i += 0.1d) {
            zresults.add(target.getDesiredPositionZ(i));
        }
        int count = countOccurrence(zresults, after.getZ() - negDrop + bounddelta);
        assertEquals(expected, count, 0);
    }

    private void init() {
        target.getDesiredPositionX(0);
        target.getDesiredPositionY(0);
        target.getDesiredPositionZ(0);
        target.getDesiredAngleZ(0);
        target.getDesiredVelocityX(0);
        target.getDesiredVelocityY(0);
        target.getDesiredVelocityZ(0);
        target.getDesiredAngularVelocityZ(0);
    }

    private int countOccurrence(List<Double> zresults, double value) {
        int count = 0;
        boolean bound = false;
        boolean counted = false;
        for (double d : zresults) {
            if (d <= value) {
                if (!counted) {
                    count += 1;
                    counted = true;
                }
            } else {
                counted = false;
            }
        }
        return count;
    }

}