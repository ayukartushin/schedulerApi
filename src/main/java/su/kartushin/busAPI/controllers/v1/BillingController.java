package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.kartushin.busAPI.objects.Billing;
import su.kartushin.busAPI.services.BillingService;

@RestController
@RequestMapping("/api/v1/billing")
@Tag(name = "Billing Controller", description = "API для работы с Билингом")
public class BillingController extends AbstractCrudController<Billing, Long> {

    public BillingController(){
        service = new BillingService();
        entity = "Billing";
    }
}
