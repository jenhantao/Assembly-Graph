/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package viewers;

/**
 *
 * @author vvasilev
 */
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import org.apache.commons.collections15.Factory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import newsds.datastructures.SDSBinaryGraph;
import newsds.datastructures.SDSJointBinaryForest;

public class BinaryGraphViewer {

    Graph<String, Number> graph;
    VisualizationViewer<String, Number> vv;
    Map<String, String[]> map = new HashMap();
    List<String> cityList;
    SDSJointBinaryForest _jbf;

    public BinaryGraphViewer(SDSJointBinaryForest jbf) {
        _jbf = jbf;
        this.graph = new DirectedSparseMultigraph<String, Number>();

        for (SDSBinaryGraph graph : jbf.getGraphs()) {
            createGraph(graph);
        }

        int count = jbf.getGraphs().size();

        Dimension layoutSize = new Dimension(600, 600);

        Layout layout = new JungSugiyamaCenter(this.graph,
                JungSugiyamaCenter.Orientation.TOP, 300, 48 * (count + 10));

        layout.setSize(layoutSize);
        this.vv = new VisualizationViewer(layout, new Dimension(800, 400));
        this.vv.setBackground(Color.white);

        AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        this.vv.setGraphMouse(graphMouse);

        this.vv.addKeyListener(graphMouse.getModeKeyListener());
        this.vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        this.vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

    }

    public void setBackground(Color color) {
        this.vv.setBackground(color);
    }

    public VisualizationViewer<String, Number> getVV() {
        return this.vv;
    }
    Factory<Integer> edgeFactory = new Factory() {

        int i = 0;

        public Integer create() {
            return Integer.valueOf(this.i++);
        }
    };

    private void createGraph(SDSBinaryGraph t) {
        if (t == null) {
            return;
        }
        if (t.getParentCount() == 0) {
            this.graph.addVertex(t.getNode().getPart().toString());
        }
        if (t.getRight() != null) {
            this.graph.addEdge(this.edgeFactory.create(),
                    t.getNode().getPart().toString(),
                    t.getRight().getNode().getPart().toString());
        }

        if (t.getLeft() != null) {
            this.graph.addEdge(this.edgeFactory.create(),
                    t.getNode().getPart().toString(),
                    t.getLeft().getNode().getPart().toString());
        }
        createGraph(t.getLeft());
        createGraph(t.getRight());
    }

    public Graph getGraph() {
        return graph;
    }
}
