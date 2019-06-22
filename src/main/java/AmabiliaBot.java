import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.text.SimpleDateFormat;
import java.util.*;

public class AmabiliaBot extends TelegramLongPollingBot {
    private static final String LOGTAG = "AmabiliaLog";
    HashMap<Integer, Order> set = new HashMap<Integer, Order>();
    public Order a;
    public static final long myID = 615351734;
    public static final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";
    public static final String LATIN_TO_CYRILLIC = "Latin-Russian/BGN";
    public static final SimpleDateFormat date=
            new SimpleDateFormat("dd.MM.yyyy");
    public static final SimpleDateFormat time=
            new SimpleDateFormat("HH:mm");

    @Override
    public void onUpdateReceived(Update update) {
        Message m;
        try {
            if (update.hasMessage()) {
                m = update.getMessage();
                if (set.containsKey(update.getMessage().getFrom().getId())) {
                    a = set.get(update.getMessage().getFrom().getId());
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
                    a = new Order(update.getMessage().getFrom());
                    set.put(update.getMessage().getFrom().getId(), a);
                    send(":boom: Новый пользователь!" +
                            "\n" + a.getUser().getFirstName() +" "+ a.getUser().getLastName() +
                            "\n@" + a.getUser().getUserName()+
                            "\nВсего пользователей: " + set.size(), myID);
                    if (m.hasText()) handleIncomingText(m);
                }
            } else if (update.hasCallbackQuery()) {
                handleCallback(update);
            }
            } catch(Exception e){
                BotLogger.error(LOGTAG, e);
            }
    }

    private void handleVoice(Message message) {
    }

    private void handleVideoNote(Message message) {
    }

    private void handleVideo(Message message) {
    }

    private void handleSticker(Message message) {
    }

    private void handleLocation(Message message) {
    }

    private void handlePhoto(Message message) {
    }

