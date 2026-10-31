package org.firstinspires.ftc.teamcode.config;

//#if !match
import com.bylazar.configurables.annotations.Configurable;
//#endif

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.RESET_YAW_TOGGLE;
import androidx.annotation.NonNull;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class DriveConfig {
    public static boolean OLD_RESET_YAW_TOGGLE = false;

//#if !match
    @Configurable
//#endif
    public static class DriveConfigPanels {
        // Don't manually change values. Control it with panels.
        public static boolean MOTORS_ACTIVE = true;
        public static boolean RESET_YAW_TOGGLE = false;
        public static boolean USE_PANELS_GAMEPAD = false;
    }

    // TODO: Map does not support DcMotorSimple.Direction
//#if !match
    @Configurable
//#endif
    public static class DriveDirPanels {
        // Don't manually change values. Control it with panels.
        public static DcMotorSimple.Direction FLD = DcMotorSimple.Direction.FORWARD;
        public static DcMotorSimple.Direction FRD = DcMotorSimple.Direction.REVERSE;
        public static DcMotorSimple.Direction BLD = DcMotorSimple.Direction.FORWARD;
        public static DcMotorSimple.Direction BRD = DcMotorSimple.Direction.REVERSE;

        public static DcMotorSimple.Direction getDir(@NonNull String name) {
            return switch (name) {
                case "fld" -> FLD;
                case "frd" -> FRD;
                case "bld" -> BLD;
                case "brd" -> BRD;
                default -> throw new IllegalStateException("Unexpected value: " + name);
            };
        }
    }

//#if !match
    @Configurable
//#endif
    public static class LaunchPanels {
        public static double ANGLE_LOAD = 220.0;
        public static double ANGLE_NEUTRAL = 120.0;
        public static double ANGLE_FIRE = 60.0;
        public static double SPEED_FIRE = 0.7;
        public static double SPEED_SUCK = -0.4;
    }

//#if !match
    @Configurable
//#endif
    public static class EncoderConfigPanels {
        // Don't manually change values. Control it with panels.
        public static boolean useVelocityControl = false; // FIXME: seems to cause issues?
        public static boolean enableHardwareEncoders = false; // TODO: it seems all this does it enable velocity, so idk if I need this
        public static boolean useMotorExVelo = false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean updateYawToggle() {
        OLD_RESET_YAW_TOGGLE = RESET_YAW_TOGGLE;
        return OLD_RESET_YAW_TOGGLE;
    }

    public static boolean updateAndCheckYawToggle() {
        final boolean result = RESET_YAW_TOGGLE != OLD_RESET_YAW_TOGGLE;
        updateYawToggle();
        return result;
    }
}
