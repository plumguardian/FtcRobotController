package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.MOTORS_ACTIVE;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.USE_PANELS_GAMEPAD;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.enableHardwareEncoders;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useMotorExVelo;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useVelocityControl;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.config.DriveConfig;
import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MecanumDrivePanels;
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MotorExVelo;

@TeleOp(name = "Mecanum Drive", group = TeamCode.GROUP_NAME)
public class MecanumDriveTeleOp extends OpMode {
    private MecanumDrivePanels mecanumDrive;
    private IMU imu;
    private TelemetryManager.TelemetryWrapper panelsTelemetry;
    private GamepadManager panelsGamepad;

    @Override
    public void init() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final MotorEx frontLeftDrive;
        final MotorEx frontRightDrive;
        final MotorEx backLeftDrive;
        final MotorEx backRightDrive;
        if (useMotorExVelo) {
            frontLeftDrive = new MotorExVelo(hardwareMap, "fld", hardwareGetter.getMotorRpm("fld"));
            frontRightDrive = new MotorExVelo(hardwareMap, "frd", hardwareGetter.getMotorRpm("frd"));
            backLeftDrive = new MotorExVelo(hardwareMap, "bld", hardwareGetter.getMotorRpm("bld"));
            backRightDrive = new MotorExVelo(hardwareMap, "brd", hardwareGetter.getMotorRpm("brd"));
        } else {
            frontLeftDrive = new MotorEx(hardwareMap, "fld", hardwareGetter.getMotorRpm("fld"));
            frontRightDrive = new MotorEx(hardwareMap, "frd", hardwareGetter.getMotorRpm("frd"));
            backLeftDrive = new MotorEx(hardwareMap, "bld", hardwareGetter.getMotorRpm("bld"));
            backRightDrive = new MotorEx(hardwareMap, "brd", hardwareGetter.getMotorRpm("brd"));
        }
        // TODO: compare motor.getMotorType().getAchieveableMaxTicksPerSecond(); and gobildaType.getAchievableMaxTicksPerSecond();
        telemetry.addLine(backRightDrive.motor.getMotorType().getAchieveableMaxTicksPerSecond() + " | " + hardwareGetter.getMotorRpm("brd").getAchievableMaxTicksPerSecond()); // FIXME: delete

        frontLeftDrive.setInverted(true);
        frontRightDrive.setInverted(false);
        backLeftDrive.setInverted(true);
        backRightDrive.setInverted(false);

        frontLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        frontLeftDrive.stopAndResetEncoder();
        frontRightDrive.stopAndResetEncoder();
        backLeftDrive.stopAndResetEncoder();
        backRightDrive.stopAndResetEncoder();

        if (useVelocityControl) {
            telemetry.addLine("Using velocity control");
            frontLeftDrive.setRunMode(Motor.RunMode.VelocityControl);
            frontRightDrive.setRunMode(Motor.RunMode.VelocityControl);
            backLeftDrive.setRunMode(Motor.RunMode.VelocityControl);
            backRightDrive.setRunMode(Motor.RunMode.VelocityControl);
        } else {
            telemetry.addLine("Using raw power");
            frontLeftDrive.setRunMode(Motor.RunMode.RawPower);
            frontRightDrive.setRunMode(Motor.RunMode.RawPower);
            backLeftDrive.setRunMode(Motor.RunMode.RawPower);
            backRightDrive.setRunMode(Motor.RunMode.RawPower);
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

        /*
        // TODO: Find
        frontLeftDrive.setVeloCoefficients();
        frontRightDrive.setVeloCoefficients();
        backLeftDrive.setVeloCoefficients();
        backRightDrive.setVeloCoefficients();

        frontLeftDrive.setFeedforwardCoefficients();
        frontRightDrive.setFeedforwardCoefficients();
        backLeftDrive.setFeedforwardCoefficients();
        backRightDrive.setFeedforwardCoefficients();
        */

        panelsTelemetry = PanelsTelemetry.INSTANCE.getFtcTelemetry();

        mecanumDrive = new MecanumDrivePanels(false, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive, panelsTelemetry);

        imu = hardwareGetter.getIMU();

        panelsGamepad = PanelsGamepad.INSTANCE.getFirstManager();
    }

    @Override
    public void start() { DriveConfig.updateYawToggle(); }

    @Override
    public void loop() {
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

        final YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
        final double yaw = angles.getYaw(AngleUnit.DEGREES);
        panelsTelemetry.addData("yaw", yaw);
        panelsTelemetry.addData("pitch", angles.getPitch(AngleUnit.DEGREES));
        panelsTelemetry.addData("roll", angles.getRoll(AngleUnit.DEGREES));

        if (MOTORS_ACTIVE) {
            // MecanumDrivePanels already calls panelsTelemetry.update();
            if (gamepad.left_bumper)
                mecanumDrive.driveRobotCentric(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x, gamepad.right_bumper);
            else
                mecanumDrive.driveFieldCentric(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x, yaw, gamepad.right_bumper);
        } else {
            mecanumDrive.stop();
            panelsTelemetry.update();
        }
    }
}
