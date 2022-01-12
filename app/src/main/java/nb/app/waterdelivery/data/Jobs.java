package nb.app.waterdelivery.data;

public class Jobs {

    private int id;
    private String name;
    private String created;
    private String finish;
    private int income;
    private int userid;

    public Jobs(int id, String name, String created, String finish, int income, int userid) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.finish = finish;
        this.income = income;
        this.userid = userid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

}
