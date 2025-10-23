package org.firstinspires.ftc.teamcode;

import androidx.annotation.Nullable;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

public class TeamCode {
    /** The name of the group used for the OpModes */
    public static final String GROUP_NAME = "Robotics Team";

    @SuppressWarnings("unused")
    public static class HardwareGetter {
        private final HardwareMap hardwareMap;
        @Nullable private final Telemetry telemetry;

        public HardwareGetter(final HardwareMap hardwareMap, @Nullable final Telemetry telemetry) {
            this.hardwareMap = hardwareMap;
            this.telemetry = telemetry;
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
            final AprilTagProcessor aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
            // BuiltinCameraDirection.BACK can be used as a camera if it exists
            final VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTagProcessor);
            // TODO: is camera calibration (teamwebcamcalibrations.xml) being applied? maybe check vid and pid.
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
