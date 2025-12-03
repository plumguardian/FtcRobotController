package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Graph Field", group = TeamCode.GROUP_NAME)
public class GraphFieldUsingAprilTags extends OpMode {
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private int fps;
    private FieldManager field;

    @Override
    public void init() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final TeamCode.HardwareGetter.Vision vision = hardwareGetter.getVision();
        visionPortal = vision.visionPortal();
        try {
            hardwareGetter.waitForVision(visionPortal);
        } catch (InterruptedException e) {
            throw new RuntimeException("[GraphFieldUsingAprilTags] Wait for vision was interrupted", e);
        }
        aprilTagProcessor = vision.aprilTagProcessor();
        fps = (int) visionPortal.getFps();
        telemetry.addData("FPS", fps);
        telemetry.update();

        field = PanelsField.INSTANCE.getField();
        field.setOffsets(FieldPresets.INSTANCE.getDEFAULT_FTC());
        field.update();
    }

    @Override
    public void start() {
        PanelsCameraStream.INSTANCE.startStream(visionPortal, fps);
        visionPortal = null;
    }

    @Override
    public void loop() {
        final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections.isEmpty())
            telemetry.addLine("No tags found");
        else
            for (final AprilTagDetection detection : detections) {
                final Telemetry.Item item = telemetry.addData("id", detection.id);
                if (detection.metadata != null)
                    item.addData("name", detection.metadata.name);
                item.addData("dist", Math.sqrt(Math.pow(detection.ftcPose.x, 2) + Math.pow(detection.ftcPose.y, 2) + Math.pow(detection.ftcPose.z, 2)));

                final Position robotPose = detection.robotPose.getPosition();
                field.moveCursor(robotPose.x, robotPose.y);
                field.circle(2.0);
            }
        field.update();
    }

    @Override
    public void stop() { PanelsCameraStream.INSTANCE.stopStream(); }
}
