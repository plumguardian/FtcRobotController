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
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MotorExVelo;

@TeleOp(name = "Mecanum Drive (Manual Velocity)", group = TeamCode.GROUP_NAME)
public class MecanumDriveTeleOpManualVelocity extends OpMode {
    private MotorEx frontLeftDrive;
    private MotorEx frontRightDrive;
    private MotorEx backLeftDrive;
    private MotorEx backRightDrive;
    private IMU imu;
    private TelemetryManager.TelemetryWrapper panelsTelemetry;
    private GamepadManager panelsGamepad;

    @Override
    public void init() {
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        if (useMotorExVelo) {
            frontLeftDrive = new MotorExVelo(hardwareMap, "frd", hardwareGetter.getMotorRpm("frd"));
            frontRightDrive = new MotorExVelo(hardwareMap, "fld", hardwareGetter.getMotorRpm("fld"));
            backLeftDrive = new MotorExVelo(hardwareMap, "bld", hardwareGetter.getMotorRpm("bld"));
            backRightDrive = new MotorExVelo(hardwareMap, "brd", hardwareGetter.getMotorRpm("brd"));
        } else {
            frontLeftDrive = new MotorEx(hardwareMap, "frd", hardwareGetter.getMotorRpm("frd"));
            frontRightDrive = new MotorEx(hardwareMap, "fld", hardwareGetter.getMotorRpm("fld"));
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

        imu = hardwareGetter.getIMU();

        panelsTelemetry = PanelsTelemetry.INSTANCE.getFtcTelemetry();
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
        if (gamepad.left_bumper)
            drive(-gamepad.left_stick_y, gamepad.left_stick_x, gamepad.right_stick_x);
        else
            driveFieldRelative(-gamepad.left_stick_y, gamepad.left_stick_x, gamepad.right_stick_x);
    }

    private void driveFieldRelative(final double forward, final double right, final double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        final double r = Math.hypot(right, forward);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        // Third, convert back to cartesian
        final double newForward = r * Math.sin(theta);
        final double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    // Code by FTC16072
    public void drive(final double forward, final double right, final double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward, strafe right, and rotate
        final double frontLeftPower = forward + right + rotate;
        final double frontRightPower = forward - right - rotate;
        final double backRightPower = forward + right - rotate;
        final double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        final double maxSpeed = 1.0;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        final double fld = maxSpeed * (frontLeftPower / maxPower);
        final double frd = maxSpeed * (frontRightPower / maxPower);
        final double bld = maxSpeed * (backLeftPower / maxPower);
        final double brd = maxSpeed * (backRightPower / maxPower);
        panelsTelemetry.addData("frontLeft", fld);
        panelsTelemetry.addData("frontRight", frd);
        panelsTelemetry.addData("backLeft", bld);
        panelsTelemetry.addData("backRight", brd);
        final YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
        panelsTelemetry.addData("yaw", angles.getYaw(AngleUnit.DEGREES));
        panelsTelemetry.addData("pitch", angles.getPitch(AngleUnit.DEGREES));
        panelsTelemetry.addData("roll", angles.getRoll(AngleUnit.DEGREES));
        panelsTelemetry.addData("encoderFrontLeft", frontLeftDrive.getCurrentPosition());
        panelsTelemetry.addData("encoderFrontRight", frontRightDrive.getCurrentPosition());
        panelsTelemetry.addData("encoderBackLeft", backLeftDrive.getCurrentPosition());
        panelsTelemetry.addData("encoderBackRight", backRightDrive.getCurrentPosition());
        panelsTelemetry.update();

        if (MOTORS_ACTIVE) {
            frontLeftDrive.set(fld);
            frontRightDrive.set(frd);
            backLeftDrive.set(bld);
            backRightDrive.set(brd);
        } else {
            frontLeftDrive.stopMotor();
            frontRightDrive.stopMotor();
            backLeftDrive.stopMotor();
            backRightDrive.stopMotor();
        }
    }
}
