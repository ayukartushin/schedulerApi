package su.kartushin.busAPI.objects;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import su.kartushin.busAPI.enums.Status;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    List<Account> accountIds;

    String userName;
    String chatId;

    @Enumerated(EnumType.STRING)
    Status status;
}
