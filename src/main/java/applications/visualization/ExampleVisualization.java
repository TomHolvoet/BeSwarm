package applications.visualization;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

import java.util.ArrayList;
import java.util.List;

/** @author Hoang Tung Dinh */
public final class ExampleVisualization {
  private ExampleVisualization() {}

  public static void main(String[] args) {
    final List<FiniteTrajectory4d> trajectory4dList = new ArrayList<>();
    trajectory4dList.add(TrajectoriesForTesting.getCorkscrew());
    trajectory4dList.add(TrajectoriesForTesting.getFastCircle());
    final MultiTrajectoryLogger multiTrajectoryLogger =
        MultiTrajectoryLogger.create(trajectory4dList, 120);
    multiTrajectoryLogger.startLogging();
  }
}
