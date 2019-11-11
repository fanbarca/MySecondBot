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
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;


public class Adminbot extends TelegramLongPollingBot {

    String messageid = "";
    public static final long myID = 615351734;
    public static String password = "zzzz1111*";
    private String category = "";
    private String listener = "";
    static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    static final String LATIN_TO_CYRILLIC = "Latin-Cyrillic";
    static TimeZone zone = TimeZone.getTimeZone("Asia/Tashkent");
    static SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
    static SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    List<String> list = new ArrayList<String>();
    String russian = "";
    {
    date.setTimeZone(zone);
    time.setTimeZone(zone);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            String id = null;
            if (update.hasCallbackQuery()) {
                id = update.getCallbackQuery().getMessage().getChatId().toString();
                checkAdmin(update, id);
            }
            else if (update.hasMessage()) {
                id = update.getMessage().getChatId().toString();
                if (update.getMessage().hasText()){
                    if (update.getMessage().getText().equals(password)){
                        DataBase.sql("update users set admin = false");
                        DataBase.sql("update users set admin = true where id ="+id);
                        allow(update, id);
                    } else {
                        checkAdmin(update, id);
                    }
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void checkAdmin(Update update, String id) throws SQLException {
        List<String> admins = DataBase.sqlQueryList("select id from users where admin = true", "id");
        if (admins.contains(id)) {
            allow(update, id);
        } else {
            enterPassword(update);
        }
	}

	private void enterPassword(Update update) throws SQLException {
        if (update.hasMessage()) {
            deleteMessage(update.getMessage());
            String adminmessage = DataBase.sqlQuery("select adminmessage from users where id="+update.getMessage().getChatId(), "adminmessage");
            if (adminmessage!=null) edit(update.getMessage(), update.getMessage().getText()+" - это неправильный пароль.\nПопробуйте ещё раз", null, 1);
            else send("Введите пароль", update.getMessage().getChatId().toString(), null, true, 1);
            listener = "password";
        } else {
            edit(update.getCallbackQuery().getMessage(), "Введите пароль", null, 1);
            listener = "password";
        }
	}

	private void allow(Update update, String id) throws SQLException {
        String adminmessage = DataBase.sqlQuery("select adminmessage from users where id="+id, "adminmessage");
        if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    if(update.getMessage().getText().equals(password)||update.getMessage().getText().equals("/start")){
                        deleteMessage(update.getMessage());
                        if (adminmessage!=null) {
                            deleteMessage(adminmessage, id);
                            send("Выберите действие", id, mainKeyboard(null), true,  1);
                        } else {
                            send("Выберите действие", id, mainKeyboard(null), true,  1);
                        }
                    }  else if (update.getMessage().getText().contains("/sql")) {
                        if (update.getMessage().getText().length()>5) {
                            String command = update.getMessage().getText().substring(5);
                            DataBase.sql(command);
                            deleteMessage(update.getMessage());
                        }
                    } else {
                        if (listener.equals("Russian")) {
                            Random rand = new Random();
                            russian = update.getMessage().getText();
                            DataBase.sql("insert into table0 (id, russian, type, instock) values ("+
                            String.format("%04d", rand.nextInt(8999)+1000)+", '"+russian+"', '"+category+"', true)");
                            listener = "Uzbek";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Введите название продукта на узбекском", list, 3);
                        } else if (listener.equals("Uzbek")) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE table0 SET uzbek = '"+Name+"' where russian = '"+russian+"'");
                            listener = "English";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Введите название продукта на английском", list, 3);
                        } else if (listener.equals("English")) {
                            String Name = update.getMessage().getText();
                            listener = "Cost";
                            DataBase.sql("UPDATE table0 SET english = '"+Name+"' where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Введите стоимость продукта", list, 3);
                        } else if (listener.equals("Cost")) {
                            String cost = update.getMessage().getText();
                            listener = "Russiandescription";
                            DataBase.sql("UPDATE table0 SET cost = "+cost+" where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Введите описание на русском", list, 3);
                        } else if (listener.equals("Russiandescription")) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE table0 SET Russiandescription = '"+Name+"' where russian = '"+russian+"'");
                            listener = "Uzbekdescription";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Введите описание на узбекском", list, 3);
                        } else if (listener.equals("Uzbekdescription")) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE table0 SET Uzbekdescription = '"+Name+"' where russian = '"+russian+"'");
                            listener = "Englishdescription";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Введите описание на английском", list, 3);
                        } else if (listener.equals("Englishdescription")) {
                            String Name = update.getMessage().getText();
                            listener = "emoji";
                            DataBase.sql("UPDATE table0 SET Englishdescription = '"+Name+"' where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Введите emoji", 1, simpleMarkUp("Пропустить"));
                        } else if (listener.equals("emoji")) {
                            String Name = update.getMessage().getText();
                            listener = "";
                            DataBase.sql("UPDATE table0 SET emoji = '"+Name+"' where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Готово", mainKeyboard(null), 1);
                        } else {
                            deleteMessage(update.getMessage());
                        }
                    }
                }





        } else if (update.hasCallbackQuery()) {
            AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId());
            String cb = update.getCallbackQuery().getData();
            if(cb.equals("Указать наличие")){
                edit(update.getCallbackQuery().getMessage(), "Указать наличие", DataBase.productsAvailability("Russian"), 3);
            } else if(cb.equals("Удалить продукт")){
                listener = "Delete";
                edit(update.getCallbackQuery().getMessage(), "Удалить продукт", DataBase.showAllProducts("Russian", false), 3);
            } else if(cb.equals("Добавить продукт")){
                edit(update.getCallbackQuery().getMessage(), "В какой раздел?", Lan.listTypes("Russian"), 3);
            } else if(cb.equals("Заказы")){
                List<String> IdList = DataBase.sqlQueryList("select userid from zakaz where conformed = true ORDER BY time ASC", "userid");
                if (IdList.isEmpty()){
                    answer.setShowAlert(false).setText("Заказов нет");
                } else {
                    String text = "Всего активных заказов: "+IdList.size();
                    List<String> orderButtons = new ArrayList<>();
                    for (String userID: IdList) {
                    String time = DataBase.sqlQuery("select time from zakaz where userid = '" +userID+"' and conformed = true", "time");
                    String name = DataBase.sqlQuery("select firstname from users where id ="+userID,"firstname");
                    orderButtons.add(name+"  -  "+time);
                    } orderButtons.add("Назад");
                    edit(update.getCallbackQuery().getMessage(), text, orderButtons, 1);
                }
            }
            List<String> idList = DataBase.sqlQueryList("select userid from zakaz where conformed = true","userid");
            for (String userID: idList) {
                String name = DataBase.sqlQuery("select firstname from users where id ="+userID,"firstname");
                if (cb.contains(name+"  -  ")) {
                    String number = DataBase.sqlQuery("select phone from users where id ="+userID,"phone");
                    String time = DataBase.sqlQuery("select time from zakaz where userid = '" +userID+"' and conformed = true", "time");
                    String address = DataBase.sqlQuery("select address from users where id ="+userID,"address");
                    String product = DataBase.sqlQuery("select product from zakaz where userid = '" +userID+"' and conformed = true", "product");
                    String text= "Имя: "+name+
                            "\nНомер: "+ number+
                            "\nВремя: "+time+
                            (address!=null?"\nАдрес: "+address+"\n":"\n")+
                            product;
                    String latitude = DataBase.sqlQuery("select latitude from users where id ="+userID,"latitude");
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Готов"))
                            .setCallbackData("Готов"+userID));
                    if (latitude!=null) row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Локация"))
                            .setCallbackData("Локация"+userID));
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Заказы"))
                            .setCallbackData("Заказы"));
                    rows.add(row);
                    markup.setKeyboard(rows);
                    edit(update.getCallbackQuery().getMessage(), text, 2, markup);
                }
                if (cb.contains("Локация"+userID)) {
                    Float latitude = Float.valueOf(DataBase.sqlQuery("select latitude from users where id ="+userID,"latitude"));
                    Float longitude = Float.valueOf(DataBase.sqlQuery("select longitude from users where id ="+userID,"longitude"));
                    deleteMessage(update.getCallbackQuery().getMessage().getMessageId().toString(), id);
                    List<String> list = new ArrayList<>();
                    list.add("Назад");
                    sendLocation(id, latitude, longitude, list);
                }
            }
            for (String t:Lan.listTypes("Russian")) {
                if (cb.equals(t)) {
                    category = Lan.listTypes("Russian").indexOf(t)+"";
                    listener = "Russian";
                    edit(update.getCallbackQuery().getMessage(), "Выбрана категория "+t+
                    "\nВведите название продукта на русском",  list, 1);
                }
            }
            for (String t:DataBase.showAllProducts("Russian", false)) {
                if (cb.contains(t)) {
                    if (cb.contains(":white_check_mark:")) {
                        DataBase.sql("UPDATE table0 SET instock = false where russian = '"+t+"'");
                        edit(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getMessage().getText(), DataBase.productsAvailability("Russian"), 3);
                    } else if (cb.contains(":x:")) {
                        DataBase.sql("UPDATE table0 SET instock = true where russian = '"+t+"'");
                        edit(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getMessage().getText(), DataBase.productsAvailability("Russian"), 3);
                    } else if (listener.equals("Delete")) {
                        listener = "";
                        try {
                            String prodId = DataBase.sqlQuery("select id from table0 where russian = '"+t+"'", "id");
                            DataBase.sql("delete from cart where item = '"+prodId+"'");
                            DataBase.sql("delete from table0 where russian = '"+t+"'");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        edit(update.getCallbackQuery().getMessage(), "Удалить продукт", DataBase.showAllProducts("Russian", false), 3);
                        answer.setShowAlert(false).setText("Удалено!");

                    }
                }
            }
                if (cb.contains("Готов")) {
                    String userid = cb.substring(5);
                    DataBase.sql("delete from zakaz where userid = "+userid);
                    edit(update.getCallbackQuery().getMessage(), "Заказ завершён", null, 2);
                }
                if (cb.equals("Отмена")||cb.equals("Ok")) {
                    deleteMessage(update.getCallbackQuery().getMessage());
                } else if(cb.equals("Назад")) {
                    if (update.getCallbackQuery().getMessage().hasLocation()) {
                        deleteMessage(update.getCallbackQuery().getMessage());
                        send("Выберите действие", id, mainKeyboard(null), true, 1);
                    } else {
                        edit(update.getCallbackQuery().getMessage(), "Выберите действие", mainKeyboard(null), 1);
                    }
                }
                if (cb.equals("Пропустить")) {
                        listener = "";
                        edit(update.getMessage(), "Готово", mainKeyboard(null), 1);
                }


            try {
				execute(answer);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
        }
	}

	private List<String> mainKeyboard(String lastbutton) {
        List<String> a = new ArrayList<>();
            a.add("Указать наличие");
            a.add("Удалить продукт");
            a.add("Добавить продукт");
            a.add("Заказы");
            if (lastbutton!=null) a.add(lastbutton);
        return a;
	}

	@Override
    public String getBotUsername() {
        return "AmabiliaBot";
    }

    @Override
    public String getBotToken() {
        return "824996881:AAFoXD_lYMKdFpAaxQiGydqldw1GEpkGm58";
    }

