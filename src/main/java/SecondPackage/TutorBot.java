package SecondPackage;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;


public class TutorBot extends TelegramLongPollingBot {
    private String botName = "";
    private String botToken = "";


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {

        }
        else if (update.hasMessage()) {

        }
    }



    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }



    private boolean checkNewUser(Update update) throws SQLException, TelegramApiException {
        String id = "";
        String firstName = "";
        String lastName = "";
        String userName = "";
        boolean res = false;
        if (update.hasMessage()) {
            id = update.getMessage().getChatId().toString();
            firstName = update.getMessage().getFrom().getFirstName();
            lastName= update.getMessage().getFrom().getLastName();
            userName = update.getMessage().getFrom().getUserName();
        }
        else if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getFrom().getId().toString();
            firstName = update.getCallbackQuery().getFrom().getFirstName();
            lastName= update.getCallbackQuery().getFrom().getLastName();
            userName = update.getCallbackQuery().getFrom().getUserName();
        }
        if (DataBase.sqlIdList().contains(id)) {

        } else {
            DataBase.sql("INSERT INTO users (id, firstname, lastname, username) VALUES ('" +
                    id + "','" +
                    firstName + "','" +
                    lastName + "','" +
                    userName + "')");
            Adminbot ab = new Adminbot();
            ab.sendMe(":boom: Новый пользователь!" +
                    "\n" + firstName + " " + lastName +
                    "\n@" + userName);


            res = true;
        }
        return res;
    }




    public synchronized void answerCallbackQuery(String callbackId, String message) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackId);
        answer.setText(message);
        answer.setShowAlert(true);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
