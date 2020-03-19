package graph;

import cochanges.CoChange;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GraphBuilder {
    private static final String VERTEX_PROPERTY_ID =  "Text";
    private static final String VERTEX_TYPE_FILE = "file";
    private static final String EDGE_TYPE_COCHANGE = "cochange";
    private static final String EDGE_LABEL_WEIGHT = "weight";

    public static void BuildAndPersist(ArrayList<CoChange> coChanges) {
        Graph graph = TinkerGraph.open();
        GraphTraversalSource g = graph.traversal();

        for (CoChange c : coChanges) { //TODO weight = commits
            Vertex vertex1;
            Vertex vertex2;

            // Try to locate vertices for the files if these exist.
            String file1 = c.getFile1();
            GraphTraversal<Vertex, Vertex> file1vertex = g.V().has(VERTEX_PROPERTY_ID, file1);

            String file2 = c.getFile2();
            Traversal<Vertex, Vertex> file2vertex = g.V().has(VERTEX_PROPERTY_ID, file2);

            // Add a vertex for the files if needed.
            vertex1 = file1vertex.hasNext() ? file1vertex.next() : g.addV(VERTEX_TYPE_FILE).property(VERTEX_PROPERTY_ID, file1).next();
            vertex2 = file2vertex.hasNext() ? file2vertex.next() : g.addV(VERTEX_TYPE_FILE).property(VERTEX_PROPERTY_ID, file2).next();

            // Either create a new edge between the files or increase its weight when it already exists.
            g.addE(EDGE_TYPE_COCHANGE).from(vertex1).to(vertex2).property(EDGE_LABEL_WEIGHT, c.getCoVersions().size()).iterate();
            /*var edgeTraversal = g.V(vertex1).outE().inV().filter(v -> v.get().equals(vertex2)).inE();
            Edge edge;
            if (edgeTraversal.hasNext()) { // edge exists => increase value
                edge = edgeTraversal.next();
                // Parse value and increase on edge
                int edgeWeight = Integer.parseInt(edge.property(EDGE_LABEL_WEIGHT).value().toString());
                edge.property(EDGE_LABEL_WEIGHT, ++edgeWeight);
            } else {

            }*/
        }

        try {
            File file = new File("resources/output/test.graphml");
            FileOutputStream fop = new FileOutputStream(file);

            GraphMLWriter.build().create().writeGraph(fop,graph);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
