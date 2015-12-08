package to;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import journal.Task;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class for writing information about user to xml.
 */
@XStreamAlias("user")
public class User {
    @XStreamAsAttribute
    String login;

    @XStreamAsAttribute
    String pass;

    public User(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }
}
