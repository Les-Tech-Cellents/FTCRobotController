package org.firstinspires.ftc.teamcode.logging;

import android.os.Environment;
import java.util.ArrayList;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.control.Mechanism;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class LogReader extends OpMode {

    //public static final String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FIRST/logs/";
    public static final String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FIRST/java/src/org/firstinspires/ftc/teamcode/logs/";

    private final String                   logFile;
    public        Mechanism[]              mechanisms;
    private       Scanner                  scanner;
    private       HashMap<Integer, String> positions;
    private       double                   lastLogTime;
    private       HashMap<String, String>  lastPos;
    private       int                      instructionId = 0;
    private       ArrayList<HashMap<String, String>> instructions = new ArrayList<>();

    public LogReader(String logFile) {
        this.logFile = logFile;
    }

    public void openFile(String fileName) {
        try {
            File destFile = new File(DIR_PATH + fileName);
            scanner = new Scanner(destFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String[] splittedLine = scanner.nextLine().split(";");

        positions = new HashMap<>();
        for (int i = 0; i < splittedLine.length; i++) {
            positions.put(i, splittedLine[i]);
        }

        // ...
        String line = next();
        while (line != null) {
            instructions.add(getPosLine(line));
            line = next();
        }

    }

    public String next() {
        String res = null;
        if (scanner.hasNextLine()) {
            res = scanner.nextLine();
        }
        return res;
    }

    private HashMap<String, String> getPosLine(String line) {
        String[] splittedLine = line.split(";");
        HashMap<String, String> pos = new HashMap<>();
        for (int i = 0; i < splittedLine.length; i++) {
            pos.put(positions.get(i), splittedLine[i]);
        }
        return pos;
    }

    @Override
    public void init() {
        openFile(this.logFile);
    }

    @Override
    public void start() {
        /*String line = next();

        if (line != null) {
            lastPos = getPosLine(line);
            lastLogTime = Double.parseDouble(Objects.requireNonNull(lastPos.get("time")));
        }*/

        resetRuntime();
    }

    @Override
    public void loop() {
        /*if (getRuntime() >= lastLogTime) {
            String line = next();

            if (line != null) {
                lastPos = getPosLine(line);
                lastLogTime = Double.parseDouble(Objects.requireNonNull(lastPos.get("time")));

                for (Mechanism mech : mechanisms) {
                    mech.setData(lastPos);
                    mech.move();
                }
            }
        } else {
            for (Mechanism mech : mechanisms) {
                mech.setData(lastPos);
                mech.move();
            }
        }*/

        //TODO
        HashMap<String, String> pos = instructions.get(instructionId);
        boolean stop = false;
        while (Double.parseDouble(pos.get("time")) < getRuntime() && !stop) {
            if (instructionId < instructions.size() - 1) {
                instructionId += 1;
                pos = instructions.get(instructionId);
            } else {
                stop = true;
            }
        }
        lastPos = pos;
        lastLogTime = Double.parseDouble(lastPos.get("time"));

        for (Mechanism mech : mechanisms) {
            mech.setData(lastPos);
            mech.move();
        }

        telemetry.addLine("### LogReader ###");
        telemetry.addData("Current time", getRuntime());
        telemetry.addData("Current log time", lastLogTime);
        telemetry.update();
    }

}

