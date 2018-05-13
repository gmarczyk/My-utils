package com.scheduler.presentation.framework;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginPanel extends Panel {

    private final TextField username;
    private final PasswordField passwordField;
    private final Button logInButton;

    private VerticalLayout layout;

    public LoginPanel(final String caption) {
        username = new TextField("Login");
        passwordField = new PasswordField("Hasło");
        logInButton = new Button("Zaloguj");

        prepareContent(caption);
    }

    public String username() {
        return username.getValue();
    }

    public String password() {
        return passwordField.getValue();
    }

    public void setLogInBackendListener(Button.ClickListener logInBackendButton) {
        logInButton.addClickListener(logInBackendButton);
    }

    private void prepareContent(final String caption) {
        layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.addComponents(username,passwordField,logInButton);

        layout.setComponentAlignment(username,Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(passwordField,Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(logInButton,Alignment.MIDDLE_RIGHT);

        this.setCaption(caption);
        this.setContent(layout);
        this.setSizeUndefined();
    }

    public TextField getUsername() {
        return username;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLogInButton() {
        return logInButton;
    }

    public VerticalLayout getLayout() {
        return layout;
    }
}
