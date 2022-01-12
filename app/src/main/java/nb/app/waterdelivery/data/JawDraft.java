package nb.app.waterdelivery.data;

public class JawDraft {

    private int jobid;
    private int customerid;
    private int waterid;
    private int water_amount;

    public JawDraft(int userid, int customerid, int waterid, int water_amount) {
        this.jobid = userid;
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

    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }
}
