package com.sigmat;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push; // IMPORTANTE
import com.vaadin.flow.theme.Theme;

@Theme("sigmat-theme")
@Push // ESTO SOLUCIONA QUE LOS DATOS NO APAREZCAN AUTOMÁTICAMENTE
public class SigmaAppShell implements AppShellConfigurator {
}