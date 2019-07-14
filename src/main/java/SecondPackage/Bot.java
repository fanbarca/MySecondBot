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

public class Bot extends TelegramLongPollingBot {
    Order a;
    @Override
    public void onUpdateReceived(Update update) {
        Message m;
        try {
            if (update.hasMessage()) {
                m = update.getMessage();
                if (DataBase.sqlIdList().contains(m.getFrom().getId().toString())) {
                        a = new Order(
                                DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(0),
                                DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(1),
                                DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(2),
                                DataBase.sqlGetUserData(m.getFrom().getId().toString()).get(3)
                                );
                        if (m.hasText()) handleIncomingText(m);
                        // else if (m.hasAnimation()) handleAnimation(m);
                        // else if (m.hasAudio()) handleAudio(m);
                        // else if (m.hasContact()) handleContact(m);
                        // else if (m.hasDocument()) handleDocument(m);
                        // else if (m.hasPhoto()) handlePhoto(m);
                        // else if (m.hasLocation()) handleLocation(m);
                        // else if (m.hasSticker()) handleSticker(m);
                        // else if (m.hasVideo()) handleVideo(m);
                        // else if (m.hasVideoNote()) handleVideoNote(m);
                        // else if (m.hasVoice()) handleVoice(m);
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
                                cbm.getFrom().getFirstName()+"','"+
                                cbm.getFrom().getLastName()+"','"+
                                cbm.getFrom().getUserName()+"','"+
                                cbm.getMessageId()+"')");
                            a = new Order(
                                    DataBase.sqlGetUserData(cbm.getFrom().getId().toString()).get(0),
                                    null,
                                    null,
                                    DataBase.sqlGetUserData(cbm.getFrom().getId().toString()).get(3)
                            );
                }
                handleCallback(update);
            }
        } catch(Exception e){
                BotLogger.error(Main.LOGTAG, e);
        }
    }

    private void handleCallback(Update update) {

	}

	private void handleIncomingText(Message m) {

	}

	@Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }
    public void editPic(String text, Message message, List<String> list, String productName, int flag) throws TelegramApiException, SQLException {
                Integer messageId= Integer.parseInt(DataBase.sqlQuery("select image from users where id="+message.getChatId(), "image"));
                InputMediaPhoto imp = new InputMediaPhoto();
                imp.setMedia(DataBase.sqlQuery("SELECT imageid from table0 where "+a.getLanguage()+" = '"+productName+"'", "imageid"));
                EditMessageMedia em = new EditMessageMedia();
                em.setChatId(message.getChatId());
                em.setMessageId(messageId);
                em.setMedia(imp);
                EditMessageCaption ec = new EditMessageCaption();
                ec.setChatId(message.getChatId().toString());
                ec.setMessageId(messageId);
                ec.setCaption(EmojiParser.parseToUnicode(text));
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
                execute(em);
                execute(ec);
    }

     public void sendPic(String text, Message message, List<String> inline,String productName, int flag) throws SQLException, TelegramApiException {
        SendPhoto aa = new SendPhoto();
                aa.setChatId(message.getChatId());
                aa.setPhoto(DataBase.sqlQuery("SELECT imageid from table0 where "+a.getLanguage()+" = '"+productName+"'", "imageid"));
                aa.setCaption(EmojiParser.parseToUnicode(text));
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
}
