package com.wavesenterprise.app.app;
import com.wavesenterprise.app.api.IContract;

import com.wavesenterprise.app.domain.TransferItem;
import com.wavesenterprise.sdk.contract.api.annotation.*;
import com.wavesenterprise.sdk.contract.api.domain.ContractCall;
import com.wavesenterprise.sdk.contract.api.state.ContractState;

import com.wavesenterprise.app.domain.PostItem;
import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.sdk.contract.api.state.TypeReference;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;

import java.util.Objects;
import java.util.UUID;

import static com.wavesenterprise.app.api.IContract.Keys.*;
import static com.wavesenterprise.app.api.IContract.Role.*;


@ContractHandler
public class Contract implements IContract {

    ContractCall call;
    ContractState state;

    public Mapping<User> users;
    public Mapping<PostItem> postals;
    public Mapping<TransferItem> transfer;


    public Contract() {

        this.users = state.getMapping(new TypeReference<User>(){}, USER_MAPPING);
        this.postals = state.getMapping(new TypeReference<PostItem>() {}, POST_ITEM_MAPPING);
        this.transfer = state.getMapping(new TypeReference<TransferItem>() {}, TRANSFERS_MAPPING);
    }

    //вспомогательные методы -------------

    /// Создание пользователя
    public void createUser(String name, String homeAddress, String blockchainAddress, int balance, String role, String poetId) {
        User newUsers = new User();
        newUsers.setName(name);
        newUsers.setHomeAddress(homeAddress);
        newUsers.setBlockchainAddress(blockchainAddress);
        newUsers.setBalance(balance);
        newUsers.setRole(role);
        newUsers.setPostId(poetId);
        users.put(call.getCaller(), newUsers);
    }

    /// Создание почтового отправления
    public PostItem createPostalItems(String trackNumber , String sender, String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom) {
        PostItem newPostalItem = new PostItem();
        newPostalItem.setTrackNumber(trackNumber);
        newPostalItem.setSender(sender);
        newPostalItem.setRecipient(recipient);
        newPostalItem.setType(type);
        newPostalItem.setClassDeparture(classDeparture);
        newPostalItem.setWeight(weight);
        newPostalItem.setAddressTo(addressTo);
        newPostalItem.setAddressFrom(addressFrom);
        return newPostalItem;
    }

    /// Создание денежного перевода
    ///
    /// @return
    public TransferItem createTransfer(String transferId , String sender, String recipient, int amount, int timeLive,String status) {
        TransferItem newTransferItem = new TransferItem();
        newTransferItem.setTransferId(transferId);
        newTransferItem.setSender(sender);
        newTransferItem.setRecipient(recipient);
        newTransferItem.setAmount(amount);
        newTransferItem.setTimeLive(timeLive);
        newTransferItem.setStatus(status);

        transfer.put(call.getCaller(), newTransferItem);

        return newTransferItem;
    }

    /// Проверка на админа
    private void checkAdminRights() {
        User user = getUserOrThrow(call.getCaller());
        if (ADMIN.equals(user.getRole())) {
            throw new IllegalStateException("Только администратор может выполнять это действие");
        }
    }

