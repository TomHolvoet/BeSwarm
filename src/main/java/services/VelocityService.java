package services;

import commands.VelocityController;
import commands.VelocityServiceVisitor;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityService {
    VelocityController accept(VelocityServiceVisitor visitor);
}
