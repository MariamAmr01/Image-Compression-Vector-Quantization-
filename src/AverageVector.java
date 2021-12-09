import java.util.Vector;

public class AverageVector {
    private final Vector<Vector<Integer>> associated;
    private Vector<Integer> averageVector;
    private String  code;

    public AverageVector(Vector<Integer> v)
    {
        associated=new Vector<>();
        code="0";
        averageVector=v;
    }

    public void setAverageVector(Vector<Integer> averageVector) {
        this.averageVector = averageVector;
    }

    public Vector<Integer> getAverageVector() {
        return averageVector;
    }

    public void setAssociated(Vector<Integer> associated) {
        this.associated.add(associated);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Vector<Vector<Integer>> getAssociated() {
        return associated;
    }

    public String  getCode() {
        return code;
    }
}
