/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jungtest;

/**
 *
 * @author vvasilev
 */

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldMapGraphDemo
{
  Graph<String, Number> graph;
  VisualizationViewer<String, Number> vv;
  Map<String, String[]> map = new HashMap();
  List<String> cityList;

  public WorldMapGraphDemo()
  {
    this.graph = new DirectedSparseMultigraph<String, Number>();
    createVertices();
    createEdges();

    Dimension layoutSize = new Dimension(600, 600);

    Layout layout = new JungSugiyamaCenter(this.graph, JungSugiyamaCenter.Orientation.TOP, 100, 50);//, new ChainedTransformer(new Transformer[] { new CityTransformer(this.map), new LatLonPixelTransformer(new Dimension(2000, 1000)) }));

    layout.setSize(layoutSize);
    this.vv = new VisualizationViewer(layout, new Dimension(800, 400));
    this.vv.setBackground(Color.white);

    AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse();
    this.vv.setGraphMouse(graphMouse);

    this.vv.addKeyListener(graphMouse.getModeKeyListener());
        this.vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        this.vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

  }

  public VisualizationViewer<String, Number> getVV() {
    return this.vv;
  }

  private void createVertices()
  {
//    for (String city : this.map.keySet())
//      this.graph.addVertex(city);
      this.graph.addVertex("V0");
  }

  void createEdges()
  {
//    for (int i = 0; i < this.map.keySet().size() * 1.3D; i++)
//      this.graph.addEdge(new java.lang.Double(Math.random()), randomCity(), randomCity(), EdgeType.DIRECTED);
      this.graph.addEdge(1, "V0", "V1", EdgeType.DIRECTED);
      this.graph.addEdge(2, "V1", "V3", EdgeType.DIRECTED);
      this.graph.addEdge(3, "V3", "V4", EdgeType.DIRECTED);
      this.graph.addEdge(4, "V4", "V5", EdgeType.DIRECTED);
      this.graph.addEdge(5, "V4", "V12", EdgeType.DIRECTED);
      this.graph.addEdge(6, "V12", "V13", EdgeType.DIRECTED);
      this.graph.addEdge(7, "V13", "V9", EdgeType.DIRECTED);
      this.graph.addEdge(8, "V13", "V14", EdgeType.DIRECTED);
      this.graph.addEdge(9, "V14", "V15", EdgeType.DIRECTED);
      this.graph.addEdge(10, "V15", "V16", EdgeType.DIRECTED);
      this.graph.addEdge(11, "V9", "V10", EdgeType.DIRECTED);
      this.graph.addEdge(12, "V10", "V11", EdgeType.DIRECTED);
      this.graph.addEdge(13, "V5", "V6", EdgeType.DIRECTED);
      this.graph.addEdge(14, "V6", "V7", EdgeType.DIRECTED);
      this.graph.addEdge(15, "V1", "V17", EdgeType.DIRECTED);
      this.graph.addEdge(16, "V17", "V8", EdgeType.DIRECTED);
      this.graph.addEdge(17, "V8", "V9", EdgeType.DIRECTED);
      this.graph.addEdge(18, "V8", "V5", EdgeType.DIRECTED);

  }


//  public static void main(String[] args)
//  {
//    JFrame frame = new JFrame();
//    Container content = frame.getContentPane();
//    content.add(new WorldMapGraphDemo());
//    frame.pack();
//    frame.setDefaultCloseOperation(3);
//    frame.setVisible(true);
//  }

}