package jungtest;

import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.ConstantTransformer;

public class TreeViewer {

    Forest<String, Integer> graph;
    Factory<DirectedGraph<String, Integer>> graphFactory = new Factory() {

        public DirectedGraph<String, Integer> create() {
            return new DirectedSparseMultigraph();
        }
    };
    Factory<Tree<String, Integer>> treeFactory = new Factory() {

        public Tree<String, Integer> create() {
            return new DelegateTree(TreeViewer.this.graphFactory);
        }
    };
    Factory<Integer> edgeFactory = new Factory() {

        int i = 0;

        public Integer create() {
            return Integer.valueOf(this.i++);
        }
    };
    Factory<String> vertexFactory = new Factory() {

        int i = 0;

        public String create() {
            return "V" + this.i++;
        }
    };
    VisualizationViewer<String, Integer> vv;
    VisualizationServer.Paintable rings;
    String root;
    TreeLayout<String, Integer> treeLayout;
    RadialTreeLayout<String, Integer> radialLayout;

    public TreeViewer(originalsds.Tree t) {
        this.graph = new DelegateForest();

        createTree(t);

        //this.treeLayout = new JungSugiyama(this.graph);
        this.radialLayout = new RadialTreeLayout(this.graph);
        this.radialLayout.setSize(new Dimension(600, 600));
        //this.vv = new VisualizationViewer(new JungSugiyamaCenter(this.graph, JungSugiyamaCenter.Orientation.TOP, 100, 100), new Dimension(600, 600));
        this.vv = new VisualizationViewer(new TreeLayout(this.graph, 100, 100), new Dimension(600, 600));
        this.vv.setBackground(Color.white);
        this.vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        this.vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

        this.vv.setVertexToolTipTransformer(new ToStringLabeller());
        this.vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));

        DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        this.vv.setGraphMouse(graphMouse);

        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    }

    public VisualizationViewer<String, Integer> getVV() {
        return this.vv;
    }

    private void createTree(originalsds.Tree t) {
        if (t == null) {
            return;
        }
        if (t.getParent() == null) {
            this.graph.addVertex(t.getPart().toString());
        }

        if (t.getRight() != null) {
            this.graph.addEdge(this.edgeFactory.create(), t.getPart().toString(), t.getRight().getPart().toString());
        }
        if (t.getLeft() != null) {
            this.graph.addEdge(this.edgeFactory.create(), t.getPart().toString(), t.getLeft().getPart().toString());
        }
        createTree(t.getLeft());
        createTree(t.getRight());

//        this.graph.addVertex("V0");
//        this.graph.addEdge(this.edgeFactory.create(), "V0", "V1");
//        this.graph.addEdge(this.edgeFactory.create(), "V0", "V2");
//        this.graph.addEdge(this.edgeFactory.create(), "V1", "V4");
//        this.graph.addEdge(this.edgeFactory.create(), "V2", "V3");
//        this.graph.addEdge(this.edgeFactory.create(), "V2", "V5");
//        this.graph.addEdge(this.edgeFactory.create(), "V4", "V6");
//        this.graph.addEdge(this.edgeFactory.create(), "V4", "V7");
//        this.graph.addEdge(this.edgeFactory.create(), "V3", "V8");
//        this.graph.addEdge(this.edgeFactory.create(), "V6", "V9");
//        this.graph.addEdge(this.edgeFactory.create(), "V4", "V10");
    }
}
