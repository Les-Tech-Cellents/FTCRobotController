package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Auto move")
public class AutoOpModeMove extends BaseAutoOpMode {
    private static final String logFile = "move_forward.txt";

    public AutoOpModeMove() {
        super(logFile);
    }
}
