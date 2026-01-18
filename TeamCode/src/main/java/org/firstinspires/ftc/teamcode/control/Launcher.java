package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.HashMap;
import java.util.Objects;

public class Launcher implements Mechanism {

    private final Telemetry telemetry;
    private final ElapsedTime time;
    private final DcMotor launcherMotor;
    private final IMU imu;

    public double launcherMotorPower = 0;
    public double launchPower = 0.8;
    public boolean launch = false;
    private int timer = 0;
    private final int timerValue = 100;

    public Launcher(Telemetry telemetry, ElapsedTime time, DcMotor launcherMotor, IMU imu) {
        this.telemetry = telemetry;
        this.time = time;
        this.launcherMotor = launcherMotor;
        this.imu = imu;

        launcherMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        //launcherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void gamepadPower(Gamepad gamepad) {
        if (gamepad.right_trigger >= 0.4 && timer == 0) {
            launch = !launch;
            timer = timerValue;
        }

        if (launch)
            launcherMotorPower = launchPower;
        else
            launcherMotorPower = 0;

    }

    @Override
    public void move() {
        launcherMotor.setPower(launcherMotorPower);

        telemetry.addLine("### Launcher ###");
        telemetry.addData("Launcher power", launcherMotorPower);

        if (timer > 0)
            timer--;
    }

    @Override
    public HashMap<String, Object> getData() {
        return new HashMap<String, Object>() {{
            put("launchMotorPower", launcherMotorPower);
        }};
    }

    @Override
    public void setData(HashMap<String, String> data) {
        launcherMotorPower = Double.parseDouble(Objects.requireNonNull(data.get("launchMotorPower")));
    }
}
