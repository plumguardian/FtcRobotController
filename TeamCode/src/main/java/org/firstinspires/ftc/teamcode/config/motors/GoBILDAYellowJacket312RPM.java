package org.firstinspires.ftc.teamcode.config.motors;

import static org.firstinspires.ftc.teamcode.config.motors.GoBILDAYellowJacket312RPM.GEARING;

import com.qualcomm.robotcore.hardware.configuration.DistributorInfo;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.MotorType;

import org.firstinspires.ftc.robotcore.external.navigation.Rotation;

@MotorType(
        ticksPerRev=GEARING * 28D,
        gearing=GEARING,
        maxRPM=312D,
        orientation=Rotation.CCW,
        achieveableMaxRPMFraction=0.9
)
@DeviceProperties(
        xmlTag="goBILDAYellowJacket312RPM",
        name="GoBILDA Yellow Jacket 312 RPM"
)
@DistributorInfo(
        distributor="goBILDA_distributor",
        model="5202-2402-0019",
        url="https://www.gobilda.com/5202-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-6mm-d-shaft-312-rpm-36mm-gearbox-3-3-5v-encoder/"
)
public interface GoBILDAYellowJacket312RPM {
    double GEARING = (1D + 46D / 17D) * (1D + 46D / 11D);
}
