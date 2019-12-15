package SecondPackage;

import java.sql.SQLException;
import java.util.TimerTask;

public class YourTask extends TimerTask {

    @Override
    public void run() {
        try {
            Bot bot = new Bot();
            for (String id : DataBase.sqlIdList()) {
                bot.deleteMessage(DataBase.sqlselect(id, "image"), id);
            }
            DataBase.sql("update users set image = null");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
