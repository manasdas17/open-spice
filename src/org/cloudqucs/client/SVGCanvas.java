package org.cloudqucs.client;

import java.util.ArrayList;

import org.cloudqucs.client.Component.Port;
import org.cloudqucs.client.Element.ElementType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.hydro4ge.raphaelgwt.client.Raphael;

/**
 * @author Sethu Madhav Bhattiprolu
 * 
 */
public class SVGCanvas extends Raphael implements HasAllMouseHandlers,
		HasClickHandlers, HasKeyPressHandlers {
	
	private ArrayList<Component> Components;
	public ArrayList<Wire> Wires;
	public ArrayList<Node> Nodes;
	public ArrayList<Element> selectedElements;

	// For Dragging
	private boolean mouseDown;
	public boolean componentMouseDown;
	private boolean dragging;
	public Element dragElement;

	// For Selecting
	private boolean selecting;
	private int sx, sy, sw, sh;
	private Rect selectRect;
	private boolean disableDeselect;

	// For Wire Drawing
	public boolean drawingWire;
	private int wx1, wy1, wx2, wy2;
	private Path pathDrawWire;
	private ArrayList<Node> selectedNodes;
	public boolean nodeReached;
	public ArrayList<PlanWire> planWires;
	// private int Width,Height;

	// Variables for Zoom and Scrolling
	private double scale;
	private int canvasWidth, canvasHeight;
	private int scaledWidth, scaledHeight;

	// Grid Size in Pixels
	private int gridSize;
	private Set gridSet;
	public boolean planningWire;

	public void startDrag(Element e) {
		dragging = true;
		dragElement = e;
		// See if the Element is Connected
		if (selectedElements.size() == 1) {
			if (e.Type == ElementType.Component) {
				Component c = (Component) e;
				for (Component.Port p : c.Ports) {
					if (p.Connection.Connections.size() > 1) {
						planWires.add(new PlanWire(p.Connection));
					}
				}
			} else if (e.Type == ElementType.Wire) {
				Wire w = (Wire) e;
				planWires.add(new PlanWire(w.end1));
				planWires.add(new PlanWire(w.end2));
			}
		} else {
			for (Element ele : selectedElements) {
				if (ele.Type == ElementType.Component) {
					Component c = (Component) ele;
					for (Component.Port p : c.Ports)
						for (Element element : p.Connection.Connections)
							if (!element.isSelected)
								planWires.add(new PlanWire(p.Connection));

				} else if (ele.Type == ElementType.Wire) {
					Wire w = (Wire) ele;
					for (Element element : w.end1.Connections)
						if (!element.isSelected)
							planWires.add(new PlanWire(w.end1));

					for (Element element : w.end2.Connections)
						if (!element.isSelected)
							planWires.add(new PlanWire(w.end2));
				}
			}
		}

		for (PlanWire pw : planWires) {
			pw.draw();

		}
	}

	public void endDrag() {
		dragging = false;
		if (planningWire)
			planningWire = false;
		for (PlanWire pw : planWires) {
			pw.remove();
		}
		planWires.clear();
	}

	public void startWire() {
		drawingWire = true;
		dragging = false;
		wx1 = 0;
		wy1 = 0;
	}

	public void startWire(int x, int y) {
		drawingWire = true;
		dragging = false;
		wx1 = gridNumber(x);
		wy1 = gridNumber(y);
		pathDrawWire = new Path();
		pathDrawWire.attr("stroke-dasharray", "-");

	}

	public void startWire(Node n) {
		startWire(n.cx, n.cy);
		selectedNodes.add(n);
	}

	public void endWire() {
		drawingWire = false;
	}

	public void endWire(int x, int y) {
		drawingWire = false;
		pathDrawWire.remove();
		drawWire(wx1, wy1, wx2, wy2);
		wx1 = wy1 = 0;
	}

	public void endWire(Node n) {
		selectedNodes.add(n);
		endWire(n.cx, n.cy);
	}

	public void placeComponent(String Type) {

		drawingWire = false;
		if (Type.equals("resistor")) {
			dragElement = new Resistor();
		} else if (Type.equals("VoltDc")) {
			dragElement = new VoltDc();
		} else if (Type.equals("Inductor")) {
			dragElement = new Inductor();
		} else if (Type.equals("Capacitor")) {
			dragElement = new Capacitor();
		} else if (Type.equals("Ground")) {
			dragElement = new Ground();
		}

		Components.add((Component) dragElement);
		dragElement.draw(this);
		startDrag(dragElement);

	}

	public void add(Component c, boolean isFloating) {
		Components.add(c);
		if (isFloating) {
			dragging = true;
			dragElement = c;
		}
		c.draw(this);

	}

	public class Rect extends Raphael.Rect implements HasClickHandlers,
			HasAllMouseHandlers {
		double x, y, w, h, r;
		boolean mouseDown;
		boolean dragging;
		Component c;

		public Rect(double x, double y, double w, double h) {
			super(x, y, w, h);
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.r = 0;
		}

		public Rect(double x, double y, double w, double h, double r) {
			super(x, y, w, h, r);
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.r = 0;
		}

		public Rect(Component c, double x, double y, double w, double h,
				double r) {
			super(x, y, w, h, r);
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.r = 0;
			this.c = c;
		}

		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addDomHandler(handler, ClickEvent.getType());
		}

		public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
			return addDomHandler(handler, MouseDownEvent.getType());
		}

		public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
			return addDomHandler(handler, MouseMoveEvent.getType());
		}

		public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
			return addDomHandler(handler, MouseOutEvent.getType());
		}

		public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
			return addDomHandler(handler, MouseOverEvent.getType());
		}

		public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
			return addDomHandler(handler, MouseUpEvent.getType());
		}

		public HandlerRegistration addMouseWheelHandler(
				MouseWheelHandler handler) {
			return addDomHandler(handler, MouseWheelEvent.getType());
		}

		public void addStyleName(String style) {

		}

		public HandlerRegistration addDoubleClickHandler(
				DoubleClickHandler handler) {
			return addDomHandler(handler, DoubleClickEvent.getType());

		}

	}

	public class Circle extends Raphael.Circle implements HasClickHandlers,
			HasAllMouseHandlers {

		public Circle(double x, double y, double r) {
			super(x, y, r);
		}

		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addDomHandler(handler, ClickEvent.getType());
		}

		public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
			return addDomHandler(handler, MouseDownEvent.getType());
		}

		public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
			return addDomHandler(handler, MouseMoveEvent.getType());
		}

		public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
			return addDomHandler(handler, MouseOutEvent.getType());
		}

		public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
			return addDomHandler(handler, MouseOverEvent.getType());
		}

		public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
			return addDomHandler(handler, MouseUpEvent.getType());
		}

		public HandlerRegistration addMouseWheelHandler(
				MouseWheelHandler handler) {
			return addDomHandler(handler, MouseWheelEvent.getType());
		}

		public void addStyleName(String style) {

		}

	}
	

	/**
	 * SVGCanvas constructor
	 */
	public SVGCanvas() {
		super(Window.getClientWidth(), Window.getClientHeight());

		canvasWidth = Window.getClientWidth();
		canvasHeight = Window.getClientHeight();
		
		gridSize = 10;
		scale = 1;

		mouseDown = false;
		componentMouseDown = false;
		dragging = false;

		Components = new ArrayList<Component>();
		selectedElements = new ArrayList<Element>();
		selecting = false;
		disableDeselect = false;

		drawingWire = false;
		planningWire = false;
		planWires = new ArrayList<PlanWire>();
		wx1 = 0;
		Nodes = new ArrayList<Node>();
		Wires = new ArrayList<Wire>();
		selectedNodes = new ArrayList<Node>();

		gridSet = new Set();
		showGrid();
		drawBoundary();
		nodeReached = false;

		addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (dragging) {
					int dx, dy;
					dx = gridNumber(event.getX())
							- gridNumberWithoutScaling(dragElement.cx);
					dy = gridNumber(event.getY())
							- gridNumberWithoutScaling(dragElement.cy);
//					dx = event.getX() - dragComponent.cx;
//					dy = event.getY() - dragComponent.cy;
					if (!selectedElements.contains(dragElement)) {
						dragElement.translate(dx, dy);
					} else {
						for (Element e : selectedElements) {
							e.translate(dx, dy);
						}
					}
					if (planningWire) {
						for (PlanWire p : planWires) {
							p.redraw();
						}
					}
				} else if (selecting) {
					int x, y;
					x = gridNumber(event.getX());
					y = gridNumber(event.getY());
					sw = x - sx;
					sh = y - sy;
					JSONObject jo = new JSONObject();
					if (sw >= 0 && sh >= 0) {
						jo.put("width", new JSONNumber(sw));
						jo.put("height", new JSONNumber(sh));
					} else if (sw < 0 && sh >= 0) {
						jo.put("width", new JSONNumber(-sw));
						jo.put("height", new JSONNumber(sh));
						jo.put("x", new JSONNumber(x));
					} else if (sw >= 0 && sh < 0) {
						jo.put("width", new JSONNumber(sw));
						jo.put("height", new JSONNumber(-sh));
						jo.put("y", new JSONNumber(y));

					} else {
						jo.put("width", new JSONNumber(-sw));
						jo.put("height", new JSONNumber(-sh));
						jo.put("x", new JSONNumber(x));
						jo.put("y", new JSONNumber(y));
					}
					// selectRect.animate(jo, 10);
					selectRect.attr(jo);
				} else if (drawingWire) {
					if (wx1 != 0) {
						wx2 = gridNumber(event.getX());
						wy2 = gridNumber(event.getY());
						pathDrawWire.attr("path", "M" + wx1 + " " + wy1 + "V"
								+ wy2 + "H" + wx2);

					}
				} else if (mouseDown) {
					selecting = true;
					sx = gridNumber(event.getX());
					sy = gridNumber(event.getY());
					selectRect = new Rect(sx, sy, 0, 0);
					// System.out.println("MouseDown:Start Select");

				} else if (componentMouseDown) {
					startDrag(dragElement);
				}
				event.preventDefault();
			}
		});

		addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {

				// System.out.println("Canvas:MouseDown");
				mouseDown = true;
				if (drawingWire) {
					if (wx1 != 0)
						endWire(event.getX(), event.getY());

					// if not clicked on a node start wiring
					if (!nodeReached) {
						startWire(event.getX(), event.getY());
					}
					nodeReached = false;
				}
				event.preventDefault();
			}
		});

		addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
			    System.out.println("Canvas:MouseUp: ");
				mouseDown = false;
				componentMouseDown = false;
				if (dragging) {
					endDrag();
				}
				if (selecting) {
					selecting = false;
					disableDeselect = true;
					event.stopPropagation();
					// System.out.println("In Selecting");
					int x1, y1, x2, y2;
					if (sw < 0) {
						x1 = sx + sw;
						x2 = sx;
					} else {
						x1 = sx;
						x2 = sx + sw;
					}

					if (sh < 0) {
						y1 = sy + sh;
						y2 = sy;
					} else {
						y1 = sy;
						y2 = sy + sh;
					}

					selectRect.remove();
					if (!event.isControlKeyDown()) {
						for (Element c : selectedElements) {
							c.Deselect();
						}
						selectedElements.clear();
					}
					for (Component c : Components) {

						if (!selectedElements.contains(c) && c.cx > x1
								&& c.cx < x2 && c.cy > y1 && c.cy < y2) {
							c.Select();
							selectedElements.add(c);
						}
					}
					for (Wire w : Wires) {

						if (!selectedElements.contains(w) && w.cx > x1
								&& w.cx < x2 && w.cy > y1 && w.cy < y2) {
							w.Select();
							selectedElements.add(w);
						}
					}

				}
			}
		});

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// System.out.println("Canvas:MouseClick");
				if (!selecting && !selectedElements.isEmpty()
						&& !disableDeselect) {

					for (Element e : selectedElements) {
						e.Deselect();
					}
					selectedElements.clear();
					// System.out.println("ClickHandler:deselected");

				}

				disableDeselect = false;
			}
		});
		addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {

				// System.out.println("Canvas:DoubleClick");
				if (drawingWire) {
					endWire(event.getX(), event.getY());
				}
			}

		});

	}

	/**
	 * Raphael Text and the Firefox 3.6 SVG implementation do not work together
	 * when the text is appended to the drawing before the drawing is appended
	 * to the document. Therefore, we defer the layout to onLoad() here instead
	 * of doing it in the constructor.
	 */
	@Override
	public void onLoad() {
		super.onLoad();

	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return addDomHandler(handler, DoubleClickEvent.getType());
	}

	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addDomHandler(handler, KeyPressEvent.getType());
	}

	public Shape drawLine(int x1, int y1, int x2, int y2) {
		Path p = new Path("M" + x1 + " " + y1 + "L" + x2 + " " + y2);
		return p;
	}

	public Shape drawPath(String Path) {
		return new Path(Path);
	}

	public Shape drawArc(int x1, int y1, int x2, int y2, int rx, int ry,
			int x_rotation, int large_arc_flag, int sweep_flag) {
		return new Path("M" + x1 + " " + y1 + "A" + rx + " " + ry + " "
				+ x_rotation + " " + large_arc_flag + " " + sweep_flag + " "
				+ x2 + " " + y2);

	}

	public Shape drawRect(int x, int y, int w, int h) {
		return new Rect(x, y, w, h);
	}
	
	Shape boundary;
	public void drawBoundary(){
		boundary = new Rect(2,2,canvasWidth-3,canvasHeight-3);
		boundary.attr("stroke-width","5");
		boundary.attr("stroke-opacity","0.1");
	}
	public void redrawBoundary(){
		boundary.remove();
		boundary = new Rect(2,2,canvasWidth-3,canvasHeight-3);
		boundary.attr("stroke-width","5");
		boundary.attr("stroke-opacity","0.1");
	}

	public Rect drawBB(int x, int y, int w, int h) {
		final Rect r = new Rect(x, y, w, h, 4);
		r.attr("fill", "#000");
		r.attr("opacity", 0.1);
		return r;
	}

	public Circle drawCircle(int cx, int cy, int r) {
		return new Circle(cx, cy, r);
	}

	public Text drawText(int x, int y, String txt) {
		return new Text(x, y, txt);
	}

	public int gridNumber(int Number) {
		Number /= scale;
		Number += gridSize >> 1;
		Number -= Number % gridSize;
		return Number;
	}

	public int gridNumberWithoutScaling(int Number) {
		Number += gridSize >> 1;
		Number -= Number % gridSize;
		return Number;
	}

	public void showGrid() {
		Shape s;
		for (int j = 0; j <= canvasHeight; j += 2 * gridSize) {
			s = drawLine(0, j, canvasWidth, j);
			s.attr("opacity", "0.1");
			gridSet.push(s);
		}
		
		for (int i = 0; i <= canvasWidth; i += 2 * gridSize) {
			s = drawLine(i,0 , i, canvasHeight);
			s.attr("opacity", "0.1");
			gridSet.push(s);
		}
		
	}

	public void hideGrid() {
		gridSet.remove();
	}

	// Method for scaling canvas
	public void scale(double scaleFactor) {
		// TODO: Validations on scaleFactor
		this.scale = scaleFactor;
		this.scaledWidth = (int) Math.round(canvasWidth * scaleFactor);
		this.scaledHeight = (int) Math.round(canvasHeight * scaleFactor);
		this.setSize(scaledWidth, scaledHeight);
		this.scale(0, 0, canvasWidth, canvasHeight);
		this.redrawBoundary();
	}

	public void drawWire(int x1, int y1, int x2, int y2) {
		// Check Horizontal or Vertical
		if (x1 == x2 || y1 == y2) {
			Wire w = new Wire(x1, y1, x2, y2);
			w.draw(this);
			// Wires.add(w);
		} else {

			Wire w;

			// Vertical wire
			w = new Wire(x1, y1, x1, y2);
			w.draw(this);
			// Wires.add(w);

			// Horizontal Wire
			w = new Wire(x1, y2, x2, y2);
			w.draw(this);
			// Wires.add(w);

		}
		x1 = y1 = 0;
	}

	public void drawWire2(int x1, int y1, int x2, int y2, boolean endHorizontal) {
		// Check Horizontal or Vertical
		if (x1 == x2 || y1 == y2) {
			Wire w = new Wire(x1, y1, x2, y2);
			w.draw(this);
			// Wires.add(w);
		} else {
			Wire w;
			if (endHorizontal == true) {
				// Vertical wire
				w = new Wire(x1, y1, x1, y2);
				w.draw(this);
				// Wires.add(w);

				// Horizontal Wire
				w = new Wire(x1, y2, x2, y2);
				w.draw(this);
				// Wires.add(w);
			} else {

				// Horizontal Wire
				w = new Wire(x1, y1, x2, y1);
				w.draw(this);

				// Vertical wire
				w = new Wire(x2, y1, x2, y2);
				w.draw(this);

			}
		}
		x1 = y1 = 0;
	}

	public class PlanWire {
		Node start, end;
		boolean endHorizontal;
		//boolean startDraw;
		Raphael.Path p;

		public PlanWire(Node n) {
			end = n;
			//startDraw = false;
		}

		public void remove() {
			p.remove();
			//Nodes.add(start);
			drawWire2(start.cx, start.cy, end.cx, end.cy, endHorizontal);

		}

		@SuppressWarnings("unchecked")
		public void draw() {
			// Go 2 wires and 2 nodes deep only
			Wire w1, w2;
			Node n1, n2;
			boolean startFound = false;
			if (end.Connections.size() == 2) {
				ArrayList<Element> eles = (ArrayList<Element>) end.Connections
						.clone();
				for (Element e : eles) {
					if (e.Type == ElementType.Wire && !e.equals(dragElement)) {

						w1 = (Wire) e;
						endHorizontal = w1.horizontal;
						// get the other node of w1
						if (w1.end1.equals(end))
							n1 = w1.end2;
						else
							n1 = w1.end1;

						// check if n1 connects two wires
						if (n1.Connections.size() == 2
								&& n1.Connections.get(0).Type == ElementType.Wire
								&& n1.Connections.get(1).Type == ElementType.Wire) {

							// get the other wire
							if (n1.Connections.get(0).equals(w1))
								w2 = (Wire) n1.Connections.get(1);
							else
								w2 = (Wire) n1.Connections.get(0);

							// get the other node of the other wire
							if (w2.end1.equals(n1))
								n2 = w2.end2;
							else
								n2 = w2.end1;

							// Delete node n1 , wire w1 , wire w2 and

							end.remove(w1);
							n1.remove(w1);
							w1.remove();

							n1.remove(w2);
							n1.remove();
							n2.remove(w2);

							w2.remove();
							start = n2;
							startFound = true;
							break;
						} else {
							// Remove Wire w1
							end.remove(w1);
							n1.remove(w1);
							w1.remove();
							start = n1;
							startFound = true;
							break;
						}
					}
				}
			}
			// node with three elements or component node
			if (!startFound) {
				// Cannot move the Node _n or end
				// Duplicate the node
				start = new Node(end.cx, end.cy);
				Component c;
				Wire w;
				// Transfer the Components
				for (Element ele : end.Connections) {
					if (!ele.equals(dragElement)) {
						start.add(ele);
						if (ele.Type == ElementType.Component) {
							c = (Component) ele;
							for (Port p : c.Ports)
								if (p.Connection.equals(end))
									p.Connection = start;

						} else if (ele.Type == ElementType.Wire) {
							w = (Wire) ele;
							if (w.end1.equals(end))
								w.end1 = start;
							else
								w.end2 = start;
						}
					}
				}
				for (Element ele : start.Connections) {
					end.remove(ele);
				}
				start.draw(end.canvas);
			}

			// Path Between start and end
			System.out.println("PlannedWires:" + planWires.size());

			if (endHorizontal == true) {
				p = new Path("M" + start.cx + " " + start.cy + "V" + end.cy
						+ "H" + end.cx);
			} else {
				p = new Path("M" + start.cx + " " + start.cy + "H" + end.cx
						+ "V" + end.cy);
			}

			planningWire = true;
			p.attr("stroke-dasharray", "-");

		}

		public void redraw() {
			if (endHorizontal == true) {
				p.attr("path", "M" + start.cx + " " + start.cy + "V" + end.cy
						+ "H" + end.cx);
			} else {
				p.attr("path", "M" + start.cx + " " + start.cy + "H" + end.cx
						+ "V" + end.cy);
			}

		}

	}

}