public void deleteMessage(Message message){
        DeleteMessage dm = new DeleteMessage()
                .setMessageId(message.getMessageId())
                .setChatId(message.getChatId());
        try {execute(dm);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
public void deleteMessage(String Messageid, String Chatid) {
        DeleteMessage dm = new DeleteMessage()
                .setMessageId(Integer.parseInt(Messageid))
                .setChatId(Chatid);
        try {
            execute(dm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
}
public void send (String text, String chatId, List<String> list, boolean inline, int flag) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        if (list!=null){
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
        //List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
        // if (!text.contains("Заказ от")) {
        //     lastRow.add(new InlineKeyboardButton()
        //                 .setText(EmojiParser.parseToUnicode("Отмена"))
        //                 .setCallbackData("Отмена"));
        //     rows.add(lastRow);
        // }
        inlineMarkup.setKeyboard(rows);
        replyMarkup.setKeyboard(rows2).setResizeKeyboard(true).setOneTimeKeyboard(false);
        if (inline) sendMessage.setReplyMarkup(inlineMarkup);
        else sendMessage.setReplyMarkup(replyMarkup);
        }
        try {
            String messageId = execute(sendMessage).getMessageId().toString();
            DataBase.sql("update users set adminMessage ="+messageId+" where id = "+chatId);
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void edit (Message message, String newText, List<String> list, int flag) throws SQLException {
        if (list!=null) {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            for (int i = 0; i < list.size(); i += flag) {
                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(i)))
                        .setCallbackData(list.get(i)));
                if ((flag==2)||(flag==3)) {if ((i + 1) < list.size()) row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                        .setCallbackData(list.get(i + 1)));}
                if (flag==3) {if ((i + 2) < list.size()) row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(i + 2)))
                        .setCallbackData(list.get(i + 2)));
                }
                rows.add(row);
            }
            List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
            if (newText.contains(mainKeyboard(null).get(0))||
                newText.contains(mainKeyboard(null).get(1))||
                newText.contains("В какой раздел?")||
                newText.contains("Введите")||
                newText.contains("Время:")) {
                lastRow.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Назад"))
                            .setCallbackData("Назад"));
            }
            rows.add(lastRow);
            markup.setKeyboard(rows);
            edit (message, newText, flag, markup);
        } else {
            edit(message, newText, flag, null);
        }
    }
    public void edit (Message message, String newText, int flag, InlineKeyboardMarkup markup) throws SQLException {
    String adminMessage = DataBase.sqlQuery("select adminmessage from users where id ="+message.getChatId(), "adminmessage");
        EditMessageText sendMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(Integer.parseInt(adminMessage))
                .setParseMode("HTML")
                .setText(EmojiParser.parseToUnicode(newText));
        if (markup!=null) {
            sendMessage.setReplyMarkup(markup);
        }
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }

