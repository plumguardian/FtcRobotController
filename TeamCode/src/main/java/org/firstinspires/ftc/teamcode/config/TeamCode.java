package org.firstinspires.ftc.teamcode.config;

import android.util.Size;

import androidx.annotation.NonNull;
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
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MotorExVelo;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.MultiSolverAprilTagProcessorImpl;
import org.jetbrains.annotations.Contract;

import java.util.Map;
import java.util.function.Function;

import lombok.Builder;

@SuppressWarnings("unused")
public class TeamCode {
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

        public record Vision<T extends AprilTagProcessor>(
                T aprilTagProcessor,
                VisionPortal visionPortal
        ) {}

        public record Motors(MotorEx frontLeft, MotorEx frontRight, MotorEx backLeft, MotorEx backRight) {}

        @NonNull
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

        @NonNull
        @Contract("_, _ -> new")
        @Builder(builderMethodName = "visionBuilder", buildMethodName = "get")
        public Vision<AprilTagProcessor> createVision(
                String webcamName,
                AngleUnit angleUnit
        ) {
            webcamName = webcamName == null ? "Webcam 1" : webcamName;
            angleUnit = angleUnit == null ? AngleUnit.RADIANS : angleUnit;

            final AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                    .setSuppressCalibrationWarnings(false)
                    .setNumThreads(4)
                    .setDrawAxes(BuildConfig.DEBUG)
                    .setDrawCubeProjection(BuildConfig.DEBUG)
                    .setDrawTagID(BuildConfig.DEBUG)
                    .setDrawTagOutline(BuildConfig.DEBUG)
//                    .setCameraPose()
                    .setOutputUnits(DistanceUnit.INCH, angleUnit)
                    .build();

            // TODO: test SQPNP, ITERATIVE, IPPE_SQUARE, and IPPE (BUILTIN and EPNP are not good for this use)
            aprilTagProcessor.setPoseSolver(AprilTagProcessor.PoseSolver.OPENCV_SQPNP);
            aprilTagProcessor.setDecimation(1.5F);

            // BuiltinCameraDirection.BACK can be used as a camera if it exists
            final VisionPortal visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, webcamName))
                    .addProcessors(aprilTagProcessor)
                    .setShowStatsOverlay(BuildConfig.DEBUG)
                    .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                    .setCameraResolution(new Size(640, 480))
                    .build();

            return new Vision<>(aprilTagProcessor, visionPortal);
        }

        @Deprecated
        @NonNull
        @Contract("_, _ -> new")
        @Builder(builderMethodName = "multiSolverVisionBuilder", buildMethodName = "getMultiSolver")
        public Vision<MultiSolverAprilTagProcessorImpl> createMultiSolverVision(
                String webcamName,
                AngleUnit angleUnit
        ) {
            webcamName = webcamName == null ? "Webcam 1" : webcamName;
            angleUnit = angleUnit == null ? AngleUnit.RADIANS : angleUnit;

            final MultiSolverAprilTagProcessorImpl aprilTagProcessor = new MultiSolverAprilTagProcessorBuilder()
                    .setSuppressCalibrationWarnings(false)
                    .setNumThreads(5)
                    .setDrawAxes(BuildConfig.DEBUG)
                    .setDrawCubeProjection(BuildConfig.DEBUG)
                    .setDrawTagID(BuildConfig.DEBUG)
                    .setDrawTagOutline(BuildConfig.DEBUG)
//                    .setCameraPose()
                    .setOutputUnits(DistanceUnit.INCH, angleUnit)
                    .build();

            aprilTagProcessor.setDecimation(1.5F);

            // BuiltinCameraDirection.BACK can be used as a camera if it exists
            final VisionPortal visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, webcamName))
                    .addProcessors(aprilTagProcessor)
                    .setShowStatsOverlay(BuildConfig.DEBUG)
                    .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                    .setCameraResolution(new Size(640, 480))
                    .build();

            return new Vision<>(aprilTagProcessor, visionPortal);
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

        @NonNull
        @Contract(" -> new")
        public Motors getMotors() {
            return getMotors(org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useMotorExVelo);
        }

        @NonNull
        @Contract("_ -> new")
        public Motors getMotors(boolean useMotorExVelo) {
            final Function<String, MotorEx> motor = useMotorExVelo
                    ? name -> new MotorExVelo(hardwareMap, name, getMotorRpm(name))
                    : name -> new MotorEx(hardwareMap, name, getMotorRpm(name));

            return new Motors(
                    motor.apply("fld"),
                    motor.apply("frd"),
                    motor.apply("bld"),
                    motor.apply("brd")
            );
        }
    }
}
