package su.kartushin.busAPI.services;

import java.util.List;
import java.util.Optional;

public interface ICrudService<T, ID> {
    Optional<List<T>> findAll(String requestId);

    Optional<T> findById(ID id, String requestId);

    Optional<T> save(T entity, String requestId);

    Optional<T> update(ID id, T entity, String requestId);

    Boolean deleteById(ID id, String requestId);
}
