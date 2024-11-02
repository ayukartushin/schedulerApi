package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.kartushin.busAPI.objects.Config;
import su.kartushin.busAPI.objects.ResponseObject;
import su.kartushin.busAPI.services.ConfigService;

import java.util.List;
import java.util.Optional;

import static su.kartushin.busAPI.utils.AuthCheck.incorrectAuthorization;

@RestController
@RequestMapping("/api/v1/account/{aId}/config")
@Tag(name = "Config Controller", description = "API для работы с Конфигами")
@Log4j2
public class ConfigController {

    @Autowired(required = false)
    protected ConfigService service;

    public ConfigController(){
        service = new ConfigService();
    }

    /**
     * Получить все конфиги в аккаунте.
     */
    @Operation(summary = "Получить все конфиги в аккаунте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Конфиги не найдены")
    })
    @GetMapping("/")
    public ResponseEntity<ResponseObject<List<Config>>> getAllConfigs(
            @Parameter(description = "ID аккаунта") @PathVariable("aId") long aId,
            @Parameter(description = "Заголовок авторизации") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID") @RequestHeader(value = "requestID", required = false) String requestID
    ) {
        log.info("Запрос на получение всех конфигов для аккаунта с ID {}", aId);
        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для получения конфигов аккаунта с ID {}", aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        Optional<List<Config>> result = service.findAll(aId, requestID);
        return result.map(configs -> {
            log.info("Конфиги успешно получены для аккаунта с ID {}", aId);
            return ResponseEntity.ok(new ResponseObject<>("success", "Конфиги успешно получены", configs));
        }).orElseGet(() -> {
            log.error("Конфиги не найдены для аккаунта с ID {}", aId);
            return ResponseEntity.status(404).body(new ResponseObject<>("error", "Конфиги не найдены"));
        });
    }

    /**
     * Получить конфиг по имени.
     */
    @Operation(summary = "Получить конфиг по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Конфиг не найден")
    })
    @GetMapping("/{name}")
    public ResponseEntity<ResponseObject<Config>> getConfigByName(
            @Parameter(description = "ID аккаунта") @PathVariable("aId") long aId,
            @Parameter(description = "Имя конфига") @PathVariable("name") String name,
            @Parameter(description = "Заголовок авторизации") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID") @RequestHeader(value = "requestID", required = false) String requestID
    ) {
        log.info("Запрос на получение конфига {} для аккаунта с ID {}", name, aId);
        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для получения конфига {} аккаунта с ID {}", name, aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        Optional<Config> result = service.findByName(aId, name, requestID);
        return result.map(config -> {
            log.info("Конфиг {} успешно найден для аккаунта с ID {}", name, aId);
            return ResponseEntity.ok(new ResponseObject<>("success", "Конфиг успешно найден", config));
        }).orElseGet(() -> {
            log.error("Конфиг {} не найден для аккаунта с ID {}", name, aId);
            return ResponseEntity.status(404).body(new ResponseObject<>("error", "Конфиг не найден"));
        });
    }

    /**
     * Создать новый конфиг.
     */
    @Operation(summary = "Создать новый конфиг")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "400", description = "Конфиг не создан")
    })
    @PostMapping("/")
    public ResponseEntity<ResponseObject<Config>> createConfig(
            @Parameter(description = "ID аккаунта") @PathVariable("aId") long aId,
            @Parameter(description = "Объект конфига для создания") @RequestBody Config config,
            @Parameter(description = "Заголовок авторизации") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID") @RequestHeader(value = "requestID", required = false) String requestID
    ) {
        log.info("Запрос на создание нового конфига для аккаунта с ID {}", aId);
        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для создания конфига аккаунта с ID {}", aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        Optional<Config> result = service.save(aId, config, requestID);
        return result.map(savedConfig -> {
            log.info("Конфиг успешно создан для аккаунта с ID {}", aId);
            return ResponseEntity.ok(new ResponseObject<>("success", "Конфиг успешно создан", savedConfig));
        }).orElseGet(() -> {
            log.error("Ошибка при создании конфига для аккаунта с ID {}", aId);
            return ResponseEntity.status(400).body(new ResponseObject<>("error", "Ошибка при создании конфига"));
        });
    }

    /**
     * Переименовать конфиг.
     */
    @Operation(summary = "Переименовать конфиг")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Конфиг не найден")
    })
    @PutMapping("/{name}")
    public ResponseEntity<ResponseObject<Config>> renameConfig(
            @Parameter(description = "ID аккаунта") @PathVariable("aId") long aId,
            @Parameter(description = "Имя конфига") @PathVariable("name") String name,
            @Parameter(description = "Новое имя конфига") @RequestParam("newName") String newName,
            @Parameter(description = "Заголовок авторизации") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID") @RequestHeader(value = "requestID", required = false) String requestID
    ) {
        log.info("Запрос на переименование конфига {} для аккаунта с ID {}", name, aId);
        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для переименования конфига {} аккаунта с ID {}", name, aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        Optional<Config> result = service.update(aId, newName, Config.builder().name(name).build(), requestID); // Метод `rename` нужно реализовать в сервисе
        return result.map(updatedConfig -> {
            log.info("Конфиг {} успешно переименован в {} для аккаунта с ID {}", name, newName, aId);
            return ResponseEntity.ok(new ResponseObject<>("success", "Конфиг успешно переименован", updatedConfig));
        }).orElseGet(() -> {
            log.error("Конфиг {} не найден для аккаунта с ID {}", name, aId);
            return ResponseEntity.status(404).body(new ResponseObject<>("error", "Конфиг не найден"));
        });
    }

    /**
     * Удалить конфиг.
     */
    @Operation(summary = "Удалить конфиг")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Конфиг успешно удален"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Конфиг не найден")
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<ResponseObject<Object>> deleteConfig(
            @Parameter(description = "ID аккаунта") @PathVariable("aId") long aId,
            @Parameter(description = "Имя конфига") @PathVariable("name") String name,
            @Parameter(description = "Заголовок авторизации") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID") @RequestHeader(value = "requestID", required = false) String requestID
    ) {
        log.info("Запрос на удаление конфига {} для аккаунта с ID {}", name, aId);
        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для удаления конфига {} аккаунта с ID {}", name, aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        boolean deleted = service.deleteByName(aId, name, requestID);
        if (deleted) {
            log.info("Конфиг {} успешно удален для аккаунта с ID {}", name, aId);
            return ResponseEntity.ok(new ResponseObject<>("success", "Конфиг успешно удален"));
        } else {
            log.error("Конфиг {} не найден для аккаунта с ID {}", name, aId);
            return ResponseEntity.status(404).body(new ResponseObject<>("error", "Конфиг не найден"));
        }
    }

    /**
     * Получить файл конфига.
     */
    @Operation(summary = "Получить файл конфига")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно получен"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Конфиг не найден")
    })
    @GetMapping("/{name}/file")
    public ResponseEntity<ResponseObject> getConfigFile(
            @Parameter(description = "ID аккаунта") @PathVariable("aId") long aId,
            @Parameter(description = "Имя конфига") @PathVariable("name") String name,
            @Parameter(description = "Заголовок авторизации") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID") @RequestHeader(value = "requestID", required = false) String requestID
    ) {
        log.info("Запрос на получение файла конфига {} для аккаунта с ID {}", name, aId);
        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для получения файла конфига {} аккаунта с ID {}", name, aId);
            return ResponseEntity.status(403).build();
        }

        Optional<String> fileContent = service.getConfigFile(aId, name, requestID);
        return fileContent.map(content -> {
            log.info("Файл конфига {} успешно получен для аккаунта с ID {}", name, aId);
            return ResponseEntity.ok(new ResponseObject("success", "Файл получен", content));
        }).orElseGet(() -> {
            log.error("Файл конфига {} не найден для аккаунта с ID {}", name, aId);
            return ResponseEntity.status(404).body(new ResponseObject("error", "Файл не найден"));
        });
    }
}