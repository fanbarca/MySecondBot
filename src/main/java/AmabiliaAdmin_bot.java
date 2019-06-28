import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.logging.BotLogger;

public class AmabiliaAdmin_bot extends TelegramLongPollingBot {
    public static final long myID = 615351734;


    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {

            } else if (update.hasCallbackQuery()) {

            }
        } catch(Exception e){
            BotLogger.error(Main.LOGTAG, e);
        }
    }

    @Override
    public String getBotUsername() {
        return "AmabiliaBot";
    }

    @Override
    public String getBotToken() {
        return "824996881:AAFoXD_lYMKdFpAaxQiGydqldw1GEpkGm58";
    }
}
