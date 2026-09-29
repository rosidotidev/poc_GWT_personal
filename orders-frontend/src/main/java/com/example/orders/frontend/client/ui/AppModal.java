package com.example.orders.frontend.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppModal extends PopupPanel {

    private final FlowPanel content = Ui.panel("app-modal-content");
    private Runnable closeHandler;

    public AppModal() {
        super(false, true);
        setGlassEnabled(true);
        setAnimationEnabled(true);
        setStyleName("app-modal");
        getElement().setAttribute("role", "dialog");
        getElement().setAttribute("aria-modal", "true");

        FlowPanel frame = Ui.panel("app-modal-frame");
        Button close = new Button("X");
        close.setStyleName("app-modal-close");
        close.getElement().setAttribute("aria-label", "Close dialog");
        close.getElement().setAttribute("title", "Close");
        close.addClickHandler(event -> hide());
        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                content.clear();
                Runnable handler = closeHandler;
                closeHandler = null;
                if (handler != null) handler.run();
            }
        });
        frame.add(close);
        frame.add(content);
        setWidget(frame);
    }

    public void showContent(Widget widget, Runnable afterShow) {
        showContent(widget, afterShow, null);
    }

    public void showContent(Widget widget, Runnable afterShow, Runnable onClose) {
        closeHandler = onClose;
        content.clear();
        content.add(widget);
        center();
        Scheduler.get().scheduleDeferred(afterShow::run);
    }
}
