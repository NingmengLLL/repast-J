package demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.AbstractGraphLayout;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.RandomGraphLayout;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.util.Random;

public class MyModel extends SimModelImpl{

	private int numNodes = 25;
	private ArrayList<MyNode> agentList = new ArrayList<>();
	private int worldXSize = 500;
	private int worldYSize = 500;
	private int initialSteps = 1;
	private double linkProb = 0.05;
	private double typeProb = 0.5;
	private int nodeIndex = -1; //当前时间步传递信息的节点
	private boolean[] hasTransmitted = new boolean[numNodes];
	
	private Schedule schedule;
	private AbstractGraphLayout graphLayout;
	private DisplaySurface surface;
	private BasicAction initialAction;
	
	
	public MyModel() {
		Vector<String> vect = new Vector<String>();
		vect.add("Random");
		ListPropertyDescriptor pd = new ListPropertyDescriptor("LayoutType", vect);
		descriptors.put("LayoutType", pd);
	}
	
	
	@Override
	public void setup() {
		Random.createUniform();
	    if (surface != null) 
	    	surface.dispose();
	    
	    surface = null;
	    schedule = null;

	    System.gc();

	    surface = new DisplaySurface(this, "MyModel Display");
	    registerDisplaySurface("Main Display", surface);
	    schedule = new Schedule();
	    agentList = new ArrayList<>();
	    worldXSize = 500;
	    worldYSize = 500;
		
	}
	
	@Override
	public void begin() {
		buildModel();
		buildDisplay();
		buildSchedule();
		surface.display();
		
	}
	
	public void buildModel() {
		
		System.out.println("building model");
		for (int n = 0; n < numNodes; n++) {
			int x = Random.uniform.nextIntFromTo(0, worldXSize - 1);
		    int y = Random.uniform.nextIntFromTo(0, worldYSize - 1);
		    agentList.add(new MyNode(x, y, String.valueOf(n)));
		}
		
		for(int i=0;i<numNodes;i++) {
			hasTransmitted[i]=true;
		}
	}
	
	public void buildSchedule() {
		
		System.out.println("building schedule");
		initialAction = schedule.scheduleActionAt(1, this, "initialAction");
		schedule.scheduleActionAt(initialSteps,this, "removeInitialAction",Schedule.LAST);
		schedule.scheduleActionBeginning(initialSteps + 1, this, "mainAction");
	}
	
	public void buildDisplay() {
		
		System.out.println("building display");
		
		graphLayout = new RandomGraphLayout(agentList, worldXSize, worldYSize);
		
		Network2DDisplay display = new Network2DDisplay(graphLayout);
		surface.addDisplayableProbeable(display, "My Display");
		surface.setBackground(Color.white);
		addSimEventListener(surface);
	}
	
	public void initialAction(){
		graphLayout.updateLayout();
		createInitialLinks();
		surface.updateDisplay();
	}
	
	
	public void removeInitialAction(){
		schedule.removeAction(initialAction);
	}
	
	
	public void mainAction() {
		
		System.out.println(this.getTickCount()+"-------------------------------------");
		if(nodeIndex==-1) {
			nodeIndex = (int)(Math.random()*25);
			hasTransmitted[nodeIndex] = false;
			MyNode node = agentList.get(nodeIndex);
			node.setMessage("New Message!!!!!!!!!!!!");
			transmitMessage(node);
		}
		else {
			hasTransmitted[nodeIndex] = false;
			MyNode node = agentList.get(nodeIndex);
			transmitMessage(node);
		}
	}
	
