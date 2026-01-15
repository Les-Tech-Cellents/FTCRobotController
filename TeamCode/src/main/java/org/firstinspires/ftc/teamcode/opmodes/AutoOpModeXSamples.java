package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.logging.LogReader;
import org.firstinspires.ftc.teamcode.control.Mechanism;
import org.firstinspires.ftc.teamcode.control.Wheels;
import org.firstinspires.ftc.teamcode.control.Storage;

@Autonomous(name="Auto X samples")
public class AutoOpModeXSamples extends BaseAutoOpMode {
    private static final String logFile = "x_samples.txt";

    public AutoOpModeXSamples() {
        super(logFile);
    }
}
