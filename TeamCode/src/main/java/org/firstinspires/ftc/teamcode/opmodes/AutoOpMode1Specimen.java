package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Auto 1 specimen")
public class AutoOpMode1Specimen extends BaseAutoOpMode {
    private static final String logFile = "1_specimen.txt";

    public AutoOpMode1Specimen() {
        super(logFile);
    }
}
