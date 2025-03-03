package org.firstinspires.ftc.teamcode.logging;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

@Autonomous(name="Send Log")
@Disabled
public class SendLog extends OpMode {

    private static String destFile = "test_log.log";

    private static String[] logValue = {
            "time;rightWheelsPower;leftWheelsPower;grabPosition;collectorPosition;bigArmPower;littleArmPower",
            "0.0;0.0;0.0;0.55;0.0;0.06;0.0",
            "1.0;0.5;-0.5;0.55;0.0;0.06;0.0",
            "1.00005;0.0;0.0;0.55;0.0;0.06;0.0"
    };

    @Override
    public void init(){
        try {
            File dest = new File(LogReader.DIR_PATH + destFile);
            dest.getParentFile().mkdirs();
            dest.createNewFile();
            PrintWriter writer = new PrintWriter(dest);
            writer.print("");

            for (String line : logValue) {
                writer.println(line);
                telemetry.addLine(line);
            }
            writer.close();
            telemetry.update();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loop() {
        //Rien
    }
}