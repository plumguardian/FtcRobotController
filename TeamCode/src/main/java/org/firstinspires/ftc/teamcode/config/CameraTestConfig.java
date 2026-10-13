package org.firstinspires.ftc.teamcode.config;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.auto.test.GraphFieldUsingAprilTags;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Configurable
public class CameraTestConfig {
    public static AprilTagProcessor.PoseSolver poseSolver = AprilTagProcessor.PoseSolver.OPENCV_SQPNP;
    public static float decimation = 1.5F;

    public static void update(AprilTagProcessor aprilTagProcessor, float oldDecimation) {
        if (decimation != oldDecimation)
            aprilTagProcessor.setDecimation(decimation);
        aprilTagProcessor.setPoseSolver(poseSolver);
    }
}
