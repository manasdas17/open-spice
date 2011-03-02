package org.cloudqucs.client;

public class Resistor extends Component {
	public Resistor() {

		Description = "resistor";
		Name = "R";
		Model = "R";
		Props
				.add(new Property("R", "50 Ohm", true,
						"ohmic resistance in Ohms"));
		Props.add(new Property("Temp", "26.85", true,
				"simulation temperature in degree Celsius"));

		createSymbol();

	}

	public Resistor(String Name, int cx, int cy) {
		this.cx = cx;
		this.cy = cy;
		this.Name = Name;
	}

	void createSymbol() {
		Lines.add(new Line(-30, 0, -18, 0));
		Lines.add(new Line(-18, 0, -15, -7));
		Lines.add(new Line(-15, -7, -9, 7));
		Lines.add(new Line(-9, 7, -3, -7));
		Lines.add(new Line(-3, -7, 3, 7));
		Lines.add(new Line(3, 7, 9, -7));
		Lines.add(new Line(9, -7, 15, 7));
		Lines.add(new Line(15, 7, 18, 0));
		Lines.add(new Line(18, 0, 30, 0));

		Ports.add(new Port(-30, 0));
		Ports.add(new Port(30, 0));

		x1 = -30;
		y1 = -11;
		x2 = 30;
		y2 = 11;
		tx = x1 + 4;
		ty = y2 + 4;
	}

}
