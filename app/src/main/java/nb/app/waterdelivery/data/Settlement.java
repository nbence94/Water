package nb.app.waterdelivery.data;

public class Settlement {

    private int id;
    private String name;
    private String created;
    private String finished;
    private int userid;
    private int finisher_id;

    public Settlement(int id, String name, String created, String finished, int userid, int finisher_id) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.finished = finished;
        this.userid = userid;
        this.finisher_id = finisher_id;
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

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getFinisher_id() {
        return finisher_id;
    }

    public void setFinisher_id(int finisher_id) {
        this.finisher_id = finisher_id;
    }
}
