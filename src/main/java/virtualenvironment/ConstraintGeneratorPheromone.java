package virtualenvironment;

/** @author Hoang Tung Dinh */
public interface ConstraintGeneratorPheromone {
  double lifeTimeInSecs();

  void addConstraint(CPInfo cpInfo);
}
