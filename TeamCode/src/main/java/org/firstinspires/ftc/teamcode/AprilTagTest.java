package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

@SuppressWarnings("BusyWait")
@Autonomous(name = "April Tag Test", group = TeamCode.GROUP_NAME)
public class AprilTagTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        AprilTagProcessor aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        // BuiltinCameraDirection.BACK can be used as a camera if it exists
        VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTagProcessor);
        // TODO: is camera calibration being applied? maybe check vid and pid.

        waitForStart();

        if (isStopRequested()) return;

        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera State", visionPortal.getCameraState());
            telemetry.update();
            Thread.sleep(50);
        }

        telemetry.addLine("Starting...");
        telemetry.update();

        while (opModeIsActive()) {
            ArrayList<AprilTagDetection> detections = aprilTagProcessor.getDetections();
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
