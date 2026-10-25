package org.firstinspires.ftc.teamcode.auto.test;

//#if !match
import com.bylazar.configurables.annotations.Configurable;
//#endif
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.camera.FieldCameraOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagClusterDetectionWithPose;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.MultiSolverAprilTagProcessorImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.val;

@Deprecated
@Autonomous(name = "PoseSolver Test", group = "Testing")
public class PoseSolverTest extends FieldCameraOpMode<MultiSolverAprilTagProcessorImpl> {
    public PoseSolverTest() {
        super(hardwareGetter -> hardwareGetter
                .multiSolverVisionBuilder()
                .angleUnit(AngleUnit.DEGREES)
                .getMultiSolver()
        );
    }

//#if !match
    @Configurable
//#endif
    static class PoseSolverTestSettings {
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
    public void loop() {
        updateCamera();

        final Map<AprilTagProcessor.PoseSolver, ArrayList<AprilTagDetection>> detections = aprilTagProcessor.getAllDetections();

        telemetry.addData("Pose solver time (ms)", aprilTagProcessor.getPerTagAvgPoseSolveTime());

        for (val entry : detections.entrySet()) {
            final AprilTagProcessor.PoseSolver solver = entry.getKey();
//#if !match
            field.setStyle("", PoseSolverTestSettings.colorMap.get(solver), 2.0);
//#endif
            telemetry.addData("Solver", solver.name());
            for (final AprilTagDetection rawDetection : entry.getValue()) {
                if (!(rawDetection instanceof AprilTagClusterDetection detection)) {
                    telemetry.addData("Unexpected AprilTag class", rawDetection.getClass().getSimpleName());
                    continue;
                }

                processCluster(detection);
            }
        }

//#if !match
        field.update();
//#endif
    }

    @Override
    protected void processCluster(AprilTagClusterDetection rawDetection) {
        val detection = (AprilTagClusterDetectionWithPose) rawDetection;

        telemetry.addData("Frame time (ns)", detection.frameAcquisitionNanoTime);
        telemetry.addData("cluster %", detection.percentClusterFound);
        if (detection.metadata != null) {
            telemetry.addData("dist (in)", DistanceUnit.INCH.fromUnit(detection.metadata.distanceUnit, detection.ftcPose.range));
        } else {
            telemetry.addData("dist", detection.ftcPose.range);
        }

        final Position robotPose = detection.robotPose.getPosition();
//#if !match
        field.moveCursor(DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.x), DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.y));
        field.circle(2.0);
//#endif
    }
}
