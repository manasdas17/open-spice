package org.cloudqucs.client;

import org.cloudqucs.client.SVGCanvas.Rect;

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
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.hydro4ge.raphaelgwt.client.Raphael.Set;
import com.hydro4ge.raphaelgwt.client.Raphael.Shape;

/**
 * @author Sethu Madhav Bhattiprolu
 * 
 */
public class Wire extends Element implements MouseDownHandler, MouseUpHandler,
		MouseMoveHandler, ClickHandler, DoubleClickHandler {

	@Override
	public void draw(SVGCanvas s) {
		canvas = s;
		Shapes = s.new Set();
		// Check for any Existing Nodes at these positions
		boolean end1Found = false, end2Found = false;
		for (Node n : s.Nodes) {
			if (n.equals(x1, y1)) {
				end1 = n;
				end1Found = true;
			} else if (n.equals(x2, y2)) {
				end2 = n;
				s.nodeReached = true;
				end2Found = true;
			}
		}

		if (end1Found) {
			if (end1.Connections.size() == 1
					&& end1.Connections.get(0).Type == ElementType.Wire
					&& end1.Connections.get(0).horizontal == this.horizontal) {
				Wire w = (Wire) end1.Connections.get(0);
				System.out.println("Extending");
				if (w.end1.equals(end1)) {
					end1 = w.end2;
					x1 = end1.cx;
					y1 = end1.cy;
					w.end1.remove();
					w.end2.remove(w);
					w.remove();
					end1.add(this);
					end1.draw(canvas);
				} else {
					end1 = w.end1;
					x1 = end1.cx;
					y1 = end1.cy;
					w.end2.remove();
					w.end1.remove(w);
					w.remove();
					end1.add(this);
					end1.draw(canvas);
				}
			} else {
				end1.add(this);
				end1.draw(canvas);
			}
		}

		if (end2Found) {
			if (end2.Connections.size() == 1
					&& end2.Connections.get(0).Type == ElementType.Wire
					&& end2.Connections.get(0).horizontal == this.horizontal) {
				Wire w = (Wire) end2.Connections.get(0);

				if (w.end1.equals(end2)) {
					end2 = w.end2;
					x2 = end2.cx;
					y2 = end2.cy;
					w.end1.remove();
					w.end2.remove(w);
					w.remove();
					end2.add(this);
					end2.draw(canvas);
				} else {
					end2 = w.end1;
					x2 = end2.cx;
					y2 = end2.cy;
					w.end2.remove();
					w.end1.remove(w);
					w.remove();
					end2.add(this);
					end2.draw(canvas);
				}
			} else {
				end2.add(this);
				end2.draw(canvas);
			}
		}

		// Draw the bounding box
		if (x1 == x2) {
			if (y2 > y1)
				boundingBox = s.drawBB(x1 - 3, y1, 6, y2 - y1);
			else
				boundingBox = s.drawBB(x1 - 3, y2, 6, y1 - y2);
		} else if (y1 == y2) {
			if (x2 > x1)
				boundingBox = s.drawBB(x1, y1 - 3, x2 - x1, 6);
			else
				boundingBox = s.drawBB(x2, y1 - 3, x1 - x2, 6);
		}

		boundingBox.addMouseDownHandler(this);
		boundingBox.addMouseUpHandler(this);
		boundingBox.addDoubleClickHandler(this);
		boundingBox.addMouseMoveHandler(this);
		boundingBox.addClickHandler(this);

		if (!end1Found) {
			end1 = new Node(x1, y1);
			end1.add(this);
			end1.draw(canvas);
		}
		if (!end2Found) {
			end2 = new Node(x2, y2);
			end2.add(this);
			end2.draw(canvas);
		}
		line = s.drawLine(x1, y1, x2, y2);
		Shapes.push(line);
		Shapes.push(boundingBox);
		canvas.Wires.add(this);

	}

	Wire(int _x1, int _y1, int _x2, int _y2) {
		Type = ElementType.Wire;

		x1 = _x1;
		y1 = _y1;
		x2 = _x2;
		y2 = _y2;
		cx = (x1 + x2) >> 1;
		cy = (y1 + y2) >> 1;

		if (x1 == x2)
			horizontal = false;
		else
			horizontal = true;

		// System.out.println("Horizontal:" + horizontal);
	}

	Node end1, end2;
	SVGCanvas canvas;
	Set Shapes;
	Rect boundingBox;
	Shape line;

	@Override
	public void translate(int dx, int dy) {
		Shapes.translate(dx, dy);
		x1 += dx;
		x2 += dx;
		y1 += dy;
		y2 += dy;
		cx = (x1 + x2) >> 1;
		cy = (y1 + y2) >> 1;
		end1.moveto(x1, y1);
		end2.moveto(x2, y2);

	}

	public void Select() {
		isSelected = true;
		boundingBox.attr("opacity", 0.3);
		end1.c.toFront();
		end2.c.toFront();
	}

	public void Deselect() {
		isSelected = false;
		boundingBox.attr("opacity", 0.1);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
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
		// TODO: Select Connected Wires
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		event.stopPropagation();
	}

	void extend(Node end, int x, int y) {
		// System.out.println("Ext:" + end1.equals(end) + "," +
		// end2.equals(end));

		if (end1.equals(end)) {
			x1 = x;
			y1 = y;
			end1.moveto(x, y);
		} else {
			x2 = x;
			y2 = y;
			end2.moveto(x, y);
		}
		JSONObject o = new JSONObject();

		if (x1 == x2) {
			if (y2 > y1) {
				// boundingBox = canvas.drawBB(x1 - 3, y1, 6, y2 - y1);

				o.put("x", new JSONNumber(x1 - 3));
				o.put("y", new JSONNumber(y1));
				o.put("width", new JSONNumber(6));
				o.put("height", new JSONNumber(y2 - y1));

			} else {
				// boundingBox = canvas.drawBB(x1 - 3, y2, 6, y1 - y2)
				o.put("x", new JSONNumber(x1 - 3));
				o.put("y", new JSONNumber(y2));
				o.put("width", new JSONNumber(6));
				o.put("height", new JSONNumber(y1 - y2));

			}
		} else if (y1 == y2) {
			if (x2 > x1) {
				// boundingBox = canvas.drawBB(x1, y1 - 3, x2 - x1, 6);
				o.put("x", new JSONNumber(x1));
				o.put("y", new JSONNumber(y1 - 3));
				o.put("width", new JSONNumber(x2 - x1));
				o.put("height", new JSONNumber(6));
			} else {
				// boundingBox = canvas.drawBB(x2, y1 - 3, x1 - x2, 6);
				o.put("x", new JSONNumber(x2));
				o.put("y", new JSONNumber(y1 - 3));
				o.put("width", new JSONNumber(x1 - x2));
				o.put("height", new JSONNumber(6));
			}
		}
		// change the size of Bounding Box
		boundingBox.attr(o);
		// new center
		cx = (x1 + x2) >> 1;
		cy = (y1 + y2) >> 1;
		// draw the wire
		line.attr("path", "M" + x1 + " " + y1 + "L" + x2 + " " + y2);

	}

	void extend(Node from, Node to) {
		// System.out.println("Ext:" + end1.equals(end) + "," +
		// end2.equals(end));

		if (end1.equals(from)) {
			x1 = to.cx;
			y1 = to.cy;
			end1.remove();
			end1 = to;
		} else {
			x2 = to.cx;
			y2 = to.cy;
			end2.remove();
			end2 = to;
		}
		JSONObject o = new JSONObject();

		if (x1 == x2) {
			if (y2 > y1) {
				// boundingBox = canvas.drawBB(x1 - 3, y1, 6, y2 - y1);

				o.put("x", new JSONNumber(x1 - 3));
				o.put("y", new JSONNumber(y1));
				o.put("width", new JSONNumber(6));
				o.put("height", new JSONNumber(y2 - y1));

			} else {
				// boundingBox = canvas.drawBB(x1 - 3, y2, 6, y1 - y2)
				o.put("x", new JSONNumber(x1 - 3));
				o.put("y", new JSONNumber(y2));
				o.put("width", new JSONNumber(6));
				o.put("height", new JSONNumber(y1 - y2));

			}
		} else if (y1 == y2) {
			if (x2 > x1) {
				// boundingBox = canvas.drawBB(x1, y1 - 3, x2 - x1, 6);
				o.put("x", new JSONNumber(x1));
				o.put("y", new JSONNumber(y1 - 3));
				o.put("width", new JSONNumber(x2 - x1));
				o.put("height", new JSONNumber(6));
			} else {
				// boundingBox = canvas.drawBB(x2, y1 - 3, x1 - x2, 6);
				o.put("x", new JSONNumber(x2));
				o.put("y", new JSONNumber(y1 - 3));
				o.put("width", new JSONNumber(x1 - x2));
				o.put("height", new JSONNumber(6));
			}
		}
		// change the size of Bounding Box
		boundingBox.attr(o);
		// new center
		cx = (x1 + x2) >> 1;
		cy = (y1 + y2) >> 1;
		// draw the wire
		line.attr("path", "M" + x1 + " " + y1 + "L" + x2 + " " + y2);
	}

	public void remove() {
		canvas.Wires.remove(canvas.Wires.indexOf(this));
		Shapes.remove();
	}

}
