package graph;

import java.io.*;
import java.util.*;

/**
 * Created by tigrushka on 19/10/16.
 */
public class Utils {

    public void WriteToAFile(HashMap<Integer, HashSet<MyNode>> communities, String FileName) throws IOException {
        Iterator<Map.Entry<Integer, HashSet<MyNode>>> it = communities.entrySet().iterator();


        FileWriter fstream;
        BufferedWriter out;

        // create your filewriter and bufferedreader
        fstream = new FileWriter(FileName);
        out = new BufferedWriter(fstream);

        // then use the iterator to loop through the map, stopping when we reach the
        // last record in the map
        for(Integer fromNodeKey : communities.keySet()){
            out.write(fromNodeKey + "\n");
            for (MyNode node : communities.get(fromNodeKey)){
                out.write(node.getValue() + " ");
            }
            out.write("\n");
        }
        // lastly, close the file and end
        out.close();
    }

    public HashMap<Integer, HashSet<MyNode>> readFromAFile(String File) throws FileNotFoundException {
        HashMap<Integer, HashSet<MyNode>> communities = new HashMap<Integer, HashSet<MyNode>>();
        Scanner sc = new Scanner(new File(File));
        Scanner sc2;
        while(sc.hasNext()){
            //read community id
             int communityID = sc.nextInt();
            String communitiesAsString = sc.nextLine();
            communitiesAsString = sc.nextLine();

            //load the HasSet of Nodes
            HashSet<MyNode> toNodes = new HashSet<MyNode>();
            sc2 = new Scanner(communitiesAsString);
            while(sc2.hasNext()){
                toNodes.add(new MyNode(sc2.nextInt()));
            }
            communities.put(communityID, toNodes);
            sc2.close();
        }
        sc.close();
        return communities;
    }
}
