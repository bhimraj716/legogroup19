package multisensor;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class MultiSensorRobot {
    static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);

    public static final Object motorLock = new Object();
    public static final Object sensorLock = new Object();

    static volatile boolean avoidObstacle = false;

    public static void main(String[] args) {
        LightSensor.calibrate(); // Calibrate the light sensor

        LCD.clear();
        LCD.drawString("Press to Start", 0, 1);
        Button.waitForAnyPress();
        LCD.clear();

        Thread lineFollower = new Thread(new LineFollower()); // Start LineFollower thread
        lineFollower.start();
        UltrasonicSensor.startAvoidance();

        while (!Button.ESCAPE.isDown()) { // Main loop running until ESCAPE button is pressed
            Delay.msDelay(100); // Wait for a short time
        }
        // Stop the motors safely
        synchronized (motorLock) {
            leftMotor.stop();
            rightMotor.stop();
        }

        // Shutdown sensors and motors
        LightSensor.shutdown();
        UltrasonicSensor.close();
        leftMotor.close();
        rightMotor.close();

        // Display stopped message
        LCD.clear();
        LCD.drawString("Stopped", 0, 0);

    }
    private float measureSide(boolean leftTurn) {
        synchronized (MultiSensorRobot.motorLock) {
            if (leftTurn) {
                // Turn left smoothly (slower speed for smoother turns)
                MultiSensorRobot.leftMotor.setSpeed(100);
                MultiSensorRobot.rightMotor.setSpeed(150);
                MultiSensorRobot.leftMotor.backward();
                MultiSensorRobot.rightMotor.forward();
            } else {
                // Turn right smoothly (slower speed for smoother turns)
                MultiSensorRobot.leftMotor.setSpeed(150);
                MultiSensorRobot.rightMotor.setSpeed(100);
                MultiSensorRobot.leftMotor.forward();
                MultiSensorRobot.rightMotor.backward();
            }
            Delay.msDelay(300);

            MultiSensorRobot.leftMotor.flt(true);
            MultiSensorRobot.rightMotor.flt();
            Delay.msDelay(100);
        }
        return getDistance();
    }
}