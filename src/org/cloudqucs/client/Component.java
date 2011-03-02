package org.cloudqucs.client;

import java.util.ArrayList;


import org.cloudqucs.client.SVGCanvas.Rect;
import org.cloudqucs.client.Dialogs.PropertiesDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.hydro4ge.raphaelgwt.client.Raphael.Set;
import com.hydro4ge.raphaelgwt.client.Raphael.Text;

/**
 * @author Sethu Madhav Bhattiprolu
 * 
 */
public class Component extends Element implements MouseDownHandler,
		MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler {

	public String Model, Name, Description;
	Rect boundingBox;
	ArrayList<Line> Lines;
	ArrayList<Arc> Arcs;
	ArrayList<Area> Rects;
	ArrayList<Port> Ports;
	public ArrayList<Property> Props;
	Set Shapes;
	SVGCanvas canvas;
	int rotated;
	int tx, ty, tdx, tdy;
	public boolean showName;

	class Line {
		int x1, y1, x2, y2;

		public Line(int _x1, int _y1, int _x2, int _y2) {
			x1 = _x1;
			y1 = _y1;
			x2 = _x2;
			y2 = _y2;
		}
	}

	class Arc {
		int x1, y1, x2, y2, rx, ry, x_rotation, large_arc_flag, sweep_flag;

		public Arc(int _x1, int _y1, int _x2, int _y2, int _rx, int _ry,
				int _x_rotation, int _large_arc_flag, int _sweep_flag) {
			x1 = _x1;
			y1 = _y1;
			x2 = _x2;
			y2 = _y2;
			rx = _rx;
			ry = _ry;
			x_rotation = _x_rotation;
			large_arc_flag = _large_arc_flag;
			sweep_flag = _sweep_flag;
		}
	}

	class Port {
		int x, y;
		Node Connection;

		public Port(int _x, int _y) {
			x = _x;
			y = _y;
			Connection = new Node(cx + x, cy + y);
		}

		public void moveto(int _x, int _y) {
			Connection.moveto(x + _x, y + _y);
		}

		public void translate(int dx, int dy) {
			Connection.moveto(cx + x + dx, cy + y + dy);
		}

		public void draw(SVGCanvas s) {
			Connection.draw(s);
		}
	}

	class Area {
		int x, y, w, h;

		public Area(int _x, int _y, int _w, int _h) {
			x = _x;
			y = _y;
			w = _w;
			h = _h;
		}
	}

	public class Property {
		public String Name, Value, Description;
		public boolean display;
		public String oldValue;

		public Property(String Name, String Value, boolean display,
				String Description) {
			this.Name = Name;
			this.Value = Value;
			this.display = display;
			this.Description = Description;
		}
	}

	public Component() {
		Type = ElementType.Component;
		Lines = new ArrayList<Line>();
		Props = new ArrayList<Property>();
		Ports = new ArrayList<Port>();
		Arcs = new ArrayList<Arc>();
		Rects = new ArrayList<Area>();
		horizontal = true;
		rotated = 0;
		showName = true;
	}

	public void draw(SVGCanvas s) {
		canvas = s;
		Shapes = s.new Set();
		for (Line line : Lines) {
			Shapes.push(s.drawLine(this.cx + line.x1, this.cy + line.y1,
					this.cx + line.x2, this.cy + line.y2));
		}
		for (Arc arc : Arcs) {
			Shapes.push(s.drawArc(this.cx + arc.x1, this.cy + arc.y1, this.cx
					+ arc.x2, this.cy + arc.y2, arc.rx, arc.ry, arc.x_rotation,
					arc.large_arc_flag, arc.sweep_flag));
		}
		for (Area rect : Rects) {
			Shapes.push(s.drawRect(this.cx + rect.x, this.cy + rect.y, rect.w,
					rect.h));
		}
		for (Port port : Ports) {
			port.Connection.setCenter(cx + port.x, cy + port.y);
			port.Connection.Connections.add(this);
			port.Connection.draw(s);
		}

		Text t;
		if (showName) {
			t = canvas.drawText(cx + tx, cy + ty, Name);
			t.attr("text-anchor", "start");
			tdx = (int)t.getBBox().width();
			tdy = (int)t.getBBox().height() ;
			Shapes.push(t);
		}

		for (Property prop : Props) {
			if (prop.display) {
				t = canvas.drawText(cx + tx, cy + ty + tdy, prop.Name + "="
						+ prop.Value);
				t.attr("text-anchor", "start");
				if((int)t.getBBox().width() > tdx)
					tdx = (int)t.getBBox().width();
				
				tdy += (int)t.getBBox().height() ;
				Shapes.push(t);
			}
		}
		boundingBox = s.drawBB(cx + x1, cy + y1, x2 - x1, y2 - y1);
		boundingBox.addMouseDownHandler(this);
		boundingBox.addMouseUpHandler(this);
		boundingBox.addDoubleClickHandler(this);
		boundingBox.addMouseMoveHandler(this);
		boundingBox.addClickHandler(this);
		Shapes.push(boundingBox);

	}

	public void redraw(SVGCanvas s) {
		Shapes.remove();
		boundingBox.remove();
		Shapes = s.new Set();
		for (Line line : Lines) {
			Shapes.push(s.drawLine(this.cx + line.x1, this.cy + line.y1,
					this.cx + line.x2, this.cy + line.y2));
		}
		for (Arc arc : Arcs) {
			Shapes.push(s.drawArc(this.cx + arc.x1, this.cy + arc.y1, this.cx
					+ arc.x2, this.cy + arc.y2, arc.rx, arc.ry, arc.x_rotation,
					arc.large_arc_flag, arc.sweep_flag));
		}
		for (Area rect : Rects) {
			Shapes.push(s.drawRect(this.cx + rect.x, this.cy + rect.y, rect.w,
					rect.h));
		}
		for (Port port : Ports) {
			port.Connection.moveto(cx + port.x, cy + port.y);
		}
		Text t;
		tdx = 0;
		tdy=0;
		if (showName) {
			t = canvas.drawText(cx + tx, cy + ty, Name);
			t.attr("text-anchor", "start");
			tdx = (int)t.getBBox().width();
			tdy = (int)t.getBBox().height() ;
			Shapes.push(t);
		}

		for (Property prop : Props) {
			if (prop.display) {
				t = canvas.drawText(cx + tx, cy + ty + tdy, prop.Name + "="
						+ prop.Value);
				t.attr("text-anchor", "start");
				if((int)t.getBBox().width() > tdx)
					tdx = (int)t.getBBox().width();
				
				tdy += (int)t.getBBox().height() ;
				Shapes.push(t);
			}
		}


		boundingBox = s.drawBB(cx + x1, cy + y1, x2 - x1, y2 - y1);
		boundingBox.addMouseDownHandler(this);
		boundingBox.addMouseUpHandler(this);
		boundingBox.addDoubleClickHandler(this);
		boundingBox.addMouseMoveHandler(this);
		boundingBox.addClickHandler(this);
		Shapes.push(boundingBox);
		canvas = s;
	}
	
	public void redraw(){
		redraw(canvas);
	}

	void moveto(int x, int y) {
		Shapes.translate(x - cx, y - cy);
		cx = x;
		cy = y;
		for (Port port : Ports) {
			port.moveto(x, y);
		}
	}

	public void translate(int dx, int dy) {
		Shapes.translate(dx, dy);
		cx += dx;
		cy += dy;

		for (Port port : Ports) {
			port.moveto(cx, cy);
		}
	}

	public void Select() {
		isSelected = true;
		boundingBox.attr("opacity", 0.3);
		for (Port port : Ports) {
			port.Connection.c.toFront();
		}
	}

	public void Deselect() {
		isSelected = false;
		boundingBox.attr("opacity", 0.0);
	}

	// void rotate(){
	// Shapes.rotate(90);
	// }

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();

		//System.out.println("Component:MouseDown");

		Select();
		if (!canvas.selectedElements.contains(this)) {
			if (!event.isControlKeyDown()) {

				for (Element e : canvas.selectedElements) {
					e.Deselect();
				}
				canvas.selectedElements.clear();
			}
			canvas.selectedElements.add(this);
		}
		canvas.componentMouseDown = true;
		canvas.dragElement = this;
		//Prevent the default drag.
		event.preventDefault();

	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		canvas.componentMouseDown = false;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {

	}

	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();
		System.out.println("Component:MouseClick");
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		event.stopPropagation();
		System.out.println("Component:DoubleClick");
		DialogBox d = new PropertiesDialog(this);
		d.center();
		//d.setPopupPosition(event.getClientX()+10, event.getClientY()+10);
		//d.show();

	}

	public void rotate() {
		if (Ports.size() < 1)
			return; // do not rotate components without ports
		int tmp;

		// rotate all lines
		for (Line l : Lines) {
			tmp = -l.x1;
			l.x1 = l.y1;
			l.y1 = tmp;
			tmp = -l.x2;
			l.x2 = l.y2;
			l.y2 = tmp;
		}

		// rotate all ports
		for (Port p : Ports) {
			tmp = -p.x;
			p.x = p.y;
			p.y = tmp;
		}

		// rotate all arcs
		for (Arc a : Arcs) {
			tmp = a.x1;
			a.x1 = a.y1;
			a.y1 = tmp;
			tmp = a.x2;
			a.x2 = a.y2;
			a.y2 = tmp;
			tmp = a.rx;
			a.rx = a.ry;
			a.ry = tmp;
		}

		// rotate all rectangles
		for (Area a : Rects) {
			tmp = a.x;
			a.x = a.y;
			a.y = tmp;
			tmp = a.w;
			a.w = a.h;
			a.h = tmp;
		}
		
		tmp = -x1; // rotate boundings
		x1 = y1;
		y1 = -x2;
		x2 = y2;
		y2 = tmp;
		
		// rotate text position
		tmp = -tx;
		tx = ty;
		ty  = tmp;
		if (tx > x2)
			ty = y1 - ty + y2; // rotate text position
		else if (ty < y1)
			ty -= tdy;
		else if (tx < x1) {
			tx += tdy -tdx;
			ty = y1 - ty + y2;
		} else
			ty -=tdx;

	
		rotated++; // keep track of what's done
		rotated &= 3;
		this.horizontal = !this.horizontal;
		redraw(canvas);
	}
}
