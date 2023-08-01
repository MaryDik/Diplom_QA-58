# Дипломная работа профессии «Тестировщик ПО»
Дипломная работа представляет собой автоматизацию тестирования веб-сервиса "Путешествие дня", взаимодействующего с СУБД и API Банка
## Документы
- [План автоматизации тестирования](https://github.com/MaryDik/Diplom_QA-58/blob/main/documents/Plan.md)
- [Отчет по итогам тестирования](https://github.com/MaryDik/Diplom_QA-58/blob/main/documents/Report.md)
- [Отчет по итогам автоматизации](https://github.com/MaryDik/Diplom_QA-58/blob/main/documents/Summary.md)
## Описание приложения
Полные условия и исходные данные описанного кейса можно посмотреть [здесь](https://github.com/netology-code/qa-diploma)
## Инструкция по запуску автотестов
### Подготовка окружения
**Шаг 1. Необходимо установить ПО (если необходимое ПО уже установлено, то данный шаг пропустить):**
- Git
- IntelliJ IDEA
- JDK 11
- Docker Desktop ([Инструкция по установке и настройке](https://docs.docker.com/desktop/) в зависимости от операционной системы)
  
**Шаг 2. Клонирование репозитория на локальный компьютер:**
1. Открыть браузер и перейти на страницу проекта по [ссылке](https://github.com/MaryDik/Diplom_QA-58)
2. На странице репозитория нажать на зеленую кнопку **"<>Code"** и скопировать **URL-адрес клона** из всплывающего окна **"Clone"**
   
**Шаг 3. Запуск Docker Desktop:**
1. Открыть Docker Desktop *
   
   _* При первом использовании может понадобиться авторизация на [Docker Hub](https://hub.docker.com/)_

**Шаг 4. Запуск проекта в IntelliJ IDEA**
1. Открыть IntelliJ IDEA
2. Нажать на кнопку **"Get from VCS"**
3. В открывшемся окне в поле **URL** вставить скопированный ранее **URL-адрес клона проекта**(см. Шаг2, п.2)
4. В поле **Directory** выбрать каталог на локальном компьютере, в котором будет размещаться клон репозитория
5. Нажать на кнопку **"Clone"**
6. Подождать пока закончится сканирование файлов и загрузка

**Шаг 5. Инициализация контейнеров с СУБД MySQL, PostgreSQL и симулятором банковских сервисов**
1. В IntelliJ IDEA открыть терминал кликнув левой кнопкой мыши на вкладку "Terminal" (альтернативный вариант Alt+F12)
2. Во вкладке терминала выполнить команду: docker-compose up -d
3. Дождаться сборки контейнеров *
   
   _* Убедиться в сборке контейнеров можно в приложении Docker Desktop или командой в терминале docker-compose ps_
   
**Шаг 6. Запуск SUT с подключением к MySQL/PostgreSQL**
1. В IntelliJ IDEA открыть дополнительную вкладку в терминале кликом по кнопке **+**
2. В новой вкладке терминала ввести следующую команду в зависимости от базы данных:
- java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar **(для MySQL)**
- java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar  **(для PostgreSQL)**
3. Приложение должно запуститься и работать по адресу http://localhost:8080/

## Запуск автотестов
1. В IntelliJ IDEA открыть еще одну дополнительную вкладку в терминале кликом по кнопке **+**
2. В новой вкладке ввести команду в зависимости от выбранной СУБД:
- ./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app" **(для MySQL)**
- ./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app" **(для PostgreSQL)**

## Создание отчет по результатам тестирования (Allure Report):
1. В IntelliJ IDEA 2 раза нажать Ctrl и в командной строке **«Run Anything»** выполнить команду: **gradlew allureServe**
- После окончания тестов завершить работу приложения и остановить все сервисы, а также удалить контейнеры командой **docker-compose down**

