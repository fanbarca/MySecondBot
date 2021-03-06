package CommonPackage;

import CurtainBotPackage.Adminbot;
import CurtainBotPackage.Bot;
import TutorBotPackage.TutorBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Main {
    public static final String LOGTAG = "AmabiliaLog";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            //botsApi.registerBot(new TutorBot());
            botsApi.registerBot(new Adminbot());
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}