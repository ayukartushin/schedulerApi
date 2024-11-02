package su.kartushin.busAPI.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.kartushin.busAPI.enums.Status;
import su.kartushin.busAPI.objects.Account;
import su.kartushin.busAPI.objects.Billing;
import su.kartushin.busAPI.objects.Config;
import su.kartushin.busAPI.objects.ResponseObject;
import su.kartushin.busAPI.repositories.AccountRepository;
import su.kartushin.busAPI.repositories.UserRepository;
import su.kartushin.busAPI.repositories.VPNProxyRepository;
import su.kartushin.busAPI.utils.HttpExecute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ConfigService {

    @Autowired
    protected VPNProxyRepository serverRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<List<Config>> findAll(Long accountId, String requestId) {
        var optAccount = accountRepository.findById(accountId);
        if(optAccount.isEmpty()){
            log.error("Нет такого аккаунта.");
            return Optional.empty();
        }

        var account = optAccount.get();
        var server = account.getServer();

        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));

        Response response = null;
        try {
            response = httpExecute.getRequest(String.format("/api/user/%s", account.getIdOnServer()));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Получение произошло с ошибкой на сервере с ID {}.", account.getServer().getId());
                return Optional.empty();
            }

            if (response.body() == null) {
                log.error("При получении произошла ошибкой на сервере с ID {}. Тело пустое.", account.getServer().getId());
                return Optional.empty();
            }

            ResponseObject<List<LinkedHashMap<String, String>>> parsedObject
                    = objectMapper.readValue(response.body().string(), ResponseObject.class);
            log.info("Обработанный ответ: {}", parsedObject);

            ArrayList<Config> configs = new ArrayList<>();
            for (var el : parsedObject.getData()){
                configs.add(
                        Config.builder()
                                .idOnServer(el.get("id"))
                                .account(account)
                                .name(el.get("name"))
                                .build());
            }

            return Optional.of(configs);
        } catch (Exception e) {
            log.error("При получении конфигов произошла ошибка", e);
            return Optional.empty();
        } finally {
            if(response != null)
                response.close();
        }
    }

    public Optional<Config> findByName(Long accountId, String name, String requestId) {
        var optAccount = accountRepository.findById(accountId);
        if(optAccount.isEmpty()){
            log.error("Нет такого аккаунта.");
            return Optional.empty();
        }

        var account = optAccount.get();
        var server = account.getServer();

        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));
        Response response = null;
        try {
            response = httpExecute.getRequest(String.format("/api/user/%s/%s", account.getIdOnServer(), name));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Получение произошло с ошибкой на сервере с ID {} и именем {}",
                        account.getServer().getId(),
                        name);
                return Optional.empty();
            }

            if (response.body() == null) {
                log.error("При получении произошла ошибкой на сервере с ID {}. Тело пустое.", account.getServer().getId());
                return Optional.empty();
            }

            var optConfigs = findAll(accountId, requestId);

            if (optConfigs.isEmpty()){
                return Optional.empty();
            }

            return optConfigs.get().stream()
                    .filter(user -> name.equals(user.getName()))
                    .findFirst();
        } catch (Exception e) {
            log.error("При получении конфигов произошла ошибка", e);
            return Optional.empty();
        } finally {
            if(response != null)
                response.close();
        }
    }

    public Optional<Config> save(Long accountId, Config entity, String requestId) {
        var optAccount = accountRepository.findById(accountId);
        if(optAccount.isEmpty()){
            log.error("Нет такого аккаунта.");
            return Optional.empty();
        }

        var account = optAccount.get();
        if (account.getStatus() == Status.DELETED) {
            log.error("Аккаунт удален. Создание не возможно.");
            return Optional.empty();
        }

        if (account.getStatus() == Status.DISACTIVE) {
            log.error("Аккаунт отключен. Создание не возможно.");
            return Optional.empty();
        }

        var server = account.getServer();

        if(findByName(accountId, entity.getName(), requestId).isPresent()){
            {
                log.error("Данное имя уже используется. Создание не возможно.");
                return Optional.empty();
            }
        }

        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));

        Response response = null;
        try {
            response = httpExecute.postRequest("",
                    String.format("/api/user/%s/%s", account.getIdOnServer(), entity.getName()));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Получение произошло с ошибкой на сервере с ID {}.", account.getServer().getId());
                return Optional.empty();
            }

            if (response.body() == null) {
                log.error("При получении произошла ошибкой на сервере с ID {}. Тело пустое.", account.getServer().getId());
                return Optional.empty();
            }

            ResponseObject<String> parsedObject
                    = objectMapper.readValue(response.body().string(), ResponseObject.class);
            log.info("Обработанный ответ: {}", parsedObject);

            return Optional.of(Config.builder()
                                .idOnServer(parsedObject.getData())
                                .account(account)
                                .name(entity.getName())
                                .build());
        } catch (Exception e) {
            log.error("При создании конфига произошла ошибка", e);
            return Optional.empty();
        } finally {
            if(response != null)
                response.close();
        }
    }

    public Optional<Config> update(Long accountId, String newName, Config entity, String requestId) {
        var optAccount = accountRepository.findById(accountId);
        if(optAccount.isEmpty()){
            log.error("Нет такого аккаунта.");
            return Optional.empty();
        }

        var account = optAccount.get();
        var server = account.getServer();

        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));
        var optEntity = findByName(accountId, entity.getName(), requestId);
        if (optEntity.isEmpty()){
            log.error("Пользователь не найден");
            return Optional.empty();
        }

        entity = optEntity.get();

        Response response = null;
        try {
            response = httpExecute.putRequest("",
                    String.format("/api/user/%s/%s/%s", account.getIdOnServer(), entity.getIdOnServer(), newName));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Получение произошло с ошибкой на сервере с ID {}.", account.getServer().getId());
                return Optional.empty();
            }

            return findByName(accountId, newName, requestId);
        } catch (Exception e) {
            log.error("При создании конфига произошла ошибка", e);
            return Optional.empty();
        } finally {
            if (response != null)
                response.close();
        }
    }

    public Boolean deleteByName(Long accountId, String name, String requestId) {
        var optAccount = accountRepository.findById(accountId);
        if(optAccount.isEmpty()){
            log.error("Нет такого аккаунта.");
            return false;
        }

        var account = optAccount.get();
        var server = account.getServer();

        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));
        var optEntity = findByName(accountId, name, requestId);
        if (optEntity.isEmpty()){
            log.error("Пользователь не найден");
            return false;
        }

        var entity = optEntity.get();

        Response response = null;
        try {
            response = httpExecute.deleteRequest("",
                    String.format("/api/user/%s/%s", account.getIdOnServer(), entity.getIdOnServer()));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Получение произошло с ошибкой на сервере с ID {}.", account.getServer().getId());
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("При создании конфига произошла ошибка", e);
            return false;
        } finally {
            if (response != null)
                response.close();
        }
    }

    public Optional<String> getConfigFile(Long accountId, String configName, String requestId) {
        var optAccount = accountRepository.findById(accountId);
        if (optAccount.isEmpty()) {
            log.error("Нет такого аккаунта.");
            return Optional.empty();
        }

        var account = optAccount.get();
        var server = account.getServer();

        HttpExecute httpExecute = new HttpExecute(server, getRequestId(requestId));
        var optEntity = findByName(accountId, configName, requestId);
        if (optEntity.isEmpty()) {
            log.error("Пользователь не найден");
            return Optional.empty();
        }

        var entity = optEntity.get();

        Response response = null;
        try {
            response = httpExecute.getRequest(
                    String.format("/api/user/config/%s/%s", account.getIdOnServer(), entity.getIdOnServer()));
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("Получение произошло с ошибкой на сервере с ID {}.", account.getServer().getId());
                return Optional.empty();
            }

            ResponseObject<String> parsedObject
                    = objectMapper.readValue(response.body().string(), ResponseObject.class);
            log.info("Обработанный ответ: {}", parsedObject);

            return Optional.of(parsedObject.getData());
        } catch (Exception e) {
            log.error("При создании конфига произошла ошибка", e);
            return Optional.empty();
        } finally {
            if (response != null)
                response.close();
        }
    }

    protected String getRequestId(String requestId){
        return (requestId == null || requestId.isEmpty()) ?
                "no requestId" :
                requestId;
    }
}
