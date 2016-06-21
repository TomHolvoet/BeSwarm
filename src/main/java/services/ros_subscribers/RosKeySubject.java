package services.ros_subscribers;

/**
 * @author Hoang Tung Dinh
 */
public interface RosKeySubject {
    void registerObserver(RosKeyObserver rosKeyObserver);

    void removeObserver(RosKeyObserver rosKeyObserver);
}
