package com.wavesenterprise.app.app;

import com.wavesenterprise.app.api.IContract;
import com.wavesenterprise.app.domain.PostItem;
import com.wavesenterprise.app.domain.Transfer;
import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.sdk.contract.api.annotation.*;
import com.wavesenterprise.sdk.contract.api.domain.ContractCall;
import com.wavesenterprise.sdk.contract.api.state.ContractState;
import com.wavesenterprise.sdk.contract.api.state.TypeReference;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wavesenterprise.app.api.IContract.Keys.*;
import static com.wavesenterprise.app.api.IContract.Role.*;

/**
 * Реализация контракта почтового сервиса с правильным использованием TypeReference
 */
@ContractHandler
public class Contract implements IContract {

    private final ContractState state;
    private final ContractCall call;

    // Маппинги с TypeReference для generic типов
    private final Mapping<User> users;
    private final Mapping<PostItem> postItems;
    private final Mapping<Transfer> transfers;
    private final Mapping<AtomicInteger> dailyCounter;
    private final Mapping<List<String>> userPostalItems;
    private final Mapping<Boolean> activeTransfers;
    private final Mapping<Boolean> usedTransfers;

    public Contract(ContractState state, ContractCall call) {
        this.state = state;
        this.call = call;

        // инициализация маппингов
        this.users = state.getMapping(new TypeReference<User>() {}, USERS_MAPPING);
        this.postItems = state.getMapping(new TypeReference<PostItem>() {}, POST_ITEMS_MAPPING);
        this.transfers = state.getMapping(new TypeReference<Transfer>() {}, TRANSFERS_MAPPING);
        this.dailyCounter = state.getMapping(new TypeReference<AtomicInteger>() {}, DAILY_COUNTER);
        this.userPostalItems = state.getMapping(new TypeReference<List<String>>() {}, USER_POSTAL_ITEMS);
        this.activeTransfers = state.getMapping(new TypeReference<Boolean>() {}, ACTIVE_TRANSFERS);
        this.usedTransfers = state.getMapping(new TypeReference<Boolean>() {}, USED_TRANSFERS);
    }

    @Override
    @ContractInit
    public void init() {
        // Создаем администратора системы
        User admin = createUser(
                "Семенов Семен Семенович",
                "Ростов-на-Дону, ул. Административная, 1",
                call.getCaller(),
                50.0,
                ADMIN,
                "344000"
        );

        users.put(call.getCaller(), admin);
        state.put("CONTRACT_OWNER", call.getCaller());
    }

    // ===== ОСНОВНЫЕ МЕТОДЫ =====

    @Override // зарегистрироваться
    @ContractAction
    public void registerUser(String name, String homeAddress, String role) {
        String caller = call.getCaller();

        if (users.tryGet(caller).isPresent()) {
            throw new IllegalStateException("Пользователь уже зарегистрирован");
        }

        User newUser = createUser(name, homeAddress, caller, 50.0, USER, null);
        users.put(caller, newUser);
    }

    @Override // изменить информацию о себе
    @ContractAction
    public void updateUserInfo(String name, String homeAddress) {
        String caller = call.getCaller();
        User user = getUserOrThrow(caller);
        user.setName(name);
        user.setHomeAddress(homeAddress);
        users.put(caller, user);
    }

    @Override
    @ContractAction
    public Optional<User> getUserInfo(String blockchainAddress) {
        return users.tryGet(blockchainAddress);
    }

    // ===== АДМИНИСТРАТИВНЫЕ ФУНКЦИИ =====

    @Override // добавить сотрудника
    @ContractAction
    public void addEmployee(String userAddress, String postId) {
        checkAdminRights();
        User user = getUserOrThrow(userAddress);
        user.setRole(EMPLOYEE);
        user.setPostId(postId);
        users.put(userAddress, user);
    }

    @Override // удалить сотрудника
    @ContractAction
    public void removeEmployee(String userAddress) {
        checkAdminRights();
        User user = getUserOrThrow(userAddress);

        user.setRole(USER);
        user.setPostId(null);
        users.put(userAddress, user);
    }

    @Override // изменения айди почтового отделения для сотрудника
    @ContractAction
    public void setEmployeePostId(String employeeAddress, String newPostId) {
        checkAdminRights();
        User employee = getUserOrThrow(employeeAddress);
        employee.setPostId(newPostId);
        users.put(employeeAddress, employee);
    }

    // ===== ПОЧТОВЫЕ ОТПРАВЛЕНИЯ =====

