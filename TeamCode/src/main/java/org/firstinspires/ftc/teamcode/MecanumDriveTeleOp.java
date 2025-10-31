package org.firstinspires.ftc.teamcode;

import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Mecanum Drive", group = TeamCode.GROUP_NAME)
public class MecanumDriveTeleOp extends OpMode {
    private MecanumDrive mecanumDrive;
    private IMU imu;

    @Override
    public void init() {
        TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        Motor frontLeftDrive = new Motor(hardwareMap, "fld", hardwareGetter.getMotorRpm("fld"));
        Motor frontRightDrive = new Motor(hardwareMap, "frd", hardwareGetter.getMotorRpm("frd"));
        Motor backLeftDrive = new Motor(hardwareMap, "bld", hardwareGetter.getMotorRpm("bld"));
        Motor backRightDrive = new Motor(hardwareMap, "brd", hardwareGetter.getMotorRpm("brd"));

        backLeftDrive.setInverted(true);
        frontLeftDrive.setInverted(true);

        frontLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        mecanumDrive = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        imu = hardwareGetter.getIMU();
    }

    @Override
    public void loop() {
        // TODO: Does this need telemetry.update()?
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

        if (gamepad1.a) {
            telemetry.addLine("IMU reset");
            imu.resetYaw();
        }

        if (gamepad1.left_bumper)
            mecanumDrive.driveRobotCentric(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_bumper);
        else
            mecanumDrive.driveFieldCentric(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES), gamepad1.right_bumper);
    }
}
