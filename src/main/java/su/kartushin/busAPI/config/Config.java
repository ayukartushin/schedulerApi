package su.kartushin.busAPI.config;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Config {

    /**
     * Метод получения токена сервера
     * @return токен сервера
     */
    public static String getServerToken(){
        return getValue("ServerToken");
    }

    /**
     * Метод получения значения переменной окружения
     * @param variableName название переменной окружения
     * @return значение переменной или null, если переменная не найдена
     */
    private static String getValue(String variableName){
        // Получение значения переменной окружения
        String value = System.getenv(variableName);

        if (value != null) {
            log.info("Значение переменной окружения {} получено", variableName);
            return value;
        } else {
            log.error("Переменная окружения {} не найдена.", variableName);
            return null;
        }
    }
}
