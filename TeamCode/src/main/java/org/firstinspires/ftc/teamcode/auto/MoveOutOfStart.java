package org.firstinspires.ftc.teamcode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.teamcode.config.TeamCode;

// TODO: only works with blue alliance next to pillar
@SuppressWarnings("BusyWait")
@Autonomous(name = "Move Out Of Start", group = TeamCode.GROUP_NAME)
public class MoveOutOfStart extends LinearOpMode {
    @Configurable
    private static class AutoMoveConfigPanels {
        // Don't manually change values. Control it with panels.
        public static int moveDist = 60_000;
        public static double movePower = 0.2;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final Motor frontLeftDrive = new Motor(hardwareMap, "fld", hardwareGetter.getMotorRpm("fld"));
        final Motor frontRightDrive = new Motor(hardwareMap, "frd", hardwareGetter.getMotorRpm("frd"));
        final Motor backLeftDrive = new Motor(hardwareMap, "bld", hardwareGetter.getMotorRpm("bld"));
        final Motor backRightDrive = new Motor(hardwareMap, "brd", hardwareGetter.getMotorRpm("brd"));
        // TODO: compare motor.getMotorType().getAchieveableMaxTicksPerSecond(); and gobildaType.getAchievableMaxTicksPerSecond();
        telemetry.addLine(backRightDrive.motor.getMotorType().getAchieveableMaxTicksPerSecond() + " | " + hardwareGetter.getMotorRpm("brd").getAchievableMaxTicksPerSecond()); // FIXME: delete
        telemetry.update();

        backLeftDrive.setInverted(true);
        frontLeftDrive.setInverted(false);
        backRightDrive.setInverted(false);
        frontRightDrive.setInverted(false);

        backLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        frontLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        backLeftDrive.stopAndResetEncoder();
        frontLeftDrive.stopAndResetEncoder();
        backRightDrive.stopAndResetEncoder();
        frontRightDrive.stopAndResetEncoder();

        /*
        if (useSolverslibEncoders) {
            telemetry.addLine("Using solvers lib encoders");
            backLeftDrive.setRunMode(Motor.RunMode.VelocityControl);
            frontLeftDrive.setRunMode(Motor.RunMode.VelocityControl);
            backRightDrive.setRunMode(Motor.RunMode.VelocityControl);
            frontRightDrive.setRunMode(Motor.RunMode.VelocityControl);
            backLeftDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frontLeftDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backRightDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frontRightDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } else {
            telemetry.addLine("Using built in encoders");
            backLeftDrive.setRunMode(Motor.RunMode.RawPower);
            frontLeftDrive.setRunMode(Motor.RunMode.RawPower);
            backRightDrive.setRunMode(Motor.RunMode.RawPower);
            frontRightDrive.setRunMode(Motor.RunMode.RawPower);
            backLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        telemetry.update();
        */
        // FIXME: the following code is because neither of the above works
        backLeftDrive.setRunMode(Motor.RunMode.RawPower);
        frontLeftDrive.setRunMode(Motor.RunMode.RawPower);
        backRightDrive.setRunMode(Motor.RunMode.RawPower);
        frontRightDrive.setRunMode(Motor.RunMode.RawPower);
        backLeftDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeftDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        final MecanumDrive mecanumDrive = new MecanumDrive(false, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        waitForStart();
        if (isStopRequested()) return;

        for (int move = 0; move < AutoMoveConfigPanels.moveDist; move++) {
            if (isStopRequested()) return;
            if (!opModeIsActive()) return;
            // TODO: Motors may be continuous, maybe just set and leave it
            mecanumDrive.driveRobotCentric(AutoMoveConfigPanels.movePower, AutoMoveConfigPanels.movePower, 0);
        }

        mecanumDrive.stop();
        while (opModeIsActive())
            Thread.sleep(100);
    }
}
