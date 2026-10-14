package org.firstinspires.ftc.teamcode.config.camera;

import com.bylazar.field.FieldManager;
import com.bylazar.field.FieldPresets;
import com.bylazar.field.PanelsField;

import org.firstinspires.ftc.teamcode.config.TeamCode;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class FieldCameraOpMode<T extends AprilTagProcessor> extends CameraOpMode<T> {
    protected FieldManager field;

    protected FieldCameraOpMode(
            @NotNull Function<
                    TeamCode.@NotNull HardwareGetter,
                    TeamCode.HardwareGetter.@NotNull Vision<T>
            > visionSupplier
    ) {
        super(visionSupplier);
    }

    @Override
    public void init() {
        super.init();
        field = PanelsField.INSTANCE.getField();
        field.setOffsets(FieldPresets.INSTANCE.getDEFAULT_FTC());
        field.update();
    }

    @Override
    public void loop() {
        super.loop();
        field.update();
    }
}
