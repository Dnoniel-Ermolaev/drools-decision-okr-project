package com.example.drools;

import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static RuleManager ruleManager;
    private static Patient patient;
    private static List<String> availableProperties = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ruleManager = new RuleManager();

        System.out.println("Система принятия решения по диагностике ОКС");

        availableProperties.add("chestPain");
        availableProperties.add("ecgAbnormal");
        availableProperties.add("troponinHigh");
        availableProperties.add("chestPainSet");
        availableProperties.add("ecgAbnormalSet");
        availableProperties.add("troponinHighSet");
        availableProperties.add("bloodPressure");
        availableProperties.add("glucose");
        availableProperties.add("lipidProfile");
        availableProperties.add("smokingBoolean");
        availableProperties.add("diabetesBoolean");
        availableProperties.add("heartRate");
        availableProperties.add("systolicBP");
        availableProperties.add("saturation");
        availableProperties.add("bloodPressureSet");
        availableProperties.add("glucoseSet");
        availableProperties.add("lipidProfileSet");
        availableProperties.add("smokingBooleanSet");
        availableProperties.add("diabetesBooleanSet");
        availableProperties.add("heartRateSet");
        availableProperties.add("systolicBPSet");
        availableProperties.add("saturationSet");

        patient = new Patient(false, false, false, "Диагноз не установлен");
        ruleManager.setPatient(patient);

        while (true) {
            System.out.println("\nТекущий диагноз: " + patient.getDiagnosis());
            System.out.println("Выберите действие:");
            System.out.println("1. Ввести данные пациента");
            System.out.println("2. Добавить или обновить правило");
            System.out.println("3. Добавить новую характеристику пациенту");
            System.out.println("4. Завершить работу");

            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                inputPatientData(scanner, patient);
            } else if (choice.equals("2")) {
                updateRules(scanner);
            } else if (choice.equals("3")) {
                addPatientProperty(scanner);
            } else if (choice.equals("4")) {
                break;
            } else {
                System.out.println("Неверный ввод. Введите 1, 2, 3 или 4.");
            }
        }

        System.out.println("Завершение работы. Финальный диагноз: " + patient.getDiagnosis());
        ruleManager.dispose();
        scanner.close();
    }

    private static void inputPatientData(Scanner scanner, Patient patient) {
        KieSession kSession = ruleManager.getKieSession();
        while (true) {
            System.out.println("\nТекущий диагноз: " + patient.getDiagnosis());

            if (!(boolean) patient.getProperty("chestPainSet")) {
                patient.setChestPain(getBooleanInput(scanner, "Есть ли боль в груди? (true/false или skip)"));
                kSession.update(kSession.getFactHandle(patient), patient);
                kSession.fireAllRules();
            } else if (!(boolean) patient.getProperty("ecgAbnormalSet")) {
                patient.setEcgAbnormal(getBooleanInput(scanner, "Есть ли отклонения на ЭКГ? (true/false или skip)"));
                kSession.update(kSession.getFactHandle(patient), patient);
                kSession.fireAllRules();
            } else if (!(boolean) patient.getProperty("troponinHighSet")) {
                patient.setTroponinHigh(getBooleanInput(scanner, "Уровень тропонина повышен? (true/false или skip)"));
                kSession.update(kSession.getFactHandle(patient), patient);
                kSession.fireAllRules();
            } else if (!(boolean) patient.getProperty("systolicBPSet")) {
                System.out.print("Введите систолическое АД (мм рт. ст.): ");
                String input = scanner.nextLine().trim();
                try {
                    double value = Double.parseDouble(input);
                    patient.setSystolicBP(value);
                    kSession.update(kSession.getFactHandle(patient), patient);
                    kSession.fireAllRules();
                } catch (NumberFormatException e) {
                    System.out.println("Неверный ввод. Ожидается число.");
                    continue;
                }
            } else if (!(boolean) patient.getProperty("heartRateSet")) {
                System.out.print("Введите частоту сердечных сокращений (уд/мин): ");
                String input = scanner.nextLine().trim();
                try {
                    double value = Double.parseDouble(input);
                    patient.setHeartRate(value);
                    kSession.update(kSession.getFactHandle(patient), patient);
                    kSession.fireAllRules();
                } catch (NumberFormatException e) {
                    System.out.println("Неверный ввод. Ожидается число.");
                    continue;
                }
            } else if (!(boolean) patient.getProperty("saturationSet")) {
                System.out.print("Введите сатурацию кислорода (%): ");
                String input = scanner.nextLine().trim();
                try {
                    double value = Double.parseDouble(input);
                    patient.setSaturation(value);
                    kSession.update(kSession.getFactHandle(patient), patient);
                    kSession.fireAllRules();
                } catch (NumberFormatException e) {
                    System.out.println("Неверный ввод. Ожидается число.");
                    continue;
                }
            } else {
                boolean allPropertiesSet = true;
                for (String prop : availableProperties) {
                    if (prop.endsWith("Set") && !prop.equals("chestPainSet") && !prop.equals("ecgAbnormalSet") && !prop.equals("troponinHighSet") && !prop.equals("systolicBPSet") && !prop.equals("heartRateSet") && !prop.equals("saturationSet")) {
                        String baseProp = prop.substring(0, prop.length() - 3);
                        if (!(boolean) patient.getProperty(prop)) {
                            allPropertiesSet = false;
                            if (baseProp.endsWith("Boolean")) {
                                String displayProp = baseProp.substring(0, baseProp.length() - 7);
                                patient.setProperty(baseProp, getBooleanInput(scanner, "Введите значение для " + displayProp + " (true/false или skip): "));
                            } else {
                                System.out.print("Введите значение для " + baseProp + " (число): ");
                                String input = scanner.nextLine().trim();
                                try {
                                    double value = Double.parseDouble(input);
                                    patient.setProperty(baseProp, value);
                                } catch (NumberFormatException e) {
                                    System.out.println("Неверный ввод. Ожидается число.");
                                    continue;
                                }
                            }
                            patient.setProperty(prop, true);
                            kSession.update(kSession.getFactHandle(patient), patient);
                            kSession.fireAllRules();
                            break;
                        }
                    }
                }

                if (allPropertiesSet) {
                    System.out.println("Все данные получены. Хотите обновить данные? (y/n): ");
                    String choice = scanner.nextLine().trim();
                    if (choice.equalsIgnoreCase("n")) {
                        break;
                    } else if (choice.equalsIgnoreCase("y")) {
                        for (String prop : availableProperties) {
                            if (prop.endsWith("Set")) {
                                patient.setProperty(prop, false);
                            }
                        }
                        patient.setDiagnosis("Диагноз не установлен");
                        patient.setPreliminaryDiagnosis("NotSet");
                        patient.setRiskLevel("NotSet");
                        patient.setClinicalManifestation("NotSet");
                        patient.setEcgFinding("NotSet");
                        patient.getRecommendations().clear(); // Clear recommendations on reset
                        kSession.update(kSession.getFactHandle(patient), patient);
                        kSession.fireAllRules();
                    } else {
                        System.out.println("Неверный ввод. Введите 'y' для обновления или 'n' для выхода.");
                    }
                }
            }
        }
    }

    private static void updateRules(Scanner scanner) {
        System.out.println("Введите название правила:");
        String ruleName = scanner.nextLine().trim();

        System.out.println("Сколько условий должно быть в правиле?");
        int conditionCount = Integer.parseInt(scanner.nextLine().trim());

        StringBuilder conditions = new StringBuilder();
        for (int i = 0; i < conditionCount; i++) {
            System.out.println("Доступные характеристики: " + availableProperties);
            System.out.println("Введите характеристику для условия " + (i + 1) + " (например, chestPain):");
            String property = scanner.nextLine().trim();

            System.out.println("Введите оператор сравнения (==, !=, <, >, <=, >=):");
            String operator = scanner.nextLine().trim();

            System.out.println("Введите значение для сравнения (true/false для булевых или число):");
            String value = scanner.nextLine().trim();

            conditions.append("$p.getProperty(\"").append(property).append("\") ").append(operator).append(" ");
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                conditions.append(value.toLowerCase());
            } else {
                try {
                    Double.parseDouble(value);
                    conditions.append(value);
                } catch (NumberFormatException e) {
                    System.out.println("Неверное значение. Используйте true/false для булевых характеристик или число.");
                    i--;
                    continue;
                }
            }

            if (i < conditionCount - 1) {
                System.out.println("Введите логический оператор для следующего условия (AND или OR):");
                String logicalOp = scanner.nextLine().trim().toUpperCase();
                if (!logicalOp.equals("AND") && !logicalOp.equals("OR")) {
                    System.out.println("Неверный логический оператор. Используйте AND или OR.");
                    i--;
                    continue;
                }
                conditions.append(" ").append(logicalOp.equals("AND") ? "&& " : "|| ");
            }
        }

        System.out.println("Введите диагноз, который нужно установить, если условия выполнены:");
        String diagnosis = scanner.nextLine().trim();

        StringBuilder drlRule = new StringBuilder();
        drlRule.append("rule \"").append(ruleName).append("\"\n");
        drlRule.append("    when\n");
        drlRule.append("        $p: Patient(").append(conditions.toString()).append(", diagnosis != \"").append(diagnosis).append("\")\n");
        drlRule.append("    then\n");
        drlRule.append("        System.out.println(\"Правило сработало: ").append(ruleName).append("\");\n");
        drlRule.append("        modify($p) { setDiagnosis(\"").append(diagnosis).append("\") };\n");
        drlRule.append("end");

        try {
            ruleManager.addOrUpdateRule(ruleName, drlRule.toString());
            System.out.println("Правило успешно добавлено/обновлено.");
            KieSession kSession = ruleManager.getKieSession();
            kSession.update(kSession.getFactHandle(patient), patient);
            kSession.fireAllRules();
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении правила: " + e.getMessage());
        }
    }

    private static void addPatientProperty(Scanner scanner) {
        System.out.println("Введите название новой характеристики (например, bloodPressure):");
        String propertyName = scanner.nextLine().trim();

        System.out.println("Тип характеристики (Boolean или Numeric):");
        String type = scanner.nextLine().trim().toLowerCase();

        if (type.equals("boolean")) {
            propertyName = propertyName + "Boolean";
            patient.setProperty(propertyName, false);
            availableProperties.add(propertyName);
            availableProperties.add(propertyName + "Set");
            patient.setProperty(propertyName + "Set", false);
            System.out.println("Характеристика " + propertyName + " (Boolean) добавлена.");
        } else if (type.equals("numeric")) {
            patient.setProperty(propertyName, 0.0);
            availableProperties.add(propertyName);
            availableProperties.add(propertyName + "Set");
            patient.setProperty(propertyName + "Set", false);
            System.out.println("Характеристика " + propertyName + " (Numeric) добавлена.");
        } else {
            System.out.println("Неверный тип. Используйте Boolean или Numeric.");
        }
    }

    private static boolean getBooleanInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Пустой ввод недопустим. Введите 'true', 'false' или 'skip'.");
                continue;
            }
            if (input.equalsIgnoreCase("skip")) {
                return false;
            } else if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.out.println("Неверный ввод. Введите 'true', 'false' или 'skip'.");
            }
        }
    }
}