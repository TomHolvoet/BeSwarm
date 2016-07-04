package utils.math;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author Hoang Tung Dinh
 */
public final class TestUtils {
    private static final double DELTA = 0.01;

    public static void assertVelocityEqual(InertialFrameVelocity vel1, InertialFrameVelocity vel2) {
        assertThat(vel1.linearX()).isWithin(DELTA).of(vel2.linearX());
        assertThat(vel1.linearY()).isWithin(DELTA).of(vel2.linearY());
        assertThat(vel1.linearZ()).isWithin(DELTA).of(vel2.linearZ());
        assertThat(vel1.angularZ()).isWithin(DELTA).of(vel2.angularZ());
    }

    public static void assertVelocityEqual(BodyFrameVelocity vel1, BodyFrameVelocity vel2) {
        assertThat(vel1.linearX()).isWithin(DELTA).of(vel2.linearX());
        assertThat(vel1.linearY()).isWithin(DELTA).of(vel2.linearY());
        assertThat(vel1.linearZ()).isWithin(DELTA).of(vel2.linearZ());
        assertThat(vel1.angularZ()).isWithin(DELTA).of(vel2.angularZ());
    }

    public static void assertPoseEqual(Pose pose1, Pose pose2) {
        assertThat(pose1.x()).isWithin(DELTA).of(pose2.x());
        assertThat(pose1.y()).isWithin(DELTA).of(pose2.y());
        assertThat(pose1.z()).isWithin(DELTA).of(pose2.z());
        assertThat(pose1.yaw()).isWithin(DELTA).of(pose2.yaw());
    }
}
