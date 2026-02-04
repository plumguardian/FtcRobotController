package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.camerastream.PanelsCameraStream;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@SuppressWarnings("BusyWait")
@Autonomous(name = "April Tag Test", group = TeamCode.GROUP_NAME)
public class AprilTagTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final TeamCode.HardwareGetter.Vision vision = hardwareGetter.getVision();

        waitForStart();

        if (isStopRequested()) return;

        final VisionPortal visionPortal = vision.visionPortal();
        hardwareGetter.waitForVision(visionPortal);
        final AprilTagProcessor aprilTagProcessor = vision.aprilTagProcessor();
        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);

        telemetry.addLine("Starting...");
        telemetry.update();

        while (opModeIsActive()) {
            final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
            if (detections.isEmpty())
                telemetry.addLine("No tags found");
            else
                for (final AprilTagDetection detection : detections) {
                    final Telemetry.Item item = telemetry.addData("id", detection.id);
                    if (detection.metadata != null) {
                        item.addData("name", detection.metadata.name);
                        item.addData("dist", DistanceUnit.INCH.fromUnit(detection.metadata.distanceUnit, detection.ftcPose.range));
                    } else {
                        item.addData("dist", detection.ftcPose.range);
                    }
                }

            telemetry.update();
            Thread.sleep(1000);
        }

        PanelsCameraStream.INSTANCE.stopStream();
    }
}
