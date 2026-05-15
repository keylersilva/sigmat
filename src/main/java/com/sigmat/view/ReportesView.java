package com.sigmat.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.sigmat.controller.SigmatController;
import com.sigmat.dto.EstadisticaDTO;
import com.sigmat.dto.RecomendacionMigracionDTO;
import com.sigmat.utils.SesionManager;

@Route(value = "reportes", layout = MainLayout.class)
public class ReportesView extends VerticalLayout {

    private final SigmatController controller;
    private Grid<RecomendacionMigracionDTO> gridRecomendaciones;
    private ComboBox<String> cmbFiltroEstado;

    public ReportesView(SigmatController controller) {
        this.controller = controller;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getElement().getStyle().set("background", "#1e1e1e");
        addClassName("page-content");

        if (!SesionManager.getInstancia().estaAutenticado()) {
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        if (!SesionManager.getInstancia().esAdministrador()) {
            Notification.show("Solo administradores pueden acceder a reportes", 5000, Notification.Position.TOP_CENTER);
            getUI().ifPresent(ui -> ui.navigate("dashboard"));
            return;
        }

        add(crearHeader());
        add(crearFiltros());
        add(crearSeccionEstadisticas());
        add(crearSeccionAnalisis());
        cargarReportes();
    }

    private Div crearHeader() {
        Div header = new Div();
        header.addClassName("page-header");

        Div titleArea = new Div();
        H1 titulo = new H1("Reportes y Análisis");
        titulo.addClassName("page-title");
        Paragraph subtitulo = new Paragraph("Análisis detallado de migraciones y estadísticas del sistema");
        subtitulo.addClassName("page-subtitle");
        titleArea.add(titulo, subtitulo);

        header.add(titleArea);
        return header;
    }

    private Div crearFiltros() {
        Div filtrosContainer = new Div();
        filtrosContainer.addClassName("stats-grid");
        filtrosContainer.getElement().getStyle().set("margin-bottom", "24px");

        H3 labelFiltro = new H3("Filtrar por prioridad:");
        labelFiltro.getElement().getStyle().set("color", "var(--text-secondary)").set("margin", "0 16px 0 0")
            .set("align-self", "center");

        cmbFiltroEstado = new ComboBox<>();
        cmbFiltroEstado.setItems("Todos", "ALTA", "MEDIA", "BAJA");
        cmbFiltroEstado.setValue("Todos");
        cmbFiltroEstado.setWidth("200px");

        Button btnActualizar = new Button("Actualizar", new Icon(VaadinIcon.REFRESH));
        btnActualizar.getElement().getStyle()
            .set("background", "var(--color-primary)")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("font-weight", "600")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnActualizar.addClickListener(e -> cargarReportes());

        filtrosContainer.add(labelFiltro, cmbFiltroEstado, btnActualizar);
        return filtrosContainer;
    }

    private Div crearSeccionEstadisticas() {
        Div section = new Div();
        section.addClassName("modern-card");
        section.setWidth("100%");

        Div cardHeader = new Div();
        cardHeader.addClassName("card-header");

        H3 titulo = new H3("Resumen General");
        titulo.addClassName("card-title");

        cardHeader.add(titulo);

        Div cardBody = new Div();
        cardBody.addClassName("card-body");

        controller.obtenerEstadisticas().thenAccept(dto -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                cardBody.removeAll();
                cardBody.add(
                    crearFilaEstadistica("Total Conductores:", String.valueOf(dto.getTotalConductores())),
                    crearFilaEstadistica("Conductores Activos:", String.valueOf(dto.getConductoresActivos())),
                    crearFilaEstadistica("Conductores Migrados:", String.valueOf(dto.getConductoresMigrados())),
                    crearFilaEstadistica("Total Vehículos:", String.valueOf(dto.getTotalVehiculos())),
                    crearFilaEstadistica("Ingresos Totales:", "$" + String.format("%,.0f", dto.getIngresosTotales())),
                    crearFilaEstadistica("Promedio Ingresos:", "$" + String.format("%,.0f", dto.getPromedioIngresos()))
                );
            }));
        });

        section.add(cardHeader, cardBody);
        return section;
    }

    private HorizontalLayout crearFilaEstadistica(String label, String valor) {
        HorizontalLayout fila = new HorizontalLayout();
        fila.setWidth("100%");
        fila.setJustifyContentMode(JustifyContentMode.BETWEEN);
        fila.getElement().getStyle().set("padding", "12px 0").set("border-bottom", "1px solid var(--border-color)");

        Paragraph lbl = new Paragraph(label);
        lbl.getElement().getStyle().set("color", "var(--text-secondary)").set("font-size", "14px").set("margin", "0");

        Paragraph val = new Paragraph(valor);
        val.getElement().getStyle().set("color", "var(--text-dark)").set("font-size", "16px").set("font-weight", "600").set("margin", "0");

        fila.add(lbl, val);
        return fila;
    }

    private Div crearSeccionAnalisis() {
        Div section = new Div();
        section.addClassName("modern-card");
        section.setWidth("100%");

        Div cardHeader = new Div();
        cardHeader.addClassName("card-header");

        H3 titulo = new H3("Análisis de Migración");
        titulo.addClassName("card-title");

        cardHeader.add(titulo);

        gridRecomendaciones = new Grid<>();
        gridRecomendaciones.setWidth("100%");
        gridRecomendaciones.setHeight("350px");

        gridRecomendaciones.addColumn(RecomendacionMigracionDTO::getNombreConductor)
            .setHeader("Conductor").setSortable(true).setFlexGrow(1);
        gridRecomendaciones.addColumn(dto -> "$" + String.format("%,.0f", dto.getIngresosActuales()))
            .setHeader("Ingresos Actuales").setWidth("140px");
        gridRecomendaciones.addColumn(dto -> "$" + String.format("%,.0f", dto.getIngresosEstimados()))
            .setHeader("Ingresos Estimados").setWidth("150px");
        gridRecomendaciones.addColumn(dto -> "+$" + String.format("%,.0f", dto.getMejoraEconomica()))
            .setHeader("Mejora").setWidth("120px");
        gridRecomendaciones.addColumn(dto -> {
            Div badge = new Div();
            badge.addClassName("status-badge");
            String badgeClass = switch (dto.getPrioridad()) {
                case "ALTA" -> "activo";
                case "MEDIA" -> "en_migracion";
                default -> "inactivo";
            };
            badge.addClassName(badgeClass);
            badge.add(new Paragraph(dto.getPrioridad()));
            return badge;
        }).setHeader("Prioridad").setWidth("100px");
        gridRecomendaciones.addColumn(RecomendacionMigracionDTO::getRazon)
            .setHeader("Razón").setFlexGrow(1);

        Div cardBody = new Div();
        cardBody.addClassName("card-body");
        cardBody.add(gridRecomendaciones);

        section.add(cardHeader, cardBody);
        return section;
    }

    private void cargarReportes() {
        controller.obtenerRecomendaciones().thenAccept(recomendaciones -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                String filtro = cmbFiltroEstado.getValue();
                if (filtro == null || "Todos".equals(filtro)) {
                    gridRecomendaciones.setItems(recomendaciones);
                } else {
                    gridRecomendaciones.setItems(
                        recomendaciones.stream()
                            .filter(r -> filtro.equals(r.getPrioridad()))
                            .toList()
                    );
                }
            }));
        });
    }
}