import { expect, test } from '@playwright/test';

const unique = () => `${Date.now()}-${Math.floor(Math.random() * 10_000)}`;

test('creates a product with multiple suppliers and supports edit plus pause without delete', async ({ page }) => {
  const suffix = unique();
  const sku = `MULTI-${suffix}`;
  const name = `Multi supplier product ${suffix}`;
  await page.goto('/OrdersApp.html#products');

  await expect(page.getByText('Studio notebook', { exact: true })).toBeVisible();
  await expect(page.getByRole('textbox', { name: 'SKU / short code' })).toHaveCount(0);
  await page.getByRole('button', { name: 'Add a product' }).click();
  await page.getByRole('textbox', { name: 'SKU / short code' }).fill(sku);
  await page.getByRole('textbox', { name: 'Product name' }).fill(name);
  await page.getByRole('textbox', { name: 'Description' }).fill('Created with two suppliers');
  await page.getByRole('textbox', { name: 'Unit price' }).fill('19.90');
  const supplierSearch = page.getByRole('textbox', { name: 'Search product suppliers' });
  await supplierSearch.fill('**');
  const northstarResult = page.locator('.supplier-search-result').filter({ hasText: 'Northstar Paper Co.' });
  await expect(northstarResult).toBeVisible();
  await expect(page.locator('.supplier-search-result')).toHaveCount(2);
  await northstarResult.click();
  await expect(page.locator('.selected-supplier').filter({ hasText: 'Northstar Paper Co.' })).toBeVisible();

  await supplierSearch.pressSequentially('Green');
  const greenlineResult = page.locator('.supplier-search-result').filter({ hasText: 'Greenline Homeware' });
  await expect(greenlineResult).toBeVisible();
  await greenlineResult.click();
  await expect(page.locator('.selected-supplier').filter({ hasText: 'Greenline Homeware' })).toBeVisible();
  await page.getByRole('button', { name: 'Add product' }).click();

  const product = page.locator('.catalog-product').filter({ hasText: sku });
  await expect(product).toContainText(name);
  await expect(product).toContainText('Northstar Paper Co.');
  await expect(product).toContainText('Greenline Homeware');
  await expect(product.getByRole('button', { name: 'Edit' })).toBeVisible();
  await expect(product.getByRole('button', { name: 'Pause' })).toBeVisible();
  await expect(product.getByRole('button', { name: 'Delete' })).toHaveCount(0);

  await product.getByRole('button', { name: 'Edit' }).click();
  await expect(page.getByText('Edit product', { exact: true })).toBeVisible();
  await page.getByRole('textbox', { name: 'Description' }).fill('E2E verified notebook');
  await page.getByRole('button', { name: 'Save changes' }).click();
  await expect(product).toContainText('E2E verified notebook');

  await product.getByRole('button', { name: 'Pause' }).click();
  await expect(product).toContainText('Paused');
  const activate = product.getByRole('button', { name: 'Activate' });
  await expect(activate).toBeVisible();
  await activate.click();
  await expect(product).toContainText('Available');
});

test('replaces a product supplier association while editing', async ({ page }) => {
  const suffix = unique();
  const sku = `SWAP-${suffix}`;
  const name = `Supplier swap product ${suffix}`;
  await page.goto('/OrdersApp.html#products');

  await page.getByRole('button', { name: 'Add a product' }).click();
  await page.getByRole('textbox', { name: 'SKU / short code' }).fill(sku);
  await page.getByRole('textbox', { name: 'Product name' }).fill(name);
  await page.getByRole('textbox', { name: 'Description' }).fill('Supplier association replacement');
  await page.getByRole('textbox', { name: 'Unit price' }).fill('10.00');

  const supplierSearch = page.getByRole('textbox', { name: 'Search product suppliers' });
  await supplierSearch.pressSequentially('North');
  await page.locator('.supplier-search-result').filter({ hasText: 'Northstar Paper Co.' }).click();
  await page.getByRole('button', { name: 'Add product' }).click();

  const product = page.locator('.catalog-product').filter({ hasText: sku });
  await expect(product).toContainText('Northstar Paper Co.');
  await product.getByRole('button', { name: 'Edit' }).click();
  await page.getByRole('button', { name: 'Remove Northstar Paper Co.' }).click();
  await supplierSearch.pressSequentially('Green');
  await page.locator('.supplier-search-result').filter({ hasText: 'Greenline Homeware' }).click();
  await page.getByRole('button', { name: 'Save changes' }).click();

  await expect(product).toContainText('Greenline Homeware');
  await expect(product).not.toContainText('Northstar Paper Co.');
});

