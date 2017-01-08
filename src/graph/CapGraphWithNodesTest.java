package graph;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by tigrushka on 30/12/16.
 */
public class CapGraphWithNodesTest {

    CapGraphWithNodes graph;
    CapGraphWithNodes graphOneShortestPath;
    CapGraphWithNodes capGraphWithFourShortestPaths;
    CapGraphWithNodes graphWithTwoCommunities;

    @Before
    public void setUp() throws Exception {
        graph = new CapGraphWithNodes();

        //setup
        //add vertices
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);

        //add edges
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(2, 4);

        ///============================

        //setup
        graphOneShortestPath = new CapGraphWithNodes();

        graphOneShortestPath.addVertex(1);
        graphOneShortestPath.addVertex(2);
        graphOneShortestPath.addVertex(3);
        graphOneShortestPath.addVertex(4);

        //add edges
        graphOneShortestPath.addEdge(1, 2);
        graphOneShortestPath.addEdge(1, 3);
        graphOneShortestPath.addEdge(2, 3);
        graphOneShortestPath.addEdge(2, 4);


        ///============================


        capGraphWithFourShortestPaths = new CapGraphWithNodes();

        //setup
        //add vertices
        capGraphWithFourShortestPaths.addVertex(1);
        capGraphWithFourShortestPaths.addVertex(2);
        capGraphWithFourShortestPaths.addVertex(3);
        capGraphWithFourShortestPaths.addVertex(4);
        capGraphWithFourShortestPaths.addVertex(5);
        capGraphWithFourShortestPaths.addVertex(6);
        capGraphWithFourShortestPaths.addVertex(7);
        capGraphWithFourShortestPaths.addVertex(8);


        //add edges
        capGraphWithFourShortestPaths.addEdge(1, 2);
        capGraphWithFourShortestPaths.addEdge(1, 3);
        capGraphWithFourShortestPaths.addEdge(2, 4);
        capGraphWithFourShortestPaths.addEdge(2, 5);
        capGraphWithFourShortestPaths.addEdge(3, 6);
        capGraphWithFourShortestPaths.addEdge(3, 7);
        capGraphWithFourShortestPaths.addEdge(4, 8);
        capGraphWithFourShortestPaths.addEdge(5, 8);
        capGraphWithFourShortestPaths.addEdge(6, 8);
        capGraphWithFourShortestPaths.addEdge(7, 8);


        ///============================

        graphWithTwoCommunities = new CapGraphWithNodes();

        graphWithTwoCommunities.addVertex(1);
        graphWithTwoCommunities.addVertex(2);
        graphWithTwoCommunities.addVertex(3);
        graphWithTwoCommunities.addVertex(4);
        graphWithTwoCommunities.addVertex(5);
        graphWithTwoCommunities.addVertex(6);
        graphWithTwoCommunities.addVertex(7);

        //add edges in first community
        graphWithTwoCommunities.addEdge(1, 2);
        graphWithTwoCommunities.addEdge(2, 1);
        graphWithTwoCommunities.addEdge(1, 3);
        graphWithTwoCommunities.addEdge(3, 1);
        graphWithTwoCommunities.addEdge(2, 4);
        graphWithTwoCommunities.addEdge(4, 2);
        graphWithTwoCommunities.addEdge(3, 4);
        graphWithTwoCommunities.addEdge(4, 3);

        //connecting edge
        graphWithTwoCommunities.addEdge(3, 5);

