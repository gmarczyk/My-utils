package com.scheduler.presentation.framework;

import java.util.List;
import java.util.Optional;

import com.configuration.MainUIView;
import com.scheduler.interfaces.web.UIContentProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public abstract class GenericGridViewWithBasicManagement<T> implements UIContentProvider {

    protected final MainUIView mv;
    protected HorizontalLayout layout;

    protected AddEditDeleteContent addEditDeleteContent;
    protected Grid<T> grid;

    protected VerticalLayout gridLayout;

    public GenericGridViewWithBasicManagement(List<T> items, final MainUIView mv) {
        this.mv = mv;

        this.initComponents(items);
        configureGrid();
        configureManagementButtons();
        mv.setMainContent(layout);
    }

    public abstract void configureGrid();
    public abstract void configureManagementButtons();

    private void initComponents(List<T> items) {
        layout = new HorizontalLayout();
        layout.setSizeFull();

        gridLayout = new VerticalLayout();
        gridLayout.setSizeFull();

        grid = new Grid<T>();
        grid.setItems(items);
        grid.setSizeFull();

        gridLayout.addComponent(grid);
        gridLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

        this.addEditDeleteContent = new AddEditDeleteContent();
        addEditDeleteContent.getContent().setSizeFull();
        addEditDeleteContent.getContent().setHeight(30, Sizeable.Unit.PERCENTAGE);

        layout.addComponent(gridLayout);
        layout.addComponent(addEditDeleteContent.getContent());
        layout.setExpandRatio(gridLayout, 8.0f);
        layout.setExpandRatio(addEditDeleteContent.getContent(), 2.0f);
    }

    public HorizontalLayout getLayout() {
        return layout;
    }

    public AddEditDeleteContent getAddEditDeleteContent() {
        return addEditDeleteContent;
    }

    public Grid<T> getGrid() {
        return grid;
    }

    public VerticalLayout getGridLayout() {
        return gridLayout;
    }

    @Override
    public Component getContent() {
        return layout;
    }
}
