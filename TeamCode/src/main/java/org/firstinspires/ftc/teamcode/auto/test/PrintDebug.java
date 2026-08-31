package org.firstinspires.ftc.teamcode.auto.test;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.teamcode.config.DualTelemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;

@Autonomous(name = "Print Debug", group = TeamCode.GROUP_NAME)
public class PrintDebug extends LinearOpMode {
    @Override
    public void runOpMode() {
        final DualTelemetry dualTelemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, dualTelemetry);
        final Motor.GoBILDA hardwareFld = hardwareGetter.getMotorRpm("fld");
        final MotorConfigurationType fld = hardwareMap.get(DcMotor.class, "fld").getMotorType();
        final double cpr = fld.getTicksPerRev();

        dualTelemetry.addData("Saved ticks/sec", hardwareFld.getAchievableMaxTicksPerSecond());
        dualTelemetry.addData("Saved cpr", hardwareFld.getCPR());
        dualTelemetry.addData("Saved rpm", hardwareFld.getRPM());
        dualTelemetry.addData("Detected ticks/sec", fld.getAchieveableMaxTicksPerSecond());
        dualTelemetry.addData("Detected cpr", cpr);
        dualTelemetry.addData("Detected rpm", fld.getMaxRPM());
        dualTelemetry.addData("Detected rpm fraction", fld.getAchieveableMaxRPMFraction());
        dualTelemetry.addData("Detected gearing", fld.getGearing());
        dualTelemetry.addData("Detected ticks/rev", fld.getTicksPerRev());
        dualTelemetry.addData("Calculated forwardTicksToInches", (Math.PI * 3.75D) / (cpr * (1D+(46D/17D)) * (1D+(46D/11D))));
        dualTelemetry.addData("Calculated strafeTicksToInches", 2D * Math.PI / 2816.5D);
        dualTelemetry.update();

        waitForStart();
    }
}
