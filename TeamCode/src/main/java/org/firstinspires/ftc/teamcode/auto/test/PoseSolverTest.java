package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.MultiSolverAprilTagProcessorImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kotlin.collections.MapsKt;
import lombok.val;

@Autonomous(name = "PoseSolver Test", group = "Testing")
public class PoseSolverTest extends OpMode {
    private VisionPortal visionPortal;
    private MultiSolverAprilTagProcessorImpl aprilTagProcessor;
    private FieldManager field;

    private float oldDecimation = 1.5F;
    @Configurable
    private static class PoseSolverTestSettings {
        public static float decimation = 1.5F;
        public static Map<AprilTagProcessor.PoseSolver, String> colorMap = new HashMap<>(Map.of(
                AprilTagProcessor.PoseSolver.APRILTAG_BUILTIN,      "#FF453A",
                AprilTagProcessor.PoseSolver.OPENCV_ITERATIVE,      "#FF9F0A",
                AprilTagProcessor.PoseSolver.OPENCV_SOLVEPNP_EPNP,  "#FFD60A",
                AprilTagProcessor.PoseSolver.OPENCV_IPPE,           "#30D158",
                AprilTagProcessor.PoseSolver.OPENCV_IPPE_SQUARE,    "#0A84FF",
                AprilTagProcessor.PoseSolver.OPENCV_SQPNP,          "#BF5AF2"
        ));
    }

    @Override
    public void init() {
        val hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        val vision = hardwareGetter.multiSolverVisionBuilder().angleUnit(AngleUnit.DEGREES).getMultiSolver();
        // TODO: calibrate camera using multiple software
        visionPortal = vision.visionPortal();
        try {
            hardwareGetter.waitForVision(visionPortal);
        } catch (InterruptedException e) {
            telemetry.addData("Wait for vision was interrupted", e.getMessage());
            telemetry.update();
        }
        aprilTagProcessor = vision.aprilTagProcessor();

        field = PanelsField.INSTANCE.getField();
        field.setOffsets(FieldPresets.INSTANCE.getDEFAULT_FTC());
        field.update();
    }

    @Override
    public void start() {
        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
        visionPortal = null;
    }

    @Override
    public void loop() {
        final float decimation = PoseSolverTestSettings.decimation;
        if (decimation != oldDecimation) {
            oldDecimation = decimation;
            aprilTagProcessor.setDecimation(decimation);
        }

        final Map<AprilTagProcessor.PoseSolver, ArrayList<AprilTagDetection>> detections = aprilTagProcessor.getAllDetections();

        telemetry.addData("Pose solver time (ms)", aprilTagProcessor.getPerTagAvgPoseSolveTime());

        for (val entry : detections.entrySet()) {
            final AprilTagProcessor.PoseSolver solver = entry.getKey();
            field.setStyle("", PoseSolverTestSettings.colorMap.get(solver), 2.0);
            final Telemetry.Item item = telemetry.addData("Solver", solver.name());
            for (final AprilTagDetection rawDetection : entry.getValue()) {
                if (!(rawDetection instanceof AprilTagClusterDetection detection)) {
                    item.addData("Unexpected AprilTag class", rawDetection.getClass().getSimpleName());
                    continue;
                }

                item.addData("Frame time (ns)", detection.frameAcquisitionNanoTime);
                item.addData("cluster %", detection.percentClusterFound);
                if (detection.metadata != null) {
                    item.addData("dist (in)", DistanceUnit.INCH.fromUnit(detection.metadata.distanceUnit, detection.ftcPose.range));
                } else {
                    item.addData("dist", detection.ftcPose.range);
                }

                final Position robotPose = detection.robotPose.getPosition();
                field.moveCursor(DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.x), DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.y));
                field.circle(2.0);
            }
        }

        field.update();
    }

    @Override
    public void stop() { PanelsCameraStream.INSTANCE.stopStream(); }
}