public List<String> listOrders(String column){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = DataBase.getConnection();
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
            Connection conn = DataBase.getConnection();
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
    public String sendMe(String text) throws TelegramApiException, SQLException {
        SendMessage sendMessage = new SendMessage()
                .setChatId(DataBase.sqlQuery("select id from users where admin = true", "id"))
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML")
                .setReplyMarkup(simpleMarkUp("Ok"));

        return execute(sendMessage).getMessageId().toString();
    }
    private InlineKeyboardMarkup simpleMarkUp(String button) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
            row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(button))
                    .setCallbackData(button));
            rows.add(row);
        markup.setKeyboard(rows);
		return markup;
	}

	public void sendLocation(String chatId, Float latitude, Float longitude, List<String> list) {
        SendLocation sendMessage = new SendLocation()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setChatId(chatId);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            for (int i = 0; i < list.size(); i++) {
                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(i)))
                        .setCallbackData(list.get(i)));
                rows.add(row);
	        }
            markup.setKeyboard(rows);
            sendMessage.setReplyMarkup(markup);
        if (list!=null) sendMessage.setReplyMarkup(markup);
        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void sendContact(String chatId, String name, String number) {
        SendContact sendMessage = new SendContact()
                .setFirstName(name)
                .setPhoneNumber(number)
                .setChatId(chatId);
        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void forwardMessage(Message message, long id) {
        ForwardMessage fm = new ForwardMessage();
        fm.setChatId(id);
        fm.setFromChatId(message.getChatId());
        fm.setMessageId(message.getMessageId());
        try {execute(fm);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public static long getMyID() {
        return myID;
    }
}
