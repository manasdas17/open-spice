package org.cloudqucs.client;

import org.cloudqucs.client.Element.ElementType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainScreen extends Composite {

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	PushButton btnResistor;
	@UiField
	PushButton btnVdc;
	@UiField
	PushButton btnInductor;
	@UiField
	PushButton btnCapacitor;
	@UiField
	PushButton btnGround;
	@UiField
	PushButton btnWire;
	@UiField
	PushButton btnZoomIn;
	@UiField
	PushButton btnZoomOut;
	@UiField
	PushButton btnHideGrid;
	@UiField
	PushButton btnShowGrid;
	@UiField
	PushButton btnRotate;
	@UiField
	HorizontalPanel hpHeader;
	@UiField
	Grid gdToolbar;
	@UiField
	Image logoImage;
	@UiField(provided = true)
	CellTable<Object> ctToolbox = new CellTable<Object>();
	@UiField
	SimplePanel SVGCanvasPanel;
	@UiField SplitLayoutPanel ctCenterArea;
	SVGCanvas canvas;

	interface Binder extends UiBinder<Widget, MainScreen> {
	}

	public MainScreen() {
		initWidget(binder.createAndBindUi(this));
		canvas = new SVGCanvas();
		SVGCanvasPanel.add(canvas);

		// canvas.scale(0, 0, Window.getClientWidth(),
		// Window.getClientHeight());
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				System.out.println("Resize");
				setScrollBars();
			}
		});

		// btnResistor.addClickHandler(handler)
		btnResistor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.placeComponent("resistor");
			}
		});

		btnVdc.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.placeComponent("VoltDc");
			}
		});

		btnInductor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.placeComponent("Inductor");
			}
		});
		btnCapacitor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.placeComponent("Capacitor");
			}
		});
		btnGround.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.placeComponent("Ground");
			}
		});

		btnWire.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.startWire();
			}
		});
		btnZoomIn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.scale(2);
			}
		});
		btnZoomOut.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.scale(1);
			}
		});
		btnHideGrid.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.hideGrid();
			}
		});
		btnShowGrid.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvas.showGrid();
			}
		});
		btnRotate.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (canvas.dragElement.Type == ElementType.Component) {
					Component c = (Component) canvas.dragElement;
					c.rotate();
				}
			}
		});

		LoadHandler h = new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Header Load:");
				setScrollBars();
			}
		};

		logoImage.addDomHandler(h, LoadEvent.getType());

	}

	public void setScrollBars() {
		Integer height = Window.getClientHeight() - hpHeader.getOffsetHeight()
				- gdToolbar.getOffsetHeight();
		System.out.println("Height:" + height + "header:"
				+ hpHeader.getOffsetHeight() + "toolbar:"
				+ gdToolbar.getOffsetHeight());
//		SVGCanvasPanel
//				.setSize(Integer.toString(Window.getClientWidth()) + "px",
//						height + "px");
		ctCenterArea.setHeight(height.toString()+"px");
	}

}
