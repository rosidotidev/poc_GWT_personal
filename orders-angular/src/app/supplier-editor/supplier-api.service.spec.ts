import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SupplierApiService, SupplierRepresentation } from './supplier-api.service';

const supplier: SupplierRepresentation = {
  id: 42,
  code: 'SUP-042',
  name: 'Example Supply',
  contactName: 'Taylor Example',
  email: 'taylor@example.test',
  phone: '+1 555 0100',
  active: true,
};

describe('SupplierApiService', () => {
  let api: SupplierApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(SupplierApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads a supplier from its resource URI', () => {
    api.get(42).subscribe((result) => expect(result).toEqual(supplier));
    const request = http.expectOne('/bff/api/v1/suppliers/42');
    expect(request.request.method).toBe('GET');
    request.flush(supplier);
  });

  it('creates a supplier with POST and preserves Problem Details on failure', () => {
    let problemCode = '';
    api.create({ code: 'SUP-042', name: 'Example Supply', contactName: '', email: '', phone: '' })
      .subscribe({ error: (problem) => problemCode = problem.code });
    const request = http.expectOne('/bff/api/v1/suppliers');
    expect(request.request.method).toBe('POST');
    request.flush({ code: 'supplier_code_conflict', detail: 'Supplier code already exists' },
      { status: 409, statusText: 'Conflict' });
    expect(problemCode).toBe('supplier_code_conflict');
  });

  it('updates the supplier resource with PUT', () => {
    api.update(42, { code: supplier.code, name: supplier.name, contactName: supplier.contactName,
      email: supplier.email, phone: supplier.phone }).subscribe((result) => expect(result.id).toBe(42));
    const request = http.expectOne('/bff/api/v1/suppliers/42');
    expect(request.request.method).toBe('PUT');
    request.flush(supplier);
  });
});