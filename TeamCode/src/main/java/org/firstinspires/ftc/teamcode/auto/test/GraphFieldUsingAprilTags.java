package org.firstinspires.ftc.teamcode.auto.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.camera.FieldCameraOpMode;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Graph Field", group = "Testing")
public class GraphFieldUsingAprilTags extends FieldCameraOpMode<AprilTagProcessor> {
    public GraphFieldUsingAprilTags() {
        super(hardwareGetter -> hardwareGetter
                .visionBuilder()
                .angleUnit(AngleUnit.DEGREES)
                .get()
        );
    }

    @Override
    protected void processDetections(List<AprilTagDetection> detections) {
        telemetry.addData("Pose solver time (ms)", aprilTagProcessor.getPerTagAvgPoseSolveTime());
        super.processDetections(detections);
    }

    @Override
    protected void processCluster(AprilTagClusterDetection detection) {
        final Telemetry.Item item = telemetry.addData("Frame time (ns)", detection.frameAcquisitionNanoTime);
        item.addData("cluster %", detection.percentClusterFound);
        item.addData("robot pose on field", detection.robotPose); // Should be null for BIOBUZZ hives
        item.addData("tag bearing from robot", detection.ftcPose.bearing);
        // Lower cluster - 36.901 in
        // Upper cluster - 50.867 in
        // TODO: use height or roll to find which cluster is down for our alliance
        // NOTE: The pos of the cluster may be the center of the hive, not the center of the tags
        final double cameraHeight = 0.0; // TODO: measure
        item.addData("Height", detection.ftcPose.z + cameraHeight);
        // NOTE: < 90 = up
        item.addData("Roll", Math.abs(detection.ftcPose.roll));
        item.addData("Unit", detection.distanceUnit.name());
        item.addData("robotPose unit", detection.robotPose.getPosition().unit.name());
        if (detection.metadata != null) {
            item.addData("name", detection.metadata.name);
            item.addData("Metadata unit", detection.metadata.distanceUnit.name());
            item.addData("dist (in)", DistanceUnit.INCH.fromUnit(detection.metadata.distanceUnit, detection.ftcPose.range));
        } else {
            item.addData("dist", detection.ftcPose.range);
        }

        final Position robotPose = detection.robotPose.getPosition();
        field.moveCursor(DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.x), DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.y));
        field.circle(2.0);
    }
}
