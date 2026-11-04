package org.firstinspires.ftc.teamcode.utility;

//#if !match
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
//#endif

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@Utility(name = "Motor Test", description = "Moves the motor named '" + MotorTest.ID + '\'')
public class MotorTest extends LinearOpMode {
    public static final String ID = "testMotor";

//#if !match
    @Configurable
//#endif
    private static class MotorTestPanels {
        public static double speed = 0.4;
    }

    @Override
    public void runOpMode() {
//#if !match
        telemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
//#endif
        final MotorEx motor = new MotorEx(hardwareMap, ID);
        motor.setInverted(false);
        motor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        motor.stopAndResetEncoder();
        motor.setRunMode(Motor.RunMode.RawPower);
        waitForStart();

        while (!isStopRequested()) {
            telemetry.addData("Position", motor.encoder.getPosition());
            telemetry.addData("Corrected Velocity", motor.encoder.getCorrectedVelocity());
            telemetry.addData("Raw Velocity", motor.encoder.getRawVelocity());
            telemetry.addData("Internal Velocity", motor.getVelocity());
            telemetry.update();
            motor.set(MotorTestPanels.speed);
        }

        motor.stopMotor();
    }
}
