package com.example.drools;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

class DroolsTest {

    private RuleManager ruleManager;

    @BeforeEach
    void setUp() {
        ruleManager = new RuleManager();
        ruleManager.clearRules();
        ruleManager.initializeDefaultRules();
        ruleManager.buildKieSession();
    }

    @Test
    void testHighRiskOCS() {
        KieSession kSession = ruleManager.getKieSession();
        Patient patient = new Patient(false, false, false, "Диагноз не установлен");
        ruleManager.setPatient(patient);
        FactHandle patientHandle = kSession.getFactHandle(patient);

        patient.setChestPain(true);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("Подозрение на ОКС, требуется ЭКГ", patient.getDiagnosis(),
                "Ожидался диагноз после Этапа I");

        patient.setEcgAbnormal(true);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("Предварительный инфаркт миокарда, ожидается тропонин", patient.getDiagnosis(),
                "Ожидался диагноз после Этапа II");

        patient.setTroponinHigh(true);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("Промежуточный риск ОКС", patient.getDiagnosis(),
                "Ожидался финальный диагноз");

        kSession.dispose();
    }

    @Test
    void testLowRiskOCS() {
        KieSession kSession = ruleManager.getKieSession();
        Patient patient = new Patient(false, false, false, "Диагноз не установлен");
        ruleManager.setPatient(patient);
        FactHandle patientHandle = kSession.getFactHandle(patient);

        patient.setChestPain(true);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("Подозрение на ОКС, требуется ЭКГ", patient.getDiagnosis(),
                "Ожидался диагноз после Этапа I");

        patient.setEcgAbnormal(false);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("ОКС без подъёма сегмента ST, ожидается тропонин", patient.getDiagnosis(),
                "Ожидался диагноз после Этапа II");

        patient.setTroponinHigh(false);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("Низкий риск ОКС", patient.getDiagnosis(),
                "Ожидался финальный диагноз");

        kSession.dispose();
    }

    @Test
    void testIntermediateRiskOCS() {
        KieSession kSession = ruleManager.getKieSession();
        Patient patient = new Patient(false, false, false, "Диагноз не установлен");
        ruleManager.setPatient(patient);
        FactHandle patientHandle = kSession.getFactHandle(patient);

        patient.setChestPain(true);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("Подозрение на ОКС, требуется ЭКГ", patient.getDiagnosis(),
                "Ожидался диагноз после Этапа I");

        patient.setEcgAbnormal(false);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("ОКС без подъёма сегмента ST, ожидается тропонин", patient.getDiagnosis(),
                "Ожидался диагноз после Этапа II");

        patient.setTroponinHigh(true);
        kSession.update(patientHandle, patient);
        kSession.fireAllRules();
        assertEquals("Промежуточный риск ОКС", patient.getDiagnosis(),
                "Ожидался финальный диагноз");

        kSession.dispose();
    }
}