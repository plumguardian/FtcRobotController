package org.firstinspires.ftc.teamcode.auto;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.enableHardwareEncoders;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useMotorExVelo;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useVelocityControl;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MecanumDrivePanels;
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MotorExVelo;

@Autonomous(name = "Move Out Of Start", group = TeamCode.GROUP_NAME)
public class MoveOutOfStart extends OpMode {
    private MecanumDrivePanels mecanumDrive;

    @Configurable
    private static class AutoMoveConfigPanels {
        // Don't manually change values. Control it with panels.
        public static double movePower = 0.2;
    }

    @Override
    public void init() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final TeamCode.HardwareGetter.Motors motors = hardwareGetter.getMotors();
        final MotorEx frontLeftDrive = motors.frontLeft();
        final MotorEx frontRightDrive = motors.frontRight();
        final MotorEx backLeftDrive = motors.backLeft();
        final MotorEx backRightDrive = motors.backRight();
        // TODO: compare motor.getMotorType().getAchieveableMaxTicksPerSecond(); and gobildaType.getAchievableMaxTicksPerSecond();
        telemetry.addLine(backRightDrive.motor.getMotorType().getAchieveableMaxTicksPerSecond() + " | " + hardwareGetter.getMotorRpm("brd").getAchievableMaxTicksPerSecond()); // FIXME: delete
        telemetry.update();

        frontLeftDrive.setInverted(true);
        frontRightDrive.setInverted(false);
        backLeftDrive.setInverted(true);
        backRightDrive.setInverted(false);

        frontLeftDrive.setZeroPowerBehavior(MotorExVelo.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(MotorExVelo.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(MotorExVelo.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(MotorExVelo.ZeroPowerBehavior.BRAKE);

        frontLeftDrive.stopAndResetEncoder();
        frontRightDrive.stopAndResetEncoder();
        backLeftDrive.stopAndResetEncoder();
        backRightDrive.stopAndResetEncoder();

        if (useVelocityControl) {
            telemetry.addLine("Using velocity control");
            frontLeftDrive.setRunMode(MotorExVelo.RunMode.VelocityControl);
            frontRightDrive.setRunMode(MotorExVelo.RunMode.VelocityControl);
            backLeftDrive.setRunMode(MotorExVelo.RunMode.VelocityControl);
            backRightDrive.setRunMode(MotorExVelo.RunMode.VelocityControl);
        } else {
            telemetry.addLine("Using raw power");
            frontLeftDrive.setRunMode(MotorExVelo.RunMode.RawPower);
            frontRightDrive.setRunMode(MotorExVelo.RunMode.RawPower);
            backLeftDrive.setRunMode(MotorExVelo.RunMode.RawPower);
            backRightDrive.setRunMode(MotorExVelo.RunMode.RawPower);
        }

        if (enableHardwareEncoders || useMotorExVelo && useVelocityControl) {
            telemetry.addLine("Using hardware encoders");
            frontLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else
            telemetry.addLine("Not using hardware encoders");

        telemetry.update();

        frontLeftDrive.setCachingTolerance(0.00005);
        frontRightDrive.setCachingTolerance(0.00005);
        backLeftDrive.setCachingTolerance(0.00005);
        backRightDrive.setCachingTolerance(0.00005);

        mecanumDrive = new MecanumDrivePanels(false, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);
    }

    @Override
    public void loop() {
        // TODO: Motors may be continuous, maybe just set and leave it
        mecanumDrive.driveRobotCentric(AutoMoveConfigPanels.movePower, AutoMoveConfigPanels.movePower, 0);
    }

    @Override
    public void stop() {
        mecanumDrive.stop();
    }
}
