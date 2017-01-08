package graph;

/**
 * Created by tigrushka on 07/10/16.
 */
 class MyEdge {
    private MyNode from;
    private MyNode to;
    private long timestamp;
    String activity;

    MyEdge(int from, int to, long timestamp, String activity){
        this.from = new MyNode(from);
        this.to = new MyNode(to);
        this.timestamp = timestamp;
        this.activity = activity;
    }

    MyEdge(MyNode from, MyNode to, long timestamp, String activity){
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.activity = activity;
    }

    @Override
    public String toString() {
        String s = String.format("%s %s %d %s", from, to, timestamp, activity);
        return s;
    }

    public MyNode getFrom() {
        return from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MyNode getTo() {
        return to;
    }

    public String getActivity() {
        return activity;
    }
}