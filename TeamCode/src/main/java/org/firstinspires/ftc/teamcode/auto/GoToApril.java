package org.firstinspires.ftc.teamcode.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

//#if !match
import com.bylazar.camerastream.PanelsCameraStream;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresetParams;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
//#endif

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
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
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

import lombok.val;

@Autonomous(name = "Go To AprilTag", group = "Auto")
public class GoToApril extends OpMode {
//#if !match
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
//#endif
    private static class GoToAprilTestConfig {
        public static int stopCount = 1000;
        public static DistanceUnit unit = DistanceUnit.INCH;
//#if !match
        public static Field fieldType = Field.DEFAULT_FTC;
//#endif
        public static double standOff = 1.0;
    }

    private AprilTagProcessor aprilTagProcessor;
    private Follower follower;
    private PoseFactory poseFactory;
    private final List<AprilTagClusterDetection> detections = new ArrayList<>(GoToAprilTestConfig.stopCount);
//#if !match
    private FieldManager field;
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
//#endif
    private boolean moving;

    @Override
    public void init() {
//#if !match
        telemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
//#endif
        Scheduler.reset();

        val hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        val vision = hardwareGetter.visionBuilder().get();

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

//#if !match
        field = PanelsField.INSTANCE.getField();
        field.setOffsets(GoToAprilTestConfig.fieldType.field);
        field.update();

        PanelsCameraStream.INSTANCE.startStream(visionPortal, TeamCode.CAMERA_FPS);
//#endif

        moving = false;
    }

    @Override
    public void init_loop() {
        final List<AprilTagDetection> newDetections = aprilTagProcessor.getDetections();
        if (newDetections.isEmpty()) {
            telemetry.addLine("No tag found");
            // Thread.sleep(50); FIXME: may not work inside a normal OpMode
//#if !match
            field.update();
//#endif
            telemetry.update();
            return;
        }

//#if !match
        field.setStyle(redStyle);
//#endif
        for (AprilTagDetection rawDetection : newDetections) {
            if (!(rawDetection instanceof AprilTagClusterDetection detection)) {
                telemetry.addData("Unexpected AprilTag class", rawDetection.getClass().getSimpleName());
                continue;
            }

            if (detection.metadata == null)
                continue;

//#if !match
            final VectorF tagpos = detection.metadata.fieldPosition;
            field.moveCursor(
                    GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(0)),
                    GoToAprilTestConfig.unit.fromUnit(detection.metadata.distanceUnit, tagpos.get(1))
            );
            field.circle(2.0);
//#endif
        }

//#if !match
        field.update();
//#endif
        telemetry.update();
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
            telemetry.addLine("No tag found");
            // Thread.sleep(50); // FIXME: may not work inside a normal OpMode
        else
            for (AprilTagDetection rawDetection : newdetections)
                if (rawDetection instanceof AprilTagClusterDetection detection)
                    if (detection.metadata != null)
                        detections.add(detection);  // FIXME: this accepts all tag IDs

        if (detections.size() < GoToAprilTestConfig.stopCount) {
            telemetry.update();
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
        final int detectSize = detections.size();
        detections.clear();
        roboposx /= detectSize;
        roboposy /= detectSize;
        tagposx /= detectSize;
        tagposy /= detectSize;

        double pointAtTag = Math.atan2(pointAtTagSin, pointAtTagCos);
        final double mag = Math.hypot(pointAtTagCos, pointAtTagSin);
        tagposx -= GoToAprilTestConfig.standOff * (pointAtTagCos / mag);
        tagposy -= GoToAprilTestConfig.standOff * (pointAtTagSin / mag);
        double heading = Math.atan2(headingSin, headingCos);

        final Pose start = poseFactory.of(roboposy + 72, 72 - roboposx, heading - Math.PI / 2);
        final Pose end = poseFactory.of(tagposy + 72, 72 - tagposx, pointAtTag - Math.PI / 2);

        follower.setPose(start);
        follower.update();
        final Path path = line(start, end).linear(start, end);

        Scheduler.schedule(follow(follower, path));

        moving = true;

        telemetry.update();
    }

    @Override
    public void stop() {
        telemetry.addData("Mode", follower.mode().name());
        follower.stop();
//#if !match
        PanelsCameraStream.INSTANCE.stopStream();
//#endif
    }
}
