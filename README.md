# Тестирование API Stellar Burgers

В этом репозитории содержатся тесты для API сервиса **Stellar Burgers**. Тестирование включает проверку основных операций с пользователями и заказами, согласно документации API.

## Задание

### 1. Создание пользователя:
- Создать уникального пользователя.
- Создать пользователя, который уже зарегистрирован.
- Создать пользователя и не заполнить одно из обязательных полей.

### 2. Логин пользователя:
- Логин под существующим пользователем.
- Логин с неверным логином и паролем.

### 3. Изменение данных пользователя:
- С авторизацией.
- Без авторизации.
- Для обеих ситуаций необходимо проверить возможность изменения каждого поля. Для неавторизованного пользователя должна возвращаться ошибка.

### 4. Создание заказа:
- С авторизацией.
- Без авторизации.
- С ингредиентами.
- Без ингредиентов.
- С неверным хешем ингредиентов.

### 5. Получение заказов конкретного пользователя:
- Авторизованный пользователь.
- Неавторизованный пользователь.



