package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Deprecated
public class AprilTagClusterDetectionWithPose extends AprilTagClusterDetection {
    public final AprilTagProcessor.PoseSolver poseSolver;

    public AprilTagClusterDetectionWithPose(
            int percentClusterFound,
            AprilTagClusterMetadata metadata,
            DistanceUnit distanceUnit,
            AprilTagPoseFtc ftcPose,
            AprilTagPoseRaw rawPose,
            Pose3D robotPose,
            long frameAcquisitionNanoTime,
            AprilTagProcessor.PoseSolver poseSolver
    ) {
        super(
                percentClusterFound,
                metadata,
                distanceUnit,
                ftcPose,
                rawPose,
                robotPose,
                frameAcquisitionNanoTime
        );
        this.poseSolver = poseSolver;
    }
}
