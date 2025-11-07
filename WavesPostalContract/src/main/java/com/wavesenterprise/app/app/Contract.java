package com.wavesenterprise.app.app;

import com.wavesenterprise.app.api.IContract;
import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.sdk.contract.api.annotation.*;
import com.wavesenterprise.sdk.contract.api.state.*;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;
import com.wavesenterprise.sdk.contract.api.domain.ContractCall;

import java.util.Optional;

import static com.wavesenterprise.app.api.IContract.Keys.USERS_MAPPING;

@ContractHandler
public class Contract implements IContract {

    private final ContractState state;
    private final ContractCall call;
    private final Mapping<User> users;

    public Contract(ContractState state, ContractCall call) {
        this.state = state;
        this.call = call;
        this.users = this.state.getMapping(new TypeReference<>() {}, USERS_MAPPING);
    }

    /**
     * Инициализация контракта.
     * Выполняется один раз при деплое.
     */
    @Override
    @ContractInit
    public void init() {
        this.state.put("CONTRACT_OWNER", call.getCaller());
    }

    /**
     * Регистрация нового пользователя.
     * blockchainAddress = call.getCaller()
     */
    @Override
    @ContractAction
    public void registerUser(String name, String homeAddress, String role) {
        String caller = call.getCaller();

        // Проверяем, есть ли уже пользователь
        Optional<User> existing = users.tryGet(caller);
        if (existing.isPresent()) {
            throw new IllegalStateException("Пользователь уже зарегистрирован");
        }

        // Создаём нового пользователя
        User newUser = new User();
        newUser.setName(name);
        newUser.setHomeAddress(homeAddress);
        newUser.setBlockchainAddress(caller);
        newUser.setBalance(0.0);
        newUser.setRole(role);
        newUser.setPostId(caller.substring(0, 6)); // "RR" + 6 символов адреса

        users.put(caller, newUser);
    }

    /**
     * Получение информации о пользователе по blockchain-адресу.
     * Возвращает объект User (фронт получит JSON).
     */
    @Override
    @ContractAction
    public Optional<User> getUserInfo(String blockchainAddress) {
        return users.tryGet(blockchainAddress);
    }
}
