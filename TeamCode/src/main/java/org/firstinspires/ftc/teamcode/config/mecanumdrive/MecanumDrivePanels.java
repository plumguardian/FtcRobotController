package org.firstinspires.ftc.teamcode.config.mecanumdrive;

//#if !match
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager.TelemetryWrapper;
//#endif
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

@SuppressWarnings("unused")
public class MecanumDrivePanels extends MecanumDrive {
//#if !match
    public final TelemetryWrapper panelsTelemetry;
//#endif

//#if !match
    public MecanumDrivePanels(final Motor frontLeft, final Motor frontRight, final Motor backLeft, final Motor backRight, final TelemetryWrapper panelsTelemetry) {
        this(true, frontLeft, frontRight, backLeft, backRight, panelsTelemetry);
    }

    public MecanumDrivePanels(final boolean autoInvert, final Motor frontLeft, final Motor frontRight, final Motor backLeft, final Motor backRight, final TelemetryWrapper panelsTelemetry) {
        super(autoInvert, frontLeft, frontRight, backLeft, backRight);
        this.panelsTelemetry = panelsTelemetry;
    }
//#endif

    public MecanumDrivePanels(final Motor frontLeft, final Motor frontRight, final Motor backLeft, final Motor backRight) {
        this(
                true,
                frontLeft,
                frontRight,
                backLeft,
                backRight
//#if !match
                , PanelsTelemetry.INSTANCE.getFtcTelemetry()
//#endif
        );
    }

    public MecanumDrivePanels(final boolean autoInvert, final Motor frontLeft, final Motor frontRight, final Motor backLeft, final Motor backRight) {
//#if !match
        this(autoInvert, frontLeft, frontRight, backLeft, backRight, PanelsTelemetry.INSTANCE.getFtcTelemetry());
//#else
//$$         super(autoInvert, frontLeft, frontRight, backLeft, backRight);
//#endif
    }

    @Override
    public void driveWithMotorPowers(final double frontLeftSpeed, final double frontRightSpeed, final double backLeftSpeed, final double backRightSpeed) {
//#if !match
        final double rightSideMultiplier = isRightSideInverted() ? -1.0 : 1.0;
        panelsTelemetry.addData("frontLeft", frontLeftSpeed * maxOutput);
        panelsTelemetry.addData("frontRight", frontRightSpeed * rightSideMultiplier * maxOutput);
        panelsTelemetry.addData("backLeft", backLeftSpeed * maxOutput);
        panelsTelemetry.addData("backRight", backRightSpeed * rightSideMultiplier * maxOutput);
        panelsTelemetry.update();
//#endif

        super.driveWithMotorPowers(frontLeftSpeed, frontRightSpeed, backLeftSpeed, backRightSpeed);
    }
}
