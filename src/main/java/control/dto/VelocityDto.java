package control.dto;

/**
 * @author Hoang Tung Dinh
 */
interface VelocityDto {
    /**
     * Returns the velocity in the x coordinate
     */
    double linearX();

    /**
     * Returns the velocity in the y coordinate
     */
    double linearY();

    /**
     * Returns the velocity in the z coordinate
     */
    double linearZ();

    /**
     * Returns the velocity in the z rotation (the yaw)
     */
    double angularZ();
}
