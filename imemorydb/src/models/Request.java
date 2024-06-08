package models;


import lombok.Data;

@Data
public class Request {
    private String key;
    private String value;

    public Request(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Request() {
    }
}
