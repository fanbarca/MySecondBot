package SecondPackage;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultLocation;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.cached.InlineQueryResultCachedPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.sql.*;


public class Bot extends TelegramLongPollingBot {
    private String botName = "DeliverySuperBot";
    private String botToken = "780864630:AAHpUc01UagThYH7wRi15zJQjwu06A6NaWM";
    Map<String, List<String>> images = new HashMap<>();
    Order a;
    public static LocalTime startOfPeriod = LocalTime.parse("04:00");
    public static LocalTime endOfPeriod = LocalTime.parse("22:30");


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
                checkNewUser(update);
                if (m.hasText()) handleIncomingText(update);
                else if (m.hasAnimation()) handleAnimation(m);
                else if (m.hasAudio()) handleAudio(m);
                else if (m.hasContact()) handleContact(m, update);
                else if (m.hasDocument()) handleDocument(m);
                else if (m.hasPhoto()) handlePhoto(m);
                else if (m.hasLocation()) handleLocation(update);
                else if (m.hasSticker()) handleSticker(m);
                else if (m.hasVideo()) handleVideo(m);
                else if (m.hasVideoNote()) handleVideoNote(m);
                else if (m.hasVoice()) handleVoice(m);
            } else if (update.hasCallbackQuery()) {
                checkNewUser(update);
                if (update.getCallbackQuery().getMessage().isChannelMessage()){
                    handleChannelCallback(update);
                } else {
                    String cb = update.getCallbackQuery().getData();
                    if (a.getLanguage() == null && !(cb.equals("O'zbek") || cb.equals("Русский") || cb.equals("English")))
                        chooseLanguage(update.getCallbackQuery().getMessage(), true);
                    else handleCallback(update);
                }
            } else if (update.hasInlineQuery()) {
                handleInline(update);
            }
        } catch (Exception e) {
            BotLogger.error(Main.LOGTAG, e);
        }
    }






    private void checkNewUser(Update update) throws SQLException, TelegramApiException {
        String id = "";
        String firstName = "";
        String lastName = "";
        String userName = "";
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
            a = new Order(
                    firstName,
                    DataBase.sqlGetUserData(id).get(1),
                    DataBase.sqlGetUserData(id).get(2),
                    id
            );
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

            a = new Order(
                    firstName,
                    null,
                    null,
                    id
            );
        }
    }









    private void handleChannelCallback(Update update) throws SQLException, TelegramApiException {
        String cb = update.getCallbackQuery().getData();
        String prodId = cb.substring(11);
        if (cb.contains("fromChannel")) {
            //deleteMessage(DataBase.sqlQuery("select image from users where id="+a.getId(), "image"), a.getId());
            if (a.getLanguage() == null) {
                DataBase.sql("UPDATE users SET language = 'Russian' WHERE id =" + a.getId());
                a.setLanguage("Russian");
                sendPicbyId(productText(prodId, a.getId()),
                        a.getId(),
                        productsMarkup(prodId),
                        prodId);
            } else {
                String image = DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image");
                boolean newMessage = image==null;
                if (images.containsKey(a.getId())) {
                    for (String id: images.get(a.getId())) {
                        deleteMessage(id, a.getId());
                    }
                    images.remove(a.getId());
                }
                if (newMessage) {
                    sendPicbyId(productText(prodId, a.getId()),
                            a.getId(),
                            productsMarkup(prodId),
                            prodId);
                } else {
                    editPic(productText(prodId, a.getId()),
                            prodId,a.getId(),Integer.parseInt(image),
                            productsMarkup(prodId));
                }
            }
            a.setAddress(Lan.pressCatalog(a.getLanguage()));
            a.setAlert(true);
        }
    }








    private void handleInline(Update update) throws SQLException,  TelegramApiException {
        String inline = update.getInlineQuery().getQuery();
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                .setInlineQueryId(update.getInlineQuery().getId());
        if (inline.equals("")) answerInlineQuery.setResults(new InlineQueryResultCachedPhoto()
                .setId("22")
                .setPhotoFileId(DataBase.sqlQuery("select imageid from table0 where russian = 'Лого'", "imageid"))
                .setCaption("@"+botName));
        else if (inline.equals("location")) {
            Float latitude = Float.parseFloat(DataBase.sqlQuery("select latitude from users where id ="+a.getId(), "latitude"));
            Float longitude = Float.parseFloat(DataBase.sqlQuery("select longitude from users where id ="+a.getId(), "longitude"));
            answerInlineQuery.setResults(new InlineQueryResultLocation()
                .setId("22")
                .setTitle(Lan.previousLocation(a.getLanguage()))
                .setLatitude(latitude).setLongitude(longitude));
        } else if (inline.equals("address")) {
            String address = DataBase.sqlQuery("select address from users where id ="+a.getId(), "address");
            InputTextMessageContent inputMessageContent = new InputTextMessageContent()
                    .setMessageText(address);
            answerInlineQuery.setResults(new InlineQueryResultArticle()
                    .setId("22")
                    .setTitle(Lan.previousAddress(a.getLanguage()))
                    .setInputMessageContent(inputMessageContent)
                    .setDescription(address));
        }
        execute(answerInlineQuery);
    }

    







    private void handleContact(Message message, Update update) throws SQLException, TelegramApiException {
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
        sendMeLocation(message, false);
    }


    
    
    
    
    
    
    
     private void showMainMenu(boolean edit, Update update) throws SQLException, TelegramApiException {
         InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                        row1.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(0)))
                                .setCallbackData(Lan.mainMenu(a.getLanguage()).get(0)));
                rows.add(row1);
                List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
                        row2.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(1)))
                                .setCallbackData(Lan.mainMenu(a.getLanguage()).get(1)));
                        row2.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(3)))
                                .setCallbackData(Lan.mainMenu(a.getLanguage()).get(3)));
                rows.add(row2);
                List<InlineKeyboardButton> row3 = new ArrayList<InlineKeyboardButton>();
                        row3.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.mainMenu(a.getLanguage()).get(2)))
                                .setCallbackData(Lan.mainMenu(a.getLanguage()).get(2)));
                        row3.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(Lan.share(a.getLanguage())))
                                .setSwitchInlineQuery(""));
                rows.add(row3);
            markup.setKeyboard(rows);
         // editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), a.getId(),Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")), Lan.mainMenu(a.getLanguage()), "Лого", 2);
         if (edit) editPic(Lan.welcome(a.getLanguage(), a.getFirstName()), "Лого", update.getCallbackQuery().getMessage(), markup);
         else sendPic(Lan.welcome(a.getLanguage(), a.getFirstName()), a.getId(), markup, "Лого");
     }
    











    private void handleIncomingText(Update update) throws SQLException, TelegramApiException {
        if (update.getMessage().getText().contains("/start")) {
            if (update.getMessage().getText().contains("selected")) {
                String prodId = update.getMessage().getText().substring(15);
                if (a.getLanguage() == null) {
                    DataBase.sql("UPDATE users SET language = 'Russian' WHERE id =" + a.getId());
                    a.setLanguage("Russian");
                }
                    String image = DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image");
                    if (image!=null) {
                        deleteMessage(image, a.getId());
                    }
                deleteMessage(update.getMessage());
                sendPicbyId(productText(prodId, a.getId()),
                        a.getId(),
                        productsMarkup(prodId),
                        prodId);

            } else {
                if (a.getLanguage() == null) {
                    chooseLanguage(update.getMessage(), false);
                } else {
                    deleteMessage(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image"), a.getId());
                    deleteMessage(update.getMessage());
                    showMainMenu(false, update);
            }
        }
        } else if (update.getMessage().getText().startsWith("+998")) {
            if (a.getNumber() == null) {
                handleContact(update.getMessage(), update);
            }
        } else {
            if (waitingForLocation()) {
                handleLocation(update);
            } else if (waitingForComment()) {
                handleComment(update);
            } else {
                deleteMessage(update.getMessage());
            }
        }
    }








    private void handleComment(Update update) throws TelegramApiException, SQLException {
        DataBase.sql("update users set comment = '"+update.getMessage().getText()+"' where id ="+a.getId());
        deleteMessage(update.getMessage());
        showCart(update, true);
    }







    private void handleCallback(Update update)
            throws TelegramApiException, SQLException, MalformedURLException, IOException {

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
                showMainMenu(true, update);
        }
        if (cb.equals(Lan.backToMenu(a.getLanguage()))) {
                showMainMenu(true, update);
            
        }

        if (cb.equals(Lan.mainMenu("Uzbek").get(0)) ||
                cb.equals(Lan.mainMenu("Russian").get(0)) ||
                cb.equals(Lan.mainMenu("English").get(0)) ||
                cb.equals(Lan.goBack(a.getLanguage()))) {
            if ((a.getLanguage() == null) || (a.getLanguage().equals(""))) {
                chooseLanguage(update.getCallbackQuery().getMessage(), true);
            } else {
                showCatalog(update, true);
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
            if (cb.equals(Lan.listTypes(a.getLanguage()).get(i))) {
                showSubCat(update, i);
            }
        }
        if (cb.contains("category")) {
            List<String> listSubTypes = DataBase.sqlQueryList("select typeid from types", "typeid");
            for (String subtype : listSubTypes) {
                if (cb.contains(subtype)) {
                    editPicItems(cb.substring(0, 1),
                            subtype, update.getCallbackQuery().getMessage());
                }
            }
        }
		if (cb.equals("toCatalog")) {
		    if (images.containsKey(a.getId())) {
                for (String id: images.get(a.getId())) {
                    deleteMessage(id, a.getId());
                }
                images.remove(a.getId());
                showCatalog(update, false);
            } else {
                showCatalog(update, true);
            }
        }
        for (String name : DataBase.showAllProducts(a.getLanguage(), false)) {
            String userid = a.getId();
            String subType = "";
            String prodId = DataBase.sqlQuery("select id from table0 where " + a.getLanguage() + " ='" + name + "'", "id");
            String type = DataBase.sqlQuery("select type from table0 where " + a.getLanguage() + " ='" + name + "'", "type");
            String[] sizes = {"XS", "S", "M", "L", "XL", "XXL"};
            for (String s:sizes) {
                if (cb.equals(s+prodId)){
                    DataBase.sql("insert into cart (userid, item, size) values (" + userid
                            + ",'" + prodId + "', '"+s+"')");
                    a.setAddress(Lan.added(a.getLanguage()));
                    a.setAlert(false);
                    editCaption(productText(prodId, a.getId()), update.getCallbackQuery().getMessage(), productsMarkup(prodId));
                }
            }

            if (cb.contains(prodId)||cb.contains(name)) {
				if (cb.contains("selected")) {
//                        if (occurrences(prodId, userid)==0) {
//                            DataBase.sql("insert into cart (userid, item) values (" + userid
//                                    + ",'" + prodId + "')");
//                        a.setAddress(Lan.added(a.getLanguage()));
//                        a.setAlert(false);
//						}
                    if (images.containsKey(a.getId())) {
                        for (String id: images.get(a.getId())) {
                            deleteMessage(id, a.getId());
                        }
                        images.remove(a.getId());
                        showCart(update, false);
                    } else {
                        showCart(update, true);
                    }
				}

				if (cb.contains("+plus")||cb.contains("-minus")) {
					if (cb.contains("+plus"+prodId)){
					    if (type.equals("0")||type.equals("1")) {
                            chooseSize(update.getCallbackQuery().getMessage(), prodId);
                        } else {
                            DataBase.sql("insert into cart (userid, item) values (" + userid
                                    + ",'" + prodId + "')");
                            a.setAddress(Lan.added(a.getLanguage()));
                            a.setAlert(false);
                            editCaption(productText(prodId, a.getId()), update.getCallbackQuery().getMessage(), productsMarkup(prodId));
                        }
                    } else if (cb.contains("-minus"+prodId)){
                        DataBase.sql("delete from cart where userid =" + userid
                            + " and item = '" + prodId + "'");
                        a.setAddress(Lan.removed(a.getLanguage()));
                        a.setAlert(false);
                        editCaption(productText(prodId, a.getId()), update.getCallbackQuery().getMessage(), productsMarkup(prodId));
                    }
                    }
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
                    editPicItems(type, subType, update.getCallbackQuery().getMessage());
                } else if (cb.contains(Lan.removeFromCart(a.getLanguage()))
                        || cb.contains(Lan.addToCart(a.getLanguage()))
                        || cb.contains(Lan.addMore(a.getLanguage()))) {
                    if (cb.contains(Lan.removeFromCart(a.getLanguage()))) {
                        DataBase.sql("delete from cart where userid =" + userid
                                + " and item = '" + prodId + "'");
                        a.setAddress(Lan.removed(a.getLanguage()));
                        a.setAlert(false);
                        editCaption(productText(prodId, userid), update.getCallbackQuery().getMessage(), markUp(productText(prodId, userid), prodId, (occurrences(prodId, userid)>0)?keybAddMore(name):keybAdd(name), 3));
                    } else if (cb.contains(Lan.addToCart(a.getLanguage()))|| cb.contains(Lan.addMore(a.getLanguage()))) {
                        chooseSize(update.getCallbackQuery().getMessage(), prodId);
                    }
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
                        showMainMenu(true, update);
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
            showMainMenu(true, update);
            
        }
        if (cb.contains(Lan.delivery(a.getLanguage()))) {
            ZoneId z = ZoneId.of("Asia/Tashkent");
            if (LocalTime.now(z).isAfter(startOfPeriod)&&LocalTime.now(z).isBefore(endOfPeriod)) {
                if (DataBase.sqlQueryList("select product from zakaz where userid =" + a.getId()+" and conformed = true", "product").size() > 0) {
                    editPic(Lan.orderExists(a.getLanguage()), update.getCallbackQuery().getMessage(), Lan.YesNo(a.getLanguage()), "Лого", 2);
                } else {
                    DataBase.sql("insert into zakaz (userid, product) values ("
                    +a.getId()+", '"
                    +curretCart(a.getId())+"' )");
                    if (a.getNumber()!=null) {
                        sendMeLocation(update.getCallbackQuery().getMessage(), true);
                    }
                    else sendMeNumber(a.getId());
                }
            } else {
                a.setAddress(Lan.tooLate(a.getLanguage()));
                a.setAlert(true);
            }
        }
        if (cb.equals(Lan.YesNo(a.getLanguage()).get(0))||(cb.equals(Lan.YesNo(a.getLanguage()).get(1)))) {
            if (cb.equals(Lan.YesNo(a.getLanguage()).get(0))) {
                Adminbot order = new Adminbot();
                order.sendMe("Заказ от " + a.getFirstName()+" отменён\n"+
                        DataBase.sqlQuery("select product from zakaz where userid = "+ a.getId(), "product"));
                DataBase.sql("update zakaz set conformed = false, time = null, product = '"
                    +curretCart(a.getId())+"' where userid =" + a.getId());
                if (a.getNumber()!=null) {
                    sendMeLocation(update.getCallbackQuery().getMessage(), true);
                }
                else sendMeNumber(a.getId());
            } else if (cb.equals(Lan.YesNo(a.getLanguage()).get(1))) {
                showOrders(update);
            }
        }
        if (cb.contains(Lan.clearOrders(a.getLanguage()))) {
            clearOrders(update);
            showMainMenu(true, update);
        }
        if (cb.contains("Отмена")) {
            showCart(update, true);
        }
        if (cb.contains("OrderTime")) {
            String time = cb.substring(9);
            DataBase.sql("update zakaz set time ='"+time+"' where userid = "+a.getId());
            if (a.getNumber() == null) {
                deleteMessage(update.getCallbackQuery().getMessage());
                sendMeNumber(a.getId());
            } else confirm(update, time);
        }
//        if (cb.contains("UseNewLocation")) {
//            sendMeLocation(update.getCallbackQuery().getMessage());
//        }
//        if (cb.contains("UseOldLocation")) {
//            editCaption(Lan.orderTime(a.getLanguage()) + Lan.tooLate(a.getLanguage()), a.getId(),
//                Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")),
//                timeKeys());
//        }
        if (cb.contains(Lan.removeSelectively(a.getLanguage()))) {
            editCaption(Lan.mainMenu(a.getLanguage()).get(3) + "\n" + curretCart(a.getId()), a.getId(),
                Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")),
                deleteItemsKey(a.getId()));
        }
        if (cb.contains(Lan.addComment(a.getLanguage()))) {
            editCaption(Lan.enterComment(a.getLanguage()), a.getId(),
                Integer.parseInt(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image")),
                simpleMarkUp(Lan.cancelComment(a.getLanguage())));
                a.setAddress(Lan.enterComment(a.getLanguage()));
                a.setAlert(true);
                DataBase.sql("update users set comment ='*waiting*' where id = "+a.getId());
        }
        if (cb.contains(Lan.cancelComment(a.getLanguage()))) {
            DataBase.sql("update users set comment = null where id = "+a.getId());
            a.setAddress(Lan.commentCancelled(a.getLanguage()));
            a.setAlert(false);
            showCart(update, true);
        }
        if (cb.contains(Lan.deleteComment(a.getLanguage()))) {
            DataBase.sql("update users set comment = null where id = "+a.getId());
            a.setAddress(Lan.commentDeleted(a.getLanguage()));
            a.setAlert(false);
            showCart(update, true);
        }
		
        if (a.getAddress()!=null) answer.setShowAlert(a.getAlert()).setText(a.getAddress());
        execute(answer);
    }






    private void chooseSize(Message message, String prodId) throws SQLException, TelegramApiException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("XS"))
                .setCallbackData("XS"+prodId));
        row.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("S"))
                .setCallbackData("S"+prodId));
        row.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("M"))
                .setCallbackData("M"+prodId));
        rows.add(row);
        List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
        row2.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("L"))
                .setCallbackData("L"+prodId));
        row2.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("XL"))
                .setCallbackData("XL"+prodId));
        row2.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("XXL"))
                .setCallbackData("XXL"+prodId));
        rows.add(row2);
        List<InlineKeyboardButton> row3 = new ArrayList<InlineKeyboardButton>();
        row3.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode(Lan.back(a.getLanguage())))
                .setCallbackData(prodId));
        row3.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                .setCallbackData(Lan.backToMenu(a.getLanguage())));
        rows.add(row3);
        markup.setKeyboard(rows);
        editPic(Lan.whatSize(a.getLanguage()), prodId, message, markup);
        a.setAddress(Lan.whatSize(a.getLanguage()));
        a.setAlert(true);
    }







    private void showCatalog(Update update, boolean edit) throws TelegramApiException, SQLException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            for (int i = 0 ; i<Lan.listTypes(a.getLanguage()).size(); i+=2) {
                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(Lan.listTypes(a.getLanguage()).get(i)))
                        .setCallbackData(Lan.listTypes(a.getLanguage()).get(i)));
                if (i+1<Lan.listTypes(a.getLanguage()).size()) row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(Lan.listTypes(a.getLanguage()).get(i+1)))
                        .setCallbackData(Lan.listTypes(a.getLanguage()).get(i+1)));
                rows.add(row);
            }
            List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.backToMenu(a.getLanguage())))
                    .setCallbackData(Lan.backToMenu(a.getLanguage())));
                    rows.add(row2);
        markup.setKeyboard(rows);

        if (edit) editPic(Lan.chooseDish(a.getLanguage()),"Лого", update.getCallbackQuery().getMessage(), markup);
		else sendPic(Lan.chooseDish(a.getLanguage()), a.getId(), markup, "Лого");
    }











    private void showSubCat(Update update, int i) throws TelegramApiException, SQLException {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        	List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
            for (String sub:listSubTypes(i)) {
				if (!DataBase.sqlQueryList("select id from table0 where instock = true and type = '"+i+"' and subtype = '"+sub+"'", "id").isEmpty()) {
					List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
					row.add(new InlineKeyboardButton()
						.setText(EmojiParser.parseToUnicode(
								DataBase.sqlQuery("select "+a.getLanguage()+" from types where typeid ="+sub, a.getLanguage())))
						.setCallbackData(i+"category"+sub));
						rows.add(row);
				}
            }
            List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
            row2.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.goBack(a.getLanguage())))
                    .setCallbackData(Lan.goBack(a.getLanguage())));
                    rows.add(row2);
        markup.setKeyboard(rows);
		
		if (rows.size()>1){
        	editPic(Lan.listTypes(a.getLanguage()).get(i), "Лого",
        		update.getCallbackQuery().getMessage(), markup);
        } else {
            a.setAddress(Lan.emptyOrders(a.getLanguage()));
            a.setAlert(true);
        }
    }




    public static List<String> listSubTypes(int i) throws SQLException{
        return listSubTypes(i, "typeid");
    }




    public static List<String> listSubTypes(int i, String column) throws SQLException {
        String condition = "";
        switch (i) {
            case 0:
                condition = "clothes = true and female = true";
                break;
            case 1:
                condition = "clothes = true and male = true";
                break;
            case 2:
                condition = "shoes = true and female = true";
                break;
            case 3:
                condition = "shoes = true and male = true";
                break;
            case 4:
                condition = "accessories = true and female = true";
                break;
            case 5:
                condition = "accessories = true and male = true";
                break;
            case 6:
                condition = "cosmetics = true";
                break;
            case 7:
                condition = "toddlers = true";
                break;
            case 8:
                condition = "kids = true and female = true";
                break;
            case 9:
                condition = "kids = true and male = true";
                break;
        }

        return DataBase.sqlQueryList("select "+column+" from types where "+ condition, column);
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












    private String productText(String prodId, String userid) throws SQLException {
        int occurrences = occurrences(prodId, userid);
        String balls = "";
        String emoji = DataBase.sqlQuery("select emoji from table0 where id = "+prodId, "emoji");
        for (int i = 0; i<occurrences; i++){
            balls += emoji==null?":large_blue_circle:":emoji;
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
        DataBase.sql("update users set comment = null where id = " + a.getId());
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
                ":gb: Choose language", a.getId(), message.getMessageId(), list, "Лого",3);
        else {
            sendPic(":uz: Tilni tanlang\n" +
                    ":ru: Выберите язык\n" +
                    ":gb: Choose language", a.getId(), list, "Лого", 3);
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









public void sendMeLocation(Message message, boolean edit) throws TelegramApiException, SQLException {
    boolean hasLocation = DataBase.sqlQuery("select latitude from users where id ="+a.getId(), "latitude")!=null;
    boolean hasAddress = DataBase.sqlQuery("select address from users where id ="+a.getId(), "address")!=null;
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        if (hasLocation) {
            List<InlineKeyboardButton> row0 = new ArrayList<InlineKeyboardButton>();
            row0.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.previousLocation(a.getLanguage())))
                    .setSwitchInlineQueryCurrentChat("location"));
            rows.add(row0);
        } else if (hasAddress) {
            List<InlineKeyboardButton> row0 = new ArrayList<InlineKeyboardButton>();
            row0.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.previousAddress(a.getLanguage())))
                    .setSwitchInlineQueryCurrentChat("address"));
            rows.add(row0);
        }
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(Lan.clearOrders(a.getLanguage())))
                        .setCallbackData("Отмена"));
        rows.add(row);
        markup.setKeyboard(rows);
        if (edit) editCaption(Lan.sendMeLocation(a.getLanguage()), message, markup);
        else {
            sendPic(Lan.sendMeLocation(a.getLanguage()), a.getId(), markup, "Лого");
        }
        DataBase.sql("update users set rmid = 0 where id = " + message.getChatId());
    }










    public void editPicItems(String typeID, String subTypeID, Message message) throws TelegramApiException, SQLException {
        List<String> listID = DataBase.sqlQueryList("select id from table0 where instock = true and type = '"+typeID +"' and subtype = '"+subTypeID +"'", "id");
        if (listID.size() != 0) {
            deleteMessage(message);
			List<String> sentArray = new ArrayList<>();
            for (String id : listID){
                	sentArray.add(
                        sendPic(productText(id, a.getId()),
                        a.getId(),
                        productsMarkup(id),
                        DataBase.sqlQuery("select "+a.getLanguage()+" from table0 where id ="+id,a.getLanguage())));
            }
            images.put(a.getId(), sentArray);
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
        if (text.length()<1024) imp.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else imp.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
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
        Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
        String file_id;
        if (productId.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId, "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        InputMediaPhoto imp = new InputMediaPhoto();
        imp.setMedia(file_id);
        if (text.length()<1024) imp.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else imp.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        EditMessageMedia em = new EditMessageMedia();
        em.setChatId(message.getChatId());
        em.setMessageId(messageId);
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
        if (text.length()<1024) imp.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else imp.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        EditMessageMedia em = new EditMessageMedia();
        em.setChatId(message.getChatId());
        em.setMessageId(message.getMessageId());
        em.setMedia(imp);
        em.setReplyMarkup(markup);
        execute(em);
        DataBase.sql("update users set image =" + message.getMessageId() + " where id =" + message.getChatId());
    }






    public void editPic(String text, String productId, String chatid, int messageid, InlineKeyboardMarkup markup) throws TelegramApiException, SQLException {
        String file_id;
        if (productId.equals("Лого")||productId==null)
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId, "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        InputMediaPhoto imp = new InputMediaPhoto();
        imp.setMedia(file_id);
        if (text.length()<1024) imp.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else imp.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        EditMessageMedia em = new EditMessageMedia();
        em.setChatId(chatid);
        em.setMessageId(messageid);
        em.setMedia(imp);
        em.setReplyMarkup(markup);
        execute(em);
        DataBase.sql("update users set image =" + messageid + " where id =" + chatid);
    }








    public void editCaption(String text, String chatId, int messageid, InlineKeyboardMarkup markup) throws TelegramApiException, SQLException {
        //Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));

        EditMessageCaption ec = new EditMessageCaption();
        ec.setChatId(chatId);
        ec.setMessageId(messageid);
        if (text.length()<1024) ec.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else ec.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        if (markup!=null) ec.setReplyMarkup(markup);
        execute(ec);
    }












    public void editCaption(String text, Message message, InlineKeyboardMarkup markup) throws TelegramApiException, SQLException {
        //Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
        EditMessageCaption ec = new EditMessageCaption();
        ec.setChatId(message.getChatId().toString());
        ec.setMessageId(message.getMessageId());
        if (text.length()<1024) ec.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else ec.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        if (markup!=null) ec.setReplyMarkup(markup);
        execute(ec);
    }









    public String sendPic(String text, String chatId, List<String> inline, String productName, int flag) throws SQLException, TelegramApiException {
        String file_id = "";
        if (productName.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where " + a.getLanguage() + " = '" + productName + "'", "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        SendPhoto aa = new SendPhoto();
        aa.setChatId(chatId);
        aa.setPhoto(file_id);
        if (text.length()<1024) aa.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else aa.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
                InlineKeyboardMarkup inlineMarkup = markUp(text, "Лого",inline, flag);
        aa.setReplyMarkup(inlineMarkup);

            String image = execute(aa).getMessageId().toString();
            DataBase.sql("update users set image =" + image + " where id =" + chatId);
            return image;
    }


	
	
	
	
	
	
	public String sendPic(String text, String chatId, InlineKeyboardMarkup inlineMarkup, String productName) throws SQLException, TelegramApiException {
        String file_id = "";
        if (productName.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where " + a.getLanguage() + " = '" + productName + "'", "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        SendPhoto aa = new SendPhoto();
        aa.setChatId(chatId);
        aa.setPhoto(file_id);
        if (text.length()<1024) aa.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else aa.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        aa.setReplyMarkup(inlineMarkup);

            String image = execute(aa).getMessageId().toString();
            DataBase.sql("update users set image =" + image + " where id =" + chatId);
            return image;
    }









    public void toChannel(String text, InlineKeyboardMarkup inlineMarkup, String productId) throws SQLException, TelegramApiException {
        String file_id = "";
        if (productId.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where id = " + productId , "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        SendPhoto aa = new SendPhoto();
        aa.setChatId(Adminbot.channelId);
        aa.setPhoto(file_id);
        if (text.length()<1024) aa.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else aa.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        aa.setReplyMarkup(inlineMarkup);
        execute(aa);
    }









    public String sendPicbyId(String text, String chatId, InlineKeyboardMarkup inlineMarkup, String productId) throws SQLException, TelegramApiException {
        String file_id = "";
        if (productId.equals("Лого"))
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        else {
            file_id = DataBase.sqlQuery("SELECT imageid from table0 where Id = '" + productId + "'", "imageid");
            if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");
        }
        SendPhoto aa = new SendPhoto();
        aa.setChatId(chatId);
        aa.setPhoto(file_id);
        if (text.length()<1024) aa.setCaption(EmojiParser.parseToUnicode(text)).setParseMode("HTML");
        else aa.setCaption(EmojiParser.parseToUnicode(text.substring(0, 1020)+"...")).setParseMode("HTML");
        aa.setReplyMarkup(inlineMarkup);
        String image = execute(aa).getMessageId().toString();
        DataBase.sql("update users set image =" + image + " where id =" + chatId);
        return image;
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
        if (waitingForLocation()) {
            if (update.getMessage().hasLocation()) DataBase.sql("update users set latitude = '"+update.getMessage().getLocation().getLatitude()+"', longitude = '"+update.getMessage().getLocation().getLongitude()+"' where id ="+ a.getId());
            else DataBase.sql("update users set address = '"+update.getMessage().getText()+"' where id ="+ a.getId());
            sendPic(Lan.orderTime(a.getLanguage()), a.getId(),
                timeKeys(), "Лого");
            DataBase.sql("update users set rmid = 1 where id = " + a.getId());
        }
        deleteMessage(DataBase.sqlQuery("SELECT image from users where id=" + a.getId(), "image"), a.getId());
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
//        if (hours<8) {
            for (int i = 0; i<=(endOfPeriod.getHour()-startOfPeriod.getHour())*60+30; i+=30) {
                menu.add(dtf.format(startOfPeriod.plusMinutes(i)));
            }
//        } else if (hours<19) {
//            int last=60;
//            if (hours<18) {
//                if (minutes<5) {
//                    menu.add(dtf.format(LocalTime.now(z).plusHours(1).truncatedTo(ChronoUnit.HOURS)));
//                    last = 90;
//                } else if (minutes<15) {
//                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(75)));
//                    last = 90;
//                } else if (minutes<30) {
//                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(90)));
//                    last = 120;
//                } else if (minutes<45) {
//                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(105)));
//                    last = 120;
//                } else {
//                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusHours(2)));
//                    last = 150;
//                }
//                for (int i = last; i<(19-LocalTime.now(z).getHour())*60+1; i+=30) {
//                    menu.add(dtf.format(LocalTime.now(z).truncatedTo(ChronoUnit.HOURS).plusMinutes(i)));
//                }
//            } else {
//                if (minutes<20) {
//                    menu.add(dtf.format(LocalTime.now(z).withHour(19).withMinute(0)));
//                } else {
//                    menu.add(Lan.tooLate(a.getLanguage()));
//                }
//            }
//        }
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











    private boolean waitingForLocation() throws SQLException{
        return DataBase.sqlQuery("SELECT rmid from users where id=" + a.getId(), "rmid").equals("0");
    }





    private boolean waitingForComment() throws SQLException{
        return DataBase.sqlQuery("SELECT comment from users where id=" + a.getId(), "comment").equals("*waiting*");
    }













    private void confirm(Update update, String time) throws SQLException, TelegramApiException {
        String address = DataBase.sqlQuery("select address from users where id ="+a.getId(), "address");
        // String latitude = DataBase.sqlQuery("select latitude from users where id ="+a.getId(), "latitude");
        if (address!=null) address= "<b>Адрес:</b> "+address+"\n";
        else address="<b>Локация получена</b>\n";
        String comment = DataBase.sqlQuery("select comment from users where id ="+a.getId(), "comment");
        if (comment!=null) {
            DataBase.sql("update zakaz set comment = '"+comment+"' where userid = " + a.getId());
            comment= "<b>Комментарий:</b> "+comment+"\n";
        }
        else comment = "";
        Adminbot order = new Adminbot();
        //String messageId =
        order.sendMe("<b>Новый заказ</b>\n\n"
                    +"<b>Имя клиента:</b> "+ a.getFirstName()+"\n"
                    +"<b>Номер клиента:</b> "+ a.getNumber()+"\n"
                    +"<b>Время доставки:</b> "+time+"\n"
                    +address+comment
                    +"<b>Заказ:</b> \n\n"+curretCart(a.getId()));
        //String adminId = DataBase.sqlQuery("select id from users where admin = true", "id");
        //DataBase.sql("update zakaz set messageId = "+messageId+" where admin = true");
        //if (latitude!=null&&longitude!=null) order.sendLocation(adminId,Float.parseFloat(latitude), Float.parseFloat(longitude), null);
        //order.sendContact(a.getFirstName(), a.getNumber());
        clearCart(update);

        DataBase.sql("update zakaz set conformed = true where userid = " + a.getId());
        if (update.hasMessage()) {
            showMainMenu(false, update);
        }
            showMainMenu(true, update);
        a.setAddress(Lan.orderPlaced(a.getLanguage()));
        a.setAlert(true);
    }












    private void handleDocument(Message message) {
        deleteMessage(message);
    }








    private void handleAudio(Message message) {
        Adminbot ab = new Adminbot();
        ab.forwardMessage(message, Adminbot.myID);
    }









    private void handleAnimation(Message message) {
        Adminbot ab = new Adminbot();
        ab.forwardMessage(message, Adminbot.myID);
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








	private InlineKeyboardMarkup productsMarkup(String productId) throws SQLException {
        	int occ = occurrences(productId, a.getId());
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
		
			List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
				row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode((occ>0)?Lan.addMore(a.getLanguage()):Lan.addToCart(a.getLanguage())))
                    .setCallbackData("+plus"+productId));
			if (occ>0) row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.removeFromCart(a.getLanguage())))
                    .setCallbackData("-minus"+productId)); 
            rows.add(row);
		
            List<InlineKeyboardButton> row0 = new ArrayList<InlineKeyboardButton>();
        	row0.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.goBack(a.getLanguage())))
                    .setCallbackData("toCatalog"));
            if (occ!=0) row0.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(Lan.delivery(a.getLanguage())))
                    .setCallbackData("selected"+productId));    
            rows.add(row0);
            // List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
            // row1.add(new InlineKeyboardButton()
            //     .setText(EmojiParser.parseToUnicode(Lan.share(a.getLanguage())))
            //     .setSwitchInlineQuery(""));
            // rows.add(row1);
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
                            String comment = DataBase.sqlQuery("select comment from users where id ="+a.getId(), "comment");
                            List<InlineKeyboardButton> rowOne = new ArrayList<InlineKeyboardButton>();
                            if (comment!=null) {
                                if(!comment.equals("*waiting*"))
                            rowOne.add(new InlineKeyboardButton()
                                    .setText(EmojiParser.parseToUnicode(Lan.deleteComment(a.getLanguage())))
                                    .setCallbackData(Lan.deleteComment(a.getLanguage())));
                            } else {
                                rowOne.add(new InlineKeyboardButton()
                                    .setText(EmojiParser.parseToUnicode(Lan.addComment(a.getLanguage())))
                                    .setCallbackData(Lan.addComment(a.getLanguage())));
                            }
                            List<InlineKeyboardButton> lastRow = new ArrayList<InlineKeyboardButton>();
                            lastRow.add(new InlineKeyboardButton()
                                    .setText(EmojiParser.parseToUnicode(Lan.clearCart(a.getLanguage())))
                                    .setCallbackData(Lan.clearCart(a.getLanguage())));
                            if (DataBase.sqlQueryList("select distinct item from cart where userid ="+a.getId(), "item").size()!=1) {
                                lastRow.add(new InlineKeyboardButton()
                                        .setText(EmojiParser.parseToUnicode(Lan.removeSelectively(a.getLanguage())))
                                        .setCallbackData(Lan.removeSelectively(a.getLanguage())));
                            }
                            rows.add(rowOne);
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
        String comment = DataBase.sqlQuery("select comment from users where id ="+a.getId(), "comment");
        if (comment!=null) comment= "<b>Комментарий:</b> "+comment+"\n";
        else comment = "";
        String text = Lan.mainMenu(a.getLanguage()).get(3) + "\n"
                    + curretCart(a.getId()) +"\n\n"
                    + comment
                    + Lan.deliveryCost(a.getLanguage())+"\n"
                    +"<b>"+Lan.tooLate(a.getLanguage())+"</b>";
            if (items.size() == 0) {
                a.setAddress(Lan.cartIsEmpty(a.getLanguage()));
                a.setAlert(true);
            } else {
                Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+a.getId(), "image"));
                if (edit) editPic(text, a.getId(), messageId, null, "Лого", 2);
                else sendPic(text, a.getId(), null, "Лого", 2);
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
            String comment = DataBase.sqlQuery("select comment from zakaz where userid ="+a.getId(), "comment");
            if (comment!=null) comment= "<b>Комментарий:</b> "+comment+"\n";
            else comment = "";
                editPic(Lan.mainMenu(a.getLanguage()).get(1)+"\n"
                    +Lan.deliveryTime(a.getLanguage())+time+"\n"
                    +address+comment
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
        Map<String, List<String>> sizes = new HashMap<String, List<String>>();

        for (String s : items) {
            Integer number = Collections.frequency(items, s);
            Integer cost = Integer.parseInt(DataBase.sqlQuery("select cost from table0 where id =" + s, "cost"));
            List<String> size = DataBase.sqlQueryList("select size from cart where item ='"+s+"' and userid ="+id, "size");
            List<Integer> aa = new ArrayList<>();
            aa.add(number);
            aa.add(cost);
            itemNames.put(s, aa);
            sizes.put(s, size);
        }
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : itemNames.entrySet()) {
            String sizesArray = "";
            if (sizes.get(entry.getKey()).contains(null)) sizesArray = "";
            else sizesArray= sizes.get(entry.getKey()).toString();
            String name = DataBase.sqlQuery("select "+a.getLanguage()+" from table0 where id =" + entry.getKey(), a.getLanguage());
            cart += name + "  -  " + entry.getValue().get(0) + " * " + entry.getValue().get(1) + " = " + entry.getValue().get(0) * entry.getValue().get(1) + Lan.currency(a.getLanguage()) + " "+sizesArray+"\n";
            result += entry.getValue().get(0) * entry.getValue().get(1);
            list.add(":heavy_multiplication_x: "+entry.getKey());
        }
        if (list.size()>1) cart += "\n" + Lan.total(a.getLanguage()) + result + Lan.currency(a.getLanguage());
        return cart;
    }










}