package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Auto X samples")
public class AutoOpModeXSamples extends BaseAutoOpMode {
    private static final String logFile = "x_samples.txt";

    public AutoOpModeXSamples() {
        super(logFile);
    }
}
