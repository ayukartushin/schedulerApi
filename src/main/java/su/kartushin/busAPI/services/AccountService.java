package su.kartushin.busAPI.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.jmx.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import okhttp3.Response;
import su.kartushin.busAPI.enums.Action;
import su.kartushin.busAPI.enums.Status;
import su.kartushin.busAPI.objects.Account;
import su.kartushin.busAPI.objects.ResponseObject;
import su.kartushin.busAPI.objects.User;
import su.kartushin.busAPI.objects.VPNProxy;
import su.kartushin.busAPI.utils.HttpExecute;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class AccountService extends CrudService<Account, Long> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Optional<List<Account>> findAll(String requestId) {
        return Optional.of(accountRepository.findAll());
    }

    @Override
    public Optional<Account> findById(Long id, String requestId){
        return accountRepository.findById(id);
    }

    public Optional<Account> findByName(String name, long sId, String requestId){
        if (name == null) {
            log.error("Не передан идентефикатор чата.");
            return Optional.empty();
        }

        Optional<User> optUser = userRepository.findByChatId(name);

        //Проверка что пользователь существует и его получение
        if (optUser.isEmpty()) {
            log.error("Пользователь с ChatId {} не найден.", name);
            return Optional.empty();
        }

        //Проверка что сервер существует и его получение
        if (!serverRepository.existsById(sId)) {
            log.error("Сервер с ID {} не найден.", sId);
            return Optional.empty();
        }

        VPNProxy server = serverRepository.findById(sId).get();

        User user = optUser.get();
        user.getAccountIds();
        Optional<Account> accountWithServerId = user.getAccountIds().stream()
                .filter(account -> account.getServer() != null && account.getServer().getId() == sId)
                .findFirst();

        if (accountWithServerId.isPresent()) {
            // Обработка найденного аккаунта
            log.info("Найден аккаунт с нужным server id: " + accountWithServerId.get());
            return accountWithServerId;
        } else {
            Response response = null;
            HttpExecute httpExecute
                    = new HttpExecute(server, getRequestId(requestId));
            try {
                response = httpExecute.getRequest(String.format("/api/account/%s", name));
                if (!response.isSuccessful()){
                    log.info("Аккаунт с server id {} не найден.", sId);
                    return  Optional.empty();
                }

                log.info("Тело ответа: {}", response.peekBody(Long.MAX_VALUE).string());
                ResponseObject<String> parsedObject
                        = objectMapper.readValue(response.body().string(), ResponseObject.class);

                return Optional.of(accountRepository.save(Account.builder()
                        .country(server.getCountry())
                        .chatId(name)
                        .server(server)
                        .status(Status.ACTIVE)
                        .idOnServer(parsedObject.getData())
                        .build()));
            } catch (Exception e) {
                // Обработка случая, когда аккаунт с нужным server id не найден
                log.error("Аккаунт с server id {} не найден.", sId, e);
                return Optional.empty();
            } finally {
                if (response != null) {
                    log.info("Закрываю соединение");
                    response.close();
                }
            }
        }
    }

    @Override
    public Optional<Account> save(Account account, String requestId) {
        if (account.getChatId() == null) {
            log.error("Не передан идентефикатор чата.");
            return Optional.empty();
        }

        Optional<User> optUser = userRepository.findByChatId(account.getChatId());

        //Проверка что пользователь существует и его получение
        if (optUser.isEmpty()) {
            log.error("Пользователь с ChatId {} не найден.", account.getChatId());
            return Optional.empty();
        }

        //Проверка что сервер существует и его получение
        if (!serverRepository.existsById(account.getServer().getId())) {
            log.error("Сервер с ID {} не найден.", account.getServer().getId());
            return Optional.empty();
        }

        var result = account;
        VPNProxy server = serverRepository.getById(account.getServer().getId());
        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));
        Response response = null;

        //Проверка что этого аккаунта нет на сервере
        response = httpExecute.getRequest(String.format("/api/account/%s", account.getChatId()));
        if(response != null){
            log.error(
                    String.format("У данного пользователя уже есть аккаунт на сервере с ID {}, и URL {}",
                            server.getId(), server.getUrl()));
            response.close();
            return Optional.empty();
        }
        response = null;

        //Запрос на создание аккаунта
        try {
            response = httpExecute.postRequest("", String.format("/api/account/%s", account.getChatId()));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Создание произошло с ошибкой на сервере с ID {}.", account.getServer().getId());
                return Optional.empty();
            }

            // Чтение тела ответа
            log.info("Тело ответа: {}", response.peekBody(Long.MAX_VALUE).string());
            ResponseObject<String> parsedObject
                    = objectMapper.readValue(response.body().string(), ResponseObject.class);
            log.info("Обработанный ответ: {}", parsedObject);

            result.setIdOnServer(parsedObject.getData());
            result.setServer(server);
            result = accountRepository.save(result);

            //обновление пользователя
            User user = optUser.get();
            user.getAccountIds().add(account);
            userRepository.save(user);

            return Optional.of(result);

        } catch (IOException e) {
            log.error("Ошибка при обработке ответа от сервера", e);
            return Optional.empty();
        } finally {
            if (response != null) {
                log.info("Закрываю соединение");
                response.close();
            }
        }
    }

    @Override
    public Optional<Account> update(Long id, Account accountDetails, String requestId) {
        return accountRepository.findById(id).map(existingAccount -> {
            existingAccount.setChatId(accountDetails.getChatId());
            existingAccount.setIdOnServer(accountDetails.getIdOnServer());
            existingAccount.setServerName(accountDetails.getServerName());
            existingAccount.setServer(accountDetails.getServer());
            existingAccount.setCountry(accountDetails.getCountry());
            existingAccount.setStatus(accountDetails.getStatus());

            try {
                var updatedAccount = accountRepository.save(existingAccount);
                log.info("Account с ID {} успешно обновлен: {}", id, updatedAccount);
                return updatedAccount;
            } catch (Exception e) {
                log.error("Ошибка при обновлении Account с ID {}", id, e);
                return null;
            }
        });
    }

    @Override
    public Boolean deleteById(Long id, String requestId) {
        try{
            log.info("Удаление сервера.");
            var optAccount = accountRepository.findById(id);
            if (optAccount.isEmpty()){
                log.error("Аккаунт с id {} не найден.", id);
                return false;
            }

            var account = optAccount.get();
            var server = serverRepository.getById(account.getServer().getId());
            HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));
            Response response = null;

            try {
                response = httpExecute
                        .deleteRequest("",
                                String.format("/api/account/%s", account.getIdOnServer()));
                log.debug("Ответ от сервера с кодом: {}", response.code());

                if (!response.isSuccessful()) {
                    log.error("Удаление произошло с ошибкой на сервере с ID {}.", account.getServer().getId());
                    return false;
                }

                // Чтение тела ответа
                log.info("Тело ответа: {}", response.peekBody(Long.MAX_VALUE).string());
                ResponseObject<Boolean> parsedObject
                        = objectMapper.readValue(response.body().string(), ResponseObject.class);
                log.info("Обработанный ответ: {}", parsedObject);

                account.setStatus(Status.DELETED);
                accountRepository.save(account);

                return true;
            } catch (IOException e) {
                log.error("Ошибка при обработке ответа от сервера", e);
                return false;
            } finally {
                if (response != null) {
                    log.info("Закрываю соединение");
                    response.close();
                }
            }

        } catch (Exception e) {
            log.error("Удаление не получилось");
            return false;
        }
    }

    public Optional<Boolean> action(Account account, String requestId, Action action){
        log.info("Работа над сервером {}", action);
        if (account.getChatId() == null) {
            log.error("Не передан идентефикатор чата.");
            return Optional.empty();
        }

        Optional<User> optUser = userRepository.findByChatId(account.getChatId());

        //Проверка что пользователь существует и его получение
        if (optUser.isEmpty()) {
            log.error("Пользователь с ChatId {} не найден.", account.getChatId());
            return Optional.empty();
        }

        //Проверка что сервер существует и его получение
        if (!serverRepository.existsById(account.getServer().getId())) {
            log.error("Сервер с ID {} не найден.", account.getServer().getId());
            return Optional.empty();
        }

        String uri = "";
        switch (action){
            case Action.BLOCK:{
                uri = "/api/account/block/%s";
                break;
            }
            case Action.UNBLOCK:{
                uri = "/api/account/unblock/%s";
                break;
            }
            case Action.RESTART:{
                uri = "/api/account/restart/%s";
                break;
            }
            default:{
                log.error("Не известно что делать с сервером");
                return Optional.of(false);
            }
        }

        var server = serverRepository.getById(account.getServer().getId());
        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));
        Response response = null;

        try {
            response = httpExecute.postRequest("", String.format(uri, account.getIdOnServer()));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Действие {} произошло с ошибкой на сервере с ID {}.", action, account.getServer().getId());
                return Optional.of(false);
            }

            // Чтение тела ответа
            log.info("Тело ответа: {}", response.peekBody(Long.MAX_VALUE).string());
            ResponseObject<Boolean> parsedObject
                    = objectMapper.readValue(response.body().string(), ResponseObject.class);
            log.info("Обработанный ответ: {}", parsedObject);

            return Optional.of(response.isSuccessful());
        } catch (IOException e) {
            log.error("Ошибка при обработке ответа от сервера", e);
            return Optional.of(false);
        } finally {
            if (response != null) {
                log.info("Закрываю соединение");
                response.close();
            }
        }
    }
}
