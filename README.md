# CNS Chat client and server

## Общее.

Для сборки проекта нужна Java 8, gradle 2.xx+.

## Сборка.

Сборка проекта производится с помощью Gradle. После получения исходных текстов запустить в папке проекта сборку можно командой 
gradlew clean build. Будет выполнена чистовая сборка проекта (чистка, компиляция, тесты, сборка дистрибутивов).
После сборки в директориях cns-client\build\distributions и cns-server\build\distributions будут архивы с готовыми 
дистрибутивами - достаточно их распаковать и запустить скрипт из дочерней директории bin.

## Параметры командной строки для запуска сервера и клиента.

Сервер: cns-client -t TCP -h host:port -n nickname

Клиент: cns-client -t TCP -h host:port 

При отсутствии параметров будет выведена справка по параметрам.

## Особенности реализации.

Клиентская часть написана на базе стандартных Java сокетов.
Серверная на базе селекторов и каналов.
Команды имеют префикс #(решетка). Доступные серверные команды - #help, #count, #nick. 
Есть клиентская команда для выхода из клиента - #quit.

## Нагрузочное тестирование.

Проводилось с помощью JMeter. Из-за особенностей реализации стандартного JMeter TCP-семплера и характера приложения-чата
был выбран следующий сценарий нагрузочного тестировани:
* семплер одного пользователя подключался к серверу и отсылал ему команду #nick и затем еще одно приветственное сообщение;
* после этого семплер оставался висеть подключенным и принимать сообщения от сервера;
* после достижения лимита пользователей сценарий останавливался - тут есть следующий нюанс - стандартный 
семплер считает, что если не был достигнут некий конец считываемых данных - то произошла ошибка; поэтому все 
подключения он считает завершившимися с ошибкой;
* также после достижения лимита пользователей перед остановкой сценария производилось подключени под 
пользователем с ником admin и посылалось тестовое сообщение для проверки, что все остальные пользователи его получат;
* чтобы удостовериться в том, что тестовые пользователи успешно подключаются и получают сообщения был настроен 
Assert Listener с проверкой входных данных на стандартное сообщение "Nickname accepted" от чат-сервера при задании ника;
* после завершения тестов JMeter сохранял результат работы лисенера в соответствующий файл.

План и результат работы находятся тут: [план и результаты](https://drive.google.com/open?id=0B6ghCUlgHG9PbkhsOGpNMF9mQ0k).

В результатах видны по каждому семплу сообщение от админа и сообщение о принятии никнейма, что говорит о том, что 
все пользователи были подключенными во время теста и держали соединение. 

Параметры теста - 5000 пользователей и 250 секунд ramp-up период.


