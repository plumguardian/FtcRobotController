package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.mecanumdrive.AbstractMove;

@Autonomous(name = "Move Forward", group = "Move")
public class MoveForward extends AbstractMove {
    @Override
    public void loop() {
        // TODO: Motors may be continuous, maybe just set and leave it
        mecanumDrive.driveRobotCentric(0, MoveLeft.MoveToEndPanels.movePower, 0);
    }
}
