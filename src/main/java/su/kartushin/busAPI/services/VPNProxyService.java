package su.kartushin.busAPI.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import su.kartushin.busAPI.objects.VPNProxy;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления сущностями VPNProxy, реализует основные CRUD операции.
 */
@Log4j2
@Service
public class VPNProxyService extends CrudService<VPNProxy, Long> {

//    @Autowired
//    private VPNProxyRepository serverRepository;

    /**
     * Возвращает список всех VPNProxy.
     *
     * @return Optional со списком VPNProxy, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<List<VPNProxy>> findAll(String requestId) {
        try {
            var vpnProxies = serverRepository.findAll();
            log.info("Все VPNProxy успешно получены: {}", vpnProxies.size());
            return Optional.of(vpnProxies);
        } catch (Exception e) {
            log.error("Ошибка при получении списка VPNProxy", e);
            return Optional.empty();
        }
    }

    /**
     * Возвращает VPNProxy по его Long.
     *
     * @param uuid Long искомого VPNProxy.
     * @return Optional с найденным VPNProxy, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<VPNProxy> findById(Long uuid, String requestId) {
        try {
            var vpnProxy = serverRepository.findById(uuid);
            log.info("VPNProxy с ID {} найден: {}", uuid, vpnProxy.isPresent());
            return vpnProxy;
        } catch (Exception e) {
            log.error("Ошибка при поиске VPNProxy с ID {}", uuid, e);
            return Optional.empty();
        }
    }

    /**
     * Сохраняет новый VPNProxy.
     *
     * @param entity объект VPNProxy для сохранения.
     * @return Optional с сохраненным VPNProxy, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<VPNProxy> save(VPNProxy entity, String requestId) {
        try {
            var savedVpnProxy = serverRepository.save(entity);
            log.info("VPNProxy успешно сохранен: {}", savedVpnProxy);
            return Optional.of(savedVpnProxy);
        } catch (Exception e) {
            log.error("Ошибка при сохранении VPNProxy: {}", entity, e);
            return Optional.empty();
        }
    }

    /**
     * Обновляет существующий VPNProxy по его Long.
     *
     * @param id Long обновляемого VPNProxy.
     * @param proxyDetails объект с новыми данными VPNProxy.
     * @return Optional с обновленным VPNProxy, если успешно, иначе пустой Optional.
     */
    @Override
    public Optional<VPNProxy> update(Long id, VPNProxy proxyDetails, String requestId) {
        return serverRepository.findById(id).map(existingVpnProxy -> {
            existingVpnProxy.setUrl(proxyDetails.getUrl());
            existingVpnProxy.setToken(proxyDetails.getToken());
            existingVpnProxy.setMaxConnection(proxyDetails.getMaxConnection());
            existingVpnProxy.setCountry(proxyDetails.getCountry());
            existingVpnProxy.setStatus(proxyDetails.getStatus());
            try {
                var updatedVpnProxy = serverRepository.save(existingVpnProxy);
                log.info("VPNProxy с ID {} успешно обновлен: {}", id, updatedVpnProxy);
                return updatedVpnProxy;
            } catch (Exception e) {
                log.error("Ошибка при обновлении VPNProxy с ID {}", id, e);
                return null;
            }
        });
    }

    /**
     * Удаляет VPNProxy по его Long.
     *
     * @param uuid Long удаляемого VPNProxy.
     * @return true, если VPNProxy успешно удален, иначе false.
     */
    @Override
    public Boolean deleteById(Long uuid, String requestId) {
        if (!serverRepository.existsById(uuid)) {
            log.warn("VPNProxy с ID {} не найден для удаления", uuid);
            return false;
        }
        try {
            serverRepository.deleteById(uuid);
            log.info("VPNProxy с ID {} успешно удален", uuid);
            return true;
        } catch (Exception e) {
            log.error("Ошибка при удалении VPNProxy с ID {}", uuid, e);
            return false;
        }
    }
}
