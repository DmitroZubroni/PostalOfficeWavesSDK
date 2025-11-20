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
import java.util.*;

import static com.wavesenterprise.app.api.IContract.Keys.*;
import static com.wavesenterprise.app.api.IContract.Role.*;
import static com.wavesenterprise.app.api.IContract.Status.*;


@ContractHandler
public class Contract implements IContract {

    private ContractCall call;
    private ContractState state;

    private Mapping<User> users; // пользователи в системе
    private Mapping<PostItem> postals; // почтовые отправления
    private Mapping<TransferItem> transfers; // переводы
    private Mapping<List<String>> userItems; // отправления пользователя

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /// Создание нового пользователя
    private void createUser(String name, String homeAddress, String blockchainAddress, int balance, String role, String postId) {
        User newUser = new User(name, homeAddress, blockchainAddress, balance, role, postId);
        users.put(blockchainAddress, newUser);
    }

    private int dailyCounter = 0; // счётчик отправлений за день
    private long lastResetTime = System.currentTimeMillis(); // время последнего перезапуска

    /// Простой счетчик, который сбрасывается каждые 5 секунд
    private int getDailyCounter() {
        long currentTime = System.currentTimeMillis();
        // Сбрасываем счетчик каждые 5 секунд (1 день в задании)
        if (currentTime - lastResetTime >= 5000) {
            dailyCounter = 0;
            lastResetTime = currentTime;
        }
        dailyCounter++;
        return dailyCounter;
    }

     /// Генерация трек-номера по формату: RR + ДДММГГГГ + порядковый номер + индекс отпр. + индекс получ.
    private String generationTrackNumber(String addressFrom, String addressTo) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateStr = DateTimeFormatter.ofPattern("ddMMyyyy");

        int dailyCounter = getDailyCounter();

