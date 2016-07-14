package commands;

import control.FiniteTrajectory4d;
import org.junit.Before;
import utils.TestUtils;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Hoang Tung Dinh
 */
public class PerformChoreographyBuilderTest extends AbstractBuilderTest {

    private FiniteTrajectory4d finiteTrajectory4d;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        finiteTrajectory4d = mock(FiniteTrajectory4d.class, RETURNS_MOCKS);
        when(finiteTrajectory4d.getTrajectoryDuration()).thenReturn(0.5);
    }

    @Override
    void createAndExecuteCommand(ArgumentHolder argumentHolder) {
        final Command performChoreography = PerformChoreography.builder()
                .withVelocityService(argumentHolder.velocityService())
                .withStateEstimator(argumentHolder.stateEstimator())
                .withFiniteTrajectory4d(finiteTrajectory4d)
                .withPidLinearXParameters(argumentHolder.pidLinearX())
                .withPidLinearYParameters(argumentHolder.pidLinearY())
                .withPidLinearZParameters(argumentHolder.pidLinearZ())
                .withPidAngularZParameters(argumentHolder.pidAngularZ())
                .build();

        performChoreography.execute();
    }

    @Override
    void checkCorrectExtraMethodsCalled() {
        verify(finiteTrajectory4d, atLeastOnce()).getTrajectoryDuration();
        TestUtils.verifyTrajectoryCalled(finiteTrajectory4d);
    }
}