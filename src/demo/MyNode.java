package demo;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.util.Random;

public class MyNode extends DefaultDrawableNode{
	
	private String label;
	private String message;
	
	public MyNode() {}
	
	public MyNode(int x,int y,String label) {
		init(x, y, label);
	}
	
	public void init(int x,int y,String label) {
		this.label = label;
		message = null;
		OvalNetworkItem oval = new OvalNetworkItem(x, y);
		oval.setLabel(label);
		setDrawable(oval);
	}
	
	public void makeEdgeTo(DefaultNode node, String type, float strength){
		if(node == null)
			return;
		
		MyEdge edge = new MyEdge(this, node, strength,type);
		addOutEdge(edge);
		node.addInEdge(edge);
		
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static void main(String[] args) {
		MyNode myNode = new MyNode(1,1,"123");
		MyNode myNode2 = new MyNode(2,2,"1234");
		MyNode myNode3 = new MyNode(3,3,"11234");
		myNode.makeEdgeTo(myNode2, "type1", 10.1f);
		myNode.makeEdgeTo(myNode2, "type2", 20.1f);
		HashSet<MyEdge> set = myNode.getEdgesTo(myNode2);
		
		System.out.println(Random.uniform.nextFloatFromTo(0,10));
			
		
	}

}
