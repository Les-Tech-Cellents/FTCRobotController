package org.firstinspires.ftc.teamcode.control;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Objects;


public class Storage implements Mechanism {

    private final Telemetry telemetry;
    private final ElapsedTime time;
    private final Servo collectorServo;
    private final Servo drumServo;
    private final Servo latchServo;
    private final IMU imu;

    public CollectorPosition collectorPosition = CollectorPosition.OPENED;
    public DrumPosition drumPosition = DrumPosition.FIRST_COLLECT;
    public LatchPosition latchPosition = LatchPosition.CLOSED;

    public int drumPosIndex = 0;
    public boolean drumCollect = true;
    public DrumPosition[] drumCollectPositions = {DrumPosition.FIRST_COLLECT, DrumPosition.SECOND_COLLECT, DrumPosition.THIRD_COLLECT};
    public DrumPosition[] drumLaunchPositions = {DrumPosition.FIRST_LAUNCH, DrumPosition.SECOND_LAUNCH, DrumPosition.THIRD_LAUNCH};

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
    public Storage(Telemetry telemetry, ElapsedTime time, Servo collectorServo, Servo drumServo, Servo latchServo, IMU imu) {
        this.telemetry = telemetry;
        this.time = time;
        this.collectorServo = collectorServo;
        this.drumServo = drumServo;
        this.latchServo = latchServo;
        this.imu = imu;
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

        if (gamepad.left_trigger <= 0.4)
            latchPosition = LatchPosition.OPENED;
        else
            latchPosition = LatchPosition.CLOSED;

        if (gamepad.left_bumper) {
            if (drumPosIndex == 0)
                drumPosIndex = 2;
            else
                drumPosIndex--;
        } else if (gamepad.right_bumper) {
            if (drumPosIndex == 2)
                drumPosIndex = 0;
            else
                drumPosIndex++;
        }

        if (gamepad.dpad_up)
            drumCollect = false;
        else if (gamepad.dpad_down)
            drumCollect = true;

        if (drumCollect)
            drumPosition = drumCollectPositions[drumPosIndex];
        else
            drumPosition = drumLaunchPositions[drumPosIndex];
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
    public void gamepadPower(Gamepad gamepad, Gamepad gamepad2) {

        servosPower(gamepad);

        if (timer>0) {
            timer -= 1;
        }
        if (balanceTimer > 0) {
            balanceTimer -= 1;
        }
    }

    public void move() {
        collectorServo.setPosition(collectorPosition.position);
        drumServo.setPosition(drumPosition.position);
        latchServo.setPosition(latchPosition.position);

        telemetry.addLine("### Mouvements stockage ###");
        telemetry.addData("Collector position", collectorPosition);
        telemetry.addData("Drum position", drumPosition);
        telemetry.addData("Latch position", latchPosition);

    }

    public HashMap<String, Object> getData() {
        return new HashMap<String, Object>() {{
            put("collectorPosition", collectorPosition);
            put("drumPosition", drumPosition);
            put("latchPosition", latchPosition);
        }};
    }

    public void setData(HashMap<String, String> data) {
        collectorPosition = CollectorPosition.valueOf(Objects.requireNonNull(data.get("collectorPosition")));
        drumPosition = DrumPosition.valueOf(Objects.requireNonNull(data.get("drumPosition")));
        latchPosition = LatchPosition.valueOf(Objects.requireNonNull(data.get("latchPosition")));
    }

    public enum CollectorPosition {
        CLOSED(1),
        OPENED(0);

        public final int position;
        CollectorPosition(int position) {
            this.position = position;
        }
    }

    public enum DrumPosition {
        FIRST_COLLECT(1),
        SECOND_COLLECT(0.5),
        THIRD_COLLECT(0),
        FIRST_LAUNCH(1),
        SECOND_LAUNCH(0.5),
        THIRD_LAUNCH(0);

        public final double position;
        DrumPosition(double position) {
            this.position = position;
        }
    }

    public enum LatchPosition {
        OPENED(0),
        CLOSED(1);

        public final int position;
        LatchPosition(int position) {
            this.position = position;
        }
    }

}