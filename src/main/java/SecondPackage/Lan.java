package SecondPackage;

import java.util.ArrayList;
import java.util.List;
import com.ibm.icu.text.Transliterator;

class Lan {
    public static Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.CYRILLIC_TO_LATIN);
    public static Transliterator toCyrilTrans = Transliterator.getInstance(AmabiliaBot.LATIN_TO_CYRILLIC);

    public static String welcome(String lan, String name){
        String r="";
        if (lan.equals("Uzbek")) r = "Assalomu Alaykum "+toLatinTrans.transliterate(name)+"!\n" +
                "Mendan nima xizmat? :blush:";
        else if (lan.equals("Russian")) r = "Здравствуйте " +toCyrilTrans.transliterate(name)+"!\n" +
                "Чем могу помочь? :blush:";
        else if (lan.equals("English")) r = "Hello " +toLatinTrans.transliterate(name)+"!\n" +
                "How can I help you? :blush:";
        return r;
    }
    public static List<String> menu(String lan){
        List<String> menu = new ArrayList<String>();
            if (lan.equals("Uzbek")) {
                menu.add("Tarjimaga buyurtma berish");
                menu.add("Xizmatlar narxlari");
                menu.add("interfeys tili");
                menu.add("Mening buyurtmalarim");
            }
            else if (lan.equals("Russian")) {
                menu.add("Заказать перевод");
                menu.add("Стоимость услуг");
                menu.add("Язык интерфейса");
                menu.add("Мои заказы");
            }
            else if (lan.equals("English")) {
                menu.add("Order translation");
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