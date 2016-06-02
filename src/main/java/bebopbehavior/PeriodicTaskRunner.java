package bebopbehavior;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Hoang Tung Dinh
 */
public final class PeriodicTaskRunner {

    private PeriodicTaskRunner() {}

    public static void run(Runnable task, double rateInSeconds, double durationInSeconds) {
        final long initialDelay = 0;
        final long rateInMilliSeconds = (long) (durationInSeconds * 1000);

        final Future<?> future = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(task, initialDelay, rateInMilliSeconds, TimeUnit.MILLISECONDS);

        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);

        try {
            future.get(durationInMilliSeconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // TODO add log
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            future.cancel(true);
        }
    }
}
