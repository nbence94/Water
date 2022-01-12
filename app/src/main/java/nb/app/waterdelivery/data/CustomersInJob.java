package nb.app.waterdelivery.data;

public class CustomersInJob {
    private int jobid;
    private int customerid;
    private String finish;

    public CustomersInJob(int jobid, int customerid, String finish) {
        this.jobid = jobid;
        this.customerid = customerid;
        this.finish = finish;
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

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }
}
