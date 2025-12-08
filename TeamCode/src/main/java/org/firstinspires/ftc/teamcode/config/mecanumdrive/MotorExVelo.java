package org.firstinspires.ftc.teamcode.config.mecanumdrive;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@SuppressWarnings("unused")
public class MotorExVelo extends MotorEx {
    public MotorExVelo(final @NonNull HardwareMap hMap, final String id) {
        super(hMap, id);
    }

    public MotorExVelo(final @NonNull HardwareMap hMap, final String id, final @NonNull GoBILDA gobildaType) {
        super(hMap, id, gobildaType);
    }

    public MotorExVelo(final @NonNull HardwareMap hMap, final String id, final double cpr, final double rpm) {
        super(hMap, id, cpr, rpm);
    }

    public void setBufferFraction(final double val) {
        bufferFraction = val;
    }

    public double getBufferFraction() {
        return bufferFraction;
    }

    @Override
    public void set(final double output) {
        if (runmode == RunMode.VelocityControl)
            // TODO: caching
            motorEx.setVelocity(output * ACHIEVABLE_MAX_TICKS_PER_SECOND * bufferFraction);
        else if (runmode == RunMode.PositionControl)
            motor.setPower(output * positionController.calculate(getDistance()));
        else
            motor.setPower(output);
    }
}
