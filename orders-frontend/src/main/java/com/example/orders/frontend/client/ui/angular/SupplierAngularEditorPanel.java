package com.example.orders.frontend.client.ui.angular;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SupplierAngularEditorPanel extends Composite {

    private static final int BOOTSTRAP_TIMEOUT_MS = 12000;

    private final Frame frame = new Frame();
    private final Label loading = new Label("Loading supplier editor…");
    private final Label failure = new Label("The supplier editor could not be loaded. Close this dialog and try again.");
    private final String sessionId = System.currentTimeMillis() + "-" + com.google.gwt.user.client.Random.nextInt();
    private final boolean editMode;
    private final long supplierId;
    private final Runnable savedHandler;
    private final Runnable cancelHandler;
    private final Timer bootstrapTimer;
    private JavaScriptObject messageHandler;
    private boolean initialized;
    private boolean completed;

    public SupplierAngularEditorPanel(boolean editMode, long supplierId,
            Runnable savedHandler, Runnable cancelHandler) {
        this.editMode = editMode;
        this.supplierId = supplierId;
        this.savedHandler = savedHandler;
        this.cancelHandler = cancelHandler;

        FlowPanel root = new FlowPanel();
        root.setStyleName("angular-editor-host");
        loading.setStyleName("angular-editor-loading");
        failure.setStyleName("angular-editor-failure");
        failure.setVisible(false);
        frame.setStyleName("angular-editor-frame");
        frame.setWidth("100%");
        frame.setHeight("516px");
        frame.getElement().setAttribute("title", editMode ? "Edit supplier" : "Create supplier");
        root.add(loading);
        root.add(failure);
        root.add(frame);
        initWidget(root);

        messageHandler = registerMessageHandler(frame.getElement());
        frame.setUrl("/angular/index.html#"
            + (editMode ? "/supplier/" + supplierId + "/edit" : "/supplier/create")
            + "?sessionId=" + sessionId);
        bootstrapTimer = new Timer() {
            @Override
            public void run() {
                if (!initialized) {
                    loading.setVisible(false);
                    failure.setVisible(true);
                }
            }
        };
        bootstrapTimer.schedule(BOOTSTRAP_TIMEOUT_MS);
    }

    public void destroy() {
        bootstrapTimer.cancel();
        if (messageHandler != null) {
            unregisterMessageHandler(messageHandler);
            messageHandler = null;
        }
    }

    @Override
    protected void onUnload() {
        destroy();
        super.onUnload();
    }

    private void handleMessage(JavaScriptObject message) {
        if (!isPlainObject(message)
                || !"orders-angular".equals(readString(message, "channel"))
                || readNumber(message, "version") != 1
                || !sessionId.equals(readString(message, "sessionId"))) {
            return;
        }

        String type = readString(message, "type");
        if ("READY".equals(type) && hasAllowedKeys(message, "channel,version,type,sessionId,requestId")) {
            if (!initialized) {
                initialized = true;
                bootstrapTimer.cancel();
                loading.setVisible(false);
                sendEditorInit(frame.getElement(), sessionId, editMode ? "edit" : "create",
                    String.valueOf(supplierId));
            }
        } else if ("SUPPLIER_SAVED".equals(type)
                && hasAllowedKeys(message, "channel,version,type,sessionId,requestId,supplierId")
                && readNumber(message, "supplierId") > 0 && !completed) {
            completed = true;
            savedHandler.run();
        } else if ("CANCEL".equals(type)
                && hasAllowedKeys(message, "channel,version,type,sessionId,requestId") && !completed) {
            completed = true;
            cancelHandler.run();
        } else if ("RESIZE".equals(type)
                && hasAllowedKeys(message, "channel,version,type,sessionId,requestId,height")) {
            double height = readNumber(message, "height");
            if (isFinite(height)) frame.setHeight(Math.max(240, Math.min(900, (int) Math.ceil(height))) + "px");
        }
    }

    private native JavaScriptObject registerMessageHandler(Element iframe) /*-{
        var self = this;
        var listener = function(event) {
            if (event.origin !== $wnd.location.origin || event.source !== iframe.contentWindow) return;
            self.@com.example.orders.frontend.client.ui.angular.SupplierAngularEditorPanel::handleMessage(Lcom/google/gwt/core/client/JavaScriptObject;)(event.data);
        };
        $wnd.addEventListener('message', listener, false);
        return listener;
    }-*/;

    private native void unregisterMessageHandler(JavaScriptObject listener) /*-{
        $wnd.removeEventListener('message', listener, false);
    }-*/;

    private native void sendEditorInit(Element iframe, String sessionId, String mode, String supplierId) /*-{
        var message = {
            channel: 'orders-angular', version: 1, type: 'SUPPLIER_EDITOR_INIT',
            sessionId: sessionId, mode: mode
        };
        if (mode === 'edit') message.supplierId = Number(supplierId);
        iframe.contentWindow.postMessage(message, $wnd.location.origin);
    }-*/;

    private native boolean isPlainObject(JavaScriptObject value) /*-{
        return value !== null && typeof value === 'object' && !Array.isArray(value);
    }-*/;

    private native String readString(JavaScriptObject value, String key) /*-{
        return typeof value[key] === 'string' ? value[key] : null;
    }-*/;

    private native double readNumber(JavaScriptObject value, String key) /*-{
        return typeof value[key] === 'number' ? value[key] : NaN;
    }-*/;

    private native boolean hasAllowedKeys(JavaScriptObject value, String allowedKeys) /*-{
        var allowed = allowedKeys.split(',');
        var keys = Object.keys(value);
        return keys.indexOf('channel') !== -1 && keys.indexOf('version') !== -1
            && keys.indexOf('type') !== -1 && keys.indexOf('sessionId') !== -1
            && keys.every(function(key) { return allowed.indexOf(key) !== -1; });
    }-*/;

    private native boolean isFinite(double value) /*-{
        return Number.isFinite(value);
    }-*/;
}