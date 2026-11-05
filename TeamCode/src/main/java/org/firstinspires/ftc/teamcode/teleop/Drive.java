package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.MOTORS_ACTIVE;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.enableHardwareEncoders;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useMotorExVelo;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.EncoderConfigPanels.useVelocityControl;

//#if !match
import static org.firstinspires.ftc.teamcode.config.TeamCode.MessageConfig.printMessage;
import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.USE_PANELS_GAMEPAD;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.config.mecanumdrive.MecanumDrivePanels;
//#endif

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoExGroup;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.config.DriveConfig;
import org.firstinspires.ftc.teamcode.config.TeamCode;

import lombok.val;

@TeleOp(name = "Drive", group = "TeleOp")
public class Drive extends OpMode {
    private MecanumDrive mecanumDrive;
    private IMU imu;
    private DcMotorEx launch;
    private double launchTicks;
    private ServoExGroup gate;
    private GamepadEx gamepad;
//#if !match
    private TelemetryManager.TelemetryWrapper panelsTelemetry;
//#endif

    @Override
    public void init() {
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class))
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        val hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        val motors = hardwareGetter.getMotors();
        val frontLeftDrive = motors.frontLeft();
        val frontRightDrive = motors.frontRight();
        val backLeftDrive = motors.backLeft();
        val backRightDrive = motors.backRight();
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
//#endif

        mecanumDrive =
//#if !match
                new MecanumDrivePanels(false, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive, PanelsTelemetry.INSTANCE.getFtcTelemetry());
//#else
//$$                 new MecanumDrive(false, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);
//#endif

        imu = hardwareGetter.getIMU();

        launch = hardwareMap.get(DcMotorEx.class, "launch");
        launch.setDirection(DcMotorSimple.Direction.REVERSE);
        launch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchTicks = launch.getMotorType().getAchieveableMaxTicksPerSecond();
//#if !match
        telemetry.addData("RPM", launch.getMotorType().getMaxRPM());
        telemetry.addData("ticks/second", launchTicks);
        telemetry.addData("SolversLib ticks/second", hardwareGetter.getMotorRpm("launch").getAchievableMaxTicksPerSecond());
//#endif

        gate = new ServoExGroup(
                new ServoEx(hardwareMap, "gate1", 300.0)
                        .setInverted(true)
                        .setPwm(new PwmControl.PwmRange(500, 2500)),
                new ServoEx(hardwareMap, "gate2", 300.0)
                        .setInverted(false)
                        .setPwm(new PwmControl.PwmRange(500, 2500))
        );

//#if !match
        if (USE_PANELS_GAMEPAD) {
            gamepad = new GamepadEx(PanelsGamepad.INSTANCE.getFirstManager().getAsFTCGamepad());
            telemetry.addLine("Using online gamepad");
        } else
//#endif
            gamepad = new GamepadEx(gamepad1);

        telemetry.update();
    }

    @Override
    public void start() {
        DriveConfig.updateYawToggle();
    }

    @Override
    public void loop() {
//#if !match
        if (USE_PANELS_GAMEPAD) {
            gamepad.gamepad = PanelsGamepad.INSTANCE.getFirstManager().getAsFTCGamepad();
            telemetry.addLine("Using online gamepad");
        }
//#endif

        gamepad.readButtons();

        telemetry.addLine("Press SHARE to reset Yaw");
        telemetry.addLine("Hold left bumper to drive robot centric");
        telemetry.addLine("Hold right bumper to square the speed");
        telemetry.addLine("Left joystick moves the robot");
        telemetry.addLine("Right joystick turns the robot");
        telemetry.addLine("Dpad up/down changes launch speed by .1");
        telemetry.addLine("Dpad left/right changes launch speed by .01");
        telemetry.addLine("Press CROSS to shoot");
        telemetry.addLine("Press SQUARE to suck");
        telemetry.addLine("Press TRIANGLE to push");
        telemetry.addLine("Press CIRCLE to load");

//#if !match
        if (DriveConfig.updateAndCheckYawToggle()) {
            telemetry.addLine("IMU reset (panels)");
            panelsTelemetry.addLine("IMU reset (panels)");
            imu.resetYaw();
        }

        printMessage(telemetry);
//#endif

        if (gamepad.getButton(GamepadKeys.Button.SHARE)) {
            telemetry.addLine("IMU reset");
            imu.resetYaw();
        }

        final YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
        final double yaw = angles.getYaw(AngleUnit.DEGREES);

//#if !match
        panelsTelemetry.addData("yaw", yaw);
        panelsTelemetry.addData("pitch", angles.getPitch(AngleUnit.DEGREES));
        panelsTelemetry.addData("roll", angles.getRoll(AngleUnit.DEGREES));
//#endif

        if (MOTORS_ACTIVE) {
            // MecanumDrivePanels already calls panelsTelemetry.update();
            if (gamepad.getButton(GamepadKeys.Button.LEFT_BUMPER))
                mecanumDrive.driveRobotCentric(gamepad.getLeftX(), gamepad.getLeftY(), gamepad.getRightX(), gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER));
            else
                mecanumDrive.driveFieldCentric(gamepad.getLeftX(), gamepad.getLeftY(), gamepad.getRightX(), yaw, gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER));
        } else {
            mecanumDrive.stop();
//#if !match
            panelsTelemetry.update();
//#endif
        }

        // TODO: does this break panels?
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT))
            DriveConfig.LaunchPanels.SPEED_FIRE += 0.01;
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_LEFT))
            DriveConfig.LaunchPanels.SPEED_FIRE -= 0.01;
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_UP))
            DriveConfig.LaunchPanels.SPEED_FIRE += 0.1;
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN))
            DriveConfig.LaunchPanels.SPEED_FIRE -= 0.1;

        telemetry.addData("Shooting speed", DriveConfig.LaunchPanels.SPEED_FIRE);

        double speed;
        if (gamepad.getButton(GamepadKeys.Button.CROSS))
            speed = DriveConfig.LaunchPanels.SPEED_FIRE;
        else if (gamepad.getButton(GamepadKeys.Button.SQUARE))
            speed = DriveConfig.LaunchPanels.SPEED_SUCK;
        else
            speed = 0.0;
        launch.setVelocity(speed * launchTicks);
//#if !match
        panelsTelemetry.addData("Launch pos", launch.getCurrentPosition());
        panelsTelemetry.addData("Launch target pos", launch.getTargetPosition());
        panelsTelemetry.addData("Launch velo", launch.getVelocity());
//#endif

        double angle;
        if (gamepad.getButton(GamepadKeys.Button.CIRCLE))
            angle = DriveConfig.LaunchPanels.ANGLE_LOAD;
        else if (gamepad.getButton(GamepadKeys.Button.TRIANGLE))
            angle = DriveConfig.LaunchPanels.ANGLE_FIRE;
        else
            angle = DriveConfig.LaunchPanels.ANGLE_NEUTRAL;
        gate.set(angle);
    }

    @Override
    public void stop() {
        launch.setPower(0D);
    }
}
