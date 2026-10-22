package proj.markrone.library.api.enums;

import lombok.Getter;


@Getter
public enum Operation {
    CREATE_BOOK("Создание книги в каталоге"),
    UPDATE_BOOK("Обновление книги"),
    DELETE_BOOK("Удаление книги"),

    CREATE_ARRIVAL("Регистрация поступления"),
    UPDATE_ARRIVAL("Обновление поступления"),
    DELETE_ARRIVAL("Удаление поступления"),

    CREATE_USER("Создание пользователя"),
    UPDATE_USER("Обновление пользователя"),
    DELETE_USER("Удаление пользователя"),

    GET_ALL_BOOKS("Получение списка книг"),
    GET_ALL_ARRIVALS("Получение списка поступлений"),
    GET_ALL_USERS("Получение списка пользователей"),
    GET_ALL_READERS("Получение списка читателей"),
    GET_ALL_ROLES("Получение списка ролей"),
    GET_ALL_BOOK_COPIES("Получение списка экземпляров книг"),
    GET_AVAILABLE_COPIES("Получение доступных для выдачи экземпляров"),

    REGISTER_COPY("Регистрация экземпляра"),
    DELETE_COPY("Удаление экземпляра"),
    INCLUDE_IN_FUND("Включить экземпляр в фонд"),
    WRITE_OFF_COPY("Списать экземпляр"),
    RELOCATE_COPY("Переместить экземпляр"),

    ISSUE_BOOK("Выдать книгу читателю"),
    RETURN_BOOK("Принять книгу обратно"),
    GET_ISSUES_BY_READER("Получение выданных книг читателя"),
    GET_OVERDUE_ISSUES("Получение просроченных выдач"),
    GET_ALL_ISSUES("Получение всех выдач"),

    LOGIN("Аутентификация пользователя"),
    REGISTER("Регистрация пользователя"),

    EXPORT_BOOKS_EXCEL("Экспорт книг в Excel"),
    EXPORT_BOOKS_PDF("Экспорт книг в PDF"),
    EXPORT_BOOKS_JSON("Экспорт книг в JSON"),
    SEARCH_BOOKS("Поиск книг"),
    GET_COPIES_BY_STATUS("Получение экземпляров по статусу"),
    GET_ACTIVE_ISSUES("Получение активных выдач"),
    GET_RETURNED_ISSUES("Получение возвращённых выдач"),

    DISCONNECT("Отключение от сервера");

    private final String description;

    Operation(String description) {
        this.description = description;
    }
}
