package org.firstinspires.ftc.teamcode.config;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.RESET_YAW_TOGGLE;

import com.bylazar.configurables.annotations.Configurable;

public class DriveConfig {
    public static boolean OLD_RESET_YAW_TOGGLE = false;

    @Configurable
    public static class DriveConfigPanels {
        // Don't manually change values. Control it with panels.
        public static boolean MOTORS_ACTIVE = true;
        public static boolean RESET_YAW_TOGGLE = false;
        public static boolean USE_PANELS_GAMEPAD = false;
    }

    @Configurable
    public static class EncoderConfigPanels {
        // Don't manually change values. Control it with panels.
        public static boolean useVelocityControl = true; // FIXME: seems to cause issues?
        public static boolean enableHardwareEncoders = true; // TODO: it seems all this does it enable velocity, so idk if I need this
        public static boolean useMotorExVelo = true;
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
