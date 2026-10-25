package org.firstinspires.ftc.teamcode.teleop.test;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.MOTORS_ACTIVE;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.enableHardwareEncoders;
import static org.firstinspires.ftc.teamcode.config.TeamCode.MessageConfig.printMessage;

import androidx.annotation.NonNull;

//#if !match
import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.USE_PANELS_GAMEPAD;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
//#endif

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.config.DriveConfig;
import org.firstinspires.ftc.teamcode.config.TeamCode;

@TeleOp(name = "Mecanum Drive (Manual)", group = "Testing")
public class MecanumDriveTeleOpManual extends OpMode {
    private DcMotorEx frontLeftDrive;
    private double frontLeftDriveTicksPerSecond;
    private DcMotorEx frontRightDrive;
    private double frontRightDriveTicksPerSecond;
    private DcMotorEx backLeftDrive;
    private double backLeftDriveTicksPerSecond;
    private DcMotorEx backRightDrive;
    private double backRightDriveTicksPerSecond;
    private IMU imu;
//#if !match
    private TelemetryManager.TelemetryWrapper panelsTelemetry;
    private GamepadManager panelsGamepad;
//#endif
    private boolean useVelocity;

    @NonNull
    private DcMotorEx motor(String name) {
        final DcMotorEx m = hardwareMap.get(DcMotorEx.class, name);
        m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m.setDirection(DriveConfig.DriveDirPanels.getDir(name));
        m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        return m;
    }

    @Override
    public void init() {
        frontLeftDrive = motor("fld");
        frontRightDrive = motor("frd");
        backLeftDrive = motor("bld");
        backRightDrive = motor("brd");

        if (enableHardwareEncoders) {
            telemetry.addLine("Using hardware encoders");
            frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontLeftDriveTicksPerSecond = frontLeftDrive.getMotorType().getAchieveableMaxTicksPerSecond();
            frontRightDriveTicksPerSecond = frontRightDrive.getMotorType().getAchieveableMaxTicksPerSecond();
            backLeftDriveTicksPerSecond = backLeftDrive.getMotorType().getAchieveableMaxTicksPerSecond();
            backRightDriveTicksPerSecond = backRightDrive.getMotorType().getAchieveableMaxTicksPerSecond();
            useVelocity = true;
        } else {
            telemetry.addLine("Not using hardware encoders");
            frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            useVelocity = false;
        }

        telemetry.update();

        imu = new TeamCode.HardwareGetter(hardwareMap, telemetry).getIMU();

//#if !match
        panelsTelemetry = PanelsTelemetry.INSTANCE.getFtcTelemetry();
        panelsGamepad = PanelsGamepad.INSTANCE.getFirstManager();
//#endif
    }

    @Override
    public void start() { DriveConfig.updateYawToggle(); }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

        final Gamepad gamepad =
//#if !match
                USE_PANELS_GAMEPAD ? panelsGamepad.getAsFTCGamepad() :
//#endif
                gamepad1;

        if (DriveConfig.updateAndCheckYawToggle()) {
            imu.resetYaw();
            telemetry.addLine("IMU reset (panels)");
//#if !match
            panelsTelemetry.addLine("IMU reset (panels)");
//#endif
        }
        if (gamepad.a) {
            telemetry.addLine("IMU reset");
            imu.resetYaw();
        }

        printMessage(telemetry);

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

        final double fld = maxSpeed * (frontLeftPower / maxPower);
        final double frd = maxSpeed * (frontRightPower / maxPower);
        final double bld = maxSpeed * (backLeftPower / maxPower);
        final double brd = maxSpeed * (backRightPower / maxPower);
//#if !match
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
//#endif

        if (MOTORS_ACTIVE) {
            if (useVelocity) {
                frontLeftDrive.setVelocity(fld * frontLeftDriveTicksPerSecond);
                frontRightDrive.setVelocity(frd * frontRightDriveTicksPerSecond);
                backLeftDrive.setVelocity(bld * backLeftDriveTicksPerSecond);
                backRightDrive.setVelocity(brd * backRightDriveTicksPerSecond);
            } else {
                frontLeftDrive.setPower(fld);
                frontRightDrive.setPower(frd);
                backLeftDrive.setPower(bld);
                backRightDrive.setPower(brd);
            }
        } else {
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }
    }
}
