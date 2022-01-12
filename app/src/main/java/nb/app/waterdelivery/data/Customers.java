package nb.app.waterdelivery.data;

public class Customers {
    private int id;
    private String created;
    private String fullname;
    private String city;
    private String address;
    private String email;
    private String phone_one;
    private String phone_two;
    private int water_weeks;
    private int bill;
    private int userid;

    public Customers (int id, String created, String fullname, String city, String address, String email, String phone, String phone_plus, int water_week, int bill, int userid) {
        this.id = id;
        this.created = created;
        this.fullname = fullname;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phone_one = phone;
        this.phone_two = phone_plus;
        this.water_weeks = water_week;
        this.bill = bill;
        this.userid = userid;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_one() {
        return phone_one;
    }

    public void setPhone_one(String phone_one) {
        this.phone_one = phone_one;
    }

    public String getPhone_two() {
        return phone_two;
    }

    public void setPhone_two(String phone_two) {
        this.phone_two = phone_two;
    }

    public int getWater_weeks() {
        return water_weeks;
    }

    public void setWater_weeks(int water_weeks) {
        this.water_weeks = water_weeks;
    }

    public int getBill() {
        return bill;
    }

    public void setBill(int bill) {
        this.bill = bill;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
