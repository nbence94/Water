package nb.app.waterdelivery.data;

public class JobAndWaters {
    private int jobid;
    private int customerid;
    private int waterid;
    private int wateramount;

    public JobAndWaters(int jobid, int customerid, int waterid, int wateramount) {
        this.jobid = jobid;
        this.customerid = customerid;
        this.waterid = waterid;
        this.wateramount = wateramount;
    }

    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
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

    public int getWateramount() {
        return wateramount;
    }

    public void setWateramount(int wateramount) {
        this.wateramount = wateramount;
    }
}
