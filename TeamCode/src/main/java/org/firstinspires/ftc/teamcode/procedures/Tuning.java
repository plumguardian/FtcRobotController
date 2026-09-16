package org.firstinspires.ftc.teamcode.procedures;

import androidx.annotation.NonNull;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.config.Constants;
import org.jetbrains.annotations.Contract;

public class Tuning {
    @NonNull
    @Contract(" -> new")
    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(
                null,
                hardwareMap -> new Mecanum(hardwareMap, Constants.MECANUM_CONFIG)
        );
    }

    @NonNull
    @Contract(" -> new")
    @Tuner
    public static Procedure tests() {
        return new Tests(
                hardwareMap -> new Mecanum(hardwareMap, Constants.MECANUM_CONFIG),
                null,
                () -> new Foresight(Constants.FORESIGHT_CONFIG)
        );
    }
}