        // Формат: RR + дата + счетчик(4 цифры) + индекс отпр. + индекс получ.
        String counterStr = String.format("%04d", dailyCounter);
        return "RR" + dateStr + counterStr + addressFrom + addressTo;
    }


     /// Проверка прав администратора
    private void checkAdminRights() {
        User user = getUserOrThrow(call.getCaller());
        if (!ADMIN.equals(user.getRole())) {
            throw new IllegalStateException("Только администратор может выполнять это действие");
        }
    }

    @ContractAction
    @Override
    /// Получение пользователя по адресу или исключение если не найден
    public User getUserOrThrow(String address) {
        return users.tryGet(address)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + address));
    }

    @ContractAction
    @Override
     /// Получение почтового отправления по трек-номеру или исключение если не найдено
    public PostItem getPostalOrThrow(String trackNumber) {
        return postals.tryGet(trackNumber)
                .orElseThrow(() -> new IllegalStateException("Отправление не найдено: " + trackNumber));
    }

    @ContractAction
    @Override
     /// Получение денежного перевода по идентификатору
    public TransferItem getTransferOrThrow(String transferId) {
        return transfers.tryGet(transferId)
                .orElseThrow(() -> new IllegalStateException("Перевод не найден: " + transferId));
    }

    // ==================== ИНИЦИАЛИЗАЦИЯ КОНТРАКТА ====================

    @ContractInit
    @Override
    public void init() {
        // Инициализация маппингов для хранения данных
        this.users = state.getMapping(new TypeReference<User>(){}, USER_MAPPING);
        this.postals = state.getMapping(new TypeReference<PostItem>(){}, POST_ITEM_MAPPING);
        this.transfers = state.getMapping(new TypeReference<TransferItem>(){}, TRANSFERS_MAPPING);
        this.userItems = state.getMapping(new TypeReference<List<String>>(){}, USER_SENT_ITEMS);


        // Создание администратора
        createUser("Семенов Семен Семенович", "ПО г. Ростова-на-Дону",
                "3NgEjoVu9e2PavxDkRU6ZsX55MyNFy7LeUR", 50, ADMIN, "RR344000");

        // Создание сотрудников почтовых отделений
        createUser("Петров Петр Петрович", "ПО г. Ростова-на-Дону",
                "3NosmW2oc9QYCi1xEPUQyyJk2WKDwQpyQnv", 50, EMPLOYEE, "RR344000");
        createUser("Антонов Антон Антонович", "ПО г. Таганрога",
                "3NwdYkLxcyCH1aP15y5cZdwKu3o6rSNWVyP", 50, EMPLOYEE, "RR347900");

        // Создание обычного пользователя
        createUser("Юрьев Юрий Юрьевич", "ул. Примерная, д.1",
                "3Ngqy6n5GQbUxNnqqdwWSrinhtknmLFGHLM", 50, USER, null);
    }

    // ==================== УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ ====================

    @ContractAction
    @Override
    /// Регистрация
    public void registration(String name, String homeAddress) {
        String caller = call.getCaller();
        if (users.tryGet(caller).isPresent()) {
            throw new IllegalStateException("Пользователь уже зарегистрирован");
        }
        createUser(name, homeAddress, caller, 0, USER, null);
    }

    @ContractAction
    @Override
    /// Изменение персональных данных
    public void setPersonInfo(String name, String homeAddress) {
        String caller = call.getCaller();
        User user = getUserOrThrow(caller);
        user.setName(name);
        user.setHomeAddress(homeAddress);
        users.put(caller, user);
    }

    // ==================== ФУНКЦИИ АДМИНИСТРАТОРА ====================

    @ContractAction
    @Override
    /// Добавление или удаление сотрудника
    public void setUserRole(String userAddress, String postId, boolean isEmployee) {
        checkAdminRights();
        User user = getUserOrThrow(userAddress);

        if (isEmployee) {
            user.setRole(EMPLOYEE);
            user.setPostId("RR" + postId);
        } else {
            user.setRole(USER);
            user.setPostId(null);
        }
        users.put(userAddress, user);
    }

    @ContractAction
    @Override
    /// Изменения айди почтового отделения для сотрудника
    public void setUserPostId(String userAddress, String postId) {
        checkAdminRights();
        User user = getUserOrThrow(userAddress);
        if (!EMPLOYEE.equals(user.getRole())) {
            throw new IllegalStateException("Можно изменять postId только сотрудникам");
        }
        user.setPostId("RR" + postId);
        users.put(userAddress, user);
    }

    // ==================== ПОЧТОВЫЕ ОТПРАВЛЕНИЯ ====================

    @ContractAction
    @Override
     /// Отправить почтовое отправление
    public void sendPostItem(String recipient, String type, byte classDeparture, int weight,
                             int importantValue, String addressTo, String addressFrom) {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        String trackNumber = generationTrackNumber(addressFrom, addressTo);
        // Создаем отправление
        PostItem postItem = new PostItem(trackNumber, caller, recipient, type, classDeparture,
                weight, importantValue, addressTo, addressFrom, ACTIVE);

        // Проверяем на наличие средств
        if (sender.balance < postItem.finalCost) {
            throw new IllegalStateException("Недостаточно средств");
        }

        // Просто списываем средства
        sender.balance -= (int) postItem.finalCost;

        Optional<List<String>> currentItemsOpt = userItems.tryGet(caller);
        List<String> currentItems = currentItemsOpt.orElse(new ArrayList<>());

        currentItems.add(trackNumber);
        userItems.put(caller, currentItems);

        // Сохраняем
        users.put(caller, sender);
        postals.put(trackNumber, postItem);
    }

    @ContractAction
    @Override
    ///  Записать данные о почтовом отправлении
    public void addTransitPoint(String trackNumber, String postOfficeId, int weight) {

        String caller = call.getCaller();
        User employee = getUserOrThrow(caller);
        PostItem postItem = getPostalOrThrow(trackNumber);

        if (!EMPLOYEE.equals(employee.getRole())) {
            throw new IllegalStateException("Только сотрудник может выполнять это действие");
        }

        // Проверка работы сотрудника в указанном отделении
        if (!postOfficeId.equals(employee.getPostId())) {
            throw new IllegalStateException("Вы не работаете в этом почтовом отделении");
        }

        postItem.addTransitPoint(postOfficeId, caller, weight);
        postals.put(trackNumber, postItem);
    }

    @ContractAction
    @Override
    /// Получить почтовое отправлении
    public PostItem getPostItem(String trackNumber) {
        String caller = call.getCaller();
        PostItem postItem = getPostalOrThrow(trackNumber);

        if (!caller.equals(postItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого отправления");
        }

        if (!ACTIVE.equals(postItem.getStatus())) {
            throw new IllegalStateException("Отправление недоступно для получения");
        }

        postItem.setStatus(ACCEPTED);
        postals.put(trackNumber, postItem);

        return postItem;
    }

    @ContractAction
    @Override
    /// Отказаться от почтового отправления
    public void refusePostItem(String trackNumber) {
        String caller = call.getCaller();
        PostItem postItem = getPostalOrThrow(trackNumber);

        if (!caller.equals(postItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого отправления");
        }

        if (!ACTIVE.equals(postItem.getStatus())) {
            throw new IllegalStateException("Нельзя отказаться от этого отправления");
        }

        postItem.setStatus(REFUSE);
        postals.put(trackNumber, postItem);
    }

    // ==================== ДЕНЕЖНЫЕ ПЕРЕВОДЫ ====================

    @ContractAction
    @Override
    ///  Отправить денежный перевод
    public void sendTransfer(String recipient, int amount, int timeLive) {
        if (amount <= 0) {
            throw new IllegalStateException("Сумма перевода должна быть положительной");
        }

        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);
        getUserOrThrow(recipient); // Проверка существования получателя

        if (sender.getBalance() < amount) {
            throw new IllegalStateException("Недостаточно средств на счете");
        }

        String transferId = UUID.randomUUID().toString();

        sender.setBalance(sender.getBalance() - amount);
        users.put(caller, sender);

        TransferItem transferItem = new TransferItem(transferId, caller, recipient, amount, timeLive, ACTIVE);
        transfers.put(transferId, transferItem);
    }

    @ContractAction
    @Override
    ///  Принять денежный перевод
    public void getTransfer(String transferId) {
        String caller = call.getCaller();
        User recipientUser = getUserOrThrow(caller);
        TransferItem transferItem = getTransferOrThrow(transferId);

        if (!caller.equals(transferItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого перевода");
        }

        if (!ACTIVE.equals(transferItem.getStatus())) {
            throw new IllegalStateException("Перевод недоступен для получения");
        }

        if (transferItem.isExpired()) {
            throw new IllegalStateException("Время жизни перевода истекло");
        }

        recipientUser.setBalance(recipientUser.getBalance() + transferItem.getAmount());
        transferItem.setStatus(ACCEPTED);

        users.put(caller, recipientUser);
        transfers.put(transferId, transferItem);
    }

    @ContractAction
    @Override
    /// Отказаться от денежного перевода
    public void refuseTransfer(String transferId) {
        String caller = call.getCaller();
        TransferItem transferItem = getTransferOrThrow(transferId);

        if (!caller.equals(transferItem.getRecipient())) {
            throw new IllegalStateException("Вы не являетесь получателем этого перевода");
        }

        if (!ACTIVE.equals(transferItem.getStatus())) {
            throw new IllegalStateException("Нельзя отказаться от этого перевода");
        }

        // Возврат средств отправителю
        User sender = getUserOrThrow(transferItem.getSender());
        sender.setBalance(sender.getBalance() + transferItem.getAmount());
        transferItem.setStatus(REFUSE);

        users.put(transferItem.getSender(), sender);
        transfers.put(transferId, transferItem);
    }

    @ContractAction
    @Override
    public List<PostItem> getUserSentItems(String userAddress) {
        // Используем tryGet вместо getOrDefault
        Optional<List<String>> trackNumbersOpt = userItems.tryGet(userAddress);
        List<String> trackNumbers = trackNumbersOpt.orElse(new ArrayList<>());

        List<PostItem> result = new ArrayList<>();

        for (String trackNumber : trackNumbers) {
            Optional<PostItem> itemOpt = postals.tryGet(trackNumber);
            itemOpt.ifPresent(result::add);
        }

        return result;
    }

    @ContractAction
    @Override
    public List<PostItem> getActivePostItems(String userAddress) {
        Optional<List<String>> trackNumbersOpt = userItems.tryGet(userAddress);
        List<String> trackNumbers = trackNumbersOpt.orElse(new ArrayList<>());

        List<PostItem> result = new ArrayList<>();

        for (String trackNumber : trackNumbers) {
            Optional<PostItem> itemOpt = postals.tryGet(trackNumber);
            if (itemOpt.isPresent() && ACTIVE.equals(itemOpt.get().status)) {
                result.add(itemOpt.get());
            }
        }

        return result;
    }

}