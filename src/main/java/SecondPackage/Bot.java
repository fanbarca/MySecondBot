package SecondPackage;

import com.ibm.icu.text.Transliterator;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.net.URISyntaxException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.sql.*;
import java.sql.Date;


public class Bot extends TelegramLongPollingBot {
    private String botName = "DeliverySuperBot";
    private String botToken = "780864630:AAHpUc01UagThYH7wRi15zJQjwu06A6NaWM";
    Order a;




    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
    @Override
    public void onUpdateReceived(Update update) {
        Message m;
        try {
            if (update.hasMessage()) {
                m = update.getMessage();
                if (DataBase.sqlIdList().contains(m.getChatId().toString())) {
                    a = new Order(
                            DataBase.sqlGetUserData(m.getChatId().toString()).get(0),
                            DataBase.sqlGetUserData(m.getChatId().toString()).get(1),
                            DataBase.sqlGetUserData(m.getChatId().toString()).get(2),
                            m.getChatId().toString()
                    );
                    if (m.hasText()) handleIncomingText(update);
                    else if (m.hasAnimation()) handleAnimation(m);
                    else if (m.hasAudio()) handleAudio(m);
                    else if (m.hasContact()) handleContact(m);
                    else if (m.hasDocument()) handleDocument(m);
                    else if (m.hasPhoto()) handlePhoto(m);
                    else if (m.hasLocation()) handleLocation(update);
                    else if (m.hasSticker()) handleSticker(m);
                    else if (m.hasVideo()) handleVideo(m);
                    else if (m.hasVideoNote()) handleVideoNote(m);
                    else if (m.hasVoice()) handleVoice(m);
                } else {
                    DataBase.sql("INSERT INTO users (id, firstname, lastname, username) VALUES ('" +
                            m.getFrom().getId().toString() + "','" +
                            m.getFrom().getFirstName() + "','" +
                            m.getFrom().getLastName() + "','" +
                            m.getFrom().getUserName() + "')");
                    a = new Order(
                            m.getFrom().getFirstName(),
                            null,
                            null,
                            m.getChatId().toString()
                    );
                    Adminbot ab = new Adminbot();
                    ab.sendMe(":boom: Новый пользователь!" +
                            "\n" + m.getFrom().getFirstName() + " " + m.getFrom().getLastName() +
                            "\n@" + m.getFrom().getUserName());
                    if (m.hasText()) handleIncomingText(update);
                }
            } else if (update.hasCallbackQuery()) {
                String cb = update.getCallbackQuery().getData();
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                if (DataBase.sqlIdList().contains(chatId)) {
                    a = new Order(DataBase.sqlGetUserData(chatId).get(0),
                                DataBase.sqlGetUserData(chatId).get(1),
                                DataBase.sqlGetUserData(chatId).get(2),
                                chatId
                    );
                } else {
                    DataBase.sql("INSERT INTO users (id, firstname, lastname, username) VALUES ('" +
                            chatId + "','" +
                            update.getCallbackQuery().getFrom().getFirstName() + "','" +
                            update.getCallbackQuery().getFrom().getLastName() + "','" +
                            update.getCallbackQuery().getFrom().getUserName() + "')");
                    a = new Order(
                            update.getCallbackQuery().getFrom().getFirstName(),
                            null,
                            null,
                            chatId
                    );
                }
                if (a.getLanguage() == null && !(cb.equals("O'zbek") || cb.equals("Русский") || cb.equals("English")))
                    chooseLanguage(update.getCallbackQuery().getMessage(), true);
                else handleCallback(update);
            }
        } catch (Exception e) {
            BotLogger.error(Main.LOGTAG, e);
        }
    }











    private void handleContact(Message message) throws SQLException, TelegramApiException {
        if (message.hasText() && !message.hasContact() && message.getText().startsWith("+998")) {
            DataBase.sql("UPDATE users SET phone = " +
                    message.getText() + " WHERE id =" + a.getId());
        } else {
            DataBase.sql("UPDATE users SET phone = " +
                    message.getContact().getPhoneNumber() + " WHERE id =" + a.getId());
        }
        a.setNumber(DataBase.sqlGetUserData(message.getChatId().toString()).get(1));
        deleteMessage(DataBase.sqlQuery("SELECT smid from users where id=" + message.getChatId(), "smid"), message.getChatId().toString());
        deleteMessage(message);
        if (a.getNumber() != null) sendPic(Lan.welcome(a.getLanguage(), message.getFrom().getFirstName()),
                message, Lan.mainMenu(a.getLanguage()), "Лого", 2);
    }













