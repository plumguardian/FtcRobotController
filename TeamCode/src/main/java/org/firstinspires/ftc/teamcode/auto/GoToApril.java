package org.firstinspires.ftc.teamcode.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresetParams;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.config.Constants;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Go To April Tag", group = TeamCode.GROUP_NAME)
public class GoToApril extends OpMode {
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
        public static DistanceUnit unit = DistanceUnit.INCH;
        public static Field fieldType = Field.DEFAULT_FTC;
        public static double standOff = 1.0;
    }

    private AprilTagProcessor aprilTagProcessor;
    private Follower follower;
    private Command command;
    private PoseFactory poseFactory;
    private FieldManager field;
    private final List<AprilTagClusterDetection> detections = new ArrayList<>(GoToAprilTestConfig.stopCount);
    private static final Style redStyle = new Style(
            "",
            "#FF0000",
            2.0
    );
    private static final Style blueStyle = new Style(
            "",
            "#0000FF",
            2.0
    );
    private boolean moving;
    private DualTelemetry dualTelemetry;

    @Override
    public void init() {
        Scheduler.reset();

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
        follower = Constants.create(hardwareMap);
        poseFactory = PoseFactory.radians();

        field = PanelsField.INSTANCE.getField();
        field.setOffsets(GoToAprilTestConfig.fieldType.field);
        field.update();

        dualTelemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());

        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
        command = null;
        moving = false;
    }

    @Override
    public void init_loop() {
        final List<AprilTagDetection> newDetections = aprilTagProcessor.getDetections();
        if (newDetections.isEmpty()) {
            dualTelemetry.addLine("No tag found");
            // Thread.sleep(50); FIXME: may not work inside a normal OpMode
            field.update();
            dualTelemetry.update();
            return;
        }

        field.setStyle(redStyle);
        for (AprilTagDetection rawDetection : newDetections) {
            if (!(rawDetection instanceof AprilTagClusterDetection detection)) {
                telemetry.addData("Unexpected april tag class", rawDetection.getClass().getSimpleName());
                continue;
            }

            if (detection.metadata == null)
                continue;

            final VectorF tagpos = detection.metadata.fieldPosition;
            field.moveCursor(
                    GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(0)),
                    GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(1))
            );
            field.circle(2.0);
        }

        field.update();
        dualTelemetry.update();
    }

    @Override
    public void loop() {
        if (moving) {
            follower.update();
            Scheduler.execute();
            return;
        }

        final List<AprilTagDetection> newdetections = aprilTagProcessor.getDetections();
        if (newdetections.isEmpty())
            dualTelemetry.addLine("No tag found");
            // Thread.sleep(50); // FIXME: may not work inside a normal OpMode
        else
            for (AprilTagDetection rawDetection : newdetections)
                if (rawDetection instanceof AprilTagClusterDetection detection)
                    if (detection.metadata != null)
                        detections.add(detection);  // FIXME: this accepts all tag IDs

        if (detections.size() < GoToAprilTestConfig.stopCount) {
            dualTelemetry.update();
            return;
        }

        double roboposx = 0.0;
        double roboposy = 0.0;
        double tagposx = 0.0;
        double tagposy = 0.0;
        double headingSin = 0.0;
        double headingCos = 0.0;
        double pointAtTagSin = 0.0;
        double pointAtTagCos = 0.0;
        for (AprilTagClusterDetection detection : detections) {
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

        // TODO: convert from ftc to pedro units
        final Pose start = poseFactory.of(roboposx, roboposy, heading);
        final Pose end = poseFactory.of(tagposx, tagposy, pointAtTag);

        follower.setPose(start);
        final Path path = line(start, end).linear(start, end);

        command = follow(follower, path);
        Scheduler.schedule(command);

        moving = true;

        dualTelemetry.update();
    }

    @Override
    public void stop() {
        if (command != null) {
            dualTelemetry.addData("Running", Scheduler.isRunning(command));
            command.cancel();
        }
        PanelsCameraStream.INSTANCE.stopStream();
    }
}
