package services.rossubscribers;

/**
 * @author Hoang Tung Dinh
 */
public interface RosKeySubject {
    /**
     * Registers an observer for the keyboard.
     *
     * @param rosKeyObserver the observer to be registered
     */
    void registerObserver(RosKeyObserver rosKeyObserver);

    /**
     * Removes an observer for the keyboard.
     *
     * @param rosKeyObserver the observer to be removed
     */
    void removeObserver(RosKeyObserver rosKeyObserver);
}
