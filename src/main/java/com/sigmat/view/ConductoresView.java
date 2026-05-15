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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.sigmat.controller.SigmatController;
import com.sigmat.model.Conductor;
import com.sigmat.utils.SesionManager;

import java.time.LocalDate;
import java.util.List;

@Route(value = "conductores", layout = MainLayout.class)
public class ConductoresView extends VerticalLayout {

    private final SigmatController controller;
    private Grid<Conductor> gridConductores;
    private TextField campoBusqueda;
    private Dialog dialogFormulario;
    private Conductor conductorEnEdicion;

    public ConductoresView(SigmatController controller) {
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

        add(crearHeader());
        add(crearToolbar());
        add(crearGrid());
        cargarConductores();
    }

    private Div crearHeader() {
        Div header = new Div();
        header.addClassName("page-header");

        Div titleArea = new Div();
        H1 titulo = new H1("Conductores");
        titulo.addClassName("page-title");
        Paragraph subtitulo = new Paragraph("Gestión completa de conductores del sistema");
        subtitulo.addClassName("page-subtitle");
        titleArea.add(titulo, subtitulo);

        header.add(titleArea);
        return header;
    }

    private Div crearToolbar() {
        Div toolbar = new Div();
        toolbar.addClassName("stats-grid");
        toolbar.getElement().getStyle().set("margin-bottom", "24px");

        campoBusqueda = new TextField();
        campoBusqueda.setPlaceholder("Buscar conductor por nombre o cédula...");
        campoBusqueda.setWidth("400px");
        campoBusqueda.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        campoBusqueda.addValueChangeListener(e -> filtrarConductores(e.getValue()));

        Button btnAgregar = new Button("Agregar Conductor", new Icon(VaadinIcon.PLUS));
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

    private Div crearGrid() {
        Div gridContainer = new Div();
        gridContainer.addClassName("modern-card");

        gridConductores = new Grid<>();
        gridConductores.setWidth("100%");
        gridConductores.setHeight("500px");

        gridConductores.addColumn(Conductor::getNombre).setHeader("Nombre").setSortable(true).setFlexGrow(1);
        gridConductores.addColumn(Conductor::getCedula).setHeader("Cédula").setSortable(true).setWidth("130px");
        gridConductores.addColumn(Conductor::getTelefono).setHeader("Teléfono").setWidth("120px");
        gridConductores.addColumn(Conductor::getDireccion).setHeader("Dirección").setFlexGrow(1);
        gridConductores.addColumn(Conductor::getTipoVehiculo).setHeader("Tipo").setWidth("130px");
        gridConductores.addColumn(c -> "$" + String.format("%,.0f", c.getIngresosDiarios())).setHeader("Ingresos").setWidth("120px");
        gridConductores.addColumn(c -> {
            String estado = c.getEstado();
            Div badge = new Div();
            badge.addClassName("status-badge");
            String badgeClass = switch (estado) {
                case "ACTIVO" -> "activo";
                case "MIGRADO" -> "migrado";
                case "EN_MIGRACION" -> "en_migracion";
                default -> "inactivo";
            };
            badge.addClassName(badgeClass);
            badge.add(new Paragraph(estado));
            return badge;
        }).setHeader("Estado").setWidth("120px");
        gridConductores.addComponentColumn(this::crearBotonesAccion).setHeader("Acciones").setWidth("200px");

        Div cardBody = new Div();
        cardBody.addClassName("card-body");
        cardBody.add(gridConductores);

        gridContainer.add(cardBody);
        dialogFormulario = new Dialog();
        dialogFormulario.setWidth("600px");
        dialogFormulario.getElement().getStyle().set("border-radius", "16px");

        return gridContainer;
    }

    private HorizontalLayout crearBotonesAccion(Conductor conductor) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        Button btnEditar = new Button(new Icon(VaadinIcon.EDIT));
        btnEditar.addClassName("action-btn");
        btnEditar.addClassName("edit");
        btnEditar.addClickListener(e -> abrirFormulario(conductor));

        Button btnEliminar = new Button(new Icon(VaadinIcon.TRASH));
        btnEliminar.addClassName("action-btn");
        btnEliminar.addClassName("delete");
        btnEliminar.addClickListener(e -> confirmarEliminacion(conductor));

        Button btnMigrar = new Button(new Icon(VaadinIcon.ARROW_FORWARD));
        btnMigrar.addClassName("action-btn");
        btnMigrar.addClassName("migrate");
        btnMigrar.addClickListener(e -> migrarConductor(conductor));

        layout.add(btnEditar, btnEliminar, btnMigrar);
        return layout;
    }

