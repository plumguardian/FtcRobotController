package org.firstinspires.ftc.teamcode.config

import com.pedropathing.algorithm.Foresight
import com.pedropathing.algorithm.ForesightConfig
import com.pedropathing.follower.Follower
import com.pedropathing.revhub.drivetrains.Mecanum
import com.pedropathing.revhub.drivetrains.MecanumConfig
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.jetbrains.annotations.Contract

object Constants {
    @JvmField
    val MECANUM_CONFIG = MecanumConfig { c ->
        c.frontLeftName.set("fld")
        c.backLeftName.set("bld")
        c.frontRightName.set("frd")
        c.backRightName.set("brd")

        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE)
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE)
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD)
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD)
    }

    @JvmField
    val FORESIGHT_CONFIG = ForesightConfig { c ->
        // TODO: tune
    }

    @JvmStatic
    @Contract("_ -> new")
    fun create(hardwareMap: HardwareMap) =
        Follower(
            null,
            Mecanum(hardwareMap, MECANUM_CONFIG),
            Foresight(FORESIGHT_CONFIG)
        )
}
