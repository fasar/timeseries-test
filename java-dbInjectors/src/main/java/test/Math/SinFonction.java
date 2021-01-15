package test.Math;


public class SinFonction {
    static double PI2 = 2 * Math.PI;

    private double timeStep;
    private final double amplitude;
    private final double offset;
    private final double period;
    private final double deph;

    public SinFonction(double timeStep, double amplitude, double offset, double period, double deph) {
        this.timeStep = timeStep;
        this.amplitude = amplitude;
        this.offset = offset;
        this.period = period;
        this.deph = deph;
    }

    public double sin(double ts) {
        return amplitude * Math.sin(ts * PI2 / period + deph) + offset;
    }

}
