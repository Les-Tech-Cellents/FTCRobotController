package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode;

@TeleOp(name="Enregistreur")
public class LoggerOpMode extends BaseOpMode {
    public LoggerOpMode() {
        super(true, "2_specimen.txt");
    }
}
