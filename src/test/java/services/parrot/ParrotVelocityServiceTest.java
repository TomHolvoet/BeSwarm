package services.parrot;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import geometry_msgs.Twist;
import geometry_msgs.Vector3;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.ros.node.topic.Publisher;
import services.VelocityService;
import utils.math.VelocityProvider;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Hoang Tung Dinh
 */
@RunWith(JUnitParamsRunner.class)
public class ParrotVelocityServiceTest {

    private static final double DELTA = 0.01;

    @Test
    @Parameters(source = VelocityProvider.class)
    public void testSendVelocityMessage(Pose pose, BodyFrameVelocity bodyFrameVelocity,
            InertialFrameVelocity inertialFrameVelocity) {
        final Publisher<Twist> publisher = mock(Publisher.class, RETURNS_DEEP_STUBS);
        when(publisher.getTopicName().toString()).thenReturn("/bebop/cmd_vel");

        final Twist twist = mock(Twist.class, RETURNS_DEEP_STUBS);
        when(publisher.newMessage()).thenReturn(twist);

        final Vector3 linear = mock(Vector3.class);
        final Vector3 angular = mock(Vector3.class);
        when(twist.getLinear()).thenReturn(linear);
        when(twist.getAngular()).thenReturn(angular);

        final VelocityService parrotVelocityService = ParrotVelocityService.builder().publisher(publisher).build();
        parrotVelocityService.sendVelocityMessage(inertialFrameVelocity, pose);

        checkMessageSetUp(bodyFrameVelocity, linear, angular);
        checkCorrectTwistMessageSent(publisher, twist);

    }

    private void checkCorrectTwistMessageSent(Publisher<Twist> publisher, Twist twist) {
        final ArgumentCaptor<Twist> twistArgumentCaptor = ArgumentCaptor.forClass(Twist.class);
        verify(publisher).publish(twistArgumentCaptor.capture());
        assertThat(twistArgumentCaptor.getValue()).isEqualTo(twist);
    }

    private void checkMessageSetUp(BodyFrameVelocity bodyFrameVelocity, Vector3 linear, Vector3 angular) {
        final ArgumentCaptor<Double> argumentCaptor = ArgumentCaptor.forClass(Double.class);

        verify(linear).setX(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isWithin(DELTA).of(bodyFrameVelocity.linearX());

        verify(linear).setY(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isWithin(DELTA).of(bodyFrameVelocity.linearY());

        verify(linear).setZ(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isWithin(DELTA).of(bodyFrameVelocity.linearZ());

        verify(angular).setZ(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isWithin(DELTA).of(bodyFrameVelocity.angularZ());
    }
}