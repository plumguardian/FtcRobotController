package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Deprecated
public interface AprilTagProcessorFactory<T> {
    T create(
            OpenGLMatrix robotInCameraFrame,
            double fx, double fy,
            double cx, double cy,
            DistanceUnit outputUnitsLength,
            AngleUnit outputUnitsAngle,
            AprilTagLibrary tagLibrary,
            boolean drawAxes,
            boolean drawCube,
            boolean drawOutline,
            boolean drawTagID,
            AprilTagProcessor.TagFamily tagFamily,
            int threads,
            boolean suppressCalibrationWarnings
    );
}
