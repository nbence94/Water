package nb.app.waterdelivery.data;

public class Draft {

    private int userid;
    private int customerid;
    private int waterid;
    private int water_amount;

    public Draft(int userid, int customerid, int waterid, int water_amount) {
        this.userid = userid;
        this.customerid = customerid;
        this.waterid = waterid;
        this.water_amount = water_amount;
    }

    public int getCustomerid() {
        return customerid;
    }

    public void setCustomerid(int customerid) {
        this.customerid = customerid;
    }

    public int getWaterid() {
        return waterid;
    }

    public void setWaterid(int waterid) {
        this.waterid = waterid;
    }

    public int getWater_amount() {
        return water_amount;
    }

    public void setWater_amount(int water_amount) {
        this.water_amount = water_amount;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
