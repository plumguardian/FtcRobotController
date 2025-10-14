import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Autonomous(name = "April Tag Test", group = "Club")
public class AprilTagTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        AprilTagProcessor aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTagProcessor);

        waitForStart();

        if (isStopRequested()) return;

        visionPortal.resumeStreaming(); // Not necessary???? idk

        while (opModeIsActive()) {
            for (AprilTagDetection detection : aprilTagProcessor.getDetections()) {
                telemetry.addLine("Detected tag ID: " + detection.id);
            }
            telemetry.update();
        }

        visionPortal.stopStreaming();
    }
}
