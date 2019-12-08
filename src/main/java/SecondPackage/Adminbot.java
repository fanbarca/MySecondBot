package SecondPackage;

import com.vdurmont.emoji.EmojiParser;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class Adminbot extends TelegramLongPollingBot {

    String messageid = "";
    public static final long myID = 615351734;
    public static String password = "54321";
    private String category = "";
    private String subcategory = "";
    private String listener = "";
    static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    static final String LATIN_TO_CYRILLIC = "Latin-Cyrillic";
    static TimeZone zone = TimeZone.getTimeZone("Asia/Tashkent");
    static SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
    static SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    List<String> list = new ArrayList<String>();
    String russian = "";
    public static final String channelId = "-1001404493971";

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
        } catch (SQLException | TelegramApiException e1) {
            e1.printStackTrace();
        }
    }

    private void checkAdmin(Update update, String id) throws SQLException, TelegramApiException {
        List<String> admins = DataBase.sqlQueryList("select id from users where admin = true", "id");
        if (admins.contains(id)) {
            allow(update, id);
        } else {
            enterPassword(update);
        }
	}

	private void enterPassword(Update update) throws SQLException, TelegramApiRequestException {
        if (update.hasMessage()) {
            deleteMessage(update.getMessage());
            String adminmessage = DataBase.sqlQuery("select adminmessage from users where id="+update.getMessage().getChatId(), "adminmessage");
            if (adminmessage!=null) {
            deleteMessage(adminmessage, update.getMessage().getChatId().toString());
            send("Введите пароль", update.getMessage().getChatId().toString(), null, true, 1);
            }
            else send("Введите пароль", update.getMessage().getChatId().toString(), null, true, 1);
            listener = "password";
        } else {
            edit(update.getCallbackQuery().getMessage(), "Введите пароль", null, 1);
            listener = "password";
        }
	}


    private List<String> columns(){
        List<String> columns = new ArrayList<>();
                            columns.add("Clothes");
                            columns.add("Accessories");
                            columns.add("Shoes");
                            columns.add("Male");
                            columns.add("Kids");
                            columns.add("Cosmetics");
                            columns.add("Toddlers");
                            columns.add("Female");
        return columns;
    }

	private void allow(Update update, String id) throws SQLException, TelegramApiException {
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
                            DataBase.sql("insert into table0 (id, russian, type, instock, subtype) values ("+
                            String.format("%04d", rand.nextInt(8999)+1000)+", '"+russian+"', '"+category+"', true, '"+subcategory+"')");
                            listener = "Uzbek";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите название продукта на узбекском", list, 3);
                        } else if (listener.equals("Uzbek")) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE table0 SET uzbek = '"+Name+"' where russian = '"+russian+"'");
                            listener = "English";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите название продукта на английском", list, 3);
                        } else if (listener.equals("English")) {
                            String Name = update.getMessage().getText();
                            listener = "Cost";
                            DataBase.sql("UPDATE table0 SET english = '"+Name+"' where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите стоимость продукта", list, 3);
                        } else if (listener.equals("Cost")) {
                            String cost = update.getMessage().getText();
                            listener = "Russiandescription";
                            DataBase.sql("UPDATE table0 SET cost = "+cost+" where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите описание на русском", list, 3);
                        } else if (listener.equals("Russiandescription")) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE table0 SET Russiandescription = '"+Name+"' where russian = '"+russian+"'");
                            listener = "Uzbekdescription";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите описание на узбекском", list, 3);
                        } else if (listener.equals("Uzbekdescription")) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE table0 SET Uzbekdescription = '"+Name+"' where russian = '"+russian+"'");
                            listener = "Englishdescription";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите описание на английском", list, 3);
                        } else if (listener.equals("Englishdescription")) {
                            String Name = update.getMessage().getText();
                            listener = "emoji";
                            DataBase.sql("UPDATE table0 SET Englishdescription = '"+Name+"' where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите emoji", simpleMarkUp("Пропустить"));
                        } else if (listener.equals("emoji")) {
                            String Name = update.getMessage().getText();
                            listener = "";
                            DataBase.sql("UPDATE table0 SET emoji = '"+Name+"' where russian = '"+russian+"'");
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), "Готово", mainKeyboard(null), 1);
                        } else if (listener.length()>4) {
                            if (columnsListNames().contains(listener.substring(4))) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE table0 SET "+listener.substring(8)+" = '"+Name+"' where id = "+listener.substring(0,4));
                            edit(update.getMessage(), "Готово", mainKeyboard(null), 1);
                            listener = "";
                            deleteMessage(update.getMessage());
                            }
                        } else if (listener.equals("RussianCategory")) {
                            russian = update.getMessage().getText();
                            Random rand = new Random();
                            DataBase.sql("insert into types (typeid, russian) values ("+String.format("%04d", rand.nextInt(8999)+1000)+", '"+russian+"')");
                            listener = "UzbekCategory";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите название категории на узбекском", list, 3);
                        } else if (listener.equals("UzbekCategory")) {
                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE types SET uzbek = '"+Name+"' where russian = '"+russian+"'");
                            listener = "EnglishCategory";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nВведите название категории на английском", list, 3);
                        } else if (listener.equals("EnglishCategory")) {




                            String Name = update.getMessage().getText();
                            DataBase.sql("UPDATE types SET english = '"+Name+"' where russian = '"+russian+"'");
                            listener = "";
                            deleteMessage(update.getMessage());
                            edit(update.getMessage(), russian+"\nУкажите критерии", updateMarkup());
                        } else {
                            deleteMessage(update.getMessage());
                        }
                    }
                }





        } else if (update.hasCallbackQuery()) {
            AnswerCallbackQuery answer = new AnswerCallbackQuery()
                .setCallbackQueryId(update.getCallbackQuery().getId());
            String cb = update.getCallbackQuery().getData();


                if (cb.contains("update")) {
                        String column = cb.substring(6);
                        DataBase.sql("UPDATE types SET "+column+" = not "+column+" where russian = '"+russian+"'");
                    edit(update.getCallbackQuery().getMessage(), russian+"\nУкажите критерии", updateMarkup());
                }
                if (cb.contains("editCat")) {
                    russian = cb.substring(7);
                    edit(update.getCallbackQuery().getMessage(), russian+"\nУкажите критерии", updateMarkup());
                }
				if (cb.contains("delCat")) {
                    russian = cb.substring(6);
					DataBase.sql("delete from types where russian = '"+russian+"'");
                    edit(update.getCallbackQuery().getMessage(), "Выберите действие", mainKeyboard(null), 1);
					answer.setShowAlert(false).setText("Удалено");
                }

            if(cb.equals("Обновить категорию")) {
                List<String> ll = DataBase.sqlQueryList("select russian from types", "russian");
                if (ll.isEmpty()){
                    answer.setShowAlert(false).setText("Категорий нет");
                } else {
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                    for (String s:ll) {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(s))
                                .setCallbackData("editCat"+s));
                        rows.add(row);
                    }
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Назад"))
                            .setCallbackData("Назад"));
                    rows.add(row);
                    markup.setKeyboard(rows);
                    edit(update.getCallbackQuery().getMessage(), "Обновить категорию", markup);
                }
            }
			
			if(cb.equals("Удалить категорию")) {
                List<String> ll = DataBase.sqlQueryList("select russian from types", "russian");
                if (ll.isEmpty()){
                    answer.setShowAlert(false).setText("Категорий нет");
                } else {
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                    for (String s:ll) {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(s))
                                .setCallbackData("delCat"+s));
                        rows.add(row);
                    }
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Назад"))
                            .setCallbackData("Назад"));
                    rows.add(row);
                    markup.setKeyboard(rows);
                    edit(update.getCallbackQuery().getMessage(), "Удалить категорию", markup);
                }
            }
			if(cb.equals("Опубликовать товар")) {
				List<String> l = DataBase.sqlQueryList("select id from table0","id");
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
				for (String s:l) {
                    String name = DataBase.sqlQuery("select russian from table0 where id ="+s,"russian");
                        if (!name.equals("Лого")) {
							List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        	row.add(new InlineKeyboardButton()
                                .setText(EmojiParser.parseToUnicode(name))
                                .setCallbackData("publish"+s));
							rows.add(row);
						}
                    }
					List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Назад"))
                            .setCallbackData("Назад"));
                    rows.add(row);
                    markup.setKeyboard(rows);
				
                if (!l.isEmpty()) edit(update.getCallbackQuery().getMessage(), "Опубликовать товар", markup);
                else answer.setShowAlert(false).setText("Товаров нет");
			}
			if(cb.contains("publish")) {
				String prodId = cb.substring(7);
                publish(prodId);
				answer.setShowAlert(true).setText("Опубликовано");
			}
            if(cb.contains("Указать время работы")) {
                edit(update.getCallbackQuery().getMessage(), "Выберите время начала", timeKeys("Начало"));
            }
            if(cb.contains("Начало")) {
                edit(update.getCallbackQuery().getMessage(), "Выберите время окончания", timeKeys("Конец"));
                Bot.startOfPeriod = LocalTime.parse(cb.substring(6));
                answer.setShowAlert(false).setText("Время начала: "+cb.substring(6));
            }
            if(cb.contains("Конец")) {
                edit(update.getCallbackQuery().getMessage(), "Новое время установлено", mainKeyboard(null), 1);
                Bot.endOfPeriod = LocalTime.parse(cb.substring(5));
                answer.setShowAlert(false).setText("Новое время работы: " +
                        "\nС "+Bot.startOfPeriod.toString()+" по "+Bot.endOfPeriod.toString());
            }
            if(cb.equals(mainKeyboard(null).get(0))){
                List<String> l = DataBase.productsAvailability("Russian");
                if (!l.isEmpty()) edit(update.getCallbackQuery().getMessage(), "Указать наличие", l, 3);
                else answer.setShowAlert(false).setText("Продуктов нет");
            } else if(cb.equals(mainKeyboard(null).get(1))){
                List<String> l = DataBase.showAllProducts("Russian", false);
                if (!l.isEmpty()) {
                    listener = "Delete";
                    edit(update.getCallbackQuery().getMessage(), "Удалить продукт", l, 3);
                } else answer.setShowAlert(false).setText("Продуктов нет");
            } else if(cb.equals(mainKeyboard(null).get(2))){
                edit(update.getCallbackQuery().getMessage(), "В какой раздел?", Lan.listTypes("Russian"), 2);
            } else if(cb.equals(mainKeyboard(null).get(3))){
                listener = "Change";
                edit(update.getCallbackQuery().getMessage(), "Изменить продукт", DataBase.showAllProducts("Russian", false), 3);
            } else if(cb.equals(mainKeyboard(null).get(4))){
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
                    String comment = DataBase.sqlQuery("select comment from zakaz where userid ="+userID, "comment");
                    if (comment!=null) comment= "<b>Комментарий:</b> "+comment+"\n";
                    else comment = "";
                    if (address!=null) address= "<b>Адрес:</b> "+address+"\n";
                    else address="<b>Локация получена</b>\n";
                    String text= "Имя: "+name+
                            "\nНомер: "+ number+
                            "\nВремя: "+time+
                            address+comment+
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
                    if (update.getCallbackQuery().getMessage().hasLocation()) {
                        deleteMessage(update.getCallbackQuery().getMessage());
                        send(text, id, markup);
                    } else {
                        edit(update.getCallbackQuery().getMessage(), text, markup);
                    }

                }
                if (cb.contains("Локация"+userID)) {
                    Float latitude = Float.valueOf(DataBase.sqlQuery("select latitude from users where id ="+userID,"latitude"));
                    Float longitude = Float.valueOf(DataBase.sqlQuery("select longitude from users where id ="+userID,"longitude"));
                    deleteMessage(update.getCallbackQuery().getMessage().getMessageId().toString(), id);
                    sendLocation(id, latitude, longitude, "Назад", userID);
                }
            }
            for (String t:Lan.listTypes("Russian")) {
                if (cb.equals(t)) {
                    category = Lan.listTypes("Russian").indexOf(t)+"";
//                    listener = "subcategory";
                    edit(update.getCallbackQuery().getMessage(), "Выбрана категория "+t+
                    "\nВыберите тип",  Bot.listSubTypes(Lan.listTypes("Russian").indexOf(t), "Russian"), 1);
                }
            }
            for (String tt:DataBase.sqlQueryList("select russian from types","russian")) {
                if (cb.equals(tt)) {
                    subcategory = DataBase.sqlQuery("select typeid from types where Russian = '"+tt+"'", "typeid");
                    listener = "Russian";
                    edit(update.getCallbackQuery().getMessage(),
                            "Выбрана категория "+Lan.listTypes("Russian").get(Integer.parseInt(category))+
                            "\nВыбран тип <b>"+tt+"</b>" +
                            "\nВведите название продукта на русском",  list, 1);
                }
            }

            if (cb.contains("Добавить категорию")) {
                    listener = "RussianCategory";
                    edit(update.getCallbackQuery().getMessage(), "Введите название категории на русском",  list, 1);
                }
            for (String t:DataBase.showAllProducts("Russian", false)) {
                String prodId = DataBase.sqlQuery("select id from table0 where russian = '"+t+"'", "id");
                if (cb.contains(t)) {
                    if (cb.contains(":white_check_mark:")) {
                        DataBase.sql("UPDATE table0 SET instock = false where russian = '"+t+"'");
                        edit(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getMessage().getText(), DataBase.productsAvailability("Russian"), 3);
                    } else if (cb.contains(":x:")) {
                        DataBase.sql("UPDATE table0 SET instock = true where russian = '"+t+"'");
                        edit(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getMessage().getText(), DataBase.productsAvailability("Russian"), 3);
                    } else if (listener.equals("Delete")) {
                        DataBase.sql("delete from cart where item = '"+prodId+"'");
						DataBase.sql("delete from table0 where russian = '"+t+"'");
                        edit(update.getCallbackQuery().getMessage(), "Удалить продукт", DataBase.showAllProducts("Russian", false), 3);
                        answer.setShowAlert(false).setText("Удалено!");
                    } else if (listener.equals("Change")) {
                        listener = "";
                        edit(update.getCallbackQuery().getMessage(), "Что именно изменить?", fields(prodId));
                    }
                }
                for(int i= 0; i<columnsList().size(); i++) {
                    if (cb.equals(prodId+columnsListNames().get(i))){
                        listener = prodId+columnsListNames().get(i);
                        String now = DataBase.sqlQuery("select "+columnsListNames().get(i).substring(4)+" from table0 where id ="+prodId, columnsListNames().get(i).substring(4));
                        edit(update.getCallbackQuery().getMessage(), "<b>"+columnsList().get(i)+"</b>"+
                        "\n\nТекущее значение = "+now+
                        "\n\nВведите новое значение: ", simpleMarkUp("Назад"));
                        answer.setShowAlert(false).setText("Введите новое значение");
                    }
                }
            }
                if (cb.contains("Готов")) {
                    String userid = cb.substring(5);
                    DataBase.sql("delete from zakaz where userid = "+userid);
                    edit(update.getCallbackQuery().getMessage(), "Заказ завершён", mainKeyboard(null), 1);
                }
                if (cb.equals("Отмена")||cb.equals("Ok")) {
                    deleteMessage(update.getCallbackQuery().getMessage());
                } else if (cb.equals("Назад")) {
                    if (update.getCallbackQuery().getMessage().hasLocation()) {
                        listener = "";
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







    private void publish(String prodId) throws SQLException, TelegramApiException {
        Bot bot = new Bot();
        String name = DataBase.sqlQuery("select russian from table0 where id ="+prodId, "russian")+"\n";
        String description = DataBase.sqlQuery("select russiandescription from table0 where id ="+prodId, "russiandescription")+"\n";
        String cost =  Lan.cost("russian")+DataBase.sqlQuery("SELECT cost from table0 where id = " + prodId, "cost")+Lan.currency("russian") +".\n";
        String file_id = "";
        file_id = DataBase.sqlQuery("SELECT imageid from table0 where id ="+prodId, "imageid");
        if (file_id == null) file_id = DataBase.sqlQuery("SELECT imageid from table0 where Russian = 'Лого'", "imageid");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
//        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
//        row.add(new InlineKeyboardButton()
//                .setText(EmojiParser.parseToUnicode("0"))
//                .setCallbackData(""));
//
//        rows.add(row);
        List<InlineKeyboardButton> row0 = new ArrayList<InlineKeyboardButton>();
        row0.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode(Lan.mainMenu("Russian").get(0)))
                .setUrl("https://t.me/"+bot.getBotUsername()));
        row0.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("Заказать"))
                .setCallbackData("fromChannel"+prodId));
                //.setUrl("https://t.me/"+bot.getBotUsername()+"?start=selected"+prodId));
        rows.add(row0);
