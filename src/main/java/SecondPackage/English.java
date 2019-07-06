package SecondPackage;

import com.ibm.icu.text.Transliterator;

import java.util.ArrayList;
import java.util.List;

public class English implements Language {
    private static final String ruseng   = "from <b>Russian</b> into <b>English</b>";
    private static final String rusuzbek = "from <b>Russian</b> into <b>Uzbek</b>";
    private static final String engrus   = "from <b>English</b> into <b>Russian</b>";
    private static final String enguzbek = "from <b>English</b> into <b>Uzbek</b>";
    private static final String uzbekeng = "from <b>Uzbek</b> into <b>English</b>";
    private static final String uzbekrus = "from <b>Uzbek</b> into <b>Russian</b>";
    Transliterator toLatinTrans = Transliterator.getInstance(AmabiliaBot.CYRILLIC_TO_LATIN);
    public static final String yes = "Yes";
    public static final String no = "No";
    English() {

    }

    @Override
    public String welcome(Order o) {
        return "Hello " +toLatinTrans.transliterate(o.getUser().getFirstName())+"!\n" +
                "How can I help you? :blush:";
    }

    @Override
    public List<String> menu() {
        List<String> menu = new ArrayList<String>();
        menu.add("Order translation");
        menu.add("Cost of services");
        menu.add("Interface language");
        menu.add("My orders");
        return menu;
    }

    @Override
    public String chooseDirection() {
        return "Choose direction of translation";
    }

    @Override
    public String orders(Translation o) {
        String ch = "";
        String f="";
        if (o.Isfinished()) f = finished();
        if (o.getDirection().equals(AmabiliaBot.directions().get(0))) ch = ruseng  ;
        if (o.getDirection().equals(AmabiliaBot.directions().get(1))) ch = rusuzbek;
        if (o.getDirection().equals(AmabiliaBot.directions().get(2))) ch = engrus  ;
        if (o.getDirection().equals(AmabiliaBot.directions().get(3))) ch = enguzbek;
        if (o.getDirection().equals(AmabiliaBot.directions().get(4))) ch = uzbekeng;
        if (o.getDirection().equals(AmabiliaBot.directions().get(5))) ch = uzbekrus;
        return ":u6307:Direction of translation is " +ch+
                "\n"+o.getDirection()+
                "\n:page_facing_up:Number of pages: " + o.getPages()  +
                "\n:date:Date of order: "+AmabiliaBot.date.format(o.getOrderTime()) +
                "\n:clock3:"+AmabiliaBot.time.format(o.getOrderTime())+
                "\n:1234:Order ID: " + o.getId()+
                "\n"+ f;
    }

    @Override
    public String emptyOrders() {
        return ":o:Orders list is empty";
    }

    @Override
    public String cost() {
        return
        ":page_facing_up: <b>1 page</b>\n" +
                "(250 words or 1800 characters)\n" +
                ":ru::point_right::gb: "+Prices.ruseng+" sum\n" +
                ":ru::point_right::uz: "+Prices.rusuzb+" sum\n" +
                ":gb::point_right::ru: "+Prices.engrus+" sum\n" +
                ":gb::point_right::uz: "+Prices.enguzb+" sum\n" +
                ":uz::point_right::gb: "+Prices.uzbeng+" sum\n" +
                ":uz::point_right::ru: "+Prices.uzbrus+" sum";
    }
    @Override
    public String confirmChoice(String choice) {
        String a = "You have chosen a translation ";
        String ch = "";
        if (choice.equals(AmabiliaBot.directions().get(0))) ch = a + ruseng  ;
        if (choice.equals(AmabiliaBot.directions().get(1))) ch = a + rusuzbek;
        if (choice.equals(AmabiliaBot.directions().get(2))) ch = a + engrus  ;
        if (choice.equals(AmabiliaBot.directions().get(3))) ch = a + enguzbek;
        if (choice.equals(AmabiliaBot.directions().get(4))) ch = a + uzbekeng;
        if (choice.equals(AmabiliaBot.directions().get(5))) ch = a + uzbekrus;
        return ch;
    }
    @Override
    public String howManyPages() {
        return "\nHow many pages?";
    }

    @Override
    public String preliminary(Translation a) {
        a.setDuration();
        int d = a.getDuration();
        String days;
        if (d==1) days = " day.\n";
        else days =" days.\n";
        return "Approximate cost of translation is "+ a.getTotalCost()+" sum" +
                "\nFinal cost will be determined based on the quantity of words in the document."+
                "\nTranslation may take up to "+ d + days;
    }

    @Override
    public String cancel() {
        return ":negative_squared_cross_mark:Cancel order";
    }

    @Override
    public String finished() {
        return ":white_check_mark:Order competed";
    }

    @Override
    public String whatVoice() {
        return "You've got a beautiful voice! :blush:";
    }

    @Override
    public String whatVideonote() {
        return "I wish I could send you circle video :blush:";
    }

    @Override
    public String whatVideo() {
        return "What kind of video is that? :blush:";
    }

    @Override
    public String whatPhoto() {
        return "What photo did you just sent? :blush:" ;
    }

    @Override
    public String whatLocation() {
        return "Are you lost? :blush:";
    }

    @Override
    public String whatSticker() {
        return "Nice sticker! :blush:";
    }

    @Override
    public String whatAnimation() {
        return "That's funny :blush:";
    }

    @Override
    public String whatAudio() {
        return "I can't hear anything! :blush:";
    }

    @Override
    public String sendMe() {
        return "Send me the document that needs to be translated!\n" +
                "(*.txt, *.rtf, *.doc, *.docx, *.pdf, ...)";
    }
    @Override
    public String sendMeContact() {
        return "";
    }

    @Override
    public String confirmOrder() {
        return "Order is processed!\n";
    }

    @Override
    public String what() {
        return "I don't think I understand what you mean :disappointed:";
    }
    @Override
    public String received() {
        return "Document received :blush:\n";
    }
    @Override
    public String doYouConfirm() {
        return "Would you like to place the order?";
    }

    @Override
    public String myContact() {
        return ":telephone_receiver: ";
    }
    @Override
    public String contactReceived() {
        return "Number received :+1:";
    }
    public String getYes() {
        return "Yes";
    }


    public String getNo() {
        return "No";
    }

    @Override
    public String cancelled() {
        return "Order cancelled";
    }

    @Override
    public String orderExists() {
        return "You have already submitted an order. Would you like to place another one?";
    }

    @Override
    public String weWillContact() {
        return "We will contact you shortly :blush:";
    }
    @Override
    public String youSure() {
        return "Are you sure you want to cancel the order?";
    }
}
