package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Auto 2 specimens")
public class AutoOpMode2Specimen extends BaseAutoOpMode {
    private static final String logFile = "2_specimen.txt";

    public AutoOpMode2Specimen() {
        super(logFile);
    }
}
