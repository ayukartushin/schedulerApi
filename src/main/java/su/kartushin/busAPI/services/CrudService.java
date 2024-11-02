package su.kartushin.busAPI.services;

import org.springframework.beans.factory.annotation.Autowired;
import su.kartushin.busAPI.repositories.AccountRepository;
import su.kartushin.busAPI.repositories.UserRepository;
import su.kartushin.busAPI.repositories.VPNProxyRepository;

import java.util.List;
import java.util.Optional;

public abstract class CrudService<T, ID> implements ICrudService<T, ID>{

    @Autowired
    protected VPNProxyRepository serverRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected UserRepository userRepository;

    @Override
    public abstract Optional<List<T>> findAll(String requestId);

    @Override
    public abstract Optional<T> findById(ID id, String requestId);

    @Override
    public abstract Optional<T> save(T entity, String requestId);

    @Override
    public abstract Optional<T> update(ID id, T entity, String requestId);

    @Override
    public abstract Boolean deleteById(ID id, String requestId);

    protected String getRequestId(String requestId){
        return (requestId == null || requestId.isEmpty()) ?
                "no requestId" :
                requestId;
    }
}
