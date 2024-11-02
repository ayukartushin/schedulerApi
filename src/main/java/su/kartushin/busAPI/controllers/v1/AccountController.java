package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.kartushin.busAPI.enums.Action;
import su.kartushin.busAPI.objects.Account;
import su.kartushin.busAPI.objects.ResponseObject;
import su.kartushin.busAPI.objects.VPNProxy;
import su.kartushin.busAPI.services.AccountService;
import su.kartushin.busAPI.services.ICrudService;

import static su.kartushin.busAPI.utils.AuthCheck.incorrectAuthorization;

@RestController
@RequestMapping("/api/v1/server/{sId}/account")
@Tag(name = "Account Controller", description = "API для работы с Аккаунтами")
@Log4j2
public class AccountController{

    @Autowired(required = false)
    protected AccountService service;

    public AccountController(){
        service = new AccountService();
    }

    @Operation(summary = "Получить аккаунт по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не найдена")
    })
    @GetMapping("/{name}")
    public ResponseEntity<ResponseObject<Account>> findByName(
            @Parameter(description = "ID сервера")
                @PathVariable("sId") long sId,
            @Parameter(description = "Имя сервера/аккаунта")
                @PathVariable("name") String name,
            @Parameter(description = "Заголовок авторизации")
                @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
                @RequestHeader(value = "requestID", required = false) String requestID
    ){
        log.info("Запрос на получение аккаунта с именем {}", name);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /api/v1/server/{}/account/{}", sId, name);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        var result = service.findByName(name, sId, requestID);

        if (result.isPresent()){
            log.info("Аккаунт найден.");
            return ResponseEntity.ok(
                    new ResponseObject<>("success", "Аккаунт найден.", result.get()));
        } else {
            log.info("Аккаунт не найден.");
            return ResponseEntity.status(404).body(
                    new ResponseObject<>("error", "Аккаунт не найден."));
        }
    }

    @Operation(summary = "Создать аккаунт по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не создана")
    })
    @PostMapping("/")
    public ResponseEntity<ResponseObject<Account>> createByName(
            @Parameter(description = "ID сервера")
            @PathVariable("sId") long sId,
            @Parameter(description = "Имя сервера/аккаунта")
            @RequestBody Account body,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID
    ){
        log.info("Запрос на создание аккаунта с телом {}", body);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /api/v1/server/{}/account/", sId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }
        body.setServer(VPNProxy.builder().id(sId).build());
        var result = service.save(body, requestID);
        if (result.isPresent())
            return ResponseEntity.ok(new ResponseObject<>("success","Аккаунт успешно создан", result.get()));
        else
            return ResponseEntity.status(404).body(new ResponseObject<>("error", "Ошибка при создании аккаунта"));
    }

    @Operation(summary = "Заблокировать аккаунт по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не создана")
    })
    @PostMapping("/block/{id}")
    public ResponseEntity<ResponseObject<Boolean>> blockById(
            @Parameter(description = "ID сервера")
            @PathVariable("sId") long sId,
            @Parameter(description = "ID сервера/аккаунта")
            @PathVariable("id") Long aId,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID
    ){
        log.info("Запрос на блокировку аккаунта с ID {}", aId);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /api/v1/server/{}/account/block/{}", sId, aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        var optAccount = service.findById(aId, requestID);
        if (optAccount.isEmpty()){
            log.error("Аккаунт с ID {} не найден", aId);
            return ResponseEntity
                    .status(404)
                    .body(new ResponseObject<>("error", "Данного аккаунта не существует."));
        }
        var account = optAccount.get();
        if (sId != account.getServer().getId()){
            log.error("Ошибка в запросе /api/v1/server/{}/account/block/{}", sId, aId);
            return ResponseEntity
                    .status(404)
                    .body(new ResponseObject<>("error", "Сервер не заблокирован."));
        }
        var optResult = service.action(account, requestID, Action.BLOCK);

        if(optResult.isEmpty()){
            log.error("Проблемы при блокировании сервера с ID {}", aId);
            return ResponseEntity
                    .status(500)
                    .body(new ResponseObject<>("error", "Сервер не заблокирован."));
        }

        var result = optResult.get();
        if (result){
            log.error("Сервер заблокирован с ID {}", aId);
            return ResponseEntity
                    .status(200)
                    .body(new ResponseObject<>("success", "Сервер заблокирован."));
        } else {
            log.error("Сервер c ID {} не заблокирован.", aId);
            return ResponseEntity
                    .status(500)
                    .body(new ResponseObject<>("error", "Сервер не заблокирован."));
        }
    }

    @Operation(summary = "Разблокировать аккаунт по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не создана")
    })
    @PostMapping("/unblock/{id}")
    public ResponseEntity<ResponseObject<Boolean>> unblockById(
            @Parameter(description = "ID сервера")
            @PathVariable("sId") long sId,
            @Parameter(description = "ID сервера/аккаунта")
            @PathVariable("id") long aId,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID
    ){
        log.info("Запрос на разблокировку аккаунта с ID {}", aId);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /api/v1/server/{}/account/unblock/{}", sId, aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        var optAccount = service.findById(aId, requestID);
        if (optAccount.isEmpty()){
            log.error("Аккаунт с ID {} не найден", aId);
            return ResponseEntity
                    .status(404)
                    .body(new ResponseObject<>("error", "Данного аккаунта не существует."));
        }
        var account = optAccount.get();

        if (sId != account.getServer().getId()){
            log.error("Ошибка в запросе /api/v1/server/{}/account/unblock/{}", sId, aId);
            return ResponseEntity
                    .status(404)
                    .body(new ResponseObject<>("error", "Сервер не заблокирован."));
        }

        var optResult = service.action(account, requestID, Action.UNBLOCK);

        if(optResult.isEmpty()){
            log.error("Проблемы при разблокировании сервера с ID {}", aId);
            return ResponseEntity
                    .status(500)
                    .body(new ResponseObject<>("error", "Сервер не разблокирован."));
        }

        var result = optResult.get();
        if (result){
            log.info("Сервер разблокирован с ID {}", aId);
            return ResponseEntity
                    .status(200)
                    .body(new ResponseObject<>("success", "Сервер разблокирован."));
        } else {
            log.error("Сервер c ID {} разблокирован.", aId);
            return ResponseEntity
                    .status(500)
                    .body(new ResponseObject<>("error", "Сервер не разблокирован."));
        }
    }

    @Operation(summary = "Перегрузить аккаунт по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не создана")
    })
    @PostMapping("/restart/{id}")
    public ResponseEntity<ResponseObject<Boolean>> restartById(
            @Parameter(description = "ID сервера")
            @PathVariable("sId") long sId,
            @Parameter(description = "ID сервера/аккаунта")
            @PathVariable("id") long aId,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID
    ){
        log.info("Запрос на перезагрузку аккаунта с ID {}", aId);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /api/v1/server/{}/account/unblock/{}", sId, aId);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        var optAccount = service.findById(aId, requestID);
        if (optAccount.isEmpty()){
            log.error("Аккаунт с ID {} не найден", aId);
            return ResponseEntity
                    .status(404)
                    .body(new ResponseObject<>("error", "Данного аккаунта не существует."));
        }
        var account = optAccount.get();

        if (sId != account.getServer().getId()){
            log.error("Ошибка в запросе /api/v1/server/{}/account/restart/{}", sId, aId);
            return ResponseEntity
                    .status(404)
                    .body(new ResponseObject<>("error", "Сервер не перезагружен."));
        }

        var optResult = service.action(account, requestID, Action.RESTART);

        if(optResult.isEmpty()){
            log.error("Проблемы при перезагрузке сервера с ID {}", aId);
            return ResponseEntity
                    .status(500)
                    .body(new ResponseObject<>("error", "Сервер не перезагружен."));
        }

        var result = optResult.get();
        if (result){
            log.info("Сервер перезагружен с ID {}", aId);
            return ResponseEntity
                    .status(200)
                    .body(new ResponseObject<>("success", "Сервер перезагружен."));
        } else {
            log.error("Сервер c ID {} ошибка при перезагрузке.", aId);
            return ResponseEntity
                    .status(500)
                    .body(new ResponseObject<>("error", "Сервер не перезагружен."));
        }
    }

    @Operation(summary = "Удалить аккаунт по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "403", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Сущность не создана")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Boolean>> deleteById(
            @Parameter(description = "ID сервера")
            @PathVariable("sId") long sId,
            @Parameter(description = "ID сервера/аккаунта")
            @PathVariable("id") long id,
            @Parameter(description = "Заголовок авторизации")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Заголовок requestID")
            @RequestHeader(value = "requestID", required = false) String requestID
    ){
        log.info("Запрос на удаление аккаунта с ID {}", id);

        if (incorrectAuthorization(authHeader)) {
            log.info("Авторизация не пройдена для /api/v1/server/{}/account/{}", sId, id);
            return ResponseEntity.status(403).body(new ResponseObject<>("error", "Доступ запрещен!"));
        }

        var result = service.deleteById(id, requestID);

        if (result){
            log.info("Аккаунт найден.");
            return ResponseEntity.ok(
                    new ResponseObject<>("success", "Аккаунт удален.", result));
        } else {
            log.info("Аккаунт не найден.");
            return ResponseEntity.status(404).body(
                    new ResponseObject<>("error", "Аккаунт не удален."));
        }
    }
}
