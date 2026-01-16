/*Copyright 2025

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.control.Launcher;
import org.firstinspires.ftc.teamcode.logging.RobotLogger;
import org.firstinspires.ftc.teamcode.control.Mechanism;
import org.firstinspires.ftc.teamcode.control.Wheels;
import org.firstinspires.ftc.teamcode.control.Storage;

/**
 * This file contains a minimal example of a Linear "OpMode". An OpMode is a 'program' that runs
 * in either the autonomous or the TeleOp period of an FTC match. The names of OpModes appear on
 * the menu of the FTC Driver Station. When an selection is made from the menu, the corresponding
 * OpMode class is instantiated on the Robot Controller and executed.
 *
 * Remove the @Disabled annotation on the next line or two (if present) to add this OpMode to the
 * Driver Station OpMode list, or add a @Disabled annotation to prevent this OpMode from being
 * added to the Driver Station.
 */


public class BaseOpMode extends OpMode {
    private Blinker control_Hub;
    private IMU imu;
    private ElapsedTime time;
    private Wheels wheels;
    private Storage storage;
    private Launcher launcher;

    private final boolean log;
    private String logFileDest;
    private int precisionI = 1;
    private double[] precisionValues = {0.4, 0.6, 0.7, 1};
    private int timer = 0;
    private int timerValue = 50;
    private boolean armBlock = false;
    private RobotLogger logger;

    public BaseOpMode(boolean log, String logFileDest) {
        this.log = log;
        this.logFileDest = logFileDest;
    }

    @Override
    public void init() {

        time = new ElapsedTime();

        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.FORWARD, RevHubOrientationOnRobot.UsbFacingDirection.LEFT)));

        wheels = new Wheels(telemetry, time,
                hardwareMap.get(DcMotor.class, "MotorRoueG"),
                hardwareMap.get(DcMotor.class, "MotorRoueD"),
                imu
        );

        storage = new Storage(telemetry, time,
                hardwareMap.get(Servo.class, "servoCollector"),
                hardwareMap.get(DcMotor.class, "MotorDrum"),
                hardwareMap.get(Servo.class, "servoLatch"),
                imu
        );

        launcher = new Launcher(telemetry, time,
                hardwareMap.get(DcMotor.class, "MotorLauncher"),
                imu
        );

        if (log) {
            logger = new RobotLogger(time, new Mechanism[]{wheels, storage, launcher});
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        time.reset();
    }

    @Override
    public void start() {
        time.reset();
        imu.resetYaw();
    }

    @Override
    public void loop() {
        if (timer == 0) {
            if (this.gamepad2.left_bumper) {
                if (precisionI >= 1) {
                    precisionI -= 1;
                    timer = timerValue;
                }
            }
            if (this.gamepad2.right_bumper) {
                if (precisionI <= precisionValues.length -2) {
                    precisionI += 1;
                    timer = timerValue;
                }
            }

            if (this.gamepad2.right_stick_button) {
                armBlock = !armBlock;
                timer = timerValue;
            }
        }

        // moteur deplacement debut
        wheels.gamepadPower(gamepad1, precisionValues[precisionI]);
        storage.gamepadPower(gamepad1, gamepad2);
        launcher.gamepadPower(gamepad1);
        // moteur deplacement fin

        if (timer > 0) {
            timer -= 1;
        }

        /*YawPitchRollAngles orient = imu.getRobotYawPitchRollAngles();
        telemetry.addData("Yaw", orient.getYaw());
        telemetry.addData("Pitch", orient.getPitch());
        telemetry.addData("Roll", orient.getRoll());*/
        telemetry.addData("> ARM BLOCK <", armBlock);
        telemetry.addData("timer", timer);
        telemetry.addData("Precision", precisionValues[precisionI]);
        telemetry.addData("Status", "Running");


        if (log) {
            logger.logCurrentPos(precisionValues[precisionI]);
        }

        wheels.move();
        storage.move();
        launcher.move();
        telemetry.update();
    }

    public void stop() {
        if (log) {
            logger.saveAndStop(logFileDest);
        }
    }
}


