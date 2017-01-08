package util;

import graph.CapGraph;
import graph.CapGraphWithNodes;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tigrushka on 07/01/17.
 */
public class GraphLoaderTest {

    @Test
    public void testLoadGraphWithEdgesBeforeAndAfter() throws Exception {
        CapGraph interactionsBefore = new CapGraph();
        CapGraph interactionsAfter = new CapGraph();
        CapGraph[] interactionGraphs = new CapGraph[] {interactionsBefore, interactionsAfter};

        //act
        GraphLoader.loadGraphWithEdgesBeforeAndAfter(interactionGraphs, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/higgs-activity_time-3before-4after.txt");

        //verify
        CapGraph loadedInteractionsBefore = interactionGraphs[0];
        CapGraph loadedInteractionsAfter = interactionGraphs[1];
        assertTrue(loadedInteractionsBefore.numEdges() == 3);
        assertTrue(loadedInteractionsAfter.numEdges() == 4);

    }
}