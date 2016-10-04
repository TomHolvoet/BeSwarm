package solvers;

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
      long a = System.nanoTime();
      final RatsProblemAssembler ratsCP =
          RatsProblemAssembler.create(
              Pose.createZeroPose(),
              Pose.builder().setX(3).setY(2).setZ(1).setYaw(0).build(),
              Velocity.createZeroVelocity(),
              Velocity.createZeroVelocity(),
              2,
              1,
              1);
      ratsCP.buildModel();
      logger.info(ratsCP.solve().toString());
      System.out.println((System.nanoTime() - a) / 1.0E9);
    } catch (IloException e) {
      logger.info("Solver exception!!!", e);
    }
  }
}
