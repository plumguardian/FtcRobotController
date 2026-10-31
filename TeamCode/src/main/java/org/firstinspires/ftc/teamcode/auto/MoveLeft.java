package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.mecanumdrive.AbstractMove;

@Autonomous(name = "Move Left", group = "Move")
public class MoveLeft extends AbstractMove {
    @Override
    public void loop() {
        // TODO: Motors may be continuous, maybe just set and leave it
        mecanumDrive.driveRobotCentric(-MoveToEndPanels.movePower, MoveToEndPanels.movePower, 0);
    }
}
