package org.cloudqucs.client;

public class Capacitor extends Component {

	public Capacitor() {
		Description = ("capacitor");

		Props.add(new Property("C", "1 pF", true, ("capacitance in Farad")));
		Props.add(new Property("V", "", false,
				("initial voltage for transient simulation")));
		Props.add(new Property("Symbol", "neutral", false, ("schematic symbol")
				+ " [neutral, polar]"));

		Model = "C";
		Name = "C";
		if (Props.get(Props.size() - 1).Value.equals("neutral")) {
			Lines.add(new Line(-4, -11, -4, 11));
			Lines.add(new Line(4, -11, 4, 11));
		} else {
			Lines.add(new Line(-11, -5, -11, -11));
			Lines.add(new Line(-14, -8, -8, -8));
			Lines.add(new Line(-4, -11, -4, 11));
			// Arcs.add(new Arc(4,-12, 20, 24, 16*122, 16*116));
		}

		Lines.add(new Line(-30, 0, -4, 0));
		Lines.add(new Line(4, 0, 30, 0));

		Ports.add(new Port(-30, 0));
		Ports.add(new Port(30, 0));

		x1 = -30;
		y1 = -13;
		x2 = 30;
		y2 = 13;
		tx = x1 + 4;
		ty = y2 + 4;
	}
}
