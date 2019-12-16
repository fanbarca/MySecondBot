package SecondPackage;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.TimerTask;


public class Main {
    public static final String LOGTAG = "AmabiliaLog";

    public static void main(String[] args) throws SQLException {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            //botsApi.registerBot(new Essaybot());
            botsApi.registerBot(new Adminbot());
            botsApi.registerBot(new Bot());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        // Calendar today = Calendar.getInstance();
        // today.set(Calendar.HOUR_OF_DAY, 2);
        // today.set(Calendar.MINUTE, 0);
        // today.set(Calendar.SECOND, 0);
        // Timer timer = new Timer();
        // timer.schedule(new YourTask(), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
//        Timer timer = new Timer();
//            timer.schedule( new TimerTask() {
//                public void run() {
//                    Bot bot = new Bot();
//                    // deleteMessage(a.getImage(), a.getId());
//                    for (String id : DataBase.sqlIdList()) {
//                        String image = DataBase.sqlselect(id, "image");
//                        if (image!=null) bot.deleteMessage(image, id);
//                    }
//                    DataBase.sql("update users set image = null");
                //}
             //}, 5*60*1000, 5*60*1000);
    }
}