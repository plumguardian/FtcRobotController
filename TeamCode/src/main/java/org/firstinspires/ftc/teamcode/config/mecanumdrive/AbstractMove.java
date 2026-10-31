package org.firstinspires.ftc.teamcode.config.mecanumdrive;

//#if !match
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
//#endif

import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.teamcode.config.TeamCode;

import lombok.val;

public abstract class AbstractMove extends OpMode {
    protected MecanumDrive mecanumDrive;

//#if !match
    @Configurable
//#endif
    public static class MoveToEndPanels {
        // Don't manually change values. Control it with panels.
        public static double movePower = 0.5;
    }

    @Override
    public void init() {
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class))
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        val hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        val motors = hardwareGetter.getMotors();
        val frontLeftDrive = motors.frontLeft();
        val frontRightDrive = motors.frontRight();
        val backLeftDrive = motors.backLeft();
        val backRightDrive = motors.backRight();
        frontLeftDrive.setRunMode(Motor.RunMode.RawPower);
        frontRightDrive.setRunMode(Motor.RunMode.RawPower);
        backLeftDrive.setRunMode(Motor.RunMode.RawPower);
        backRightDrive.setRunMode(Motor.RunMode.RawPower);

        mecanumDrive =
//#if !match
                new MecanumDrivePanels(false, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive, PanelsTelemetry.INSTANCE.getFtcTelemetry());
//#else
//$$                 new MecanumDrive(false, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);
//#endif
    }

    @Override
    public void stop() {
        mecanumDrive.stop();
    }
}
