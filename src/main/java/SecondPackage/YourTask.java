package SecondPackage;

import java.sql.SQLException;
import java.util.TimerTask;

public class YourTask extends TimerTask {

    @Override
    public void run() {
        try {
            for (String id : DataBase.sqlIdList()) {
                Bot.deleteMessage(DataBase.sqlselect(id, "image"), id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
