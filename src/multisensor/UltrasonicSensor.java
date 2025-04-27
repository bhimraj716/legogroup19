package multisensor;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

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