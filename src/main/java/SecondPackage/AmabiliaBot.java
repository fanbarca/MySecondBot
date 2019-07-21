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

public class AmabiliaBot extends TelegramLongPollingBot {
    private HashMap<Integer, Order> set = new HashMap<Integer, Order>();
    private static final long myID = 615351734;
    static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    static final String LATIN_TO_CYRILLIC = "Latin-Cyrillic";
    static TimeZone zone = TimeZone.getTimeZone("Asia/Tashkent");
    static SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
    static SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    private static final String DRIVER = "org.postgresql.Driver";
    Order a;
    private String botName ="";
    private String botToken ="";

    {
    date.setTimeZone(zone);
    time.setTimeZone(zone);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message m;
        try {
            if (update.hasMessage()) {
                m = update.getMessage();
                if (sqlIdList().contains(m.getChatId().toString())) {
                        a = new Order(
                                sqlGetUserData(m.getChatId().toString()).get(0),
                                sqlGetUserData(m.getChatId().toString()).get(1),
                                sqlGetUserData(m.getChatId().toString()).get(2),
                                sqlGetUserData(m.getChatId().toString()).get(3)
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
                        sql("INSERT INTO users (id, firstname, lastname, username, rmid) VALUES ('"+
                                m.getFrom().getId().toString()+"','"+
                                m.getFrom().getFirstName()+"','"+
                                m.getFrom().getLastName()+"','"+
                                m.getFrom().getUserName()+"','"+
                                m.getMessageId()+"')");
                            a = new Order(
                                    sqlGetUserData(m.getFrom().getId().toString()).get(0),
                                    null,
                                    null,
                                    sqlGetUserData(m.getFrom().getId().toString()).get(3)
                            );
                        Adminbot ab = new Adminbot();
                        ab.sendMe(":boom: Новый пользователь!" +
                                "\n" + m.getFrom().getFirstName() +" "+ m.getFrom().getLastName() +
                                "\n@" + m.getFrom().getUserName());
                        if (m.hasText()) handleIncomingText(m);
                    }
            } else if (update.hasCallbackQuery()) {
                Message cbm = update.getCallbackQuery().getMessage();
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                if (sqlIdList().contains(chatId)) {
                a = new Order(  sqlGetUserData(chatId).get(0),
                                sqlGetUserData(chatId).get(1),
                                sqlGetUserData(chatId).get(2),
                                sqlGetUserData(chatId).get(3)
                                );
                } else {
                    sql("INSERT INTO users (id, firstname, lastname, username, rmid) VALUES ('"+
                                chatId+"','"+
                                cbm.getFrom().getFirstName()+"','"+
                                cbm.getFrom().getLastName()+"','"+
                                cbm.getFrom().getUserName()+"','"+
                                cbm.getMessageId()+"')");
                            a = new Order(
                                    sqlGetUserData(cbm.getFrom().getId().toString()).get(0),
                                    null,
                                    null,
                                    sqlGetUserData(cbm.getFrom().getId().toString()).get(3)
                            );
                }
                handleCallback(update);
            }
        } catch(Exception e){
                BotLogger.error(Main.LOGTAG, e);
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

    private void handleLocation(Message message) {
        forwardMessage(message, myID);
    }

    private void handlePhoto(Message message) throws SQLException {
            String photoId = message.getPhoto().get(message.getPhoto().size()-1).getFileId();
            String caption = message.getCaption();
            sql("UPDATE table0 SET imageid = '"+photoId+"' where russian = '"+caption+"'");
            sql("UPDATE users SET image = '"+message.getMessageId()+"' where id = '"+message.getChatId()+"'");
            a.setImage(sqlGetUserData(message.getChatId().toString()).get(5));
            deleteMessage(message);
    }

    private void handleDocument(Message message) {
        deleteMessage(message);
    }

    private void handleContact(Message message) throws SQLException {
            sql("UPDATE users SET phone = "+
                    message.getContact().getPhoneNumber()+
                    " WHERE id ="+message.getFrom().getId());
            a.setNumber(sqlGetUserData(message.getChatId().toString()).get(1));
        deleteMessage(sqlQuery("SELECT smid from users where id="+message.getChatId(), "smid"), message.getChatId().toString());
        deleteMessage(message);
        send(Lan.welcome(a.getLanguage(), message.getFrom().getFirstName()),
                    message.getChatId(), Lan.mainMenu(a.getLanguage()),null, 2);
    }

    private void handleAudio(Message message) {
        forwardMessage(message, myID);
    }

    private void handleAnimation(Message message) {
        forwardMessage(message, myID);
    }

    private void handleCallback(Update update) throws InterruptedException, TelegramApiException, SQLException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId()).setShowAlert(false);
        execute(answer);
        String cb = update.getCallbackQuery().getData();
        if (cb.equals("O'zbek")||cb.equals("Русский")||cb.equals("English")){
            if (cb.equals("O'zbek")) {
                sql("UPDATE users SET language = 'Uzbek' WHERE id ="+update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("Uzbek");
            }
            else if (cb.equals("Русский")) {
                sql("UPDATE users SET language = 'Russian' WHERE id ="+update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("Russian");
            }
            else {
                sql("UPDATE users SET language = 'English' WHERE id ="+update.getCallbackQuery().getMessage().getChatId());
                a.setLanguage("English");
            }
            if (a.getNumber()==null) {
                deleteMessage(update.getCallbackQuery().getMessage());
                sendMeNumber(update.getCallbackQuery().getMessage().getChatId());
            } else {
                edit(update.getCallbackQuery().getMessage(), Lan.welcome(a.getLanguage(), sqlselect(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "firstname")),
                        Lan.mainMenu(a.getLanguage()),2);
            }
        }
        if (cb.equals(Lan.backToMenu(a.getLanguage()))) {
            edit(update.getCallbackQuery().getMessage(), Lan.welcome(a.getLanguage(), sqlselect(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "firstname")),
            Lan.mainMenu(a.getLanguage()), 2);
                String image = sqlQuery("SELECT image from users where id="+update.getCallbackQuery().getMessage().getChatId(), "image");
                if (image!=null) {
                    deleteMessage(image, update.getCallbackQuery().getMessage().getChatId().toString());
                    sql("update users set image = null where id="+update.getCallbackQuery().getMessage().getChatId());
                }
           }
        if (cb.equals(Lan.mainMenu("Uzbek").get(0))||
            cb.equals(Lan.mainMenu("Russian").get(0))||
            cb.equals(Lan.mainMenu("English").get(0))||
            cb.equals(Lan.goBack(a.getLanguage()))) {
                if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                 chooseLanguage(update.getCallbackQuery().getMessage(), true);
                } else {
                 edit(update.getCallbackQuery().getMessage(),Lan.chooseDish(a.getLanguage()), Lan.listTypes(a.getLanguage()),3);
                String image = sqlQuery("SELECT image from users where id="+update.getCallbackQuery().getMessage().getChatId(), "image");
                if (image!=null) {
                    deleteMessage(image, update.getCallbackQuery().getMessage().getChatId().toString());
                    sql("update users set image = null where id="+update.getCallbackQuery().getMessage().getChatId());
                }
                }
         }
         else if (cb.equals(Lan.mainMenu("Uzbek").get(1))||
                cb.equals(Lan.mainMenu("Russian").get(1))||
                cb.equals(Lan.mainMenu("English").get(1))) {
            if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                chooseLanguage(update.getCallbackQuery().getMessage(), true);
            } else {
                edit(update.getCallbackQuery().getMessage(), Lan.deliveryCost(a.getLanguage()), Lan.backToMenu(a.getLanguage()));
                String image = sqlQuery("SELECT image from users where id="+update.getCallbackQuery().getMessage().getChatId(), "image");
                if (image!=null) {
                    deleteMessage(image, update.getCallbackQuery().getMessage().getChatId().toString());
                    sql("update users set image = null where id="+update.getCallbackQuery().getMessage().getChatId());
                }
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
                if (listMyOrders(update.getCallbackQuery().getMessage().getChatId().toString(),"orderid").size()==0){
                    edit(update.getCallbackQuery().getMessage(), Lan.emptyOrders(a.getLanguage()), Lan.backToMenu(a.getLanguage()));
                } else {
                    edit(update.getCallbackQuery().getMessage(), Lan.myOrders(a.getLanguage())+"\n"+listMyOrders(update.getCallbackQuery().getMessage().getChatId().toString(),"orderid"), Lan.backToMenu(a.getLanguage()));
                }
                String image = sqlQuery("SELECT image from users where id="+update.getCallbackQuery().getMessage().getChatId(), "image");
                if (image!=null) {
                    deleteMessage(image, update.getCallbackQuery().getMessage().getChatId().toString());
                    sql("update users set image = null where id="+update.getCallbackQuery().getMessage().getChatId());
                }
            }
        }
        for (String t: Lan.listTypes(a.getLanguage())) {
            String language = a.getLanguage();
            if (cb.equals(t)&&!cb.equals(Lan.backToMenu(a.getLanguage()))) {
                List<String> a = showProducts(language, language, String.valueOf(Lan.listTypes(language).indexOf(t)));
                edit(update.getCallbackQuery().getMessage(), t, a, a.size()>1?2:1);
                String image = sqlQuery("SELECT image from users where id="+update.getCallbackQuery().getMessage().getChatId(), "image");
                if (image!=null) {
                    deleteMessage(image, update.getCallbackQuery().getMessage().getChatId().toString());
                    sql("update users set image = null where id="+update.getCallbackQuery().getMessage().getChatId());
                }
            }
        }
        for (int i = 0; i<showAllProducts(a.getLanguage()).size(); i++) {
            String t = showAllProducts(a.getLanguage()).get(i);
            if (cb.equals(showAllProducts(a.getLanguage()).get(i))) {
                SendPhoto aa = new SendPhoto();
                aa.setChatId(update.getCallbackQuery().getMessage().getChatId());
                aa.setPhoto(sqlQuery("SELECT imageid from table0 where "+a.getLanguage()+" = '"+t+"'", "imageid"));
                String image = execute(aa).getMessageId().toString();
                sql("update users set image ="+image+" where id ="+update.getCallbackQuery().getMessage().getChatId());
                List<String> keyb = new ArrayList<>();
                if (i>0) keyb.add(":point_left: "+showAllProducts(a.getLanguage()).get(i-1));
                else keyb.add(":point_left: "+showAllProducts(a.getLanguage()).get(showAllProducts(a.getLanguage()).size()-1));
                keyb.add("🛒:heavy_plus_sign:"+t);
                if (i<showAllProducts(a.getLanguage()).size()-1) keyb.add(showAllProducts(a.getLanguage()).get(i+1)+" :point_right:");
                else keyb.add(showAllProducts(a.getLanguage()).get(0)+" :point_right:");
                keyb.add(Lan.listTypes(a.getLanguage()).get(Integer.parseInt(sqlQuery("SELECT type from table0 where "+a.getLanguage()+" = '"+t+"'", "type"))));
                keyb.add(Lan.mainMenu(a.getLanguage()).get(3));
                keyb.add(Lan.goBack(a.getLanguage()));
                keyb.add(Lan.backToMenu(a.getLanguage()));
                send(t + "\n"+
                Lan.cost(a.getLanguage()) + sqlQuery("SELECT cost from table0 where "+a.getLanguage()+" = '"+t+"'", "cost") + " "+ Lan.currency(a.getLanguage()),
                update.getCallbackQuery().getMessage().getChatId(), keyb, null, 3);
                deleteMessage(update.getCallbackQuery().getMessage());
            } else if (cb.contains(showAllProducts(a.getLanguage()).get(i))&&(cb.contains(":point_left:")||cb.contains(":point_right:"))) {
                InputMediaPhoto imp = new InputMediaPhoto();
                imp.setMedia(sqlQuery("SELECT imageid from table0 where "+a.getLanguage()+" = '"+t+"'", "imageid"));
                EditMessageMedia em = new EditMessageMedia();
                em.setChatId(update.getCallbackQuery().getMessage().getChatId());
                em.setMessageId(Integer.parseInt(sqlQuery("select image from users where id="+update.getCallbackQuery().getMessage().getChatId(), "image")));
                em.setMedia(imp);
                execute(em);
                List<String> keyb = new ArrayList<>();
                if (i>0) keyb.add(":point_left: "+showAllProducts(a.getLanguage()).get(i-1));
                else keyb.add(":point_left: "+showAllProducts(a.getLanguage()).get(showAllProducts(a.getLanguage()).size()-1));
                keyb.add("🛒:heavy_plus_sign:"+t);
                if (i<showAllProducts(a.getLanguage()).size()-1) keyb.add(showAllProducts(a.getLanguage()).get(i+1)+" :point_right:");
                else keyb.add(showAllProducts(a.getLanguage()).get(0)+" :point_right:");
                keyb.add(Lan.listTypes(a.getLanguage()).get(Integer.parseInt(sqlQuery("SELECT type from table0 where "+a.getLanguage()+" = '"+t+"'", "type"))));
                keyb.add(Lan.mainMenu(a.getLanguage()).get(3));
                keyb.add(Lan.goBack(a.getLanguage()));
                keyb.add(Lan.backToMenu(a.getLanguage()));
                edit(update.getCallbackQuery().getMessage(), t + "\n"+
                Lan.cost(a.getLanguage()) + sqlQuery("SELECT cost from table0 where "+a.getLanguage()+" = '"+t+"'", "cost") + " "+ Lan.currency(a.getLanguage()),
                keyb, 3);
            } else if (cb.equals("🛒:heavy_plus_sign:"+showAllProducts(a.getLanguage()).get(i))) {

            }
        }
    }



    private void handleIncomingText(Message message) throws TelegramApiException, InterruptedException, SQLException {
        if (message.getText().equals("/start")) {
            if (a.getSentMessage()!=null) deleteMessage(sqlQuery("SELECT smid from users where id="+message.getChatId(), "smid"), message.getChatId().toString());
            deleteMessage(message);
            if (a.getLanguage() == null) {
                chooseLanguage(message, false);
            }
            else {
                send(Lan.welcome(a.getLanguage(), message.getFrom().getFirstName()), message.getChatId(),Lan.mainMenu(a.getLanguage()), null, 2);
            }
        } else if (message.getText().contains("/sql")) {
             if (message.getText().length()>5) {
                 String command = message.getText().substring(5);
                 sql(command);
             }
             deleteMessage(message);
        } else {
             deleteMessage(message);
        }
        // else if (message.getText().equals("O'zbek")||message.getText().equals("Русский")||message.getText().equals("English")){
        //     if (message.getText().equals("O'zbek")) {
        //         sql("UPDATE users SET language = 'Uzbek' WHERE id ="+message.getFrom().getId());
        //         language = "Uzbek";
        //     }
        //     else if (message.getText().equals("Русский")) {
        //         sql("UPDATE users SET language = 'Russian' WHERE id ="+message.getFrom().getId());
        //         language = "Russian";
        //     }
        //     else if (message.getText().equals("English")) {
        //         sql("UPDATE users SET language = 'English' WHERE id ="+message.getFrom().getId());
        //         language = "English";
        //     }
        //     if ("".equals(number)||number==null) {
        //         sendMeNumber(message.getChatId());
        //     } else {
        //         send(Lan.welcome(language, message.getFrom().getFirstName()), message.getChatId(), null,
        //                 Lan.mainMenu(language),2);
        //     }
        // }
        //  else if (message.getText().equals(Lan.mainMenu("Uzbek").get(0))||
        //           message.getText().equals(Lan.mainMenu("Russian").get(0))||
        //           message.getText().equals(Lan.mainMenu("English").get(0))) {
        //      if ((language == null) || (language.equals(""))) {
        //          chooseLanguage(message, false);
        //      } else {
        //          send(Lan.chooseDish(language), message.getChatId(), Lan.listTypes(language),null,3);
        //      }
        //  }
        //  else if (message.getText().equals(Lan.mainMenu("Uzbek").get(1))||
        //         message.getText().equals(Lan.mainMenu("Russian").get(1))||
        //         message.getText().equals(Lan.mainMenu("English").get(1))) {
        //     if ((language == null) || (language.equals(""))) {
        //         chooseLanguage(message, false);
        //     } else send(Lan.deliveryCost(language), message.getChatId());
        // }
        //  else if (message.getText().equals(Lan.mainMenu("Uzbek").get(2))||
        //         message.getText().equals(Lan.mainMenu("Russian").get(2))||
        //         message.getText().equals(Lan.mainMenu("English").get(2))) {
        //      chooseLanguage(message, false);
        // }
        //  else if (message.getText().equals(Lan.mainMenu("Uzbek").get(3))||
        //         message.getText().equals(Lan.mainMenu("Russian").get(3))||
        //         message.getText().equals(Lan.mainMenu("English").get(3))) {
        //     if ((language == null) || (language.equals(""))) {
        //         chooseLanguage(message, false);
        //     } else {
        //         if (listMyOrders(message.getChatId().toString(),"orderid").size()==0){
        //             send(Lan.emptyOrders(language), message.getChatId());
        //         } else {
        //             send(Lan.myOrders(language), message.getChatId(),listMyOrders(message.getChatId().toString(),"orderid"), null, 1);
        //         }
        //     }
        // } else if (message.getText().contains("/sql")) {
        //      if (message.getText().length()>5) {
        //          String command = message.getText().substring(5);
        //          sql(command);
        //      }
        // } else {
        //     if ((language == null) || (language.equals(""))) {
        //         if (sentMessage!=null) {deleteMessage(sentMessage);sentMessage=null;}
        //         if (receivedMes!=null) {deleteMessage(receivedMes);receivedMes=null;}
        //         if (image!=null) {deleteMessage(image);image=null;}
        //         chooseLanguage(message, false);
        //     }
        //     else {
        //     if (sentMessage!=null) {deleteMessage(sentMessage);sentMessage=null;}
        //     if (receivedMes!=null) {deleteMessage(receivedMes);receivedMes=null;}
        //     send(Lan.welcome(language, message.getFrom().getFirstName()), message.getChatId(), Lan.mainMenu(language),null, 2);
        //     }
        // }
    }
    public static List<String> directions() {
        List<String> directions = new ArrayList<String>();
        directions.add(":ru::point_right::gb:");
        directions.add(":ru::point_right::uz:");
        directions.add(":gb::point_right::ru:");
        directions.add(":gb::point_right::uz:");
        directions.add(":uz::point_right::gb:");
        directions.add(":uz::point_right::ru:");
        return directions;
    }
    public static List<String> dial() {
        List<String> dial = new ArrayList<String>();
        dial.add("::one::");
        dial.add("::two::");
        dial.add("::three::");
        dial.add("::four::");
        dial.add("::five::");
        dial.add("::six::");
        dial.add("::seven::");
        dial.add("::eight::");
        dial.add("::nine::");
        dial.add("::arrow_backward::");
        dial.add("::zero::");
        dial.add("::x::");
        dial.add("::ok::white_check_mark::");
        return dial;
    }
    private void chooseLanguage(Message message, boolean edit) {
        if (!edit){
            List<String> list = new ArrayList<String>();
            list.add("O'zbek");
            list.add("Русский");
            list.add("English");
            send(":uz: Tilni tanlang\n" +
                ":ru: Выберите язык\n" +
                ":gb: Choose language" ,
                message.getChatId(), list, null, 3);
        } else {
        List<String> list = new ArrayList<String>();
        list.add("O'zbek");
        list.add("Русский");
        list.add("English");
            edit(message, ":uz: Tilni tanlang\n" +
                        ":ru: Выберите язык\n" +
                        ":gb: Choose language", list, 3);
        }
    }

    public void send (String text, long chatId){
        send(text, chatId, null, null, 1);
    }
    public void send (String text, long chatId, String a, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        if (inline) send(text, chatId, list, null, 1);
        else send(text, chatId, null, list, 1);
    }
    public void send (String text, long chatId, String a, String b, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        if (inline) send(text, chatId, list, null, 2);
        else send(text, chatId, null, list, 2);
    }
    public void send (String text, long chatId, String a, String b, String c, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        list.add(c);
        if (inline) send(text, chatId, list, null, 3);
        else send(text, chatId, null, list, 3);
    }
    public void send (String text, long chatId, List<String> inline,List<String> reply, int flag) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        List<KeyboardRow> rows2 = new ArrayList<KeyboardRow>();
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
            sendMessage.setReplyMarkup(inlineMarkup);
        }
        if (reply!=null) {for (int i = 0; i < reply.size(); i += flag) {
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
            sql("update users set smid ="+smid+" where id = "+chatId);
        }
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void edit (Message message, String newText){
        EditMessageText sendMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(message.getMessageId())
                .setParseMode("HTML")
                .setText(EmojiParser.parseToUnicode(newText));
        //sendMessage.setReplyMarkup(a.getIM());
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void edit (Message message, String newText, String a){
        List<String> list = new ArrayList<String>();
        list.add(a);
        edit(message, newText, list,1);
    }
    public void edit (Message message, String newText, String a, String b){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        edit(message, newText, list, 2);
    }
    public void edit (String newText, Message message, String a, String b, String c){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        list.add(c);
        edit(message, newText, list, 3);
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
        sendMessage.setReplyMarkup(markup);
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
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
            sql("update users set smid ="+smid+" where id = "+ChatId);
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
    public void forwardMessage(Message message, long id) {
        ForwardMessage fm = new ForwardMessage();
        fm.setChatId(id);
        fm.setFromChatId(message.getChatId());
        fm.setMessageId(message.getMessageId());
        try {execute(fm);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public static Connection getConnection() throws URISyntaxException, SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
            + "Include in your library path!");
            return null;
        }
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
        return DriverManager.getConnection(dbUrl, username, password);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    public static void sql(String command) {
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                prst.executeUpdate(command);
                prst.close();
                conn.close();
            }
            }
            catch(Exception ex) {
                System.err.println(ex);
            }
    }

        public static List<String> sqlIdList() throws SQLException {
            List<String> idList = new ArrayList<String>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select id from users");
                while (rs.next()) {
                    idList.add(rs.getString("id"));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return idList;
    }

    public String sqlselect(String id, String column) throws SQLException {
        String lan = "";
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from users where id =" + id);
                while (rs.next()){
                    lan= rs.getString(column);
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

    public void makeOrder(String userid, String date, String type,
                          String product, String cost, int destlatitude, int destlongitude) throws SQLException {
        Random rand = new Random();
        sql("INSERT INTO users (orderid, userid, date, type, product, cost, destlatitude, destlongitude) VALUES ('"+
                rand.nextInt(10000)+"','"+
                userid+"','"+
                date+"','"+
                type+"','"+
                product+"','"+
                cost+"',"+
                destlatitude+","+
                destlongitude+")");
    }
    public List<String> listMyOrders(String id, String column){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from orders where userid =" + id);
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
    public List<String> showProducts(String language, String column, String type){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from table0 where type = '"+type+"'");
                while (rs.next()){
                    lan.add(rs.getString(column));
                }
                lan.add(Lan.goBack(language));
                lan.add(Lan.backToMenu(language));
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }
    public List<String> showAllProducts(String column){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from table0");
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
    public String sqlQuery(String command, String field) throws SQLException {
            String lan = "";
            try {
                Connection conn = getConnection();
                if (conn!=null) {
                    Statement prst = conn.createStatement();
                    ResultSet rs = prst.executeQuery(command);
                    while (rs.next()){
                        lan= rs.getString(field);
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
    public List<String> sqlGetUserData(String id) throws SQLException {
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select * from users where id ="+id);
                while (rs.next()){
                    lan.add(rs.getString("firstname"));
                    lan.add(rs.getString("phone"));
                    lan.add(rs.getString("language"));
                    lan.add(rs.getString("rmid"));
                    lan.add(rs.getString("smid"));
                    lan.add(rs.getString("image"));
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

}