    @Override  // отправить посылку
    @ContractAction
    public void sendPostal(String recipient, String type, byte departureClass,
                           int weight, int declaredValue, String destinationAddress) {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        validatePostalData(weight, departureClass);
        String trackNumber = generateTrackNumber(destinationAddress);
        PostalCost cost = calculatePostalCost(departureClass, weight, declaredValue);

        if (sender.getBalance() < cost.totalCost) {
            throw new IllegalStateException("Недостаточно средств на балансе");
        }

        // Создаем и сохраняем отправление
        PostItem postItem = createPostItem(trackNumber, caller, recipient, type, departureClass,
                weight, declaredValue, destinationAddress,
                sender.getHomeAddress(), cost);

        // Списание средств
        sender.setBalance(sender.getBalance() - cost.totalCost);
        users.put(caller, sender);
        postItems.put(trackNumber, postItem);

        // Добавляем в историю
        addToUserHistory(caller, trackNumber);
        addToUserHistory(recipient, trackNumber);
    }

    @Override // записать отправление в блокчейн
    @ContractAction
    public void recordPostal(String recipient, String type, byte departureClass,
                             int weight, int declaredValue, String destinationAddress) {
        String caller = call.getCaller();
        User employee = getUserOrThrow(caller);

        if (EMPLOYEE.equals(employee.getRole())) {
            throw new IllegalStateException("Только сотрудник может записывать отправления");
        }

        if (employee.getPostId() == null) {
            throw new IllegalStateException("У сотрудника не установлен идентификатор отделения");
        }

        validatePostalData(weight, departureClass);
        String trackNumber = generateTrackNumber(destinationAddress);
        PostalCost cost = calculatePostalCost(departureClass, weight, declaredValue);

        PostItem postItem = createPostItem(trackNumber, caller, recipient, type, departureClass,
                weight, declaredValue, destinationAddress,
                employee.getHomeAddress(), cost);

        postItems.put(trackNumber, postItem);
        addToUserHistory(caller, trackNumber);
        addToUserHistory(recipient, trackNumber);
    }

    @Override // получить отправление
    @ContractAction
    public void receivePostal(String trackNumber) {
        String caller = call.getCaller();
        PostItem postItem = getPostItemOrThrow(trackNumber);

        if (caller.equals(postItem.getRecipient())) {// -------------
            throw new IllegalStateException("Вы не являетесь получателем этого отправления");
        }
    }

    @Override // отслеживаем отправление по трек номеру
    @ContractAction
    public Optional<PostItem> trackDeparture(String trackNumber) {
        return postItems.tryGet(trackNumber);
    }

    @Override // история отправлений
    @ContractAction
    public List<PostItem> getHistoryDeparture() {
        String caller = call.getCaller();
        List<String> userTrackNumbers = userPostalItems.tryGet(caller).orElse(Collections.emptyList());
        List<PostItem> result = new ArrayList<>();

        for (String trackNumber : userTrackNumbers) {
            Optional<PostItem> postItemOpt = postItems.tryGet(trackNumber);
            if (postItemOpt.isPresent()) {
                PostItem item = postItemOpt.get();
                if (caller.equals(item.getSender()) || caller.equals(item.getRecipient())) {// -------
                    result.add(item);
                }
            }
        }

        return result;
    }

    // ===== ДЕНЕЖНЫЕ ПЕРЕВОДЫ =====

    @Override // отправить денежный перевод
    @ContractAction
    public void sendTransfer(String recipient, int amount, int liveTime) {
        String caller = call.getCaller();
        User sender = getUserOrThrow(caller);

        if (sender.getBalance() < amount) {
            throw new IllegalStateException("Недостаточно средств на балансе");
        }

        // Списание средств
        sender.setBalance(sender.getBalance() - amount);
        users.put(caller, sender);

        // Создание перевода
        String transferId = UUID.randomUUID().toString();
        Transfer transfer = new Transfer(caller, recipient, amount, liveTime);

        transfers.put(transferId, transfer);
        activeTransfers.put(transferId, true);
    }

