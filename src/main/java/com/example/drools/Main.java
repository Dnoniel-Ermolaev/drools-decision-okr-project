package com.example.drools;

import java.util.Scanner;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Система принятия решения по диагностике ОКС");
        System.out.println("Введите данные пациента:");

        System.out.print("Есть ли боль в груди? (true/false): ");
        boolean chestPain = Boolean.parseBoolean(scanner.nextLine());

        System.out.print("Есть ли отклонения на ЭКГ? (true/false): ");
        boolean ecgAbnormal = Boolean.parseBoolean(scanner.nextLine());

        System.out.print("Уровень тропонина повышен? (true/false): ");
        boolean troponinHigh = Boolean.parseBoolean(scanner.nextLine());

        // Создаем объект пациента на основе введенных данных
        Patient patient = new Patient(chestPain, ecgAbnormal, troponinHigh);

        // Инициализируем Drools
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        KieSession kSession = kieContainer.newKieSession();

        // Вставляем данные пациента в сессию
        kSession.insert(patient);

        // Запускаем правила
        int fired = kSession.fireAllRules();
        System.out.println("Количество сработавших правил: " + fired);

        // Вывод результата диагностики
        System.out.println("Результат диагностики: " + patient.getDiagnosis());

        kSession.dispose();
        scanner.close();
    }
}
