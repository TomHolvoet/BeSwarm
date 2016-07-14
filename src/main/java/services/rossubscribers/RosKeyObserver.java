package services.rossubscribers;

import keyboard.Key;

/**
 * @author Hoang Tung Dinh
 */
public interface RosKeyObserver {
    /**
     * Updates the key pressed.
     *
     * @param key the pressed key
     */
    void update(Key key);
}
