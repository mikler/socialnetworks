/**
 * 
 */
package graph;

import util.GraphLoader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Your name here.
 * 
 * For the warm up assignment, you must implement your Graph in a class
 * named CapGraph.  Here is the stub file.
 *
 */
public class CapGraphWithNodesBackup implements Graph {

	HashMap<Integer, MyNode> nodes = new HashMap<Integer,MyNode>();
    HashMap<Integer, HashSet<Integer>> edges = new HashMap<Integer, HashSet<Integer>>();
    List<Graph> SCCs;
    HashMap<SimpleEdge, Double> flow;

    public CapGraphWithNodesBackup() {
        flow = new HashMap<SimpleEdge, Double>();
    }

    /* (non-Javadoc)
         * @see graph.Graph#addVertex(int)
         */
	@Override
	public void addVertex(int num) {
        nodes.put(num, new MyNode(num));
        if(!edges.keySet().contains(num)){
            edges.put(num, new HashSet<Integer>());
        }
	}

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */
	@Override
	public void addEdge(int from, int to) {

//        System.out.printf("Calling addEdge %d %d%n", from, to);
        if(!edges.keySet().contains(from)){
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.add(to);
            edges.put(from, hs);
//            System.out.printf("Calling put     %d %d%n", from, to);
        }

        else{
            HashSet<Integer> hs = edges.get(from);
            hs.add(to);
            edges.put(from, hs);
        }
        flow.put(new SimpleEdge(from, to), 0.0);
    }


