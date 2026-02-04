package org.firstinspires.ftc.teamcode.auto;

import com.bylazar.camerastream.PanelsCameraStream;
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
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Go To April Tag", group = TeamCode.GROUP_NAME)
public class GoToApril extends OpMode {
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private Follower follower;
    private FollowPathCommand pathCommand;

    @Override
    public void init() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final TeamCode.HardwareGetter.Vision vision = hardwareGetter.getVision();

        visionPortal = vision.visionPortal();
        try {
            hardwareGetter.waitForVision(visionPortal);
        } catch (InterruptedException e) {
            telemetry.addData("Wait for vision was interrupted", e.getMessage());
            telemetry.update();
        }
        aprilTagProcessor = vision.aprilTagProcessor();

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
        final double cpr = hardwareMap.get(DcMotor.class, "fld").getMotorType().getTicksPerRev(); // TODO: should i use a different method to get the cpr
        final DriveEncoderConstants driveEncoderConstants = new DriveEncoderConstants() // TODO: if we have odometry pods, use that plus IMU. Maybe use SolversLib's mecanum odometry
                .leftFrontMotorName("fld")
                .leftFrontEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .leftRearMotorName("bld")
                .leftRearEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .rightFrontMotorName("frd")
                .rightFrontEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .rightRearMotorName("brd")
                .rightRearEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .forwardTicksToInches((Math.PI * 3.75D) / (cpr * (1D+(46D/17D)) * (1D+(46D/11D)))) // TODO: check
                .strafeTicksToInches(2D * Math.PI / 2816.5D) // TODO: check
                .turnTicksToInches(0.0) // TODO: find
                .robotLength(0.0) // TODO: find
                .robotWidth(0.0); // TODO: find
        follower = new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .driveEncoderLocalizer(driveEncoderConstants)
                .build();
        follower.setStartingPose(new Pose());
    }

    @Override
    public void start() {
        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
        visionPortal = null;
        pathCommand = null;
    }

    @Override
    public void loop() {
        if (pathCommand != null && !pathCommand.isFinished())
            return;
        final List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections.isEmpty()) {
            telemetry.addLine("No tag found");
            telemetry.update();
            // Thread.sleep(50); FIXME: may not work inside a normal OpMode
        } else {
            @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
            final AprilTagDetection detection = detections.get(0);
            final Pose endPose = new Pose(detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.bearing, FTCCoordinates.INSTANCE).getAsCoordinateSystem(PedroCoordinates.INSTANCE); // FIXME: may be wrong system
            follower.update();
            final PathChain path = follower.pathBuilder()
                    .addPath(new BezierLine(follower.getPose(), endPose))
                    .setLinearHeadingInterpolation(follower.getHeading(), endPose.getHeading()) // TODO: is this needed?
                    .build();
            pathCommand = new FollowPathCommand(follower, path);
            pathCommand.schedule(true);
        }
    }

    @Override
    public void stop() { PanelsCameraStream.INSTANCE.stopStream(); }
}
