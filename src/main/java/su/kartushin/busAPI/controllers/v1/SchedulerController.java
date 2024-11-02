package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.kartushin.busAPI.objects.Scheduler;
import su.kartushin.busAPI.services.SchedulerService;

@RestController
@RequestMapping("/api/v1/scheduler")
@Tag(name = "Scheduler Controller", description = "API для работы с планировщиком задач")
public class SchedulerController extends AbstractCrudController<Scheduler, Long>{

    public SchedulerController(){
        service = new SchedulerService();
        entity = "Scheduler";
    }

}
