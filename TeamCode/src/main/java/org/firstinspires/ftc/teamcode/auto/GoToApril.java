package org.firstinspires.ftc.teamcode.auto;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresetParams;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.CoordinateSystem;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Go To April Tag", group = TeamCode.GROUP_NAME)
public class GoToApril extends OpMode {
    @SuppressWarnings("unused")
    private enum Coords {
        FTC(FTCCoordinates.INSTANCE),
        PEDRO(PedroCoordinates.INSTANCE);

        public final CoordinateSystem system;

        Coords(CoordinateSystem system) {
            this.system = system;
        }
    }

    @SuppressWarnings("unused")
    private enum Field {
        DEFAULT_FTC(FieldPresets.INSTANCE.getDEFAULT_FTC()),
        PEDRO_PATHING(FieldPresets.INSTANCE.getPEDRO_PATHING()),
        PANELS(FieldPresets.INSTANCE.getPANELS()),
        ROAD_RUNNER(FieldPresets.INSTANCE.getROAD_RUNNER());

        public final FieldPresetParams field;

        Field(FieldPresetParams field) {
            this.field = field;
        }
    }

    @Configurable
    private static class GoToAprilTestConfig {
        public static int stopCount = 1000;
        public static Coords start = Coords.FTC;
        public static Coords end = Coords.PEDRO;
        public static DistanceUnit unit = DistanceUnit.INCH;
        public static Field fieldType = Field.DEFAULT_FTC;
        public static boolean yawDegrees = true;
        public static double standOff = 1.0;
    }

    private AprilTagProcessor aprilTagProcessor;
    private Follower follower;
    private FollowPathCommand pathCommand;
    private FieldManager field;
    private final List<AprilTagDetection> detections = new ArrayList<>(GoToAprilTestConfig.stopCount);
    private double tagPoseX = 0.0;
    private double tagPoseY = 0.0;
    private static final Style redStyle = new Style(
            "",    // fill color (no fill)
            "#FF0000", // outline color (red)
            2.0        // outline width
    );
    private static final Style blueStyle = new Style(
            "",    // fill color (no fill)
            "#0000FF", // outline color (red)
            2.0        // outline width
    );
    private boolean moving;
    private DualTelemetry dualTelemetry;

    @Override
    public void init() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final TeamCode.HardwareGetter.Vision vision = hardwareGetter.getVision();

        final VisionPortal visionPortal = vision.visionPortal();
        try {
            hardwareGetter.waitForVision(visionPortal);
        } catch (InterruptedException e) {
            telemetry.addData("Wait for vision was interrupted", e.getMessage());
            telemetry.update();
        }
        aprilTagProcessor = vision.aprilTagProcessor();

        final FollowerConstants followerConstants = new FollowerConstants();
        final MecanumConstants mecanumConstants = new MecanumConstants()
                .maxPower(1)
                .leftFrontMotorName("fld")
                .leftFrontMotorDirection(DcMotor.Direction.REVERSE)
                .leftRearMotorName("bld")
                .leftRearMotorDirection(DcMotor.Direction.REVERSE)
                .rightFrontMotorName("frd")
                .rightFrontMotorDirection(DcMotor.Direction.FORWARD)
                .rightRearMotorName("brd")
                .rightRearMotorDirection(DcMotor.Direction.FORWARD);
        final PathConstraints pathConstraints = new PathConstraints(0.995, 100.0);
//        final double cpr = hardwareMap.get(DcMotor.class, "fld").getMotorType().getTicksPerRev(); // TODO: should i use a different method to get the cpr
        final DriveEncoderConstants driveEncoderConstants = new DriveEncoderConstants() // TODO: if we have odometry pods, use that plus IMU. Maybe use SolversLib's mecanum odometry
                .leftFrontMotorName("fld")
                .leftFrontEncoderDirection(Encoder.REVERSE) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .leftRearMotorName("bld")
                .leftRearEncoderDirection(Encoder.REVERSE) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .rightFrontMotorName("frd")
                .rightFrontEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
                .rightRearMotorName("brd")
                .rightRearEncoderDirection(Encoder.FORWARD) // FIXME: test this and make sure that all ticks go up when going forward. If not, make it reverse
//                .forwardTicksToInches(-0.1) //(Math.PI * 3.75D) / (cpr * (1D+(46D/17D)) * (1D+(46D/11D)))) // TODO: check
//                .strafeTicksToInches(0.0) //2D * Math.PI / 2816.5D) // TODO: check
//                .turnTicksToInches(0.0) // TODO: find
                .robotLength(8.5) // TODO: find
                .robotWidth(11.0); // TODO: find
        follower = new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .driveEncoderLocalizer(driveEncoderConstants)
                .build();

        field = PanelsField.INSTANCE.getField();
        field.setOffsets(GoToAprilTestConfig.fieldType.field);
        field.update();

        dualTelemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());

        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
        pathCommand = null;
    }

    @Override
    public void init_loop() {
        final List<AprilTagDetection> newdetections = aprilTagProcessor.getDetections();
        if (newdetections.isEmpty()) {
            dualTelemetry.addLine("No tag found");
            // Thread.sleep(50); FIXME: may not work inside a normal OpMode
        } else {
            field.setStyle(redStyle);
            for (AprilTagDetection detection : newdetections) {
                if (detection.metadata == null)
                    continue;

                final VectorF tagpos = detection.metadata.fieldPosition;
                field.moveCursor(
                        GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(0)),
                        GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(1))
                );
                field.circle(2.0);
            }
        }
        field.update();
        dualTelemetry.update();
    }

    @Override
    public void loop() {
        if (pathCommand != null && !pathCommand.isFinished()) {
            follower.update();

            field.moveCursor(tagPoseX, tagPoseY);
            field.setStyle(redStyle);
            field.circle(2.0);

            final Pose robotPose = follower.poseTracker.getPose().getAsCoordinateSystem(GoToAprilTestConfig.start.system);
            final double robox = robotPose.getX();
            final double roboy = robotPose.getY();
            field.moveCursor(robox, roboy);
            field.setStyle(blueStyle);
            field.circle(2.0);

            field.update();

            dualTelemetry.addData("Tag Pose", tagPoseX + ", " + tagPoseY);
            dualTelemetry.addData("Robot Pose", robox + ", " + roboy);
        }

        if (moving) {
            dualTelemetry.update();
            return;
        }

        final List<AprilTagDetection> newdetections = aprilTagProcessor.getDetections();
        if (newdetections.isEmpty())
            dualTelemetry.addLine("No tag found");
            // Thread.sleep(50); FIXME: may not work inside a normal OpMode
        else
            for (AprilTagDetection detection : newdetections)
                if (detection.metadata != null)
                    detections.add(detection);  // FIXME: this accepts all tag IDs

        if (detections.size() >= GoToAprilTestConfig.stopCount) {
            double roboposx = 0.0;
            double roboposy = 0.0;
            double tagposx = 0.0;
            double tagposy = 0.0;
            double headingSin = 0.0;
            double headingCos = 0.0;
            double pointAtTagSin = 0.0;
            double pointAtTagCos = 0.0;
            for (AprilTagDetection detection : detections) {
                final Position robotpos = detection.robotPose.getPosition();
                roboposx += GoToAprilTestConfig.unit.fromUnit(robotpos.unit, robotpos.x);
                roboposy += GoToAprilTestConfig.unit.fromUnit(robotpos.unit, robotpos.y);

                final double h = detection.robotPose.getOrientation().getYaw(AngleUnit.RADIANS);
                headingSin += Math.sin(h);
                headingCos += Math.cos(h);

                final VectorF tagpos = detection.metadata.fieldPosition;
                tagposx += GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(0));
                tagposy += GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(1));

                final double o = detection.metadata.fieldOrientation
                        .toOrientation(AxesReference.EXTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS)
                        .firstAngle + Math.PI;
                pointAtTagSin += Math.sin(o);
                pointAtTagCos += Math.cos(o);
            }
            final int detectsize = detections.size();
            detections.clear();
            roboposx /= detectsize;
            roboposy /= detectsize;
            tagposx /= detectsize;
            tagposy /= detectsize;

            double pointAtTag = Math.atan2(pointAtTagSin, pointAtTagCos);
            final double mag = Math.hypot(pointAtTagCos, pointAtTagSin);
            tagposx -= GoToAprilTestConfig.standOff * (pointAtTagCos / mag);
            tagposy -= GoToAprilTestConfig.standOff * (pointAtTagSin / mag);
            double heading = Math.atan2(headingSin, headingCos);
            if (GoToAprilTestConfig.yawDegrees) {
                // TODO: make vars final after this is removed
                pointAtTag = Math.toDegrees(pointAtTag);
                heading = Math.toDegrees(heading);
            }

            tagPoseX = tagposx;
            tagPoseY = tagposy;

            follower.setStartingPose(new Pose(roboposx, roboposy, heading, GoToAprilTestConfig.start.system).getAsCoordinateSystem(GoToAprilTestConfig.end.system)); // FIXME: may be wrong system
            follower.update();

            final Pose endPose = new Pose(tagposx, tagposy, pointAtTag, GoToAprilTestConfig.start.system).getAsCoordinateSystem(GoToAprilTestConfig.end.system); // FIXME: may be wrong system
            final PathChain path = follower.pathBuilder()
                    .addPath(new BezierLine(follower.getPose(), endPose))
//                    .setLinearHeadingInterpolation(follower.getHeading(), endPose.getHeading()) // TODO: is this needed?
                    .build();
            pathCommand = new FollowPathCommand(follower, path);
            pathCommand.schedule(true);
            moving = true;
        }

        dualTelemetry.update();
    }

    @Override
    public void stop() {
        if (pathCommand != null) {
            dualTelemetry.addData("finished", pathCommand.isFinished());
            pathCommand.cancel();
        }
        PanelsCameraStream.INSTANCE.stopStream();
    }
}
