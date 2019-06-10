package demo;

import java.awt.Color;

import uchicago.src.sim.gui.DrawableEdge;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.Node;

public class MyEdge extends DefaultEdge implements DrawableEdge{
	
	private Color color;
	//private String type;
	
	public MyEdge() {}
	
	public MyEdge(Node from, Node to,  float strength,String type) {
		super(from, to, "",strength);
		super.type = type;
		
		switch (type) {
		case "type1":
			this.color = Color.RED;
			break;
		case "type2":
			this.color = Color.yellow;
		default:
			break;
		}
		
		
	}
	
	public void setColor(Color c){
		color = c;
	}

	public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY){
		g.drawDirectedLink(color, fromX, toX, fromY, toY);
		
	}
	
	
}
