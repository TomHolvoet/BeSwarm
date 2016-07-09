package services.crates;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import services.VelocityService;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Hoang Tung Dinh
 */
public abstract class CratesVelocityServiceTest {

    abstract void responseToMessage(ServiceResponseListener<VelocityResponse> serviceResponseListener);

    @Test
    public void testSendVelocityMessage() throws InterruptedException {
        final ServiceClient<VelocityRequest, VelocityResponse> serviceClient = mock(ServiceClient.class,
                RETURNS_DEEP_STUBS);
        when(serviceClient.newMessage()).thenReturn(mock(VelocityRequest.class));

        final VelocityService cratesVelocityService = CratesVelocityService.create(serviceClient);
        final InertialFrameVelocity inertialFrameVelocity = getInertialFrameVelocity();
        final Future<?> future = checkSendingMessageBeforeReponse(cratesVelocityService, inertialFrameVelocity);

        final ArgumentCaptor<VelocityRequest> velocityRequestArgumentCaptor = ArgumentCaptor.forClass(
                VelocityRequest.class);
        final ArgumentCaptor<ServiceResponseListener> serviceResponseListenerArgumentCaptor = ArgumentCaptor.forClass(
                ServiceResponseListener.class);
        verify(serviceClient, atLeastOnce()).call(velocityRequestArgumentCaptor.capture(),
                serviceResponseListenerArgumentCaptor.capture());

        checkCorrectVelocitySent(inertialFrameVelocity, velocityRequestArgumentCaptor);
        checkResponseToMessage(future, serviceResponseListenerArgumentCaptor);
    }

    private static InertialFrameVelocity getInertialFrameVelocity() {
        return Velocity.builder()
                    .linearX(1)
                    .linearY(2)
                    .linearZ(3)
                    .angularZ(-1)
                    .build();
    }

    private Future<?> checkSendingMessageBeforeReponse(final VelocityService cratesVelocityService,
            final InertialFrameVelocity inertialFrameVelocity) throws InterruptedException {
        final Future<?> future = Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                cratesVelocityService.sendVelocityMessage(inertialFrameVelocity, mock(Pose.class));
            }
        });

        TimeUnit.MILLISECONDS.sleep(300);

        assertThat(future.isDone()).isFalse();
        return future;
    }

    private void checkResponseToMessage(Future<?> future,
            ArgumentCaptor<ServiceResponseListener> serviceResponseListenerArgumentCaptor) throws InterruptedException {
        final ServiceResponseListener<VelocityResponse> serviceResponseListener =
                serviceResponseListenerArgumentCaptor.getValue();
        responseToMessage(serviceResponseListener);

        TimeUnit.MILLISECONDS.sleep(50);
        assertThat(future.isDone()).isTrue();
    }

    private static void checkCorrectVelocitySent(InertialFrameVelocity inertialFrameVelocity,
            ArgumentCaptor<VelocityRequest> velocityRequestArgumentCaptor) {
        final VelocityRequest velocityRequest = velocityRequestArgumentCaptor.getValue();
        verify(velocityRequest).setDx(inertialFrameVelocity.linearX());
        verify(velocityRequest).setDy(inertialFrameVelocity.linearY());
        verify(velocityRequest).setDz(inertialFrameVelocity.linearZ());
        verify(velocityRequest).setYaw(inertialFrameVelocity.angularZ());
    }
}