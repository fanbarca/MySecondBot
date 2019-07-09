package SecondPackage;

import com.ibm.icu.text.Transliterator;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        Message m;
        try {
            if (update.hasMessage()) {
                try {
                    language = sqlselect(update.getMessage().getFrom().getId().toString(), "language");
                    number = sqlselect(update.getMessage().getFrom().getId().toString(),"phone");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                m = update.getMessage();
//                if (m.getFrom().getId()==myID) {
//                    if (m.hasText()) {
//                        if (m.getText().equals("/start")&&set.size()>0) {
//                            send("Всего пользователей: "+set.size(), myID);
//                        } else if(m.getText().equals("/start")&&set.size()==0){
//                            send("Ещё нет пользователей", myID);
//                        }
//                        Collection<Order> values = set != null ? set.values() : null;
//                        if (values!=null) {
//                            for (Order o: values) {
//                                for (Translation tr: o.getOrdersList()) {
//                                    if (m.getText().equals("Unfinished")&&!tr.Isfinished()) {
//                                        myself(o,tr,true);
//                                    } else if (m.getText().equals("Finished")&&tr.Isfinished()) {
//                                        myself(o,tr,false);
//                                    }
//                                }
//                            }
//                        } else {
//                            if (m.getText().equals("Unfinished")||m.getText().equals("Finished")) send("Ещё нет пользователей", myID);
//                        }
//                    }
//                }
                if (sqlIdList().contains(m.getFrom().getId().toString())) {
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
                        sql("INSERT INTO users (id, firstname, lastname, username) VALUES ('"+
                                m.getFrom().getId().toString()+"','"+
                                m.getFrom().getFirstName()+"','"+
                                m.getFrom().getLastName()+"','"+
                                m.getFrom().getUserName()+"')");
                        send(":boom: Новый пользователь!" +
                                "\n" + m.getFrom().getFirstName() +" "+ m.getFrom().getLastName() +
                                "\n@" + m.getFrom().getUserName()+
                                "\nВсего пользователей: " + set.size(), myID);
                        if (m.hasText()) handleIncomingText(m);
                    }
            } else if (update.hasCallbackQuery()) {
//                 if (update.getCallbackQuery().getFrom().getId()==myID) {
//                     Collection<Order> values = set.values();
//                     for (Order o: values) {
//                         for (Translation tr: o.getOrdersList()) {
//                             if (update.getCallbackQuery().getData().contains("Выполнено")) {
//                                 if (update.getCallbackQuery().getData().contains(tr.getId())) {
//                                     tr.setfinished(true);
//                                     deleteMessage(update.getCallbackQuery().getMessage());
//                                     send("№"+tr.getId()+" Заказ отмечен как выполненный", myID);
//                                     send("№"+tr.getId()+ " "+o.getLanguage().finished(), o.getUser().getId());
//                                 }
//                             } else if (update.getCallbackQuery().getData().contains("Отмена заказа")) {
//                                 if (update.getCallbackQuery().getData().contains(tr.getId())) {
//                                     set.get(o.getUser().getId()).getOrdersList().remove(tr);
//                                     deleteMessage(update.getCallbackQuery().getMessage());
//                                     send("№"+tr.getId()+" Заказ отменен", myID);
//                                     send("№"+tr.getId()+ " "+o.getLanguage().cancelled(), o.getUser().getId());
//                                 }
//                             }
//                         }
//                     }
//                 }
//
//                 if (set.containsKey(update.getCallbackQuery().getFrom().getId())) {
//                     a = set.get(update.getCallbackQuery().getFrom().getId());
//                     handleCallback(update);
//                 } else {
//                     a = new Order(update.getCallbackQuery().getFrom());
//                     set.put(update.getCallbackQuery().getFrom().getId(), a);
//                     send(":boom: Новый пользователь!" +
//                             "\n" + a.getUser().getFirstName() +" "+ a.getUser().getLastName() +
//                             "\n@" + a.getUser().getUserName()+
//                             "\nВсего пользователей: " + set.size(), myID);
//                 }
                try {
                    language = sqlselect(update.getCallbackQuery().getFrom().getId().toString(), "language");
                    number = sqlselect(update.getCallbackQuery().getFrom().getId().toString(),"phone");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                handleCallback(update);
            }
        } catch(Exception e){
                BotLogger.error(Main.LOGTAG, e);
        }
    }

    private void handleVoice(Message message) {
        forwardMessage(message, myID);
    }

    private void handleVideoNote(Message message) {
        forwardMessage(message, myID);
    }

    private void handleVideo(Message message) {
        forwardMessage(message, myID);

    }

    private void handleSticker(Message message) {
        forwardMessage(message, myID);
    }

    private void handleLocation(Message message) {
        forwardMessage(message, myID);
    }

    private void handlePhoto(Message message) {
        forwardMessage(message, myID);
    }

    private void handleDocument(Message message) {
        forwardMessage(message, myID);

//        Document doc = message.getDocument();
//        t.setDoc(doc);
//        if (t.getDirection()!=null&& !t.hasOrdered()) {
//            send(a.getLanguage().received() +
//                    "\n"+a.getLanguage().preliminary(t) + "\n"+
//                    a.getLanguage().doYouConfirm(), message.getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), false);
//        } else if (!t.hasOrdered()) {
//            send(a.getLanguage().chooseDirection(),message.getChatId(), directions(), true,false);
//        } else {
//            send(a.getLanguage().orderExists(),message.getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), true);
//        }
    }

    private void handleContact(Message message) throws SQLException {
        if (number==null) {
            sql("UPDATE users SET phone = "+
                    message.getContact().getPhoneNumber()+
                    " WHERE id ="+message.getFrom().getId());
            send(Lan.welcome(language, message.getFrom().getFirstName()),
                    message.getChatId(), Lan.mainMenu(language), false, 2);
        } else {
            send(Lan.welcome(language, message.getFrom().getFirstName()),
                    message.getChatId(), Lan.mainMenu(language), false, 2);
        }
    }

    private void handleAudio(Message message) {
        forwardMessage(message, myID);
    }

    private void handleAnimation(Message message) {
        forwardMessage(message, myID);
    }

    private void handleCallback(Update update) throws InterruptedException, TelegramApiException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId()).setShowAlert(false);
        execute(answer);
        String cb = update.getCallbackQuery().getData();
        for (String t: Lan.listTypes(language)) {
            if (cb.equals(t)) {
                deleteMessage(update.getCallbackQuery().getMessage());
                // for (String s: showProducts(language, "name", "table"+Lan.listTypes(language).indexOf(t))){
                //     send(s, update.getCallbackQuery().getMessage().getChatId(), ":heavy_plus_sign:", ":x:", true);
                // }
                edit(update.getCallbackQuery().getMessage(), t,
                showProducts(language, "name", String.valueOf(Lan.listTypes(language).indexOf(t))), 2);
            }
        }

        if (cb.equals(Lan.goBack(language))) {
            edit(update.getCallbackQuery().getMessage(), Lan.chooseDish(language), Lan.listTypes(language), 3);
        }
