import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Mecanum Drive", group = TeamCode.GROUP_NAME)
public class MecanumDriveTeleOp extends OpMode {
    MecanumDrive mecanumDrive;
    IMU imu;

    @Override
    public void init() {
        // TODO: check GoBILDA RPM
        Motor frontLeftDrive = new Motor(hardwareMap, "fld", Motor.GoBILDA.RPM_312);
        Motor frontRightDrive = new Motor(hardwareMap, "frd", Motor.GoBILDA.RPM_312);
        Motor backLeftDrive = new Motor(hardwareMap, "bld", Motor.GoBILDA.RPM_312);
        Motor backRightDrive = new Motor(hardwareMap, "brd", Motor.GoBILDA.RPM_312);

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        backLeftDrive.setInverted(true);
        frontLeftDrive.setInverted(true);

        // This uses RUN_USING_ENCODER to be more accurate.   If you don't have the encoder
        // wires, you should remove these
        frontLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        mecanumDrive = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        imu = hardwareMap.get(IMU.class, "imu");
        // This needs to be changed to match the orientation on your robot
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }

    @Override
    public void loop() {
        // TODO: Does this need telemetry.update()?
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (gamepad1.a) {
            telemetry.addLine("IMU reset");
            imu.resetYaw();
        }
        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)
        if (gamepad1.left_bumper)
            mecanumDrive.driveRobotCentric(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_bumper);
        else
            mecanumDrive.driveFieldCentric(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES), gamepad1.right_bumper);
    }
}
