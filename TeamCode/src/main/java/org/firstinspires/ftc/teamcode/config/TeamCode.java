package org.firstinspires.ftc.teamcode.config;

import android.util.Size;

import androidx.annotation.Nullable;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.ftcrobotcontroller.BuildConfig;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Map;

@SuppressWarnings("unused")
public class TeamCode {
    /** The name of the group used for the OpModes */
    public static final String GROUP_NAME = "Robotics Team";
    /** The fps for the camera (Logitech C270 HD Webcam) */
    public static final int CAMERA_FPS = 30;

    @Configurable
    public static class MessageConfig {
        public static String message = "";
        public static int blankLines = 2;
        public static void printMessage(Telemetry telemetry) {
            if (!message.isEmpty() && blankLines >= 0)
                telemetry.addLine("\n".repeat(blankLines) + message.replace("\\n", "\n"));
        }
    }

    public record HardwareGetter(HardwareMap hardwareMap, @Nullable Telemetry telemetry) {
        public final static Map<String, Motor.GoBILDA> motorRpmMap = Map.of(
        );

        public Motor.GoBILDA getMotorRpm(final String name) {
            final Motor.GoBILDA motor = motorRpmMap.get(name);
            if (motor == null) {
                if (telemetry != null) {
                    telemetry.addData("Motor not in RPM map", name);
                    telemetry.update();
                }
                return Motor.GoBILDA.NONE;
            }
            return motor;
        }

        public HardwareGetter(final HardwareMap hardwareMap) { this(hardwareMap, null); }

        public record Vision(AprilTagProcessor aprilTagProcessor, VisionPortal visionPortal) {}

        public record Motors(MotorEx frontLeft, MotorEx frontRight, MotorEx backLeft, MotorEx backRight) {}

        public IMU getIMU() {
            final IMU imu = hardwareMap.get(IMU.class, "imu");

            final RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                    RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
            );

            if (!imu.initialize(new IMU.Parameters(orientationOnRobot)) && telemetry != null) {
                telemetry.addLine("IMU failed to init");
                telemetry.update();
            }

            return imu;
        }

        public Vision getVision() {
            return getVision("Webcam 1");
        }

        public Vision getVision(final String webcamName) {
            final AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                    .setSuppressCalibrationWarnings(false)
                    .setNumThreads(4)
                    .setDrawAxes(BuildConfig.DEBUG)
                    .setDrawCubeProjection(BuildConfig.DEBUG)
                    .setDrawTagID(BuildConfig.DEBUG)
                    .setDrawTagOutline(BuildConfig.DEBUG)
                    .build();
//            aprilTagProcessor.setPoseSolver(AprilTagProcessor.PoseSolver.);
            aprilTagProcessor.setDecimation(2);
            // BuiltinCameraDirection.BACK can be used as a camera if it exists
            final VisionPortal visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, webcamName))
                    .addProcessors(aprilTagProcessor)
                    .setShowStatsOverlay(BuildConfig.DEBUG)
                    .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                    .setCameraResolution(new Size(640, 480))
                    .build();
            return new Vision(aprilTagProcessor, visionPortal);
        }

        public void waitForVision(final VisionPortal visionPortal) throws InterruptedException { waitForVision(visionPortal, 50); }

        @SuppressWarnings("BusyWait")
        public void waitForVision(final VisionPortal visionPortal, final long sleepMillis) throws InterruptedException {
            if (telemetry != null) { // Only do one check instead of every loop
                while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
                    telemetry.addData("Camera State", visionPortal.getCameraState());
                    telemetry.update();
                    Thread.sleep(sleepMillis);
                }
            } else {
                while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)
                    Thread.sleep(sleepMillis);
            }
        }
    }
}
