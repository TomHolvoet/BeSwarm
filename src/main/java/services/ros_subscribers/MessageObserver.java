package services.ros_subscribers;

import org.ros.internal.message.Message;

/**
 * @author Hoang Tung Dinh
 */
public interface MessageObserver<T extends Message> {
    void onNewMessage(T message);
}
