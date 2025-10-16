import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@SuppressWarnings("BusyWait")
@Autonomous(name = "April Tag Test", group = "Club")
public class AprilTagTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        AprilTagProcessor aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        // BuiltinCameraDirection.BACK can be used as a camera if it exists
        VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTagProcessor);

        waitForStart();

        if (isStopRequested()) return;

        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera State", visionPortal.getCameraState());
            telemetry.update();
            Thread.sleep(50);
        }

        while (opModeIsActive()) {
            for (AprilTagDetection detection : aprilTagProcessor.getDetections()) {
                if (detection.metadata != null)
                    telemetry.addLine("id: " + detection.id + " | name: " + detection.metadata.name);
                else
                    telemetry.addLine("id: " + detection.id);
            }
            telemetry.update();
            Thread.sleep(1000);
        }

        visionPortal.close();
    }
}
