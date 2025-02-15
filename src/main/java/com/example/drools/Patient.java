package com.example.drools;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient {
    private Map<String, Object> properties;
    private String diagnosis;
    private String preliminaryDiagnosis;
    private String riskLevel;
    private String clinicalManifestation;
    private String ecgFinding;
    private List<String> recommendations;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Patient(boolean chestPain, boolean ecgAbnormal, boolean troponinHigh, String diagnosis) {
        this.properties = new HashMap<>();
        this.recommendations = new ArrayList<>();
        this.properties.put("chestPain", chestPain);
        this.properties.put("ecgAbnormal", ecgAbnormal);
        this.properties.put("troponinHigh", troponinHigh);
        this.properties.put("chestPainSet", false);
        this.properties.put("ecgAbnormalSet", false);
        this.properties.put("troponinHighSet", false);
        this.properties.put("bloodPressure", 0.0);
        this.properties.put("glucose", 0.0);
        this.properties.put("lipidProfile", 0.0);
        this.properties.put("smokingBoolean", false);
        this.properties.put("diabetesBoolean", false);
        this.properties.put("heartRate", 0.0);
        this.properties.put("systolicBP", 0.0);
        this.properties.put("saturation", 0.0);
        this.properties.put("bloodPressureSet", false);
        this.properties.put("glucoseSet", false);
        this.properties.put("lipidProfileSet", false);
        this.properties.put("smokingBooleanSet", false);
        this.properties.put("diabetesBooleanSet", false);
        this.properties.put("heartRateSet", false);
        this.properties.put("systolicBPSet", false);
        this.properties.put("saturationSet", false);
        this.diagnosis = diagnosis;
        this.preliminaryDiagnosis = "NotSet";
        this.riskLevel = "NotSet";
        this.clinicalManifestation = "NotSet";
        this.ecgFinding = "NotSet";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, Object value) {
        Object oldValue = properties.get(key);
        properties.put(key, value);
        pcs.firePropertyChange(key, oldValue, value);
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        String oldValue = this.diagnosis;
        this.diagnosis = diagnosis;
        pcs.firePropertyChange("diagnosis", oldValue, diagnosis);
    }

    public String getPreliminaryDiagnosis() {
        return preliminaryDiagnosis;
    }

    public void setPreliminaryDiagnosis(String preliminaryDiagnosis) {
        String oldValue = this.preliminaryDiagnosis;
        this.preliminaryDiagnosis = preliminaryDiagnosis;
        pcs.firePropertyChange("preliminaryDiagnosis", oldValue, preliminaryDiagnosis);
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        String oldValue = this.riskLevel;
        this.riskLevel = riskLevel;
        pcs.firePropertyChange("riskLevel", oldValue, riskLevel);
    }

    public String getClinicalManifestation() {
        return clinicalManifestation;
    }

    public void setClinicalManifestation(String clinicalManifestation) {
        String oldValue = this.clinicalManifestation;
        this.clinicalManifestation = clinicalManifestation;
        pcs.firePropertyChange("clinicalManifestation", oldValue, clinicalManifestation);
    }

    public String getEcgFinding() {
        return ecgFinding;
    }

    public void setEcgFinding(String ecgFinding) {
        String oldValue = this.ecgFinding;
        this.ecgFinding = ecgFinding;
        pcs.firePropertyChange("ecgFinding", oldValue, ecgFinding);
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void addRecommendation(String recommendation) {
        recommendations.add(recommendation);
    }

    public boolean isChestPain() {
        return (boolean) properties.getOrDefault("chestPain", false);
    }

    public void setChestPain(boolean chestPain) {
        setProperty("chestPain", chestPain);
        setProperty("chestPainSet", true);
    }

    public boolean isEcgAbnormal() {
        return (boolean) properties.getOrDefault("ecgAbnormal", false);
    }

    public void setEcgAbnormal(boolean ecgAbnormal) {
        setProperty("ecgAbnormal", ecgAbnormal);
        setProperty("ecgAbnormalSet", true);
    }

    public boolean isTroponinHigh() {
        return (boolean) properties.getOrDefault("troponinHigh", false);
    }

    public void setTroponinHigh(boolean troponinHigh) {
        setProperty("troponinHigh", troponinHigh);
        setProperty("troponinHighSet", true);
    }

    public boolean isChestPainSet() {
        return (boolean) properties.getOrDefault("chestPainSet", false);
    }

    public void setChestPainSet(boolean chestPainSet) {
        setProperty("chestPainSet", chestPainSet);
    }

    public boolean isEcgAbnormalSet() {
        return (boolean) properties.getOrDefault("ecgAbnormalSet", false);
    }

    public void setEcgAbnormalSet(boolean ecgAbnormalSet) {
        setProperty("ecgAbnormalSet", ecgAbnormalSet);
    }

    public boolean isTroponinHighSet() {
        return (boolean) properties.getOrDefault("troponinHighSet", false);
    }

    public void setTroponinHighSet(boolean troponinHighSet) {
        setProperty("troponinHighSet", troponinHighSet);
    }

    public double getBloodPressure() {
        return (double) properties.getOrDefault("bloodPressure", 0.0);
    }

    public void setBloodPressure(double bloodPressure) {
        setProperty("bloodPressure", bloodPressure);
        setProperty("bloodPressureSet", true);
    }

    public double getGlucose() {
        return (double) properties.getOrDefault("glucose", 0.0);
    }

    public void setGlucose(double glucose) {
        setProperty("glucose", glucose);
        setProperty("glucoseSet", true);
    }

    public double getLipidProfile() {
        return (double) properties.getOrDefault("lipidProfile", 0.0);
    }

    public void setLipidProfile(double lipidProfile) {
        setProperty("lipidProfile", lipidProfile);
        setProperty("lipidProfileSet", true);
    }

    public boolean isSmoking() {
        return (boolean) properties.getOrDefault("smokingBoolean", false);
    }

    public void setSmoking(boolean smoking) {
        setProperty("smokingBoolean", smoking);
        setProperty("smokingBooleanSet", true);
    }

    public boolean isDiabetes() {
        return (boolean) properties.getOrDefault("diabetesBoolean", false);
    }

    public void setDiabetes(boolean diabetes) {
        setProperty("diabetesBoolean", diabetes);
        setProperty("diabetesBooleanSet", true);
    }

    public double getHeartRate() {
        return (double) properties.getOrDefault("heartRate", 0.0);
    }

    public void setHeartRate(double heartRate) {
        setProperty("heartRate", heartRate);
        setProperty("heartRateSet", true);
    }

    public double getSystolicBP() {
        return (double) properties.getOrDefault("systolicBP", 0.0);
    }

    public void setSystolicBP(double systolicBP) {
        setProperty("systolicBP", systolicBP);
        setProperty("systolicBPSet", true);
    }

    public double getSaturation() {
        return (double) properties.getOrDefault("saturation", 0.0);
    }

    public void setSaturation(double saturation) {
        setProperty("saturation", saturation);
        setProperty("saturationSet", true);
    }
}