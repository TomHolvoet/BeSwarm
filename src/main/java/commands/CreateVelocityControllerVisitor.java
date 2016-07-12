package commands;

import control.Trajectory4d;
import services.Velocity2dService;
import services.Velocity3dService;
import services.Velocity4dService;

/**
 * @author Hoang Tung Dinh
 */
public class CreateVelocityControllerVisitor implements VelocityServiceVisitor {

    private final Trajectory4d trajectory4d;

    private CreateVelocityControllerVisitor(Trajectory4d trajectory4d) {
        this.trajectory4d = trajectory4d;
    }

    public static CreateVelocityControllerVisitor create(Trajectory4d trajectory4d) {
        return new CreateVelocityControllerVisitor(trajectory4d);
    }

    @Override
    public VelocityController visit(Velocity2dService velocity2dService) {
        return Velocity2dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity2dService(velocity2dService)
                .build();
    }

    @Override
    public VelocityController visit(Velocity3dService velocity3dService) {
        return Velocity3dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity3dService(velocity3dService)
                .build();
    }

    @Override
    public VelocityController visit(Velocity4dService velocity4dService) {
        return Velocity4dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity4dService(velocity4dService)
                .build();
    }
}
