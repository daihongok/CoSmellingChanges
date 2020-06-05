package Model;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLReader;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Represents a graph of packages and their smells.
 * Typically constructed from the output of ASTracker.
 */
public class SmellGraph {

    private Graph graph;
    private GraphTraversalSource g;

    // Only allow creation via build methods.
    private SmellGraph() {}

    /**
     * Creates a graph from a .graphML file.
     * @param file Path to a file, including the file itself.
     * @return Graph constructed from the data in the file.
     */
    public static SmellGraph buildFromFile(String file) {
        Graph graph = TinkerGraph.open();
        GraphTraversalSource g = graph.traversal();

        try {
            FileInputStream graphFileInputStream = new FileInputStream(file);

            GraphMLReader.build().create().readGraph(graphFileInputStream, graph);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SmellGraph smellGraph = new SmellGraph();
        smellGraph.setGraph(graph);
        smellGraph.setG(g);

        return smellGraph;
    }


    /* Getters and Setters */

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setG(GraphTraversalSource g) {
        this.g = g;
    }

    public GraphTraversalSource getG() {
        return g;
    }
}
