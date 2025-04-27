package multisensor;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class UltrasonicSensor implements Runnable {
    private static EV3UltrasonicSensor ultrasonic = new EV3UltrasonicSensor(SensorPort.S2);
    private static SampleProvider distanceProvider = ultrasonic.getDistanceMode();
    private static float[] sample = new float[distanceProvider.sampleSize()];
    private static Thread avoidanceThread;

    public static void startAvoidance() {
        avoidanceThread = new Thread(new UltrasonicSensor());
        avoidanceThread.start();
    }

    public static float getDistance() {
        synchronized (MultiSensorRobot.sensorLock) {
            distanceProvider.fetchSample(sample, 0);
            return sample[0];
        }
    }

    public static void close() {
        ultrasonic.close();
    }

    @Override
    public void run() {
        while (!Thread.interrupted() && !Button.ESCAPE.isDown()) {
            float distance = getDistance();

            // Check for obstacle
            if (distance > 0.02 && distance < 0.15 && !MultiSensorRobot.avoidObstacle) {
                MultiSensorRobot.avoidObstacle = true;

                synchronized (MultiSensorRobot.motorLock) {
                    // Stop motors temporarily with smooth deceleration
                    MultiSensorRobot.leftMotor.setSpeed(100);
                    MultiSensorRobot.rightMotor.setSpeed(100);
                    MultiSensorRobot.leftMotor.forward();
                    MultiSensorRobot.rightMotor.forward();
                    Delay.msDelay(200);

                    MultiSensorRobot.leftMotor.flt(true);
                    MultiSensorRobot.rightMotor.flt();
                    Delay.msDelay(100);

                    LCD.clear(2);
                    LCD.drawString("Obstacle!", 0, 2);

                    // Measure distances to the left and right
                    float leftDist = measureSide(true); // Measure distance to the left
                    float rightDist = measureSide(false); // Measure distance to the right

                    // Decide which direction to turn based on obstacle distance
                    if (rightDist < leftDist) {
                        MultiSensorRobot.leftMotor.setSpeed(150);
                        MultiSensorRobot.rightMotor.setSpeed(250); // Avoid large speed difference
                    } else {
                        MultiSensorRobot.leftMotor.setSpeed(250);
                        MultiSensorRobot.rightMotor.setSpeed(150); // Avoid large speed difference
                    }

                    // Smoothly turn around the obstacle
                    MultiSensorRobot.leftMotor.forward();
                    MultiSensorRobot.rightMotor.forward();
                    Delay.msDelay(800); // Slightly reduced time for smoother turn

                    MultiSensorRobot.leftMotor.flt(true);
                    MultiSensorRobot.rightMotor.flt();
                    Delay.msDelay(200);

                    // Gradually increase speed back to normal
                    MultiSensorRobot.leftMotor.setSpeed(200); // Smoother speed adjustment
                    MultiSensorRobot.rightMotor.setSpeed(200); // Smoother speed adjustment
                    MultiSensorRobot.leftMotor.forward();
                    MultiSensorRobot.rightMotor.forward();

                    // Re-enable line following after obstacle avoidance
                    MultiSensorRobot.avoidObstacle = false;
                }
            }

            Delay.msDelay(100);
        }
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