package org.firstinspires.ftc.teamcode.auto.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.camera.FieldCameraOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagClusterDetectionWithPose;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.MultiSolverAprilTagProcessorAltImpl;

import java.util.List;

import dev.frozenmilk.sinister.loading.Pinned;
import lombok.val;

@Pinned
@Deprecated
@Autonomous(name = "PoseSolver Test (Alt)", group = "Testing")
public class PoseSolverTestAlt extends FieldCameraOpMode<MultiSolverAprilTagProcessorAltImpl> {
    public PoseSolverTestAlt() {
        super(hardwareGetter -> hardwareGetter
                .multiSolverAltVisionBuilder()
                .angleUnit(AngleUnit.DEGREES)
                .getMultiSolverAlt()
        );
    }

    @Override
    protected void processDetections(List<AprilTagDetection> detections) {
        telemetry.addData("Pose solver time (ms)", aprilTagProcessor.getPerTagAvgPoseSolveTime());
        super.processDetections(detections);
    }

    @Override
    protected void processCluster(AprilTagClusterDetection rawDetection) {
        val detection = (AprilTagClusterDetectionWithPose) rawDetection;

        field.setStyle("", PoseSolverTest.PoseSolverTestSettings.colorMap.get(detection.poseSolver), 2.0);
        telemetry.addData("Solver", detection.poseSolver.name());
        telemetry.addData("Frame time (ns)", detection.frameAcquisitionNanoTime);
        telemetry.addData("cluster %", detection.percentClusterFound);
        if (detection.metadata != null) {
            telemetry.addData("dist (in)", DistanceUnit.INCH.fromUnit(detection.metadata.distanceUnit, detection.ftcPose.range));
        } else {
            telemetry.addData("dist", detection.ftcPose.range);
        }

        final Position robotPose = detection.robotPose.getPosition();
        field.moveCursor(DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.x), DistanceUnit.INCH.fromUnit(robotPose.unit, robotPose.y));
        field.circle(2.0);
    }
}
