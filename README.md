# Order Management POC — GWT, CXF, Spring (Java 21)

Three Maven modules, with Java 21 for server and build code and GWT 2.13.1 for the browser client. The GWT-translated client source level is configured separately at Java 17. Both servers use Spring Core/Web contexts; Spring Boot and Spring MVC are not used.

- `orders-frontend` — GWT client, GWT-RPC servlet, and an Apache CXF JAX-WS SOAP client. The browser talks only to this server.
- `orders-soap-contract` — hand-authored, code-first JAX-WS service interface and JAXB DTOs shared by the two servers. CXF publishes the runtime WSDL from these annotations; there is no WSDL-first generation or `wsdl2java` step.
- `orders-backend` — Spring Core services and JDBC repositories backed by a file-based H2 database, exposed only through CXF SOAP.

Order lifecycle: create, approve, send (`CREATED` → `APPROVED` → `SENT`). Invalid transitions remain errors. Product and supplier entries persist across restarts; products can be associated with multiple suppliers, and each supplier can serve multiple products. New orders snapshot the selected product's name and price.

## Flows

Request path:

```text
Browser / compiled GWT JavaScript
	-> GWT-RPC :8081/ordersapp/rpc/orders
	-> frontend Spring Core context / OrdersRpcServiceImpl
	-> CXF JAX-WS proxy bean (server-side)
	-> SOAP :8080/soap/orders
	-> backend CXFServlet
	-> annotated OrdersSoapEndpoint
	-> OrderService / ProductService / SupplierService -> JdbcTemplate -> H2 file database
	<- SOAP response/fault <- CXF proxy <- GWT-RPC response
	<- GWT callback -> UI
```

## Build

```text
mvn -pl orders-backend,orders-frontend -am package
mvn -pl orders-frontend -am gwt:compile
```

## Run

Install the reactor modules once so each independently started webapp can resolve the shared SOAP contract jar:

```text
mvn -pl orders-backend,orders-frontend -am install
```

Start the backend from its module directory in one terminal (default port 8080):

```text
cd orders-backend
mvn exec:java
```

If port 8080 is already occupied, choose another backend port:

```text
cd orders-backend
mvn "-Dorders.http.port=8082" exec:java
```

Start the frontend from its module directory in another terminal:

```text
cd orders-frontend
mvn jetty:run
```

Open `http://localhost:8081/OrdersApp.html`. The frontend server calls `http://localhost:8080/soap/orders` by default. For a backend on another port, set JVM property `orders.soap.url` or environment variable `ORDERS_SOAP_URL` when starting the frontend, for example `mvn "-Dorders.soap.url=http://localhost:8082/soap/orders" jetty:run`.

The GWT app has separate Overview, Orders, Products, and Suppliers pages. Orders, Products, and Suppliers show their lists by default; create and edit actions open focused data-entry modals. The product editor provides a searchable supplier picker: matching suppliers appear below the input and selected suppliers are listed as removable rows. Product cards show current associations. Products and suppliers support create, edit, pause, and reactivate; records are never physically deleted. The order form searches active products by name from the third typed character; current product prices are snapshotted into each order.

The client UI is componentized under `client/ui`: `AppShell` owns the application frame and tab host, each tab has its own class, and the main editor/list areas are separate panel classes. `OrdersApp` contains only startup, routing, and cross-tab coordination.

H2 runs in file mode at `orders-backend/data/orders` by default. The schema, four starter products, and two starter suppliers are initialized from `orders-backend/src/main/resources/schema.sql`; seed rows are inserted only when their code is missing. Restarts keep catalog, supplier, and order data.

CXF publishes the code-first SOAP description at `http://localhost:8080/soap/orders?wsdl`. This WSDL is runtime output, not a source file used for code generation.

## SOAP operations

- `listOrders`
- `getOrder(id)`
- `createOrder(request)`
- `approveOrder(id)`
- `sendOrder(id)`
- `listProducts()` / `listActiveProducts()`
- `createProduct(product)` / `updateProduct(product)`
- `setProductActive(id, active)` / `searchActiveProductsByName(name)`
- `listSuppliers()` / `createSupplier(supplier)` / `updateSupplier(supplier)`
- `setSupplierActive(id, active)`

SOAP endpoints delegate to Spring Core order, product, and supplier services. SOAP faults describe missing entities and invalid order transitions; GWT-RPC maps these to client-visible failures. The backend exposes no REST endpoints.

## Automated browser tests

The versioned Playwright suite covers multi-supplier product creation, product editing/cancellation, supplier create/edit/cancellation, product autocomplete, and order creation. It starts isolated servers and deletes its dedicated H2 database after every run, including failed runs:

```text
npm install
npx playwright install chromium
npm test
```

Use `npm run test:e2e:headed` to watch the tests or `npm run test:e2e:clean` to remove an interrupted test environment and generated reports. See `e2e/README.md` for details.
