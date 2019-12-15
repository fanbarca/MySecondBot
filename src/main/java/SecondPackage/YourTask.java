package SecondPackage;

import java.sql.SQLException;
import java.util.TimerTask;

public class YourTask extends TimerTask {

    @Override
    public void run() {
        try {
            Bot bot = new Bot();
            for (String id : DataBase.sqlIdList()) {
                String image = DataBase.sqlselect(id, "image");
                if (image!=null) bot.deleteMessage(image, id);
            }
            DataBase.sql("update users set image = null");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
