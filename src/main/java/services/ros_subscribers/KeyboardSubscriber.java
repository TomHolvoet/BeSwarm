package services.ros_subscribers;

import keyboard.Key;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Hoang Tung Dinh
 */
public final class KeyboardSubscriber extends MessagesSubscriberService<Key> implements RosKeySubject {
    private final Collection<RosKeyObserver> rosKeyObservers = new ArrayList<>();

    private KeyboardSubscriber(Subscriber<Key> subscriber) {
    	super(subscriber);
    }

    public static KeyboardSubscriber createKeyboardSubscriber(Subscriber<Key> subscriber) {
    	return new KeyboardSubscriber(subscriber);
    }

    @Override
    public void registerObserver(RosKeyObserver rosKeyObserver) {
        rosKeyObservers.add(rosKeyObserver);
    }

    @Override
    public void removeObserver(RosKeyObserver rosKeyObserver) {
        rosKeyObservers.remove(rosKeyObserver);
    }
}