    private void handleIncomingText(Update update) throws SQLException, TelegramApiException {
        if (update.getMessage().getText().equals("/start")) {
            if (a.getSentMessage() != null)
                deleteMessage(DataBase.sqlQuery("SELECT smid from users where id=" + a.getId(), "smid"), a.getId());
            deleteMessage(update.getMessage());
            if (a.getLanguage() == null) {
                chooseLanguage(update.getMessage(), false);
            } else {
                sendPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getMessage(), Lan.mainMenu(a.getLanguage()), "Лого", 2);
            }
        } else if (update.getMessage().getText().startsWith("+998")) {
            if (a.getNumber() == null) {
                handleContact(update.getMessage());
            }
        } else {
            if (waitingForLocation(update.getMessage())) {
                handleLocation(update);;
            } else {
                deleteMessage(update.getMessage());
            }
        }
    }








    private void handleCallback(Update update) throws TelegramApiException, SQLException, MalformedURLException, IOException {

        AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId());

        String cb = update.getCallbackQuery().getData();
        if (cb.equals("O'zbek") || cb.equals("Русский") || cb.equals("English")) {
            if (cb.equals("O'zbek")) {
                DataBase.sql("UPDATE users SET language = 'Uzbek' WHERE id =" + a.getId());
                a.setLanguage("Uzbek");
            } else if (cb.equals("Русский")) {
                DataBase.sql("UPDATE users SET language = 'Russian' WHERE id =" + a.getId());
                a.setLanguage("Russian");
            } else {
                DataBase.sql("UPDATE users SET language = 'English' WHERE id =" + a.getId());
                a.setLanguage("English");
            }
            if (a.getNumber() == null) {
                deleteMessage(update.getCallbackQuery().getMessage());
                sendMeNumber(a.getId());
            } else {
                editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getCallbackQuery().getMessage(), Lan.mainMenu(a.getLanguage()), "Лого", 2);
            }
        }
        if (cb.equals(Lan.backToMenu(a.getLanguage()))) {
            editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getCallbackQuery().getMessage(),
                    Lan.mainMenu(a.getLanguage()), "Лого", 2);
        }
        if (cb.equals(Lan.mainMenu("Uzbek").get(0)) ||
                cb.equals(Lan.mainMenu("Russian").get(0)) ||
                cb.equals(Lan.mainMenu("English").get(0)) ||
                cb.equals(Lan.goBack(a.getLanguage()))) {
            if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                chooseLanguage(update.getCallbackQuery().getMessage(), true);
            } else {
                editPic(Lan.chooseDish(a.getLanguage()), update.getCallbackQuery().getMessage(), Lan.listTypes(a.getLanguage()), "Лого", 3);
            }
        } else if (cb.equals(Lan.mainMenu("Uzbek").get(1)) ||
                cb.equals(Lan.mainMenu("Russian").get(1)) ||
                cb.equals(Lan.mainMenu("English").get(1))) {
            if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                chooseLanguage(update.getCallbackQuery().getMessage(), true);
            } else {
                showOrders(update);
            }
        } else if (cb.equals(Lan.mainMenu("Uzbek").get(2)) ||
                cb.equals(Lan.mainMenu("Russian").get(2)) ||
                cb.equals(Lan.mainMenu("English").get(2))) {
            chooseLanguage(update.getCallbackQuery().getMessage(), true);
        } else if (cb.equals(Lan.mainMenu("Uzbek").get(3)) ||
                cb.equals(Lan.mainMenu("Russian").get(3)) ||
                cb.equals(Lan.mainMenu("English").get(3))) {
            if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                chooseLanguage(update.getCallbackQuery().getMessage(), true);
            } else {
                showCart(update, true);
            }
        }
        for (int i=0; i<Lan.listTypes(a.getLanguage()).size(); i++) {
            if (cb.equals(Lan.listTypes(a.getLanguage()).get(i)) && !cb.equals(Lan.backToMenu(a.getLanguage()))) {
                editPicItems(String.valueOf(i), update.getCallbackQuery().getMessage(), "Лого");
            }
        }
        for (String name : DataBase.showAllProducts(a.getLanguage(), false)) {
            String userid = a.getId();
            String prodId = DataBase.sqlQuery("select id from table0 where " + a.getLanguage() + " ='" + name + "'", "id");
            String type = DataBase.sqlQuery("select type from table0 where " + a.getLanguage() + " ='" + name + "'", "type");
            if (cb.contains(prodId)||cb.contains(name)) {
                if (cb.contains("+++")||cb.contains("---")) {
                    if (cb.contains("+++"+prodId)){
                        DataBase.sql("insert into cart (userid, item) values (" + userid
                                + ",'" + prodId + "')");
                        a.setAddress(Lan.added(a.getLanguage()));
                        a.setAlert(false);
                    } else if (cb.contains("---"+prodId)){
                        DataBase.sql("delete from cart where userid =" + userid
                            + " and item = '" + prodId + "'");
                        a.setAddress(Lan.removed(a.getLanguage()));
                        a.setAlert(false);
                    }
                    editPicItems(type, update.getCallbackQuery().getMessage(), "Лого");
                } else if (cb.contains(Lan.removeFromCart(a.getLanguage()))
                        || cb.contains(Lan.addToCart(a.getLanguage()))
                        || cb.contains(Lan.addMore(a.getLanguage()))) {
                    if (cb.contains(Lan.removeFromCart(a.getLanguage()))) {
                        DataBase.sql("delete from cart where userid =" + userid
                                + " and item = '" + prodId + "'");
                        a.setAddress(Lan.removed(a.getLanguage()));
                        a.setAlert(false);
                    } else if (cb.contains(Lan.addToCart(a.getLanguage()))|| cb.contains(Lan.addMore(a.getLanguage()))) {
                        DataBase.sql("insert into cart (userid, item) values (" + userid
                                    + ",'" + prodId + "')");
                        a.setAddress(Lan.added(a.getLanguage()));
                        a.setAlert(false);
                    }
                    editCaption(productText(prodId, userid), update.getCallbackQuery().getMessage(), markUp(productText(prodId, userid), prodId, (occurrences(prodId, userid)>0)?keybAddMore(name):keybAdd(name), 3));
                } else if (cb.contains("delete"+prodId)){
                    DataBase.sql("delete from cart where userid =" + a.getId()
                    + " and item = '" + prodId + "'");
                    List<String> items = DataBase.sqlQueryList("select item from cart where userid =" + a.getId(), "item");
                    if (items.size()>0) {
                        editCaption(Lan.mainMenu(a.getLanguage()).get(3) + "\n"
                            + curretCart(a.getId()), update.getCallbackQuery().getMessage().getChatId().toString(),
                        Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")),
                        deleteItemsKey(a.getId()));
                        a.setAddress(Lan.removed(a.getLanguage()));
                        a.setAlert(false);
                    } else {
                        a.setAddress(Lan.cartCleared(a.getLanguage()));
                        a.setAlert(false);
                        editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getCallbackQuery().getMessage(), Lan.mainMenu(a.getLanguage()), "Лого", 2);

                    }
                } else if (cb.equals(prodId)) {
                    editPic(productText(prodId, userid), prodId, update.getCallbackQuery().getMessage(), markUp(productText(prodId, userid), prodId, (occurrences(prodId, userid)>0)?keybAddMore(name):keybAdd(name), 3));
                }
            }
        }
        if (cb.contains(Lan.clearCart(a.getLanguage()))) {
            clearCart(update);
            a.setAddress(Lan.cartCleared(a.getLanguage()));
            a.setAlert(true);
            editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getCallbackQuery().getMessage(), Lan.mainMenu(a.getLanguage()), "Лого", 2);
        }
        if (cb.contains(Lan.delivery(a.getLanguage()))) {
            ZoneId z = ZoneId.of("Asia/Tashkent");
            if (LocalTime.now(z).getHour()>3&&LocalTime.now(z).isBefore(LocalTime.now(z).withHour(18).withMinute(20))) {
                if (DataBase.sqlQueryList("select product from zakaz where userid =" + a.getId()+" and conformed = true", "product").size() > 0) {
                    editPic(Lan.orderExists(a.getLanguage()), update.getCallbackQuery().getMessage(), Lan.YesNo(a.getLanguage()), "Лого", 2);
                } else {
                    DataBase.sql("insert into zakaz (userid, product) values ("
                    +a.getId()+", '"
                    +curretCart(a.getId())+"' )");
                    String address = null;
                    boolean hasLocation = DataBase.sqlQuery("select latitude from users where id ="+a.getId(), "latitude")!=null;
                    boolean hasAddress = DataBase.sqlQuery("select address from users where id ="+a.getId(), "address")!=null;
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.YesNo(a.getLanguage()).get(0)))
                                .setCallbackData("UseOldLocation"));
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.YesNo(a.getLanguage()).get(1)))
                                .setCallbackData("UseNewLocation"));
                    rows.add(row);
                    markup.setKeyboard(rows);
                    if (hasLocation) {
                        editCaption(Lan.useOldLocation(a.getLanguage()), update.getCallbackQuery().getMessage(), markup);
                    } else if (hasAddress) {
                        address = DataBase.sqlQuery("select address from users where id ="+a.getId(), "address");
                        if (address!=null) {
                        editCaption(Lan.useOldAddress(a.getLanguage())+"\n\n"+address, update.getCallbackQuery().getMessage(), markup);
                        }
                    } else {
                        sendMeLocation(update.getCallbackQuery().getMessage());
                    }
                }
            } else {
                a.setAddress(Lan.tooLate(a.getLanguage()));
                a.setAlert(true);
            }
        }
        if (cb.equals(Lan.YesNo(a.getLanguage()).get(0))||(cb.equals(Lan.YesNo(a.getLanguage()).get(1)))) {
            if (cb.equals(Lan.YesNo(a.getLanguage()).get(0))) {
                DataBase.sql("update zakaz set conformed = false, time = null where userid =" + a.getId());
                sendMeLocation(update.getCallbackQuery().getMessage());
            } else if (cb.equals(Lan.YesNo(a.getLanguage()).get(1))) {
                showOrders(update);
            }
        }
        if (cb.contains(Lan.clearOrders(a.getLanguage()))) {
            clearOrders(update);
            editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getCallbackQuery().getMessage(),
                    Lan.mainMenu(a.getLanguage()), "Лого", 2);
        }
        if (cb.contains("Отмена")) {
            showCart(update, true);
        }
        if (cb.contains("OrderTime")) {
            String time = cb.substring(9);
            DataBase.sql("update zakaz set time ='"+time+"' where userid = "+a.getId());
            confirm(update, time);
        }
        if (cb.contains("UseNewLocation")) {
            sendMeLocation(update.getCallbackQuery().getMessage());
        }
        if (cb.contains("UseOldLocation")) {
            editCaption(Lan.orderTime(a.getLanguage()) + Lan.tooLate(a.getLanguage()), a.getId(),
                Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")),
                timeKeys());
        }
        if (cb.contains(Lan.removeSelectively(a.getLanguage()))) {
            editCaption(Lan.mainMenu(a.getLanguage()).get(3) + "\n" + curretCart(a.getId()), update.getCallbackQuery().getMessage().getChatId().toString(),
                Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + update.getCallbackQuery().getMessage().getChatId(), "image")),
                deleteItemsKey(a.getId()));
        }
        if (a.getAddress()!=null) answer.setShowAlert(a.getAlert()).setText(a.getAddress());
        execute(answer);
    }










    private String productText(String prodId, String userid) throws SQLException {
        int occurrences = occurrences(prodId, userid);
        String balls = "";
        int type = Integer.parseInt(DataBase.sqlQuery("select type from table0 where id =" + prodId + "", "type"));
        for (int i = 0; i<occurrences; i++){
            balls += Lan.emogisList().get(type);
        }
        return "<b>" + DataBase.sqlQuery("select "+ a.getLanguage() + " from table0 where id=" + prodId + "", a.getLanguage()) + "</b>\n"+
                            "<i>"+DataBase.sqlQuery("select "+a.getLanguage()+"description from table0 where id =" +prodId, a.getLanguage()+"description")+"</i>\n\n"
                            +Lan.cost(a.getLanguage())+DataBase.sqlQuery("SELECT cost from table0 where id = " + prodId, "cost")+Lan.currency(a.getLanguage()) +".\n"
                            +Lan.inCart(a.getLanguage(), occurrences) + balls;
        }










    private int occurrences(String prodId, String userid) throws SQLException {
        List<String> items = DataBase.sqlQueryList("select item from cart where userid =" + userid, "item");
        return Collections.frequency(items, prodId);
    }








    private void clearCart(Update update) throws TelegramApiException{
        DataBase.sql("delete from cart where userid =" + a.getId());
    }











    private void clearOrders(Update update) throws SQLException, TelegramApiException{
        boolean confirmed = DataBase.sqlQueryBoolean("select conformed from zakaz where userid = "+ a.getId(), "conformed");
        if (confirmed) {
            Adminbot order = new Adminbot();
            order.sendMe("Заказ от " + a.getFirstName()+" отменён\n"+
                        DataBase.sqlQuery("select product from zakaz where userid = "+ a.getId(), "product"));
        }
        a.setAddress(Lan.orderCancelled(a.getLanguage()));
        a.setAlert(true);
        DataBase.sql("delete from zakaz where userid =" + a.getId());
    }











    private List<String> keybAdd(String name) throws SQLException {
        List<String> keyb = new ArrayList<>();
        keyb.add(Lan.addToCart(a.getLanguage()));
        keyb.add(Lan.listTypes(a.getLanguage()).get(Integer.parseInt(DataBase.sqlQuery("SELECT type from table0 where " + a.getLanguage() + " = '" + name + "'", "type"))));
        return keyb;
    }






    private List<String> keybAddMore(String name) throws SQLException {
        List<String> keyb = new ArrayList<>();
        keyb.add(Lan.addMore(a.getLanguage()));
        keyb.add(Lan.listTypes(a.getLanguage()).get(Integer.parseInt(DataBase.sqlQuery("SELECT type from table0 where " + a.getLanguage() + " = '" + name + "'", "type"))));
        keyb.add(Lan.removeFromCart(a.getLanguage()));
        return keyb;
    }










    private void chooseLanguage(Message message, boolean edit) throws SQLException, TelegramApiException {
        List<String> list = new ArrayList<String>();
        list.add("O'zbek");
        list.add("Русский");
        list.add("English");
        if (edit) editPic(":uz: Tilni tanlang\n" +
                ":ru: Выберите язык\n" +
                ":gb: Choose language", message, list, "Лого",3);
        else {
            sendPic(":uz: Tilni tanlang\n" +
                    ":ru: Выберите язык\n" +
                    ":gb: Choose language", message, list, "Лого", 3);
            deleteMessage(message);
        }
    }












    public void sendMeNumber(String ChatId) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(ChatId)
                .setText(EmojiParser.parseToUnicode(Lan.sendMeContact(a.getLanguage())))
                .setParseMode("HTML");
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton keyboardButton =
                new KeyboardButton()
                        .setRequestContact(true)
                        .setText(EmojiParser.parseToUnicode(Lan.myContact(a.getLanguage())));
        List<KeyboardRow> rows2 = new ArrayList<KeyboardRow>();
        row2.add(keyboardButton);
        rows2.add(row2);
        replyMarkup.setKeyboard(rows2).setResizeKeyboard(true).setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyMarkup);
        try {
            int smid = execute(sendMessage).getMessageId();
            DataBase.sql("update users set smid =" + smid + " where id = " + ChatId);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }









