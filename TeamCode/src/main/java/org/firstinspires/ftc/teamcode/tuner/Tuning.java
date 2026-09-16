package org.firstinspires.ftc.teamcode.tuner;

import androidx.annotation.NonNull;

import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.jetbrains.annotations.Contract;

public class Tuning {
    @NonNull
    @Contract(" -> new")
    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }
}