	/* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 */
	@Override
	public Graph getEgonet(int center) {
        CapGraphWithNodesBackup cg = new CapGraphWithNodesBackup();
        HashSet<Integer> neighbors = edges.get(center);

        if(!neighbors.isEmpty()){
            cg.addVertex(center);
        }

        //copy noces in the new graph
        for (Integer neighbor : neighbors){
            //add neighboring nodes
            cg.addVertex(neighbor);

            //add edges from the center to these nodes
            cg.addEdge(center, neighbor);
        }

        //copy vertices between the neighbors
        for(Integer neighbor : neighbors){
            HashSet<Integer> n = edges.get(neighbor);
            for(Integer neigh : neighbors){
                if(neigh != neighbor && n.contains(neigh)){
                    cg.addEdge(neighbor, neigh);
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
        Stack<Integer> vertices = new Stack<Integer>();
        for(Integer node : nodes.keySet()){
            vertices.push(node);
        }
        //do depth first search
        SCCs = new ArrayList<Graph>();
        Stack<Integer> finished = dfs(this, vertices);

        //compute the transpose
        CapGraphWithNodesBackup transpose = computeTranspose();
        //do dfs in reverse order. Each tree returned is a SCC

        SCCs = new ArrayList<Graph>();
        Stack<Integer> result = dfs(transpose, finished);

		return SCCs;
	}


    private Stack<Integer> dfs(CapGraphWithNodesBackup graph, Stack<Integer> vertices){ //// TODO: 15/09/16
        HashSet<Integer> visited = new HashSet<Integer>();
        Stack<Integer> finished = new Stack<Integer>();


        while(!vertices.isEmpty()){
            Integer v = vertices.pop();
            if(!visited.contains(v)){
                Graph scc = new CapGraphWithNodesBackup();
                SCCs.add(scc);
                scc.addVertex(v);
                dfsVisit(graph, v, visited, finished, scc);
            }
        }

        return finished;
    }

    private void dfsVisit(CapGraphWithNodesBackup graph, Integer v, HashSet<Integer> visited, Stack<Integer> finished, Graph scc) {

        visited.add(v);
        for(Integer n : graph.edges.get(v)){
            if(!visited.contains(n)){
                dfsVisit(graph, n, visited, finished, scc);
            }
        }
        finished.push(v);

        scc.addVertex(v);
        for(Integer node : edges.get(v)){
            addEdge(v, node);
        }

    }

    private CapGraphWithNodesBackup computeTranspose(){
        CapGraphWithNodesBackup cg = new CapGraphWithNodesBackup();
        for(Integer node : nodes.keySet()){
            cg.addVertex(node);
            HashSet<Integer> es = edges.get(node);
            for(Integer e : es){
                cg.addEdge(e, node);
            }
        }

        return cg;
    }


    //todo overall method to compute communities
    public HashMap<Integer, HashSet<MyNode>> findCommunities(int numCommunities){

//        List<Graph> result = new LinkedList<Graph>();

        //compute betweenness of all edges
        //for each node do a bfs
        int count = 0;
//        int innerCount = 0;
        Set<Integer> allNodeKeys = nodes.keySet();
        int size = allNodeKeys.size();
        int totalPermutations =  size;
        int significantWork = (int) (totalPermutations/10000);
        LocalDateTime startTime = LocalDateTime.now();

        for (Integer start : allNodeKeys) {

            //track & display progress
            count++;
            if (count % significantWork == 0) {
                displayProgress(count, totalPermutations, startTime);
            }
            if(count % 100 == 0){
                System.out.println("Starting bfs for " + count + "th node: from " + start);
            }

            //do work
            HashMap<Integer, ArrayList<MyNode>> parent = bfs(start);//doing BFS with no goal. stops when out of nodes.

            for(Integer end : allNodeKeys){//now we reconstruct from all nodes back to start
                MyNode endNode = nodes.get(end);
                if (start != end && endNode.getStepReached() > 0){//stepReached == -1 mean there is no path (is set in the bfs)
//                    innerCount++;

                    ArrayList<LinkedList<MyNode>> shortestPaths = reconstructPaths(start, end, endNode.getStepReached(), parent);
                    calculateFlow(shortestPaths);
                }
            }
        }

        System.out.println("Sorting edges...");
        LinkedList<Entry<SimpleEdge, Double>> sortedEdges = sortByFlow(flow);
        System.out.println("Finding Communities");

        HashMap<Integer, HashSet<MyNode>> result = divideInCommunities(numCommunities, sortedEdges);

        return result;
    }

    private void displayProgress(double count, double totalPermutations, LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration elapsed = Duration.between(startTime, now);
        long elapsedMillis = elapsed.toMillis();
        double progress = count / totalPermutations;
        double prPerTime = progress / elapsedMillis;
        Duration remaining = Duration.ofMillis((long) (prPerTime * (1 - progress)));
        System.out.printf("Progress: %.3f%%, Remaning time: %s %n", 100 * progress, remaining.toMinutes());
    }

    private void calculateFlow(ArrayList<LinkedList<MyNode>> shortestPaths) {

        double totalEdges = 0;

        int numPaths = shortestPaths.size();
        if (!shortestPaths.isEmpty()) {
            for (LinkedList<MyNode> path : shortestPaths) {
                Object[] pathArray = path.toArray();
                for (int i = 0; i < pathArray.length - 1; i++) {
                    MyNode fromNode = (MyNode) pathArray[i];
                    MyNode toNode = (MyNode) pathArray[i + 1];
                    if (fromNode.getValue() != toNode.getValue()) {
                        SimpleEdge edge = new SimpleEdge(fromNode.getValue(), toNode.getValue());
                        totalEdges++;
                    }
                }
            }
        }
    }

    private HashMap<Integer, HashSet<MyNode>> divideInCommunities(int numCommunities, LinkedList<Entry<SimpleEdge, Double>> sortedEdges) {
        HashMap<Integer, HashSet<MyNode>> clusters = new HashMap<Integer, HashSet<MyNode>>();
        HashMap<Integer, Integer> mapOfMovements = new HashMap<Integer, Integer>(nodes.keySet().size());

        //fill the clusters
        //we start with each node pointing to itself
        for (Integer nodeID : nodes.keySet()) {
            HashSet<MyNode> hs = new HashSet<MyNode>();
            MyNode node = nodes.get(nodeID);
            hs.add(node);
            node.setCluster(node.getValue());
            clusters.put(nodeID, hs);
            mapOfMovements.put(nodeID, nodeID);
        }

        for (int i = 0; i < sortedEdges.size(); i++) {
            if(clusters.keySet().size() == numCommunities) break;
            Entry<SimpleEdge, Double> edgeFlowPair = sortedEdges.get(i);
            SimpleEdge edge = edgeFlowPair.getKey();
            int clusterToRemove = nodes.get(edge.getFrom()).getCluster();
            MyNode fromNode = nodes.get(clusterToRemove);
            MyNode toNode = nodes.get(edge.getTo());

            if (fromNode.getCluster() == toNode.getCluster()){
                continue;
            }

            HashSet<MyNode> toMove = clusters.get(fromNode.getCluster());
            HashSet<MyNode> targetClusterNodes = clusters.get(toNode.getCluster());

            for (MyNode node :
                    toMove) {
                node.setCluster(toNode.getCluster());
            }

            targetClusterNodes.addAll(toMove);

            clusters.remove(clusterToRemove);

        }

        return clusters;
    }

    public HashMap<Integer, ArrayList<MyNode>> bfs(int start){

        Queue<MyNode> queue = new LinkedList<MyNode>();
        HashSet<MyNode> visited = new HashSet<MyNode>();
        HashMap<Integer, ArrayList<MyNode>> parent = new HashMap<Integer, ArrayList<MyNode>>();

        for (MyNode node :
                nodes.values()) {
            node.setStepReached(-1); //is this loop needed?
        }

        MyNode startNode = nodes.get(start);
        startNode.setStepReached(0);
        queue.add(startNode);


        HashMap<Integer, MyNode> nodeCache = new HashMap<Integer, MyNode>();

        while(!queue.isEmpty()){
            MyNode curr = queue.poll();
            visited.add(curr);

            if(visited.size() == nodes.size()) break;

            HashSet<Integer> neighbors = edges.get(curr.getValue());

            for (Integer neighbor : neighbors) {

                //minimizing MyNode object creation for performance reasons
                MyNode n;
                if (nodeCache.containsKey(neighbor)){
                    n = nodeCache.get(neighbor);
                } else {
                    n = new MyNode(neighbor);
                    nodeCache.put(neighbor, n);
                }


                if (!visited.contains(n)) {
                    n.setStepReached(curr.getStepReached() + 1);
                    nodes.get(n.getValue()).setStepReached(n.getStepReached());

                    if (!parent.containsKey(neighbor)) {
                        ArrayList<MyNode> list = new ArrayList<MyNode>();
                        list.add(curr);
                        parent.put(neighbor, list);
                    } else {
                        parent.get(neighbor).add(curr);
                    }


                    queue.add(n);
                }

            }
//                    if(curr.getValue() == 50927 || curr.getValue() == 64911) {
//                        System.out.println();
//                        System.out.println("Printing parent map:");
//                        for (Integer p : parent.keySet()) {
//                            System.out.printf("||      %d: ", p);
//                            for (MyNode nn : parent.get(p)) {
//                                System.out.print(nn + " ");
//                            }
//                        }
//                        System.out.println();
//                        System.out.println("=================================");
//                    }
        }


        return parent;
    }


    //reconstructing multiple shortest paths found in a single bfs with no end point
    public ArrayList<LinkedList<MyNode>> reconstructPaths(int start, int end, int stepReached, HashMap<Integer, ArrayList<MyNode>> parent){

        //return an empty structure because an empty/single element array doesn't have a path
        if(parent.size() <= 1) return new ArrayList<LinkedList<MyNode>>();



        ArrayList<MyNode> currentNodes;
        ArrayList<LinkedList<MyNode>> result = new ArrayList<LinkedList<MyNode>>(stepReached);

        LinkedList<MyNode> endList = new LinkedList<MyNode>();

        MyNode e = nodes.get(end);
        e.setStepReached(stepReached);
        endList.addFirst(e);
        result.add(endList);


        boolean foundStart = false;
        int numberOfSteps = 0;
        do {
            ArrayList<LinkedList<MyNode>> nextResult = new ArrayList<LinkedList<MyNode>>();

            for (LinkedList<MyNode> res : result) {
                MyNode first = res.getFirst();

                //terminate if found start
                if (first.getValue() == start) {
                    foundStart = true;
                    nextResult = result;
                    break;
                }


                currentNodes = parent.get(first.getValue());
                HashSet<MyNode> visitedParents = new HashSet<MyNode>();
                for (MyNode neibh  :  currentNodes) {
                    if (neibh.getStepReached() != first.getStepReached()-1 || visitedParents.contains(neibh)) continue;  //do I need these checks now?
                    LinkedList<MyNode> newRes = new LinkedList<MyNode>(res);
                    newRes.addFirst(neibh);
                    nextResult.add(newRes);
                    visitedParents.add(neibh); //need this?
                }
            }
            result = nextResult;

        } while (!foundStart && numberOfSteps++ < nodes.size());//second part doesn't make sense
//
//        if (numberOfSteps >= nodes.size()){
//            System.out.printf("Failed to reconstruct path from %d to %d%n", start, end);
//        }

        return result;
    }

    public Comparator<Entry<SimpleEdge, Double>> compareByFlow(){
        return new byFlow();
    }

    private class byFlow implements Comparator<Entry<SimpleEdge, Double>> {

        @Override
        public int compare(Entry<SimpleEdge, Double> o1, Entry<SimpleEdge, Double> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }


    public LinkedList<Entry<SimpleEdge, Double>> sortByFlow(Map<SimpleEdge, Double> unsortedFlowMap){
        LinkedList<Entry<SimpleEdge, Double>> list = new LinkedList<Entry<SimpleEdge, Double>>(unsortedFlowMap.entrySet());

        Collections.sort(list,  compareByFlow());

        return list;
    }

    /* (non-Javadoc)
     * @see graph.Graph#exportGraph()
     */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		return edges;
	}

    public static void main(String[] args) {
        CapGraphWithNodesBackup cg = new CapGraphWithNodesBackup();
        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/twitter_higgs.txt");
//        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/course_example_users.txt");
//        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/course_example_users_directed.txt");
        System.out.println("Graph loaded");
//        cg.getSCCs();
//        cg.bfs(4,677);
//        ArrayList<LinkedList<MyNode>> shortestPaths = cg.bfs(1, 6);
//        System.out.println("Stop... there should've been an error");
        HashMap<Integer, HashSet<MyNode>> communities = cg.findCommunities(2);


        for(Integer clusterNumber : communities.keySet()){
            HashSet<MyNode> communityMembers = communities.get(clusterNumber);
            int communitySize = communityMembers.size();
            System.out.printf("Cluster number: %dCommunity size: %d%n", clusterNumber, communitySize);
            System.out.println("Members:");
            for(MyNode member : communityMembers){
                System.out.println(member);
            }
        }


//    ArrayList<LinkedList<MyNode>> testBfsOutput = cg.bfs(5, 2); // expected value 2
//        ArrayList<LinkedList<MyNode>> testBfsOutput1 = cg.bfs(2, 5); //
//    ArrayList<LinkedList<MyNode>> testBfsOutput2 = cg.bfs(4, 1); // expected value 2


//        for (LinkedList<MyNode> res:
//        testBfsOutput){
//            for (MyNode n :
//                    res) {
//                System.out.printf("%s, %n", n);
//            }
//            System.out.println();
//        }

        System.out.println("Mission accomlished");

    }


}
