package su.kartushin.busAPI.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import su.kartushin.busAPI.objects.VPNProxy;

import java.io.IOException;

@RequiredArgsConstructor
@Log4j2
public class HttpExecute {

    private final VPNProxy server;
    private final String requestId;

    private OkHttpClient client;

    {
        client = new OkHttpClient();
    }

    /**
     * Выполнение POST-запроса.
     *
     * @param bodyContent Тело запроса в виде строки
     * @param uri URI для отправки POST-запроса
     * @return Ответ от сервера
     */
    public Response postRequest(String bodyContent, String uri) {
        log.info("Выполнение POST-запроса на URI: {}, тело: {}", uri, bodyContent);
        return executeRequest("POST", bodyContent, uri);
    }

    /**
     * Выполнение PUT-запроса.
     *
     * @param bodyContent Тело запроса в виде строки
     * @param uri URI для отправки PUT-запроса
     * @return Ответ от сервера
     */
    public Response putRequest(String bodyContent, String uri) {
        log.info("Выполнение PUT-запроса на URI: {}, тело: {}", uri, bodyContent);
        return executeRequest("PUT", bodyContent, uri);
    }

    /**
     * Выполнение GET-запроса.
     *
     * @param uri URI для отправки GET-запроса
     * @return Ответ от сервера
     */
    public Response getRequest(String uri) {
        log.info("Выполнение GET-запроса на URI: {}", uri);
        return executeRequest("GET", null, uri);
    }

    /**
     * Выполнение DELETE-запроса.
     *
     * @param bodyContent Тело запроса в виде строки (если требуется)
     * @param uri URI для отправки DELETE-запроса
     * @return Ответ от сервера
     */
    public Response deleteRequest(String bodyContent, String uri) {
        log.info("Выполнение DELETE-запроса на URI: {}, тело: {}", uri, bodyContent);
        return executeRequest("DELETE", bodyContent, uri);
    }

    /**
     * Общий метод для выполнения HTTP-запросов.
     *
     * @param method HTTP-метод (GET, POST, PUT, DELETE)
     * @param bodyContent Тело запроса в виде строки
     * @param uri URI для выполнения запроса
     * @return Ответ от сервера
     */
    private Response executeRequest(String method, String bodyContent, String uri) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = null;

        if (bodyContent != null) {
            body = RequestBody.create(mediaType, bodyContent);
            log.debug("Тело запроса: {}", bodyContent);
        }

        Request request = new Request.Builder()
                .url(server.getUrl() + uri)
                .method(method, body)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:131.0) Gecko/20100101 Firefox/131.0")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
                .addHeader("Content-Type", "application/json")
                .addHeader("Origin", server.getUrl())
                .addHeader("Connection", "keep-alive")
                .addHeader("Referer", server.getUrl() + "/login")
                .addHeader("requestID", requestId)
                .addHeader("Authorization", String.format("Bearer %s", server.getToken()) )
                .build();

        log.info("Выполнение {} запроса на URI: {}", method, server.getUrl() + uri);

        try {
            Response response = client.newCall(request).execute();
            log.debug("Ответ от сервера с кодом: {}", response.code());

            if (response.body() != null) {
                log.trace("Тело ответа: {}", response.peekBody(Long.MAX_VALUE).string());
            }

            if (!response.isSuccessful()) {
                log.warn("Неожиданный код ответа: {}", response.code());
                throw new IOException("Unexpected code " + response);
            }
            return response;
        } catch (IOException e) {
            LogUtil.logError(log, e);
            return null;
        }
    }
}
