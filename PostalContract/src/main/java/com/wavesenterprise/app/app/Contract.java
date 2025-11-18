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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

import static com.wavesenterprise.app.api.IContract.Keys.*;
import static com.wavesenterprise.app.api.IContract.Role.*;
import static com.wavesenterprise.app.api.IContract.StutusPostal.*;


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

    //вспомогательные методы -------------------------------------------------------------------------------------------

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
    public PostItem createPostalItems(String trackNumber , String sender, String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom, String status) {
        PostItem newPostalItem = new PostItem();
        newPostalItem.setTrackNumber(trackNumber);
        newPostalItem.setSender(sender);
        newPostalItem.setRecipient(recipient);
        newPostalItem.setType(type);
        newPostalItem.setClassDeparture(classDeparture);
        newPostalItem.setWeight(weight);
        newPostalItem.setAddressTo(addressTo);
        newPostalItem.setAddressFrom(addressFrom);
        newPostalItem.setStatus(status);
        return newPostalItem;
    }

    /// Создание денежного перевода
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

    /// Генерация трек номера
    public static String generationTrackNumber(String addressTo, String addressFrom) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyy");
        String s = now.format(fmt);
        String trackNumber = "RR" + s + addressTo + addressFrom;
        System.out.println(trackNumber);
        return trackNumber;
    }

    // проверки --------------------------------------------------------------------------------------------------------

    /// Проверка на админа
    private void checkAdminRights() {
        User user = getUserOrThrow(call.getCaller());
        if (ADMIN.equals(user.getRole())) {
            throw new IllegalStateException("Только администратор может выполнять это действие");
        }
    }

    /// Проверка на существование пользователя и его получение
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

    // инициализация контракта -----------------------------------------------------------------------------------------

    @ContractInit
    @Override
    public void init(){
        // создаём админа
        createUser("Семенов Семен Семенович", "ПО г. Ростова-на-Дону", "3NgEjoVu9e2PavxDkRU6ZsX55MyNFy7LeUR", 50, ADMIN, "344000");

        // создаём других пользователей
        createUser("Петров Петр Петрович", "ПО г. Ростова-на-Дону", "3NosmW2oc9QYCi1xEPUQyyJk2WKDwQpyQnv", 50, EMPLOYEE, "344000");
        createUser("Антонов Антон Антонов", "ПО г. Таганрога", "3NwdYkLxcyCH1aP15y5cZdwKu3o6rSNWVyP", 50, EMPLOYEE, "347900");

        createUser("Юрьев Юрий Юрьевич", "", "3Ngqy6n5GQbUxNnqqdwWSrinhtknmLFGHLM", 50, USER, null);
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
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);
        sender.setName(name);
        sender.setHomeAddress(homeAddress);
        users.put(call.getCaller(), sender);
    };

    // функции администратора ------------------------------------------------------------------------------------------

    /// Добавление или удаление сотрудника почтового отделения
    @ContractAction
    @Override
    public void setUserRole(String userAddress,String postId, boolean isEmployee){
        checkAdminRights();
        User sender = getUserOrThrow(userAddress);
        if(isEmployee) {
            sender.setRole(EMPLOYEE);
            sender.setPostId("RR" + postId);
        } else {
            sender.setRole(USER);
            sender.setPostId(null);
        }

        users.put(userAddress, sender);
    };

    /// Изменить айди почтового отделения у пользователя
    @ContractAction
    @Override
    public void setUserPostId(String userAddress, String postId){
        checkAdminRights();
        User sender = getUserOrThrow(userAddress);
        sender.setPostId(postId);
        users.put(userAddress, sender);
    }

    // почтовое отправление --------------------------------------------------------------------------------------------

    /// Отправить почтовое отправление
    @Override
    @ContractAction
    public void sendPostItem( String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom) throws IllegalAccessException {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        String trackNumber = generationTrackNumber(addressTo, addressFrom);

        // Создаем отправление
        PostItem newPosatItem = createPostalItems(trackNumber, caller, recipient, type, classDeparture, weight, addressTo, addressFrom, ACTIVE);

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
    @ContractAction
    @Override
    public void recordPostItem(String sender, String recipient, String type, byte classDeparture, int weight, String addressTo, String addressFrom) {
        String caller = call.getCaller();
        User employee = getUserOrThrow(caller);

        if (EMPLOYEE.equals(employee.getRole())) {
            throw new IllegalStateException("Только сотрудник может записывать отправления");
        }

        if (employee.getPostId() == addressFrom) {
            throw new IllegalStateException("отправление не относится к вашему отделению");
        }

        String trackNumber = generationTrackNumber(addressTo, addressFrom);

        PostItem postItem = createPostalItems(trackNumber, sender, recipient, type, classDeparture, weight, addressTo, addressFrom, ACTIVE);

        postals.put(trackNumber, postItem);
    }

    /// Получить почтовое отправление
    @ContractAction
    @Override
    public PostItem getPostItem(String trackNumber) {
        String caller = call.getCaller();
        PostItem postItem = getPostItemOrThrow(trackNumber);

        if (caller.equals(postItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого отправления");
        }
        postItem.setStatus(ACCEPTED);
        postals.put(trackNumber, postItem);

        return postItem;
    }

    /// Отказаться от отправления
    @ContractAction
    @Override
    public void refusePostItem(String trackNumber) {
        String caller = call.getCaller();
        PostItem postItem = getPostItemOrThrow(trackNumber);

        if (caller.equals(postItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого отправления");
        }

        postItem.setStatus(REFUSE);
        postals.put(trackNumber, postItem);
    }


    // Денежные переводы -----------------------------------------------------------------------------------------------

    ///  Отправить денежный перевод
    @ContractAction
    @Override
    public void sendTransfer(String recipient, int amount, int timeLive) {
        String transferId = UUID.randomUUID().toString();
        String caller = call.getCaller();
        User sender =  getUserOrThrow(caller);

        if(sender.getBalance() > amount) {
            throw new IllegalStateException("Вы недостаточно средств");
        }

        sender.setBalance(sender.getBalance() - amount);

        TransferItem transferItem = createTransfer(transferId, call.getCaller(),recipient,  amount, timeLive, ACTIVE);
        transfer.put(transferId, transferItem);
        users.put(caller, sender);
    }

    /// Получить денежный перевод
    @ContractAction
    @Override
    public void getTransfer(String transferId) {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        TransferItem transferItem = getTransferOrThrow(transferId);

        if(caller.equals(transferItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого перевода");
        }

        if(Objects.equals(transferItem.getStatus(), ACTIVE)) {
            throw new IllegalStateException("перевод не активен");
        }
        int amount = transferItem.getAmount();
        sender.setBalance(sender.getBalance() + amount);

        transferItem.setStatus(ACCEPTED);

        transfer.put(transferItem.getTransferId(), transferItem);
        users.put(caller, sender);
    }

    /// Отказаться от денежного перевода
    @ContractAction
    @Override
    public void refuseTransfer(String transferId) {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        TransferItem transferItem = getTransferOrThrow(transferId);

        if(caller.equals(transferItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого перевода");
        }

        transferItem.setStatus(REFUSE);

        transfer.put(transferItem.getTransferId(), transferItem);
        users.put(caller, sender);
    }


    // получение информации --------------------------------------------------------------------------------------------


    /// Получить активные отправления-----------------------------------------------------------------------------------

    /// Получить историю отправлений------------------------------------------------------------------------------------

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
