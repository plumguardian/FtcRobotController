package org.firstinspires.ftc.teamcode.config;

import com.bylazar.telemetry.TelemetryManager.TelemetryWrapper;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

@SuppressWarnings("unused")
public class MecanumDrivePanels extends MecanumDrive {
    public final TelemetryWrapper panelsTelemetry;

    public MecanumDrivePanels(final Motor frontLeft, final Motor frontRight, final Motor backLeft, final Motor backRight, final TelemetryWrapper panelsTelemetry) {
        this(true, frontLeft, frontRight, backLeft, backRight, panelsTelemetry);
    }

    public MecanumDrivePanels(final boolean autoInvert, final Motor frontLeft, final Motor frontRight, final Motor backLeft, final Motor backRight, final TelemetryWrapper panelsTelemetry) {
        super(autoInvert, frontLeft, frontRight, backLeft, backRight);
        this.panelsTelemetry = panelsTelemetry;
    }

    @Override
    public void driveWithMotorPowers(final double frontLeftSpeed, final double frontRightSpeed, final double backLeftSpeed, final double backRightSpeed) {
        final double rightSideMultiplier = isRightSideInverted() ? -1.0 : 1.0;
        panelsTelemetry.addData("frontLeft", frontLeftSpeed * maxOutput);
        panelsTelemetry.addData("frontRight", frontRightSpeed * rightSideMultiplier * maxOutput);
        panelsTelemetry.addData("backLeft", backLeftSpeed * maxOutput);
        panelsTelemetry.addData("backRight", backRightSpeed * rightSideMultiplier * maxOutput);
        panelsTelemetry.update();

        super.driveWithMotorPowers(frontLeftSpeed, frontRightSpeed, backLeftSpeed, backRightSpeed);
    }
}
