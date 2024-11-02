package su.kartushin.busAPI.controllers.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.kartushin.busAPI.objects.TelegramBot;
import su.kartushin.busAPI.services.TelegramBotService;

@RestController
@RequestMapping("/api/v1/telegramBot")
@Tag(name = "Telegram Bot Controller", description = "API для работы с Telegram Bot")
public class TelegramBotController extends AbstractCrudController<TelegramBot, Long>{

    public TelegramBotController(){
        service = new TelegramBotService();
        entity = "Telegram Bot";
    }
}
