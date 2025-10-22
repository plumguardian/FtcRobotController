import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Conveyor Test", group = TeamCode.GROUP_NAME)
public class ConveyorTest extends OpMode {
    Servo ConveyorServo1;
    Servo ConveyorServo2;

    @Override
    public void init() {
        ConveyorServo1 = hardwareMap.get(Servo.class, "c1");
        ConveyorServo2 = hardwareMap.get(Servo.class, "c2");
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
    }
}
