package pidcontroller;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author Hoang Tung Dinh
 */
public class PidController1DTest {

    private static final double DELTA = 0.000001;

    @Test
    public void testCompute() {
        final PidParameters pidParameters = PidParameters.builder().kp(0.05).kd(1).ki(0).build();
        final PidController1D pidController1D = PidController1D.builder()
                .goalPoint(10)
                .goalVelocity(0)
                .parameters(pidParameters)
                .build();

        assertThat(pidController1D.compute(0, 0)).isWithin(DELTA).of(0.5);
        assertThat(pidController1D.compute(0.5, 0.5)).isWithin(DELTA).of(-0.025);
    }
}