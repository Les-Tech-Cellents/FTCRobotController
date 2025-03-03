package org.firstinspires.ftc.teamcode.logging;

import com.qualcomm.robotcore.util.ElapsedTime;
import java.io.File;

import org.firstinspires.ftc.teamcode.control.Mechanism;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class RobotLogger {
    private final ArrayList<String> logs;
    private final Mechanism[] mechanisms;
    private final ElapsedTime time;

    public RobotLogger(ElapsedTime time, Mechanism[] mechanisms) {
        logs = new ArrayList<>();

        this.mechanisms = mechanisms;
        this.time = time;

        StringBuilder line = new StringBuilder();
        line.append("time").append(";");
        for (Mechanism mech : this.mechanisms) {
            for (Object keys : mech.getData().keySet()) {
                line.append(keys.toString()).append(";");
            }
        }
        line.setLength(line.length() - 1);
        logLine(line.toString());
    }

    private void logLine(String line) {
        logs.add(line);
    }

    public void logCurrentPos(double precision) {
        StringBuilder line = new StringBuilder();
        line.append(time.time()).append(";");
        for (Mechanism mech : this.mechanisms) {
            for (Object obj : mech.getData().values()) {
                line.append(obj.toString()).append(";");
            }
        }
        line.setLength(line.length() - 1);
        logLine(line.toString());
    }

    public void saveAndStop(String destFile) {
        try {
            File dest = new File(LogReader.DIR_PATH + destFile);
            dest.getParentFile().mkdirs();
            dest.createNewFile();
            PrintWriter writer = new PrintWriter(dest);
            writer.print("");

            for (String line : logs) {
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

