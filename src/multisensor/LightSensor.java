package multisensor;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class LightSensor {
    private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
    private static SensorMode lightMode = colorSensor.getRedMode();
    private static float[] sample = new float[lightMode.sampleSize()];
    private static float lightThreshold = 0.35f;
    public static void calibrate() {
        LCD.clear();
        LCD.drawString("Place on BLACK", 0, 0);
        Button.waitForAnyPress();
        lightMode.fetchSample(sample, 0);
        float black = sample[0];

        LCD.clear();
        LCD.drawString("Place on WHITE", 0, 0);
        Button.waitForAnyPress();
        lightMode.fetchSample(sample, 0);
        float white = sample[0];

        lightThreshold = (black + white) / 2.0f;

        LCD.clear();
        LCD.drawString("Calibrated!", 0, 0);
        LCD.drawString("Threshold: " + lightThreshold, 0, 1);
        Delay.msDelay(1500);
    }

    public static float getLightValue() {
        synchronized (MultiSensorRobot.sensorLock) {
            lightMode.fetchSample(sample, 0);
            return sample[0];
        }
    }

    public static float getThreshold() {
        return lightThreshold;
    }
}
