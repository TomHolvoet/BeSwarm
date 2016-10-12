package virtualenvironment;

import ilog.concert.IloException;

/**
 * Constraint generator interface.
 *
 * @author Hoang Tung Dinh
 */
@FunctionalInterface
public interface ConstraintGenerator {

  void getConstraints(CpState cpState) throws IloException;
}
