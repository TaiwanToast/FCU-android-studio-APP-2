package fcu.edu.check_in.model;

public class PointChangeMono {
    private final String item;
    private final int costPoint;

    public PointChangeMono(String item, int costPoint) {
        this.item = item;
        this.costPoint = costPoint;
    }

    public String getItem() {
        return item;
    }

    public int getCostPoint() {
        return costPoint;
    }
}
