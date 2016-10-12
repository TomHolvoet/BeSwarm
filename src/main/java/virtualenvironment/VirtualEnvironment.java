package virtualenvironment;

import ilog.concert.IloException;
import org.ros.message.Time;
import org.ros.time.TimeProvider;

import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/** @author Hoang Tung Dinh */
public final class VirtualEnvironment {

  private final PriorityBlockingQueue<ConstraintGeneratorPheromone> pheromones;

  private VirtualEnvironment(TimeProvider timeProvider, double refreshFrequencyInHz) {
    final long refreshRateInNanoSecs = (long) (1.0E9 / refreshFrequencyInHz);
    final Comparator<ConstraintGeneratorPheromone> comparator =
        (o1, o2) -> (int) o1.vanishingTime().compareTo(o2.vanishingTime());
    this.pheromones = new PriorityBlockingQueue<>(11, comparator);

    // refresh and remove outdated pheromones
    Executors.newSingleThreadScheduledExecutor()
        .scheduleWithFixedDelay(
            () -> {
              final Time currentTime = timeProvider.getCurrentTime();
              Pheromone pheromone = pheromones.peek();
              while (pheromone != null && pheromone.vanishingTime().compareTo(currentTime) <= 0) {
                pheromones.poll();
                pheromone = pheromones.peek();
              }
            },
            0,
            refreshRateInNanoSecs,
            TimeUnit.NANOSECONDS);
  }

  public static VirtualEnvironment create(TimeProvider timeProvider, double refreshFrequencyInHz) {
    return new VirtualEnvironment(timeProvider, refreshFrequencyInHz);
  }

  public void dropPheromone(ConstraintGeneratorPheromone constraintGeneratorPheromone) {
    pheromones.offer(constraintGeneratorPheromone);
  }

  public void getConstraint(CpState cpState) throws IloException {
    for (final ConstraintGenerator constraintGenerator : pheromones) {
      constraintGenerator.getConstraints(cpState);
    }
  }
}
