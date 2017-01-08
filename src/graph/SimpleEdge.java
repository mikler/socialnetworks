package graph;

/**
 * Created by tigrushka on 17/10/16.
 */
public class SimpleEdge {
    private int from;
    private int to;


    public SimpleEdge(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "SimpleEdge{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEdge that = (SimpleEdge) o;

        if (from != that.from) return false;
        return to == that.to;

    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        return result;
    }
}
