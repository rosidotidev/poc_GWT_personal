# Browser E2E tests

Run the complete reproducible suite from the repository root:

```text
npm install
npx playwright install chromium
npm test
```

The suite covers product-to-supplier assignment, product and supplier maintenance, autocomplete, and order creation. It starts dedicated servers on ports 18080 and 18081 and uses an isolated H2 database under `orders-backend/target/e2e`. `e2e/scripts/run-tests.ps1` invokes cleanup before the run and again from a `finally` block, so interrupted or failed tests still stop the test servers and delete the test database. The normal database under `orders-backend/data` is never touched. The latest HTML report is retained until the next run.

Use `npm run test:e2e:headed` to watch the browser, or `npm run test:e2e:clean` to clean an interrupted run and delete generated reports manually.
