package multisensor;

import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class UltrasonicSensor {
    private static EV3UltrasonicSensor ultrasonic = new EV3UltrasonicSensor(SensorPort.S2);
    private static SampleProvider distanceProvider = ultrasonic.getDistanceMode();
    private static float[] sample = new float[distanceProvider.sampleSize()];

    public static float getDistance() {
        distanceProvider.fetchSample(sample, 0);
        return sample[0];
    }

    public static void close() {
        ultrasonic.close();
    }
}

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
        distanceProvider.fetchSample(sample, 0);
        return sample[0];
    }

    public static void close() {
        ultrasonic.close();
    }

    @Override
    public void run() {
        while (!Thread.interrupted() && !Button.ESCAPE.isDown()) {
            float distance = getDistance();
            Delay.msDelay(100);
        }
    }
}