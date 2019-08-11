package SecondPackage;

import com.ibm.icu.text.Transliterator;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
import java.sql.Connection;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;

public class Bot extends TelegramLongPollingBot {
    private String botName = "DeliverySuperBot";
    private String botToken = "780864630:AAHpUc01UagThYH7wRi15zJQjwu06A6NaWM";

    Order a;

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
                            DataBase.sqlGetUserData(m.getChatId().toString()).get(3)
                    );
                    if (m.hasText()) handleIncomingText(m);
                    else if (m.hasAnimation()) handleAnimation(m);
                    else if (m.hasAudio()) handleAudio(m);
                    else if (m.hasContact()) handleContact(m);
                    else if (m.hasDocument()) handleDocument(m);
                    else if (m.hasPhoto()) handlePhoto(m);
                    else if (m.hasLocation()) handleLocation(m);
                    else if (m.hasSticker()) handleSticker(m);
                    else if (m.hasVideo()) handleVideo(m);
                    else if (m.hasVideoNote()) handleVideoNote(m);
                    else if (m.hasVoice()) handleVoice(m);
                } else {
                    DataBase.sql("INSERT INTO users (id, firstname, lastname, username, rmid) VALUES ('" +
                            m.getFrom().getId().toString() + "','" +
                            m.getFrom().getFirstName() + "','" +
                            m.getFrom().getLastName() + "','" +
                            m.getFrom().getUserName() + "','" +
                            m.getMessageId() + "')");
                    a = new Order(
                            DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(0),
                            null,
                            null,
                            DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(3)
                    );
                    Adminbot ab = new Adminbot();
                    ab.sendMe(":boom: Новый пользователь!" +
                            "\n" + m.getFrom().getFirstName() + " " + m.getFrom().getLastName() +
                            "\n@" + m.getFrom().getUserName());
                    if (m.hasText()) handleIncomingText(m);
                }
            } else if (update.hasCallbackQuery()) {
                String cb = update.getCallbackQuery().getData();
                Message cbm = update.getCallbackQuery().getMessage();
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                if (DataBase.sqlIdList().contains(chatId)) {
                    a = new Order(DataBase.sqlGetUserData(chatId).get(0),
                            DataBase.sqlGetUserData(chatId).get(1),
                            DataBase.sqlGetUserData(chatId).get(2),
                            DataBase.sqlGetUserData(chatId).get(3)
                    );
                } else {
                    DataBase.sql("INSERT INTO users (id, firstname, lastname, username, rmid) VALUES ('" +
                            chatId + "','" +
                            update.getCallbackQuery().getFrom().getFirstName() + "','" +
                            update.getCallbackQuery().getFrom().getLastName() + "','" +
                            update.getCallbackQuery().getFrom().getUserName() + "','" +
                            cbm.getMessageId() + "')");
                    a = new Order(
                            DataBase.sqlGetUserData(update.getCallbackQuery().getFrom().getId().toString()).get(0),
                            null,
                            null,
                            DataBase.sqlGetUserData(update.getCallbackQuery().getFrom().getId().toString()).get(3)
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
                    message.getText() + " WHERE id =" + message.getFrom().getId());
        } else {
            DataBase.sql("UPDATE users SET phone = " +
                    message.getContact().getPhoneNumber() + " WHERE id =" + message.getFrom().getId());
        }
        a.setNumber(DataBase.sqlGetUserData(message.getChatId().toString()).get(1));
        deleteMessage(DataBase.sqlQuery("SELECT smid from users where id=" + message.getChatId(), "smid"), message.getChatId().toString());
        deleteMessage(message);
        if (a.getNumber() != null) sendPic(Lan.welcome(a.getLanguage(), message.getFrom().getFirstName()),
                message, Lan.mainMenu(a.getLanguage()), "Лого", 2);
    }

    private void handleCallback(Update update) throws TelegramApiException, SQLException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId()).setShowAlert(false);
        execute(answer);
        String cb = update.getCallbackQuery().getData();
        if (cb.equals("O'zbek") || cb.equals("Русский") || cb.equals("English")) {
            if (cb.equals("O'zbek")) {
                DataBase.sql("UPDATE users SET language = 'Uzbek' WHERE id =" + update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("Uzbek");
            } else if (cb.equals("Русский")) {
                DataBase.sql("UPDATE users SET language = 'Russian' WHERE id =" + update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("Russian");
            } else {
                DataBase.sql("UPDATE users SET language = 'English' WHERE id =" + update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("English");
            }
            if (a.getNumber() == null) {
                deleteMessage(update.getCallbackQuery().getMessage());
                sendMeNumber(update.getCallbackQuery().getMessage().getChatId());
            } else {
                editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getCallbackQuery().getMessage(), Lan.mainMenu(a.getLanguage()), "Лого", 2);
            }
        }
        if (cb.equals(Lan.backToMenu(a.getLanguage()))) {
            editPic(Lan.welcome(a.getLanguage(), DataBase.sqlselect(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "firstname")), update.getCallbackQuery().getMessage(),
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
                editPic(Lan.mainMenu(a.getLanguage()).get(1)+Lan.deliveryCost(a.getLanguage()), update.getCallbackQuery().getMessage(), null, "Лого", 2);
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
                showCart(update);
            }
        }
        for (String t : Lan.listTypes(a.getLanguage())) {
            String language = a.getLanguage();
            List<String> list = DataBase.showProducts(language, language, String.valueOf(Lan.listTypes(language).indexOf(t)));

            if (cb.equals(t) && !cb.equals(Lan.backToMenu(a.getLanguage()))) {
                String nothing = "";
                if (list.size() < 1) nothing = Lan.emptyOrders(a.getLanguage());
                editPic(Lan.mainMenu(a.getLanguage()).get(0)+" :arrow_forward: "+t + "\n" + nothing, update.getCallbackQuery().getMessage(), list, "Лого", 1);
            }
        }
        if (cb.contains(Lan.removeFromCart(a.getLanguage())) || cb.contains(Lan.addToCart(a.getLanguage()))) {
            String prodId = cb.substring(0, 4);
            if (cb.contains(Lan.removeFromCart(a.getLanguage()))) {
                DataBase.sql("delete from cart where userid =" + update.getCallbackQuery().getFrom().getId()
                        + " and item = '" + prodId + "'");
            } else if (cb.contains(Lan.addToCart(a.getLanguage()))) {
                DataBase.sql("insert into cart (userid, item) values (" + update.getCallbackQuery().getFrom().getId()
                        + ",'" + prodId + "')");
            }
            String name = DataBase.sqlQuery("select " + a.getLanguage() + " from table0 where id =" + prodId, a.getLanguage());
            int occurrences = 0;
            String total = "";
            List<String> items = DataBase.sqlQueryList("select item from cart where userid =" + update.getCallbackQuery().getFrom().getId(), "item");
            if (items.contains(prodId)) {
                occurrences = Collections.frequency(items, prodId);
                total = Lan.inCart(a.getLanguage(), occurrences);
            }
            String text = "<b>" + name + "</b>\n" +
                    Lan.cost(a.getLanguage()) +
                    DataBase.sqlQuery("SELECT cost from table0 where id = " + prodId, "cost") +
                    Lan.currency(a.getLanguage()) +
                    ".    " + total;
            editCaption(text, update.getCallbackQuery().getMessage(), keyb(occurrences, name), prodId, 3);
        }

        for (String name : DataBase.showAllProducts(a.getLanguage())) {
            String prodId = DataBase.sqlQuery("select id from table0 where " + a.getLanguage() + " ='" + name + "'", "id");
            if (cb.contains(name)&&!cb.contains(":heavy_multiplication_x:")) {
                List<String> cart = DataBase.sqlQueryList("select item from cart where userid =" + update.getCallbackQuery().getFrom().getId(), "item");
                int occurrences = 0;
                String total = "";
                if (cart.contains(prodId)) {
                    occurrences = Collections.frequency(cart, prodId);
                    total = Lan.inCart(a.getLanguage(), occurrences);
                }
                editPic("<b>" + name + "</b>\n" + Lan.cost(a.getLanguage()) + DataBase.sqlQuery("SELECT cost from table0 where " + a.getLanguage() + " = '" + name + "'", "cost") + Lan.currency(a.getLanguage()) + ".    " + total,
                        update.getCallbackQuery().getMessage(), keyb(occurrences, name), prodId, 3);
            } else if (cb.contains(":heavy_multiplication_x: "+name)){
                DataBase.sql("delete from cart where userid =" + update.getCallbackQuery().getFrom().getId()
                        + " and item = '" + prodId + "'");
                showCart(update);
            }
        }
        if (cb.contains(Lan.clearCart(a.getLanguage()))) {
            DataBase.sql("delete from cart where userid =" + update.getCallbackQuery().getFrom().getId());
            showCart(update);
        }
    }

    private List<String> keyb(Integer occurances, String name) throws SQLException {
        List<String> keyb = new ArrayList<>();

        keyb.add(Lan.addToCart(a.getLanguage()));
//                keyb.add(":point_left: "+prevName);
//                keyb.add(nextName+" :point_right:");
        keyb.add(Lan.listTypes(a.getLanguage()).get(Integer.parseInt(DataBase.sqlQuery("SELECT type from table0 where " + a.getLanguage() + " = '" + name + "'", "type"))));
        if (occurances > 0) keyb.add(Lan.removeFromCart(a.getLanguage()));
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

    public void sendMeNumber(long ChatId) {
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

    private void handleIncomingText(Message m) throws SQLException, TelegramApiException {
        if (m.getText().equals("/start")) {
            if (a.getSentMessage() != null)
                deleteMessage(DataBase.sqlQuery("SELECT smid from users where id=" + m.getChatId(), "smid"), m.getChatId().toString());
            deleteMessage(m);
            if (a.getLanguage() == null) {
                chooseLanguage(m, false);
            } else {
                sendPic(Lan.welcome(a.getLanguage(), a.getFirstName()), m, Lan.mainMenu(a.getLanguage()), "Лого", 2);
            }
        } else if (m.getText().startsWith("+998")) {
            if (a.getNumber() == null) {
                handleContact(m);
            }
        }
    }

    public void editPic(String text, Message message, List<String> list, String productId, int flag) throws TelegramApiException, SQLException {
        String file_id;
        if (productId.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId, "imageid");
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
    }
    public void editCaption(String text, Message message, List<String> list, String productId, int flag) throws TelegramApiException, SQLException {
        //Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
        EditMessageCaption ec = new EditMessageCaption();
        ec.setChatId(message.getChatId().toString());
        ec.setMessageId(message.getMessageId());
        ec.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        InlineKeyboardMarkup markup = markUp(text, productId,list, flag);
        ec.setReplyMarkup(markup);
        execute(ec);
    }
    public void sendPic(String text, Message message, List<String> inline, String productName, int flag) throws SQLException, TelegramApiException {
        String file_id = "";
        if (productName.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where " + a.getLanguage() + " = '" + productName + "'", "imageid");
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



    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
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

    private void handleLocation(Message message) {
        Adminbot ab = new Adminbot();
        ab.forwardMessage(message, ab.myID);
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

    private InlineKeyboardMarkup productMarkup(String productId, List<String> list) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            List<InlineKeyboardButton> row0 = new ArrayList<InlineKeyboardButton>();
            row0.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(0)))
                    .setCallbackData(productId + list.get(0)));
            if (list.size() == 3) {
                row0.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(2)))
                        .setCallbackData(productId + list.get(2)));
            }
            List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(1)))
                    .setCallbackData(list.get(1)));
            List<InlineKeyboardButton> row3 = new ArrayList<InlineKeyboardButton>();
            row3.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.goBack(a.getLanguage())))
                    .setCallbackData(a.getLanguage()));
            row3.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(3)))
                    .setCallbackData(Lan.mainMenu(a.getLanguage()).get(3)));
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
        if (DataBase.sqlQueryList("select * from table0", "id").contains(productId)) {
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
            if (text.contains(Lan.total(a.getLanguage()))) {
                List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
                lastRow.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(Lan.clearCart(a.getLanguage())))
                        .setCallbackData(Lan.goBack(a.getLanguage())));
                rows.add(lastRow);
            }
            if (text.contains(Lan.mainMenu(a.getLanguage()).get(0)) ||
                    text.contains(Lan.mainMenu(a.getLanguage()).get(1)) ||
                    text.contains(Lan.mainMenu(a.getLanguage()).get(3))) {
                List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
                lastRow.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(Lan.goBack(a.getLanguage())))
                        .setCallbackData(Lan.goBack(a.getLanguage())));
                lastRow.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                        .setCallbackData(Lan.backToMenu(a.getLanguage())));
                rows.add(lastRow);
            }

            markup.setKeyboard(rows);
        }
        return markup;
    }
    private void showCart(Update update) throws TelegramApiException, SQLException {
        List<String> items = DataBase.sqlQueryList("select item from cart where userid =" + update.getCallbackQuery().getMessage().getChatId(), "item");
        if (items.size() == 0) {
            editPic(Lan.mainMenu(a.getLanguage()).get(3) + "\n" + Lan.emptyOrders(a.getLanguage()), update.getCallbackQuery().getMessage(), null, "Лого", 2);
        } else {
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
            cart += "\n" + Lan.total(a.getLanguage()) + result + Lan.currency(a.getLanguage());
            editPic(Lan.mainMenu(a.getLanguage()).get(3) + "\n" + cart, update.getCallbackQuery().getMessage(), list, "Лого", 2);
        }
    }
}