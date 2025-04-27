package multisensor;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class LineFollower implements Runnable {
    public void run() {
        int normalSpeed = 200;
        int maxMotorSpeed = 900;
        int minMotorSpeed = 0;
        float proportionalGain = 700;

        while (!Button.ESCAPE.isDown()) {
            if (MultiSensorRobot.avoidObstacle) {
                Delay.msDelay(100);
                continue;
            }
            float currentLight = LightSensor.getLightValue();
            float deviation = currentLight - LightSensor.getThreshold();
            int adjust = (int) (proportionalGain * deviation);

            int leftSpeed = normalSpeed - adjust;
            int rightSpeed = normalSpeed + adjust;

            leftSpeed = Math.max(minMotorSpeed, Math.min(maxMotorSpeed, leftSpeed));
            rightSpeed = Math.max(minMotorSpeed, Math.min(maxMotorSpeed, rightSpeed));

            synchronized (MultiSensorRobot.motorLock) {
                MultiSensorRobot.leftMotor.setSpeed(Math.abs(leftSpeed));
                MultiSensorRobot.rightMotor.setSpeed(Math.abs(rightSpeed));

                if (leftSpeed > 0)
                    MultiSensorRobot.leftMotor.forward();
                else
                    MultiSensorRobot.leftMotor.backward();

                if (rightSpeed > 0)
                    MultiSensorRobot.rightMotor.forward();
                else
                    MultiSensorRobot.rightMotor.backward();
            }

            // Display motor speeds and deviation
            LCD.clear(1);
            LCD.drawString("L: " + leftSpeed + " R: " + rightSpeed, 0, 1);
            LCD.drawString("Dev: " + deviation, 0, 2);

            Delay.msDelay(10); // Wait before checking again
        }
    }

}
