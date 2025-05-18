package com.petshop.crmbackend.dto;


public class LabelValue {
    private String label;
    private long   value;
    private double percent;   // 新增

    public LabelValue() {}

    public LabelValue(String label, long value, double percent) {
        this.label   = label;
        this.value   = value;
        this.percent = percent;
    }

    // getters & setters
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public long getValue() { return value; }
    public void setValue(long value) { this.value = value; }

    public double getPercent() { return percent; }
    public void setPercent(double percent) { this.percent = percent; }
}
