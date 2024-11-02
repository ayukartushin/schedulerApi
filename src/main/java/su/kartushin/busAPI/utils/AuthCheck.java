package su.kartushin.busAPI.utils;

import lombok.extern.log4j.Log4j2;
import su.kartushin.busAPI.config.Config;

@Log4j2
public class AuthCheck {

    /**
     * Проверяет корректность переданного токена авторизации.
     *
     * @param authorization Токен авторизации, переданный в заголовке Authorization.
     * @return true, если токен некорректный, false, если токен корректный.
     */
    public static boolean incorrectAuthorization(String authorization){
        log.info("Проверка авторизации.");
        String expectedToken = String.format("Bearer %s", Config.getServerToken());
        boolean result = authorization == null || !authorization.equals(expectedToken);

        if (result) {
            log.warn("Авторизация не корректна. Переданный ключ: {}", authorization);
        } else {
            log.info("Авторизация корректна.");
        }

        return result;
    }
}
