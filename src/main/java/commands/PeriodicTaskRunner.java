package commands;

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
        final long rateInMilliSeconds = (long) (rateInSeconds * 1000);

        final Future<?> future = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(task, initialDelay, rateInMilliSeconds, TimeUnit.MILLISECONDS);

        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);

        try {
            future.get(durationInMilliSeconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            // TODO add log
            // the task is cancelled if time ran out or the current thread is interrupted while waiting.
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
