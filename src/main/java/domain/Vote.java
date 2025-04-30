package domain;

public class Vote {
    private String userId;
    private boolean inFavor;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public boolean isInFavor() {
        return inFavor;
    }
    public void setInFavor(boolean inFavor) {
        this.inFavor = inFavor;
    }

}
