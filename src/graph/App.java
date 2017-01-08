package graph;

import util.GraphLoader;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by tigrushka on 15/10/16.
 */
public class App {


    public static void main(String[] args) {

        Utils utils = new Utils();
        //load connections between users
        CapGraphWithNodes connections = new CapGraphWithNodes();
        GraphLoader.loadGraph(connections, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/twitter_higgs.txt");
//        GraphLoader.loadGraph(connections, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/course_example_users.txt");

//        //load twitter interactions
//        CapGraph interactionsBefore = new CapGraph();
//        CapGraph interactionsAfter = new CapGraph();
//        CapGraph[] interactionGraphs = new CapGraph[] {interactionsBefore, interactionsAfter};
//        GraphLoader.loadGraphWithEdgesBeforeAndAfter(interactionGraphs, "/Users/tigrushka/Documents/learning/programming/java/JavaCapstone/SocialNetworks/data/higgs-activity_time.txt");
//
//        //initialize values in nodes
//        for(CapGraph interactions : interactionGraphs) {
//            interactions.initializeOutcomingNeighbors();
//            interactions.calculateEngagementRatios();
//        }

        //calculate communities within the connections graph
        HashMap<Integer, HashSet<MyNode>> communities = connections.findCommunities(2);


        //write communities to a file for future use
        try {
            utils.WriteToAFile(communities, "communities.txt");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        HashMap<Integer, HashSet<MyNode>> loadedCommunities = new HashMap<Integer, HashSet<MyNode>>();
        //read communities from a file
        try {
            loadedCommunities = utils.readFromAFile("communities.txt");
//            System.out.println("Why!?");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        System.out.println("Done!");
    }
}
