/**
 * @author UCSD MOOC development team
 * 
 * Utility class to add vertices and edges to a graph
 *
 */
package util;

import graph.CapGraph;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GraphLoader {
    /**
     * Loads graph with data from a file.
     * The file should consist of lines with 2 integers each, corresponding
     * to a "from" vertex and a "to" vertex.
     */ 
    public static void loadGraph(graph.Graph g, String filename) {
        Set<Integer> seen = new HashSet<Integer>();
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // Iterate over the lines in the file, adding new
        // vertices as they are found and connecting them with edges.
        while (sc.hasNextInt()) {
            int v1 = sc.nextInt();
            int v2 = sc.nextInt();
            if (!seen.contains(v1)) {
                g.addVertex(v1);
                seen.add(v1);
            }
            if (!seen.contains(v2)) {
                g.addVertex(v2);
                seen.add(v2);
            }
            g.addEdge(v1, v2);
        }
        
        sc.close();
    }

    public static void loadGraphWithEdges(CapGraph g, String filename) {
        Set<Integer> seen = new HashSet<Integer>();
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // Iterate over the lines in the file, adding new
        // vertices as they are found and connecting them with edges.
        while (sc.hasNextInt()) {
            int v1 = sc.nextInt();
            int v2 = sc.nextInt();
            long time = sc.nextLong();
            String activity = sc.next();
            if (!seen.contains(v1)) {
                g.addVertex(v1);
                seen.add(v1);
            }
            if (!seen.contains(v2)) {
                g.addVertex(v2);
                seen.add(v2);
            }
            g.addEdge(v1, v2, time, activity);
        }

        sc.close();
    }

    public static void loadGraphWithEdgesBeforeAndAfter(CapGraph[] graphs, String filename) {
        long timeOfDiscovery = 1341360000;
        Set<Integer> seen = new HashSet<Integer>();
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // Iterate over the lines in the file, adding new
        // vertices as they are found and connecting them with edges.
        while (sc.hasNextInt()) {
            int v1 = sc.nextInt();
            int v2 = sc.nextInt();
            long time = sc.nextLong();
            String activity = sc.next();
            CapGraph before = graphs[0];
            CapGraph after = graphs[1];
                if (!seen.contains(v1)) {
                    before.addVertex(v1);
                    after.addVertex(v1);
                    seen.add(v1);
                }
            if (!seen.contains(v2)) {
                before.addVertex(v2);
                after.addVertex(v2);
                seen.add(v2);
            }
            if (time < timeOfDiscovery) {
                before.addEdge(v1, v2, time, activity);
            }
            else{
                after.addEdge(v1, v2, time, activity);
            }
        }

        sc.close();
    }
}
