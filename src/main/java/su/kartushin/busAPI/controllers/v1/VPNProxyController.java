package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.kartushin.busAPI.objects.VPNProxy;
import su.kartushin.busAPI.services.VPNProxyService;

@RestController
@RequestMapping("/api/v1/vpnProxy")
@Tag(name = "VPN Proxy Controller", description = "API для работы с VPN proxy")
public class VPNProxyController extends AbstractCrudController<VPNProxy, Long>{

    public VPNProxyController(){
        service = new VPNProxyService();
        entity = "VPN Proxy";
    }
}
