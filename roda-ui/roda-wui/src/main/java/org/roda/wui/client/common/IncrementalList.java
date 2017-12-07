/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.client.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import org.roda.wui.client.common.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class IncrementalList extends Composite implements HasValueChangeHandlers<List<String>> {
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  interface MyUiBinder extends UiBinder<Widget, IncrementalList> {
  }

  @UiField
  FlowPanel textBoxPanel;

  @UiField
  Button addDynamicButton;

  private ArrayList<RemovableTextBox> textBoxes;
  boolean changed = false;

  public IncrementalList() {
    this(false);
  }

  public IncrementalList(boolean vertical) {
    initWidget(uiBinder.createAndBindUi(this));
    textBoxes = new ArrayList<>();
    if (vertical) {
      addStyleDependentName("vertical");
    }
  }

  public List<String> getTextBoxesValue() {
    ArrayList<String> listValues = new ArrayList<>();
    for (RemovableTextBox textBox : textBoxes) {
      if (StringUtils.isNotBlank(textBox.getTextBoxValue())) {
        listValues.add(textBox.getTextBoxValue());
      }
    }
    return listValues;
  }

  public void setTextBoxList(List<String> list) {
    for (String element : list) {
      addTextBox(element);
    }
  }

  public void clearTextBoxes() {
    textBoxPanel.clear();
    textBoxes = new ArrayList<>();
  }

  @UiHandler("addDynamicButton")
  void addMore(ClickEvent event) {
    addTextBox(null);
  }

  private void addTextBox(String element) {
    final RemovableTextBox box = new RemovableTextBox(element);
    textBoxPanel.add(box);
    textBoxes.add(box);

    box.addRemoveClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        textBoxPanel.remove(box);
        textBoxes.remove(box);
        ValueChangeEvent.fire(IncrementalList.this, getTextBoxesValue());
      }
    });

    box.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        ValueChangeEvent.fire(IncrementalList.this, getTextBoxesValue());
      }
    });
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

}
