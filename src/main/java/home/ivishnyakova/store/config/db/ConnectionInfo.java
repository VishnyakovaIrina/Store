package home.ivishnyakova.store.config.db;

import java.io.*;

/* Класс ConnectionInfo предназначен для хранения информации о соединении к БД.
 *
 * Автор: Вишнякова И.
*/
public class ConnectionInfo implements Serializable{

    private String driver;
    private String url;
    private String userName;
    private String password;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ConnectionInfo{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
