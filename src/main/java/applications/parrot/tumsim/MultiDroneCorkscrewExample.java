package applications.parrot.tumsim;

import applications.trajectory.MultiTrajectoryServer;
import applications.trajectory.TrajectoryServer;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Example file for providing trajectories for 2 drones with 2 implementation of corkscrew
 * trajectories around one another. these trajectories keep a minimum distance of 1 m at all times.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class MultiDroneCorkscrewExample implements MultiTrajectoryServer {

  private final TrajectoryServer drone1Server;
  private final TrajectoryServer drone2Server;

  /** Default constructor. */
  public MultiDroneCorkscrewExample() {
    this.drone1Server = new TumSimulatorMultiDroneCorkscrewExample1();
    this.drone2Server = new TumSimulatorMultiDroneCorkscrewExample2();
  }

  @Override
  public List<TrajectoryServer> getAllDifferentTrajectories() {
    return Lists.newArrayList(drone1Server, drone2Server);
  }
}
