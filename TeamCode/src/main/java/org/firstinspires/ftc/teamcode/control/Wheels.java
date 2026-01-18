package org.firstinspires.ftc.teamcode.control;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.util.HashMap;
import java.util.Objects;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class Wheels implements Mechanism {

    private final Telemetry telemetry;
    private final ElapsedTime time;
    private final DcMotor leftWheelsMotor;
    private final DcMotor rightWheelsMotor;
    private final IMU imu;

    public double leftWheelsPower = 0;
    public double rightWheelsPower = 0;
    public boolean frontCollect = true;
    private double leftBalanceValue = 0.02;

    private int timer = 0;
    private final int timerValue = 100;
    private Double timeStamp = null;
    private final double turnTime = 0.3;
    private boolean leftTurn = false;
    private double targetedYaw = 0;

    public Wheels(Telemetry telemetry, ElapsedTime time, DcMotor leftWheelsMotor, DcMotor rightWheelsMotor, IMU imu) {
        this.telemetry = telemetry;
        this.time = time;
        this.leftWheelsMotor = leftWheelsMotor;
        this.rightWheelsMotor = rightWheelsMotor;
        this.imu = imu;

        leftWheelsMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightWheelsMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        leftWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void gamepadPower(Gamepad gamepad, double precision) {
        double turn = 0;
        double forward = 0;
        YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();

        if (timeStamp == null) {
            turn = -gamepad.left_stick_x;
            forward = gamepad.left_stick_y;

            if (timer == 0) {
                //leftWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                //rightWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                if (false && gamepad.left_bumper) {
                    turn = 1;
                    leftTurn = true;

                    timeStamp = time.time() + turnTime;
                    precision = 1;
                    timer = timerValue;
                    //leftWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    //rightWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

                    targetedYaw = angles.getYaw();
                    for (int i=0; i<90; i++) {
                        targetedYaw += 1;
                        if (targetedYaw > 180) {
                            targetedYaw -= 360;
                        }
                    }
                } else if (false && gamepad.right_bumper) {
                    turn = -1;
                    leftTurn = false;

                    timeStamp = time.time() + turnTime;
                    precision = 1;
                    timer = timerValue;
                    leftWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    rightWheelsMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

                    targetedYaw = angles.getYaw();
                    for (int i=0; i<90; i++) {
                        targetedYaw -= 1;
                        if (targetedYaw < -180) {
                            targetedYaw += 360;
                        }
                    }
                }

                if (gamepad.dpad_right) {
                    frontCollect = !frontCollect;
                    timer = timerValue;
                }
            }
        } else {
            if (timeStamp <= time.time() /*&& (angles.getYaw() >= targetedYaw -5 && angles.getYaw() <= targetedYaw +5)*/) {
                timeStamp = null;
            } else {
                if (leftTurn) {
                    turn = 1;
                } else {
                    turn = -1;
                }
                precision = 1;
            }
        }

        setPower(forward, turn, precision);
    }

    public void setPower(double forward, double turn, double precision) {
        if (turn < 0.3 && turn > -0.3) {
            turn = 0;
        } else {
            if (forward < 0.4 && forward > -0.4) {
                forward = 0;
            }
        }

        double leftBalance = 0;
        if (forward >= 0.01) {
            leftBalance = leftBalanceValue;
        } else if (forward <= -0.01) {
            leftBalance = leftBalanceValue;
        }

        if (frontCollect) {
            leftWheelsMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            rightWheelsMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            leftWheelsPower = forward + turn;
            rightWheelsPower = forward - turn;
        } else {
            leftWheelsMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            rightWheelsMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            leftWheelsPower = forward - turn;
            rightWheelsPower = forward + turn;
        }

        leftWheelsPower *= precision;
        rightWheelsPower *= precision;

        leftWheelsPower += leftBalance;
    }

    public void move() {
        leftWheelsMotor.setPower(leftWheelsPower);
        rightWheelsMotor.setPower(rightWheelsPower);

        telemetry.addLine("### Mouvements roues ###");
        telemetry.addData("targetedYaw", targetedYaw);
        telemetry.addData("timer", timer);
        telemetry.addData("Left wheels power", leftWheelsPower);
        telemetry.addData("Right wheels power", rightWheelsPower);

        if (timer != 0) {
            timer -= 1;
        }

    }

    public HashMap<String, Object> getData() {
        return new HashMap<String, Object>() {{
            put("leftWheelsPower", leftWheelsPower);
            put("rightWheelsPower", rightWheelsPower);
        }};
    }

    public void setData(HashMap<String, String> data) {
        leftWheelsPower = Double.parseDouble(Objects.requireNonNull(data.get("leftWheelsPower")));
        rightWheelsPower = Double.parseDouble(Objects.requireNonNull(data.get("rightWheelsPower")));
    }

}