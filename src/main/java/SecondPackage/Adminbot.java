package SecondPackage;

import com.vdurmont.emoji.EmojiParser;

import org.postgresql.core.SqlCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class Adminbot extends TelegramLongPollingBot {
    private static final long myID = 615351734;
    private String category = "";
    static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    static final String LATIN_TO_CYRILLIC = "Latin-Cyrillic";
    static TimeZone zone = TimeZone.getTimeZone("Asia/Tashkent");
    static SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
    static SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    private static final String DRIVER = "org.postgresql.Driver";
    List<String> list = new ArrayList<String>();
    String russian = "";
    {
    date.setTimeZone(zone);
    time.setTimeZone(zone);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if(update.getMessage().getChatId().equals(myID)){
                if (update.getMessage().hasText()) {
                    if(update.getMessage().getText().equals("/start")){
                        List<String> a = new ArrayList<>();
                        a.add("Изменить Меню");
                        a.add("Заказы");
                        a.add("Добавть продукт");
                        send("Выберите действие", myID, a,false, 2);
                    } else if(update.getMessage().getText().equals("Изменить Меню")){
                        send("Изменить Меню", myID, Lan.listTypes("Russian"), true, 3);
                    } else if(update.getMessage().getText().equals("Заказы")){
                        send("Заказы", myID, listOrders("orderid"), true, 2);
                    } else if(update.getMessage().getText().equals("Добавть продукт")){
                        send("В какой раздел?", myID, Lan.listTypes("Russian"), true, 3);
                    }
                    if (!category.equals("")){
                        if (update.getMessage().getText().contains("/Р")) {
                            Random rand = new Random();
                            russian = update.getMessage().getText().substring(3);
                            AmabiliaBot.sql("insert into table0 (id, russian, type, instock) values ("+
                            String.format("%04d", rand.nextInt(10000))+", '"+russian+"', '"+category+"', true)");
                            
                            send("Введите название продукта на узбекском", myID, list, false, 3);
                        } else if (update.getMessage().getText().contains("/U")) {
                            String Name = update.getMessage().getText().substring(3);
                            AmabiliaBot.sql("UPDATE table0 SET uzbek = '"+Name+"' where russian = '"+russian+"'");
                            send("Введите название продукта на английском", myID, list, false, 3);

                        } else if (update.getMessage().getText().contains("/E")) {
                            String Name = update.getMessage().getText().substring(3);
                            AmabiliaBot.sql("UPDATE table0 SET english = '"+Name+"' where russian = '"+russian+"'");
                            send("Введите стоимость продукта", myID, list, false, 3);
                        } else if (update.getMessage().getText().contains("/C")) {
                            String cost = update.getMessage().getText().substring(3);
                            AmabiliaBot.sql("UPDATE table0 SET cost = "+cost+" where russian = '"+russian+"'");
                            send("Готово", myID, list, false, 3);
                        }
                    } else {
                        send("В какой раздел?", myID, Lan.listTypes("Russian"), true, 3);
                    }
                    if (update.getMessage().getText().contains("/sql")) {
                        if (update.getMessage().getText().length()>5) {
                            String command = update.getMessage().getText().substring(5);
                            AmabiliaBot.sql(command);
                        }
                    }
                } else if (update.getMessage().hasPhoto()) {
                    
                }
            }
        } else if (update.hasCallbackQuery()) {
                if (update.getCallbackQuery().getMessage().getChatId().equals(myID)) {
                for (String t:Lan.listTypes("Russian")) {
                    if (update.getCallbackQuery().getData().equals(t)) {
                        category = Lan.listTypes("Russian").indexOf(t)+"";
                        send("Выбрана категория "+t+
                        "\nВведите название продукта на русском",  //На узбекском На английском
                        myID, list, false, 1);
                    } else if(update.getCallbackQuery().getData().equals("Назад")) {
                        edit(update.getCallbackQuery().getMessage(), "Изменить Меню",
                        Lan.listTypes("Russian"), 3);
                    }
                }
            }
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


public void send (String text, long chatId, List<String> list, boolean inline, int flag) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        List<KeyboardRow> rows2 = new ArrayList<KeyboardRow>();

        for (int i = 0; i < list.size(); i += flag) {
                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                KeyboardRow row2 = new KeyboardRow();

                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(i)))
                        .setCallbackData(list.get(i)));
                row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(list.get(i))));
            if ((flag==2)||(flag==3)) {
                if ((i + 1) < list.size()) {
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                            .setCallbackData(list.get(i + 1)));
                    row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(list.get(i + 1))));
                }
            }
            if (flag==3){
                if ((i + 2) < list.size()) {
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i + 2)))
                            .setCallbackData(list.get(i + 2)));
                    row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(list.get(i + 2))));
                }
            }
                rows.add(row);
                rows2.add(row2);
            }
        inlineMarkup.setKeyboard(rows);
        replyMarkup.setKeyboard(rows2).setResizeKeyboard(true).setOneTimeKeyboard(false);
        if (inline) sendMessage.setReplyMarkup(inlineMarkup);
        else sendMessage.setReplyMarkup(replyMarkup);
        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }
public void edit (Message message, String newText, List<String> list, int flag) {
        EditMessageText sendMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(message.getMessageId())
                .setParseMode("HTML")
                .setText(EmojiParser.parseToUnicode(newText));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        for (int i = 0; i < list.size(); i += flag) {
            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
            row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(i)))
                    .setCallbackData(list.get(i)));
            if ((flag==2)||(flag==3)) row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                    .setCallbackData(list.get(i + 1)));
            if (flag==3) row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(i + 2)))
                    .setCallbackData(list.get(i + 2)));
            rows.add(row);
        }
        markup.setKeyboard(rows);
        sendMessage.setReplyMarkup(markup);
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }

public List<String> listOrders(String column){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = AmabiliaBot.getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from orders");
                while (rs.next()){
                    lan.add(rs.getString(column));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }
    public List<String> showProducts(String column, String table){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = AmabiliaBot.getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from "+table+" where instock = true");
                while (rs.next()){
                    lan.add(rs.getString(column));
                }
                lan.add("Назад");
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }
    public void sendMe(String text) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(myID)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }
}
