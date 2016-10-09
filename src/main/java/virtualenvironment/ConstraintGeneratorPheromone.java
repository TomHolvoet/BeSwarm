package virtualenvironment;

import ilog.concert.IloException;

/** @author Hoang Tung Dinh */
public interface ConstraintGeneratorPheromone {
  double lifeTimeInSecs();

  void addConstraints(CPInfo cpInfo) throws IloException;
}
