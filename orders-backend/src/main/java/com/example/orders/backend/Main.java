package com.example.orders.backend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/** Boots the app with an embedded Jetty container, no external Tomcat/app-server needed. */
public class Main {

    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("orders.http.port", 8080);

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase("src/main/webapp");
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        server.start();
        System.out.println("orders-backend started: http://localhost:" + port + "/soap/orders");
        server.join();
    }
}
