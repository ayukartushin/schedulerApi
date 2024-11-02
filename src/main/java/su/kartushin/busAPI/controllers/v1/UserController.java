package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.kartushin.busAPI.objects.ResponseObject;
import su.kartushin.busAPI.objects.User;
import su.kartushin.busAPI.services.UserService;

import static su.kartushin.busAPI.utils.AuthCheck.incorrectAuthorization;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller", description = "API для работы с Пользователями")
@Log4j2
public class UserController extends AbstractCrudController<User, Long> {

    public UserController(){
        service = new UserService();
        entity = "User";
    }

    /**
     * Получить сущность по chatID.
     *
     * @param chatId         Идентификатор сущности
     * @param authHeader Заголовок авторизации
     * @param requestID  Заголовок requestID
     * @return Ответ с объектом {@link ResponseObject}, содержащим найденную сущность или сообщение об ошибке
     */
    @Operation(summary = "Получить сущность по chatID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не найдена")
    })
    @GetMapping("/chatId/{chatId}")
    public ResponseEntity<ResponseObject<User>> findByChatId(
            @Parameter(description = "Идентификатор сущности", required = true)
            @PathVariable("chatId") String chatId,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID
    ){
        log.info("Запрос на получение сущности {} с ID {}", entity, chatId);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /{}", chatId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        return ((UserService)service).findByChatId(chatId)
                .map(data -> {
                    log.info("{} с ID {} успешно найден", entity, chatId);
                    return ResponseEntity.ok(new ResponseObject<>("success",
                            String.format("%s успешно получен", entity),
                            data));
                })
                .orElseGet(() -> {
                    log.warn("{} с ID {} не найден", entity, chatId);
                    return ResponseEntity.status(404).body(
                            new ResponseObject<>("error", String.format("%s не найден", entity)));
                });
    }
}
