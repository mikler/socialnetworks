/**
 * 
 */
package graph;

import util.GraphLoader;

import java.util.*;

/**
 * @author Your name here.
 * 
 * For the warm up assignment, you must implement your Graph in a class
 * named CapGraph.  Here is the stub file.
 *
 */
public class CapGraph implements Graph {

	HashMap<Integer, MyNode> nodes = new HashMap<Integer, MyNode>();
    HashMap<MyNode, HashSet<MyEdge>> edges = new HashMap<MyNode, HashSet<MyEdge>>();
    List<Graph> SCCs;
	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 */
	@Override
	public void addVertex(int num) {
        MyNode node = new MyNode(num);
        nodes.put(num, node);
        if(!edges.keySet().contains(num)){
            edges.put(node, new HashSet<MyEdge>());
        }
	}


    public int numNodes(){
        return nodes.size();
    }

    public int numEdges(){
        int size = 0;
        for(MyNode key : edges.keySet()){
            size += edges.get(key).size();
        }
        return size;
    }


    public void addVertex(MyNode node) {
        nodes.put(node.getValue(), node);
        if(!edges.keySet().contains(node.getValue())){
            edges.put(node, new HashSet<MyEdge>());
        }
    }

    @Override
    public void addEdge(int from, int to) {

    }

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */


	public void addEdge(int from, int to, long timestamp, String activity) {

        MyNode fromNode = new MyNode(from);
        MyNode toNode = new MyNode(to);


        MyEdge edge = new MyEdge(fromNode, toNode, timestamp, activity);



        if(!edges.keySet().contains(fromNode)){
            HashSet<MyEdge> hs = new HashSet<MyEdge>();
            hs.add(edge);
//            fromNode.setWeight(1);
//            toNode.setWeight(1);
            edges.put(fromNode, hs);
        }

        else{
//            edges.get(fromNode);

            HashSet<MyEdge> hs = edges.get(fromNode);
            hs.add(edge);
            edges.put(fromNode, hs);
        }
	}


    public void addEdge(MyEdge edge) {

        if(!edges.keySet().contains(edge.getFrom())){
            HashSet<MyEdge> hs = new HashSet<MyEdge>();
            hs.add(edge);
            edges.put(edge.getFrom(), hs);
        }

        else{
            HashSet<MyEdge> hs = edges.get(edge.getFrom());
            hs.add(edge);
            edges.put(edge.getFrom(), hs);
        }
    }


    /* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 */
	@Override
	public Graph getEgonet(int center) {
        CapGraph cg = new CapGraph();
        HashSet<MyEdge> neighbors = edges.get(center);

        if(!neighbors.isEmpty()){
            cg.addVertex(center);
        }

        //copy noces in the new graph
        for (MyEdge neighbor : neighbors){
            //add neighboring nodes
            cg.addVertex(neighbor.getTo());

            //add edges from the center to these nodes
            cg.addEdge(center, neighbor.getTo().getValue(), neighbor.getTimestamp(), neighbor.activity);
        }

        //copy vertices between the neighbors
        for(MyEdge neighbor : neighbors){
            HashSet<MyEdge> n = edges.get(neighbor);
            for(MyEdge neigh : neighbors){
                if(neigh != neighbor && n.contains(neigh)){
                    cg.addEdge(neighbor.getFrom().getValue(), neigh.getTo().getValue(), neigh.getTimestamp(), neigh.activity);
                }
            }
        }

		return cg;
	}

	/* (non-Javadoc)
	 * @see graph.Graph#getSCCs()
	 */
	@Override
	public List<Graph> getSCCs() {



        //copy all vertices from a hashset to a stack so we can use dfs on them.
        Stack<MyNode> vertices = new Stack<MyNode>();
        for(Integer nodeID : nodes.keySet()){
            vertices.push(nodes.get(nodeID));
        }
        //do depth first search
        SCCs = new ArrayList<Graph>();
        Stack<MyNode> finished = dfs(this, vertices);

        //compute the transpose
        CapGraph transpose = computeTranspose();
        //do dfs in reverse order. Each tree returned is a SCC

        SCCs = new ArrayList<Graph>();
        Stack<MyNode> result = dfs(transpose, finished);



		// TODO Auto-generated method stub
		return SCCs;
	}