    // проверка на существование пользователя и его получение
    private User getUserOrThrow(String address) {
        return users.tryGet(address)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + address));
    }

    /// Проверка на существование отправление и его получение
    private PostItem getPostItemOrThrow(String trackNumber) {
        return postals.tryGet(trackNumber)
                .orElseThrow(() -> new IllegalStateException("Отправление не найдено: " + trackNumber));
    }

    /// Проверка на существования пользователя и его получение
    private TransferItem getTransferOrThrow(String transferId) {
        return transfer.tryGet(transferId)
                .orElseThrow(() -> new IllegalStateException("Перевод не найден: " + transferId));
    }

    /*
    // генерация трек номера
    public void String generationTrackNumber() {
        String trackNumber = "RR";
        return trackNumber;
    }
     */


    // инициализация контракта -------

    @ContractInit
    @Override
    public void init(){
        // создаём админа
        createUser("Семенов Семен Семенович", "ПО г. Ростова-на-Дону", "3NgEjoVu9e2PavxDkRU6ZsX55MyNFy7LeUR", 50, ADMIN, "344000");

        // создаём других пользователей
        createUser("Петров Петр Петрович", "ПО г. Ростова-на-Дону", "3NosmW2oc9QYCi1xEPUQyyJk2WKDwQpyQnv", 50, EMPLOYEE, "344000");
        createUser("Петров Петр Петрович", "ПО г. Ростова-на-Дону", "3NwdYkLxcyCH1aP15y5cZdwKu3o6rSNWVyP", 50, EMPLOYEE, "344000");

        createUser("Петров Петр Петрович", "ПО г. Ростова-на-Дону", "3Ngqy6n5GQbUxNnqqdwWSrinhtknmLFGHLM", 50, USER, null);
    };

    /// регистрация
    @ContractAction
    @Override
    public void registration(String name, String homeAddress) throws IllegalAccessException {
        if(users.tryGet(call.getCaller()).isPresent()) {
             throw new IllegalAccessException("пользователь уже зарегестрирован");
        }
        createUser(name, homeAddress, call.getCaller(), 0, USER, null);
    };

    /// Изменение персональных данных
    @ContractAction
    @Override
    public void setPersonInfo(String name, String homeAddress){
        User newUser = new User();
        newUser.setName(name);
        newUser.setHomeAddress(homeAddress);
        users.put(call.getCaller(), newUser);
    };

    // функции администратора ----------

    /// Добавление или удаление сотрудника почтового отделения
    @ContractAction
    @Override
    public void setUserRole(String userAddress,String postId, boolean isEmployee){
        checkAdminRights();
        User newEmployee = new User();
        if(isEmployee) {
            newEmployee.setRole(EMPLOYEE);
            newEmployee.setPostId(postId);
        } else {
            newEmployee.setRole(USER);
            newEmployee.setPostId(null);
        }

        users.put(userAddress, newEmployee);
    };

    /// Изменить айди почтового отделения у пользователя
    @ContractAction
    @Override
    public void setUserPostId(String userAddress, String postId){
        checkAdminRights();
        User newEmployee = new User();
        newEmployee.setPostId(postId);
        users.put(userAddress, newEmployee);
    }

    // почтовое отправление -----------


    /// Отправить почтовое отправление
    @Override
    @ContractAction
    public void sendPostItem( String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom) throws IllegalAccessException {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        String trackNumber = "RR + addressTo +  addressFrom";

        // Создаем отправление
        PostItem newPosatItem = createPostalItems(trackNumber, caller, recipient, type, classDeparture, weight, addressTo, addressFrom);

        // проверка на средства
        if(sender.getBalance() < newPosatItem.getFinalCost()) {
            throw new IllegalAccessException("недостаточно средств");
        }

        sender.setBalance((int) (sender.getBalance() - newPosatItem.getFinalCost()));

        // Списание средств
        users.put(caller, sender);
        postals.put(trackNumber, newPosatItem);
    }

    /// Записать данные об отправлении
    @Override
    public void recordPostItem(String sender, String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom) {
        String caller = call.getCaller();
        User employee = getUserOrThrow(caller);

        if (EMPLOYEE.equals(employee.getRole())) {
            throw new IllegalStateException("Только сотрудник может записывать отправления");
        }

        if (employee.getPostId() == null) {
            throw new IllegalStateException("У сотрудника не установлен идентификатор отделения");
        }

        String trackNumber = "RR + addressTo +  addressFrom";

        PostItem postItem = createPostalItems(trackNumber, sender, recipient, type, classDeparture, weight, addressTo, addressFrom);

        postals.put(trackNumber, postItem);
    }

    /// Получить почтовое отправление
    @Override
    public PostItem getPostItem(String trackNumber) {
        String caller = call.getCaller();
        PostItem postItem = getPostItemOrThrow(trackNumber);

        if (caller.equals(postItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого отправления");
        }
        return postItem;
    }

    // Денежные переводы --------

    ///  Отправить денежный перевод
    @Override
    public void sendTransfer(String recipient, int amount, int timeLive) {
        String transferId = UUID.randomUUID().toString();
        String caller = call.getCaller();
        User sender =  getUserOrThrow(caller);

        if(sender.getBalance() > amount) {
            throw new IllegalStateException("Вы недостаточно средств");
        }

        sender.setBalance(sender.getBalance() - amount);

        TransferItem transferItem = createTransfer(transferId, call.getCaller(),recipient,  amount, timeLive, "Active");
        transfer.put(transferId, transferItem);
        users.put(caller, sender);
    }

    /// Получить денежный перевод
    @Override
    public void getTransfer(String transferId) {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        TransferItem transferItem = getTransferOrThrow(transferId);

        if(caller.equals(transferItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого перевода");
        }

        if(Objects.equals(transferItem.getStatus(), "Active")) {
            throw new IllegalStateException("перевод не активен");
        }
        int amount = transferItem.getAmount();
        sender.setBalance(sender.getBalance() + amount);

        transferItem.setStatus("Cancelled");

        transfer.put(transferItem.getTransferId(), transferItem);
        users.put(caller, sender);


    }


    // получение информации -----------

    /// Получить информацию о денежном перевод
    @ContractAction
    @Override
    public TransferItem getTransferInfo(String transferId) {
        return transfer.tryGet(transferId)
                .orElseThrow(() -> new IllegalStateException("перевод не найден : " + transferId));
    }


    /// Отслеживаем отправление по трек номеру
    @Override
    @ContractAction
    public PostItem trackDeparture(String trackNumber) {
        return postals.tryGet(trackNumber)
                .orElseThrow(() -> new IllegalStateException("отправление не найдено : " + trackNumber));
    }


    /// Получить информацию о пользователе
    @ContractAction
    @Override
    public User getPersonInfo(String userAddress){
        return users.tryGet(userAddress)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + userAddress));
    };
}
