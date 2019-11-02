package SecondPackage;


public class LocalStudent {


    private String sentMessage;
    private String receivedMes;
    private String image;
    private String language;
    private String number;
    private String firstName;

    public LocalStudent(String firstName,
                        String number,
                        String language,
                        String receivedMes,
                        String sentMessage,
                        String image) {
		this.firstName = firstName;
		this.number = number;
		this.language = language;
		this.image = image;
		this.receivedMes = receivedMes;
		this.sentMessage = sentMessage;
	}
	public LocalStudent(String firstName,
                        String number,
                        String language,
                        String receivedMes) {
		this.firstName = firstName;
		this.number = number;
		this.language = language;
		this.receivedMes = receivedMes;
		this.sentMessage = null;
		this.image = null;
	}
	public LocalStudent() {
		this.firstName = null;
		this.number = null;
		this.language = null;
		this.receivedMes = null;
		this.sentMessage = null;
		this.image = null;
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


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	public String getReceivedMes() {
		return receivedMes;
	}


	public void setReceivedMes(String receivedMes) {
		this.receivedMes = receivedMes;
	}


	public String getSentMessage() {
		return sentMessage;
	}


	public void setSentMessage(String sentMessage) {
		this.sentMessage = sentMessage;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}
