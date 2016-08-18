package commands;

import control.dto.FlipDirection;
import services.FlipService;

/**
 * Flip command for parrot drones.
 *
 * @author Hoang Tung Dinh
 */
public abstract class ParrotFlip implements Command {

  private final FlipService flipService;
  private final FlipDirection flipDirection;

  protected ParrotFlip(FlipService flipService, FlipDirection flipDirection) {
    this.flipService = flipService;
    this.flipDirection = flipDirection;
  }

  @Override
  public void execute() {
    flipService.sendFlipMessage(flipDirection);
  }
}
