package org.cloudqucs.client;
/**
 * @author Sethu Madhav Bhattiprolu
 * 
 */
public abstract class Element {

	public abstract void draw(SVGCanvas s);
	void setCenter(int _x, int _y){
		cx=_x;
		cy=_y;
	}
	public abstract void translate(int dx, int dy);
	public abstract void Select();
	public abstract void Deselect();
	boolean isSelected;

	public enum ElementType{
		Component        ,
		ComponentText    ,
		AnalogComponent  ,
		DigitalComponent ,

		Graph            ,
		Node             ,
		Marker           ,
		Wire             ,

		Painting         ,
		PaintingResize   ,

		Label            ,
		HWireLabel       ,
		VWireLabel       ,
		NodeLabel        ,
		MovingLabel      ,
		HMovingLabel     ,
		VMovingLabel     ,

		Diagram          ,
		DiagramResize    ,
		DiagramHScroll   ,
		DiagramVScroll   
	}
	ElementType Type; // whether it is Component, Wire, ...
	int cx, cy, x1, y1, x2, y2; // center and relative boundings
	boolean horizontal; // orientation.
}