    @Override
    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        return null;
    }


    private Stack<MyNode> dfs(CapGraph graph, Stack<MyNode> vertices){ //// TODO: 15/09/16
        HashSet<MyNode> visited = new HashSet<MyNode>();
        Stack<MyNode> finished = new Stack<MyNode>();


        while(!vertices.isEmpty()){
            MyNode v = vertices.pop();
            if(!visited.contains(v)){
                Graph scc = new CapGraph();
                SCCs.add(scc);
                scc.addVertex(v.getValue());
                dfsVisit(graph, v, visited, finished, scc);
            }
        }

        return finished;
    }

    private void dfsVisit(CapGraph graph, MyNode v, HashSet<MyNode> visited, Stack<MyNode> finished, Graph scc) {

        visited.add(v);
        for(MyEdge n : graph.edges.get(v)){
            if(!visited.contains(n)){
                dfsVisit(graph, n.getFrom(), visited, finished, scc);
            }
        }
        finished.push(v);

        scc.addVertex(v.getValue());
        for(MyEdge edge : edges.get(v)){
            addEdge(edge);
        }

    }

    private CapGraph computeTranspose(){
        CapGraph cg = new CapGraph();
        for(Integer nodeID : nodes.keySet()){
            MyNode node = nodes.get(nodeID);
            cg.addVertex(node);
            HashSet<MyEdge> es = edges.get(node);
            for(MyEdge e : es){
                cg.addEdge(e.getTo().getValue(), e.getFrom().getValue(), e.getTimestamp(), e.activity);
            }
        }

        return cg;
    }

//    /* (non-Javadoc)
//     * @see graph.Graph#exportGraph()
//     */
//	@Override
//	public HashMap<Integer, HashSet<Integer>> exportGraph() {
//		return edges;
//	}

    public void printGraph() {
        for(MyNode node : edges.keySet()){
            System.out.println("Inspecting node: " + node);
            HashSet<MyEdge>  hs = edges.get(node);
            for(MyEdge e : hs){
                e.toString();
            }
        }
    }


    //todo calculate incoming engagement activity
    public void calculateOutgoingEngagement(){
        for(Integer nodeID : nodes.keySet()){
            MyNode node = nodes.get(nodeID);
            int weight = node.getOutgoingNeighbors().size();
            node.setWeight(weight);
        }
    }


    public void calculateEngagementRatios(){
        calculateOutgoingEngagement();
        calculateIncomingEngagement();
    }


    public void initializeOutcomingNeighbors(){
        for (MyNode n : edges.keySet()){
            for (MyEdge edge : edges.get(n)){
                n.AddOutgoingNeighbor(edge.getTo());
            }
        }
    }

    public void calculateIncomingEngagement(){
        for(MyNode node : edges.keySet()){
            for(MyEdge edge : edges.get(node)){
                int toNode = edge.getTo().getValue();
                MyNode nodeToAddWeightTo = nodes.get(toNode);
                int weight = nodeToAddWeightTo.getWeight() + 1;
                nodeToAddWeightTo.setWeight(weight);
            }
        }
    }


    public MyNode[] findTopEngagers(double topPercent){
        Collection<MyNode> values = nodes.values();
        MyNode[] array = new MyNode[values.size()];
        values.toArray(array);
        Arrays.sort(array, MyNode.byWeight());
        MyNode[] result = Arrays.copyOfRange(array, 0, (int)(array.length * topPercent));
        return result;
    }


    public static void main(String[] args) {
        CapGraph cg = new CapGraph();
        GraphLoader.loadGraphWithEdges(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/higgs-activity_time.txt");
        cg.initializeOutcomingNeighbors();
        cg.calculateEngagementRatios();
        MyNode[] array = cg.findTopEngagers(0.0001);
        for(MyNode node : array){
            System.out.println(node);
        }
        System.out.println("Graph loaded");
//        cg.getSCCs();
        System.out.println("Mission accomlished");

    }





}
