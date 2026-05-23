package com.sigmat.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.sigmat.controller.SigmatController;
import com.sigmat.model.Vehiculo;
import com.sigmat.utils.SesionManager;

import java.util.List;

@Route(value = "vehiculos", layout = MainLayout.class)
public class VehiculosView extends VerticalLayout {

    private final SigmatController controller;
    private Grid<Vehiculo> gridVehiculos;
    private Dialog dialogFormulario;
    private Vehiculo vehiculoEnEdicion;

    public VehiculosView(SigmatController controller) {
        this.controller = controller;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        if (!SesionManager.getInstancia().estaAutenticado()) {
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        add(crearHeader());
        add(crearToolbar());

        crearGrid();
        add(gridVehiculos);
        expand(gridVehiculos);

        cargarVehiculos();
    }

    private Div crearHeader() {
        Div header = new Div();
        header.addClassName("page-header");

        Div titleArea = new Div();
        H1 titulo = new H1("Vehículos");
        titulo.addClassName("page-title");
        Paragraph subtitulo = new Paragraph("Gestión de vehículos del sistema");
        subtitulo.addClassName("page-subtitle");
        titleArea.add(titulo, subtitulo);

        header.add(titleArea);
        return header;
    }

    private Div crearToolbar() {
        Div toolbar = new Div();
        toolbar.addClassName("stats-grid");
        toolbar.getElement().getStyle().set("margin-bottom", "24px");

        TextField campoBusqueda = new TextField();
        campoBusqueda.setPlaceholder("Buscar vehículo por placa o propietario...");
        campoBusqueda.setWidth("400px");
        campoBusqueda.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        campoBusqueda.addValueChangeListener(e -> filtrarVehiculos(e.getValue()));

        Button btnAgregar = new Button("Agregar Vehículo", new Icon(VaadinIcon.PLUS));
        btnAgregar.getElement().getStyle()
            .set("background", "var(--color-primary)")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("font-weight", "600")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnAgregar.addClickListener(e -> abrirFormulario(null));

        toolbar.add(campoBusqueda, btnAgregar);
        return toolbar;
    }

    private void crearGrid() {
        gridVehiculos = new Grid<>();
        gridVehiculos.setSizeFull();

        gridVehiculos.addColumn(Vehiculo::getTipoVehiculo).setHeader("Tipo").setSortable(true).setWidth("130px");
        gridVehiculos.addColumn(Vehiculo::getPlaca).setHeader("Placa").setSortable(true).setWidth("120px");
        gridVehiculos.addColumn(Vehiculo::getPropietario).setHeader("Propietario").setSortable(true).setFlexGrow(1);
        gridVehiculos.addColumn(Vehiculo::getConductorAsignado).setHeader("Conductor Asignado").setWidth("160px");
        gridVehiculos.addColumn(v -> {
            String estado = v.getEstado();
            Div badge = new Div();
            badge.addClassName("status-badge");
            String badgeClass = switch (estado) {
                case "ACTIVO" -> "activo";
                case "EN_MANTENIMIENTO" -> "en_migracion";
                case "DADO_BAJA" -> "inactivo";
                default -> "inactivo";
            };
            badge.addClassName(badgeClass);
            badge.add(new Paragraph(estado != null ? estado.replace("_", " ") : ""));
            return badge;
        }).setHeader("Estado").setWidth("140px");
        gridVehiculos.addColumn(Vehiculo::getObservaciones).setHeader("Observaciones").setFlexGrow(1);
        gridVehiculos.addComponentColumn(this::crearBotonesAccion).setHeader("Acciones").setWidth("160px");

        dialogFormulario = new Dialog();
        dialogFormulario.setWidth("600px");
    }

    private HorizontalLayout crearBotonesAccion(Vehiculo vehiculo) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        Button btnEditar = new Button(new Icon(VaadinIcon.EDIT));
        btnEditar.addClassName("action-btn");
        btnEditar.addClassName("edit");
        btnEditar.addClickListener(e -> abrirFormulario(vehiculo));

        Button btnEliminar = new Button(new Icon(VaadinIcon.TRASH));
        btnEliminar.addClassName("action-btn");
        btnEliminar.addClassName("delete");
        btnEliminar.addClickListener(e -> confirmarEliminacion(vehiculo));

        layout.add(btnEditar, btnEliminar);
        return layout;
    }

