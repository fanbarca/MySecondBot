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
    private String botName ="DeliverySuperBot";
    private String botToken ="780864630:AAHpUc01UagThYH7wRi15zJQjwu06A6NaWM";

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
                        DataBase.sql("INSERT INTO users (id, firstname, lastname, username, rmid) VALUES ('"+
                                m.getFrom().getId().toString()+"','"+
                                m.getFrom().getFirstName()+"','"+
                                m.getFrom().getLastName()+"','"+
                                m.getFrom().getUserName()+"','"+
                                m.getMessageId()+"')");
                            a = new Order(
                                    DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(0),
                                    null,
                                    null,
                                    DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(3)
                            );
                        Adminbot ab = new Adminbot();
                        ab.sendMe(":boom: Новый пользователь!" +
                                "\n" + m.getFrom().getFirstName() +" "+ m.getFrom().getLastName() +
                                "\n@" + m.getFrom().getUserName());
                        if (m.hasText()) handleIncomingText(m);
                    }
            } else if (update.hasCallbackQuery()) {
                String cb = update.getCallbackQuery().getData();
                Message cbm = update.getCallbackQuery().getMessage();
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                if (DataBase.sqlIdList().contains(chatId)) {
                a = new Order(  DataBase.sqlGetUserData(chatId).get(0),
                                DataBase.sqlGetUserData(chatId).get(1),
                                DataBase.sqlGetUserData(chatId).get(2),
                                DataBase.sqlGetUserData(chatId).get(3)
                                );
                } else {
                    DataBase.sql("INSERT INTO users (id, firstname, lastname, username, rmid) VALUES ('"+
                                chatId+"','"+
                                update.getCallbackQuery().getFrom().getFirstName()+"','"+
                                update.getCallbackQuery().getFrom().getLastName()+"','"+
                                update.getCallbackQuery().getFrom().getUserName()+"','"+
                                cbm.getMessageId()+"')");
                            a = new Order(
                                    DataBase.sqlGetUserData(update.getCallbackQuery().getFrom().getId().toString()).get(0),
                                    null,
                                    null,
                                    DataBase.sqlGetUserData(update.getCallbackQuery().getFrom().getId().toString()).get(3)
                            );
                }
                if (a.getLanguage()==null&&!(cb.equals("O'zbek")||cb.equals("Русский")||cb.equals("English"))) chooseLanguage(update.getCallbackQuery().getMessage(), false);
                else handleCallback(update);
            }
        } catch(Exception e){
                BotLogger.error(Main.LOGTAG, e);
        }
    }
    private void handleContact(Message message) throws SQLException, TelegramApiException {
            DataBase.sql("UPDATE users SET phone = "+
                    message.getContact().getPhoneNumber()+
                    " WHERE id ="+message.getFrom().getId());
            a.setNumber(DataBase.sqlGetUserData(message.getChatId().toString()).get(1));
        deleteMessage(DataBase.sqlQuery("SELECT smid from users where id="+message.getChatId(), "smid"), message.getChatId().toString());
        deleteMessage(message);
        sendPic(Lan.welcome(a.getLanguage(), message.getFrom().getFirstName()),
                    message, Lan.mainMenu(a.getLanguage()),"Лого", 2);
    }
    private void handleCallback(Update update) throws TelegramApiException, SQLException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId()).setShowAlert(false);
        execute(answer);
        String cb = update.getCallbackQuery().getData();
        if (cb.equals("O'zbek")||cb.equals("Русский")||cb.equals("English")){
            if (cb.equals("O'zbek")) {
                DataBase.sql("UPDATE users SET language = 'Uzbek' WHERE id ="+update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("Uzbek");
            }
            else if (cb.equals("Русский")) {
                DataBase.sql("UPDATE users SET language = 'Russian' WHERE id ="+update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("Russian");
            }
            else {
                DataBase.sql("UPDATE users SET language = 'English' WHERE id ="+update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("English");
            }
            if (a.getNumber()==null) {
                deleteMessage(update.getCallbackQuery().getMessage());
                sendMeNumber(update.getCallbackQuery().getMessage().getChatId());
            } else {
                editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), update.getCallbackQuery().getMessage(), Lan.mainMenu(a.getLanguage()), "Лого",2);
            }
        }
        if (cb.equals(Lan.backToMenu(a.getLanguage()))) {
            editPic(Lan.welcome(a.getLanguage(), DataBase.sqlselect(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "firstname")), update.getCallbackQuery().getMessage(),
            Lan.mainMenu(a.getLanguage()), "Лого", 2);
           }
        if (cb.equals(Lan.mainMenu("Uzbek").get(0))||
            cb.equals(Lan.mainMenu("Russian").get(0))||
            cb.equals(Lan.mainMenu("English").get(0))||
            cb.equals(Lan.goBack(a.getLanguage()))) {
                if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                 chooseLanguage(update.getCallbackQuery().getMessage(), true);
                } else {
                editPic(Lan.chooseDish(a.getLanguage()), update.getCallbackQuery().getMessage(),Lan.listTypes(a.getLanguage()),"Лого",3);
                }
         }
         else if (cb.equals(Lan.mainMenu("Uzbek").get(1))||
                cb.equals(Lan.mainMenu("Russian").get(1))||
                cb.equals(Lan.mainMenu("English").get(1))) {
            if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                chooseLanguage(update.getCallbackQuery().getMessage(), true);
            } else {
                editPic(Lan.deliveryCost(a.getLanguage()), update.getCallbackQuery().getMessage(), Lan.keyBoard(a.getLanguage()),"Лого", 2);
            }
        }
         else if (cb.equals(Lan.mainMenu("Uzbek").get(2))||
                cb.equals(Lan.mainMenu("Russian").get(2))||
                cb.equals(Lan.mainMenu("English").get(2))) {
             chooseLanguage(update.getCallbackQuery().getMessage(), true);
        }
         else if (cb.equals(Lan.mainMenu("Uzbek").get(3))||
                cb.equals(Lan.mainMenu("Russian").get(3))||
                cb.equals(Lan.mainMenu("English").get(3))) {
            if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                chooseLanguage(update.getCallbackQuery().getMessage(), true);
            } else {
                if (DataBase.listMyOrders(update.getCallbackQuery().getMessage().getChatId().toString(),"orderid").size()==0){
                    editPic(Lan.emptyOrders(a.getLanguage()), update.getCallbackQuery().getMessage(), Lan.keyBoard(a.getLanguage()),"Лого", 2);
                } else {
                    editPic(Lan.myOrders(a.getLanguage())+"\n"+DataBase.listMyOrders(update.getCallbackQuery().getMessage().getChatId().toString(),"orderid"), update.getCallbackQuery().getMessage(),Lan.keyBoard(a.getLanguage()),"Лого",2);
                }
            }
        }
        for (String t: Lan.listTypes(a.getLanguage())) {
            String language = a.getLanguage();
            if (cb.equals(t)&&!cb.equals(Lan.backToMenu(a.getLanguage()))) {
                List<String> list = DataBase.showProducts(language, language, String.valueOf(Lan.listTypes(language).indexOf(t)));
                String nothing = "";
                if (list.size()<3) nothing = Lan.emptyOrders(a.getLanguage());
                editPic(t+"\n"+nothing, update.getCallbackQuery().getMessage(), list, "Лого",  list.size()>1?2:1);
            }
        }
        for (int i = 0; i<DataBase.showAllProducts(a.getLanguage()).size(); i++) {
            String t = DataBase.showAllProducts(a.getLanguage()).get(i);
            if (cb.contains("🛒:heavy_plus_sign:"+t)) {
                DataBase.sql("insert into cart (userid, item) values ("+update.getCallbackQuery().getFrom().getId()
                +",'"+t+"')");
                editPic("<b>"+t+"</b>\n"+ Lan.cost(a.getLanguage()) + DataBase.sqlQuery("SELECT cost from table0 where "+a.getLanguage()+" = '"+t+"'", "cost") + " "+ Lan.currency(a.getLanguage()),
                update.getCallbackQuery().getMessage(), keyb(i, t, update.getCallbackQuery().getFrom().getId()), t,  3);
            } else if (cb.contains("🛒:x:"+t)) {
                DataBase.sql("delete from cart where userid ="+update.getCallbackQuery().getFrom().getId()
                +" and item = '"+t+"'");
                editPic("<b>"+t+"</b>\n"+ Lan.cost(a.getLanguage()) + DataBase.sqlQuery("SELECT cost from table0 where "+a.getLanguage()+" = '"+t+"'", "cost") + " "+ Lan.currency(a.getLanguage()),
                update.getCallbackQuery().getMessage(), keyb(i, t, update.getCallbackQuery().getFrom().getId()), t,  3);
            } else if (cb.contains(t)) {
                editPic("<b>"+t+"</b>\n"+ Lan.cost(a.getLanguage()) + DataBase.sqlQuery("SELECT cost from table0 where "+a.getLanguage()+" = '"+t+"'", "cost") + " "+ Lan.currency(a.getLanguage()),
                update.getCallbackQuery().getMessage(), keyb(i, t, update.getCallbackQuery().getFrom().getId()), t,  3);
            }
        }
	}

	private List<String> keyb(int i, String t, Integer userid) throws SQLException {
        List<String> keyb = new ArrayList<>();
        if (i>0) keyb.add(":point_left: "+DataBase.showAllProducts(a.getLanguage()).get(i-1));
                else keyb.add(":point_left: "+DataBase.showAllProducts(a.getLanguage()).get(DataBase.showAllProducts(a.getLanguage()).size()-1));
                List<String> items = DataBase.sqlQueryList("select * from cart where userid ="+userid, "item");
                if (items.contains(t)) {
                    int occurrences = Collections.frequency(items, t);
                    keyb.add("🛒:heavy_plus_sign:"+t+" "+occurrences);
                } else {
                    keyb.add("🛒:heavy_plus_sign:"+t);
                }
                if (i<DataBase.showAllProducts(a.getLanguage()).size()-1) keyb.add(DataBase.showAllProducts(a.getLanguage()).get(i+1)+" :point_right:");
                else keyb.add(DataBase.showAllProducts(a.getLanguage()).get(0)+" :point_right:");
                keyb.add(Lan.listTypes(a.getLanguage()).get(Integer.parseInt(DataBase.sqlQuery("SELECT type from table0 where "+a.getLanguage()+" = '"+t+"'", "type"))));
                keyb.add(Lan.mainMenu(a.getLanguage()).get(3));
                keyb.add(Lan.goBack(a.getLanguage()));
                if (items.contains(t)) keyb.add("🛒:x:"+t);
                keyb.add(Lan.backToMenu(a.getLanguage()));
                return keyb;
	}
	private void chooseLanguage(Message message, boolean edit) throws SQLException, TelegramApiException {
                List<String> list = new ArrayList<String>();
                list.add("O'zbek");
                list.add("Русский");
                list.add("English");
            if (edit) editCaption(":uz: Tilni tanlang\n" +
                ":ru: Выберите язык\n" +
                ":gb: Choose language" , message, list, 3);
            else {sendPic(":uz: Tilni tanlang\n" +
                ":ru: Выберите язык\n" +
                ":gb: Choose language" , message, list, "Лого", 3);
                deleteMessage(message);}
	}
	public void sendMeNumber(long ChatId){
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
            DataBase.sql("update users set smid ="+smid+" where id = "+ChatId);
            }
        catch (TelegramApiException e) {e.printStackTrace();}
    }

	private void handleIncomingText(Message m) throws SQLException, TelegramApiException {
        if (m.getText().equals("/start")) {
            if (a.getSentMessage()!=null) deleteMessage(DataBase.sqlQuery("SELECT smid from users where id="+m.getChatId(), "smid"), m.getChatId().toString());
            deleteMessage(m);
            if (a.getLanguage() == null) {
                chooseLanguage(m, false);
            } else {
            sendPic(Lan.welcome(a.getLanguage(), a.getFirstName()), m,Lan.mainMenu(a.getLanguage()), "Лого", 2);
            }
         }
	}
    public void editPic(String text, Message message, List<String> list, String productName, int flag) throws TelegramApiException, SQLException {
                String file_id = "";
        if (productName.equals("Лого")) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else file_id = DataBase.sqlQuery("SELECT imageid from table0 where "+a.getLanguage()+" = '"+productName+"'", "imageid");
                Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
                InputMediaPhoto imp = new InputMediaPhoto();
                imp.setMedia(file_id);
                EditMessageMedia em = new EditMessageMedia();
                em.setChatId(message.getChatId());
                em.setMessageId(messageId);
                em.setMedia(imp);
                EditMessageCaption ec = new EditMessageCaption();
                ec.setChatId(message.getChatId().toString());
                ec.setMessageId(messageId);
                ec.setCaption(EmojiParser.parseToUnicode(text));
                ec.setParseMode("HTML");
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                for (int i = 0; i < list.size(); i += flag) {
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i)))
                            .setCallbackData(list.get(i)));
                    if ((flag==2)||(flag==3)) { if ((i + 1) < list.size()) row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                            .setCallbackData(list.get(i + 1)));}
                    if (flag==3) {if ((i + 2) < list.size()) row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i + 2)))
                            .setCallbackData(list.get(i + 2)));
                            }
                    rows.add(row);
                }
                markup.setKeyboard(rows);
                em.setReplyMarkup(markup);
                ec.setReplyMarkup(markup);
                execute(em);
                execute(ec);
    }

     public void sendPic(String text, Message message, List<String> inline,String productName, int flag) throws SQLException, TelegramApiException {
        String file_id = "";
        if (productName.equals("Лого")) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else file_id = DataBase.sqlQuery("SELECT imageid from table0 where "+a.getLanguage()+" = '"+productName+"'", "imageid");
        SendPhoto aa = new SendPhoto();
                aa.setChatId(message.getChatId());
                aa.setPhoto(file_id);
                aa.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        if (inline!=null) {
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
            aa.setReplyMarkup(inlineMarkup);
        }
        try {
                String image = execute(aa).getMessageId().toString();
                DataBase.sql("update users set image ="+image+" where id ="+message.getChatId());
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }
     public void deleteMessage(Message message){
        DeleteMessage dm = new DeleteMessage()
                .setMessageId(message.getMessageId())
                .setChatId(message.getChatId());
        try {execute(dm);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void deleteMessage(String Messageid, String Chatid){
        DeleteMessage dm = new DeleteMessage()
                .setMessageId(Integer.parseInt(Messageid))
                .setChatId(Chatid);
        try {execute(dm);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void editCaption(String text, Message message, List<String> list, int flag) throws TelegramApiException, SQLException {
                Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
                EditMessageCaption ec = new EditMessageCaption();
                ec.setChatId(message.getChatId().toString());
                ec.setMessageId(messageId);
                ec.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                for (int i = 0; i < list.size(); i += flag) {
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i)))
                            .setCallbackData(list.get(i)));
                    if ((flag==2)||(flag==3)) { if ((i + 1) < list.size()) row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                            .setCallbackData(list.get(i + 1)));}
                    if (flag==3) {if ((i + 2) < list.size()) row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode(list.get(i + 2)))
                            .setCallbackData(list.get(i + 2)));
                            }
                    rows.add(row);
                }
                markup.setKeyboard(rows);
                ec.setReplyMarkup(markup);
                execute(ec);
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
            String photoId = message.getPhoto().get(message.getPhoto().size()-1).getFileId();
            String caption = message.getCaption();
            DataBase.sql("UPDATE table0 SET imageid = '"+photoId+"' where russian = '"+caption+"'");
            DataBase.sql("UPDATE users SET image = '"+message.getMessageId()+"' where id = '"+message.getChatId()+"'");
            a.setImage(DataBase.sqlGetUserData(message.getChatId().toString()).get(5));
            deleteMessage(message);
    }
}
