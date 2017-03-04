package ee364e.happs;

/**
 * Created by Young on 2017-02-19.
 */

public class Message {
    private String id;
    private String username;
    private String message;

    public Message() {

    }

    public Message(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public Message(String id, String username, String message) {
        this.id = id;
        this.username = username;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
