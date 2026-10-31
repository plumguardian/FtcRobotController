package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.mecanumdrive.AbstractMove;

@Autonomous(name = "Move Right", group = "Move")
public class MoveRight extends AbstractMove {
    @Override
    public void loop() {
        // TODO: Motors may be continuous, maybe just set and leave it
        mecanumDrive.driveRobotCentric(MoveLeft.MoveToEndPanels.movePower, MoveLeft.MoveToEndPanels.movePower, 0);
    }
}
