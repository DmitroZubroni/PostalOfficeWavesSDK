package com.wavesenterprise.app.api;

import com.wavesenterprise.app.domain.PostItem;
import com.wavesenterprise.app.domain.Transfer;
import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.sdk.contract.api.annotation.ContractAction;
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс контракта почтового сервиса
 * Определяет все доступные методы для взаимодействия с контрактом
 */
public interface IContract {

    // ===== ИНИЦИАЛИЗАЦИЯ =====

    /**
     * Инициализация контракта при деплое
     * Создает администратора системы
     */
    @ContractInit
    void init();


    // ===== УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ =====

    /**
     * Регистрация нового пользователя
     * @param name Имя пользователя
     * @param homeAddress Домашний адрес
     * @param role Роль: USER, EMPLOYEE, ADMIN (только для администраторов)
     */
    @ContractAction
    void registerUser(String name, String homeAddress, String role);

    /**
     * Обновление персональных данных пользователя
     * @param name Новое имя
     * @param homeAddress Новый адрес
     */
    @ContractAction
    void updateUserInfo(String name, String homeAddress);

    /**
     * Получение информации о пользователе
     * @param blockchainAddress Адрес пользователя в блокчейне
     * @return Информация о пользователе
     */
    @ContractAction
    Optional<User> getUserInfo(String blockchainAddress);


    // ===== АДМИНИСТРАТИВНЫЕ ФУНКЦИИ =====

    /**
     * Добавление сотрудника почтового отделения (только для администраторов)
     * @param userAddress Адрес пользователя
     * @param postId ID почтового отделения
     */
    @ContractAction
    void addEmployee(String userAddress, String postId);

    /**
     * Удаление сотрудника (только для администраторов)
     * @param userAddress Адрес сотрудника
     */
    @ContractAction
    void removeEmployee(String userAddress);

    /**
     * Изменение почтового отделения сотрудника (только для администраторов)
     * @param employeeAddress Адрес сотрудника
     * @param newPostId Новый ID отделения
     */
    @ContractAction
    void setEmployeePostId(String employeeAddress, String newPostId);


    // ===== ПОЧТОВЫЕ ОТПРАВЛЕНИЯ =====

    /**
     * Отправка почтового отправления
     * @param recipient Адрес получателя
     * @param type Тип: письмо, бандероль, посылка
     * @param departureClass Класс: 1, 2, 3
     * @param weight Вес в кг (1-10)
     * @param declaredValue Объявленная ценность
     * @param destinationAddress Адрес назначения
     */
    @ContractAction
    void sendPostal(String recipient, String type, byte departureClass,
                    int weight, int declaredValue, String destinationAddress);

    /**
     * Регистрация отправления сотрудником
     * @param recipient Адрес получателя
     * @param type Тип отправления
     * @param departureClass Класс отправления
     * @param weight Вес
     * @param declaredValue Объявленная ценность
     * @param destinationAddress Адрес назначения
     */
    @ContractAction
    void recordPostal(String recipient, String type, byte departureClass,
                      int weight, int declaredValue, String destinationAddress);

    /**
     * Получение почтового отправления
     * @param trackNumber Трек-номер отправления
     */
    @ContractAction
    void receivePostal(String trackNumber);

    /**
     * Отслеживание отправления по трек-номеру
     * @param trackNumber Трек-номер
     * @return Информация об отправлении
     */
    @ContractAction
    Optional<PostItem> trackDeparture(String trackNumber);

    /**
     * Получение истории отправлений пользователя
     * @return Список отправлений
     */
    @ContractAction
    List<PostItem> getHistoryDeparture();


    // ===== ДЕНЕЖНЫЕ ПЕРЕВОДЫ =====

    /**
     * Отправка денежного перевода
     * @param recipient Адрес получателя
     * @param amount Сумма перевода
     * @param liveTime Время жизни в днях
     */
    @ContractAction
    void sendTransfer(String recipient, int amount, int liveTime);

    /**
     * Получение денежного перевода
     * @param transferId ID перевода
     */
    @ContractAction
    void receiveTransfer(String transferId);


    // ===== КОНСТАНТЫ =====

    class Role {
        public static final String USER = "USER";
        public static final String EMPLOYEE = "EMPLOYEE";
        public static final String ADMIN = "ADMIN";
    }

    class Keys {
        public static final String USERS_MAPPING = "USERS_MAPPING";
        public static final String POST_ITEMS_MAPPING = "POST_ITEMS_MAPPING";
        public static final String TRANSFERS_MAPPING = "TRANSFERS_MAPPING";
        public static final String DAILY_COUNTER = "DAILY_COUNTER";
        public static final String USER_POSTAL_ITEMS = "USER_POSTAL_ITEMS";
        public static final String ACTIVE_TRANSFERS = "ACTIVE_TRANSFERS";
        public static final String USED_TRANSFERS = "USED_TRANSFERS";
    }
}