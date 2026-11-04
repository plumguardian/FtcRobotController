package org.firstinspires.ftc.teamcode.utility;

//#if !match
import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.USE_PANELS_GAMEPAD;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
//#endif

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.ToggleButtonReader;

@Utility(name = "Maintain Servo", description = "Holds the servos named '" + MaintainServo.ID1 + "' and '" + MaintainServo.ID2 + "' at an angle")
public class MaintainServo extends OpMode {
    public static final String ID1 = "gate1";
    public static final String ID2 = "gate2";

    private ServoImplEx gate1;
    private ServoImplEx gate2;
    private GamepadEx gamepad;
    private double pos;
    private ToggleButtonReader halt;

    @Override
    public void init() {
//#if !match
        telemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
//#endif

        for (LynxModule hub : hardwareMap.getAll(LynxModule.class))
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        gate1 = hardwareMap.get(ServoImplEx.class, ID1);
        gate2 = hardwareMap.get(ServoImplEx.class, ID2);
        gate1.setDirection(Servo.Direction.REVERSE);
        gate2.setDirection(Servo.Direction.FORWARD);
        gate1.setPwmRange(new PwmControl.PwmRange(500, 2500));
        gate2.setPwmRange(new PwmControl.PwmRange(500, 2500));
        gate1.setPwmEnable();
        gate2.setPwmEnable();

//#if !match
        if (USE_PANELS_GAMEPAD) {
            gamepad = new GamepadEx(PanelsGamepad.INSTANCE.getFirstManager().getAsFTCGamepad());
            telemetry.addLine("Using online gamepad");
        } else
//#endif
            gamepad = new GamepadEx(gamepad1);

        pos = 150.0;
        halt = new ToggleButtonReader(
                gamepad, GamepadKeys.Button.CROSS
        );

        telemetry.addLine("Use DPAD left/right to change the angle by 10 degrees");
        telemetry.addLine("Use DPAD up/down to change the angle by 1 degree");
        telemetry.addLine("Press the X/A to halt the servos");
        telemetry.update();
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
        halt.readValue();

        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT))
            pos += 10.0;
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_LEFT))
            pos -= 10.0;
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_UP))
            pos += 1.0;
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN))
            pos -= 1.0;

        if (halt.getState()) {
            telemetry.addLine("Halted");
        } else {
            double angle = pos / 300.0;
            gate1.setPosition(angle);
            gate2.setPosition(angle);
        }

        telemetry.addData("Angle", pos);
    }

    @Override
    public void stop() {
        gate1.setPwmDisable();
        gate2.setPwmDisable();
    }
}