test('saves a product after removing its last supplier', async ({ page }) => {
  await page.goto('/OrdersApp.html#products');

  const product = page.locator('.catalog-product').filter({ hasText: 'Studio notebook' });
  await product.getByRole('button', { name: 'Edit' }).click();
  await page.getByRole('button', { name: 'Remove Northstar Paper Co.' }).click();
  await expect(page.getByText('No suppliers selected', { exact: true })).toBeVisible();
  await page.getByRole('button', { name: 'Save changes' }).click();

  await expect(product).toContainText('No suppliers assigned');
  await expect(product).not.toContainText('Northstar Paper Co.');
  await expect(page.getByRole('dialog')).toHaveCount(0);
});

test('creates, edits, and pauses a supplier without exposing delete', async ({ page }) => {
  const suffix = unique();
  const code = `E2E-${suffix}`;
  const name = `Test Supplier ${suffix}`;

  await page.goto('/OrdersApp.html#suppliers');
  await expect(page.getByText('Northstar Paper Co.', { exact: true })).toBeVisible();
  await expect(page.getByText('Greenline Homeware', { exact: true })).toBeVisible();
  await expect(page.getByRole('textbox', { name: 'Supplier code' })).toHaveCount(0);
  await page.getByRole('button', { name: 'Add a supplier' }).click();

  const createEditor = page.frameLocator('iframe[title="Create supplier"]');
  await createEditor.getByRole('textbox', { name: 'Supplier code' }).fill(code);
  await createEditor.getByRole('textbox', { name: 'Company name' }).fill(name);
  await createEditor.getByRole('textbox', { name: 'Contact name' }).fill('E2E Contact');
  await createEditor.getByRole('textbox', { name: 'Email' }).fill('e2e@example.test');
  await createEditor.getByRole('textbox', { name: 'Phone' }).fill('+39 000 000');
  await createEditor.getByRole('button', { name: 'Create supplier' }).click();

  const supplier = page.locator('.catalog-product').filter({ hasText: code });
  await expect(supplier).toContainText(name);
  await expect(supplier.getByRole('button', { name: 'Delete' })).toHaveCount(0);
  await supplier.getByRole('button', { name: 'Edit' }).click();
  const editEditor = page.frameLocator('iframe[title="Edit supplier"]');
  await editEditor.getByRole('textbox', { name: 'Contact name' }).fill('Updated Contact');
  await editEditor.getByRole('button', { name: 'Save changes' }).click();
  await expect(supplier).toContainText('Updated Contact');

  await supplier.getByRole('button', { name: 'Pause' }).click();
  await expect(supplier).toContainText('Paused');
});

test('searches products after three letters and creates an order', async ({ page }) => {
  const customer = `E2E Customer ${unique()}`;
  await page.goto('/OrdersApp.html#orders');

  const navigation = page.locator('.primary-navigation');
  await expect(navigation.getByRole('button', { name: 'Orders', exact: true })).toHaveCount(1);
  await expect(navigation.getByRole('button', { name: 'Create order', exact: true })).toHaveCount(0);
  await expect(page.getByRole('textbox', { name: 'Customer name' })).toHaveCount(0);
  await expect(page.locator('.board-panel')).toBeVisible();
  await page.getByRole('button', { name: 'Create order', exact: true }).click();
  await expect(page.getByRole('textbox', { name: 'Customer name' })).toBeVisible();

  const search = page.getByRole('textbox', { name: 'Search catalog product' });
  await search.pressSequentially('no');
  await expect(page.locator('.product-search-result')).toHaveCount(0);
  await search.fill('**');
  await expect(page.locator('.product-search-result')).toHaveCount(4);

  const result = page.locator('.product-search-result').filter({ hasText: 'Studio notebook' });
  await expect(result).toBeVisible();
  const inputBox = await search.boundingBox();
  const resultBox = await page.locator('.product-search-results').boundingBox();
  expect(inputBox).not.toBeNull();
  expect(resultBox).not.toBeNull();
  expect(Math.abs(resultBox!.y - (inputBox!.y + inputBox!.height))).toBeLessThanOrEqual(2);
  await result.click();

  await page.getByRole('textbox', { name: 'Customer name' }).fill(customer);
  await page.getByRole('textbox', { name: 'Order description' }).fill('Playwright regression order');
  await page.getByRole('textbox', { name: 'Quantity' }).fill('2');
  await page.getByRole('button', { name: 'Create order', exact: true }).last().click();

  await expect(page).toHaveURL(/#orders$/);
  const order = page.locator('.order-card').filter({ hasText: customer });
  await expect(order).toBeVisible();
  await expect(order).toContainText('2 x Studio notebook');
  await expect(order).toContainText('$25.00');
});
