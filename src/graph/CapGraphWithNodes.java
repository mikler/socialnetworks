/**
 *
 */
package graph;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import util.GraphLoader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Map.Entry;

import java.util.*;

/**
 * @author Your name here.
 *         <p>
 *         For the warm up assignment, you must implement your Graph in a class
 *         named CapGraph.  Here is the stub file.
 */
public class CapGraphWithNodes implements Graph {

    HashMap<Integer, MyNode> nodes = new HashMap<Integer, MyNode>();
    HashMap<Integer, HashSet<Integer>> edges = new HashMap<Integer, HashSet<Integer>>();
    List<Graph> SCCs;
    HashMap<SimpleEdge, Double> flow;

    public CapGraphWithNodes() {
        flow = new HashMap<SimpleEdge, Double>();
    }

    /* (non-Javadoc)
         * @see graph.Graph#addVertex(int)
         */
    @Override
    public void addVertex(int num) {
        nodes.put(num, new MyNode(num));
        if (!edges.keySet().contains(num)) {
            edges.put(num, new HashSet<Integer>());
        }
    }

    /* (non-Javadoc)
     * @see graph.Graph#addEdge(int, int)
     */
    @Override
    public void addEdge(int from, int to) {

//        System.out.printf("Calling addEdge %d %d%n", from, to);
        if (!edges.keySet().contains(from)) {
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.add(to);
            edges.put(from, hs);
//            System.out.printf("Calling put     %d %d%n", from, to);
        } else {
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
        CapGraphWithNodes cg = new CapGraphWithNodes();
        HashSet<Integer> neighbors = edges.get(center);

        if (!neighbors.isEmpty()) {
            cg.addVertex(center);
        }

        //copy noces in the new graph
        for (Integer neighbor : neighbors) {
            //add neighboring nodes
            cg.addVertex(neighbor);

            //add edges from the center to these nodes
            cg.addEdge(center, neighbor);
        }

        //copy vertices between the neighbors
        for (Integer neighbor : neighbors) {
            HashSet<Integer> n = edges.get(neighbor);
            for (Integer neigh : neighbors) {
                if (neigh != neighbor && n.contains(neigh)) {
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
        for (Integer node : nodes.keySet()) {
            vertices.push(node);
        }
        //do depth first search
        SCCs = new ArrayList<Graph>();
        Stack<Integer> finished = dfs(this, vertices);

        //compute the transpose
        CapGraphWithNodes transpose = computeTranspose();
        //do dfs in reverse order. Each tree returned is a SCC

        SCCs = new ArrayList<Graph>();
        Stack<Integer> result = dfs(transpose, finished);

        return SCCs;
    }


    private Stack<Integer> dfs(CapGraphWithNodes graph, Stack<Integer> vertices) { //// TODO: 15/09/16
        HashSet<Integer> visited = new HashSet<Integer>();
        Stack<Integer> finished = new Stack<Integer>();


        while (!vertices.isEmpty()) {
            Integer v = vertices.pop();
            if (!visited.contains(v)) {
                Graph scc = new CapGraphWithNodes();
                SCCs.add(scc);
                scc.addVertex(v);
                dfsVisit(graph, v, visited, finished, scc);
            }
        }

        return finished;
    }

    private void dfsVisit(CapGraphWithNodes graph, Integer v, HashSet<Integer> visited, Stack<Integer> finished, Graph scc) {

        visited.add(v);
        for (Integer n : graph.edges.get(v)) {
            if (!visited.contains(n)) {
                dfsVisit(graph, n, visited, finished, scc);
            }
        }
        finished.push(v);

        scc.addVertex(v);
        for (Integer node : edges.get(v)) {
            addEdge(v, node);
        }

    }

    private CapGraphWithNodes computeTranspose() {
        CapGraphWithNodes cg = new CapGraphWithNodes();
        for (Integer node : nodes.keySet()) {
            cg.addVertex(node);
            HashSet<Integer> es = edges.get(node);
            for (Integer e : es) {
                cg.addEdge(e, node);
            }
        }

        return cg;
    }


    //todo overall method to compute communities
    //assuming that the existing graph is one piece - will run the routine at least once.
    public HashMap<Integer, HashSet<MyNode>> findCommunities(int thresholdNumCommunities) {

        HashMap<Integer, HashSet<MyNode>> communities = new HashMap<Integer, HashSet<MyNode>>();
        int communitySize = 1;

        while (communitySize < thresholdNumCommunities) {

            calculateBetweenness();

//            System.out.println("Sorting edges...");
            //find the maxFlow and sort the edges for easier deletion
            LinkedList<Entry<SimpleEdge, Double>> sortedEdges = sortByFlow(flow);

//            System.out.println("Removing edges...");

            contractEdgesWithMaxFlow(sortedEdges);

//            System.out.println("Finding Communities");
            communities = divideInCommunities(/*numCommunities,*/ sortedEdges);

            communitySize = communities.keySet().size();
        }

        return communities;
    }


    //the items with max value must be in the front of the list???
    void contractEdgesWithMaxFlow(LinkedList<Entry<SimpleEdge, Double>> sortedEdges) {
        double maxFlow = sortedEdges.getFirst().getValue();
        LinkedList<Entry<SimpleEdge, Double>> edgesToDelete = new LinkedList<Entry<SimpleEdge, Double>>();
        int i = 0;
        Entry<SimpleEdge, Double> currentEdge = sortedEdges.getFirst();

        while (Math.abs(sortedEdges.get(i).getValue() - maxFlow) < 0.00000000001) {
            int from = currentEdge.getKey().getFrom();
            int to = currentEdge.getKey().getTo();
            HashSet<Integer> toEdges = edges.get(from);
            toEdges.remove(to);
            edgesToDelete.add(currentEdge);

            if (i >= sortedEdges.size() - 1) break;
            i++;
            currentEdge = sortedEdges.get(i);
        }
        sortedEdges.removeAll(edgesToDelete);
    }

    void calculateBetweenness() {
        int count = 0;
//        int innerCount = 0;
        Set<Integer> allNodeKeys = nodes.keySet();
        int size = allNodeKeys.size();
        int totalPermutations = size;
        int significantWork = (int) (totalPermutations / 10000);
        LocalDateTime startTime = LocalDateTime.now();

        for (Integer start : allNodeKeys) {

            //track & display progress
            count++;
            if (significantWork > 0 && count % significantWork == 0) {
                displayProgress(count, totalPermutations, startTime);
            }
            if (count % 100 == 0) {
                System.out.println("Starting bfs for " + count + "th node: from " + start);
            }

            //do bfs from each node without an end point
            //it will set the flow for each edge to
            HashMap<Integer, ArrayList<Integer>> parentMap = bfs(start);

            for (Integer end : allNodeKeys) {
                MyNode endNode = nodes.get(end);
                if (start != end && endNode.getValue() != Integer.MAX_VALUE) {
                    ArrayList<LinkedList<Integer>> shortestPaths = reconstructPaths(start, end, endNode.getStepReached(), parentMap);
                    calculateFlow(shortestPaths);
                }
            }

        }
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

    void calculateFlow(ArrayList<LinkedList<Integer>> shortestPaths) {

        if (shortestPaths.isEmpty()) return;

        double totalEdges = 0;

        int numPaths = shortestPaths.size();

        if (!shortestPaths.isEmpty()) {
            for (LinkedList<Integer> path : shortestPaths) {

                Object[] pathArray = path.toArray();

                for (int i = 0; i < pathArray.length - 1; i++) {

                    Integer fromNodeNumber = (Integer) pathArray[i];
                    Integer toNodeNumber = (Integer) pathArray[i + 1];

                    if (fromNodeNumber != toNodeNumber) {
                        SimpleEdge edge = new SimpleEdge(fromNodeNumber, toNodeNumber);
                        Double flowSoFar = flow.get(edge);
                        if (flow.containsKey(edge)) {
                            flow.put(edge, flowSoFar + (1.0 / numPaths));
                        } else {
                            flow.put(edge, 1.0 / numPaths);
                        }
                        totalEdges++;
                    }
                }
            }
        }
    }

    HashMap<Integer, HashSet<MyNode>> divideInCommunities(/*int numCommunities,*/ LinkedList<Entry<SimpleEdge, Double>> sortedEdges) {
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

            Entry<SimpleEdge, Double> edgeFlowPair = sortedEdges.get(i);
            SimpleEdge edge = edgeFlowPair.getKey();
            int clusterToRemove = nodes.get(edge.getFrom()).getCluster();
            MyNode fromNode = nodes.get(clusterToRemove);
            MyNode toNode = nodes.get(edge.getTo());

            if (fromNode.getCluster() == toNode.getCluster()) {
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

    public HashMap<Integer, ArrayList<Integer>> bfs(int start) {

        //store this node as key, parent as value
        HashMap<Integer, ArrayList<Integer>> parentMap = new HashMap<Integer, ArrayList<Integer>>();

        //initialize all stepsreached to -1
        for (Integer nodeKey : nodes.keySet()) {
            MyNode n = nodes.get(nodeKey);

            //should be safe to use MAX_Value here.
            // According to documentation, the longest shortest path is 9.
            // See documentation at http://snap.stanford.edu/data/higgs-twitter.html
            n.setStepReached(Integer.MAX_VALUE);
        }

        //initialize stepsreached in the start node to 0
        MyNode startNode = nodes.get(start);
        startNode.setStepReached(0);

        HashSet<Integer> edgesFromNode = edges.get(start);
        HashSet<Integer> visited = new HashSet<Integer>();

        Queue<MyNode> queue = new LinkedList<MyNode>();
        queue.add(startNode);

        while (!queue.isEmpty()) {

            //get the next node from queue & its curr value to be able to get edges
            MyNode curr = queue.poll();
            int currValue = curr.getValue();

            if (!visited.contains(currValue)) {

                visited.add(currValue);
                HashSet<Integer> currToNodes = edges.get(currValue);


                if (currToNodes.isEmpty())
                    continue; //if there are no outgoing edges from this node, pull the next node in queue

                for (Integer toNodeKey : currToNodes) {
                    int stepReached = curr.getStepReached() + 1;
                    MyNode toNode = nodes.get(toNodeKey);

                    //we're only keeping track of parents for shortest paths && not adding the same node to the queue twice
                    //because if it already has a smaller stepReached, there's a shorter path than the one we're inspecting

                    if (stepReached <= toNode.getStepReached()) {  //equals enables us to keep track of multiple shortest paths found in this bfs

                        toNode.setStepReached(stepReached);

                        //put the toNodeKey as the key into the parent Map, and add curr to the list of nodes

                        if (parentMap.get(toNodeKey) == null) {
                            ArrayList<Integer> newListOfParentNodes = new ArrayList<Integer>();
                            parentMap.put(toNodeKey, newListOfParentNodes);
                        }

                        parentMap.get(toNodeKey).add(curr.getValue());
                        queue.add(toNode);
                    }
                }
            }
        }
        return parentMap;


//         for each node n in Graph:
//        n.distance = INFINITY  //initialize visited hashset
//        n.parent = NIL
//
//        create empty queue Q
//
//        root.distance = 0
//        Q.enqueue(root)
//
//        while Q is not empty:
//            current = Q.dequeue()
//            for each node n that is adjacent to current:
//                if n.distance == INFINITY: //if !visited
//                    n.distance = current.distance + 1
//                    n.parent = current
//                    Q.enqueue(n)


    }


    //reconstructing multiple shortest paths found in a single bfs with no end point
    public ArrayList<LinkedList<Integer>> reconstructPaths(int start, int end, int stepReached, HashMap<Integer, ArrayList<Integer>> parentMap) {
        if (parentMap.size() <= 1 || stepReached == Integer.MAX_VALUE) return new ArrayList<LinkedList<Integer>>();

        LinkedList currNodes;
        ArrayList<LinkedList<Integer>> shortestPaths = new ArrayList<LinkedList<Integer>>();

        LinkedList<Integer> endList = new LinkedList<Integer>();
        endList.add(end);
        shortestPaths.add(endList);
        boolean foundStart = false;

        do {
            ArrayList<LinkedList<Integer>> tempResult = new ArrayList<LinkedList<Integer>>();

            for (LinkedList<Integer> singlePath : shortestPaths) {
                int lastNodeAddedToList = singlePath.getFirst();

                if (lastNodeAddedToList == start) {
                    foundStart = true;
                    tempResult = shortestPaths;
                    break;
                }

                ArrayList<Integer> parentsOfNode = parentMap.get(lastNodeAddedToList);

//                if(parentsOfNode == null) break;
                for (Integer parent : parentsOfNode) {
                    LinkedList<Integer> copyOfPathSoFar = new LinkedList<Integer>(singlePath);
                    copyOfPathSoFar.addFirst(parent);
                    tempResult.add(copyOfPathSoFar);
                }


            }
            shortestPaths = tempResult;
        } while (!foundStart);


        return shortestPaths;
    }

    public Comparator<Entry<SimpleEdge, Double>> compareByFlow() {
        return new byFlow();
    }

    private class byFlow implements Comparator<Entry<SimpleEdge, Double>> {

        @Override
        public int compare(Entry<SimpleEdge, Double> o1, Entry<SimpleEdge, Double> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }

    }


    public LinkedList<Entry<SimpleEdge, Double>> sortByFlow(Map<SimpleEdge, Double> unsortedFlowMap) {
        LinkedList<Entry<SimpleEdge, Double>> list = new LinkedList<Entry<SimpleEdge, Double>>(unsortedFlowMap.entrySet());

        Collections.sort(list, compareByFlow());

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
        CapGraphWithNodes cg = new CapGraphWithNodes();
//        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/twitter_higgs.txt");
//        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/course_example_users.txt");
//        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/course_example_users_directed.txt");
        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/directed_graph_single_crossing_edge.txt");
        System.out.println("Graph loaded");
//        cg.getSCCs();
//        cg.bfs(4,677);
//        ArrayList<LinkedList<MyNode>> shortestPaths = cg.bfs(1, 6);
//        System.out.println("Stop... there should've been an error");
        HashMap<Integer, HashSet<MyNode>> communities = cg.findCommunities(2);


        for (Integer clusterNumber : communities.keySet()) {
            HashSet<MyNode> communityMembers = communities.get(clusterNumber);
            int communitySize = communityMembers.size();
            System.out.printf("\nCluster number: %d Community size: %d%n", clusterNumber, communitySize);
            System.out.println("Members:");
            for (MyNode member : communityMembers) {
                System.out.println(member);
            }
        }


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
