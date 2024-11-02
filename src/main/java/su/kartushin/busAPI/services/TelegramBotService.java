package su.kartushin.busAPI.services;

import su.kartushin.busAPI.objects.TelegramBot;

import java.util.List;
import java.util.Optional;

public class TelegramBotService extends CrudService<TelegramBot, Long> {

    @Override
    public Optional<List<TelegramBot>> findAll(String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<TelegramBot> findById(Long aLong, String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<TelegramBot> save(TelegramBot entity, String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<TelegramBot> update(Long aLong, TelegramBot entity, String requestId) {
        return Optional.empty();
    }

    @Override
    public Boolean deleteById(Long aLong, String requestId) {
        return null;
    }
}
