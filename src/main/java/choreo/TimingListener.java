package choreo;

/**
 * Listener interface for timing events.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
interface TimingListener {
    void notifyNewTiming(double timeInSeconds);
}
