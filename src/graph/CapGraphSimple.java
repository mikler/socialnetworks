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
public class CapGraphSimple implements Graph {

	HashSet<Integer> nodes = new HashSet<Integer>();
    HashMap<Integer, HashSet<Integer>> edges = new HashMap<Integer, HashSet<Integer>>();
    List<Graph> SCCs;
    HashMap<Integer, Integer> flow = new HashMap<Integer, Integer>();
	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 */
	@Override
	public void addVertex(int num) {
        nodes.add(num);
        if(!edges.keySet().contains(num)){
            edges.put(num, new HashSet<Integer>());
        }
	}

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */
	@Override
	public void addEdge(int from, int to) {

        if(!edges.keySet().contains(from)){
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.add(to);
            edges.put(from, hs);
        }

        else{
            HashSet<Integer> hs = edges.get(from);
            hs.add(to);
            edges.put(from, hs);
        }
	}

	/* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 */
	@Override
	public Graph getEgonet(int center) {
        CapGraphSimple cg = new CapGraphSimple();
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
        for(Integer node : nodes){
            vertices.push(node);
        }
        //do depth first search
        SCCs = new ArrayList<Graph>();
        Stack<Integer> finished = dfs(this, vertices);

        //compute the transpose
        CapGraphSimple transpose = computeTranspose();
        //do dfs in reverse order. Each tree returned is a SCC

        SCCs = new ArrayList<Graph>();
        Stack<Integer> result = dfs(transpose, finished);



		// TODO Auto-generated method stub
		return SCCs;
	}


    private Stack<Integer> dfs(CapGraphSimple graph, Stack<Integer> vertices){ //// TODO: 15/09/16
        HashSet<Integer> visited = new HashSet<Integer>();
        Stack<Integer> finished = new Stack<Integer>();


        while(!vertices.isEmpty()){
            Integer v = vertices.pop();
            if(!visited.contains(v)){
                Graph scc = new CapGraphSimple();
                SCCs.add(scc);
                scc.addVertex(v);
                dfsVisit(graph, v, visited, finished, scc);
            }
        }

        return finished;
    }

    private void dfsVisit(CapGraphSimple graph, Integer v, HashSet<Integer> visited, Stack<Integer> finished, Graph scc) {

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

    private CapGraphSimple computeTranspose(){
        CapGraphSimple cg = new CapGraphSimple();
        for(Integer node : nodes){
            cg.addVertex(node);
            HashSet<Integer> es = edges.get(node);
            for(Integer e : es){
                cg.addEdge(e, node);
            }
        }

        return cg;
    }


    //todo overall method to compute communities
    public List<Graph> findCommunities(){
        List<Graph> result = new LinkedList<Graph>();

        return result;
    }

    //todo do a breadth first search algorithm to find all the shortest paths and distribute flow between them
    public LinkedList<Integer> bfs(int start, int goal){ //not sure about the return type

        //implemented in CapGraphSimpleWithNodes

        return new LinkedList<Integer>();
    }


    //todo write a method to reconstruct path(s) based on the parent HashMap


    /* (non-Javadoc)
     * @see graph.Graph#exportGraph()
     */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		return edges;
	}

    public static void main(String[] args) {
        CapGraphSimple cg = new CapGraphSimple();
        GraphLoader.loadGraph(cg, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/course_example_users.txt");
        System.out.println("Graph loaded");
        cg.getSCCs();
        System.out.println("Mission accomlished");

    }
}
