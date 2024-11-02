package su.kartushin.busAPI.objects;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseObject<T> {

    private String status;   // Указывает статус ("success", "error" и т.д.)
    private String message;  // Сообщение, описывающее результат
    private T data;          // Данные (например, список пользователей)

    public ResponseObject(String status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}