## 🏥 Drools Decision OCS Project  

### 📌 Описание  
Этот проект представляет собой **систему поддержки принятия решений (СППР)** на основе **Drools** для диагностики **Острого Коронарного Синдрома (ОКС)**.  
Приложение работает в **консоли**, запрашивает у пользователя данные о состоянии пациента и **на основе правил** делает предварительное заключение о степени риска.  

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

---

### 🏥 Как пользоваться  
После запуска приложение задаст вопросы о симптомах пациента. Введите `true` или `false` и нажмите **Enter**.  

#### Пример работы:  
```
Система принятия решения по диагностике ОКС
Введите данные пациента:
Есть ли боль в груди? (true/false): true
Есть ли отклонения на ЭКГ? (true/false): true
Уровень тропонина повышен? (true/false): false

Диагноз: Высокий риск ОКС. Рекомендуется срочное вмешательство.
Количество сработавших правил: 1
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
└── README.md
```

---

### ⚙️ Расширение функционала  
Вы можете **добавлять новые правила** в файл `rules.drl`, например:  

```java
rule "Средний риск ОКС"
when
    $p : Patient( chestPain == true, ecgAbnormal == true, troponinHigh == false )
then
    System.out.println("Диагноз: Средний риск ОКС. Требуется дополнительное обследование.");
    $p.setDiagnosis("Средний риск ОКС");
end
```

После изменения правил, **пересоберите проект**:  
```bash
mvn clean install
mvn exec:java
```

---

### 👨‍💻 Автор  
**Dnoniel-Ermolaev**  
📧 **Контакты:** [GitHub Profile](https://github.com/Dnoniel-Ermolaev)  
📩 **Email:** ermolaev12danil@gmail.com  

---
