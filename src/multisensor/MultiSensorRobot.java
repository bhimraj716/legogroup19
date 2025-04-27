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
    }

}

public static void main(String[] args) {
    LightSensor.calibrate();  // Calibrate the light sensor

    LCD.clear();
    LCD.drawString("Press to Start", 0, 1);
    Button.waitForAnyPress();
    LCD.clear();

    Thread lineFollower = new Thread(new LineFollower());  // Start LineFollower thread
    lineFollower.start();


   
}
