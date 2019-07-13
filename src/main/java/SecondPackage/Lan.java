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
                menu.add(":notebook_with_decorative_cover: Menyu");
                menu.add(":truck: Yetkazish");
                menu.add(":uz: Til");
                menu.add("🛒 Savatcha");
            }
            else if (lan.equals("Russian")) {
                menu.add(":notebook_with_decorative_cover: Меню");
                menu.add(":truck: Доставка");
                menu.add(":ru: Язык");
                menu.add("🛒 Корзина");
            }
            else if (lan.equals("English")) {
                menu.add(":notebook_with_decorative_cover: Menu");
                menu.add(":truck: Delivery");
                menu.add(":gb: Language");
                menu.add("🛒 Shopping cart");
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
    public static String emptyOrders(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":o:Buyurtmalar ro'yxati bo'sh";
        else if (lan.equals("Russian")) r = ":o:Список заказов пуст";
        else if (lan.equals("English")) r = ":o:Orders list is empty";
        return r;
    }
    public static String myOrders(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Buyurtmalar ro'yxati";
        else if (lan.equals("Russian")) r = "Список заказов";
        else if (lan.equals("English")) r = "Orders list";
        return r;
    }
    public static List<String> listTypes(String lan){
        List<String> list = new ArrayList<>();
        if (lan.equals("Uzbek")) {
            list.add(":custard: Salatlar");
            list.add(":stew: Suyuq ovqatlar");
            list.add(":spaghetti: Asosiy taomlar");
            list.add(":coffee: Ichimliklar");
            list.add(":cake: Shirinliklar");
            list.add(":hamburger: Fastfud");
            list.add(":pizza: Pitsa");
            list.add(":meat_on_bone: Shashlik");
            list.add(":bread: Boshqa");
            list.add(backToMenu(lan));
        }
        else if (lan.equals("Russian")) {
            list.add(":custard: Салаты");
            list.add(":stew: Первое");
            list.add(":spaghetti: Второе");
            list.add(":coffee: Напитки");
            list.add(":cake: Десерт");
            list.add(":hamburger: Фастфуд");
            list.add(":pizza: Пицца");
            list.add(":meat_on_bone: Шашлык");
            list.add(":bread: Другое");
            list.add(backToMenu(lan));
        }
        else if (lan.equals("English")) {
            list.add(":custard: Salads");
            list.add(":stew: Entrees");
            list.add(":spaghetti: Main courses");
            list.add(":coffee: Beverages");
            list.add(":cake: Dessert");
            list.add(":hamburger: Fast food");
            list.add(":pizza: Pizza");
            list.add(":meat_on_bone: Barbecue");
            list.add(":bread: Other");
            list.add(backToMenu(lan));
        }
        return list;
    }
    public static String goBack(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":book: Menyu";
        else if (lan.equals("Russian")) r = ":book: Меню";
        else if (lan.equals("English")) r = ":book: Menu";
        return r;
        }
        public static String backToMenu(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":iphone: Bosh sahifa";
        else if (lan.equals("Russian")) r = ":iphone: Главная";
        else if (lan.equals("English")) r = ":iphone: Home";
        return r;
        }
    public static String deliveryCost(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Yetkazib berish:\n3km gacha - 8000 so'm\nundan keyin - 1000 so'm/km ";
        else if (lan.equals("Russian")) r = "Доставка:\nдо 3 км - 8000 сум\nдалее - 1000 сум/км";
        else if (lan.equals("English")) r = "Delivery:\nup to 3 km - 8000 sum\nfarther - 1000 sum per km";
        return r;
    }
    public static String cost(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Narxi: ";
        else if (lan.equals("Russian")) r = "Стоимость: ";
        else if (lan.equals("English")) r = "Cost: ";
        return r;
    }
    public static String currency(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "so'm";
        else if (lan.equals("Russian")) r = "сум";
        else if (lan.equals("English")) r = "sum";
        return r;
    }
    public static String toCart(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Savatchaga :heavy_plus_sign:🛒";
        else if (lan.equals("Russian")) r = "В корзину :heavy_plus_sign:🛒";
        else if (lan.equals("English")) r = "Add to cart :heavy_plus_sign:🛒";
        return r;
    }
}