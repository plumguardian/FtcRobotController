package org.firstinspires.ftc.teamcode.config.motors;

import com.qualcomm.robotcore.hardware.configuration.DistributorInfo;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.MotorType;

import org.firstinspires.ftc.robotcore.external.navigation.Rotation;

@MotorType(
        ticksPerRev=28D,
        gearing=1D,
        maxRPM=6000D,
        orientation=Rotation.CCW,
        achieveableMaxRPMFraction=0.9
)
@DeviceProperties(
        xmlTag="goBILDA5000Motor",
        name="GoBILDA 5000 Motor"
)
@DistributorInfo(
        distributor="goBILDA_distributor",
        model="5000-0002-4008",
        url="https://www.gobilda.com/5000-series-12vdc-motor-with-8mm-rex-pinion-shaft/"
)
public interface GoBILDA5000Motor {}
