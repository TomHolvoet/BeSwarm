package applications.parrot.tumsim;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TumSimulatorCircleExample extends AbstractTumSimulatorExample {

    protected TumSimulatorCircleExample(String nodeName) {
        super("TumRunExampleTrajectory2");
    }

    @Override
    public FiniteTrajectory4d getConcreteTrajectory() {
        return TrajectoriesForTesting.getSlowCircle();
    }
}
