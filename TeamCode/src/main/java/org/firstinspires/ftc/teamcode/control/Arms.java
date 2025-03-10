package org.firstinspires.ftc.teamcode.control;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Objects;


public class Arms implements Mechanism {

    private final Telemetry telemetry;
    private final ElapsedTime time;
    private final DcMotor bigArmMotor;
    private final DcMotor littleArmMotor;
    private final Servo grabServo;
    private final Servo collectorServo;
    private final IMU imu;

    public BigArmPosition bigArmPosition = BigArmPosition.OTHER;
    public double bigArmPower = 0;
    public int bigTargetPos = 0;

    public LittleArmPosition littleArmPosition = LittleArmPosition.OTHER;
    public double littleArmPower = 0;
    public int littleTargetPos = 0;

    public GrabPosition grabPosition = GrabPosition.CLOSED;
    public CollectorPosition collectorPosition = CollectorPosition.OPENED;

    public double bigBalance = 0.06;
    public double littleBalance = 0.05;

    public double timer = 0;
    public int    timerValue = 80;

    public double balanceTimer = 0;
    public int    balanceTimerValue = 20;

    private double lastGivenPrecision = 1;

    /**
     * Teste si <code>value</code> est compris dans l'intervalle <code>[wanted-errorRange ; wanted+errorRange]</code>
     * @param value
     * @param wanted
     * @param errorRange
     * @return <code>true</code> si vrai
     * @see #isAlmostExact(double, double, double)
     */
    public static boolean isAlmostExact(int value, int wanted, int errorRange) {
        return (wanted - errorRange <= value && value <= wanted + errorRange);
    }

    /**
     * Teste si <code>value</code> est compris dans l'intervalle <code>[wanted-errorRange ; wanted+errorRange]</code>
     * @param value
     * @param wanted
     * @param errorRange
     * @return <code>true</code> si vrai
     * @see #isAlmostExact(int, int, int)
     */
    public static boolean isAlmostExact(double value, double wanted, double errorRange) {
        return (wanted - errorRange <= value && value <= wanted + errorRange);
    }


