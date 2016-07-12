package services;

import commands.VelocityServiceVisitor;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityService {
    void accept(VelocityServiceVisitor visitor);
}
