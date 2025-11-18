package org.firstinspires.ftc.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.config.TeamCode;

@TeleOp(name = "Conveyor Test", group = TeamCode.GROUP_NAME)
public class ConveyorTest extends OpMode {
    private Servo ConveyorServo1;
    private Servo ConveyorServo2;

    @Override
    public void init() {
        ConveyorServo1 = hardwareMap.get(Servo.class, "c1");
        ConveyorServo2 = hardwareMap.get(Servo.class, "c2");

        ConveyorServo1.setDirection(Servo.Direction.FORWARD);
        ConveyorServo2.setDirection(Servo.Direction.FORWARD);
    }

    @Override
    public void loop() {
        // TODO: pick better button
        if (gamepad1.b) {
            // Motors are continuous
            // setPosition acts as setPower
            ConveyorServo1.setPosition(1);
            ConveyorServo2.setPosition(1);
        }
        // FIXME: will it stop even without setting power to 0?
        // I assume that depends on the zero power behavior
    }
}
