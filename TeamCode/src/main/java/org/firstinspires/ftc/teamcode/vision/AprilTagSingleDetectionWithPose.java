package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;
import org.opencv.core.Point;

@Deprecated
public class AprilTagSingleDetectionWithPose extends AprilTagSingleDetection {
    public final AprilTagProcessor.PoseSolver poseSolver;

    public AprilTagSingleDetectionWithPose(
            int id,
            int hamming,
            float decisionMargin,
            Point center,
            Point[] corners,
            AprilTagMetadata metadata,
            AprilTagPoseFtc ftcPose,
            AprilTagPoseRaw rawPose,
            Pose3D robotPose,
            long frameAcquisitionNanoTime,
            DistanceUnit distanceUnit,
            AprilTagProcessor.PoseSolver poseSolver
    ) {
        super(
                id,
                hamming,
                decisionMargin,
                center,
                corners,
                metadata,
                ftcPose,
                rawPose,
                robotPose,
                frameAcquisitionNanoTime,
                distanceUnit
        );
        this.poseSolver = poseSolver;
    }
}
