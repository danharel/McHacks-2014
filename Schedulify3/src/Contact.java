import java.util.ArrayList;

/**
 * Created by Edmund on 2/22/14.
 */
public class Contact {
    private String name;
    private ArrayList<String> emails;
    private ArrayList<String> numbers;
    private ArrayList<String> facebooks;
    private ArrayList<String> groups;

    public Contact(String name, ArrayList<String> emails, ArrayList<String> numbers,
                   ArrayList<String> facebooks, ArrayList<String> groups) {
        this.name = name;
        this.emails = emails;
        this.numbers = numbers;
        this.facebooks = facebooks;
        this.groups = groups;
    }

    public Contact() {
        this.name = "";
        this.emails = new ArrayList<String>();
        this.numbers = new ArrayList<String>();
        this.facebooks = new ArrayList<String>();
        this.groups = new ArrayList<String>();
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList<String> numbers) {
        this.numbers = numbers;
    }

    public ArrayList<String> getFacebooks() {
        return facebooks;
    }

    public void setFacebooks(ArrayList<String> facebooks) {
        this.facebooks = facebooks;
    }


}
