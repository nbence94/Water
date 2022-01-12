package nb.app.waterdelivery.data;

public class CustomerAndWaters {

    private int customerid;
    private int waterid;

    public CustomerAndWaters(int customerid, int waterid) {
        this.customerid = customerid;
        this.waterid = waterid;
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
}
