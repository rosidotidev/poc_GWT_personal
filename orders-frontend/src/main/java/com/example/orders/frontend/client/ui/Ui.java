package com.example.orders.frontend.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public final class Ui {

    private Ui() {
    }

    public static FlowPanel panel(String style) {
        FlowPanel panel = new FlowPanel();
        panel.setStyleName(style);
        return panel;
    }

    public static Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyleName(style);
        return label;
    }

    public static FlowPanel field(String name, TextBox input, String placeholder, String extraClass) {
        FlowPanel field = panel("form-field " + extraClass);
        input.setStyleName("text-input");
        input.getElement().setAttribute("placeholder", placeholder);
        input.getElement().setAttribute("aria-label", name);
        field.add(label(name, "field-label"));
        field.add(input);
        return field;
    }

    public static FlowPanel hero(String context, String title, String subtitle, Widget... actions) {
        FlowPanel hero = panel("hero");
        FlowPanel copy = panel("hero-copy");
        copy.add(label("ORDER MANAGEMENT  /  " + context, "eyebrow"));
        copy.add(label(title, "hero-title"));
        copy.add(label(subtitle, "hero-description"));
        hero.add(copy);
        FlowPanel actionPanel = panel("hero-actions");
        for (Widget action : actions) {
            if (action != null) {
                actionPanel.add(action);
            }
        }
        hero.add(actionPanel);
        return hero;
    }

    public static String formatMoney(double amount) {
        long cents = Math.round(amount * 100);
        long whole = cents / 100;
        long fraction = Math.abs(cents % 100);
        return whole + (fraction < 10 ? ".0" : ".") + fraction;
    }

    public static String escapeHtml(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static String errorMessage(Throwable caught) {
        return caught.getMessage() == null ? caught.toString() : caught.getMessage();
    }
}
