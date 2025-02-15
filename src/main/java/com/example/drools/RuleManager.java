package com.example.drools;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;

public class RuleManager {
    private KieServices kieServices;
    private KieContainer kieContainer;
    private KieSession kieSession;
    private List<String> rules;
    private Patient currentPatient;
    private FactHandle patientHandle;

    public RuleManager() {
        kieServices = KieServices.Factory.get();
        rules = new ArrayList<>();
        initializeDefaultRules();
        buildKieSession();
    }

    void initializeDefaultRules() {
        // Stage I: Clinical Manifestations
        rules.add(
            "rule \"Этап I: Асимптомные или малосимптомные\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"chestPain\") == false, $p.getProperty(\"chestPainSet\") == true, clinicalManifestation == \"NotSet\", diagnosis != \"ОКС не исключён, требуется ЭКГ и тропонин\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап I: Асимптомные или малосимптомные\");\n" +
            "        System.out.println(\"Требуется дополнительная диагностика: выполните ЭКГ и анализ тропонина.\");\n" +
            "        modify($p) { setClinicalManifestation(\"Asymptomatic\"), setPreliminaryDiagnosis(\"ОКС не исключён\"), setDiagnosis(\"ОКС не исключён, требуется ЭКГ и тропонин\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап I: Нарастание ангинальной боли\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"chestPain\") == true, $p.getProperty(\"chestPainSet\") == true, clinicalManifestation == \"NotSet\", diagnosis != \"Подозрение на ОКС, требуется ЭКГ\", ($p.getProperty(\"systolicBPSet\") == false || $p.getProperty(\"systolicBP\") >= 90) && ($p.getProperty(\"heartRateSet\") == false || $p.getProperty(\"heartRate\") <= 150))\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап I: Нарастание ангинальной боли или персистирующая боль\");\n" +
            "        System.out.println(\"Требуется ЭКГ в течение 10 минут.\");\n" +
            "        modify($p) { setClinicalManifestation(\"Angina\"), setPreliminaryDiagnosis(\"ОКС без подъема сегмента ST\"), setDiagnosis(\"Подозрение на ОКС, требуется ЭКГ\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап I: Персистирующая боль\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"chestPain\") == true, $p.getProperty(\"chestPainSet\") == true, clinicalManifestation == \"NotSet\", diagnosis != \"Подозрение на ОКС, требуется ЭКГ\", ($p.getProperty(\"systolicBPSet\") == false || $p.getProperty(\"systolicBP\") >= 90) && ($p.getProperty(\"heartRateSet\") == false || $p.getProperty(\"heartRate\") <= 150))\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап I: Персистирующая боль\");\n" +
            "        System.out.println(\"Требуется ЭКГ в течение 10 минут.\");\n" +
            "        modify($p) { setClinicalManifestation(\"PersistentPain\"), setPreliminaryDiagnosis(\"ОКС без подъема сегмента ST\"), setDiagnosis(\"Подозрение на ОКС, требуется ЭКГ\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап I: Кардиогенный шок или острая сердечная недостаточность\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"systolicBPSet\") == true, $p.getProperty(\"systolicBP\") < 90 || $p.getProperty(\"heartRateSet\") == true && $p.getProperty(\"heartRate\") > 110, clinicalManifestation == \"NotSet\", preliminaryDiagnosis != \"ИМ с подъемом сегмента ST\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап I: Кардиогенный шок или острая сердечная недостаточность\");\n" +
            "        System.out.println(\"Требуется немедленная ЭКГ и дальнейшая диагностика.\");\n" +
            "        modify($p) { setClinicalManifestation(\"CardiogenicShock\"), setPreliminaryDiagnosis(\"ИМ с подъемом сегмента ST\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап I: Жизнеопасные аритмии\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"heartRateSet\") == true, $p.getProperty(\"heartRate\") > 150, clinicalManifestation == \"NotSet\", preliminaryDiagnosis != \"ИМ с подъемом сегмента ST\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап I: Жизнеопасные аритмии или остановка сердца\");\n" +
            "        System.out.println(\"Требуется немедленная ЭКГ и дальнейшая диагностика.\");\n" +
            "        modify($p) { setClinicalManifestation(\"Arrhythmias\"), setPreliminaryDiagnosis(\"ИМ с подъемом сегмента ST\") };\n" +
            "end"
        );

        // Stage II: ECG Findings
        rules.add(
            "rule \"Этап II: Нормальная ЭКГ\"\n" +
            "    salience 5\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"ecgAbnormalSet\") == true, $p.getProperty(\"ecgAbnormal\") == false, ecgFinding == \"NotSet\", diagnosis != \"ОКС без подъёма сегмента ST, ожидается тропонин\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап II: Нормальная ЭКГ\");\n" +
            "        System.out.println(\"Требуется анализ тропонина.\");\n" +
            "        modify($p) { setEcgFinding(\"Normal\"), setPreliminaryDiagnosis(\"ОКС без подъема сегмента ST\"), setDiagnosis(\"ОКС без подъёма сегмента ST, ожидается тропонин\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап II: Стойкая элевация сегмента ST\"\n" +
            "    salience 10\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"ecgAbnormalSet\") == true, $p.getProperty(\"ecgAbnormal\") == true, ($p.getProperty(\"heartRateSet\") == false || $p.getProperty(\"heartRate\") <= 150), ecgFinding == \"NotSet\", preliminaryDiagnosis != \"ИМ с подъемом сегмента ST\", clinicalManifestation == \"CardiogenicShock\" || clinicalManifestation == \"Arrhythmias\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап II: Стойкая элевация сегмента ST\");\n" +
            "        System.out.println(\"Подозрение на ИМ с подъемом сегмента ST.\");\n" +
            "        modify($p) { setEcgFinding(\"STElevation\"), setPreliminaryDiagnosis(\"ИМ с подъемом сегмента ST\"), setDiagnosis(\"Предварительный инфаркт миокарда, ожидается тропонин\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап II: Депрессия сегмента ST\"\n" +
            "    salience 12\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"ecgAbnormalSet\") == true, $p.getProperty(\"ecgAbnormal\") == true, ($p.getProperty(\"heartRateSet\") == false || $p.getProperty(\"heartRate\") <= 150), ecgFinding == \"NotSet\", diagnosis != \"Предварительный инфаркт миокарда, ожидается тропонин\", preliminaryDiagnosis != \"ИМ с подъемом сегмента ST\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап II: Депрессия сегмента ST или элевация сегмента ST\");\n" +
            "        System.out.println(\"Требуется анализ тропонина для подтверждения.\");\n" +
            "        modify($p) { setEcgFinding(\"STDepression\"), setPreliminaryDiagnosis(\"ОКС без подъема сегмента ST\"), setDiagnosis(\"Предварительный инфаркт миокарда, ожидается тропонин\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап II: Жизнеопасные аритмии на ЭКГ\"\n" +
            "    salience 15\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"heartRateSet\") == true, $p.getProperty(\"heartRate\") > 150, ecgFinding == \"NotSet\", preliminaryDiagnosis != \"ИМ с подъемом сегмента ST\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап II: Жизнеопасные аритмии на ЭКГ\");\n" +
            "        System.out.println(\"Подозрение на ИМ с подъемом сегмента ST.\");\n" +
            "        modify($p) { setEcgFinding(\"Arrhythmias\"), setPreliminaryDiagnosis(\"ИМ с подъемом сегмента ST\"), setDiagnosis(\"Предварительный инфаркт миокарда, ожидается тропонин\") };\n" +
            "end"
        );

        // Stage IV: Troponin Levels
        rules.add(
            "rule \"Этап IV: Тропонин не повышен\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"troponinHighSet\") == true, $p.getProperty(\"troponinHigh\") == false, diagnosis != \"Низкий риск ОКС\", preliminaryDiagnosis == \"ОКС без подъема сегмента ST\", !recommendations.contains(\"Тропонин не повышен\"))\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап IV: Тропонин не повышен\");\n" +
            "        $p.addRecommendation(\"Тропонин не повышен\");\n" +
            "        modify($p) { setDiagnosis(\"Низкий риск ОКС\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап IV: Тропонин повышен\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"troponinHighSet\") == true, $p.getProperty(\"troponinHigh\") == true, diagnosis != \"Промежуточный риск ОКС\", preliminaryDiagnosis == \"ОКС без подъема сегмента ST\", !recommendations.contains(\"Тропонин повышен, ОКС без подъема сегмента ST\"))\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап IV: Тропонин повышен, ОКС без подъема сегмента ST\");\n" +
            "        $p.addRecommendation(\"Тропонин повышен, ОКС без подъема сегмента ST\");\n" +
            "        modify($p) { setDiagnosis(\"Промежуточный риск ОКС\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Этап IV: Тропонин повышен с ИМпST\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"troponinHighSet\") == true, $p.getProperty(\"troponinHigh\") == true, preliminaryDiagnosis == \"ИМ с подъемом сегмента ST\", diagnosis != \"ИМ с подъемом сегмента ST\", !recommendations.contains(\"Тропонин повышен, ИМ с подъемом сегмента ST\"))\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Этап IV: Тропонин повышен, ИМ с подъемом сегмента ST\");\n" +
            "        $p.addRecommendation(\"Тропонин повышен, ИМ с подъемом сегмента ST\");\n" +
            "        modify($p) { setDiagnosis(\"ИМ с подъемом сегмента ST\") };\n" +
            "end"
        );

        // Risk Stratification for ОКС без подъема сегмента ST
        rules.add(
            "rule \"Risk Stratification: Very High Risk for ОКС без подъема сегмента ST\"\n" +
            "    when\n" +
            "        $p: Patient(preliminaryDiagnosis == \"ОКС без подъема сегмента ST\", ($p.getProperty(\"systolicBPSet\") == true && $p.getProperty(\"systolicBP\") < 90) || ($p.getProperty(\"heartRateSet\") == true && $p.getProperty(\"heartRate\") > 150), riskLevel == \"NotSet\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Очень высокий риск для ОКС без подъема сегмента ST\");\n" +
            "        System.out.println(\"Рекомендуется неотложная СКГ в течение 2 часов.\");\n" +
            "        $p.addRecommendation(\"Неотложная СКГ в течение 2 часов.\");\n" +
            "        modify($p) { setRiskLevel(\"VeryHigh\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Risk Stratification: High Risk for ОКС без подъема сегмента ST\"\n" +
            "    when\n" +
            "        $p: Patient(preliminaryDiagnosis == \"ОКС без подъема сегмента ST\", $p.getProperty(\"troponinHighSet\") == true, $p.getProperty(\"troponinHigh\") == true, riskLevel == \"NotSet\", ($p.getProperty(\"systolicBPSet\") == false || $p.getProperty(\"systolicBP\") >= 90) && ($p.getProperty(\"heartRateSet\") == false || $p.getProperty(\"heartRate\") <= 150))\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Высокий риск для ОКС без подъема сегмента ST\");\n" +
            "        System.out.println(\"Рекомендуется ранняя СКГ в течение 2-24 часов.\");\n" +
            "        $p.addRecommendation(\"Ранняя СКГ в течение 2-24 часов.\");\n" +
            "        modify($p) { setRiskLevel(\"High\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Risk Stratification: Low Risk for ОКС без подъема сегмента ST\"\n" +
            "    when\n" +
            "        $p: Patient(preliminaryDiagnosis == \"ОКС без подъема сегмента ST\", $p.getProperty(\"troponinHighSet\") == true, $p.getProperty(\"troponinHigh\") == false, riskLevel == \"NotSet\", ($p.getProperty(\"systolicBPSet\") == false || $p.getProperty(\"systolicBP\") >= 90) && ($p.getProperty(\"heartRateSet\") == false || $p.getProperty(\"heartRate\") <= 150))\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Невысокий риск для ОКС без подъема сегмента ST\");\n" +
            "        System.out.println(\"Рекомендуется СКГ до выписки или плановая СКГ.\");\n" +
            "        $p.addRecommendation(\"СКГ до выписки или плановая СКГ.\");\n" +
            "        modify($p) { setRiskLevel(\"Low\") };\n" +
            "end"
        );

        rules.add(
            "rule \"Risk Stratification: Very High Risk for ОКС с подъемом сегмента ST\"\n" +
            "    when\n" +
            "        $p: Patient(preliminaryDiagnosis == \"ИМ с подъемом сегмента ST\", riskLevel == \"NotSet\")\n" +
            "    then\n" +
            "        System.out.println(\"Правило сработало: Очень высокий риск для ОКС с подъемом сегмента ST\");\n" +
            "        System.out.println(\"Рекомендуется неотложная СКГ в течение 2 часов.\");\n" +
            "        $p.addRecommendation(\"Неотложная СКГ в течение 2 часов.\");\n" +
            "        modify($p) { setRiskLevel(\"VeryHigh\") };\n" +
            "end"
        );

        // Treatment: Pain Management
        rules.add(
            "rule \"Treatment: Nitroglycerin for Chest Pain\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"chestPainSet\") == true, $p.getProperty(\"chestPain\") == true, $p.getProperty(\"systolicBPSet\") == true, $p.getProperty(\"systolicBP\") >= 90, !recommendations.contains(\"Нитроглицерин 0,4–0,5 мг под язык или в виде аэрозоля.\"))\n" +
            "    then\n" +
            "        System.out.println(\"Рекомендуется: Нитроглицерин 0,4–0,5 мг под язык или в виде аэрозоля (систолическое АД: \" + $p.getSystolicBP() + \" мм рт. ст.).\");\n" +
            "        System.out.println(\"Если симптомы не исчезают через 5 минут, повторить прием. Если боль сохраняется после 2-3 приемов, перейти к введению морфина.\");\n" +
            "        $p.addRecommendation(\"Нитроглицерин 0,4–0,5 мг под язык или в виде аэрозоля.\");\n" +
            "end"
        );

        rules.add(
            "rule \"Treatment: Morphine for Persistent Chest Pain\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"chestPainSet\") == true, $p.getProperty(\"chestPain\") == true, clinicalManifestation == \"PersistentPain\", $p.getProperty(\"systolicBPSet\") == true, $p.getProperty(\"systolicBP\") >= 90, !recommendations.contains(\"Морфина гидрохлорид 2-4 мг в/в медленно, повторять каждые 5–15 мин до купирования боли.\"))\n" +
            "    then\n" +
            "        System.out.println(\"Рекомендуется: Морфина гидрохлорид 2-4 мг в/в медленно, повторять каждые 5–15 мин до купирования боли (систолическое АД: \" + $p.getSystolicBP() + \" мм рт. ст.).\");\n" +
            "        $p.addRecommendation(\"Морфина гидрохлорид 2-4 мг в/в медленно, повторять каждые 5–15 мин до купирования боли.\");\n" +
            "end"
        );

        // Treatment: Hypoxemia Correction
        rules.add(
            "rule \"Treatment: Oxygen Therapy for Hypoxemia\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"saturationSet\") == true, $p.getProperty(\"saturation\") < 90, !recommendations.contains(\"Оксигенотерапия через носовые катетеры 2-8 л/мин.\"))\n" +
            "    then\n" +
            "        System.out.println(\"Рекомендуется: Оксигенотерапия через носовые катетеры 2-8 л/мин (сатурация кислорода: \" + $p.getSaturation() + \"%).\");\n" +
            "        $p.addRecommendation(\"Оксигенотерапия через носовые катетеры 2-8 л/мин.\");\n" +
            "end"
        );

        // Treatment: Invasive Interventions
        rules.add(
            "rule \"Treatment: СКГ for Very High Risk\"\n" +
            "    when\n" +
            "        $p: Patient(riskLevel == \"VeryHigh\", !recommendations.contains(\"Неотложная СКГ в течение 2 часов.\"))\n" +
            "    then\n" +
            "        System.out.println(\"Рекомендуется: Неотложная СКГ в течение 2 часов.\");\n" +
            "        $p.addRecommendation(\"Неотложная СКГ в течение 2 часов.\");\n" +
            "end"
        );

        rules.add(
            "rule \"Treatment: СКГ for High Risk\"\n" +
            "    when\n" +
            "        $p: Patient(riskLevel == \"High\", !recommendations.contains(\"Ранняя СКГ в течение 2-24 часов.\"))\n" +
            "    then\n" +
            "        System.out.println(\"Рекомендуется: Ранняя СКГ в течение 2-24 часов.\");\n" +
            "        $p.addRecommendation(\"Ранняя СКГ в течение 2-24 часов.\");\n" +
            "end"
        );

        rules.add(
            "rule \"Treatment: СКГ for Low Risk\"\n" +
            "    when\n" +
            "        $p: Patient(riskLevel == \"Low\", !recommendations.contains(\"СКГ до выписки или плановая СКГ.\"))\n" +
            "    then\n" +
            "        System.out.println(\"Рекомендуется: СКГ до выписки или плановая СКГ.\");\n" +
            "        $p.addRecommendation(\"СКГ до выписки или плановая СКГ.\");\n" +
            "end"
        );

        // Completion Check
        rules.add(
            "rule \"Проверка завершения диагностики\"\n" +
            "    when\n" +
            "        $p: Patient($p.getProperty(\"chestPainSet\") == true, $p.getProperty(\"ecgAbnormalSet\") == true, $p.getProperty(\"troponinHighSet\") == true, !recommendations.contains(\"Все данные получены. Диагноз подтверждён.\"))\n" +
            "    then\n" +
            "        System.out.println(\"Все данные получены. Диагноз подтверждён: \" + $p.getDiagnosis());\n" +
            "        $p.addRecommendation(\"Все данные получены. Диагноз подтверждён.\");\n" +
            "end"
        );
    }

