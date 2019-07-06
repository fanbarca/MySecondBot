package SecondPackage;

import java.util.ArrayList;
import java.util.List;
import com.ibm.icu.text.Transliterator;

class Lan {
    public static Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.CYRILLIC_TO_LATIN);
    public static Transliterator toCyrilTrans = Transliterator.getInstance(AmabiliaBot.LATIN_TO_CYRILLIC);

    public static String welcome(String lan, String name){
        String r="";
        String emoji = "\n:red_car::pizza::poultry_leg::hamburger:";
        if (lan.equals("Uzbek")) r = toLatinTrans.transliterate(name)+", \n" +
                "<b>BIG FOOD</b> tayyor ovqat yetkazib berish xizmatiga xush kelibsiz!" +
                emoji;
        else if (lan.equals("Russian")) r = toCyrilTrans.transliterate(name)+", \n" +
                "Добро пожаловать в службу доставки готовых блюд <b>BIG FOOD</b>!" +
                emoji;
        else if (lan.equals("English")) r = toLatinTrans.transliterate(name)+", \n" +
                "Welcome to ready meal delivery service <b>BIG FOOD</b>!"+
                emoji;
        return r;
    }
    public static List<String> mainMenu(String lan){
        List<String> menu = new ArrayList<String>();
            if (lan.equals("Uzbek")) {
                menu.add("Menyu");
                menu.add("Xizmatlar narxlari");
                menu.add("interfeys tili");
                menu.add("Mening buyurtmalarim");
            }
            else if (lan.equals("Russian")) {
                menu.add("Меню");
                menu.add("Стоимость услуг");
                menu.add("Язык интерфейса");
                menu.add("Мои заказы");
            }
            else if (lan.equals("English")) {
                menu.add("Menu");
                menu.add("Cost of services");
                menu.add("Interface language");
                menu.add("My orders");
            }
        return menu;
        }
    public static String sendMeContact(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = "Siz bilan aloqaga chiqishimiz uchun, telefon raqamingizni jo'nating :telephone_receiver:";
        else if (lan.equals("Russian")) r = "Отправьте мне свой номер телефона что-бы мы могли c вами связаться :telephone_receiver:";
        else if (lan.equals("English")) r = "Send me your phone number, so we can contact you :telephone_receiver:";
        return r;
    }
    public static String myContact(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = "Telefon raqamni jo'natish :telephone_receiver:";
        else if (lan.equals("Russian")) r = "Отправить свой номер :telephone_receiver:";
        else if (lan.equals("English")) r = "Send my number :telephone_receiver:";
        return r;
    }
    public static String chooseDish(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = "Nima buyurtma berishni istaysiz? ";
        else if (lan.equals("Russian")) r = "Что желаете заказать?";
        else if (lan.equals("English")) r = "What would you like to order?";
        return r;
    }
}