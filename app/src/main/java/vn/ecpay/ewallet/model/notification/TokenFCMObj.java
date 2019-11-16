package vn.ecpay.ewallet.model.notification;

public class TokenFCMObj {
    private String app_name;
    private String created_date;
    private String status;
    private String terminal_id;
    private String terminal_info;
    private String token;
    private String channel_code;


    // Getter Methods

    public String getApp_name() {
        return app_name;
    }

    public String getCreated_date() {
        return created_date;
    }

    public String getStatus() {
        return status;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public String getTerminal_info() {
        return terminal_info;
    }

    public String getToken() {
        return token;
    }

    public String getChannel_code() {
        return channel_code;
    }

    // Setter Methods

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public void setTerminal_info(String terminal_info) {
        this.terminal_info = terminal_info;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setChannel_code(String channel_code) {
        this.channel_code = channel_code;
    }
}
