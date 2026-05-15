package com.sigmat.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.sigmat.controller.SigmatController;
import com.sigmat.dto.LoginRequest;
import com.sigmat.model.Usuario;
import com.sigmat.utils.SesionManager;

@Route("")
public class LoginView extends VerticalLayout {

    private final SigmatController controller;

    public LoginView(SigmatController controller) {
        this.controller = controller;
        setSizeFull();
        addClassName("login-container");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Div card = new Div();
        card.addClassName("login-card");

        Div logoContainer = new Div();
        logoContainer.addClassName("login-logo");
        Icon logoIcon = new Icon(VaadinIcon.TRUCK);
        logoContainer.add(logoIcon);

        H1 titulo = new H1("SIGMAT");
        titulo.addClassName("login-title");

        Paragraph subtitulo = new Paragraph("Sistema Inteligente de Gestión de Movilidad y Atracción Animal");
        subtitulo.addClassName("login-subtitle");

        Div formContainer = new Div();
        formContainer.addClassName("login-form");

        TextField usuarioField = new TextField();
        usuarioField.setLabel("Usuario");
        usuarioField.setPlaceholder("Ingrese su usuario");
        usuarioField.setWidth("100%");
        usuarioField.setPrefixComponent(new Icon(VaadinIcon.USER));

        PasswordField contrasenaField = new PasswordField();
        contrasenaField.setLabel("Contraseña");
        contrasenaField.setPlaceholder("Ingrese su contraseña");
        contrasenaField.setWidth("100%");
        contrasenaField.setPrefixComponent(new Icon(VaadinIcon.LOCK));

        Button btnLogin = new Button("Iniciar Sesión", new Icon(VaadinIcon.SIGN_IN));
        btnLogin.addClassName("login-btn");
        btnLogin.setWidth("100%");

        btnLogin.addClickListener(e -> {
            String usuario = usuarioField.getValue().trim();
            String contrasena = contrasenaField.getValue().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Notification.show("Por favor complete todos los campos", 3000, Position.TOP_CENTER);
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("Verificando...");

            controller.iniciarSesion(new LoginRequest(usuario, contrasena)).thenAccept(u -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");
                    if (u != null) {
                        SesionManager.getInstancia().iniciarSesion(u);
                        ui.navigate("dashboard");
                        Notification.show("Bienvenido, " + u.getNombreCompleto(), 3000, Position.TOP_CENTER);
                    } else {
                        Notification.show("Usuario o contraseña incorrectos", 5000, Position.TOP_CENTER);
                    }
                }));
            });
        });

        formContainer.add(usuarioField, contrasenaField, btnLogin);
        card.add(logoContainer, titulo, subtitulo, formContainer);
        add(card);
    }
}