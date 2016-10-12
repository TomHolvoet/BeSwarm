package virtualenvironment;

import ilog.concert.IloException;
import org.ros.message.Duration;
import org.ros.message.Time;

/** @author Hoang Tung Dinh */
public final class ConstraintGeneratorPheromone implements Pheromone, ConstraintGenerator {

  private final Time vanishingTime;
  private final ConstraintGenerator constraintGenerator;

  private ConstraintGeneratorPheromone(
      Time currentTime, Duration lifeTime, ConstraintGenerator constraintGenerator) {
    this.constraintGenerator = constraintGenerator;
    this.vanishingTime = currentTime.add(lifeTime);
  }

  public static ConstraintGeneratorPheromone create(
      Time currentTime, Duration lifeTime, ConstraintGenerator constraintGenerator) {
    return new ConstraintGeneratorPheromone(currentTime, lifeTime, constraintGenerator);
  }

  @Override
  public Time vanishingTime() {
    return vanishingTime;
  }

  @Override
  public void getConstraints(CpState cpState) throws IloException {
    constraintGenerator.getConstraints(cpState);
  }
}
