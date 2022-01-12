package nb.app.waterdelivery.data;

public class Roles {

    private int id;
    private String name;

    public Roles(int id, String role_name) {
        this.id = id;
        this.name = role_name;
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
}

