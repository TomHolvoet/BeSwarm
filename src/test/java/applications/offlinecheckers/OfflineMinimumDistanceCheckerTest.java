package applications.offlinecheckers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/** @author Hoang Tung Dinh */
@RunWith(JUnitParamsRunner.class)
public class OfflineMinimumDistanceCheckerTest {

  private Object[] inputValues() {
    final FiniteTrajectory4d firstTrajectory =
        new FiniteTrajectory4d() {
          @Override
          public double getTrajectoryDuration() {
            return 1;
          }

          @Override
          public double getDesiredPositionX(double timeInSeconds) {
            return timeInSeconds;
          }

          @Override
          public double getDesiredPositionY(double timeInSeconds) {
            return timeInSeconds;
          }

          @Override
          public double getDesiredPositionZ(double timeInSeconds) {
            return 1;
          }

          @Override
          public double getDesiredAngleZ(double timeInSeconds) {
            return 0;
          }
        };

    final FiniteTrajectory4d secondTrajectory =
        new FiniteTrajectory4d() {
          @Override
          public double getTrajectoryDuration() {
            return 1;
          }

          @Override
          public double getDesiredPositionX(double timeInSeconds) {
            return timeInSeconds;
          }

          @Override
          public double getDesiredPositionY(double timeInSeconds) {
            return 2 - timeInSeconds;
          }

          @Override
          public double getDesiredPositionZ(double timeInSeconds) {
            return 1;
          }

          @Override
          public double getDesiredAngleZ(double timeInSeconds) {
            return 0;
          }
        };

    final FiniteTrajectory4d thirdTrajectory =
        new FiniteTrajectory4d() {
          @Override
          public double getTrajectoryDuration() {
            return 1;
          }

          @Override
          public double getDesiredPositionX(double timeInSeconds) {
            return timeInSeconds;
          }

          @Override
          public double getDesiredPositionY(double timeInSeconds) {
            return timeInSeconds - 2;
          }

          @Override
          public double getDesiredPositionZ(double timeInSeconds) {
            return 1;
          }

          @Override
          public double getDesiredAngleZ(double timeInSeconds) {
            return 0;
          }
        };

    return new Object[] {
      new Object[] {Lists.newArrayList(firstTrajectory, secondTrajectory), Result.HAS_VIOLATION},
      new Object[] {Lists.newArrayList(firstTrajectory, thirdTrajectory), Result.NO_VIOLATION}
    };
  }

  @Test
  @Parameters(method = "inputValues")
  public void testViolationChecker(List<FiniteTrajectory4d> trajectories, Result result) {
    final Optional<OfflineMinimumDistanceChecker.Violation> violation =
        OfflineMinimumDistanceChecker.create(1, trajectories).checkMinimumDistanceConstraint();
    final Result computedResult =
        violation.isPresent() ? Result.HAS_VIOLATION : Result.NO_VIOLATION;
    assertThat(computedResult).isEqualTo(result);
  }

  private enum Result {
    HAS_VIOLATION,
    NO_VIOLATION
  }
}
