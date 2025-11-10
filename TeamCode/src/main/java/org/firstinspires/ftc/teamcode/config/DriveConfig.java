package org.firstinspires.ftc.teamcode.config;

import static org.firstinspires.ftc.teamcode.config.DriveConfig.DriveConfigPanels.*;

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
    public static class SolverslibConfigPanels {
        // Don't manually change values. Control it with panels.
        public static boolean useSolverslibEncoders = false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean updateYawToggle() {
        OLD_RESET_YAW_TOGGLE = RESET_YAW_TOGGLE;
        return OLD_RESET_YAW_TOGGLE;
    }

    public static boolean updateAndCheckYawToggle() {
        boolean result = RESET_YAW_TOGGLE != OLD_RESET_YAW_TOGGLE;
        updateYawToggle();
        return result;
    }
}
