package com.sigmat.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;
import com.sigmat.controller.SigmatController;
import com.sigmat.dto.EstadisticaDTO;
import com.sigmat.dto.RecomendacionMigracionDTO;
import com.sigmat.model.Conductor;
import com.sigmat.utils.SesionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(DashboardView.class);
    private final SigmatController controller;
    private VerticalLayout statsContainer;

    public DashboardView(SigmatController controller) {
        logger.info("Entrando a DashboardView");
        this.controller = controller;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getElement().getStyle()
            .set("background", "#111827")
            .set("min-height", "100vh")
            .set("color", "#ffffff");

        if (!SesionManager.getInstancia().estaAutenticado()) {
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        add(crearHeader());
        statsContainer = new VerticalLayout();
        statsContainer.setPadding(true);
        statsContainer.setSpacing(true);
        statsContainer.setWidth("100%");
        statsContainer.getElement().getStyle()
            .set("background", "#111827")
            .set("min-height", "calc(100vh - 80px)");
        add(statsContainer);

        statsContainer.add(crearStatsFallback());
        cargarEstadisticas();
    }

    private Div crearHeader() {
        Div header = new Div();
        header.getElement().getStyle()
            .set("background", "#1f2937")
            .set("padding", "20px 24px")
            .set("border-bottom", "1px solid #374151")
            .set("display", "flex")
            .set("justify-content", "space-between")
            .set("align-items", "center");

        H1 titulo = new H1("Dashboard");
        titulo.getElement().getStyle()
            .set("color", "#ffffff")
            .set("font-size", "28px")
            .set("font-weight", "700")
            .set("margin", "0");

        Paragraph subtitulo = new Paragraph("Resumen general del sistema");
        subtitulo.getElement().getStyle()
            .set("color", "#9ca3af")
            .set("font-size", "14px")
            .set("margin", "4px 0 0 0");

        Div titleArea = new Div();
        titleArea.add(titulo, subtitulo);

        header.add(titleArea);
        return header;
    }

    private Div crearStatsFallback() {
        Div statsGrid = new Div();
        statsGrid.getElement().getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fit, minmax(220px, 1fr))")
            .set("gap", "16px")
            .set("padding", "24px");

        statsGrid.add(crearStatCard("Total Conductores", "0", VaadinIcon.USERS, "#3b82f6", "#dbeafe"));
        statsGrid.add(crearStatCard("Conductores Activos", "0", VaadinIcon.CHECK, "#22c55e", "#dcfce7"));
        statsGrid.add(crearStatCard("Total Vehículos", "0", VaadinIcon.CAR, "#f59e0b", "#ffedd5"));
        statsGrid.add(crearStatCard("Migrados", "0", VaadinIcon.ARROW_FORWARD, "#9333ea", "#f3e8ff"));
        statsGrid.add(crearStatCard("Ingresos Totales", "$0", VaadinIcon.MONEY, "#22c55e", "#dcfce7"));
        statsGrid.add(crearStatCard("Promedio Ingresos", "$0", VaadinIcon.TRENDING_UP, "#3b82f6", "#dbeafe"));

        return statsGrid;
    }

    private void cargarEstadisticas() {
        controller.obtenerEstadisticas().thenAccept(estadisticas -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                statsContainer.removeAll();
                crearStatsCards(estadisticas);
            }));
        });

        controller.obtenerRecomendaciones().thenAccept(recomendaciones -> {
            getUI().ifPresent(ui -> ui.access(() -> crearSeccionAnalisis(recomendaciones)));
        });

        controller.listarConductores().thenAccept(conductores -> {
            getUI().ifPresent(ui -> ui.access(() -> crearTablaResumen(conductores)));
        });
    }

    private void crearStatsCards(EstadisticaDTO estadisticas) {
        Div statsGrid = new Div();
        statsGrid.getElement().getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fit, minmax(220px, 1fr))")
            .set("gap", "16px")
            .set("padding", "24px");

        statsGrid.add(crearStatCard("Total Conductores", String.valueOf(estadisticas.getTotalConductores()), VaadinIcon.USERS, "#3b82f6", "#dbeafe"));
        statsGrid.add(crearStatCard("Conductores Activos", String.valueOf(estadisticas.getConductoresActivos()), VaadinIcon.CHECK, "#22c55e", "#dcfce7"));
        statsGrid.add(crearStatCard("Total Vehículos", String.valueOf(estadisticas.getTotalVehiculos()), VaadinIcon.CAR, "#f59e0b", "#ffedd5"));
        statsGrid.add(crearStatCard("Migrados", String.valueOf(estadisticas.getConductoresMigrados()), VaadinIcon.ARROW_FORWARD, "#9333ea", "#f3e8ff"));
        statsGrid.add(crearStatCard("Ingresos Totales", "$" + String.format("%,.0f", estadisticas.getIngresosTotales()), VaadinIcon.MONEY, "#22c55e", "#dcfce7"));
        statsGrid.add(crearStatCard("Promedio Ingresos", "$" + String.format("%,.0f", estadisticas.getPromedioIngresos()), VaadinIcon.TRENDING_UP, "#3b82f6", "#dbeafe"));

        statsContainer.add(statsGrid);
    }

    private Div crearStatCard(String label, String value, VaadinIcon icono, String colorIcon, String colorBg) {
        Div card = new Div();
        card.getElement().getStyle()
            .set("background", "#1f2937")
            .set("border-radius", "12px")
            .set("padding", "20px")
            .set("border", "1px solid #374151")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "12px");

        Div header = new Div();
        header.getElement().getStyle()
            .set("display", "flex")
            .set("justify-content", "space-between")
            .set("align-items", "flex-start");

        Div iconContainer = new Div();
        iconContainer.getElement().getStyle()
            .set("width", "40px")
            .set("height", "40px")
            .set("border-radius", "8px")
            .set("background", colorBg)
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");
        iconContainer.add(new Icon(icono));
        iconContainer.getElement().getStyle().set("color", colorIcon);

        header.add(iconContainer);

        Div valueArea = new Div();
        Paragraph valor = new Paragraph(value);
        valor.getElement().getStyle()
            .set("color", "#ffffff")
            .set("font-size", "24px")
            .set("font-weight", "700")
            .set("margin", "0");

        Paragraph labelP = new Paragraph(label);
        labelP.getElement().getStyle()
            .set("color", "#9ca3af")
            .set("font-size", "14px")
            .set("margin", "0");

        valueArea.add(valor, labelP);
        card.add(header, valueArea);
        return card;
    }

    private void crearSeccionAnalisis(List<RecomendacionMigracionDTO> recomendaciones) {
        Div section = new Div();
        section.getElement().getStyle()
            .set("background", "#1f2937")
            .set("border-radius", "12px")
            .set("border", "1px solid #374151")
            .set("margin", "24px")
            .set("overflow", "hidden");

        Div cardHeader = new Div();
        cardHeader.getElement().getStyle()
            .set("padding", "16px 20px")
            .set("border-bottom", "1px solid #374151");

        H3 tituloSeccion = new H3("Análisis de Migración");
        tituloSeccion.getElement().getStyle()
            .set("color", "#ffffff")
            .set("font-size", "16px")
            .set("font-weight", "600")
            .set("margin", "0");

        cardHeader.add(tituloSeccion);

        Div cardBody = new Div();
        cardBody.getElement().getStyle().set("padding", "20px");

        if (recomendaciones.isEmpty()) {
            Paragraph noData = new Paragraph("No hay recomendaciones de migración");
            noData.getElement().getStyle().set("color", "#9ca3af");
            cardBody.add(noData);
        } else {
            HorizontalLayout cardsLayout = new HorizontalLayout();
            cardsLayout.getElement().getStyle()
                .set("display", "flex")
                .set("gap", "16px")
                .set("width", "100%");

            for (int i = 0; i < Math.min(recomendaciones.size(), 3); i++) {
                RecomendacionMigracionDTO rec = recomendaciones.get(i);
                cardsLayout.add(crearRecomendacionCard(rec));
            }

            cardBody.add(cardsLayout);
        }

        section.add(cardHeader, cardBody);
        statsContainer.add(section);
    }

    private Div crearRecomendacionCard(RecomendacionMigracionDTO rec) {
        Div card = new Div();
        String prioridad = rec.getPrioridad().toLowerCase();
        String borderColor = switch (prioridad) {
            case "alta" -> "#ef4444";
            case "media" -> "#f59e0b";
            default -> "#22c55e";
        };
        card.getElement().getStyle()
            .set("background", "#374151")
            .set("border-radius", "8px")
            .set("padding", "16px")
            .set("border-left", "4px solid " + borderColor)
            .set("flex", "1");

        H3 nombre = new H3(rec.getNombreConductor());
        nombre.getElement().getStyle()
            .set("color", "#ffffff")
            .set("font-size", "16px")
            .set("margin", "0 0 8px 0");

        Paragraph ingresos = new Paragraph("Ingresos: $" + String.format("%,.0f", rec.getIngresosActuales()));
        ingresos.getElement().getStyle()
            .set("color", "#9ca3af")
            .set("font-size", "14px")
            .set("margin", "0 0 4px 0");

        Paragraph mejora = new Paragraph("Mejora estimada: +$" + String.format("%,.0f", rec.getMejoraEconomica()));
        mejora.getElement().getStyle()
            .set("color", "#22c55e")
            .set("font-size", "14px")
            .set("font-weight", "600")
            .set("margin", "0 0 8px 0");

        Div badge = new Div();
        badge.getElement().getStyle()
            .set("display", "inline-flex")
            .set("align-items", "center")
            .set("padding", "4px 12px")
            .set("border-radius", "12px")
            .set("font-size", "12px")
            .set("font-weight", "600");
        
        String badgeBg, badgeColor;
        switch (rec.getPrioridad()) {
            case "ALTA" -> { badgeBg = "#dcfce7"; badgeColor = "#15803d"; }
            case "MEDIA" -> { badgeBg = "#ffedd5"; badgeColor = "#c2410c"; }
            default -> { badgeBg = "#f1f5f9"; badgeColor = "#64748b"; }
        }
        badge.getElement().getStyle()
            .set("background", badgeBg)
            .set("color", badgeColor);
        badge.add(new Paragraph(rec.getPrioridad()));

        card.add(nombre, ingresos, mejora, badge);
        return card;
    }

    private void crearTablaResumen(List<Conductor> conductores) {
        Div section = new Div();
        section.getElement().getStyle()
            .set("background", "#1f2937")
            .set("border-radius", "12px")
            .set("border", "1px solid #374151")
            .set("margin", "24px")
            .set("overflow", "hidden");

        Div cardHeader = new Div();
        cardHeader.getElement().getStyle()
            .set("padding", "16px 20px")
            .set("border-bottom", "1px solid #374151");

        H3 titulo = new H3("Conductores Recientes");
        titulo.getElement().getStyle()
            .set("color", "#ffffff")
            .set("font-size", "16px")
            .set("font-weight", "600")
            .set("margin", "0");

        cardHeader.add(titulo);

        Grid<Conductor> grid = new Grid<>();
        grid.setItems(conductores);
        grid.setWidth("100%");
        grid.setHeight("300px");
        grid.getElement().getStyle()
            .set("background", "#1f2937")
            .set("--vaadin-grid-background", "#1f2937");

        grid.addColumn(Conductor::getNombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(Conductor::getCedula).setHeader("Cédula").setSortable(true);
        grid.addColumn(Conductor::getTipoVehiculo).setHeader("Tipo Vehículo").setSortable(true);
        grid.addColumn(c -> "$" + String.format("%,.0f", c.getIngresosDiarios())).setHeader("Ingresos Diarios").setSortable(true);
        grid.addColumn(c -> {
            Div badge = new Div();
            badge.getElement().getStyle()
                .set("display", "inline-flex")
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "12px");
            String badgeBg, badgeColor;
            switch (c.getEstado()) {
                case "ACTIVO" -> { badgeBg = "#dcfce7"; badgeColor = "#15803d"; }
                case "MIGRADO" -> { badgeBg = "#dbeafe"; badgeColor = "#1d4ed8"; }
                case "EN_MIGRACION" -> { badgeBg = "#ffedd5"; badgeColor = "#c2410c"; }
                default -> { badgeBg = "#f1f5f9"; badgeColor = "#64748b"; }
            }
            badge.getElement().getStyle()
                .set("background", badgeBg)
                .set("color", badgeColor);
            badge.add(new Paragraph(c.getEstado()));
            return badge;
        }).setHeader("Estado");

        Div cardBody = new Div();
        cardBody.getElement().getStyle().set("padding", "20px");
        cardBody.add(grid);

        section.add(cardHeader, cardBody);
        statsContainer.add(section);
    }
}