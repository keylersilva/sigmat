package com.sigmat.view;

import com.sigmat.utils.SesionManager;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainLayout extends AppLayout {

    private static final Logger logger = LoggerFactory.getLogger(MainLayout.class);

    public MainLayout() {
        logger.info("Construyendo MainLayout");

        setPrimarySection(Section.DRAWER);

        addToDrawer(crearSidebar());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        setDrawerOpened(true);
    }

    private Div crearSidebar() {
        Div sidebar = new Div();
        sidebar.getStyle()
            .set("background", "#0f172a")
            .set("color", "white")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("justify-content", "space-between")
            .set("padding", "20px")
            .set("height", "100vh")
            .set("box-sizing", "border-box")
            .set("min-width", "260px");

        Div top = new Div();

        Div logo = new Div();
        logo.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "12px")
            .set("margin-bottom", "40px");

        Icon icon = new Icon(VaadinIcon.TRUCK);
        icon.getStyle()
            .set("color", "#38bdf8")
            .set("font-size", "28px");

        Span title = new Span("SIGMAT");
        title.getStyle()
            .set("font-size", "24px")
            .set("font-weight", "bold");

        logo.add(icon, title);

        Div nav = new Div();
        nav.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "10px");

        nav.add(crearNavItem("Dashboard", VaadinIcon.DASHBOARD, "dashboard"));
        nav.add(crearNavItem("Conductores", VaadinIcon.USERS, "conductores"));
        nav.add(crearNavItem("Vehículos", VaadinIcon.CAR, "vehiculos"));
        nav.add(crearNavItem("Reportes", VaadinIcon.CHART, "reportes"));

        top.add(logo, nav);

        Div footer = new Div();
        Button logout = new Button("Cerrar Sesión", new Icon(VaadinIcon.SIGN_OUT));
        logout.getStyle()
            .set("width", "100%")
            .set("background", "#dc2626")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "10px")
            .set("padding", "12px")
            .set("cursor", "pointer");

        logout.addClickListener(e -> {
            SesionManager.getInstancia().cerrarSesion();
            getUI().ifPresent(ui -> ui.navigate(""));
            Notification.show("Sesión cerrada", 2000, Notification.Position.TOP_CENTER);
        });

        footer.add(logout);
        sidebar.add(top, footer);
        return sidebar;
    }

    private Div crearNavItem(String texto, VaadinIcon icono, String ruta) {
        Div item = new Div();
        item.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "12px")
            .set("padding", "14px")
            .set("border-radius", "12px")
            .set("cursor", "pointer")
            .set("transition", "0.2s")
            .set("background", "#1e293b")
            .set("color", "white")
            .set("font-size", "16px");

        item.add(new Icon(icono), new Span(texto));

        item.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ruta)));
        return item;
    }
}
