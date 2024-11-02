package su.kartushin.busAPI.objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import su.kartushin.busAPI.enums.Country;
import su.kartushin.busAPI.enums.Status;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class VPNProxy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String url;
    String token;

    @Enumerated(EnumType.STRING)
    Country country;

    int maxConnection;

    @OneToMany(mappedBy = "server")
    @JsonManagedReference
    private List<Account> accounts;

    @Enumerated(EnumType.STRING)
    Status status;
}