    private void handleDocument(Message message) {
        Document doc = message.getDocument();
        a.setDoc(doc);
        if (a.getDirection()!=null&& !a.hasOrdered()) {
            send(a.getLanguage().received() +
                    "\n"+a.getLanguage().preliminary(a) + "\n"+
                    a.getLanguage().doYouConfirm(), message.getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), false);
        } else {
            if (!a.hasOrdered()) send(a.getLanguage().chooseDirection(),message.getChatId(), directions(), true,false);
            else send(a.getLanguage().orderExists(),message.getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), true);
        }
    }

    private void handleContact(Message message) {
        if (a.getDoc() == null) send(a.getLanguage().sendMe(), message.getChatId());
        else {
            a.setContact(message.getContact());
            send(a.getLanguage().contactReceived()+"\n"+a.getLanguage().weWillContact(), message.getChatId(),a.getLanguage().menu(), false,true);
            resendContact();
        }
    }


    private void handleAudio(Message message) {
    }

    private void handleAnimation(Message message) {
    }

    private void handleCallback(Update update) throws InterruptedException, TelegramApiException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId()).setShowAlert(false);
        execute(answer);
        String cb = update.getCallbackQuery().getData();
        a.setIM(null);
        for (int i = 0; i<6; i++){
            if (cb.equals(directions().get(i))) {
                a.setDirection(directions().get(i));
                edit(update.getCallbackQuery().getMessage(), a.getLanguage().confirmChoice(directions().get(i)));
                send(a.getLanguage().howManyPages()+"\n:page_facing_up: "+ a.getPages(), update.getCallbackQuery().getMessage().getChatId(), dial(), true, false);
            }
        }
        for (String b: dial()){
            if (cb.contains(b)&&!cb.contains(":ok:")&&!cb.contains(":arrow_backward:")&&!cb.contains(":x:")) {
                a.setPages(b);
                if (!(cb.contains(":zero:")&&(a.getPages().length()==0))){
                    edit(update.getCallbackQuery().getMessage(), a.getLanguage().howManyPages()+"\n:page_facing_up: "+ a.getPages(), dial(), false);
                }
            }
        }
        if (cb.contains(":arrow_backward:")&&a.getPages().length() > 0) {
            a.pagesBack();
            edit(update.getCallbackQuery().getMessage(), a.getLanguage().howManyPages()+"\n:page_facing_up: " + a.getPages(), dial(), false);
        } else if (cb.contains(":ok:")&&a.getPages().length()>0) {
            a.setTotalCost();
            if (a.getDoc()==null) {
                edit(update.getCallbackQuery().getMessage(), a.getLanguage().sendMe());
            } else if (a.getDirection()!=null){
                deleteMessage(update.getCallbackQuery().getMessage());
                send(a.getLanguage().preliminary(a) +
                        "\n"+a.getLanguage().doYouConfirm(), update.getCallbackQuery().getMessage().getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), false);
            }
        } else if (cb.equals(a.getLanguage().getYes())) {
            if (a.hasOrdered()) send("Заказ перевода "+ a.getDirection()+" от "+a.getUser().getFirstName()+ " отменен пользователем", myID);
            a.clearOrder();
            edit(update.getCallbackQuery().getMessage(), a.getLanguage().cancelled());
        }else if (cb.equals(a.getLanguage().getNo())) {
            deleteMessage(update.getCallbackQuery().getMessage());
        }else if (cb.contains(":x:")) {
            areYouSure(update.getCallbackQuery().getMessage(), true);
        }

    }
    private void handleIncomingText(Message message) throws TelegramApiException, InterruptedException {
        if (message.getText().equals("/start")) {
            if (a.getLanguage()==null) chooseLanguage(message);
            else send(a.getLanguage().welcome(a), message.getChatId(), a.getLanguage().menu(), false,true);
        } else if (message.getText().equals("O'zbek")||message.getText().equals("Русский")||message.getText().equals("English")){
            if      (message.getText().equals("O'zbek")) a.setLanguage(new Uzbek());
            else if (message.getText().equals("Русский")) a.setLanguage(new Russian());
            else if (message.getText().equals("English")) a.setLanguage(new English());
            send(a.getLanguage().welcome(a), message.getChatId(), a.getLanguage().menu(), false, true);
        } else if (a.getLanguage()==null) chooseLanguage(message);
        else if (message.getText().equals(a.getLanguage().menu().get(0))) {
            if (!a.hasOrdered()) send(a.getLanguage().chooseDirection(),message.getChatId(), directions(), true,false);
            else send(a.getLanguage().orderExists(),message.getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), true);
        }
        else if (message.getText().equals(a.getLanguage().menu().get(1))) send(a.getLanguage().cost(),message.getChatId());
        else if (message.getText().equals(a.getLanguage().menu().get(2))) chooseLanguage(message);
        else if (message.getText().equals(a.getLanguage().menu().get(3))&& a.hasOrdered()) send(a.getLanguage().orders(a), message.getChatId(), ":x:", true);
        else if (message.getText().equals(a.getLanguage().menu().get(3))&&!a.hasOrdered()) send(a.getLanguage().emptyOrders(), message.getChatId());
        else if (message.getText().equals(a.getLanguage().getYes())) {
            a.setOrderTime(new Date(System.currentTimeMillis()));
            a.setOrdered(true);
            send(a.getLanguage().confirmOrder(), message.getChatId(), a.getLanguage().menu(), false,true);
            myself();
            if (a.getContact()==null) {
                sendMeNumber(message);
            }
            else {
                resendContact();
                send(a.getLanguage().weWillContact(), message.getChatId(), a.getLanguage().menu(), false,true);
            }
        }
        else if (message.getText().equals(a.getLanguage().getNo())) {
            a.clearOrder();
            send(a.getLanguage().cancelled(), message.getChatId(), a.getLanguage().menu(), false, true);
        }
        else send(a.getLanguage().what(),message.getChatId());
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
        send(text, chatId, list, true, true);
    }
    public void send (String text, long chatId, String a, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        send(text, chatId, list, inline, true);
    }
    public void send (String text, long chatId, String a, String b, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        send(text, chatId, list, inline, true);
    }
    public void send (String message, long chatId, String a, String b, String c, boolean inline){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        list.add(c);
        send(message, chatId, list, inline, false);
    }
    public void send (String text, long chatId, List<String> list, boolean inline, boolean flag) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        List<KeyboardRow> rows2 = new ArrayList<KeyboardRow>();

        for (int i = 0; i < list.size(); i += flag?2:3) {
                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                KeyboardRow row2 = new KeyboardRow();

                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(i)))
                        .setCallbackData(list.get(i)));
                row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(list.get(i))));
                if ((i + 1) < list.size()) {
                    row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                        .setCallbackData(list.get(i + 1)));
                    row2.add(new KeyboardButton().setText(EmojiParser.parseToUnicode(list.get(i+1))));
                }
            if (!flag){
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
        a.setIM(inlineMarkup);
        a.setRM(replyMarkup);
        if (inline) sendMessage.setReplyMarkup(inlineMarkup);
        else sendMessage.setReplyMarkup(replyMarkup);
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void edit (Message message, String newText){
        EditMessageText sendMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(message.getMessageId())
                .setParseMode("HTML")
                .setText(EmojiParser.parseToUnicode(newText));
        sendMessage.setReplyMarkup(a.getIM());
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public void edit (Message message, String newText, String a){
        List<String> list = new ArrayList<String>();
        list.add(a);
        edit(message, newText, list,true);
    }
    public void edit (Message message, String newText, String a, String b){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        edit(message, newText, list, true);
    }
    public void edit (Message message, String newText, String a, String b,String c){
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        list.add(c);
        edit(message, newText, list, false);
    }
    public void edit (Message message, String newText, List<String> list, boolean flag) {
        EditMessageText sendMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(message.getMessageId())
                .setParseMode("HTML")
                .setText(EmojiParser.parseToUnicode(newText));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        for (int i = 0; i < list.size(); i += flag?2:3) {
            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
            row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(i)))
                    .setCallbackData(list.get(i)));
            if ((i + 1) < list.size()) row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(i + 1)))
                    .setCallbackData(list.get(i + 1)));
            if ((i + 2) < list.size() && !flag) row.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(list.get(i + 2)))
                    .setCallbackData(list.get(i + 2)));
            rows.add(row);
        }
        markup.setKeyboard(rows);
        sendMessage.setReplyMarkup(markup);
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }

    public void myself(){
        SendDocument sendMyselfdoc = new SendDocument()
                .setChatId(myID)
                .setDocument(a.getDoc().getFileId())
                .setCaption(EmojiParser.parseToUnicode(":fire:Получен заказ на перевод! " +
                        "\n:fast_forward:Направление: "+a.getDirection()+
                        "\n:page_facing_up:Количество листов: "+ a.getPages()+
                        "\n:date:Заказ оформлен: "+date.format(a.getOrderTime()) + ":clock3:"+time.format(a.getOrderTime()) +
                        "\n:u6307:Язык интерфейса: " + a.getLanguage().getClass().getName()+
                        "\n:moneybag:Стоимость: "+a.getTotalCost()+ " сум"+
                        "\n:watch:Требуется дней: "+a.getDuration()+
                        "\n:busts_in_silhouette:Заказчик: "+ a.getUser().getFirstName()+" @"+a.getUser().getUserName()));
        try {
            execute(sendMyselfdoc);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void resendContact() {
        SendContact sendMyselfContact = new SendContact()
                .setChatId(myID)
                .setPhoneNumber(a.getContact().getPhoneNumber())
                .setFirstName(a.getContact().getFirstName())
                .setLastName(a.getContact().getLastName());
        try {
            execute(sendMyselfContact);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMeNumber(Message message){
        SendMessage sendMessage = new SendMessage()
                .setChatId(message.getChatId())
                .setText(EmojiParser.parseToUnicode(a.getLanguage().sendMeContact()))
                .setParseMode("HTML");
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton().setRequestContact(true).setText(EmojiParser.parseToUnicode(a.getLanguage().myContact()));
        List<KeyboardRow> rows2 = new ArrayList<KeyboardRow>();
        row2.add(keyboardButton);
        rows2.add(row2);
        replyMarkup.setKeyboard(rows2).setResizeKeyboard(true).setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyMarkup);
        try { execute(sendMessage);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    public  void areYouSure(Message message, boolean edit){
        if (edit) edit(message, a.getLanguage().youSure(), a.getLanguage().getYes(), a.getLanguage().getNo());
        else send(a.getLanguage().youSure(), message.getChatId(), a.getLanguage().getYes(), a.getLanguage().getNo(), true);
    }
    public void deleteMessage(Message message){
        DeleteMessage dm = new DeleteMessage()
                .setMessageId(message.getMessageId())
                .setChatId(message.getChatId());
        try {execute(dm);}
        catch (TelegramApiException e) {e.printStackTrace();}
    }
    @Override
    public String getBotUsername() {
        return "zakaz_perevodov_bot";
    }

    @Override
    public String getBotToken() {
        return "862414237:AAEaeXRZgMRrqQZjMfmoeq-fYyyFhc9LmAc";
    }
}