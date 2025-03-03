package org.firstinspires.ftc.teamcode.control;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.control.Mechanism;

import java.util.HashMap;


public class Arms implements Mechanism {

    private final Telemetry telemetry;
    private final ElapsedTime time;
    private final DcMotor bigArmMotor;
    private final DcMotor littleArmMotor;
    private final Servo grabServo;
    private final Servo collectorServo;
    private final IMU imu;

    public double bigArmPower = 0;
    public double littleArmPower = 0;
    public double grabPosition = 0.55;
    public double collectorPosition = 0;

    public double bigBalance = 0.06;
    public double littleBalance = 0.05;

    public double timer = 0;
    public int    timerValue = 100;

    public double balanceTimer = 0;
    public int    balanceTimerValue = 20;

    public Arms(Telemetry telemetry, ElapsedTime time, DcMotor bigArmMotor, DcMotor littleArmMotor, Servo grabServo, Servo collectorServo, IMU imu) {
        this.telemetry = telemetry;
        this.time = time;
        this.bigArmMotor = bigArmMotor;
        this.littleArmMotor = littleArmMotor;
        this.grabServo = grabServo;
        this.collectorServo = collectorServo;
        this.imu = imu;

        littleArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        littleArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void gamepadPower(Gamepad gamepad, Gamepad gamepad2, double precision, boolean armBlock) {

        if (balanceTimer == 0 && gamepad2.dpad_down) {
            bigBalance = -bigBalance;
            balanceTimer = balanceTimerValue;
        }
        if (balanceTimer == 0 && gamepad2.dpad_up) {
            littleBalance = -littleBalance;
            balanceTimer = balanceTimerValue;
        }

        if (!armBlock) {
            bigArmPower = -gamepad.right_stick_y;

            bigArmPower *= precision;

            bigArmPower += bigBalance;
        }

        littleArmPower = 0;
        if (gamepad.dpad_down){
            littleArmPower = 0.5;
        } else if (gamepad.dpad_up) {
            littleArmPower = -0.4;
        }
        littleArmPower += littleBalance;

        if (timer == 0 && (gamepad.circle || gamepad.cross || gamepad.square || gamepad.triangle)) {
            if (collectorPosition == 1) {
                collectorPosition = 0;
            } else {
                collectorPosition = 1;
            }
            timer = timerValue;
        }

        if (gamepad.right_trigger<=0.4) {
            grabPosition = 0.55;
        } else {
            grabPosition = 0.35;
        }

        if (timer>0) {
            timer -= 1;
        }
        if (balanceTimer > 0) {
            balanceTimer -= 1;
        }
    }

    public void move() {
        bigArmMotor.setPower(bigArmPower);
        littleArmMotor.setPower(littleArmPower);

        grabServo.setPosition(grabPosition);
        collectorServo.setPosition(collectorPosition);

        telemetry.addLine("### Mouvements bras ###");
        telemetry.addData("Big balance", bigBalance);
        telemetry.addData("Little balance", littleBalance);
        telemetry.addData("Big arm power", bigArmPower);
        telemetry.addData("Little Arm power", littleArmPower);
        telemetry.addData("Grab position", grabPosition);
        telemetry.addData("Collector position", collectorPosition);

    }

    public HashMap<String, Object> getData() {
        return new HashMap<String, Object>() {{
            put("bigArmPower", bigArmPower);
            put("littleArmPower", littleArmPower);
            put("grabPosition", grabPosition);
            put("collectorPosition", collectorPosition);
        }};
    }

    public void setData(HashMap<String, String> data) {
        bigArmPower = Double.parseDouble(data.get("bigArmPower"));
        littleArmPower = Double.parseDouble(data.get("littleArmPower"));
        grabPosition = Double.parseDouble(data.get("grabPosition"));
        collectorPosition = Double.parseDouble(data.get("collectorPosition"));
    }

}