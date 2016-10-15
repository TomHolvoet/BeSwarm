package control;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/** @author Hoang Tung Dinh */
public class LinearPidController1DTest {

  private static final double DELTA = 0.000001;

  @Test
  public void testCompute() {
    final PidParameters pidParameters =
        PidParameters.builder().setKp(0.05).setKd(1).setKi(0).build();
    final Trajectory1d trajectory1d =
        new Trajectory1d() {
          @Override
          public double getDesiredPosition(double timeInSeconds) {
            return 10;
          }
        };
    final LinearPidController1d linearPidController1D =
        LinearPidController1d.create(pidParameters, trajectory1d);

    assertThat(linearPidController1D.computeNextResponse(0, 0, 0)).isWithin(DELTA).of(0.5);
    assertThat(linearPidController1D.computeNextResponse(0.5, 0.5, 0)).isWithin(DELTA).of(-0.025);
  }
}