    private void abrirFormulario(Conductor conductor) {
        conductorEnEdicion = conductor;
        dialogFormulario.removeAll();

        VerticalLayout formContent = new VerticalLayout();
        formContent.setSpacing(true);
        formContent.setPadding(true);
        formContent.getElement().getStyle().set("padding", "32px");

        H3 tituloForm = new H3(conductor == null ? "Nuevo Conductor" : "Editar Conductor");
        tituloForm.getElement().getStyle().set("color", "var(--text-dark)").set("margin", "0 0 24px 0");

        TextField txtNombre = new TextField("Nombre Completo");
        txtNombre.setWidth("100%");
        if (conductor != null) txtNombre.setValue(nonNull(conductor.getNombre()));

        TextField txtCedula = new TextField("Cédula");
        txtCedula.setWidth("100%");
        if (conductor != null) txtCedula.setValue(nonNull(conductor.getCedula()));

        TextField txtTelefono = new TextField("Teléfono");
        txtTelefono.setWidth("100%");
        if (conductor != null) txtTelefono.setValue(nonNull(conductor.getTelefono()));

        TextField txtDireccion = new TextField("Dirección");
        txtDireccion.setWidth("100%");
        if (conductor != null) txtDireccion.setValue(nonNull(conductor.getDireccion()));

        ComboBox<String> cmbTipoVehiculo = new ComboBox<>("Tipo de Vehículo");
        cmbTipoVehiculo.setItems("Carro de Mula", "Motocarro");
        cmbTipoVehiculo.setWidth("100%");
        if (conductor != null) cmbTipoVehiculo.setValue(conductor.getTipoVehiculo());

        NumberField numIngresos = new NumberField("Ingresos Diarios");
        numIngresos.setWidth("100%");
        numIngresos.setMin(0);
        if (conductor != null) numIngresos.setValue(conductor.getIngresosDiarios());

        ComboBox<String> cmbEstado = new ComboBox<>("Estado");
        cmbEstado.setItems("ACTIVO", "INACTIVO", "EN_MIGRACION", "MIGRADO");
        cmbEstado.setWidth("100%");
        if (conductor != null) cmbEstado.setValue(conductor.getEstado());

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
        btnGuardar.addClickListener(e -> guardarConductor(
            txtNombre.getValue(),
            txtCedula.getValue(),
            txtTelefono.getValue(),
            txtDireccion.getValue(),
            cmbTipoVehiculo.getValue(),
            numIngresos.getValue() != null ? numIngresos.getValue() : 0.0,
            cmbEstado.getValue()
        ));

        botonesForm.add(btnCancelar, btnGuardar);

        formContent.add(tituloForm, txtNombre, txtCedula, txtTelefono, txtDireccion,
                       cmbTipoVehiculo, numIngresos, cmbEstado, botonesForm);
        dialogFormulario.add(formContent);
        dialogFormulario.open();
    }

    private String nonNull(String value) {
        return value != null ? value : "";
    }

