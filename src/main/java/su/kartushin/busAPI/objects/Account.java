package su.kartushin.busAPI.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import su.kartushin.busAPI.enums.Country;
import su.kartushin.busAPI.enums.Status;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String chatId;
    String idOnServer;
    String serverName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "vpn_proxy_id") // Это имя колонки в таблице Account для хранения внешнего ключа
    VPNProxy server;

    @Enumerated(EnumType.STRING)
    Country country;
    @Enumerated(EnumType.STRING)
    Status status;
}
