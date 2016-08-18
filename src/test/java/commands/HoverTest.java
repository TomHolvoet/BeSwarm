package commands;

import control.localization.StateEstimator;
import org.junit.Test;
import services.VelocityService;

import static org.mockito.Mockito.mock;

/**
 * Tests for {@link Hover}.
 *
 * @author Hoang Tung Dinh
 */
public class HoverTest {
  @Test(expected = NullPointerException.class)
  public void testMissingTimeProvider() {
    final Command hover =
        Hover.create(mock(VelocityService.class), mock(StateEstimator.class), 100, null);
    hover.execute();
  }
}
