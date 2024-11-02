package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.kartushin.busAPI.objects.ResponseObject;
import su.kartushin.busAPI.services.ICrudService;

import java.util.List;

import static su.kartushin.busAPI.utils.AuthCheck.incorrectAuthorization;

/**
 * Абстрактный контроллер для выполнения CRUD-операций над сущностями.
 *
 * @param <T>  Тип сущности
 * @param <ID> Тип идентификатора сущности
 */
@Log4j2
public abstract class AbstractCrudController<T, ID> {

    @Autowired(required = false)
    protected ICrudService<T, ID> service;

    @Autowired(required = false)
    protected String entity = "Сущность";

    /**
     * Получить все сущности.
     *
     * @param authHeader Заголовок авторизации
     * @param requestID  Заголовок requestID
     * @return Ответ с объектом {@link ResponseObject}, содержащим список сущностей
     */
    @Operation(summary = "Получить все сущности")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущности не найдены")
    })
    @GetMapping
    public ResponseEntity<ResponseObject<List<T>>> findAll(
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID) {
        log.info("Запрос на получение всех сущностей: {}", entity);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /");
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        return service.findAll(requestID)
                .map(data -> {
                    log.info("Успешное получение всех {}: найдено {} записей", entity, data.size());
                    return ResponseEntity.ok(new ResponseObject<>("success",
                            String.format("%s успешно получены", entity),
                            data));
                })
                .orElseGet(() -> {
                    log.warn("Сущности {} не найдены", entity);
                    return ResponseEntity.status(404).body(
                            new ResponseObject<>("error", String.format("%s не найдены", entity)));
                });
    }

    /**
     * Получить сущность по ID.
     *
     * @param id         Идентификатор сущности
     * @param authHeader Заголовок авторизации
     * @param requestID  Заголовок requestID
     * @return Ответ с объектом {@link ResponseObject}, содержащим найденную сущность или сообщение об ошибке
     */
    @Operation(summary = "Получить сущность по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<T>> findById(
            @Parameter(description = "Идентификатор сущности", required = true)
            @PathVariable("id") ID id,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID) {
        log.info("Запрос на получение сущности {} с ID {}", entity, id);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /{}", id);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        return service.findById(id, requestID)
                .map(data -> {
                    log.info("{} с ID {} успешно найден", entity, id);
                    return ResponseEntity.ok(new ResponseObject<>("success",
                            String.format("%s успешно получен", entity),
                            data));
                })
                .orElseGet(() -> {
                    log.warn("{} с ID {} не найден", entity, id);
                    return ResponseEntity.status(404).body(
                            new ResponseObject<>("error", String.format("%s не найден", entity)));
                });
    }

    /**
     * Создать новую сущность.
     *
     * @param entity     Сущность для создания
     * @param authHeader Заголовок авторизации
     * @param requestID  Заголовок requestID
     * @return Ответ с объектом {@link ResponseObject}, содержащим созданную сущность или сообщение об ошибке
     */
    @Operation(summary = "Создать новую сущность")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "400", description = "Сущность не создана")
    })
    @PostMapping
    public ResponseEntity<ResponseObject<T>> create(
            @Parameter(description = "Сущность для создания", required = true)
            @RequestBody T entity,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID) {
        log.info("Запрос на создание новой сущности {}", this.entity);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для создания /{}", entity);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        return service.save(entity, requestID)
                .map(data -> {
                    log.info("Сущность {} успешно создана: {}", this.entity, data);
                    return ResponseEntity.ok(new ResponseObject<>("success",
                            String.format("%s создан", this.entity),
                            data));
                })
                .orElseGet(() -> {
                    log.error("Не удалось создать сущность {}", this.entity);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new ResponseObject<>("error", String.format("%s не создан", this.entity)));
                });
    }

    /**
     * Обновить сущность по ID.
     *
     * @param id         Идентификатор сущности
     * @param entity     Обновленные данные сущности
     * @param authHeader Заголовок авторизации
     * @param requestID  Заголовок requestID
     * @return Ответ с объектом {@link ResponseObject}, содержащим обновленную сущность или сообщение об ошибке
     */
    @Operation(summary = "Обновить сущность")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<T>> update(
            @Parameter(description = "Идентификатор сущности", required = true)
            @PathVariable("id") ID id,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID,
            @RequestBody T entity) {
        log.info("Запрос на обновление сущности {} с ID {}", this.entity, id);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для обновления /{}", id);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        return service.update(id, entity, requestID)
                .map(updatedEntity -> {
                    log.info("Сущность {} с ID {} успешно обновлена", this.entity, id);
                    return ResponseEntity.ok(new ResponseObject<>("success",
                            String.format("%s успешно обновлен", this.entity),
                            updatedEntity));
                })
                .orElseGet(() -> {
                    log.warn("Сущность {} с ID {} не найдена", this.entity, id);
                    return ResponseEntity.status(404).body(
                            new ResponseObject<>("error", String.format("%s не найден", this.entity)));
                });
    }


    /**
     * Удалить сущность по ID.
     *
     * @param id         Идентификатор сущности
     * @param authHeader Заголовок авторизации
     * @param requestID  Заголовок requestID
     * @return Ответ с объектом {@link ResponseObject}, указывающим результат операции
     */
    @Operation(summary = "Удалить сущность")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Object>> delete(
            @Parameter(description = "Идентификатор сущности", required = true)
            @PathVariable("id") ID id,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID) {
        log.info("Запрос на удаление сущности {} с ID {}", this.entity, id);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /{}", id);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        boolean deleted = service.deleteById(id, requestID);

        if (deleted) {
            log.info("Сущность {} с ID {} успешно удалена", this.entity, id);
            return ResponseEntity.ok(new ResponseObject<>("success", String.format("%s успешно удален", this.entity)));
        } else {
            log.warn("Сущность {} с ID {} не найдена", this.entity, id);
            return ResponseEntity.status(404).body(new ResponseObject<>("error", String.format("%s не найден", this.entity)));
        }
    }
}