package org.firstinspires.ftc.teamcode.config;

import androidx.annotation.NonNull;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.jetbrains.annotations.Contract;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public final MecanumConfig MECANUM_CONFIG = new MecanumConfig(c -> {
        c.frontLeftName.set("fld");
        c.backLeftName.set("bld");
        c.frontRightName.set("frd");
        c.backRightName.set("brd");

        c.frontLeftDirection.set(DcMotor.Direction.REVERSE);
        c.backLeftDirection.set(DcMotor.Direction.REVERSE);
        c.frontRightDirection.set(DcMotor.Direction.FORWARD);
        c.backRightDirection.set(DcMotor.Direction.FORWARD);
    });

    public ForesightConfig FORESIGHT_CONFIG = new ForesightConfig(c -> {

    });

    @NonNull
    @Contract("_ -> new")
    public Follower create(HardwareMap h) {
        return new Follower(
                null,
                new Mecanum(h, MECANUM_CONFIG),
                new Foresight(FORESIGHT_CONFIG)
        );
    }
}
