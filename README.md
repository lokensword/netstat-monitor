# Монитор сетевых соединений

Приложение для анализа активных сетевых соединений через утилиту `netstat`.  
Поддерживает Windows и Linux. Автоматически определяет ОС, выполняет соответствующую команду, парсит вывод и генерирует отчёт.

## Технологии
- Java 17
- Gradle (сборка и управление зависимостями)
- JUnit 5 (тестирование)

## Пример работы

### Вход (автоматически получается из `netstat`)

Proto  Local Address      Foreign Address    State       PID
TCP    0.0.0.0:135        0.0.0.0:0          LISTENING   1216
TCP    192.168.0.141:49665 20.189.173.15:443  ESTABLISHED 784

### Выход (stdout)

--- Listening Ports ---
PID: 1216, Process: svchost, Port: 135

--- External Connections ---
PID: 784, Process: chrome, Foreign: 20.189.173.15:443

--- Port 8080 Check ---
Port 8080 is not in use.


## Сборка проекта

Убедитесь, что установлены:
- JDK 17 или новее
- Git

Выполните в корне проекта:
./gradlew build

или на Windows:
gradlew(.bat) build

Результат: исполняемый JAR-файл в build/libs/.
Конфигурация

Создайте файл app.properties в корне проекта (он игнорируется в Git):
# Порт для проверки занятости
monitor.port.check=135

# Режим вывода (в текущей версии не используется)
output.mode=VERBOSE

!app.properties обязателен для запуска. Без него приложение завершится с ошибкой.

Запуск тестов

Выполните:

./gradlew test

Ожидаемый результат:

    Все тесты проходят успешно
    Отчёт доступен в build/reports/tests/test/index.html

Тесты покрывают:

    Парсинг вывода netstat (Windows и Linux)
    Извлечение порта из IPv4/IPv6 адресов
    Загрузку конфигурации

Запуск приложения

    Создайте app.properties (см. выше)
    Выполните:

	./gradlew run

    или

    cmd
    gradlew(.bat) run

Приложение выведет три раздела:

    Слушающие порты (LISTEN/LISTENING)
    Установленные внешние соединения (ESTABLISHED)
    Результат проверки указанного порта

    Приложение работает только с привилегиями, достаточными для запуска netstat -ano (Windows) или netstat -anp (Linux).