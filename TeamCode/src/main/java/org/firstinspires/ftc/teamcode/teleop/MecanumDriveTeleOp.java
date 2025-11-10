package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.*;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.SolverslibConfigPanels.*;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.config.DriveConfig;
import org.firstinspires.ftc.teamcode.config.TeamCode;

@TeleOp(name = "Mecanum Drive", group = TeamCode.GROUP_NAME)
public class MecanumDriveTeleOp extends OpMode {
    private MecanumDrive mecanumDrive;
    private IMU imu;
    private TelemetryManager.TelemetryWrapper panelsTelemetry;
    private static GamepadManager panelsGamepad;

    @Override
    public void init() {
        TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        Motor frontLeftDrive = new Motor(hardwareMap, "fld", hardwareGetter.getMotorRpm("fld"));
        Motor frontRightDrive = new Motor(hardwareMap, "frd", hardwareGetter.getMotorRpm("frd"));
        Motor backLeftDrive = new Motor(hardwareMap, "bld", hardwareGetter.getMotorRpm("bld"));
        Motor backRightDrive = new Motor(hardwareMap, "brd", hardwareGetter.getMotorRpm("brd"));

        backLeftDrive.setInverted(true);
        frontLeftDrive.setInverted(true);
        backRightDrive.setInverted(false);
        frontRightDrive.setInverted(true);

        backLeftDrive.stopAndResetEncoder();
        frontLeftDrive.stopAndResetEncoder();
        backRightDrive.stopAndResetEncoder();
        frontRightDrive.stopAndResetEncoder();

        if (useSolverslibEncoders) {
            backLeftDrive.setRunMode(Motor.RunMode.VelocityControl);
            frontLeftDrive.setRunMode(Motor.RunMode.VelocityControl);
            backRightDrive.setRunMode(Motor.RunMode.VelocityControl);
            frontRightDrive.setRunMode(Motor.RunMode.VelocityControl);
        } else {
            backLeftDrive.setRunMode(Motor.RunMode.RawPower);
            frontLeftDrive.setRunMode(Motor.RunMode.RawPower);
            backRightDrive.setRunMode(Motor.RunMode.RawPower);
            frontRightDrive.setRunMode(Motor.RunMode.RawPower);
            backLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        backLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        frontLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        mecanumDrive = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        imu = hardwareGetter.getIMU();

        panelsTelemetry = PanelsTelemetry.INSTANCE.getFtcTelemetry();
        panelsGamepad = PanelsGamepad.INSTANCE.getFirstManager();
    }

    @Override
    public void start() { DriveConfig.updateYawToggle(); }

    @Override
    public void loop() {
        // TODO: Does this need telemetry.update()?
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

        final Gamepad gamepad = USE_PANELS_GAMEPAD ? panelsGamepad.getAsFTCGamepad() : gamepad1;

        if (DriveConfig.updateAndCheckYawToggle()) {
            imu.resetYaw();
            telemetry.addLine("IMU reset (panels)");
            panelsTelemetry.addLine("IMU reset (panels)");
        }
        if (gamepad.a) {
            telemetry.addLine("IMU reset");
            imu.resetYaw();
        }

        YawPitchRollAngles yawPitchRollAngles = imu.getRobotYawPitchRollAngles();

        panelsTelemetry.addData("yaw", yawPitchRollAngles.getYaw(AngleUnit.DEGREES));
        panelsTelemetry.update();

        if (MOTORS_ACTIVE) {
            if (gamepad.left_bumper)
                mecanumDrive.driveRobotCentric(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x, gamepad.right_bumper);
            else
                mecanumDrive.driveFieldCentric(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x, yawPitchRollAngles.getYaw(AngleUnit.DEGREES), gamepad.right_bumper);
        } else {
            mecanumDrive.stop();
        }
    }
}
