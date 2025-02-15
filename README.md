## 🏥 Drools Decision OCS Project

### 📌 Описание  
Этот проект представляет собой **систему поддержки принятия решений (СППР)** на основе **Drools** для диагностики **Острого Коронарного Синдрома (ОКС)**. Приложение работает в консоли, запрашивает у пользователя данные о состоянии пациента и на основе правил делает предварительное заключение о степени риска.

---

### 🛠️ Требования  
Перед запуском убедитесь, что у вас установлены:  
- **Java** (JDK 11 или 17)  
- **Apache Maven** (3.x)  

Проверить установку:  
```bash
java -version
mvn -version
```
Если JDK или Maven отсутствуют, установите их:
```bash
sudo apt update
sudo apt install openjdk-17-jdk maven
```

---

### 🚀 Установка и запуск  

1️⃣ **Клонировать репозиторий:**  
```bash
git clone https://github.com/Dnoniel-Ermolaev/drools-decision-okr-project.git
cd drools-decision-okr-project
```

2️⃣ **Собрать проект:**  
```bash
mvn clean install
```

3️⃣ **Запустить приложение:**  
```bash
mvn exec:java
```

После запуска приложение задаст вопросы о симптомах пациента. Введите `true` или `false` и нажмите Enter. Пример работы:
```
Система принятия решения по диагностике ОКС
Введите данные пациента:
Есть ли боль в груди? (true/false): true
Есть ли отклонения на ЭКГ? (true/false): true
Уровень тропонина повышен? (true/false): true

Диагноз: Высокий риск ОКС. Рекомендуется неотложное вмешательство.
Количество сработавших правил: 1
```

---

### 🧪 Тестирование

Для автоматизированного тестирования проекта используются тесты на **JUnit 5**.  
Тесты размещены в каталоге:
```
src/test/java/com/example/drools/
```
Файл тестового класса, например, называется **DroolsTest.java**. В нём описаны тестовые сценарии, проверяющие работу правил Drools. Пример тестового класса:

```java
package com.example.drools;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

class DroolsTest {

    private KieSession getKieSession() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        return kieContainer.newKieSession();
    }

    @Test
    void testHighRiskOCS() {
        KieSession kSession = getKieSession();
        Patient patient = new Patient(true, true, true);
        kSession.insert(patient);
        kSession.fireAllRules();
        kSession.dispose();

        // Ожидается, что правило "Высокий риск ОКС" установит диагноз
        assertEquals("Высокий риск ОКС", patient.getDiagnosis());
    }

    @Test
    void testLowRiskOCS() {
        KieSession kSession = getKieSession();
        Patient patient = new Patient(true, false, false);
        kSession.insert(patient);
        kSession.fireAllRules();
        kSession.dispose();

        // Ожидается, что правило "Низкий риск ОКС" установит диагноз
        assertEquals("Низкий риск ОКС", patient.getDiagnosis());
    }
}
```

Чтобы запустить тесты, выполните в терминале:
```bash
mvn test
```

В результате в консоли будет показана сводка тестов. Если все тесты пройдены успешно, вы увидите:
```
Tests run: X, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

### 📂 Структура проекта

```
drools-decision-okr-project/
├── pom.xml                # Конфигурация Maven
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.drools
│   │   │       ├── Main.java       # Главный класс (консольный интерфейс)
│   │   │       ├── Patient.java    # Класс пациента
│   │   └── resources
│   │       ├── META-INF
│   │       │   └── kmodule.xml    # Конфигурация Drools
│   │       └── rules
│   │           └── rules.drl      # Правила Drools
│   └── test
│       └── java
│           └── com.example.drools
│               └── DroolsTest.java  # Тесты проекта
└── README.md
```

---

### ⚙️ Расширение функционала  
Вы можете добавлять новые правила в файл `rules.drl`, например:

```java
rule "Средний риск ОКС"
salience 10
when
    $p : Patient( chestPain == true, ecgAbnormal == true, troponinHigh == false )
then
    System.out.println("Диагноз: Средний риск ОКС. Требуется дополнительное обследование.");
    $p.setDiagnosis("Средний риск ОКС");
    update($p);
end
```

После изменения правил, пересоберите проект:
```bash
mvn clean install
mvn exec:java
```
и запустите тесты:
```bash
mvn test
```

---

### 👨‍💻 Автор  
**Dnoniel-Ermolaev**  
📧 **Контакты:** [GitHub Profile](https://github.com/Dnoniel-Ermolaev)  
📩 **Email:** ermolaev12danil@gmail.com

---
