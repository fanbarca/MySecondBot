import com.ibm.icu.text.Transliterator;

import java.util.ArrayList;
import java.util.List;

public class Uzbek implements Language {
    public static final String ruseng      = "<b>rus</b> tilidan <b>ingliz</b> tiliga";
    public static final String rusuzbek    = "<b>rus</b> tilidan <b>o'zbek</b> tiliga";
    public static final String engrus      = "<b>ingliz</b> tilidan <b>rus</b> tiliga";
    public static final String enguzbek = "<b>ingliz</b> tilidan <b>o'zbek</b> tiliga";
    public static final String uzbekeng = "<b>o'zbek</b> tilidan <b>ingliz</b> tiliga";
    public static final String uzbekrus    = "<b>o'zbek</b> tilidan <b>rus</b> tiliga";
    Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.CYRILLIC_TO_LATIN);



    public Uzbek() {
    }

    @Override
    public String welcome(Order o) {
        return "Assalomu Alaykum "+toLatinTrans.transliterate(o.getUser().getFirstName())+"!\n" +
                "Mendan nima xizmat? :blush:";
    }

    @Override
    public List<String> menu() {
        List<String> menu = new ArrayList<String>();
        menu.add("Tarjimaga buyurtma berish");
        menu.add("Xizmatlar narxlari");
        menu.add("interfeys tili");
        menu.add("Mening buyurtmalarim");
        return menu;
    }

    @Override
    public String chooseDirection() {
        return "Tarjima yo'nalishini tanlang";
    }

    @Override
    public String orders(Translation o) {
        String ch = "";
        if (o.getDirection().equals(AmabiliaBot.directions().get(0))) ch = ruseng;
        if (o.getDirection().equals(AmabiliaBot.directions().get(1))) ch = rusuzbek;
        if (o.getDirection().equals(AmabiliaBot.directions().get(2))) ch = engrus;
        if (o.getDirection().equals(AmabiliaBot.directions().get(3))) ch = enguzbek;
        if (o.getDirection().equals(AmabiliaBot.directions().get(4))) ch = uzbekeng;
        if (o.getDirection().equals(AmabiliaBot.directions().get(5))) ch = uzbekrus;
        return ":u6307:Buyurtma yo'nalishi " + ch +
                "\n"+o.getDirection()+
                "\n:page_facing_up:Varaqlar soni: " + o.getPages() +
                "\n:date:Buyurtma sanasi: "+AmabiliaBot.date.format(o.getOrderTime()) +
                " :clock3:"+AmabiliaBot.time.format(o.getOrderTime())+
                "\n:1234:Buyurtma tartib raqami: " + o.getId();
    }

    @Override
    public String emptyOrders() {
        return ":o:Sizda hali birorta buyurtma yo'q";
    }

    @Override
    public String cost() {
        return ":page_facing_up: <b>1 varaq</b> (250 ta so'z yoki 1800 ta harf)\n" +
                ":ru::point_right::gb: "+Prices.ruseng+" so'm\n" +
                ":ru::point_right::uz: "+Prices.rusuzb+" so'm\n" +
                ":gb::point_right::ru: "+Prices.engrus+" so'm\n" +
                ":gb::point_right::uz: "+Prices.enguzb+" so'm\n" +
                ":uz::point_right::gb: "+Prices.uzbeng+" so'm\n" +
                ":uz::point_right::ru: "+Prices.uzbrus+" so'm";
    }

    @Override
    public String confirmChoice(String choice) {
        String ch = "";
        String b = " tarjimani tanladingiz!";
        if (choice.equals(AmabiliaBot.directions().get(0))) ch = "Siz "+ruseng  +b;
        if (choice.equals(AmabiliaBot.directions().get(1))) ch = "Siz "+rusuzbek+b;
        if (choice.equals(AmabiliaBot.directions().get(2))) ch = "Siz "+engrus  +b;
        if (choice.equals(AmabiliaBot.directions().get(3))) ch = "Siz "+enguzbek+b;
        if (choice.equals(AmabiliaBot.directions().get(4))) ch = "Siz "+uzbekeng+b;
        if (choice.equals(AmabiliaBot.directions().get(5))) ch = "Siz "+uzbekrus+b;
        return ch;
    }



    @Override
    public String sendMe() {
        return "Tarjima qilish kerak bo'lgan hujjatni menga jo'nating! :blush:";
    }
    @Override
    public String sendMeContact() {
        return "Siz bilan aloqaga chiqishimiz uchun, telefon raqamingizni jo'nating :telephone_receiver:";
    }

    @Override
    public String confirmOrder() {
        return "Buyurtma tasdiqlandi!\n";
    }

    @Override
    public String what() {
        return "Tushunmadim :disappointed:";
    }

    @Override
    public String received() {
        return "Hujjat qabul qilindi :blush:\n";
    }
    @Override
    public String doYouConfirm() {
        return "Buyurtmani tasdiqlaysizmi?";
    }

    @Override
    public String howManyPages() {
        return "\nVaraqlar soni nechta?";
    }

    @Override
    public String preliminary(Translation a) {
        a.setDuration();
        return "Tarjimaning narxi tahminan "+ a.getTotalCost()+" so'm." +
                "\nYakuniy narx hujjatdagi so'zlar soni bo'yicha aniqlanadi." +
                "\nTarjimaga tahminan "+ a.getDuration() + " kun ketadi.\n" ;
    }

    @Override
    public String cancel() {
        return ":negative_squared_cross_mark:Bekor qilmoq";
    }

    @Override
    public String myContact() {
        return ":telephone_receiver: Telefon raqamni jo'natish :telephone_receiver:";
    }

    @Override
    public String contactReceived() {
        return "Telefon raqam qabul qilindi :+1:";
    }

    public String getYes() {
        return "Ha";
    }


    public String getNo() {
        return "Yo'q";
    }

    @Override
    public String cancelled() {
        return "Buyurtma bekor qilindi";
    }

    @Override
    public String orderExists() {
        return "Sizda amaldagi buyurtma mavjud. Yangi buyurma qilmoqchimisiz?";
    }

    @Override
    public String weWillContact() {
        return "Biz tez orada aloqaga chiqamiz :blush:";
    }

    @Override
    public String youSure() {
        return "Buyurtmani bekor qilishga ishonchingiz komilmi?";
    }

}
