package SecondPackage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.text.Transliterator;

class Lan {
    public static Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.CYRILLIC_TO_LATIN);
    public static Transliterator toCyrilTrans = Transliterator.getInstance(AmabiliaBot.LATIN_TO_CYRILLIC);


    public static String welcome(String lan, String name){
        String r="";
        String emoji = "\n:red_car::pizza::poultry_leg::hamburger:";
        if (lan.equals("Uzbek")) r = toLatinTrans.transliterate(name)+", \n" +
                "tayyor ovqat yetkazib berish xizmatiga xush kelibsiz!" +
                emoji;
        else if (lan.equals("Russian")) r = toCyrilTrans.transliterate(name)+", \n" +
                "Добро пожаловать в службу доставки готовых блюд!" +
                emoji;
        else if (lan.equals("English")) r = toLatinTrans.transliterate(name)+", \n" +
                "Welcome to ready meal delivery service!"+
                emoji;
        return r;
    }
    public static List<String> mainMenu(String lan){
        List<String> menu = new ArrayList<String>();
            if (lan.equals("Uzbek")) {
                menu.add(":notebook_with_decorative_cover: Menyu");
                menu.add(":truck: Buyurtma");
                menu.add(":uz: Til");
                menu.add("🛒 Savatcha");
            }
            else if (lan.equals("Russian")) {
                menu.add(":notebook_with_decorative_cover: Меню");
                menu.add(":truck: Мой заказ");
                menu.add(":ru: Язык");
                menu.add("🛒 Корзина");
            }
            else if (lan.equals("English")) {
                menu.add(":notebook_with_decorative_cover: Menu");
                menu.add(":truck: My order");
                menu.add(":gb: Language");
                menu.add("🛒 Shopping cart");
            }
        return menu;
        }

    public static String delivery(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = ":truck: Buyurtma berish";
        else if (lan.equals("Russian")) r = ":truck: Оформить заказ";
        else if (lan.equals("English")) r = ":truck: Place an order";
        return r;
    }
    public static String alreadyHaveLocation(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = "Oldin yuborilgan geolokatsiyani tanlaysizmi?";
        else if (lan.equals("Russian")) r = "Ранее была отправленную геолакацию?";
        else if (lan.equals("English")) r = "Would you like to use previously sent location?";
        return r;
    }
    public static String tooLate(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = "\n<b>Yetkazish xizmati 9:00 dan 19:00 gacha ishlaydi</b>";
        else if (lan.equals("Russian")) r = "\n<b>Доставка работает с 9:00 до 19:00</b>";
        else if (lan.equals("English")) r = "\n<b>Delivery service hours are from 9:00 to 19:00</b>";
        return r;
    }
    public static String sendMeContact(String lan){
        String r="";
        if (lan.equals("Uzbek")) r = "Siz bilan aloqaga chiqishimiz uchun, telefon raqamingizni jo'nating,\nbuning uchun, \""+
        myContact(lan)+"\" tugmaisini bosing, yoki raqamingizni +998xxxxxxx ko'rinishda jo'nating";
        else if (lan.equals("Russian")) r = "Отправьте мне свой номер телефона что-бы мы могли c вами связаться,\nдля этого, нажмите на кнопку \""+
        myContact(lan)+"\", или напишите свой номер в формате +998xxxxxxx";
        else if (lan.equals("English")) r = "Send me your phone number, so we can contact you,\nfor this, press the \""+
        myContact(lan)+"\" button, or text me your number as +998xxxxxxx";
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
        if (lan.equals("Uzbek")) r = ":o: Hali hech narsa yo'q";
        else if (lan.equals("Russian")) r = ":o: Тут пока ничего нет";
        else if (lan.equals("English")) r = ":o: There's nothing yet";
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
            list.add(emogisList().get(0)+"Salatlar");
            list.add(emogisList().get(1)+"Suyuq ovqatlar");
            list.add(emogisList().get(2)+"Asosiy taomlar");
            list.add(emogisList().get(3)+"Ichimliklar");
            list.add(emogisList().get(4)+"Shirinliklar");
            list.add(emogisList().get(5)+"Fastfud");
            list.add(emogisList().get(6)+"Pitsa");
            list.add(emogisList().get(7)+"Shashlik");
            list.add(emogisList().get(8)+"Boshqa");
        }
        else if (lan.equals("Russian")) {
            list.add(emogisList().get(0)+"Салаты");
            list.add(emogisList().get(1)+"Первое");
            list.add(emogisList().get(2)+"Второе");
            list.add(emogisList().get(3)+"Напитки");
            list.add(emogisList().get(4)+"Десерт");
            list.add(emogisList().get(5)+"Фастфуд");
            list.add(emogisList().get(6)+"Пицца");
            list.add(emogisList().get(7)+"Шашлык");
            list.add(emogisList().get(8)+"Другое");
        }
        else if (lan.equals("English")) {
            list.add(emogisList().get(0)+"Salads");
            list.add(emogisList().get(1)+"Entrees");
            list.add(emogisList().get(2)+"Main courses");
            list.add(emogisList().get(3)+"Beverages");
            list.add(emogisList().get(4)+"Dessert");
            list.add(emogisList().get(5)+"Fast food");
            list.add(emogisList().get(6)+"Pizza");
            list.add(emogisList().get(7)+"Barbecue");
            list.add(emogisList().get(8)+"Other");
        }
        return list;
    }
    public static List<String> emogisList(){
        List<String> list = new ArrayList<>();
                list.add(":custard:");
                list.add(":stew:");
                list.add(":spaghetti:");
                list.add(":coffee:");
                list.add(":cake:");
                list.add(":hamburger:");
                list.add(":pizza:");
                list.add(":meat_on_bone:");
                list.add(":bread:");
        return list;
    }
    public static String goBack(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":book: Menyuga qaytish";
        else if (lan.equals("Russian")) r = ":book: Назад в меню";
        else if (lan.equals("English")) r = ":book: Back to menu";
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
        if (lan.equals("Uzbek")) r = "\nYetkazish narxi:\n3km gacha - 8000 so'm\nundan keyin - 1000 so'm/km ";
        else if (lan.equals("Russian")) r = "\nСтоимость доставки:\nдо 3 км - 8000 сум\nдалее - 1000 сум/км";
        else if (lan.equals("English")) r = "\nDelivery cost:\nup to 3 km - 8000 sum\nfarther - 1000 sum per km";
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
        if (lan.equals("Uzbek")) r = " so'm";
        else if (lan.equals("Russian")) r = " сум";
        else if (lan.equals("English")) r = " sum";
        return r;
    }
    public static List<String> keyBoard(String lan){
        List<String> list = new ArrayList<>();
            list.add(goBack(lan));
            list.add(backToMenu(lan));
        return list;
    }
    public static String clearCart(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":x: Bo'shatish";
        else if (lan.equals("Russian")) r = ":x: Очистить";
        else if (lan.equals("English")) r = ":x: Empty cart";
        return r;
    }
    public static String clearOrders(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":x: Bekor qilish";
        else if (lan.equals("Russian")) r = ":x: Отменить заказ";
        else if (lan.equals("English")) r = ":x: Cancel order";
        return r;
    }
    public static String orderCancelled(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Buyurtma bekor qilindi";
        else if (lan.equals("Russian")) r = "Заказ отменён";
        else if (lan.equals("English")) r = "Order cancelled";
        return r;
    }
    public static String cartCleared(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Savatcha bo'shatildi";
        else if (lan.equals("Russian")) r = "Корзина очищена";
        else if (lan.equals("English")) r = "Cart is emptied";
        return r;
    }
