package org.firstinspires.ftc.teamcode.auto.test;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.enableHardwareEncoders;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useMotorExVelo;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useVelocityControl;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MotorExVelo;

@Autonomous(name = "New Motor Test", group = TeamCode.GROUP_NAME)
public class NewMotorTest extends LinearOpMode {
    @Configurable
    private static class NewMotorTestConfig {
        public static int swap = 100000;
    }

    @Override
    public void runOpMode() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final MotorEx intakeLeft = useMotorExVelo
                ? new MotorExVelo(hardwareMap, "il", hardwareGetter.getMotorRpm("il"))
                : new MotorEx(hardwareMap, "il", hardwareGetter.getMotorRpm("il"));
        final MotorEx intakeRight = useMotorExVelo
                ? new MotorExVelo(hardwareMap, "ir", hardwareGetter.getMotorRpm("ir"))
                : new MotorEx(hardwareMap, "ir", hardwareGetter.getMotorRpm("ir"));

        if (useVelocityControl) {
            telemetry.addLine("Using velocity control");
            intakeLeft.setRunMode(Motor.RunMode.VelocityControl);
            intakeRight.setRunMode(Motor.RunMode.VelocityControl);
        } else {
            telemetry.addLine("Using raw power");
            intakeLeft.setRunMode(Motor.RunMode.RawPower);
            intakeRight.setRunMode(Motor.RunMode.RawPower);
        }

        if (enableHardwareEncoders || useMotorExVelo && useVelocityControl) {
            telemetry.addLine("Using hardware encoders");
            intakeLeft.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            intakeRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else
            telemetry.addLine("Not using hardware encoders");

        waitForStart();

        if (isStopRequested()) return;

        int c = 0;
        double v = 1;

        while (opModeIsActive()) {
            if (c++ > NewMotorTestConfig.swap) {
                c = 0;
                v = -v;
            }
            intakeLeft.set(v);
            intakeRight.set(v);
        }
    }
}