//        for (int i = 0; i<Lan.listTypes(language).size(); i++){
//            if (cb.equals(Lan.listTypes(language).get(i))) {
//
//            }
//        }
//        for (String b: dial()){
//            if (cb.contains(b)&&!cb.contains(":ok:")&&!cb.contains(":arrow_backward:")&&!cb.contains(":x:")) {
//                t.setPages(b);
//                if (!(cb.contains(":zero:")&&(t.getPages().length()==0))){
//                    edit(update.getCallbackQuery().getMessage(),
//                            a.getLanguage().howManyPages()+
//                                    "\n:page_facing_up: "+
//                                    t.getPages(), dial(), false);
//                }
//            }
//        }
//        if (cb.contains(":arrow_backward:")&&t.getPages().length() > 0) {
//            t.pagesBack();
//            edit(update.getCallbackQuery().getMessage(), a.getLanguage().howManyPages()+"\n:page_facing_up: " + t.getPages(), dial(), false);
//        } else if (cb.contains(":ok:")&&t.getPages().length()>0) {
//            t.setTotalCost();
//            if (t.getDoc()==null) {
//                edit(update.getCallbackQuery().getMessage(), a.getLanguage().sendMe());
//            } else if (t.getDirection()!=null){
//                deleteMessage(update.getCallbackQuery().getMessage());
//                send(a.getLanguage().preliminary(t) +
//                        "\n"+a.getLanguage().doYouConfirm(), update.getCallbackQuery().getMessage().getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), false);
//            }
//        } else if (cb.equals(a.getLanguage().getYes())) {
//            deleteMessage(update.getCallbackQuery().getMessage());
//            send(a.getLanguage().chooseDirection(),update.getCallbackQuery().getMessage().getChatId(), directions(), true,false);
//        } else if (cb.equals(a.getLanguage().getNo())) {
//            deleteMessage(update.getCallbackQuery().getMessage());
//        } else if (cb.contains(":x:")) {
//            areYouSure(update.getCallbackQuery().getMessage(), true);
//        } else if (cb.contains(a.getLanguage().cancel())) {
//            String id = "";
//            String direction = "";
//            Iterator<Translation> iter = a.getOrdersList().iterator();
//            while (iter.hasNext()) {
//                Translation tran = iter.next();
//                if (cb.contains(String.valueOf(tran.getId()))){
//                    id = tran.getId();
//                    direction = tran.getDirection();
//                    iter.remove();
//                }
//            }
//            edit(update.getCallbackQuery().getMessage(), a.getLanguage().cancelled());
//            //deleteMessage(update.getCallbackQuery().getMessage());
//            send(direction + " Заказ №"+ id +" от "+ a.getUser().getFirstName()+
//                    " отменен пользователем", myID);
//        } else if (cb.contains(":ok_hand::white_check_mark:")) {
//            a.getOrdersList().remove(t);
//            edit(update.getCallbackQuery().getMessage(), a.getLanguage().cancelled());
//        } else if (cb.contains(":raised_hand::negative_squared_cross_mark:")) {
//            edit(update.getCallbackQuery().getMessage(), a.getLanguage().howManyPages()+"\n:page_facing_up: "+ t.getPages(), dial(), false);
//        }
    }
    private void handleIncomingText(Message message) throws TelegramApiException, InterruptedException, SQLException {
        if (message.getText().equals("/start")) {
            if (!language.equals("Uzbek")&&!language.equals("Russian")&&!language.equals("English")) {
                chooseLanguage(message);
            }
            else {
                send(Lan.welcome(language, message.getFrom().getFirstName()), message.getChatId(),
                Lan.mainMenu(language), false,2);
            }
        } else if (message.getText().equals("O'zbek")||message.getText().equals("Русский")||message.getText().equals("English")){
            if (message.getText().equals("O'zbek")) {
                sql("UPDATE users SET language = 'Uzbek' WHERE id ="+message.getFrom().getId());
                language = "Uzbek";
            }
            else if (message.getText().equals("Русский")) {
                sql("UPDATE users SET language = 'Russian' WHERE id ="+message.getFrom().getId());
                language = "Russian";
            }
            else if (message.getText().equals("English")) {
                sql("UPDATE users SET language = 'English' WHERE id ="+message.getFrom().getId());
                language = "English";
            }
            if ("".equals(number)||number==null) {
                sendMeNumber(message);
            } else {
                send(Lan.welcome(language, message.getFrom().getFirstName()), message.getChatId(),
                        Lan.mainMenu(language), false,2);
            }
        }
         else if (message.getText().equals(Lan.mainMenu("Uzbek").get(0))||
                  message.getText().equals(Lan.mainMenu("Russian").get(0))||
                  message.getText().equals(Lan.mainMenu("English").get(0))) {
             if ((language == null) || (language.equals(""))) {
                 chooseLanguage(message);
             } else {
                 send(Lan.chooseDish(language), message.getChatId(), Lan.listTypes(language),true,3);
             }
//             boolean exists = false;
//             for (Translation tr: a.getOrdersList()) {
//                 if (!tr.Isfinished()) exists = true;
//             }
//             if (!exists) {
//                 send(a.getLanguage().chooseDirection(),message.getChatId(), directions(), true,false);
//             }
//             else send(a.getLanguage().orderExists(),message.getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), true);
         }
         else if (message.getText().equals(Lan.mainMenu("Uzbek").get(1))||
                message.getText().equals(Lan.mainMenu("Russian").get(1))||
                message.getText().equals(Lan.mainMenu("English").get(1))) {
            if ((language == null) || (language.equals(""))) {
                chooseLanguage(message);
            } else send(Lan.deliveryCost(language), message.getChatId());
        }
         else if (message.getText().equals(Lan.mainMenu("Uzbek").get(2))||
                message.getText().equals(Lan.mainMenu("Russian").get(2))||
                message.getText().equals(Lan.mainMenu("English").get(2))) {
             chooseLanguage(message);
        }
         else if (message.getText().equals(Lan.mainMenu("Uzbek").get(3))||
                message.getText().equals(Lan.mainMenu("Russian").get(3))||
                message.getText().equals(Lan.mainMenu("English").get(3))) {
            if ((language == null) || (language.equals(""))) {
                chooseLanguage(message);
            } else {
                if (listMyOrders(message.getChatId().toString(),"orderid").size()==0){
                    send(Lan.emptyOrders(language), message.getChatId());
                } else {
                    send(Lan.myOrders(language), message.getChatId(),listMyOrders(message.getChatId().toString(),"orderid"), true, 1);
                }
            }
//             for (Translation tr: a.getOrdersList()) {
//                 SendMessage sendMessage = new SendMessage()
//                         .setChatId(message.getChatId())
//                         .setText(EmojiParser.parseToUnicode(a.getLanguage().orders(tr)))
//                         .setParseMode("HTML");
//                 InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
//                 List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
//                 List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
//                 row.add(new InlineKeyboardButton()
//                         .setText(EmojiParser.parseToUnicode(a.getLanguage().cancel()))
//                         .setCallbackData(a.getLanguage().cancel()+tr.getId()));
//                 rows.add(row);
//                 inlineMarkup.setKeyboard(rows);
//                 if (!tr.Isfinished()) sendMessage.setReplyMarkup(inlineMarkup);
//                 try { execute(sendMessage);}
//                 catch (TelegramApiException e) {e.printStackTrace();}
//             }
        }

//         else if (message.getText().equals(Lan.menu(language).get(3))&& a.getOrdersList().size()==0) send(a.getLanguage().emptyOrders(), message.getChatId());
//         else if (message.getText().equals(a.getLanguage().getYes())) {
//             t.setOrderTime(new Date(System.currentTimeMillis()));
//             t.setOrdered(true);
//             a.getOrdersList().add(t);
//             send(a.getLanguage().confirmOrder(), message.getChatId(), a.getLanguage().menu(), false,true);
//             myself(a,t,false);
//             if (a.getContact()==null) {
//                 sendMeNumber(message);
//             }
//             else {
//                 resendContact();
//                 send(a.getLanguage().weWillContact(), message.getChatId(), a.getLanguage().menu(), false,true);
//             }
//         }
//         else if (message.getText().equals(a.getLanguage().getNo())) {
//             t.clearOrder();
//             send(a.getLanguage().cancelled(), message.getChatId(), a.getLanguage().menu(), false, true);
//         }
         else if (message.getText().contains("/sql")) {
             if (message.getText().length()>5) {
                 String command = message.getText().substring(5);
                 sql(command);
             }
         }
         else send(Lan.welcome(language, message.getFrom().getFirstName()), message.getChatId());
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
    private void chooseLanguage(Message message) {
        send(":uz: Tilni tanlang\n" +
                        ":ru: Выберите язык\n" +
                        ":gb: Choose language" ,
                message.getChatId(),
                "O'zbek","Русский","English", false);
    }

    public void send (String text, long chatId){
        List<String> list = new ArrayList<String>();
        send(text, chatId, list, true, 1);
    }
    public void send (String text, long chatId, String a, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        send(text, chatId, list, inline, 1);
    }
    public void send (String text, long chatId, String a, String b, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        send(text, chatId, list, inline, 2);
    }
    public void send (String message, long chatId, String a, String b, String c, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        list.add(c);
        send(message, chatId, list, inline, 3);
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
//            if (chatId==myID){
//                    KeyboardRow myRow = new KeyboardRow();
//                    myRow.add(new KeyboardButton().setText("Finished"));
//                    myRow.add(new KeyboardButton().setText("Unfinished"));
//                    rows2.add(myRow);
//            }
        inlineMarkup.setKeyboard(rows);
        replyMarkup.setKeyboard(rows2).setResizeKeyboard(true).setOneTimeKeyboard(false);
//        if (a!=null) a.setIM(inlineMarkup);
//        if (a!=null) a.setRM(replyMarkup);
        if (inline) sendMessage.setReplyMarkup(inlineMarkup);
        else sendMessage.setReplyMarkup(replyMarkup);
        try {
            execute(sendMessage);
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
    public void edit (Message message, String newText, String a, String b,String c){
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

//    public void myself(Order o, Translation tr, boolean inline){
//        SendDocument sendMyselfdoc = new SendDocument()
//                .setChatId(myID)
//                .setDocument(tr.getDoc().getFileId())
//                .setCaption(EmojiParser.parseToUnicode(
//                        "\n:fast_forward:Направление: "+tr.getDirection()+
//                        "\n:page_facing_up:Количество листов: "+ tr.getPages()+
//                        "\n:date:Заказ оформлен: "+date.format(tr.getOrderTime()) +
//                        "\n:clock3:"+time.format(tr.getOrderTime()) +
//                        "\n:moneybag:Стоимость: "+tr.getTotalCost()+ " сум"+
//                        "\n:watch:Требуется дней: "+tr.getDuration()+
//                        "\n:1234:Номер заказа: " + tr.getId()+
//                        "\n:busts_in_silhouette:Заказчик: "+ o.getUser().getFirstName()+" @"+o.getUser().getUserName()+
//                        "\n:u6307:Язык интерфейса: " + o.getLanguage().getClass().getSimpleName()));
//        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
//        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
//        row.add(new InlineKeyboardButton()
//                .setText(EmojiParser.parseToUnicode("Выполнено :thumbsup:"))
//                .setCallbackData("Выполнено :thumbsup:" + tr.getId()));
//        row.add(new InlineKeyboardButton()
//        .setText(EmojiParser.parseToUnicode(":negative_squared_cross_mark:Отмена заказа"))
//        .setCallbackData(":negative_squared_cross_mark:Отмена заказа" + tr.getId()));
//        rows.add(row);
//        inlineMarkup.setKeyboard(rows);
//        if(inline)sendMyselfdoc.setReplyMarkup(inlineMarkup);
//        try {
//            execute(sendMyselfdoc);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//    public void resendContact() {
//        SendContact sendMyselfContact = new SendContact()
//                .setChatId(myID)
//                .setPhoneNumber(a.getContact().getPhoneNumber())
//                .setFirstName(a.getContact().getFirstName())
//                .setLastName(a.getContact().getLastName());
//        try {
//            execute(sendMyselfContact);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
    public void sendMeNumber(Message message){
        SendMessage sendMessage = new SendMessage()
                .setChatId(message.getChatId())
                .setText(EmojiParser.parseToUnicode(Lan.sendMeContact(language)))
                .setParseMode("HTML");
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton keyboardButton =
                new KeyboardButton()
                        .setRequestContact(true)
                        .setText(EmojiParser.parseToUnicode(Lan.myContact(language)));
        List<KeyboardRow> rows2 = new ArrayList<KeyboardRow>();
        row2.add(keyboardButton);
        rows2.add(row2);
        replyMarkup.setKeyboard(rows2).setResizeKeyboard(true).setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyMarkup);
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
//    public  void areYouSure(Message message, boolean edit){
//        if (edit) {
//            edit(message, a.getLanguage().youSure(), ":ok_hand::white_check_mark:", ":raised_hand::negative_squared_cross_mark:");
//        }
//        else {
//            send(a.getLanguage().youSure(), message.getChatId(), ":ok_hand::white_check_mark:", ":raised_hand::negative_squared_cross_mark:", true);
//        }
//    }
    public void deleteMessage(Message message){
        DeleteMessage dm = new DeleteMessage()
                .setMessageId(message.getMessageId())
                .setChatId(message.getChatId());
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
        return "DeliverySuperBot";
    }

    @Override
    public String getBotToken() {
        return "780864630:AAHpUc01UagThYH7wRi15zJQjwu06A6NaWM";
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

        public List<String> sqlIdList() throws SQLException {
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
        Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.CYRILLIC_TO_LATIN);
        Transliterator toCyrilTrans = Transliterator.getInstance(AmabiliaBot.LATIN_TO_CYRILLIC);
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from table0 where type ="+type);
                while (rs.next()){
                    if (language.equals("Uzbek")||language.equals("English")) lan.add(toLatinTrans.transliterate(rs.getString(column)));
                    else if (language.equals("Russian")) lan.add(toCyrilTrans.transliterate(rs.getString(column)));
                }
                lan.add(Lan.goBack(language));
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
