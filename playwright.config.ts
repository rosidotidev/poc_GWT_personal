import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e/tests',
  fullyParallel: false,
  workers: 1,
  timeout: 30_000,
  expect: { timeout: 10_000 },
  retries: process.env.CI ? 2 : 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://127.0.0.1:18081',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
  webServer: [
    {
      command: 'powershell -NoProfile -ExecutionPolicy Bypass -File e2e/scripts/start-backend.ps1',
      url: 'http://127.0.0.1:18080/soap/orders?wsdl',
      timeout: 120_000,
      reuseExistingServer: false,
    },
    {
      command: 'powershell -NoProfile -ExecutionPolicy Bypass -File e2e/scripts/start-frontend.ps1',
      url: 'http://127.0.0.1:18081/angular/index.html',
      timeout: 180_000,
      reuseExistingServer: false,
    },
  ],
});
