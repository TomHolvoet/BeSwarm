package solvers;

import control.Pid4dParameters;
import control.dto.Pose;
import control.dto.Velocity;
import ilog.concert.IloException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Hoang Tung Dinh */
public final class ExampleRatsCP {
  private static final Logger logger = LoggerFactory.getLogger(ExampleRatsCP.class);

  private ExampleRatsCP() {}

  public static void main(String[] args) {
    // this one will not be used in the future. We will use LD_LIBRARY_PATH instead.
    System.load(
        "/Users/Tung/Applications/IBM/ILOG/CPLEX_Studio1263/cplex/bin/x86-64_osx"
            + "/libcplex1263.jnilib");

    try {
      long runningTime = 0;
      for (int i = 0; i < 1000; i++) {
        final long startTime = System.nanoTime();
        final RatsCPSolver ratsCP =
            RatsCPSolver.builder()
                .withCurrentPose(Pose.createZeroPose())
                .withDesiredPose(Pose.builder().setX(3).setY(2).setZ(1).setYaw(0).build())
                .withCurrentRefVelocity(Velocity.createZeroVelocity())
                .withDesiredRefVelocity(Velocity.createZeroVelocity())
                .withPoseValid(1)
                .withOnTrajectory(1)
                .withPid4dParameters(Pid4dParameters.createWithDefaultValues())
                .build();
        logger.info(ratsCP.solve().toString());
        runningTime += System.nanoTime() - startTime;
      }
      logger.info("Running time is {}", (runningTime / 1000.0) / 1.0E9);
    } catch (IloException e) {
      logger.info("Solver exception!!!", e);
    }
  }
}
