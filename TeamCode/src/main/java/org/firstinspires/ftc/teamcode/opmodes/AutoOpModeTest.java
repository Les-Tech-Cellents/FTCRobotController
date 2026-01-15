package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Auto Test")
public class AutoOpModeTest extends BaseAutoOpMode {
    private static final String logFile = "test_log.txt";

    public AutoOpModeTest() {
        super(logFile);
    }
}
