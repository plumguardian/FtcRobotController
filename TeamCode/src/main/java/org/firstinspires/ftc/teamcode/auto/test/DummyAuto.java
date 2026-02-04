package org.firstinspires.ftc.teamcode.auto.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.config.TeamCode;

@SuppressWarnings("BusyWait")
@Autonomous(name = "Dummy Auto", group = TeamCode.GROUP_NAME)
public class DummyAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive())
            Thread.sleep(100);
    }
}