//         List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
//         row1.add(new InlineKeyboardButton()
//             .setText(EmojiParser.parseToUnicode(Lan.share("Russian")))
//             .setSwitchInlineQuery(prodId));
//         rows.add(row1);
        markup.setKeyboard(rows);
        bot.toChannel(name+description+cost,markup,prodId);
    }








    private InlineKeyboardMarkup timeKeys(String cb) {
        List<String> menu = new ArrayList<String>();
        ZoneId z = ZoneId.of("Asia/Tashkent");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i<=1440; i+=30) {
            menu.add(dtf.format(LocalTime.parse("00:00").plusMinutes(i)));
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        for (int i = 0; i<menu.size(); i+=3) {
            List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
            row1.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(menu.get(i)))
                    .setCallbackData(cb+menu.get(i)));
            if((i+1)<menu.size()) row1.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(menu.get(i+1)))
                    .setCallbackData(cb+menu.get(i+1)));
            if((i+2)<menu.size()) row1.add(new InlineKeyboardButton()
                    .setText(EmojiParser.parseToUnicode(menu.get(i+2)))
                    .setCallbackData(cb+menu.get(i+2)));
            rows.add(row1);
        }
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton()
                .setText(EmojiParser.parseToUnicode("Назад"))
                .setCallbackData("Назад"));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

















	private InlineKeyboardMarkup updateMarkup() throws SQLException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                                List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
                                for(int i= 0; i<columns().size(); i++) {
                                    boolean yes = DataBase.sqlQueryBoolean("select "+columns().get(i)+ " from types where russian = '"+russian+"'", columns().get(i));
                                    String ok = "";
                                    if (yes) ok = ":white_check_mark:";
                                    else ok = ":x:";
                                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                                        row.add(new InlineKeyboardButton()
                                                .setText(EmojiParser.parseToUnicode(ok+columns().get(i)))
                                                .setCallbackData("update"+columns().get(i)));
                                        rows.add(row);
                                }
                                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                                        row.add(new InlineKeyboardButton()
                                                .setText(EmojiParser.parseToUnicode("Назад"))
                                                .setCallbackData("Назад"));
                                        rows.add(row);
                                markup.setKeyboard(rows);
        return markup;
    }

    private List<String> columnsList() {
        List<String> a = new ArrayList<>();
            a.add("Название на русском");
            a.add("Название на узбекском");
            a.add("Название на английском");
            a.add("Описание на русском");
            a.add("Описание на узбекском");
            a.add("Описание на английском");
            a.add("Цена");
            a.add("Emoji");
		return a;
    }
    private List<String> columnsListNames() {
        List<String> a = new ArrayList<>();
            a.add("EditRussian");
            a.add("EditUzbek");
            a.add("EditEnglish");
            a.add("EditRussiandescription");
            a.add("EditUzbekdescription");
            a.add("EditEnglishdescription");
            a.add("EditCost");
            a.add("EditEmoji");
		return a;
	}

	private List<String> mainKeyboard(String lastbutton) {
        List<String> a = new ArrayList<>();
            a.add("Указать наличие");
            a.add("Удалить продукт");
            a.add("Добавить продукт");
            a.add("Изменить продукт");
            a.add("Заказы");
            a.add("Добавить категорию");
            a.add("Обновить категорию");
		    a.add("Удалить категорию");
			a.add("Опубликовать товар");
            a.add("Указать время работы");
            if (lastbutton!=null) a.add(lastbutton);
        return a;
	}

    private InlineKeyboardMarkup fields(String prodID) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();
        for(int i= 0; i<columnsList().size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(columnsList().get(i)))
                        .setCallbackData(prodID+columnsListNames().get(i)));
                rows.add(row);
        }
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode("Назад"))
                        .setCallbackData("Назад"));
                rows.add(row);
            markup.setKeyboard(rows);
        return markup;
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


    public void send (String text, String chatId, InlineKeyboardMarkup inlineMarkup) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML");
        sendMessage.setReplyMarkup(inlineMarkup);
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
                newText.contains(mainKeyboard(null).get(3))||
                newText.contains("В какой раздел?")||
                newText.contains("Введите")||
                newText.contains("Время:")) {
                lastRow.add(new InlineKeyboardButton()
                            .setText(EmojiParser.parseToUnicode("Назад"))
                            .setCallbackData("Назад"));
            }
            rows.add(lastRow);
            markup.setKeyboard(rows);
            edit (message, newText, markup);
        } else {
            edit(message, newText, null);
        }
    }
    public void edit (Message message, String newText, InlineKeyboardMarkup markup) throws SQLException {
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
        String chatId = DataBase.sqlQuery("select id from users where admin = true", "id");
        if (chatId!=null&&!chatId.equals("")) {
            SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text))
                .setParseMode("HTML")
                .setReplyMarkup(simpleMarkUp("Ok"));
            return execute(sendMessage).getMessageId().toString();
        }
        else return null;
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

	public void sendLocation(String chatId, Float latitude, Float longitude, String button, String userid)
            throws SQLException {
            String time = DataBase.sqlQuery("select time from zakaz where userid = '" +userid+"' and conformed = true", "time");
            String name = DataBase.sqlQuery("select firstname from users where id = " +userid, "firstname");
        SendLocation sendMessage = new SendLocation()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setChatId(chatId);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<List<InlineKeyboardButton>>();

                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(button))
                        .setCallbackData(name+"  -  "+time));
                rows.add(row);

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
