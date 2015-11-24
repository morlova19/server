package to;

import java.util.Date;

/**
 * TransferObject is an object which contains parameters that should be transferred.
 */
public class TransferObject {
    private String name;
    private String description;
    private Date date;
    private String contacts;
    private int id;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getContacts() {
        return contacts;
    }

    public int getId() {
        return id;
    }
}
