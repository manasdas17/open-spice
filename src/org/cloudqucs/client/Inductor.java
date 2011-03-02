package org.cloudqucs.client;

public class Inductor extends Component {
	public Inductor() {
		Description = ("inductor");
		Arcs.add(new Arc(-18, 0, -6, 0, 6, 6, 0, 0, 1));
		Arcs.add(new Arc(-6, 0, 6, 0, 6, 6, 0, 0, 1));
		Arcs.add(new Arc(6, 0, 18, 0, 6, 6, 0, 0, 1));

		Lines.add(new Line(-30, 0, -18, 0));
		Lines.add(new Line(18, 0, 30, 0));

		Ports.add(new Port(-30, 0));
		Ports.add(new Port(30, 0));

		x1 = -30;
		y1 = -10;
		x2 = 30;
		y2 = 6;
		tx = x1 + 4;
		ty = y2 + 4;
		Model = "L";
		Name = "L";

		Props.add(new Property("L", "1 nH", true, ("inductance in Henry")));
		Props.add(new Property("I", "", false,
				("initial current for transient simulation")));
	}
}
