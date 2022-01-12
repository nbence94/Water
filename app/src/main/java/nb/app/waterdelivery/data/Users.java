package nb.app.waterdelivery.data;

public class Users {

    private int id;
    private String created;
    private String name;
    private String email;
    private String password;
    private String phone;
    private int jobnumber;
    private int roleid;
    private int status;

    public Users(int id, String created, String name, String email, String password, String phone, int jobnumber, int roleid, int status) {
        this.id = id;
        this.created = created;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.jobnumber = jobnumber;
        this.roleid = roleid;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(int jobnumber) {
        this.jobnumber = jobnumber;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
