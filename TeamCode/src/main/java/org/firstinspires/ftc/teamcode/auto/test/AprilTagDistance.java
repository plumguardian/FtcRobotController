package org.firstinspires.ftc.teamcode.auto.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.config.camera.CameraOpMode;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Distance Test", group = "Testing")
public class AprilTagDistance extends CameraOpMode<AprilTagProcessor> {
    public AprilTagDistance() {
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
        telemetry.addData("cluster %", detection.percentClusterFound);

        telemetry.addData("X", detection.ftcPose.x); // positive X axis extends out to the right of the camera viewpoint
        telemetry.addData("Y", detection.ftcPose.y); // positive Y axis extends out in the direction the camera is pointing
        telemetry.addData("Z", detection.ftcPose.z); // positive Z axis extends Upwards in the camera viewpoint

        telemetry.addData("dist", detection.ftcPose.range);

        if (detection.metadata != null)
            telemetry.addData("unit", detection.metadata.distanceUnit.name());
    }
}
