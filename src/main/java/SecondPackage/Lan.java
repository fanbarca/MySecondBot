package SecondPackage;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.Transliterator;

class Lan {
    static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    static final String LATIN_TO_CYRILLIC = "Latin-Cyrillic";
    public static Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
    public static Transliterator toCyrilTrans = Transliterator.getInstance(LATIN_TO_CYRILLIC);


    public static String welcome(String lan, String name){
        String r="";
        String emoji = "";
        if (lan.equals("Uzbek")) r = "Xush kelibsiz, "+toLatinTrans.transliterate(name)+"!\n"+ emoji;
        else if (lan.equals("Russian")) r = "Добро пожаловать, " + toCyrilTrans.transliterate(name)+"!\n" + emoji;
        else if (lan.equals("English")) r = "Welcome, "+toLatinTrans.transliterate(name)+"!\n" + emoji;
        return r;
    }
    public static String welcome(String lan){
        String r="";
        String emoji = "";
        if (lan.equals("Uzbek")) r = "Xush kelibsiz!" +
                emoji;
        else if (lan.equals("Russian")) r = "Добро пожаловать!" +
                emoji;
        else if (lan.equals("English")) r = "Welcome!"+
                emoji;
        return r;
    }
    public static List<String> mainMenu(String lan){
        List<String> menu = new ArrayList<String>();
            if (lan.equals("Uzbek")) {
                menu.add(":notebook_with_decorative_cover: Katalog");
                menu.add("📦 Buyurtma");
                menu.add(":uz: Til");
                menu.add("🛒 Savatcha");
            }
            else if (lan.equals("Russian")) {
                menu.add(":notebook_with_decorative_cover: Каталог");
                menu.add("📦 Мой заказ");
                menu.add(":ru: Язык");
                menu.add("🛒 Корзина");
            }
            else if (lan.equals("English")) {
                menu.add(":notebook_with_decorative_cover: Catalog");
                menu.add("📦 My order");
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
        if (lan.equals("Uzbek")) r = "Ishlash vaqti "+Bot.startOfPeriod+" dan "+Bot.endOfPeriod+" gacha";
        else if (lan.equals("Russian")) r = "Мы работаем с "+Bot.startOfPeriod+" до "+Bot.endOfPeriod;
        else if (lan.equals("English")) r = "Service hours are from "+Bot.startOfPeriod+" to "+Bot.endOfPeriod;
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
        if (lan.equals("Uzbek")) r = "Bo'limni tanlang";
        else if (lan.equals("Russian")) r = "Выберите раздел";
        else if (lan.equals("English")) r = "Select a section";
        return r;
    }
    public static String emptyOrders(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Hali hech narsa yo'q";
        else if (lan.equals("Russian")) r = "Тут пока ничего нет";
        else if (lan.equals("English")) r = "There's nothing yet";
        return r;
    }
    public static String bookAppointment(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "📖 O'lchovlarni olish uchun yozilmoq";
        else if (lan.equals("Russian")) r = "📖 Записаться на снятие мерок";
        else if (lan.equals("English")) r = "📖 Make an appointment for measurements";
        return r;
    }
    public static String cartIsEmpty(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Savatcha bo'sh";
        else if (lan.equals("Russian")) r = "Корзина пуста";
        else if (lan.equals("English")) r = "Cart is empty";
        return r;
    }
    public static String whatSize(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "О'lchamni tanlang";
        else if (lan.equals("Russian")) r = "Выберите размер";
        else if (lan.equals("English")) r = "Choose a size";
        return r;
    }
    public static String chooseDate(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":date: Kunni tanlang";
        else if (lan.equals("Russian")) r = ":date: Выберите день";
        else if (lan.equals("English")) r = ":date: Choose a day";
        return r;
    }
    public static String noOrderYet(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Hali buyurtma qilinmagan";
        else if (lan.equals("Russian")) r = "Ещё нет заказа";
        else if (lan.equals("English")) r = "There's no order yet";
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
        List<String> emogisList = new ArrayList<>();
                emogisList.add(":dress:");
                emogisList.add(":necktie:");
                emogisList.add(":high_heel:");
                emogisList.add(":mans_shoe:");
                emogisList.add(":purse:");
                emogisList.add(":briefcase:");
                emogisList.add(":lipstick:");
                emogisList.add(":baby_bottle:");
                emogisList.add(":ribbon:");
                emogisList.add(":tshirt:");
                emogisList.add(":eight_pointed_black_star:");

        List<String> list = new ArrayList<>();
        if (lan.equals("Uzbek")) {
            list.add(emogisList.get(0)+"Ayollar kiyimi");
            list.add(emogisList.get(1)+"Erkaklar kiyimi");
            list.add(emogisList.get(2)+"Ayollar poyabzali");
            list.add(emogisList.get(3)+"Erkaklar poyabzali");
            list.add(emogisList.get(4)+"Ayollar aksessuarlari");
            list.add(emogisList.get(5)+"Erkaklar aksessuarlari");
            list.add(emogisList.get(6)+"Kosmetika");
            list.add(emogisList.get(7)+"Go'daklar uchun");
            list.add(emogisList.get(8)+"Qizlar uchun");
            list.add(emogisList.get(9)+"O'g'illar uchun");
            list.add(emogisList.get(10)+"Pardalar");
        }
        else if (lan.equals("Russian")) {
            list.add(emogisList.get(0)+"Женская одежда");
            list.add(emogisList.get(1)+"Мужская одежда");
            list.add(emogisList.get(2)+"Женская обувь");
            list.add(emogisList.get(3)+"Мужская обувь");
            list.add(emogisList.get(4)+"Женские аксессуары");
            list.add(emogisList.get(5)+"Мужские аксессуары");
            list.add(emogisList.get(6)+"Косметика");
            list.add(emogisList.get(7)+"Для малышей");
            list.add(emogisList.get(8)+"Для девочек");
            list.add(emogisList.get(9)+"Для мальчиков");
            list.add(emogisList.get(10)+"Шторы");
        }
        else if (lan.equals("English")) {
            list.add(emogisList.get(0)+"Women's clothes");
            list.add(emogisList.get(1)+"Men's clothes");
            list.add(emogisList.get(2)+"Women's shoes");
            list.add(emogisList.get(3)+"Men's shoes");
            list.add(emogisList.get(4)+"Women's accessories");
            list.add(emogisList.get(5)+"Men's accessories");
            list.add(emogisList.get(6)+"Cosmetics");
            list.add(emogisList.get(7)+"For toddlers");
            list.add(emogisList.get(8)+"For girls");
            list.add(emogisList.get(9)+"For boys");
            list.add(emogisList.get(10)+"Curtains");
        }
        return list;
    }




    public static String goBack(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":book: Katalogga qaytish";
        else if (lan.equals("Russian")) r = ":book: Назад в каталог";
        else if (lan.equals("English")) r = ":book: Back to catalog";
        return r;
        }
    public static String back(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":back: Orqaga";
        else if (lan.equals("Russian")) r = ":back: Назад";
        else if (lan.equals("English")) r = ":back: Back";
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
        else if (lan.equals("English")) r = "\nDelivery cost:\nup to 3 km - 8000 sum\nafter that - 1000 sum per km";
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
    public static List<String> select(String lan){
        List<String> list = new ArrayList<>();
        list.add(goBack(lan));     
		list.add(addToCart(lan));
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
    public static String cancelComment(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":x: Izoh shart emas";
        else if (lan.equals("Russian")) r = ":x: Отменить ввод комментария";
        else if (lan.equals("English")) r = ":x: Cancel commenting";
        return r;
    }
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
        if (lan.equals("Uzbek")) r = "O'lchamlarni olish uchun, qayerga borish kerak?\nLokatsiyani yuboring, yoki manzilni yozing";
        else if (lan.equals("Russian")) r = "Куда подъехать, что-бы снять мерки?\nОтправьте локацию, или напишите адрес";
        else if (lan.equals("English")) r = "We need to take measurements.\nSend your location or type the address";
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
        if (lan.equals("Uzbek")) r = "Vaqtni tanlang";
        else if (lan.equals("Russian")) r = "Выберите время";
        else if (lan.equals("English")) r = "Choose time";
        return r;
    }
    public static String deliveryTime(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":date: O'lchovlarni olish vaqti va sanasi: ";
        else if (lan.equals("Russian")) r = ":date: Дата и время снятия мерок: ";
        else if (lan.equals("English")) r = ":date: Time & Date of taking measurements: ";
        return r;
    }
    public static String locationReceived(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "<b>Lokatsiya qabul qilingan</b>";
        else if (lan.equals("Russian")) r = "<b>Геолокация получена</b>";
        else if (lan.equals("English")) r = "<b>Location received</b>";
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

    public static String addComment(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":memo: Izoh qo'shish";
        else if (lan.equals("Russian")) r = ":memo: Добавить комментарий";
        else if (lan.equals("English")) r = ":memo: Add comment";
        return r;
    }

    public static String enterComment(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Izohni yozib, yuboring";
        else if (lan.equals("Russian")) r = "Введите комментарий";
        else if (lan.equals("English")) r = "Text me your comment";
        return r;
    }

    public static String deleteComment(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = ":x: Izohni o'chirish";
        else if (lan.equals("Russian")) r = ":x: Удалить комментарий";
        else if (lan.equals("English")) r = ":x: Delete comment";
        return r;
    }

    public static String commentCancelled(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Izoh berish bekor qilindi";
        else if (lan.equals("Russian")) r = "Комментарий отменён";
        else if (lan.equals("English")) r = "Comment cancelled";
        return r;
    }

    public static String commentDeleted(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Izoh o'chirildi";
        else if (lan.equals("Russian")) r = "Комментарий удалён";
        else if (lan.equals("English")) r = "Comment deleted";
        return r;
    }

    public static String share(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "📤 Baham ko'rmoq";
        else if (lan.equals("Russian")) r = "📤 Поделиться";
        else if (lan.equals("English")) r = "📤 Share";
        return r;
    }

    public static String previousLocation(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Oldin yuborilgan geolokatsiya";
        else if (lan.equals("Russian")) r = "Ранее отправленная геолокация";
        else if (lan.equals("English")) r = "Previously sent location";
        return r;
    }
    public static String previousAddress(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Oldin yuborilgan manzil";
        else if (lan.equals("Russian")) r = "Ранее отправленный адрес";
        else if (lan.equals("English")) r = "Previously sent address";
        return r;
    }

    public static String needCatalog(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Katalogda ro'yxatdan o'tishingiz kerak";
        else if (lan.equals("Russian")) r = "Вам надо зарегистрироваться в Каталоге";
        else if (lan.equals("English")) r = "You need to sign up in the Catalog";
        return r;
    }

    public static String pressCatalog(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "Endi Katalog tugmasini bosing";
        else if (lan.equals("Russian")) r = "Теперь нажмите на кнопку Каталог";
        else if (lan.equals("English")) r = "Now press the Catalog button";
        return r;
    }
    public static String seeAll(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r = "🔍 Barcha modellar";
        else if (lan.equals("Russian")) r = "🔍 Все модели";
        else if (lan.equals("English")) r = "🔍 Browse";
        return r;
    }
    public static String links(String lan, String botName, String channelName) {
        String r="";
        if (lan.equals("Uzbek")) r = "<a href=\"t.me/"+botName+"\">🤖 Bizning Bot</a> <a href=\"t.me/"+channelName+"\">📺 Bizning Kanal</a>";
        else if (lan.equals("Russian")) r = "<a href=\"t.me/"+botName+"\">🤖 Наш Бот</a> <a href=\"t.me/"+channelName+"\">📺 Наш Канал</a>";
        else if (lan.equals("English")) r = "<a href=\"t.me/"+botName+"\">🤖 Our Bot</a> <a href=\"t.me/"+channelName+"\">📺 Our Channel</a>";
        return r;
    }
    public static String error(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r += "Xatolik yuz berdi";
        else if (lan.equals("Russian")) r += "Что-то пошло не так";
        else if (lan.equals("English")) r += "Something went wrong";
        return r;
    }
    public static String ourChannel(String lan) {
        String r="";
        if (lan.equals("Uzbek")) r += "📺 Bizning kanal";
        else if (lan.equals("Russian")) r += "📺 Наш канал";
        else if (lan.equals("English")) r += "📺 Our channel";
        return r;
    }
}
