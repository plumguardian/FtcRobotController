package org.firstinspires.ftc.teamcode.teleop.test;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.MOTORS_ACTIVE;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.enableHardwareEncoders;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useMotorExVelo;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useVelocityControl;
import static org.firstinspires.ftc.teamcode.config.TeamCode.MessageConfig.printMessage;

//#if !match
import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.USE_PANELS_GAMEPAD;
import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
//#endif

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

@TeleOp(name = "Mecanum Drive", group = "Testing")
public class MecanumDriveTeleOp extends OpMode {
    private MecanumDrivePanels mecanumDrive;
    private IMU imu;
//#if !match
    private TelemetryManager.TelemetryWrapper panelsTelemetry;
    private GamepadManager panelsGamepad;
//#endif

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
        // Only needed for !useMotorExVelo && useVelocityControl
        frontLeftDrive.setVeloCoefficients();
        frontRightDrive.setVeloCoefficients();
        backLeftDrive.setVeloCoefficients();
        backRightDrive.setVeloCoefficients();

        frontLeftDrive.setFeedforwardCoefficients();
        frontRightDrive.setFeedforwardCoefficients();
        backLeftDrive.setFeedforwardCoefficients();
        backRightDrive.setFeedforwardCoefficients();
        */

//#if !match
        panelsTelemetry = PanelsTelemetry.INSTANCE.getFtcTelemetry();
        panelsGamepad = PanelsGamepad.INSTANCE.getFirstManager();
//#endif

        mecanumDrive = new MecanumDrivePanels(
                false,
                frontLeftDrive,
                frontRightDrive,
                backLeftDrive,
                backRightDrive
//#if !match
                , panelsTelemetry
//#endif
        );

        imu = hardwareGetter.getIMU();
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

        final YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
        final double yaw = angles.getYaw(AngleUnit.DEGREES);

//#if !match
        panelsTelemetry.addData("yaw", yaw);
        panelsTelemetry.addData("pitch", angles.getPitch(AngleUnit.DEGREES));
        panelsTelemetry.addData("roll", angles.getRoll(AngleUnit.DEGREES));
//#endif

        if (MOTORS_ACTIVE) {
            // MecanumDrivePanels already calls panelsTelemetry.update();
            if (gamepad.left_bumper)
                mecanumDrive.driveRobotCentric(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x, gamepad.right_bumper);
            else
                mecanumDrive.driveFieldCentric(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x, yaw, gamepad.right_bumper);
        } else {
            mecanumDrive.stop();
//#if !match
            panelsTelemetry.update();
//#endif
        }
    }
}
