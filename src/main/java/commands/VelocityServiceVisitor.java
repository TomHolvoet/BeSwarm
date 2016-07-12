package commands;

import services.Velocity2dService;
import services.Velocity3dService;
import services.Velocity4dService;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityServiceVisitor {
    VelocityController visit(Velocity2dService velocity2dService);

    VelocityController visit(Velocity3dService velocity3dService);

    VelocityController visit(Velocity4dService velocity4dService);
}
