package com.wavesenterprise.app.api;

import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.sdk.contract.api.annotation.ContractAction;
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit;

import java.util.Optional;

public interface IContract {

    @ContractInit
    void init();

    // регистрация (все)
    @ContractAction
    void registerUser(String name, String homeAddress, String role);

    // авторизация (все)

    // изменить свои персональны данные (все)

    // добавить или удалить сотрудника почтового отделение (админ)

    // изменить идентификатор почтового отделения для сотрудника (админ)

    // отправить почтовое отделение (все)

    // записать данные о почтовом отделении (сотрудник)

    // принять почтовое отправление (все)

    // отправить денежный перевод (все)

    // принять денежный перевод

    // отследить отправление по трек номеру (все)

    // просмотреть историю своих отправлений

    // не забываем про то что трек номер генерируется автоматически и сумма рассчитывается согласно бизнес логике задания

    // информация о пользователе
    @ContractAction
    Optional<User> getUserInfo(String blockchainAddress);


    class Role {
        public static final String USER = "USER";
        public static final String EMPLOYEE = "EMPLOYEE";
        public static final String ADMIN = "ADMIN";
    }

    class Keys {
        public static final String USERS_MAPPING = "USERS_MAPPING";
    }
}