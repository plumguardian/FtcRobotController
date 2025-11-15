package org.firstinspires.ftc.teamcode.auto;

import com.bylazar.camerastream.PanelsCameraStream;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
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
        final int fps = (int) visionPortal.getFps();
        telemetry.addData("FPS", fps);
        PanelsCameraStream.INSTANCE.startStream(visionPortal, fps);

        telemetry.addLine("Starting...");
        telemetry.update();

        while (opModeIsActive()) {
            final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
            if (detections.isEmpty())
                telemetry.addLine("No tags found");
            else
                for (final AprilTagDetection detection : detections) {
                    final Telemetry.Item item = telemetry.addData("id", detection.id);
                    if (detection.metadata != null)
                        item.addData("name", detection.metadata.name);
                }

            telemetry.update();
            Thread.sleep(1000);
        }

        PanelsCameraStream.INSTANCE.stopStream();
    }
}
