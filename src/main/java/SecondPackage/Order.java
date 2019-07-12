package SecondPackage;

import org.telegram.telegrambots.meta.api.objects.Message;

public class Order {


    private Message sentMessage = null;
    private Message receivedMes = null;
    private Message image = null;
    private String language ="";
    private String number = "";
    

    public Order() {

    }


	public String getNumber() {
		return number;
	}


	public void setNumber(String number) {
		this.number = number;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public Message getImage() {
		return image;
	}


	public void setImage(Message image) {
		this.image = image;
	}


	public Message getReceivedMes() {
		return receivedMes;
	}


	public void setReceivedMes(Message receivedMes) {
		this.receivedMes = receivedMes;
	}


	public Message getSentMessage() {
		return sentMessage;
	}


	public void setSentMessage(Message sentMessage) {
		this.sentMessage = sentMessage;
	}


}
