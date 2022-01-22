package nb.app.waterdelivery.data;

public class JobsInSettlement {

    private int settlementid;
    private int jobid;

    public JobsInSettlement(int settlementid, int jobid) {
        this.settlementid = settlementid;
        this.jobid = jobid;
    }

    public int getSettlementid() {
        return settlementid;
    }

    public void setSettlementid(int settlementid) {
        this.settlementid = settlementid;
    }

    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }
}