public void sendMeLocation(Message message) throws TelegramApiException, SQLException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(Lan.clearOrders(a.getLanguage())))
                            .setCallbackData("Отмена"));
        rows.add(row);
        markup.setKeyboard(rows);
        editCaption(Lan.sendMeLocation(a.getLanguage()), message, markup);
        DataBase.sql("update users set rmid = 0 where id = " + message.getChatId());
    }










    public void editPicItems(String typeID, Message message,String productId) throws TelegramApiException, SQLException {
        List<String> listID = DataBase.sqlQueryList("select id from table0 where instock = true and type = '"+typeID +"'", "id");
        if (listID.size() != 0) {
            String file_id;
            if (productId.equals("Лого"))
                file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
            else file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId, "imageid");
            InputMediaPhoto imp = new InputMediaPhoto();
            imp.setMedia(file_id);
            String typename = Lan.listTypes(a.getLanguage()).get(Integer.parseInt(typeID));
            imp.setCaption(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(0)+"    "+typename)).setParseMode("HTML");
            EditMessageMedia em = new EditMessageMedia();
            em.setChatId(message.getChatId());
            em.setMessageId(message.getMessageId());
            em.setMedia(imp);
            InlineKeyboardMarkup markup = listMarkup(listID, message.getChatId());
            em.setReplyMarkup(markup);
            execute(em);
        } else {
            a.setAddress(Lan.emptyOrders(a.getLanguage()));
            a.setAlert(true);
        }

    }










    public void editPic(String text, String chatid, int messageid, List<String> list, String productId, int flag) throws TelegramApiException, SQLException {
        String file_id;
        if (productId.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId, "imageid");
        //Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
        InputMediaPhoto imp = new InputMediaPhoto();
        imp.setMedia(file_id);
        imp.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        EditMessageMedia em = new EditMessageMedia();
        em.setChatId(chatid);
        em.setMessageId(messageid);
        em.setMedia(imp);
        InlineKeyboardMarkup markup = markUp(text, productId,list, flag);
        em.setReplyMarkup(markup);
        execute(em);
        DataBase.sql("update users set image =" + messageid + " where id =" + chatid);
    }











    public void editPic(String text, Message message, List<String> list, String productId, int flag) throws TelegramApiException, SQLException {
        String file_id;
        if (productId.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId, "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        //Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
        InputMediaPhoto imp = new InputMediaPhoto();
        imp.setMedia(file_id);
        imp.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        EditMessageMedia em = new EditMessageMedia();
        em.setChatId(message.getChatId());
        em.setMessageId(message.getMessageId());
        em.setMedia(imp);
        InlineKeyboardMarkup markup = markUp(text, productId,list, flag);
        em.setReplyMarkup(markup);
        execute(em);
        DataBase.sql("update users set image =" + message.getMessageId() + " where id =" + message.getChatId());
    }









    public void editPic(String text, String productId, Message message, InlineKeyboardMarkup markup) throws TelegramApiException, SQLException {
        String file_id;
        if (productId.equals("Лого")||productId==null)
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId, "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        InputMediaPhoto imp = new InputMediaPhoto();
        imp.setMedia(file_id);
        imp.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        EditMessageMedia em = new EditMessageMedia();
        em.setChatId(message.getChatId());
        em.setMessageId(message.getMessageId());
        em.setMedia(imp);
        em.setReplyMarkup(markup);
        execute(em);
        DataBase.sql("update users set image =" + message.getMessageId() + " where id =" + message.getChatId());
    }









    public void editCaption(String text, String chatId, int messageid, InlineKeyboardMarkup markup) throws TelegramApiException, SQLException {
        //Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
        EditMessageCaption ec = new EditMessageCaption();
        ec.setChatId(chatId);
        ec.setMessageId(messageid);
        ec.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        if (markup!=null) ec.setReplyMarkup(markup);
        execute(ec);
    }












    public void editCaption(String text, Message message, InlineKeyboardMarkup markup) throws TelegramApiException, SQLException {
        //Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
        EditMessageCaption ec = new EditMessageCaption();
        ec.setChatId(message.getChatId().toString());
        ec.setMessageId(message.getMessageId());
        ec.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        if (markup!=null) ec.setReplyMarkup(markup);
        execute(ec);
    }









    public void sendPic(String text, Message message, List<String> inline, String productName, int flag) throws SQLException, TelegramApiException {
        String file_id = "";
        if (productName.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where " + a.getLanguage() + " = '" + productName + "'", "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        SendPhoto aa = new SendPhoto();
        aa.setChatId(message.getChatId());
        aa.setPhoto(file_id);
        aa.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        InlineKeyboardMarkup inlineMarkup = markUp(text, "Лого",inline, flag);
        aa.setReplyMarkup(inlineMarkup);

        try {
            String image = execute(aa).getMessageId().toString();
            DataBase.sql("update users set image =" + image + " where id =" + message.getChatId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }









    public void deleteMessage(Message message) {
        DeleteMessage dm = new DeleteMessage()
                .setMessageId(message.getMessageId())
                .setChatId(message.getChatId());
        try {
            execute(dm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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







    private void handleVoice(Message message) {
        deleteMessage(message);
    }








    private void handleVideoNote(Message message) {
        deleteMessage(message);
    }









    private void handleVideo(Message message) {
        deleteMessage(message);

    }







    private void handleSticker(Message message) {
        deleteMessage(message);
    }








    private void handleLocation(Update update) throws SQLException, TelegramApiException {
        if (waitingForLocation(update.getMessage())) {
            if (update.getMessage().hasLocation()) DataBase.sql("update users set latitude = '"+update.getMessage().getLocation().getLatitude()+"', longitude = '"+update.getMessage().getLocation().getLongitude()+"', address = null where id ="+ a.getId());
            else DataBase.sql("update users set latitude = null, longitude = null, address = '"+update.getMessage().getText()+"' where id ="+ a.getId());
            editCaption(Lan.orderTime(a.getLanguage()) + Lan.tooLate(a.getLanguage()), a.getId(),
                Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")),
                timeKeys());
            DataBase.sql("update users set rmid = 1 where id = " + a.getId());
        }
        deleteMessage(update.getMessage());
    }











    private InlineKeyboardMarkup deleteItemsKey(String id) throws SQLException {
        List<String> menu = DataBase.sqlQueryList("select DISTINCT item from cart where userid ="+id, "item");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            for (int i = 0; i<menu.size(); i+=3) {
                List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(":heavy_multiplication_x:"+DataBase.sqlQuery("select "+a.getLanguage()+" from table0 where id ="+menu.get(i), a.getLanguage())))
                                .setCallbackData("delete"+menu.get(i)));
                if((i+1)<menu.size()) row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(":heavy_multiplication_x:"+DataBase.sqlQuery("select "+a.getLanguage()+" from table0 where id ="+menu.get(i+1), a.getLanguage())))
                                .setCallbackData("delete"+menu.get(i+1)));
                if((i+2)<menu.size()) row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(":heavy_multiplication_x:"+DataBase.sqlQuery("select "+a.getLanguage()+" from table0 where id ="+menu.get(i+2), a.getLanguage())))
                                .setCallbackData("delete"+menu.get(i+2)));
                rows.add(row1);
            }
            List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
            row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(3)))
                                .setCallbackData(Lan.mainMenu(a.getLanguage()).get(3)));
            row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                                .setCallbackData(Lan.backToMenu(a.getLanguage())));

            rows.add(row1);
            markup.setKeyboard(rows);
        return markup;
    }









    private InlineKeyboardMarkup timeKeys() {
        List<String> menu = new ArrayList<String>();
        ZoneId z = ZoneId.of("Asia/Tashkent");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        int minutes = LocalTime.now(z).getMinute();
        int hours = LocalTime.now(z).getHour();
        if (hours<8) {
            for (int i = 0; i<601; i+=30) {
                menu.add(dtf.format(LocalTime.now(z).withHour(9).withMinute(0).plusMinutes(i)));
            }
        } else if (hours<19) {
            int last=60;
            if (hours<18) {
                if (minutes<5) {
                    menu.add(dtf.format(LocalTime.now(z).plusHours(1).truncatedTo(ChronoUnit.HOURS)));
                    last = 90;
                } else if (minutes<15) {
                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(75)));
                    last = 90;
                } else if (minutes<30) {
                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(90)));
                    last = 120;
                } else if (minutes<45) {
                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(105)));
                    last = 120;
                } else {
                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusHours(2)));
                    last = 150;
                }
                for (int i = last; i<(19-LocalTime.now(z).getHour())*60+1; i+=30) {
                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(i)));
                }
            } else {
                if (minutes<20) {
                    menu.add(dtf.format(LocalTime.now(z).withHour(19).withMinute(0)));
                } else {
                    menu.add(Lan.tooLate(a.getLanguage()));
                }
            }
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            for (int i = 0; i<menu.size(); i+=3) {
                List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(menu.get(i)))
                                .setCallbackData("OrderTime"+menu.get(i)));
                if((i+1)<menu.size()) row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(menu.get(i+1)))
                                .setCallbackData("OrderTime"+menu.get(i+1)));
                if((i+2)<menu.size()) row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(menu.get(i+2)))
                                .setCallbackData("OrderTime"+menu.get(i+2)));
                rows.add(row1);
            }
            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
            row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.clearOrders(a.getLanguage())))
                                .setCallbackData(Lan.clearOrders(a.getLanguage())));
            rows.add(row);
            markup.setKeyboard(rows);

        return markup;
    }











    private boolean waitingForLocation(Message message) throws SQLException{
        return DataBase.sqlQuery("SELECT rmid from users where id=" + message.getChatId(), "rmid").equals("0");
    }














    private void confirm(Update update, String time) throws SQLException, TelegramApiException {
        String address = DataBase.sqlQuery("select address from users where id ="+a.getId(), "address");
        String latitude = DataBase.sqlQuery("select latitude from users where id ="+a.getId(), "latitude");
        String longitude = DataBase.sqlQuery("select longitude from users where id ="+a.getId(), "longitude");
        if (address!=null) address= "<b>Адрес:</b> "+address+"\n";
        else address="";
        Adminbot order = new Adminbot();
        order.sendMe("<b>Новый заказ</b>\n\n"
                    +"<b>Имя клиента:</b> "+ a.getFirstName()+"\n"
                    +"<b>Номер клиента:</b> "+ a.getNumber()+"\n"
                    +"<b>Время доставки:</b> "+time+"\n"
                    +address
                    +"<b>Заказ:</b> \n\n"+curretCart(a.getId()));
        if (latitude!=null&&longitude!=null) order.sendLocation(Float.parseFloat(latitude), Float.parseFloat(longitude));
        order.sendContact(a.getFirstName(), a.getNumber());
        clearCart(update);
        DataBase.sql("update zakaz set conformed = true where userid = " + a.getId());
        editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), a.getId(),
            Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")),
            Lan.mainMenu(a.getLanguage()), "Лого", 2);
        a.setAddress(Lan.orderPlaced(a.getLanguage()));
        a.setAlert(true);
    }












    private void handleDocument(Message message) {
        deleteMessage(message);
    }








    private void handleAudio(Message message) {
        Adminbot ab = new Adminbot();
        ab.forwardMessage(message, ab.myID);
    }









    private void handleAnimation(Message message) {
        Adminbot ab = new Adminbot();
        ab.forwardMessage(message, ab.myID);
    }










    private void handlePhoto(Message message) throws SQLException {
        String photoId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
        String caption = message.getCaption();
        DataBase.sql("UPDATE table0 SET imageid = '" + photoId + "' where russian = '" + caption + "'");
        DataBase.sql("UPDATE users SET image = '" + message.getMessageId() + "' where id = '" + message.getChatId() + "'");
        a.setImage(DataBase.sqlGetUserData(message.getChatId().toString()).get(5));
        deleteMessage(message);
    }









    private InlineKeyboardMarkup listMarkup (List<String> list, long id) throws SQLException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            if (list!=null){
                for (String prodID:list) {
                    String name = DataBase.sqlQuery("select "+a.getLanguage()+" from table0 where id ="+ prodID, a.getLanguage());
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(name))
                            .setCallbackData(prodID));
                    if (!DataBase.sqlQueryList("select item from cart where userid = "+id, "item").contains(prodID)) {
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(":heavy_plus_sign:🛒"))
                                .setCallbackData("+++"+prodID));
                    } else {
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(":x:"))
                                .setCallbackData("---"+prodID));
                    }
                    rows.add(row);
                }
            }
        List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.goBack(a.getLanguage())))
                    .setCallbackData(Lan.goBack(a.getLanguage())));
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(3)))
                    .setCallbackData(Lan.mainMenu(a.getLanguage()).get(3)));
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                    .setCallbackData(Lan.backToMenu(a.getLanguage())));
            rows.add(row2);
            markup.setKeyboard(rows);
        return markup;
    }









    private InlineKeyboardMarkup productMarkup(String productId, List<String> list) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            List<InlineKeyboardButton> row0 = new ArrayList<InlineKeyboardButton>();
            row0.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(0)))
                    .setCallbackData(list.get(0)+productId));
            if (list.size() == 3) {
                row0.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(2)))
                        .setCallbackData(list.get(2)+productId));
            }
            List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(1)))
                    .setCallbackData(list.get(1)));
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(3)))
                    .setCallbackData(Lan.mainMenu(a.getLanguage()).get(3)));
            List<InlineKeyboardButton> row3 = new ArrayList<InlineKeyboardButton>();
            row3.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.goBack(a.getLanguage())))
                    .setCallbackData(Lan.goBack(a.getLanguage())));
            row3.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                    .setCallbackData(Lan.backToMenu(a.getLanguage())));
            rows.add(row0);
            rows.add(row2);
            rows.add(row3);
            markup.setKeyboard(rows);
        return markup;
    }











    private InlineKeyboardMarkup markUp(String text, String productId, List<String> list, int flag) throws SQLException {
        InlineKeyboardMarkup markup;
        if (DataBase.sqlQueryList("select id from table0", "id").contains(productId)) {
            markup = productMarkup(productId, list);
        } else {
            markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            if (list!=null){
                for (int i = 0; i < list.size(); i += flag) {
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i)))
                            .setCallbackData(list.get(i)));
                    if ((flag == 2) || (flag == 3)) {
                        if ((i + 1) < list.size()) row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                                .setCallbackData(list.get(i + 1)));
                    }
                    if (flag == 3) {
                        if ((i + 2) < list.size()) row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(list.get(i + 2)))
                                .setCallbackData(list.get(i + 2)));
                    }
                    rows.add(row);
                }
            }
            if (a.getLanguage()!=null) {
                if (text.contains(Lan.mainMenu(a.getLanguage()).get(0)) ||
                        text.contains(Lan.mainMenu(a.getLanguage()).get(1)) ||
                        text.contains(Lan.mainMenu(a.getLanguage()).get(3))) {
                    if (text.contains(Lan.mainMenu(a.getLanguage()).get(3))) {
                        if (text.contains(Lan.currency(a.getLanguage()))) {
                            List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
                            lastRow.add(new InlineKeyboardButton()
                                    .setText(EmojiParser.parseToUnicode(Lan.clearCart(a.getLanguage())))
                                    .setCallbackData(Lan.clearCart(a.getLanguage())));
                            if (DataBase.sqlQueryList("select distinct item from cart where userid ="+a.getId(), "item").size()!=1) {
                                lastRow.add(new InlineKeyboardButton()
                                        .setText(EmojiParser.parseToUnicode(Lan.removeSelectively(a.getLanguage())))
                                        .setCallbackData(Lan.removeSelectively(a.getLanguage())));
                            }
                            rows.add(lastRow);
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton()
                                    .setText(EmojiParser.parseToUnicode(Lan.delivery(a.getLanguage())))
                                    .setCallbackData(Lan.delivery(a.getLanguage())));
                            rows.add(row);
                        }
                    }
                    if (text.contains(Lan.mainMenu(a.getLanguage()).get(1))){
                        if (!text.contains(Lan.emptyOrders(a.getLanguage()))) {
                            List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
                            lastRow.add(new InlineKeyboardButton()
                                    .setText(EmojiParser.parseToUnicode(Lan.clearOrders(a.getLanguage())))
                                    .setCallbackData(Lan.clearOrders(a.getLanguage())));
                            rows.add(lastRow);
                        }
                    }
                    List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
                    if (!text.contains(Lan.mainMenu(a.getLanguage()).get(1))) lastRow.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(Lan.goBack(a.getLanguage())))
                            .setCallbackData(Lan.goBack(a.getLanguage())));
                    if (!text.contains(Lan.mainMenu(a.getLanguage()).get(3))) lastRow.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(3)))
                            .setCallbackData(Lan.mainMenu(a.getLanguage()).get(3)));
                    lastRow.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                            .setCallbackData(Lan.backToMenu(a.getLanguage())));
                    rows.add(lastRow);
                }
                if (text.contains(Lan.chooseDish(a.getLanguage()))) {
                    List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
                    lastRow.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(3)))
                            .setCallbackData(Lan.mainMenu(a.getLanguage()).get(3)));
                    lastRow.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                            .setCallbackData(Lan.backToMenu(a.getLanguage())));
                    rows.add(lastRow);
                }
            }
            markup.setKeyboard(rows);
        }
        return markup;
    }










    private void showCart(Update update, boolean edit) throws TelegramApiException, SQLException {
        List<String> items = DataBase.sqlQueryList("select item from cart where userid =" + a.getId(), "item");
        String text = Lan.mainMenu(a.getLanguage()).get(3) + "\n"
                    + curretCart(a.getId()) +"\n"
                    + Lan.deliveryCost(a.getLanguage())+"\n"
                    +"<b>"+Lan.tooLate(a.getLanguage())+"</b>";
            if (items.size() == 0) {
                a.setAddress(Lan.cartIsEmpty(a.getLanguage()));
                a.setAlert(true);
            } else {
                if (edit) editPic(text, update.getCallbackQuery().getMessage(), null, "Лого", 2);
                else sendPic(text, update.getCallbackQuery().getMessage(), null, "Лого", 2);
            }
    }










    private void showOrders(Update update) throws TelegramApiException, SQLException {
        List<String> items = DataBase.sqlQueryList("select product from zakaz where userid =" + a.getId()+" and conformed = true", "product");
        if (items.size() == 0) {
                a.setAddress(Lan.noOrderYet(a.getLanguage()));
                a.setAlert(true);
        } else {
            String time = DataBase.sqlQuery("select time from zakaz where userid ="+a.getId(), "time");
            String address = DataBase.sqlQuery("select address from users where id ="+a.getId(), "address");
            String latitude = DataBase.sqlQuery("select latitude from users where id ="+a.getId(), "latitude");
            if (address!=null) address= Lan.address(a.getLanguage())+address+"\n";
            else if (latitude!=null) {
                address=Lan.locationReceived(a.getLanguage());
            }
                editPic(Lan.mainMenu(a.getLanguage()).get(1)+"\n"
                    +Lan.deliveryTime(a.getLanguage())+time+"\n"
                    +address
                    +items.get(0)+"\n", update.getCallbackQuery().getMessage(), null, "Лого", 2);
        }
    }












    public void send (String text, long chatId, List<String> inline,List<String> reply, int flag) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        if (inline!=null) {
            InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            for (int i = 0; i < inline.size(); i += flag) {
                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(inline.get(i)))
                        .setCallbackData(inline.get(i)));
                if ((flag==2)||(flag==3)) {
                    if ((i + 1) < inline.size()) {
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(inline.get(i + 1)))
                                .setCallbackData(inline.get(i + 1)));
                    }
                }
                if (flag==3){
                    if ((i + 2) < inline.size()) {
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(inline.get(i + 2)))
                                .setCallbackData(inline.get(i + 2)));
                    }
                }
                rows.add(row);
            }
            inlineMarkup.setKeyboard(rows);
            sendMessage.setReplyMarkup(inlineMarkup);
        }
        if (reply!=null) {
            ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
            List<KeyboardRow> rows2 = new ArrayList<KeyboardRow>();
            for (int i = 0; i < reply.size(); i += flag) {
            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(reply.get(i))));
            if ((flag==2)||(flag==3)) {
                if ((i + 1) < reply.size()) {
                    row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(reply.get(i + 1))));
                }
            }
            if (flag==3){
                if ((i + 2) < reply.size()) {
                    row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(reply.get(i + 2))));
                }
            }
            rows2.add(row2);
            replyMarkup.setKeyboard(rows2).setResizeKeyboard(true).setOneTimeKeyboard(false);
            sendMessage.setReplyMarkup(replyMarkup);
        }
        }
        try {
            int smid = execute(sendMessage).getMessageId();
            DataBase.sql("update users set smid ="+smid+" where id = "+chatId);
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }











    public String curretCart(String id) throws SQLException {
        List<String> items = DataBase.sqlQueryList("select item from cart where userid =" + id, "item");
        String cart = "";
        int result = 0;
        Map<String, List<Integer>> itemNames = new HashMap<String, List<Integer>>();
        for (String s : items) {
            Integer number = Collections.frequency(items, s);
            Integer cost = Integer.parseInt(DataBase.sqlQuery("select cost from table0 where id =" + s, "cost"));
            List<Integer> aa = new ArrayList<>();
            aa.add(number);
            aa.add(cost);
            itemNames.put(DataBase.sqlQuery("select "+a.getLanguage()+" from table0 where id =" + s, a.getLanguage()),
                    aa);
        }
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : itemNames.entrySet()) {
            cart += entry.getKey() + "  -  " + entry.getValue().get(0) + " * " + entry.getValue().get(1) + " = " + entry.getValue().get(0) * entry.getValue().get(1) + Lan.currency(a.getLanguage()) + "\n";
            result += entry.getValue().get(0) * entry.getValue().get(1);
            list.add(":heavy_multiplication_x: "+entry.getKey());
        }
        if (list.size()>1) cart += "\n" + Lan.total(a.getLanguage()) + result + Lan.currency(a.getLanguage());
        return cart;
    }










}