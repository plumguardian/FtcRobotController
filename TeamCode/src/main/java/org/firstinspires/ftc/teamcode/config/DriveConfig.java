package org.firstinspires.ftc.teamcode.config;

import com.bylazar.configurables.annotations.Configurable;

public class DriveConfig {
    @Configurable
    public static class DriveConfigPanels {
        // Don't manually change values. Control it with panels.
        public static boolean MOTORS_ACTIVE = true;
        public static boolean USE_PANELS_GAMEPAD = false;
    }
}
