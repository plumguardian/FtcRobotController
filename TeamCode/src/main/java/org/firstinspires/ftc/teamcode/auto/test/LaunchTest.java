package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.hardware.motors.CRServoEx;

import org.firstinspires.ftc.teamcode.config.TeamCode;

@Autonomous(name = "Launch Test", group = TeamCode.GROUP_NAME)
public class LaunchTest extends OpMode {
    @Configurable
    private static class LaunchTestDir {
        public static double top = 1.0;
        public static double mid = 1.0;
        public static double end = 1.0;
    }

    private CRServoEx top;
    private CRServoEx mid;
    private CRServoEx end;

    @Override
    public void init() {
        top = new CRServoEx(hardwareMap, "top");
        top.stop();
        top.setRunMode(CRServoEx.RunMode.RawPower);
        top.setInverted(false);

        mid = new CRServoEx(hardwareMap, "mid");
        mid.stop();
        mid.setRunMode(CRServoEx.RunMode.RawPower);
        mid.setInverted(true);

        end = new CRServoEx(hardwareMap, "end");
        end.stop();
        end.setRunMode(CRServoEx.RunMode.RawPower);
        end.setInverted(false);
    }

    @Override
    public void loop() {
        top.set(LaunchTestDir.top);
        mid.set(LaunchTestDir.mid);
        end.set(LaunchTestDir.end);
    }
}
