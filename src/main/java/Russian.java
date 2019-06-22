import com.ibm.icu.text.Transliterator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Russian implements Language{
    public static final String ruseng   = "с <b>русского</b> на <b>английский</b>";
    public static final String rusuzbek = "с <b>русского</b> на <b>узбекский</b>";
    public static final String engrus   = "с <b>английского</b> на <b>русский</b>";
    public static final String enguzbek = "с <b>английского</b> на <b>узбекский</b>";
    public static final String uzbekeng = "с <b>узбекского</b> на <b>английский</b>";
    public static final String uzbekrus = "с <b>узбекского</b> на <b>русский</b>";
    Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.LATIN_TO_CYRILLIC);
    public static final String yes = "Да";
    public static final String no = "Нет";
    public Russian() {

    }

    @Override
    public String welcome(Order o) {
        return "Здравствуйте " +toLatinTrans.transliterate(o.getUser().getFirstName())+"!\n" +
                "Чем могу помочь? :blush:";
    }

    @Override
    public List<String> menu() {
        List<String> menu = new ArrayList<String>();
        menu.add("Заказать перевод");
        menu.add("Стоимость услуг");
        menu.add("Язык интерфейса");
        menu.add("Мой заказ");
        return menu;
    }

    @Override
    public String chooseDirection() {
        return "Выберете направление перевода";
    }

    @Override
    public String orders(Order o) {
        String ch = "";
        if (o.getDirection().equals(AmabiliaBot.directions().get(0))) ch = ruseng  ;
        if (o.getDirection().equals(AmabiliaBot.directions().get(1))) ch = rusuzbek;
        if (o.getDirection().equals(AmabiliaBot.directions().get(2))) ch = engrus  ;
        if (o.getDirection().equals(AmabiliaBot.directions().get(3))) ch = enguzbek;
        if (o.getDirection().equals(AmabiliaBot.directions().get(4))) ch = uzbekeng;
        if (o.getDirection().equals(AmabiliaBot.directions().get(5))) ch = uzbekrus;
        return ":u6307:Направление перевода " + ch +
                "\n"+o.getDirection()+
                "\n:page_facing_up:Количество листов: " + o.getPages()  +
                "\n:date:Дата заказа: "+AmabiliaBot.date.format(o.getOrderTime()) +
                " :clock3:"+AmabiliaBot.time.format(o.getOrderTime());
    }

    @Override
    public String emptyOrders() {
        return ":o:  Заказ не найден  :o:";
    }

    @Override
    public String cost() {
        return ":page_facing_up:1 лист (250 слов или 1800 букв)\n" +
                ":ru::point_right::gb: "+Prices.ruseng+" сум\n" +
                ":ru::point_right::uz: "+Prices.rusuzb+" сум\n" +
                ":gb::point_right::ru: "+Prices.engrus+" сум\n" +
                ":gb::point_right::uz: "+Prices.enguzb+" сум\n" +
                ":uz::point_right::gb: "+Prices.uzbeng+" сум\n" +
                ":uz::point_right::ru: "+Prices.uzbrus+" сум";

    }
    @Override
    public String confirmChoice(String choice) {
        String ch = "";
        if (choice.equals(AmabiliaBot.directions().get(0))) ch = "Вы выбрали перевод " + ruseng  +"!";
        if (choice.equals(AmabiliaBot.directions().get(1))) ch = "Вы выбрали перевод " + rusuzbek+"!";
        if (choice.equals(AmabiliaBot.directions().get(2))) ch = "Вы выбрали перевод " + engrus  +"!";
        if (choice.equals(AmabiliaBot.directions().get(3))) ch = "Вы выбрали перевод " + enguzbek+"!";
        if (choice.equals(AmabiliaBot.directions().get(4))) ch = "Вы выбрали перевод " + uzbekeng+"!";
        if (choice.equals(AmabiliaBot.directions().get(5))) ch = "Вы выбрали перевод " + uzbekrus+"!";
        return ch;
    }
    @Override
    public String howManyPages() {
        return "\nСколько листов?";
    }

    @Override
    public String preliminary(Order a) {
        a.setDuration();
        int days = a.getDuration();
        String day;
        if ((days==1||days%10==1)&&days!=11) day = " день.";
        else if (days>21&&days%10<5||days<5) day = " дня.";
        else day = " дней.";
        return "Примерная стоимость перевода "+ a.getTotalCost()+" сум." +
                "\nОкончательная стоимость будет рассчитана по количеству слов в документе." +
                "\nПеревод займет примерно "+ days + day+"\n";
    }

    @Override
    public String sendMe() {
        return "Отправьте мне документ который нужно перевести! :blush:";
    }
    @Override
    public String sendMeContact() {
        return "Отправьте мне свой номер что-бы мы могли c вами связаться :telephone_receiver:";
    }

    @Override
    public String confirmOrder() {
        return "Заказ оформлен!\n";
    }

    @Override
    public String what() {
        return "Не понял :disappointed:";
    }
    @Override
    public String received() {
        return "Документ получен! \n";
    }
    @Override
    public String doYouConfirm() {
        return "Оформить заказ?";
    }
    @Override
    public String myContact() {
        return ":telephone_receiver: Отправить свой номер :telephone_receiver:";
    }
    @Override
    public String contactReceived() {
        return "Номер получен :+1:";
    }
    public String getYes() {
        return "Да";
    }


    public String getNo() {
        return "Нет";
    }

    @Override
    public String cancelled() {
        return "Заказ отменён";
    }

    @Override
    public String orderExists() {
        return "У Вас уже имеется заказ. Вы хотите его отменить?";
    }

    @Override
    public String weWillContact() {
        return "Мы свяжемся с вами в скором времени :blush:";
    }
    @Override
    public String youSure() {
        return "Вы уверены что хотите отменить заказ?";
    }
}