        //add edges in the 2nd community
        graphWithTwoCommunities.addEdge(5, 6);
        graphWithTwoCommunities.addEdge(6, 5);
        graphWithTwoCommunities.addEdge(6, 7);
        graphWithTwoCommunities.addEdge(7, 6);
        graphWithTwoCommunities.addEdge(5, 7);
        graphWithTwoCommunities.addEdge(7, 5);


    }

    @Test
    public void testBfs() throws Exception {


        HashMap<Integer, ArrayList<Integer>> parentMap = graph.bfs(1);
        for (Integer key : parentMap.keySet()) {
            System.out.println("\n\nNode: " + key);
            ArrayList<Integer> parents = parentMap.get(key);
            System.out.print("Parents: ");
            for (Integer value : parents) {
                System.out.print(value + ", ");
            }
        }
        assertEquals(1, graph.nodes.get(3).getStepReached());
        assertEquals(2, parentMap.get(4).size());

    }

    @Test
    public void testReconstructPaths() throws Exception {

        //setup
        HashMap<Integer, ArrayList<Integer>> parentMap = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<Integer> listOfParents = new ArrayList<>();
        listOfParents.add(1);
        parentMap.put(2, listOfParents);

        listOfParents = new ArrayList<>();
        listOfParents.add(1);
        parentMap.put(3, listOfParents);

        listOfParents = new ArrayList<>();
        listOfParents.add(2);
        listOfParents.add(3);
        parentMap.put(4, listOfParents);

        ArrayList<LinkedList<Integer>> shortestPaths = graph.reconstructPaths(1, 4, 2, parentMap);

        assertEquals(2, shortestPaths.size());
        Integer firstElementFirstPath = shortestPaths.get(0).get(0);
        Integer firstElementSecondPath = shortestPaths.get(1).get(0);

        int count = 0;
        for (LinkedList<Integer> path : shortestPaths) {

            System.out.println("\n\nPrinting path #: " + count + "\n");
            for (Integer nodeValue : path) {
                System.out.print(nodeValue + ", ");
            }
        }

        assertTrue(1 == firstElementFirstPath && 1 == firstElementSecondPath);
        assertTrue(3 == shortestPaths.get(0).size() && 3 == shortestPaths.get(1).size());
    }

    @Test
    public void testReconstructPathsEmptyParentMap() throws Exception {
        //setup
        HashMap<Integer, ArrayList<Integer>> parentMap = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<LinkedList<Integer>> emptyLinkedList = graph.reconstructPaths(0, 5, 3, parentMap);

        assertTrue(emptyLinkedList.isEmpty());
    }

    @Test
    public void testBfsWithFourShortestPaths() throws Exception {

        HashMap<Integer, ArrayList<Integer>> parentMap = capGraphWithFourShortestPaths.bfs(1);
        for (Integer key : parentMap.keySet()) {
            System.out.println("\n\nNode: " + key);
            ArrayList<Integer> parents = parentMap.get(key);
            System.out.print("Parents: ");
            for (Integer value : parents) {
                System.out.print(value + ", ");
            }
        }

        ArrayList<LinkedList<Integer>> shortestPaths = capGraphWithFourShortestPaths.reconstructPaths(1, 8, 3, parentMap);
        assertEquals(4, shortestPaths.size());
//        assertEquals(2, parentMap.get(4).size());

    }

    @Test
    public void testCalculateFlowTwoPaths() {

        //setup
        ArrayList<LinkedList<Integer>> shortestPaths = new ArrayList<LinkedList<Integer>>();

        LinkedList<Integer> path1 = new LinkedList<>();
        path1.add(1);
        path1.add(2);
        path1.add(4);

        LinkedList<Integer> path2 = new LinkedList<>();
        path1.add(1);
        path1.add(3);
        path1.add(4);

        shortestPaths.add(path1);
        shortestPaths.add(path2);

        //act
        graph.calculateFlow(shortestPaths);
        Double flowAtEdgeTwoFour = graph.flow.get(new SimpleEdge(2, 4));
        assertTrue(0.5 == flowAtEdgeTwoFour);
        assertTrue(0 == graph.flow.get(new SimpleEdge(2,3)));


        //verify

    }


    @Test
    public void testBfsOneNode() throws Exception {


        HashMap<Integer, ArrayList<Integer>> parentMap = graphOneShortestPath.bfs(1);
        for (Integer key : parentMap.keySet()) {
            System.out.println("\n\nNode: " + key);
            ArrayList<Integer> parents = parentMap.get(key);
            System.out.print("Parents: ");
            for (Integer value : parents) {
                System.out.print(value + ", ");
            }
        }
        assertEquals(1, graphOneShortestPath.nodes.get(3).getStepReached());
        assertEquals(1, parentMap.get(4).size());

    }


    @Test
    public void testReconstructPathsWithOneShortestPath() throws Exception {

        //setup
        HashMap<Integer, ArrayList<Integer>> parentMap = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<Integer> listOfParents = new ArrayList<>();
        listOfParents.add(1);
        parentMap.put(2, listOfParents);

        listOfParents = new ArrayList<>();
        listOfParents.add(1);
        parentMap.put(3, listOfParents);

        listOfParents = new ArrayList<>();
        listOfParents.add(2);
        parentMap.put(4, listOfParents);

        ArrayList<LinkedList<Integer>> shortestPaths = graph.reconstructPaths(1, 4, 2, parentMap);

        assertEquals(1, shortestPaths.size());
        Integer firstElementFirstPath = shortestPaths.get(0).get(0);

        int count = 0;
        for (LinkedList<Integer> path : shortestPaths) {

            System.out.println("\n\nPrinting path #: " + count + "\n");
            for (Integer nodeValue : path) {
                System.out.print(nodeValue + ", ");
            }
        }

        assertTrue(3 == shortestPaths.get(0).size());
    }


    @Test
    public void testCalculateFlowOnePath() {

        //setup
        ArrayList<LinkedList<Integer>> shortestPaths = new ArrayList<LinkedList<Integer>>();

        LinkedList<Integer> path1 = new LinkedList<>();
        path1.add(1);
        path1.add(2);
        path1.add(4);


        shortestPaths.add(path1);

        //act
        graphOneShortestPath.calculateFlow(shortestPaths);
        Double flowAtEdgeTwoFour = graphOneShortestPath.flow.get(new SimpleEdge(2, 4));

        //verify
        assertTrue(1 == flowAtEdgeTwoFour);
    }


    @Test
    public void testBfsNoOutgoingEdgesFromNodeTwoToOne() throws Exception {


        HashMap<Integer, ArrayList<Integer>> parentMap = graph.bfs(2);
        for (Integer key : parentMap.keySet()) {
            System.out.println("\n\nNode: " + key);
            ArrayList<Integer> parents = parentMap.get(key);
            System.out.print("Parents: ");
            for (Integer value : parents) {
                System.out.print(value + ", ");
            }
        }

        assertNull(parentMap.get(2));
        assertTrue(2 == parentMap.get(3).get(0) || 3 == parentMap.get(4).get(0));

    }


    @Test
    public void testReconstructPathsNoOutgoingEdgesFromNodeTwoToOne() throws Exception {

        //setup
        HashMap<Integer, ArrayList<Integer>> parentMap = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<Integer> listOfParents = new ArrayList<>();
        listOfParents.add(1);
        parentMap.put(2, listOfParents);

        listOfParents = new ArrayList<>();
        listOfParents.add(1);
        parentMap.put(3, listOfParents);

        listOfParents = new ArrayList<>();
        listOfParents.add(2);
        listOfParents.add(3);
        parentMap.put(4, listOfParents);


        //act
        ArrayList<LinkedList<Integer>> shortestPaths = graph.reconstructPaths(2, 1, Integer.MAX_VALUE, parentMap);

        //verify
        assertEquals(0, shortestPaths.size());

    }

    @Test
    public void sortByFlowTest() {
        //setup
        Map<SimpleEdge, Double> unsortedFlowMap = new HashMap<SimpleEdge, Double>();
        SimpleEdge edge12 = new SimpleEdge(1,2);
        SimpleEdge edge13 = new SimpleEdge(1,3);
        SimpleEdge edge23 = new SimpleEdge(2,3);
        SimpleEdge edge24 = new SimpleEdge(2,4);
        SimpleEdge edge34 = new SimpleEdge(3,4);
        SimpleEdge edge45 = new SimpleEdge(4,5);

        unsortedFlowMap.put(edge12, 0.5);
        unsortedFlowMap.put(edge13, 0.5);
        unsortedFlowMap.put(edge24, 0.5);
        unsortedFlowMap.put(edge34, 0.5);
        unsortedFlowMap.put(edge45, 1.0);


        //act
        LinkedList<Map.Entry<SimpleEdge, Double>> sortedbyFlow = graph.sortByFlow(unsortedFlowMap);

        //verify
        for(Map.Entry<SimpleEdge, Double> entry : sortedbyFlow){
            System.out.printf("From %d to %d Flow (map): %s%n", entry.getKey().getFrom(), entry.getKey().getTo(), entry.getValue());
        }
        assertTrue(sortedbyFlow.getFirst().getValue() == 1.0);
    }

    @Test
    public void testDivideInCommunitiesTwo(){

        //setup
        LinkedList<Map.Entry<SimpleEdge, Double>> edges = new LinkedList<Map.Entry<SimpleEdge, Double>>();
        Map.Entry<SimpleEdge, Double> entry12 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(1,2), 0.5);
        Map.Entry<SimpleEdge, Double> entry31 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(3,1), 0.5);
        Map.Entry<SimpleEdge, Double> entry24 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(2,4), 0.5);
        Map.Entry<SimpleEdge, Double> entry34 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(3,4), 0.5);
        Map.Entry<SimpleEdge, Double> entry56 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(5,6), 0.5);
        Map.Entry<SimpleEdge, Double> entry57 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(5,7), 0.5);
        Map.Entry<SimpleEdge, Double> entry67 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(6,7), 0.5);


        edges.add(entry12);
        edges.add(entry31);
        edges.add(entry24);
        edges.add(entry34);
        edges.add(entry56);
        edges.add(entry57);
        edges.add(entry67);

        //act
        HashMap<Integer, HashSet<MyNode>> communities = graphWithTwoCommunities.divideInCommunities(edges);

        for (Integer key : communities.keySet()) {
            System.out.println("\n\nCommunity #: " + key);
            HashSet<MyNode> members = communities.get(key);
            System.out.print("Members: ");
            for (MyNode value : members) {
                System.out.print(value + ", ");
            }
        }

        //assert
        assertEquals(2, communities.keySet().size());
    }

    @Test
    public void testFindCommunities() throws Exception {
        //setup done in constructor

        //act
        HashMap<Integer, HashSet<MyNode>> communities = graphWithTwoCommunities.findCommunities(2);

        //assert
        assertEquals(2, communities.size());
    }

    @Test
    public void testContractEdgesWithMaxFlow() throws Exception {
//        //setup
//        LinkedList<Map.Entry<SimpleEdge, Double>> edges = new LinkedList<Map.Entry<SimpleEdge, Double>>();
//        Map.Entry<SimpleEdge, Double> entry35 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(3,5), 2.0);
//        Map.Entry<SimpleEdge, Double> entry12 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(1,2), 0.5);
//        Map.Entry<SimpleEdge, Double> entry31 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(3,1), 0.5);
//        Map.Entry<SimpleEdge, Double> entry24 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(2,4), 0.5);
//        Map.Entry<SimpleEdge, Double> entry34 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(3,4), 0.5);
//        Map.Entry<SimpleEdge, Double> entry56 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(5,6), 0.5);
//        Map.Entry<SimpleEdge, Double> entry57 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(5,7), 0.5);
//        Map.Entry<SimpleEdge, Double> entry67 = new AbstractMap.SimpleEntry<SimpleEdge, Double>(new SimpleEdge(6,7), 0.5);
//
//
//        edges.add(entry12);
//        edges.add(entry31);
//        edges.add(entry24);
//        edges.add(entry34);
//        edges.add(entry56);
//        edges.add(entry57);
//        edges.add(entry67);
//        edges.add(entry35);

        System.out.println("================== Before contraction ==================");
        HashMap<Integer, HashSet<Integer>> edgesInGraph = graphWithTwoCommunities.edges;
        for(Integer key : edgesInGraph.keySet()){
            System.out.print("\nKey: " + key + " Values: ");
            for (Integer value: edgesInGraph.get(key)) {
                System.out.print(value + ", ");
            }
        }

        //act
        graphWithTwoCommunities.calculateBetweenness();
        LinkedList<Map.Entry<SimpleEdge, Double>> sortedEdges = graphWithTwoCommunities.sortByFlow(graphWithTwoCommunities.flow);
        graphWithTwoCommunities.contractEdgesWithMaxFlow(sortedEdges);


        System.out.println("\n\n================== After contraction ==================");
        HashMap<Integer, HashSet<Integer>> edgesInGraphAfter = graphWithTwoCommunities.edges;
        for(Integer key : edgesInGraphAfter.keySet()){
            System.out.print("\nKey: " + key + "Values: ");
            for (Integer value: edgesInGraphAfter.get(key)) {
                System.out.print(value + ", ");
            }
        }

        //verify
        assertTrue(!edgesInGraph.get(3).contains(5));
    }
}