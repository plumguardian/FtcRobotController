package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@SuppressWarnings("BusyWait")
@Autonomous(name = "Mecanum Drive Auto", group = TeamCode.GROUP_NAME)
public class MecanumDriveAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final TeamCode.HardwareGetter.Vision vision = hardwareGetter.getVision();

        // TODO: compare motor.getMotorType().getAchieveableMaxTicksPerSecond(); and gobildaType.getAchievableMaxTicksPerSecond();
        // TODO: do something that uses getMotorRpm
        // check Motor constructors

        final FollowerConstants followerConstants = new FollowerConstants()
                .mass(0.0) // TODO: find
                .forwardZeroPowerAcceleration(0.0) // TODO: find
                .lateralZeroPowerAcceleration(0.0) // TODO: find
                .centripetalScaling(0.0) // TODO: find
                .drivePIDFCoefficients(null) // TODO: find
                .drivePIDFSwitch(0.0) // TODO: find
                .headingPIDFCoefficients(null) // TODO: find
                .headingPIDFSwitch(0.0) // TODO: find
                .secondaryDrivePIDFCoefficients(null) // TODO: find
                .secondaryHeadingPIDFCoefficients(null) // TODO: find
                .secondaryTranslationalPIDFCoefficients(null) // TODO: find
                .useSecondaryDrivePIDF(true) // TODO: find
                .useSecondaryHeadingPIDF(true) // TODO: find
                .useSecondaryTranslationalPIDF(true) // TODO: find
                .holdPointHeadingScaling(0.0) // TODO: find
                .holdPointTranslationalScaling(0.0) // TODO: find
                .turnHeadingErrorThreshold(0.0) // TODO: find
                .automaticHoldEnd(true) // TODO: find
                .translationalIntegral(0.0) // TODO: find
                .translationalPIDFSwitch(0.0) // TODO: find
                .translationalPIDFCoefficients(null) // TODO: find
                .BEZIER_CURVE_SEARCH_LIMIT(0); // TODO: find
        final MecanumConstants mecanumConstants = new MecanumConstants()
                .maxPower(1)
                .leftFrontMotorName("fld")
                .leftFrontMotorDirection(DcMotor.Direction.REVERSE)
                .leftRearMotorName("bld")
                .leftRearMotorDirection(DcMotor.Direction.REVERSE)
                .rightFrontMotorName("frd")
                .rightFrontMotorDirection(DcMotor.Direction.FORWARD)
                .rightRearMotorName("brd")
                .rightRearMotorDirection(DcMotor.Direction.FORWARD)
                .xVelocity(0.0) // TODO: find
                .yVelocity(0.0) // TODO: find
                .motorCachingThreshold(0.0) // TODO: find
                .staticFrictionCoefficient(0.0) // TODO: find
                .nominalVoltage(12.0) // TODO: find
                .useBrakeModeInTeleOp(false) // TODO: find
                .useVoltageCompensation(false); // TODO: find
        mecanumConstants.setFrontLeftVector(new Vector()); // TODO: find
        final PathConstraints pathConstraints = new PathConstraints(0.995, 100);
        final DriveEncoderConstants driveEncoderConstants = new DriveEncoderConstants() // TODO: if we have odometry pods, use that plus IMU. Maybe use SolversLib's mecanum odometry
                .leftFrontMotorName("fld")
                .leftFrontEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .leftRearMotorName("bld")
                .leftRearEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .rightFrontMotorName("frd")
                .rightFrontEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .rightRearMotorName("brd")
                .rightRearEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .forwardTicksToInches(0.0) // TODO: find
                .strafeTicksToInches(0.0) // TODO: find
                .turnTicksToInches(0.0) // TODO: find
                .robotLength(0.0) // TODO: find
                .robotWidth(0.0); // TODO: find
        final Follower follower = new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .driveEncoderLocalizer(driveEncoderConstants)
                .build();
        follower.setStartingPose(new Pose());

        waitForStart();

        if (isStopRequested()) return;

        hardwareGetter.waitForVision(vision.visionPortal());
        final AprilTagProcessor aprilTagProcessor = vision.aprilTagProcessor();

        telemetry.addLine("Starting...");
        telemetry.update();

        while (opModeIsActive()) {
            final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
            if (!detections.isEmpty()) {
                @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
                final AprilTagDetection detection = detections.get(0);
                final Pose endPose = new Pose(detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.bearing, FTCCoordinates.INSTANCE).getAsCoordinateSystem(PedroCoordinates.INSTANCE); // FIXME: may be wrong system
                follower.update();
                final PathChain path = follower.pathBuilder()
                        .addPath(new BezierLine(follower.getPose(), endPose))
                         .setLinearHeadingInterpolation(follower.getHeading(), endPose.getHeading()) // TODO: is this needed?
                        .build();
                new FollowPathCommand(follower, path);
                break;
            }

            telemetry.addLine("No tag found");
            telemetry.update();
            Thread.sleep(50);
        }
    }
}
