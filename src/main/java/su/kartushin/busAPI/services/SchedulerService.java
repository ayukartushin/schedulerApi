package su.kartushin.busAPI.services;

import su.kartushin.busAPI.objects.Scheduler;

import java.util.List;
import java.util.Optional;

public class SchedulerService extends CrudService<Scheduler, Long> {
    @Override
    public Optional<List<Scheduler>> findAll(String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<Scheduler> findById(Long aLong, String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<Scheduler> save(Scheduler entity, String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<Scheduler> update(Long aLong, Scheduler entity, String requestId) {
        return Optional.empty();
    }

    @Override
    public Boolean deleteById(Long aLong, String requestId) {
        return null;
    }
}
