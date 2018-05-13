package com.scheduler.presentation.framework;

import java.util.ArrayList;
import java.util.List;

import com.scheduler.interfaces.web.UIContentProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;



public abstract class GenericCreateObjectContent<T extends EditableViewElement> implements UIContentProvider {

    protected VerticalLayout layout = new VerticalLayout();
    protected List<T> objects = new ArrayList<>();

    protected final Button createButton = new Button("Utworz");
    protected final Button nextObjectButton = new Button("Dodaj nastepny", FontAwesome.PLUS);

    public abstract T getNewObject();
    public abstract Button.ClickListener initCreateButtonListener();

    public  GenericCreateObjectContent(boolean withoutNextObjectButton) {
        this();
        this.layout.removeComponent(nextObjectButton);
    }

    public GenericCreateObjectContent() {
        this.createButton.addClickListener(initCreateButtonListener());
        this.nextObjectButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                addNextObject();
            }
        });

        T singleObj = getNewObject();
        objects.add(singleObj);

        Component objectLayout = singleObj.getEditableLayout();
        layout.addComponent(objectLayout);
        layout.setComponentAlignment(objectLayout, Alignment.TOP_CENTER);

        settleButtons();
    }

    private void settleButtons() {
        layout.addComponents(nextObjectButton);
        layout.addComponents(createButton);
        layout.setComponentAlignment(nextObjectButton,Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(createButton,Alignment.BOTTOM_CENTER);
    }

    private void addNextObject() {
        layout.removeComponent(createButton);
        layout.removeComponent(nextObjectButton);

        T singleObj = getNewObject();
        objects.add(singleObj);

        Component objectLayout = singleObj.getEditableLayout();
        layout.addComponent(objectLayout);
        layout.setComponentAlignment(objectLayout, Alignment.TOP_CENTER);

        settleButtons();
    }

    public List<T> getItems() {
        return this.objects;
    }

    @Override
    public Component getContent() {
        return layout;
    }
}
