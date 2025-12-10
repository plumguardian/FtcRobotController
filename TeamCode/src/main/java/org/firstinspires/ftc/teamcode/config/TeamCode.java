package org.firstinspires.ftc.teamcode.config;

import androidx.annotation.Nullable;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Map;

public class TeamCode {
    /** The name of the group used for the OpModes */
    public static final String GROUP_NAME = "Robotics Team";
    /** The fps for the camera (Logitech C270 HD Webcam) */
    public static final int cameraFps = 30;

    @SuppressWarnings("unused")
    public record HardwareGetter(HardwareMap hardwareMap, @Nullable Telemetry telemetry) {
        public final static Map<String, Motor.GoBILDA> motorRpmMap = Map.of(
                "fld", Motor.GoBILDA.RPM_312,
                "frd", Motor.GoBILDA.RPM_312,
                "bld", Motor.GoBILDA.RPM_312,
                "brd", Motor.GoBILDA.RPM_312
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
            final AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                    .setSuppressCalibrationWarnings(false)
                    .setNumThreads(4)
                    .build();
            // BuiltinCameraDirection.BACK can be used as a camera if it exists
            final VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTagProcessor);
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
                while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
                    Thread.sleep(sleepMillis);
                }
            }
        }
    }
}
