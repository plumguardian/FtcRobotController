package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Launch Mechanism", group = TeamCode.GROUP_NAME)
public class LaunchMechanism extends OpMode {
    DcMotor launchLeft;
    DcMotor launchRight;

    @Override
    public void init() {
        launchLeft = hardwareMap.get(DcMotor.class, "ll");
        launchRight = hardwareMap.get(DcMotor.class, "lr");

        launchRight.setDirection(DcMotor.Direction.REVERSE);

        // FIXME: encoder
        launchLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launchRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
