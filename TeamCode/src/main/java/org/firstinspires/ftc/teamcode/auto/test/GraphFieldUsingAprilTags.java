package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Graph Field", group = TeamCode.GROUP_NAME)
public class GraphFieldUsingAprilTags extends OpMode {
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private FieldManager field;

    @Configurable
    private static class FieldGraphSettings {
        public static AprilTagProcessor.PoseSolver poseSolver = AprilTagProcessor.PoseSolver.OPENCV_ITERATIVE;
    }

    @Override
    public void init() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final TeamCode.HardwareGetter.Vision vision = hardwareGetter.getVision();
        // TODO: calibrate camera using multiple software
        visionPortal = vision.visionPortal();
        try {
            hardwareGetter.waitForVision(visionPortal);
        } catch (InterruptedException e) {
            telemetry.addData("Wait for vision was interrupted", e.getMessage());
            telemetry.update();
        }
        aprilTagProcessor = vision.aprilTagProcessor();

        field = PanelsField.INSTANCE.getField();
        field.setOffsets(FieldPresets.INSTANCE.getDEFAULT_FTC());
        field.update();
    }

    @Override
    public void start() {
        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
        visionPortal = null;
    }

    @Override
    public void loop() {
        aprilTagProcessor.setPoseSolver(FieldGraphSettings.poseSolver);
        final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections.isEmpty())
            telemetry.addLine("No tags found");
        else
            for (final AprilTagDetection detection : detections) {
                final Telemetry.Item item = telemetry.addData("id", detection.id);
                if (detection.metadata != null) {
                    item.addData("name", detection.metadata.name);
                    if (detection.ftcPose != null)
                        item.addData("dist", DistanceUnit.INCH.fromUnit(detection.metadata.distanceUnit, detection.ftcPose.range));
                } else if (detection.ftcPose != null) {
                    item.addData("dist", detection.ftcPose.range);
                }

                final Position robotPose = detection.robotPose.getPosition();
                item.addData("unit", robotPose.unit.name());
                field.moveCursor(DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.x), DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.y));
                field.circle(2.0);
            }
        field.update();
    }

    @Override
    public void stop() { PanelsCameraStream.INSTANCE.stopStream(); }
}
