package fcu.edu.check_in.model;

public class PointChangeMono {
    private final String item;
    private final int status;
    private final int costPoint;

    public PointChangeMono(String item, int status, int costPoint) {
        this.item = item;
        this.status = status;
        this.costPoint = costPoint;
    }

    public String getItem() {
        return item;
    }
    public int getStatus() {
        return status;
    }
    public int getCostPoint() {
        return costPoint;
    }
}
