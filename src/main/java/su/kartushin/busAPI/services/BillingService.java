package su.kartushin.busAPI.services;

import su.kartushin.busAPI.objects.Billing;

import java.util.List;
import java.util.Optional;

public class BillingService extends CrudService<Billing, Long> {
    @Override
    public Optional<List<Billing>> findAll(String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<Billing> findById(Long aLong, String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<Billing> save(Billing entity, String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<Billing> update(Long aLong, Billing entity, String requestId) {
        return Optional.empty();
    }

    @Override
    public Boolean deleteById(Long aLong, String requestId) {
        return null;
    }
}