    @Override // получить перевод
    @ContractAction
    public void receiveTransfer(String transferId) {
        String caller = call.getCaller();
        Transfer transfer = getTransferOrThrow(transferId);

        if (caller.equals(transfer.getRecipient())) { // ----------
            throw new IllegalStateException("Вы не являетесь получателем этого перевода");
        }

        if (usedTransfers.tryGet(transferId).isPresent()) {
            throw new IllegalStateException("Перевод уже был получен");
        }

        // Зачисление средств
        User recipientUser = getUserOrThrow(caller);
        recipientUser.setBalance(recipientUser.getBalance() + transfer.getValue());
        users.put(caller, recipientUser);

        // Помечаем перевод как использованный
        usedTransfers.put(transferId, true);
        activeTransfers.put(transferId, false);
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    // создать пользователя
    private User createUser(String name, String homeAddress, String blockchainAddress,
                            double balance, String role, String postId) {
        User user = new User();
        user.setName(name);
        user.setHomeAddress(homeAddress);
        user.setBlockchainAddress(blockchainAddress);
        user.setBalance(balance);
        user.setRole(role);
        if (postId != null) {
            user.setPostId(postId);
        }
        return user;
    }
    //  создать почтовое отправление
    private PostItem createPostItem(String trackNumber, String sender, String recipient, String type,
                                    byte departureClass, int weight, int declaredValue,
                                    String destinationAddress, String departureAddress, PostalCost cost) {
        PostItem item = new PostItem();
        item.setTrackNumber(Integer.parseInt(trackNumber.substring(2)));
        item.setSender(sender);
        item.setRecipient(recipient);
        item.setType(type);
        item.setDepartureClass(departureClass);
        item.setDeliveryTime(cost.deliveryTime);
        item.setPrice(cost.pricePerKg);
        item.setWeight(weight);
        item.setDeclaredValue(declaredValue);
        item.setTotalCost(cost.totalCost);
        item.setDestinationAddress(destinationAddress);
        item.setDepartureAddress(departureAddress);
        return item;
    }
    // проверка на админа
    private void checkAdminRights() {
        User user = getUserOrThrow(call.getCaller());
        if (ADMIN.equals(user.getRole())) {
            throw new IllegalStateException("Только администратор может выполнять это действие");
        }
    }

    private User getUserOrThrow(String address) {
        return users.tryGet(address)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + address));
    }

    private PostItem getPostItemOrThrow(String trackNumber) {
        return postItems.tryGet(trackNumber)
                .orElseThrow(() -> new IllegalStateException("Отправление не найдено: " + trackNumber));
    }

    private Transfer getTransferOrThrow(String transferId) {
        return transfers.tryGet(transferId)
                .orElseThrow(() -> new IllegalStateException("Перевод не найден: " + transferId));
    }

    private void validatePostalData(int weight, byte departureClass) {
        if (weight > 10) {
            throw new IllegalStateException("Вес отправления не может превышать 10 кг");
        }
        if (departureClass < 1 || departureClass > 3) {
            throw new IllegalStateException("Класс отправления должен быть 1, 2 или 3");
        }
    }

    private String generateTrackNumber(String destinationAddress) {
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        String counterKey = "COUNTER_" + dateStr;
        AtomicInteger counter = dailyCounter.tryGet(counterKey).orElse(new AtomicInteger(0));
        int dailyNumber = counter.incrementAndGet();
        dailyCounter.put(counterKey, counter);

        String departureIndex = "344000";
        String destinationIndex = extractPostalCode(destinationAddress);

        return "RR" + dateStr + dailyNumber + departureIndex + destinationIndex;
    }

    private String extractPostalCode(String address) {
        String[] postalCodes = {
                "344000", "347900", "347901", "347902", "347903",
                "346770", "346771", "346772", "346773", "343760",
                "343761", "343762", "343763", "346780", "346781",
                "346782", "346783"
        };

        for (String code : postalCodes) {
            if (address.contains(code)) {
                return code;
            }
        }
        return "000000";
    }

    private PostalCost calculatePostalCost(byte departureClass, int weight, int declaredValue) {
        int pricePerKg;
        int deliveryTime;

        switch (departureClass) {
            case 1:
                pricePerKg = 50;
                deliveryTime = 5;
                break;
            case 2:
                pricePerKg = 30;
                deliveryTime = 10;
                break;
            case 3:
            default:
                pricePerKg = 10;
                deliveryTime = 15;
                break;
        }

        int totalCost = (pricePerKg * weight) + (int)(declaredValue * 0.1);
        return new PostalCost(pricePerKg, deliveryTime, totalCost);
    }

    private void addToUserHistory(String userAddress, String trackNumber) {
        List<String> items = userPostalItems.tryGet(userAddress).orElse(new ArrayList<>());
        if (!items.contains(trackNumber)) {
            items.add(trackNumber);
            userPostalItems.put(userAddress, items);
        }
    }

    // Вспомогательный класс для стоимости отправления
    private static class PostalCost {
        final int pricePerKg;
        final int deliveryTime;
        final int totalCost;

        PostalCost(int pricePerKg, int deliveryTime, int totalCost) {
            this.pricePerKg = pricePerKg;
            this.deliveryTime = deliveryTime;
            this.totalCost = totalCost;
        }
    }
}