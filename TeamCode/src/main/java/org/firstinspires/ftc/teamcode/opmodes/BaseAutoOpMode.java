package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.control.Launcher;
import org.firstinspires.ftc.teamcode.logging.LogReader;
import org.firstinspires.ftc.teamcode.control.Mechanism;
import org.firstinspires.ftc.teamcode.control.Wheels;
import org.firstinspires.ftc.teamcode.control.Storage;

public class BaseAutoOpMode extends LogReader {
    private Wheels wheels;
    private Storage storage;
    private Launcher launcher;

    private ElapsedTime time;

    public BaseAutoOpMode(String logFile) {
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

        storage = new Storage(telemetry, time,
                hardwareMap.get(Servo.class, "servoCollector"),
                hardwareMap.get(DcMotor.class, "MotorDrum"),
                hardwareMap.get(Servo.class, "servoLatch"),
                imu
        );

        launcher = new Launcher(telemetry, time,
                hardwareMap.get(DcMotor.class, "MotorLauncher"),
                imu
        );

        this.mechanisms = new Mechanism[]{
                this.wheels,
                this.storage,
                this.launcher
        };
    }
}