package services.parrot;

import bebop_msgs.Ardrone3PilotingStateFlyingStateChanged;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;
import services.FlyingStateService;
import services.ros_subscribers.FlyingState;
import services.ros_subscribers.MessagesSubscriberService;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Hoang Tung Dinh
 */
public class BebopFlyingStateServiceTest {

    @Test
    public void testUpdateFlyingState() {
        final Subscriber<Ardrone3PilotingStateFlyingStateChanged> subscriber = mock(Subscriber.class);
        final MessagesSubscriberService<Ardrone3PilotingStateFlyingStateChanged> flyingStateSubscriber =
                MessagesSubscriberService
                .create(subscriber);

        final ArgumentCaptor<MessageListener> argumentCaptor = ArgumentCaptor.forClass(MessageListener.class);
        verify(subscriber).addMessageListener(argumentCaptor.capture());
        final MessageListener<Ardrone3PilotingStateFlyingStateChanged> messageListener = argumentCaptor.getValue();

        final FlyingStateService bebopFlyingStateService = BebopFlyingStateService.create(flyingStateSubscriber);
        assertThat(bebopFlyingStateService.getCurrentFlyingState()).isAbsent();

        checkUpdateNewState(messageListener, bebopFlyingStateService, (byte) 0, FlyingState.LANDED);
        checkUpdateNewState(messageListener, bebopFlyingStateService, (byte) 1, FlyingState.TAKING_OFF);
        checkUpdateNewState(messageListener, bebopFlyingStateService, (byte) 2, FlyingState.HOVERING);
        checkUpdateNewState(messageListener, bebopFlyingStateService, (byte) 3, FlyingState.FLYING);
        checkUpdateNewState(messageListener, bebopFlyingStateService, (byte) 4, FlyingState.LANDING);
        checkUpdateNewState(messageListener, bebopFlyingStateService, (byte) 5, FlyingState.EMERGENCY);
        checkUpdateNewState(messageListener, bebopFlyingStateService, (byte) 6, FlyingState.USER_TAKEOFF);
    }

    private void checkUpdateNewState(MessageListener<Ardrone3PilotingStateFlyingStateChanged> messageListener,
            FlyingStateService bebopFlyingStateService, byte newStateCode, FlyingState newState) {
        final Ardrone3PilotingStateFlyingStateChanged state = mock(Ardrone3PilotingStateFlyingStateChanged.class,
                RETURNS_MOCKS);
        when(state.getState()).thenReturn(newStateCode);
        messageListener.onNewMessage(state);
        assertThat(bebopFlyingStateService.getCurrentFlyingState()).hasValue(newState);
    }
}