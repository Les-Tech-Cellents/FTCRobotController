package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.logging.LogReader;
import org.firstinspires.ftc.teamcode.control.Mechanism;
import org.firstinspires.ftc.teamcode.control.Wheels;
import org.firstinspires.ftc.teamcode.control.Arms;

@Autonomous(name="Auto 1 specimen")
public class AutoOpMode1Specimen extends LogReader {
    private static String logFile = "1_specimen.txt";

    private Wheels wheels;
    private Arms arms;

    private ElapsedTime time;

    public AutoOpMode1Specimen() {
        super(logFile);
    }

    @Override
    public void init() {
        super.init();

        time = new ElapsedTime();

        IMU imu = hardwareMap.get(IMU.class, "imu");

        wheels = new Wheels(telemetry, time,
                hardwareMap.get(DcMotor.class, "MotorRoueG"),
                hardwareMap.get(DcMotor.class, "MotorRoueD"),
                imu
        );

        arms = new Arms(telemetry, time,
                hardwareMap.get(DcMotor.class, "MotorBras1"),
                hardwareMap.get(DcMotor.class, "MotorBras2"),
                hardwareMap.get(Servo.class, "servoPince"),
                hardwareMap.get(Servo.class, "servoTourniquet"),
                imu
        );

        this.mechanisms = new Mechanism[]{
                this.wheels,
                this.arms
        };
    }
}
