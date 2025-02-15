package com.example.drools;

public class Patient {
    private boolean chestPain;    // Наличие боли в груди
    private boolean ecgAbnormal;  // Наличие отклонений на ЭКГ
    private boolean troponinHigh; // Повышенный уровень тропонина
    private String diagnosis;     // Результирующий диагноз

    public Patient(boolean chestPain, boolean ecgAbnormal, boolean troponinHigh) {
        this.chestPain = chestPain;
        this.ecgAbnormal = ecgAbnormal;
        this.troponinHigh = troponinHigh;
    }

    public boolean isChestPain() {
        return chestPain;
    }

    public void setChestPain(boolean chestPain) {
        this.chestPain = chestPain;
    }

    public boolean isEcgAbnormal() {
        return ecgAbnormal;
    }

    public void setEcgAbnormal(boolean ecgAbnormal) {
        this.ecgAbnormal = ecgAbnormal;
    }

    public boolean isTroponinHigh() {
        return troponinHigh;
    }

    public void setTroponinHigh(boolean troponinHigh) {
        this.troponinHigh = troponinHigh;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
