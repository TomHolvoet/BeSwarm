package applications.parrot.tumsim;

import applications.trajectory.TrajectoryServer;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Initialization tests for TumSim example files.
 * Manually add new runnable examples to the getData() list upon creation.
 * These tests validate correct parameters passed to trajectory creators before attempting to use
 * such trajectories in simulation or real world experiments.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
@RunWith(Parameterized.class)
public class TumSimulatorExampleTest {
    private TrajectoryServer server;

    public TumSimulatorExampleTest(Class cl) {
        try {
            this.server = (TrajectoryServer) cl.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            fail("Instantiation with default constructor should be possible for "
                    + "TumSimulatorExamples");
        }
    }

    @Parameterized.Parameters
    public static Collection<? extends Class> getData() {
        return Lists.newArrayList(TumRunSimpleLinePattern.class,
                TumRunStraightLinePattern.class, TumSimulatorCircleExample.class,
                TumSimulatorComplexExample.class, TumSimulatorCorkscrewExample.class,
                TumSimulatorPendulumExample.class, TumSimulatorZDropExample.class);
    }

    @Test
    public void testTrajectoryInitialization() {
        FiniteTrajectory4d traj = server.getConcreteTrajectory();
        assertNotEquals(0, traj.getTrajectoryDuration());
    }

}