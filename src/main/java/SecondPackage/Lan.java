package SecondPackage;

import java.util.ArrayList;
import java.util.List;
import com.ibm.icu.text.Transliterator;

class Lan {
    public static Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.CYRILLIC_TO_LATIN);
    public static Transliterator toCyrilTrans = Transliterator.getInstance(AmabiliaBot.LATIN_TO_CYRILLIC);

    public static String welcome(String lan, String name){
        String r="";
        if (lan.equals("Uzbek")) r = "Xush kelibsiz, "+toLatinTrans.transliterate(name)+"!\n" +
                "<b>BIG FOOD</b> tayyor ovqat yetkazib berish xizmati :red_car::pizza::poultry_leg::hamburger:";
        else if (lan.equals("Russian")) r = "Добро пожаловать, " +toCyrilTrans.transliterate(name)+"!\n" +
                "Служба доставки готовых блюд <b>BIG FOOD</b> :red_car::pizza::poultry_leg::hamburger:";
        else if (lan.equals("English")) r = "Welcome, " +toLatinTrans.transliterate(name)+"!\n" +
                "Ready meal delivery service <b>BIG FOOD</b> :red_car::pizza::poultry_leg::hamburger:";
        return r;
    }
    public static List<String> menu(String lan){
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
    public static String chooseDirection(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = "Tarjima yo'nalishini tanlang";
        else if (lan.equals("Russian")) r = "Выберете направление перевода";
        else if (lan.equals("English")) r = "Choose direction of translation";
        return r;
    }

}