	public void createInitialLinks() {
		
		//随机生成type1的link
		for(int i = 0 ; i < agentList.size()-1 ; i++){
			MyNode node = (MyNode) agentList.get(i);
			for(int j = i+1 ; j < agentList.size() ; j++){
				DefaultNode otherNode = (DefaultNode) agentList.get(j);
				if(!(node == otherNode)){
					if(Random.uniform.nextDoubleFromTo(0,1) < linkProb){
						if(Random.uniform.nextDoubleFromTo(0,1) < 1)
							node.makeEdgeTo(otherNode,"type1", (float)(Math.random()*10));
						else
							node.makeEdgeTo(otherNode,"type2", (float)(Math.random()*10));
					}
				}
			}
		}
		
		//随机生成type2的link
		for(int i = 0 ; i < agentList.size()-1 ; i++){
			MyNode node = (MyNode) agentList.get(i);
			for(int j = i ; j < agentList.size() ; j++){
				DefaultNode otherNode = (DefaultNode) agentList.get(j);
				if(!(node == otherNode)){
					if(Random.uniform.nextDoubleFromTo(0,1) < linkProb){
						if(Random.uniform.nextDoubleFromTo(0,1) >1)
							node.makeEdgeTo(otherNode,"type1", (float)(Math.random()*10));
						else
							node.makeEdgeTo(otherNode,"type2", (float)(Math.random()*10));
					}
				}
			}
		}
	}
	
	public void transmitMessage(MyNode myNode) {
		
		int index = Integer.parseInt(myNode.getLabel());
		int len = agentList.size();
		
		Float[] weight = new Float[agentList.size()];  //记录与各个节点的权值
		for(int i=0;i<len;i++) {
			weight[i]=0.0f;
		}
		
		//遍历入节点
		for(int i=0;i<index;i++) {
			MyNode tempNode = agentList.get(i);
			Float totalWeight = 0.0f;
			if(myNode.hasEdgeFrom(tempNode)) {
				HashSet<MyEdge> set = myNode.getEdgesFrom(tempNode);
				for(MyEdge myEdge:set) {
					if(myEdge.getType()=="type1")
						totalWeight += (float) myEdge.getStrength()*0.3f;
					else 
						totalWeight += (float) myEdge.getStrength()*0.7f;
				}
			}
			if(hasTransmitted[i])
				weight[i] = totalWeight;
		}
		
		//遍历出节点
		for(int i=index+1;i<len-1;i++) {
			MyNode tempNode = agentList.get(i);
			Float totalWeight = 0.0f;
			if(myNode.hasEdgeTo(tempNode)) {
				HashSet<MyEdge> set = myNode.getEdgesTo(tempNode);
				for(MyEdge myEdge:set) {
					if(myEdge.getType()=="type1")
						totalWeight += (float) myEdge.getStrength()*0.3f;
					else 
						totalWeight += (float) myEdge.getStrength()*0.7f;
				}
			}
			if(hasTransmitted[i])
				weight[i] = totalWeight;
		}
		
		
		float maxWeight = Collections.max(Arrays.asList(weight));
		
		int nextIndex = -1;
		for(int i=0;i<len;i++) {
			if(weight[i]==maxWeight){
				nextIndex = i;
				weight[i] = 0.0f;
			}
		}
		
		MyNode node = agentList.get(nextIndex);
		node.setMessage(myNode.getMessage()); 
		System.out.println("Node"+myNode.getLabel()+" tranmit message to Node"+nextIndex);
		nodeIndex = nextIndex;
		
	}

	@Override
	public String[] getInitParam() {
		
		String[] params = {"numNodes","worldXSize", "worldYSize","LayoutType", "LinkProb"};
	    return params;
		
	}

	@Override
	public String getName() {
		
		return "MyModel";
	}

	@Override
	public Schedule getSchedule() {
		return schedule;
	}
	
	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public int getWorldXSize() {
		return worldXSize;
	}

	public void setWorldXSize(int worldXSize) {
		this.worldXSize = worldXSize;
	}

	public int getWorldYSize() {
		return worldYSize;
	}

	public void setWorldYSize(int worldYSize) {
		this.worldYSize = worldYSize;
	}

	public double getLinkProb() {
		return linkProb;
	}

	public void setLinkProb(double linkProb) {
		this.linkProb = linkProb;
	}

	public AbstractGraphLayout getGraphLayout() {
		return graphLayout;
	}

	public void setGraphLayout(AbstractGraphLayout graphLayout) {
		this.graphLayout = graphLayout;
	}

	public static void main(String[] args) {
		SimInit simInit = new SimInit();
		MyModel model = new MyModel();
		simInit.loadModel(model, null, false);
	}

	

}
