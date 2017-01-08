package graph;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by tigrushka on 30/12/16.
 */
public class CapGraphWithNodesBackupTest {

    CapGraphWithNodesBackup graph;
    @Before
    public void setUp() throws Exception {
        graph = new CapGraphWithNodesBackup();

    }


    @Test
    public void testBfs() throws Exception {

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

        HashMap<Integer, ArrayList<MyNode>> parentMap = graph.bfs(1);
        for(Integer key: parentMap.keySet()){
            System.out.println("\n\nNode: " + key);
            ArrayList<MyNode> myNodes = parentMap.get(key);
            System.out.print("Parents: ");
            for(MyNode value : myNodes){
                System.out.print(value + ", ");
            }
        }
        assertEquals(1, graph.nodes.get(3).getStepReached());
        assertEquals(2, parentMap.get(4).size());
    }
}