    private void abrirFormulario(Vehiculo vehiculo) {
        vehiculoEnEdicion = vehiculo;
        dialogFormulario.removeAll();

        VerticalLayout formContent = new VerticalLayout();
        formContent.setSpacing(true);
        formContent.setPadding(true);
        formContent.getElement().getStyle().set("padding", "32px");

        H3 tituloForm = new H3(vehiculo == null ? "Nuevo Vehículo" : "Editar Vehículo");
        tituloForm.getElement().getStyle().set("color", "var(--text-dark)").set("margin", "0 0 24px 0");

        ComboBox<String> cmbTipo = new ComboBox<>("Tipo de Vehículo");
        cmbTipo.setItems("Carro de Mula", "Motocarro");
        cmbTipo.setWidth("100%");
        if (vehiculo != null) cmbTipo.setValue(vehiculo.getTipoVehiculo());

        TextField txtPlaca = new TextField("Placa");
        txtPlaca.setWidth("100%");
        if (vehiculo != null) txtPlaca.setValue(nonNull(vehiculo.getPlaca()));

        TextField txtPropietario = new TextField("Propietario");
        txtPropietario.setWidth("100%");
        if (vehiculo != null) txtPropietario.setValue(nonNull(vehiculo.getPropietario()));

        ComboBox<String> cmbEstado = new ComboBox<>("Estado");
        cmbEstado.setItems("ACTIVO", "INACTIVO", "EN_MANTENIMIENTO", "DADO_BAJA");
        cmbEstado.setWidth("100%");
        if (vehiculo != null) cmbEstado.setValue(vehiculo.getEstado());

        TextField txtObservaciones = new TextField("Observaciones");
        txtObservaciones.setWidth("100%");
        if (vehiculo != null && vehiculo.getObservaciones() != null) {
            txtObservaciones.setValue(vehiculo.getObservaciones());
        }

        HorizontalLayout botonesForm = new HorizontalLayout();
        botonesForm.setWidth("100%");
        botonesForm.setJustifyContentMode(JustifyContentMode.END);
        botonesForm.setSpacing(true);
        botonesForm.getElement().getStyle().set("margin-top", "16px");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getElement().getStyle()
            .set("background", "#f1f5f9")
            .set("color", "var(--text-secondary)")
            .set("border-radius", "8px")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnCancelar.addClickListener(e -> dialogFormulario.close());

        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.getElement().getStyle()
            .set("background", "var(--color-primary)")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("font-weight", "600")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnGuardar.addClickListener(e -> guardarVehiculo(
            cmbTipo.getValue(),
            txtPlaca.getValue(),
            txtPropietario.getValue(),
            cmbEstado.getValue(),
            txtObservaciones.getValue()
        ));

        botonesForm.add(btnCancelar, btnGuardar);

        formContent.add(tituloForm, cmbTipo, txtPlaca, txtPropietario, cmbEstado, txtObservaciones, botonesForm);
        dialogFormulario.add(formContent);
        dialogFormulario.open();
    }

    private String nonNull(String value) {
        return value != null ? value : "";
    }

    private void guardarVehiculo(String tipo, String placa, String propietario, String estado, String observaciones) {
        if (vehiculoEnEdicion == null) {
            Vehiculo nuevo = new Vehiculo();
            nuevo.setTipoVehiculo(tipo);
            nuevo.setPlaca(placa.toUpperCase());
            nuevo.setPropietario(propietario);
            nuevo.setEstado(estado);
            nuevo.setObservaciones(observaciones);

            controller.registrarVehiculo(nuevo).thenAccept(id -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Vehículo registrado exitosamente", 3000, Notification.Position.TOP_CENTER);
                    dialogFormulario.close();
                    cargarVehiculos();
                }));
            });
        } else {
            vehiculoEnEdicion.setTipoVehiculo(tipo);
            vehiculoEnEdicion.setPlaca(placa.toUpperCase());
            vehiculoEnEdicion.setPropietario(propietario);
            vehiculoEnEdicion.setEstado(estado);
            vehiculoEnEdicion.setObservaciones(observaciones);

            controller.actualizarVehiculo(vehiculoEnEdicion.getId(), vehiculoEnEdicion).thenAccept(success -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Vehículo actualizado", 3000, Notification.Position.TOP_CENTER);
                    dialogFormulario.close();
                    cargarVehiculos();
                }));
            });
        }
    }

    private void confirmarEliminacion(Vehiculo vehiculo) {
        Dialog confirmarDialog = new Dialog();
        confirmarDialog.setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setAlignItems(Alignment.CENTER);
        content.getElement().getStyle().set("padding", "32px").set("text-align", "center");

        Icon warningIcon = new Icon(VaadinIcon.WARNING);
        warningIcon.getElement().getStyle().set("color", "var(--danger)").set("width", "48px").set("height", "48px");

        H3 mensaje = new H3("¿Está seguro de eliminar este vehículo?");
        mensaje.getElement().getStyle().set("color", "var(--text-dark)").set("margin", "16px 0");

        HorizontalLayout botones = new HorizontalLayout();
        botones.setSpacing(true);

        Button btnSi = new Button("Sí, Eliminar");
        btnSi.getElement().getStyle()
            .set("background", "var(--danger)")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnSi.addClickListener(ev -> {
            controller.eliminarVehiculo(vehiculo.getId()).thenAccept(success -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Vehículo eliminado", 3000, Notification.Position.TOP_CENTER);
                    confirmarDialog.close();
                    cargarVehiculos();
                }));
            });
        });

        Button btnNo = new Button("Cancelar");
        btnNo.getElement().getStyle()
            .set("background", "#f1f5f9")
            .set("color", "var(--text-secondary)")
            .set("border-radius", "8px")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnNo.addClickListener(ev -> confirmarDialog.close());

        botones.add(btnSi, btnNo);
        content.add(warningIcon, mensaje, botones);
        confirmarDialog.add(content);
        confirmarDialog.open();
    }

    private void filtrarVehiculos(String texto) {
        controller.listarVehiculos().thenAccept(vehiculos -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                if (texto == null || texto.isEmpty()) {
                    gridVehiculos.setItems(vehiculos);
                } else {
                    String filtro = texto.toLowerCase();
                    List<Vehiculo> filtrados = vehiculos.stream()
                        .filter(v -> (v.getPlaca() != null && v.getPlaca().toLowerCase().contains(filtro)) ||
                                    (v.getPropietario() != null && v.getPropietario().toLowerCase().contains(filtro)))
                        .toList();
                    gridVehiculos.setItems(filtrados);
                }
            }));
        });
    }

    private void cargarVehiculos() {
        controller.listarVehiculos().thenAccept(vehiculos -> {
            getUI().ifPresent(ui -> ui.access(() -> gridVehiculos.setItems(vehiculos)));
        });
    }
}