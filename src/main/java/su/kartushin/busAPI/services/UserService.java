package su.kartushin.busAPI.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import su.kartushin.busAPI.objects.User;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserService extends CrudService<User, Long> {

//    @Autowired
//    private UserRepository repository;

    /**
     * Возвращает список всех User.
     *
     * @return Optional со списком User, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<List<User>> findAll(String requestId) {
        try {
            var vpnProxies = userRepository.findAll();
            log.info("Все User успешно получены: {}", vpnProxies.size());
            return Optional.of(vpnProxies);
        } catch (Exception e) {
            log.error("Ошибка при получении списка User", e);
            return Optional.empty();
        }
    }

    public Optional<User> findByChatId(String chatId){
        return userRepository.findByChatId(chatId);
    }

    /**
     * Возвращает User по его Long.
     *
     * @param uuid Long искомого User.
     * @return Optional с найденным User, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<User> findById(Long uuid, String requestId) {
        try {
            var User = userRepository.findById(uuid);
            log.info("User с ID {} найден: {}", uuid, User.isPresent());
            return User;
        } catch (Exception e) {
            log.error("Ошибка при поиске User с ID {}", uuid, e);
            return Optional.empty();
        }
    }

    /**
     * Сохраняет новый User.
     *
     * @param entity объект User для сохранения.
     * @return Optional с сохраненным User, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<User> save(User entity, String requestId) {
        try {
            var savedVpnProxy = userRepository.save(entity);
            log.info("User успешно сохранен: {}", savedVpnProxy);
            return Optional.of(savedVpnProxy);
        } catch (Exception e) {
            log.error("Ошибка при сохранении User: {}", entity, e);
            return Optional.empty();
        }
    }

    /**
     * Обновляет существующий User по его Long.
     *
     * @param id Long обновляемого User.
     * @param userDetails объект с новыми данными User.
     * @return Optional с обновленным User, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<User> update(Long id, User userDetails, String requestId) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setAccountIds(userDetails.getAccountIds());
            existingUser.setUserName(userDetails.getUserName());
            existingUser.setChatId(userDetails.getChatId());
            existingUser.setStatus(userDetails.getStatus());

            try {
                User updatedUser = userRepository.save(existingUser);
                log.info("User с ID {} успешно обновлен: {}", id, updatedUser);
                return updatedUser;
            } catch (Exception e) {
                log.error("Ошибка при обновлении User с ID {}: {}", id, e.getMessage());
                return null;
            }
        });
    }

    /**
     * Удаляет User по его Long.
     *
     * @param uuid Long удаляемого User.
     * @return true, если User успешно удален, иначе false.
     */
    @Override
    public Boolean deleteById(Long uuid, String requestId) {
        if (!userRepository.existsById(uuid)) {
            log.warn("User с ID {} не найден для удаления", uuid);
            return false;
        }
        try {
            userRepository.deleteById(uuid);
            log.info("User с ID {} успешно удален", uuid);
            return true;
        } catch (Exception e) {
            log.error("Ошибка при удалении User с ID {}", uuid, e);
            return false;
        }
    }
}
