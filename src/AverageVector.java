import java.util.Vector;

public class AverageVector {
    private Vector<Vector<Integer>> associated;
    private Vector<Integer> averageVector;
    private int code;


    public AverageVector(Vector<Integer> v)
    {
        associated=new Vector<>();
        code=0;
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

    public void setCode(int code) {
        this.code = code;
    }

    public Vector<Vector<Integer>> getAssociated() {
        return associated;
    }

    public int getCode() {
        return code;
    }
}
