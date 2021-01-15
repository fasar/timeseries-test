package test.Math;

import java.util.stream.DoubleStream;

public class MathManager {
    static double PI2 = 2 * Math.PI;

    /**
     * Create a sinusoidal signal with all these parameters.
     *
     * @param timeStep   how to add for each step in seconds
     * @param amplitude  signal amplitude
     * @param offset     offset of the signal
     * @param period     period of the signal in second
     * @param deph       dephasage of the signal
     * @return
     */
    public static DoubleStream sinFunction(double timeStep, double amplitude, double offset, double period, double deph) {
        double frequency = 1 / period;
        return DoubleStream
            .iterate(0, f -> f + timeStep)
            .map(i -> {
                return amplitude * Math.sin(i * PI2 * frequency + deph) + offset;
            });
    }

}
