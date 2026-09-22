package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.teamcode.vision.AprilTagClusterDetectionWithPose;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.MultiSolverAprilTagProcessorAltImpl;

import java.util.List;

import lombok.val;

@Autonomous(name = "PoseSolver Test (Alt)", group = "Testing")
public class PoseSolverTestAlt extends OpMode {
    private VisionPortal visionPortal;
    private MultiSolverAprilTagProcessorAltImpl aprilTagProcessor;
    private FieldManager field;

    private float oldDecimation = 1.5F;

    @Override
    public void init() {
        val hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        val vision = hardwareGetter.multiSolverAltVisionBuilder().angleUnit(AngleUnit.DEGREES).getMultiSolverAlt();
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
        final float decimation = PoseSolverTest.PoseSolverTestSettings.decimation;
        if (decimation != oldDecimation) {
            oldDecimation = decimation;
            aprilTagProcessor.setDecimation(decimation);
        }

        final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

        telemetry.addData("Pose solver time (ms)", aprilTagProcessor.getPerTagAvgPoseSolveTime());

        for (AprilTagDetection rawDetection : detections) {
            if (!(rawDetection instanceof AprilTagClusterDetectionWithPose detection)) {
                telemetry.addData("Unexpected AprilTag class", rawDetection.getClass().getSimpleName());
                continue;
            }

            field.setStyle("", PoseSolverTest.PoseSolverTestSettings.colorMap.get(detection.poseSolver), 2.0);
            final Telemetry.Item item = telemetry.addData("Solver", detection.poseSolver.name());
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

        field.update();
    }

    @Override
    public void stop() { PanelsCameraStream.INSTANCE.stopStream(); }
}