    void buildKieSession() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        StringBuilder rulesContent = new StringBuilder();
        rulesContent.append("package com.example.drools;\n");
        rulesContent.append("import com.example.drools.Patient;\n\n");

        for (String rule : rules) {
            rulesContent.append(rule).append("\n");
        }

        kieFileSystem.write("src/main/resources/com/example/drools/rules.drl", rulesContent.toString());

        String kmoduleContent =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
            "    <kbase name=\"defaultKieBase\" packages=\"com.example.drools\">\n" +
            "        <ksession name=\"defaultKieSession\"/>\n" +
            "    </kbase>\n" +
            "</kmodule>";
        kieFileSystem.write("META-INF/kmodule.xml", kmoduleContent);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            System.out.println("Ошибки при компиляции правил: " + kieBuilder.getResults().getMessages());
            throw new IllegalStateException("Не удалось скомпилировать правила.");
        } else {
            System.out.println("Правила успешно скомпилированы. Количество правил: " + rules.size());
        }

        KieModule kieModule = kieBuilder.getKieModule();
        kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        if (kieSession != null) {
            kieSession.dispose();
        }
        kieSession = kieContainer.newKieSession("defaultKieSession");

        if (currentPatient != null) {
            patientHandle = kieSession.insert(currentPatient);
        }
    }

    public KieSession getKieSession() {
        return kieSession;
    }

    public void setPatient(Patient patient) {
        this.currentPatient = patient;
        if (kieSession != null) {
            patientHandle = kieSession.insert(patient);
        }
    }

    public void addOrUpdateRule(String ruleName, String newRuleContent) {
        String cleanedRuleContent = newRuleContent;
        if (newRuleContent.contains("package com.example.drools")) {
            cleanedRuleContent = cleanedRuleContent.replace("package com.example.drools;", "").trim();
        }
        if (newRuleContent.contains("import com.example.drools.Patient")) {
            cleanedRuleContent = cleanedRuleContent.replace("import com.example.drools.Patient;", "").trim();
        }

        KieFileSystem tempKieFileSystem = kieServices.newKieFileSystem();
        StringBuilder tempRuleContent = new StringBuilder();
        tempRuleContent.append("package com.example.drools;\n");
        tempRuleContent.append("import com.example.drools.Patient;\n\n");
        tempRuleContent.append(cleanedRuleContent);
        tempKieFileSystem.write("src/main/resources/com/example/drools/temp_rule.drl", tempRuleContent.toString());

        KieBuilder kieBuilder = kieServices.newKieBuilder(tempKieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new IllegalArgumentException("Ошибка в синтаксисе правила: " + kieBuilder.getResults().getMessages());
        }

        rules.removeIf(rule -> rule.contains("rule \"" + ruleName + "\""));
        rules.add(cleanedRuleContent);
        buildKieSession();
    }

    public void clearRules() {
        rules.clear();
    }

    public void dispose() {
        if (kieSession != null) {
            kieSession.dispose();
        }
    }
}