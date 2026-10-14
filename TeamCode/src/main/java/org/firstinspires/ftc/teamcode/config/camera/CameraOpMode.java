package org.firstinspires.ftc.teamcode.config.camera;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.config.DualTelemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CameraOpMode<T extends AprilTagProcessor> extends OpMode {
    protected T aprilTagProcessor;
    private VisionPortal visionPortal;
    private float oldDecimation = 1.5F;

    @NotNull
    private final Function<
            TeamCode.@NotNull HardwareGetter,
            TeamCode.HardwareGetter.@NotNull Vision<T>
    > visionSupplier;

    @Configurable
    private static class CameraTestConfig {
        public static AprilTagProcessor.PoseSolver poseSolver = AprilTagProcessor.PoseSolver.OPENCV_SQPNP;
        public static float decimation = 1.5F;
    }

    @Override
    public void init() {
        telemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
        val hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        val vision = visionSupplier.apply(hardwareGetter);
        visionPortal = vision.visionPortal();
        try {
            hardwareGetter.waitForVision(visionPortal);
        } catch (InterruptedException e) {
            telemetry.addData("Wait for vision was interrupted", e.getMessage());
            telemetry.update();
        }
        aprilTagProcessor = vision.aprilTagProcessor();
    }

    @Override
    public void start() {
        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
        visionPortal = null;
    }

    @Override
    public void loop() {
        updateCamera();

        final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections.isEmpty()) {
            telemetry.addLine("No tags found");
            return;
        }

        processDetections(detections);
    }

    protected void updateCamera() {
        float decimation = CameraTestConfig.decimation;
        if (decimation != oldDecimation) {
            oldDecimation = decimation;
            aprilTagProcessor.setDecimation(decimation);
        }
        aprilTagProcessor.setPoseSolver(CameraTestConfig.poseSolver);
    }

    protected void processDetections(List<AprilTagDetection> detections) {
        for (final AprilTagDetection rawDetection : detections) {
            if (!(rawDetection instanceof AprilTagClusterDetection detection)) {
                telemetry.addData("Unexpected AprilTag class", rawDetection.getClass().getSimpleName());
                continue;
            }

            processCluster(detection);
        }
    }

    protected abstract void processCluster(AprilTagClusterDetection detection);

    @Override
    public void stop() {
        PanelsCameraStream.INSTANCE.stopStream();
    }
}
