package org.cloudqucs.client;

public class Ground extends Component {
	public Ground() {
		Description = "Ground (Reference Potential)";
		Lines.add(new Line(0, 0, 0, 10));
		Lines.add(new Line(-11, 10, 11, 10));
		Lines.add(new Line(-7, 16, 7, 16));
		Lines.add(new Line(-3, 22, 3, 22));

		Ports.add(new Port(0, 0));

		x1 = -12;
		y1 = 0;
		x2 = 12;
		y2 = 25;

		Model = "GND";
		Name = "";

	}
}
