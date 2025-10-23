package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@SuppressWarnings("BusyWait")
@Autonomous(name = "April Tag Test", group = TeamCode.GROUP_NAME)
public class AprilTagTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        TeamCode.HardwareGetter.Vision vision = hardwareGetter.getVision();

        waitForStart();

        if (isStopRequested()) return;

        hardwareGetter.waitForVision(vision.visionPortal());
        AprilTagProcessor aprilTagProcessor = vision.aprilTagProcessor();

        telemetry.addLine("Starting...");
        telemetry.update();

        while (opModeIsActive()) {
            List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
            if (detections.isEmpty())
                telemetry.addLine("No tags found");
            else
                for (AprilTagDetection detection : detections) {
                    Telemetry.Item item = telemetry.addData("id", detection.id);
                    if (detection.metadata != null)
                                item.addData("name", detection.metadata.name);
                }

            telemetry.update();
            Thread.sleep(1000);
        }
    }
}
