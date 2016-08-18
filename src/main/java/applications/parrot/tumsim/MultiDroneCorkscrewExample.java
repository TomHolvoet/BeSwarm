package applications.parrot.tumsim;

import applications.trajectory.MultiTrajectoryServer;
import applications.trajectory.TrajectoryServer;
import com.google.common.collect.Lists;

import java.util.List;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public class MultiDroneCorkscrewExample implements MultiTrajectoryServer {

  private TrajectoryServer drone1Server;
  private TrajectoryServer drone2Server;

  public MultiDroneCorkscrewExample() {
    this.drone1Server = new TumSimulatorMultiDroneCorkscrewExample1();
      this.drone2Server = new TumSimulatorMultiDroneCorkscrewExample2();
  }

  @Override
  public List<TrajectoryServer> getAllDifferentTrajectories() {
    return Lists.newArrayList(drone1Server, drone2Server);
  }
}
