package org.cloudqucs.client.Dialogs;

import org.cloudqucs.client.Component;
import org.cloudqucs.client.Component.Property;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class PropertiesDialog extends DialogBox {

	Component propertyComponent;
	CheckBox cbDisplayName;
	TextBox tbName;

	public PropertiesDialog(Component c) {
		propertyComponent = c;
		setText("Edit Component Properties");
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(2);

		vp.add(new HTML("<h5>" + c.Description.toUpperCase() + "</h5>"));

		FlexTable layout = new FlexTable();
		layout.setCellSpacing(6);
		FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

		tbName = new TextBox();
		tbName.setText(c.Name);
		cbDisplayName = new CheckBox();
		cbDisplayName.setText("Display in Schematic");
		cbDisplayName.setWordWrap(false);
		cbDisplayName.setValue(c.showName);
		layout.setHTML(0, 0, "Name:");
		layout.setWidget(0, 1, tbName);
		layout.setWidget(0, 2, cbDisplayName);

		CellTable<Property> table = new CellTable<Property>();
		TextColumn<Property> name = new TextColumn<Property>() {
			public String getValue(Property prop) {
				return prop.Name;
			}
		};

		Column<Property, String> value = new Column<Property, String>(
				new EditTextCell()) {

			@Override
			public String getValue(Property object) {
				object.oldValue = object.Value;
				return object.Value;
			}
		};

		value.setFieldUpdater(new FieldUpdater<Property, String>() {

			@Override
			public void update(int index, Property object, String value) {
				object.Value = value;
			}
		});

		Column<Property, Boolean> display = new Column<Property, Boolean>(
				new CheckboxCell()) {

			@Override
			public Boolean getValue(Property object) {
				return object.display;
			}
		};

		display.setFieldUpdater(new FieldUpdater<Property, Boolean>() {

			@Override
			public void update(int index, Property object, Boolean value) {
				object.display = value;
			}

		});

		TextColumn<Property> description = new TextColumn<Property>() {
			public String getValue(Property prop) {
				return prop.Description;
			}
		};

		table.addColumn(name, "Name");
		table.addColumn(value, "Value");
		table.addColumn(display, "Display");
		table.addColumn(description, "Description");
		table.setRowCount(c.Props.size(), true);
		table.setRowData(c.Props.subList(0, c.Props.size()));

		DecoratorPanel dp = new DecoratorPanel();
		layout.setWidget(1, 0, new HTML("<h4>Properties:</h4>"));
		layout.setWidget(2, 0, dp);
		cellFormatter.setColSpan(1, 0, 3);
		cellFormatter.setColSpan(2, 0, 3);
		dp.add(table);

		vp.add(layout);
		HorizontalPanel hp = new HorizontalPanel();
		Button btnApply = new Button("Apply");
		Button btnCancel = new Button("Cancel");
		hp.add(btnApply);
		hp.add(btnCancel);
		hp.setSpacing(4);
		vp.add(hp);
		vp.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(vp);
		setAutoHideEnabled(true);

		btnApply.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				propertyComponent.showName = cbDisplayName.getValue();
				propertyComponent.Name = tbName.getText();
				propertyComponent.redraw();
				hide();
			}
		});

		btnCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				for (Property prop : propertyComponent.Props) {
					prop.Value = prop.oldValue;
				}
				hide();
			}
		});
	}

}
