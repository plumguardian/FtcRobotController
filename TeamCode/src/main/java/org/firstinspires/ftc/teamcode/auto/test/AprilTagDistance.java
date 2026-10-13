package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.config.CameraTestConfig;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

import lombok.val;

@Autonomous(name = "Distance Test", group = "Testing")
public class AprilTagDistance extends OpMode {
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private float oldDecimation = 1.5F;

    @Override
    public void init() {
        telemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
        val hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        val vision = hardwareGetter.visionBuilder().angleUnit(AngleUnit.DEGREES).get();
        visionPortal = vision.visionPortal();
        try {
            hardwareGetter.waitForVision(visionPortal);
        } catch (InterruptedException e) {
            telemetry.addData("Wait for vision was interrupted", e.getMessage());
            telemetry.update();
        }
        aprilTagProcessor = vision.aprilTagProcessor();
    }

    @Override
    public void start() {
        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
        visionPortal = null;
    }

    @Override
    public void loop() {
        CameraTestConfig.update(aprilTagProcessor, oldDecimation);
        oldDecimation = CameraTestConfig.decimation;

        final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections.isEmpty()) {
            telemetry.addLine("No tags found");
            return;
        }

        telemetry.addData("Pose solver time (ms)", aprilTagProcessor.getPerTagAvgPoseSolveTime());

        for (final AprilTagDetection rawDetection : detections) {
            if (!(rawDetection instanceof AprilTagClusterDetection detection)) {
                telemetry.addData("Unexpected AprilTag class", rawDetection.getClass().getSimpleName());
                continue;
            }

            telemetry.addData("cluster %", detection.percentClusterFound);

            telemetry.addData("X", detection.ftcPose.x); // positive X axis extends out to the right of the camera viewpoint
            telemetry.addData("Y", detection.ftcPose.y); // positive Y axis extends out in the direction the camera is pointing
            telemetry.addData("Z", detection.ftcPose.z); // positive Z axis extends Upwards in the camera viewpoint

            telemetry.addData("dist", detection.ftcPose.range);

            if (detection.metadata != null)
                telemetry.addData("unit", detection.metadata.distanceUnit.name());
        }
    }

    @Override
    public void stop() {
        PanelsCameraStream.INSTANCE.stopStream();
    }
}