    /**
     * Crée un objet pour gérer le bras, implémentant {@link Mechanism}
     * @param telemetry La télémétrie utilisée dans l'OpMode, pour y ajouter des informations
     * @param time L'{@link ElapsedTime} de l'OpMode
     * @param bigArmMotor Le {@link DcMotor} du grand bras
     * @param littleArmMotor Le {@link DcMotor} du petit bras
     * @param grabServo Le {@link Servo} de la pince
     * @param collectorServo Le {@link Servo} du tourniquet
     * @param imu L'{@link IMU} du robot, dans l'OpMode
     */
    public Arms(Telemetry telemetry, ElapsedTime time, DcMotor bigArmMotor, DcMotor littleArmMotor, Servo grabServo, Servo collectorServo, IMU imu) {
        this.telemetry = telemetry;
        this.time = time;
        this.bigArmMotor = bigArmMotor;
        this.littleArmMotor = littleArmMotor;
        this.grabServo = grabServo;
        this.collectorServo = collectorServo;
        this.imu = imu;

        //bigArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //littleArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        littleArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        littleArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetEncoders() {
        bigArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        littleArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void motorsPower(Gamepad gamepad, boolean armBlock) {
        if (!armBlock) {
            double right_stick_y = -gamepad.right_stick_y;
            if (!isAlmostExact(right_stick_y, 0, 0.1)) {
                bigArmPower = right_stick_y;

                bigArmPower *= lastGivenPrecision;

                bigArmPosition = BigArmPosition.OTHER;
            } else {
                bigArmPower = 0;
            }

            bigArmPower += bigBalance;


        }

        littleArmPower = 0;
        if (gamepad.dpad_down){
            littleArmPower = 0.5;
            littleArmPosition = LittleArmPosition.OTHER;
        } else if (gamepad.dpad_up) {
            littleArmPower = -0.4;
            littleArmPosition = LittleArmPosition.OTHER;
        }
        littleArmPower += littleBalance;
    }

    private void servosPower(Gamepad gamepad) {
        if (timer == 0 && (gamepad.circle || gamepad.cross || gamepad.square || gamepad.triangle)) {
            if (collectorPosition == CollectorPosition.CLOSED) {
                collectorPosition = CollectorPosition.OPENED;
            } else {
                collectorPosition = CollectorPosition.CLOSED;
            }
            timer = timerValue;
        }

        if (gamepad.right_trigger<=0.4) {
            grabPosition = GrabPosition.CLOSED;
        } else {
            grabPosition = GrabPosition.OPENED;
        }
    }

    /**
     * Vérifie les combinaisons de touches du copilote pour les mouvements préenregistrés
     * puis enregistre les valeurs qui seront appliquées dans {@link #move()}
     * @param gamepad La manette du copilote (gamepad2 dans l'OpMode)
     * @see #move()
     * @see #gamepadPower(Gamepad, Gamepad, double, boolean)
     */
    private void checkPresets(Gamepad gamepad) {
        if (gamepad.right_trigger >= 0.4 && bigArmPosition == BigArmPosition.OTHER) { // Big arm presets
            if (gamepad.cross) {
                bigArmPosition = BigArmPosition.REST;
            } else if (gamepad.circle) {
                bigArmPosition = BigArmPosition.OBSERVABLE_ZONE;
            } else if (gamepad.square) {
                bigArmPosition = BigArmPosition.LOW_BASKET;
            } else if (gamepad.triangle) {
                bigArmPosition = BigArmPosition.HIGH_CHAMBER;
            }
        } /*else if (gamepad.left_trigger >= 0.4 && littleArmPosition == LittleArmPosition.OTHER) { // Little arm presets
            if (gamepad.cross) {                                                                    // X Désactivés car trop de problèmes
                littleArmPosition = LittleArmPosition.REST;
            } else if (gamepad.circle) {
                littleArmPosition = LittleArmPosition.FLOOR;
            } else if (gamepad.square) {
                littleArmPosition = LittleArmPosition.SUBMERSIBLE;
            } else if (gamepad.triangle) {
                littleArmPosition = LittleArmPosition.LOW_BASKET;
            } else if (gamepad.options) {
                littleArmPosition = LittleArmPosition.HIGH_BASKET;
            }
        }*/
    }

    /**
     * Vérifie les actions performées par le pilote et copilote sur les manettes
     * et enregistre les valeurs qui seront appliquées dans {@link #move()}
     * @param gamepad La manette du pilote
     * @param gamepad2 La manette du copilote
     * @param precision Une valeur qui sera multipliée aux puissances des moteurs pour augmenter ou diminuer la vitesse
     * @param armBlock Si le bras est actuellement vérouillé
     * @see #move()
     * @see #motorsPower(Gamepad, boolean)
     * @see #checkPresets(Gamepad)
     * @see #servosPower(Gamepad)
     */
    public void gamepadPower(Gamepad gamepad, Gamepad gamepad2, double precision, boolean armBlock) {

        lastGivenPrecision = precision;

        if (balanceTimer == 0 && gamepad2.dpad_down) {
            bigBalance = -bigBalance;
            balanceTimer = balanceTimerValue;
        }
        if (balanceTimer == 0 && gamepad2.dpad_up) {
            littleBalance = -littleBalance;
            balanceTimer = balanceTimerValue;
        }

        motorsPower(gamepad, armBlock);

        checkPresets(gamepad2);

        servosPower(gamepad);

        if (timer>0) {
            timer -= 1;
        }
        if (balanceTimer > 0) {
            balanceTimer -= 1;
        }
    }

    public void move() {
        if (bigArmPosition == BigArmPosition.OTHER && (littleArmPosition == LittleArmPosition.OTHER || littleArmPosition == LittleArmPosition.REST)) {
            if (bigArmMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                bigArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            bigArmMotor.setPower(bigArmPower);
        } else {
            if (isAlmostExact(bigArmMotor.getCurrentPosition(), bigArmPosition.position, 5)) {
                bigArmPosition = BigArmPosition.OTHER;
            } else {
                bigArmMotor.setTargetPosition(bigArmPosition.position);
                if (bigArmMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                    bigArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                bigArmMotor.setPower(lastGivenPrecision);
            }
        }

        if (littleArmPosition == LittleArmPosition.OTHER) {
            if (littleArmMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                littleArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            littleArmMotor.setPower(littleArmPower);
        } else {
            if (isAlmostExact(bigArmMotor.getCurrentPosition(), littleArmPosition.bigPosition, 5)) {
                bigArmPosition = BigArmPosition.OTHER;
            } else {
                bigArmMotor.setTargetPosition(littleArmPosition.bigPosition);
                if (bigArmMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                    bigArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                bigArmMotor.setPower(lastGivenPrecision);
            }

            if (isAlmostExact(littleArmMotor.getCurrentPosition(), littleArmPosition.littlePosition, 2)
                    && bigArmPosition == BigArmPosition.OTHER) {
                littleArmPosition = LittleArmPosition.OTHER;
            } else {
                littleArmMotor.setTargetPosition(littleArmPosition.littlePosition);
                if (littleArmMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                    littleArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                littleArmMotor.setPower(0.3);
            }
        }

        grabServo.setPosition(grabPosition.position);
        collectorServo.setPosition(collectorPosition.position);

        telemetry.addLine("### Mouvements bras ###");
        telemetry.addData("Big balance", bigBalance);
        telemetry.addData("Big position", bigArmPosition.name() + ": " + bigArmMotor.getCurrentPosition());
        telemetry.addData("Big arm power", bigArmPower);

        telemetry.addData("Little balance", littleBalance);
        telemetry.addData("Little position", littleArmPosition.name() + ": " +littleArmMotor.getCurrentPosition());
        telemetry.addData("Little Arm power", littleArmPower);

        telemetry.addData("Grab position", grabPosition);
        telemetry.addData("Collector position", collectorPosition);

    }

    public HashMap<String, Object> getData() {
        return new HashMap<String, Object>() {{
            put("bigArmPosition", bigArmPosition.name());
            put("bigArmPower", bigArmPower);
            put("littleArmPosition", littleArmPosition.name());
            put("littleArmPower", littleArmPower);
            put("grabPosition", grabPosition);
            put("collectorPosition", collectorPosition);
        }};
    }

    public void setData(HashMap<String, String> data) {
        bigArmPosition = BigArmPosition.valueOf(Objects.requireNonNull(data.get("bigArmPosition")));
        bigArmPower = Double.parseDouble(Objects.requireNonNull(data.get("bigArmPower")));
        littleArmPosition = LittleArmPosition.valueOf(Objects.requireNonNull(data.get("littleArmPosition")));
        littleArmPower = Double.parseDouble(Objects.requireNonNull(data.get("littleArmPower")));
        grabPosition = GrabPosition.valueOf(Objects.requireNonNull(data.get("grabPosition")));
        collectorPosition = CollectorPosition.valueOf(Objects.requireNonNull(data.get("collectorPosition")));
    }


    // TODO Take the right values
    public enum BigArmPosition {
        REST(0),
        OBSERVABLE_ZONE(1535), //
        LOW_BASKET(2300), //
        HIGH_CHAMBER(2500), //
        OTHER(0);

        public final int position;
        BigArmPosition(int position) {
            this.position = position;
        }
    }

    // TODO Take the right values
    public enum LittleArmPosition {
        REST(0,0),
        FLOOR(-150,0),
        SUBMERSIBLE(-180,371),
        LOW_BASKET(-151,1188), //
        HIGH_BASKET(-315,3086), //
        OTHER(0,0);

        public final int littlePosition;
        public final int bigPosition;
        LittleArmPosition(int littlePosition, int bigPosition) {
            this.littlePosition = littlePosition;
            this.bigPosition = bigPosition;
        }
    }

    public enum GrabPosition {
        CLOSED(0.55),
        OPENED(0.35);

        public final double position;
        GrabPosition(double position) {
            this.position = position;
        }
    }

    public enum CollectorPosition {
        CLOSED(1),
        OPENED(0);

        public final int position;
        CollectorPosition(int position) {
            this.position = position;
        }
    }

}