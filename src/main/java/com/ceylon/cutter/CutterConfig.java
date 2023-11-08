package com.ceylon.cutter;

public class CutterConfig {
    private static final CutterConfig instance = new CutterConfig();

    public static CutterConfig getInstance() {
        return CutterConfig.instance;
    }

    private int initial;
    private int minimum;
    private int maximum;
    private int step;

    private CutterConfig() { }

    public int getInitial() {
        return this.initial;
    }

    public void setInitial(int initial) {
        this.initial = initial;
    }

    public int getMinimum() {
        return this.minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return this.maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getStep() {
        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
