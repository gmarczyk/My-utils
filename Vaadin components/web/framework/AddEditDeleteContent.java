package com.scheduler.presentation.framework;

import com.scheduler.interfaces.web.UIContentProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class AddEditDeleteContent implements UIContentProvider {

    private final VerticalLayout layout = new VerticalLayout();

    private Button addButton = new Button("Dodaj");
    private Button editButton = new Button("Edytuj");
    private Button deleteButton = new Button("Usun");

    public AddEditDeleteContent() {
        addButton.setSizeFull();
        editButton.setSizeFull();
        deleteButton.setSizeFull();

        this.layout.addComponents(addButton,editButton,deleteButton);
        this.layout.setSizeUndefined();
    }

    public VerticalLayout getLayout() {
        return layout;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    @Override
    public Component getContent() {
        return this.layout;
    }

    public void setEnabled(final boolean enabled) {
        addButton.setEnabled(enabled);
        editButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }
}