    private void guardarConductor(String nombre, String cedula, String telefono, String direccion,
                                  String tipoVehiculo, double ingresos, String estado) {
        if (conductorEnEdicion == null) {
            Conductor nuevo = new Conductor();
            nuevo.setNombre(nombre);
            nuevo.setCedula(cedula);
            nuevo.setTelefono(telefono);
            nuevo.setDireccion(direccion);
            nuevo.setTipoVehiculo(tipoVehiculo);
            nuevo.setIngresosDiarios(ingresos);
            nuevo.setEstado(estado);
            nuevo.setFechaRegistro(LocalDate.now());

            controller.registrarConductor(nuevo).thenAccept(id -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Conductor registrado exitosamente", 3000, Notification.Position.TOP_CENTER);
                    dialogFormulario.close();
                    cargarConductores();
                }));
            });
        } else {
            conductorEnEdicion.setNombre(nombre);
            conductorEnEdicion.setCedula(cedula);
            conductorEnEdicion.setTelefono(telefono);
            conductorEnEdicion.setDireccion(direccion);
            conductorEnEdicion.setTipoVehiculo(tipoVehiculo);
            conductorEnEdicion.setIngresosDiarios(ingresos);
            conductorEnEdicion.setEstado(estado);

            controller.actualizarConductor(conductorEnEdicion.getId(), conductorEnEdicion).thenAccept(success -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Conductor actualizado", 3000, Notification.Position.TOP_CENTER);
                    dialogFormulario.close();
                    cargarConductores();
                }));
            });
        }
    }

    private void confirmarEliminacion(Conductor conductor) {
        Dialog confirmarDialog = new Dialog();
        confirmarDialog.setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setAlignItems(Alignment.CENTER);
        content.getElement().getStyle().set("padding", "32px").set("text-align", "center");

        Icon warningIcon = new Icon(VaadinIcon.WARNING);
        warningIcon.getElement().getStyle().set("color", "var(--danger)").set("width", "48px").set("height", "48px");

        H3 mensaje = new H3("¿Está seguro de eliminar este conductor?");
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
            controller.eliminarConductor(conductor.getId()).thenAccept(success -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Conductor eliminado", 3000, Notification.Position.TOP_CENTER);
                    confirmarDialog.close();
                    cargarConductores();
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

    private void migrarConductor(Conductor conductor) {
        if ("Motocarro".equals(conductor.getTipoVehiculo())) {
            Notification.show("Este conductor ya usa motocarro", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        Dialog confirmarDialog = new Dialog();
        confirmarDialog.setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setAlignItems(Alignment.CENTER);
        content.getElement().getStyle().set("padding", "32px").set("text-align", "center");

        H3 mensaje = new H3("¿Migrar a " + conductor.getNombre() + " a Motocarro?");
        mensaje.getElement().getStyle().set("color", "var(--text-dark)").set("margin", "16px 0");

        HorizontalLayout botones = new HorizontalLayout();
        botones.setSpacing(true);

        Button btnMigrar = new Button("Migrar");
        btnMigrar.getElement().getStyle()
            .set("background", "var(--success)")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnMigrar.addClickListener(ev -> {
            controller.migrarConductor(conductor.getId()).thenAccept(success -> {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Conductor migrado exitosamente", 3000, Notification.Position.TOP_CENTER);
                    confirmarDialog.close();
                    cargarConductores();
                }));
            });
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getElement().getStyle()
            .set("background", "#f1f5f9")
            .set("color", "var(--text-secondary)")
            .set("border-radius", "8px")
            .set("padding", "12px 24px")
            .set("border", "none")
            .set("cursor", "pointer");
        btnCancelar.addClickListener(ev -> confirmarDialog.close());

        botones.add(btnMigrar, btnCancelar);
        content.add(mensaje, botones);
        confirmarDialog.add(content);
        confirmarDialog.open();
    }

    private void filtrarConductores(String texto) {
        controller.listarConductores().thenAccept(conductores -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                if (texto == null || texto.isEmpty()) {
                    gridConductores.setItems(conductores);
                } else {
                    String filtro = texto.toLowerCase();
                    List<Conductor> filtrados = conductores.stream()
                        .filter(c -> (c.getNombre() != null && c.getNombre().toLowerCase().contains(filtro)) ||
                                    (c.getCedula() != null && c.getCedula().toLowerCase().contains(filtro)))
                        .toList();
                    gridConductores.setItems(filtrados);
                }
            }));
        });
    }

    private void cargarConductores() {
        controller.listarConductores().thenAccept(conductores -> {
            getUI().ifPresent(ui -> ui.access(() -> gridConductores.setItems(conductores)));
        });
    }
}