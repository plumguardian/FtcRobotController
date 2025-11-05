package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.config.TeamCode;

@TeleOp(name = "Launch Mechanism", group = TeamCode.GROUP_NAME)
public class LaunchMechanism extends OpMode {
    private DcMotor launchLeft;
    private DcMotor launchRight;

    @Override
    public void init() {
        launchLeft = hardwareMap.get(DcMotor.class, "ll");
        launchRight = hardwareMap.get(DcMotor.class, "lr");

        launchRight.setDirection(DcMotor.Direction.REVERSE);

        launchLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        float power = gamepad1.right_trigger;
        launchLeft.setPower(power);
        launchRight.setPower(power);
        telemetry.addData("power", power);
        telemetry.update();
    }
}
