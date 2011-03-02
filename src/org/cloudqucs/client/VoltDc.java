package org.cloudqucs.client;

public class VoltDc extends Component {

	public VoltDc() {
		Description = "ideal dc voltage source";
		Model = "Vdc";
		Name = "V";
		Props.add(new Property("U", "1 V", true, "voltage in Volts"));
		createSymbol();
	}

	void createSymbol() {
		Lines.add(new Line(4, -13, 4, 13));
		Lines.add(new Line(-4, -6, -4, 6));
		Lines.add(new Line(30, 0, 4, 0));
		Lines.add(new Line(-4, 0, -30, 0));
		Lines.add(new Line(11, 5, 11, 11));
		Lines.add(new Line(14, 8, 8, 8));
		Lines.add(new Line(-11, 5, -11, 11));

		Ports.add(new Port(30, 0));
		Ports.add(new Port(-30, 0));
		x1 = -30;
		y1 = -14;
		x2 = 30;
		y2 = 14;
		tx = x1 + 4;
		ty = y2 + 4;
	}

}
