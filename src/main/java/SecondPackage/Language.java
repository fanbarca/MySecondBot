package SecondPackage;

import java.util.List;

interface Language {
    String welcome(Order o);

    List<String> menu();
    String chooseDirection();
    String orders(Translation o);
    String emptyOrders();
    String cost();
    String confirmChoice(String choice);
    String sendMe();
    String what();
    String received();
    String myContact();
    String contactReceived();
    String sendMeContact();
    String confirmOrder();
    String getYes();
    String getNo();
    String cancelled();
    String orderExists();
    String weWillContact();
    String youSure();
    String doYouConfirm();
    String howManyPages();
    String preliminary(Translation a);
    String cancel();
    String finished();
    String whatVoice();
    String whatVideonote();
    String whatVideo();
    String whatPhoto();
    String whatLocation();
    String whatSticker();
    String whatAnimation();
    String whatAudio();

}