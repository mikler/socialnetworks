package graph;

import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by tigrushka on 07/10/16.
 */
 class MyNode {
    private int cluster;
    private int value;
    private int weight = 0;
    private HashSet<MyNode> outgoingNeighbors;
    private int stepReached = 0; //used in parent map to track # of steps in which that node was reached
    private double flow = 0;

    MyNode(int value){
        this.value = value;
        this.cluster = value;
        outgoingNeighbors = new HashSet<MyNode>();
    }

    void setWeight(int weight){
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("%d", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyNode myNode = (MyNode) o;

        return value == myNode.value;

    }

    public int getCluster() {
        return cluster;
    }


    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    @Override
    public int hashCode() {
        return value;
    }

    public void AddOutgoingNeighbor(MyNode neighbor){
        outgoingNeighbors.add(neighbor);
    }


    public int getValue() {
        return value;
    }

    public HashSet<MyNode> getOutgoingNeighbors() {
        return outgoingNeighbors;
    }


    public int getWeight() {
        return weight;
    }

    public static Comparator<MyNode> byWeight() {
        return new byWeight();
    }

    public int getStepReached() {
        return stepReached;
    }

    public void setStepReached(int stepReached) {
        this.stepReached = stepReached;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }

    private static class byWeight implements Comparator<MyNode> {
        @Override
        public int compare(MyNode n1, MyNode n2) {
            if (n1 == null || n2 == null) throw new java.lang.NullPointerException();

            int weight1 = n1.getWeight();
            int weight2 = n2.getWeight();
            return -(weight1 - weight2);
        }
    }
}
