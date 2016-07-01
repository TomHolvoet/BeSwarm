package control.dto;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.*;

/**
 * @author Hoang Tung Dinh
 */
@RunWith(JUnitParamsRunner.class)
public class PoseTest {
    private Object[] poseValues() {
        final Pose p1 = Pose.builder().x(0).y(0).z(0).yaw(0).build();
        final Pose p2 = Pose.builder().x(0.2).y(0).z(0).yaw(0).build();
        final Pose p3 = Pose.builder().x(0.0009).y(-0.0009).z(0.00015).yaw(-0.00015).build();
        return new Object[]{new Object[]{p1, p2, false}, new Object[]{p1, p3, true}, new Object[] {p2, p3, false}};
    }

    @Test
    @Parameters(method = "poseValues")
    public void testAreSamePoseWithinEps(Pose p1, Pose p2, boolean areTheSame) {
        assertThat(Pose.areSamePoseWithinEps(p1, p2)).isEqualTo(areTheSame);
    }
}