package org.firstinspires.ftc.teamcode.teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.config.TeamCode;

@TeleOp(name = "Launch Mechanism", group = TeamCode.GROUP_NAME)
public class LaunchMechanism extends OpMode {
    private DcMotor launchLeft;
    private DcMotor launchRight;

    @Configurable
    private static class LaunchConfigPanels {
        // Don't manually change values. Control it with panels.
        public static boolean ZeroPowerBrake = false;
        public static DcMotor.ZeroPowerBehavior getZeroPowerBehavior() {
            return ZeroPowerBrake ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT;
        }
    }

    @Override
    public void init() {
        launchLeft = hardwareMap.get(DcMotor.class, "ll");
        launchRight = hardwareMap.get(DcMotor.class, "lr");

        launchLeft.setDirection(DcMotor.Direction.FORWARD);
        launchRight.setDirection(DcMotor.Direction.REVERSE);

        final DcMotor.ZeroPowerBehavior zpb = LaunchConfigPanels.getZeroPowerBehavior();
        launchLeft.setZeroPowerBehavior(zpb);
        launchLeft.setZeroPowerBehavior(zpb);

        launchLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launchRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launchLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        final float power = gamepad1.right_trigger;
        launchLeft.setPower(power);
        launchRight.setPower(power);
        telemetry.addData("power", power);
        telemetry.update();
    }
}
