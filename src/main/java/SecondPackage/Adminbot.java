package SecondPackage;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.*;

public class Adminbot extends TelegramLongPollingBot {
    private HashMap<Integer, Order> set = new HashMap<Integer, Order>();
    private Translation t;
    private static final long myID = 615351734;
    static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    static final String LATIN_TO_CYRILLIC = "Latin-Cyrillic";
    static TimeZone zone = TimeZone.getTimeZone("Asia/Tashkent");
    static SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
    static SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    private static final String DRIVER = "org.postgresql.Driver";
    private String language ="";
    private String number = "";
    {
    date.setTimeZone(zone);
    time.setTimeZone(zone);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().getChatId().equals(myID)){
            send("есть контакт!", myID);
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

    public void send (String text, long chatId) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }


}
