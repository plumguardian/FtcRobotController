package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.*;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.config.DriveConfig;
import org.firstinspires.ftc.teamcode.config.TeamCode;

@TeleOp(name = "Mecanum Drive (Manual)", group = TeamCode.GROUP_NAME)
public class MecanumDriveTeleOpManual extends OpMode {
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;
    private IMU imu;
    private TelemetryManager.TelemetryWrapper panelsTelemetry;
    private static GamepadManager panelsGamepad;

    @Override
    public void init() {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "fld");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frd");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bld");
        backRightDrive = hardwareMap.get(DcMotor.class, "brd");

        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);

        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // FIXME: encoders
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        imu = new TeamCode.HardwareGetter(hardwareMap, telemetry).getIMU();

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

        /* Reports orientation
        telemetry.addLine("yaw: " + imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
        telemetry.addLine("roll: " + imu.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES));
        telemetry.addLine("pitch: " + imu.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES));
         */

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
        panelsTelemetry.addData("frontLeft", fld);
        panelsTelemetry.addData("frontRight", frd);
        panelsTelemetry.addData("backLeft", bld);
        panelsTelemetry.addData("backRight", brd);
        panelsTelemetry.addData("yaw", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
        panelsTelemetry.addData("encoderFrontLeft", frontLeftDrive.getCurrentPosition());
        panelsTelemetry.addData("encoderFrontRight", frontRightDrive.getCurrentPosition());
        panelsTelemetry.addData("encoderBackLeft", backLeftDrive.getCurrentPosition());
        panelsTelemetry.addData("encoderBackRight", backRightDrive.getCurrentPosition());
        panelsTelemetry.update();

        // We multiply by maxSpeed so that it can be set lower for outreaches
        if (MOTORS_ACTIVE) {
            frontLeftDrive.setPower(fld);
            frontRightDrive.setPower(frd);
            backLeftDrive.setPower(bld);
            backRightDrive.setPower(brd);
        } else {
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }
    }
}
