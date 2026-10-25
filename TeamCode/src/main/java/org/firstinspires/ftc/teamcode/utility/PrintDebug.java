package org.firstinspires.ftc.teamcode.utility;

import static org.firstinspires.ftc.teamcode.utility.PrintDebug.PrintDebugTest.*;

//#if !match
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.config.DualTelemetry;
//#endif

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.config.TeamCode;

@Utility(name = "Print Debug", description = "Logs various info about the robot")
public class PrintDebug extends LinearOpMode {
//#if !match
    @Configurable
//#endif
    protected static class PrintDebugTest {
        public static final String saved = "<font color='#e37c07'>";
        public static final String detected = "<font color='#09e087'>";
        public static final String calculated = "<font color='#451fed'>";
        public static final String end = "</font>";
    }

    @Override
    public void runOpMode() {
//#if !match
        telemetry = new DualTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
//#endif
        final TeamCode.HardwareGetter hardwareGetter = new TeamCode.HardwareGetter(hardwareMap, telemetry);
        final Motor.GoBILDA hardwareFld = hardwareGetter.getMotorRpm("fld");
        final MotorConfigurationType fld = hardwareMap.get(DcMotor.class, "fld").getMotorType();
        final double cpr = fld.getTicksPerRev();

        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML); // TODO: do i need to set this back?

        telemetry.addData(saved + "Saved ticks/sec" + end, hardwareFld.getAchievableMaxTicksPerSecond());
        telemetry.addData(saved + "Saved cpr" + end, hardwareFld.getCPR());
        telemetry.addData(saved + "Saved rpm" + end, hardwareFld.getRPM());
        telemetry.addData(detected + "Detected ticks/sec" + end, fld.getAchieveableMaxTicksPerSecond());
        telemetry.addData(detected + "Detected cpr" + end, cpr);
        telemetry.addData(detected + "Detected rpm" + end, fld.getMaxRPM());
        telemetry.addData(detected + "Detected rpm fraction" + end, fld.getAchieveableMaxRPMFraction());
        telemetry.addData(detected + "Detected gearing" + end, fld.getGearing());
        telemetry.addData(detected + "Detected ticks/rev" + end, fld.getTicksPerRev());
        telemetry.addData(calculated + "Calculated forwardTicksToInches" + end, (Math.PI * 3.75D) / (cpr * (1D+(46D/17D)) * (1D+(46D/11D))));
        telemetry.addData(calculated + "Calculated strafeTicksToInches" + end, 2D * Math.PI / 2816.5D);
        telemetry.update();

        waitForStart();
    }
}
