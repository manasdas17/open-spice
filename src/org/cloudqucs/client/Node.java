/**
 * 
 */
package org.cloudqucs.client;

import java.util.ArrayList;
import org.cloudqucs.client.SVGCanvas.Circle;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

/**
 * @author Sethu Madhav Bhattiprolu
 * 
 */
public class Node extends Element implements MouseDownHandler{

	//Node Centers are NOT Relative
	Node(int _cx, int _cy) {
		Connections = new ArrayList<Element>();
		cx = _cx;
		cy = _cy;
		// TODO: check String Type and int Type
		Type = ElementType.Node;
	}

	@Override
	public void draw(SVGCanvas s) {
		canvas =s;
		if(!canvas.Nodes.contains(this)){
			c = s.drawCircle(cx, cy, 4);
			s.Nodes.add(this);
		}
		switch(Connections.size()){
		case 1:
			c.attr("fill", "black");
			c.toFront();
			//c.addMouseDownHandler(this);
			break;
		case 2:
			if(Connections.get(0).Type == ElementType.Wire &&
					Connections.get(1).Type == ElementType.Wire){
				c.attr("r","2");
				c.attr("fill","black");
			}
			else{ 
				c.attr("r","2");
				c.attr("fill","black");
			}
			break;
		default:
			c.attr("r","2");
			break;		
		}
		
	}

	// TODO : Associate with some Label.
	public void setName(String _Name) {
		Name = _Name;
	}
	
	public void add(Element e){
		Connections.add(e);
	}

	ArrayList<Element> Connections;
	String Name; // node name used by creation of netlist
	Circle c; // to delete the node we should know the circle
	SVGCanvas canvas;
	

	@Override
	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		if(!canvas.drawingWire)
			canvas.startWire(this);
		else
			canvas.endWire(this);		
	}
	
	public boolean equals(int _cx , int _cy){
		if(cx==_cx && cy == _cy)
			return true;
		else 
			return false;
	}
	

	@Override
	public void translate(int dx, int dy) {
		cx += dx;
		cy += dy;
	}
	
	public void moveto(int x, int y){
		c.attr("cx",""+x+"");
		c.attr("cy",""+y+"");
		cx = x;
		cy = y;
	}

	@Override
	public void Deselect() {
		// TODO Auto-generated method stub
	}

	@Override
	public void Select() {
		// TODO Auto-generated method stub
		
	}
	public void extend(int x , int y){
		
	}

	//Removes the Node
	public void remove() {
		canvas.Nodes.remove(canvas.Nodes.indexOf(this));
		this.c.remove();
	}

	//Removes Element From the Node 
	public void remove(Element e) {
		Connections.remove(Connections.indexOf(e));
	}
	
}
