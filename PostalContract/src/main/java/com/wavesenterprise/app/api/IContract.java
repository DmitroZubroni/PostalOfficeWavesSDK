package com.wavesenterprise.app.api;
import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.app.domain.PostItem;
import com.wavesenterprise.app.domain.TransferItem;
import com.wavesenterprise.sdk.contract.api.annotation.*;

public interface IContract {

    // инициализация контракта
    @ContractInit
    void init();

    // регистрация
    @ContractAction
    void registration(String name, String homeAddress) throws IllegalAccessException;

    // изменение персональных данных
    @ContractAction
    void setPersonInfo(String name, String homeAddress);

    // добавление или удаление сотрудника почтового отделения
    @ContractAction
     void setUserRole(String userAddress,String postId, boolean isEmployee);

    // изменить айди почтового отделения у пользователя
    @ContractAction
    void setUserPostId(String userAddress, String postId);

    // отправить почтовое отправление
    @ContractAction
    void sendPostItem( String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom) throws IllegalAccessException;

    // записать данные о почтовом отправлении
    @ContractAction
    void recordPostItem(String sender, String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom);

    // получить почтовое отправление
    @ContractAction
    PostItem getPostItem(String trackNumber);

    // отказаться от денежного отправления
    @ContractAction
    void refusePostItem(String trackNumber);

    // отправить денежный перевод
    @ContractAction
    void sendTransfer(String recipient, int amount, int timeLive);

    // получить денежный перевод
    @ContractAction
    void getTransfer(String transferId);

    // отказаться от денежного перевода
    @ContractAction
    void refuseTransfer(String transferId);

    // получить информацию о денежном перевод
    @ContractAction
    TransferItem getTransferInfo(String transferId);

    // отследить по трек номеру
    @ContractAction
    PostItem trackDeparture(String trackNumber);

    // получить информацию о пользователе
    @ContractAction
    User getPersonInfo(String userAddress);

    class Role {
        public static final String ADMIN = "ADMIN"; // администратор
        public static final String EMPLOYEE = "EMPLOYEE"; // сотрудник
        public static final String USER = "USER"; // обычный пользователь
    }

    class PostalItemType {
        public static final String MESSAGE = "MESSAGE"; // письма
        public static final String POSTAL = "POSTAL"; // посылка
        public static final String BANDEROL = "BANDEROL"; // бандероль
    }

    class StutusPostal {
        public static final String ACTIVE = "ACTIVE"; // активно
        public static final String REFUSE = "REFUSE"; // отказались
        public static final String ACCEPTED = "ACCEPTED"; // принято
    }

    class Keys {
        public static final String USER_MAPPING = "USER_MAPPING"; // мапинг всех пользователей
        public static final String POST_ITEM_MAPPING = "POST_ITEM_MAPPING"; // мапинг всех отправлений
        public static final String TRANSFERS_MAPPING = "TRANSFERS_MAPPING"; // мапинг всех переводов
    }
}
