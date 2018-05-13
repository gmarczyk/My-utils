package com.scheduler.interfaces.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.scheduler.application.LoginService;
import com.scheduler.presentation.framework.LoginPanel;
import com.scheduler.shared.core.ResultCallback;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class UserLoggingPage {

    @Autowired
    private LoginService loginService;

    private final ResultCallback<String> callback;
    private VerticalLayout viewLayout;
    private LoginPanel loginPanel;

    public UserLoggingPage(ResultCallback<String> callback) {
        this.callback = callback;
        this.loginPanel = new LoginPanel("Logowanie użytkownika");
        this.loginPanel.setWidth(20, Sizeable.Unit.PERCENTAGE);
        this.loginPanel.getLayout().setSizeFull();
        this.loginPanel.getLayout().setComponentAlignment(loginPanel.getLogInButton(), Alignment.MIDDLE_CENTER);

        viewLayout = new VerticalLayout();
        viewLayout.setSizeFull();
        viewLayout.addComponent(loginPanel);
        viewLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
        //this.viewLayout.addStyleName("mainContent");

        this.loginPanel.setLogInBackendListener(new UserLoginBackendListener());
    }

    public Component getContent() {
        return viewLayout;
    }

    public String getCurrentUname() {
        return this.loginPanel.username();
    }

    private class UserLoginBackendListener implements Button.ClickListener {

        @Override
        public void buttonClick(final Button.ClickEvent clickEvent) {
            if(!isUserInputValid(loginPanel.username(),loginPanel.password())) {
                Notification.show("Niepoprawne dane", Notification.Type.WARNING_MESSAGE);
                return;
            }

            if(!loginService.tryLoggingIn(loginPanel.username(),loginPanel.password())) {
                Notification.show("Bledny login lub haslo");
                return;
            }

            callback.callback(loginPanel.username());
        }

        private boolean isUserInputValid(String uname, String pass) {
            return (StringUtils.isNotBlank(uname) && StringUtils.isNotBlank(pass));
        }
    }

}
