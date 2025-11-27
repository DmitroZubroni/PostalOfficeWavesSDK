package com.wavesenterprise.app.api;

import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.app.domain.PostItem;
import com.wavesenterprise.app.domain.TransferItem;
import com.wavesenterprise.sdk.contract.api.annotation.*;

import java.util.List;

/**
 * Интерфейс смарт-контракта системы "Почта-сервис"
 * Определяет все методы, доступные для вызова извне
 */
public interface IContract {

    // ==================== ИНИЦИАЛИЗАЦИЯ ====================

    /**
     * Инициализация контракта - создание начальных пользователей
     */
    @ContractInit
    void init();

    // ==================== ПОЛУЧЕНИЕ ИНФОРМАЦИИ ====================

    /**
     * Получение информации о пользователе
     * @param userAddress Адрес пользователя
     * @return Объект пользователя
     */
    @ContractAction
    User getUserOrThrow(String userAddress);

    /**
     * Отслеживание почтового отправления по трек-номеру
     * @param trackNumber Трек-номер отправления
     * @return Объект почтового отправления
     */
    @ContractAction
    PostItem getPostalOrThrow(String trackNumber);

    /**
     * Отслеживание почтового отправления по трек-номеру
     * @param transferId айди перевода
     * @return Объект переввода
     */
    TransferItem getTransferOrThrow(String transferId);

    // ==================== УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ ====================

    /**
     * Регистрация нового пользователя в системе
     * @param name Имя пользователя
     * @param homeAddress Домашний адрес
     */
    @ContractAction
    void registration(String name, String homeAddress);

    /**
     * Изменение персональных данных пользователя
     * @param name Новое имя
     * @param homeAddress Новый домашний адрес
     */
    @ContractAction
    void setPersonInfo(String name, String homeAddress);

    // ==================== ФУНКЦИИ АДМИНИСТРАТОРА ====================

    /**
     * Назначение или снятие роли сотрудника почтового отделения
     * @param userAddress Адрес пользователя
     * @param postId Индекс почтового отделения
     * @param isEmployee true - назначить сотрудником, false - снять роль
     */
    @ContractAction
    void setUserRole(String userAddress, String postId, boolean isEmployee);

    /**
     * Изменение идентификатора почтового отделения для сотрудника
     * @param userAddress Адрес сотрудника
     * @param postId Новый индекс почтового отделения
     */
    @ContractAction
    void setUserPostId(String userAddress, String postId);

    // ==================== ПОЧТОВЫЕ ОТПРАВЛЕНИЯ ====================

    /**
     * Отправка почтового отправления пользователем
     * @param recipient Адрес получателя
     * @param type Тип отправления
     * @param classDeparture Класс отправления
     * @param weight Вес отправления
     * @param importantValue Объявленная ценность
     * @param addressTo Адрес назначения
     * @param addressFrom Адрес отправления
     */
    @ContractAction
    void sendPostItem(String recipient, String type, byte classDeparture, int weight,
                      int importantValue, String addressTo, String addressFrom);

    /**
     * Запись информации о почтовом отправлении сотрудником
     * Добавление точки транзита для почтового отправления
     * @param trackNumber Трек-номер отправления
     * @param postOfficeId Идентификатор почтового отделения
     * @param weight Вес отправления в точке транзита
     */
    @ContractAction
    void addTransitPoint(String trackNumber, String postOfficeId, int weight);

    /**
     * Получение почтового отправления получателем
     * @param trackNumber Трек-номер отправления
     * @return Объект почтового отправления
     */
    @ContractAction
    PostItem getPostItem(String trackNumber);

    /**
     * Отказ от получения почтового отправления
     * @param trackNumber Трек-номер отправления
     */
    @ContractAction
    void refusePostItem(String trackNumber);

    // ==================== ДЕНЕЖНЫЕ ПЕРЕВОДЫ ====================

    /**
     * Отправка денежного перевода
     * @param recipient Адрес получателя
     * @param amount Сумма перевода
     * @param timeLive Время жизни перевода в днях
     */
    @ContractAction
    void sendTransfer(String recipient, int amount, int timeLive);

    /**
     * Получение денежного перевода получателем
     * @param transferId Идентификатор перевода
     */
    @ContractAction
    void getTransfer(String transferId);

    /**
     * Отказ от получения денежного перевода
     * @param transferId Идентификатор перевода
     */
    @ContractAction
    void refuseTransfer(String transferId);

    // ==================== МЕТОДЫ ИСТОРИИ ====================

    /**
     * Получение списка отправленных пользователем почтовых отправлений
     * @param userAddress Адрес пользователя
     * @return Список почтовых отправлений
     */
    @ContractAction
    List<PostItem> getUserSentItems(String userAddress);

    /**
     * Получение списка активных почтовых отправлений пользователя
     * @param userAddress Адрес пользователя
     * @return Список активных почтовых отправлений
     */
    @ContractAction
    List<PostItem> getActivePostItems(String userAddress);



    // ==================== КОНСТАНТЫ ====================

    /** Роли пользователей в системе */
    class Role {
        public static final String ADMIN = "ADMIN";
        public static final String EMPLOYEE = "EMPLOYEE";
        public static final String USER = "USER";
    }

    /** Типы почтовых отправлений */
    class PostalItemType {
        public static final String MESSAGE = "MESSAGE";
        public static final String POSTAL = "POSTAL";
        public static final String BANDEROL = "BANDEROL";
    }

    /** Статусы почтовых отправлений и денежных переводов */
    class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String REFUSE = "REFUSE";
        public static final String ACCEPTED = "ACCEPTED";
        public static final String CANCELLED = "CANCELLED";
    }

    /** Ключи для хранения данных в состоянии контракта */
    class Keys {
        public static final String USER_MAPPING = "USER_MAPPING";
        public static final String POST_ITEM_MAPPING = "POST_ITEM_MAPPING";
        public static final String TRANSFERS_MAPPING = "TRANSFERS_MAPPING";
        public static final String USER_SENT_ITEMS = "USER_SENT_ITEMS";
        public static final String LAST_DATE = "LAST_DATE";
    }
}