//    public static String orderComplete(String lan) {
//        String r="";
//        if (lan.equals("Uzbek")) r = "";
//        else if (lan.equals("Russian")) r = ":x: Отменить заказ";
//        else if (lan.equals("English")) r = ":x: Cancel order";
//        return r;
//    }
    public static String total(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Umumiy: ";
        else if (lan.equals("Russian")) r = "Всего: ";
        else if (lan.equals("English")) r = "Total: ";
        return r;
    }
    public static String addToCart(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":heavy_plus_sign: Savatchaga qo'shish";
        else if (lan.equals("Russian")) r = ":heavy_plus_sign: Добавить в корзину";
        else if (lan.equals("English")) r = ":heavy_plus_sign: Add to cart";
        return r;
    }
        public static String addMore(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":heavy_plus_sign: Yana qo'shish";
        else if (lan.equals("Russian")) r = ":heavy_plus_sign: Добавить ещё";
        else if (lan.equals("English")) r = ":heavy_plus_sign: Add more";
        return r;
    }
    public static String removeFromCart(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":heavy_minus_sign: Savatchadan olib tashlash";
        else if (lan.equals("Russian")) r = ":heavy_minus_sign: Убрать из корзины";
        else if (lan.equals("English")) r = ":heavy_minus_sign: Remove from cart";
        return r;
    }
    public static String removeSelectively(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":heavy_minus_sign: Tanlab olib tashlash";
        else if (lan.equals("Russian")) r = ":heavy_minus_sign: Убрать выборочно";
        else if (lan.equals("English")) r = ":heavy_minus_sign: Remove one by one";
        return r;
    }
    public static String inCart(String lan, int items) {
        String r="";
        if (lan.equals("Uzbek")) r = "Savatchada : " + items + "ta";
        else if (lan.equals("Russian")) r = "В корзине: "+ items + "шт." ;
        else if (lan.equals("English")) r = "In your cart: "+items;
        return r;
    }
    public static String useOldLocation(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Oldin jo'natilgan geolokatsiyani tanlaysizmi?";
        else if (lan.equals("Russian")) r = "Хотите использовать ранее отправленную геолокацию?";
        else if (lan.equals("English")) r = "Would you like to use a previously sent location?";
        return r;
    }
    public static String useOldAddress(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Oldin jo'natilgan manzilni tanlaysizmi?";
        else if (lan.equals("Russian")) r = "Хотите использовать ранее отправленный адрес?";
        else if (lan.equals("English")) r = "Would you like to use a previously sent address?";
        return r;
    }
    public static String address(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Manzil: ";
        else if (lan.equals("Russian")) r = "Адрес: ";
        else if (lan.equals("English")) r = "Address: ";
        return r;
    }
    public static String sendMeLocation(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Buyurtmani qayerga yetkazish kerak?\nJoyni lokatsiyasini yuboring, yoki manzilni yozing";
        else if (lan.equals("Russian")) r = "Куда надо доставить заказ?\nОтправьте локацию, или напишите адрес";
        else if (lan.equals("English")) r = "Where would you like your order to be delivered?\nSend your location or type the address";
        return r;
    }
    public static String myLocation(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Lokatsiyasini yuborish";
        else if (lan.equals("Russian")) r = "Отправить локацию";
        else if (lan.equals("English")) r = "Send location";
        return r;
    }
    public static String orderPlaced(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Buyurtma tasdiqlandi!";
        else if (lan.equals("Russian")) r = "Заказ оформлен!";
        else if (lan.equals("English")) r = "Order has been placed!";
        return r;
    }
    public static String orderTime(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Buyurtmani yetkazish vaqtini tanlang";
        else if (lan.equals("Russian")) r = "Выберите время доставки";
        else if (lan.equals("English")) r = "Choose delivery time";
        return r;
    }
    public static String deliveryTime(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Buyurtmani yetkazish vaqti: ";
        else if (lan.equals("Russian")) r = "Время доставки: ";
        else if (lan.equals("English")) r = "Delivery time: ";
        return r;
    }
    public static String locationReceived(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "<b>Lokatsiya qabul qilingan</b> \n";
        else if (lan.equals("Russian")) r = "<b>Геолокация получена</b> \n";
        else if (lan.equals("English")) r = "<b>Location received</b> \n";
        return r;
    }
    public static String added(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Qo'shildi";
        else if (lan.equals("Russian")) r = "Добавлено";
        else if (lan.equals("English")) r = "Added";
        return r;
    }
    public static String removed(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Savatchadan olib tashlandi";
        else if (lan.equals("Russian")) r = "Убрано с корзины";
        else if (lan.equals("English")) r = "Removed from cart";
        return r;
    }
    public static String orderExists(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Sizda buyurtma mavjud. Uni bekor qilasizmi?";
        else if (lan.equals("Russian")) r = "У вас уже есть заказ. Хотите его отменить?";
        else if (lan.equals("English")) r = "You've already placed your order. Would you like to cancel it?";
        return r;
    }
    public static List<String> YesNo(String lan){
        List<String> menu = new ArrayList<String>();
        if (lan.equals("Uzbek")) {
            menu.add("Ha");
            menu.add("Yo'q");
        }
        else if (lan.equals("Russian")) {
            menu.add("Да");
            menu.add("Нет");
        }
        else if (lan.equals("English")) {
            menu.add("Yes");
            menu.add("No");
        }
        return menu;
    }
}