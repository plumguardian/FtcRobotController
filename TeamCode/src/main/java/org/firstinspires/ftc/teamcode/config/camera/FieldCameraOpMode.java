package org.firstinspires.ftc.teamcode.config.camera;

//#if flavor != competition
import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;
//#endif

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.function.Function;

public abstract class FieldCameraOpMode<T extends AprilTagProcessor> extends CameraOpMode<T> {
//#if flavor != competition
    protected FieldManager field;
//#endif

    protected FieldCameraOpMode(
            @NonNull Function<
                    TeamCode.HardwareGetter,
                    TeamCode.HardwareGetter.Vision<T>
            > visionSupplier
    ) {
        super(visionSupplier);
    }

    @Override
    public void init() {
        super.init();
//#if flavor != competition
        field = PanelsField.INSTANCE.getField();
        field.setOffsets(FieldPresets.INSTANCE.getDEFAULT_FTC());
        field.update();
//#endif
    }

    @Override
    public void loop() {
        super.loop();
//#if flavor != competition
        field.update();
//#endif
    }
}
