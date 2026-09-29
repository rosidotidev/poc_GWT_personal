import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { ParentBridgeService } from '../bridge/parent-bridge.service';
import { SupplierApiService, SupplierRepresentation } from './supplier-api.service';
import { SupplierEditorComponent } from './supplier-editor.component';

const supplier: SupplierRepresentation = {
  id: 42,
  code: 'SUP-042',
  name: 'Example Supply',
  contactName: 'Taylor Example',
  email: 'taylor@example.test',
  phone: '+1 555 0100',
  active: true,
};

describe('SupplierEditorComponent', () => {
  it('validates and submits a trimmed create request', async () => {
    const api = { get: vi.fn(), create: vi.fn().mockReturnValue(of(supplier)), update: vi.fn() };
    const bridge = { editorInit$: of(null), supplierSaved: vi.fn(), cancel: vi.fn(), resize: vi.fn() };
    await TestBed.configureTestingModule({
      imports: [SupplierEditorComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { data: { mode: 'create' }, paramMap: { get: () => null } } } },
        { provide: SupplierApiService, useValue: api },
        { provide: ParentBridgeService, useValue: bridge },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(SupplierEditorComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;
    component.form.setValue({ code: '  SUP-042 ', name: ' Example Supply ', contactName: ' Taylor Example ',
      email: ' taylor@example.test ', phone: ' +1 555 0100 ' });
    component.submit();

    expect(api.create).toHaveBeenCalledWith({ code: 'SUP-042', name: 'Example Supply',
      contactName: 'Taylor Example', email: 'taylor@example.test', phone: '+1 555 0100' });
    expect(api.update).not.toHaveBeenCalled();
  });

  it('loads and updates the path supplier in edit mode', async () => {
    const api = { get: vi.fn().mockReturnValue(of(supplier)), create: vi.fn(), update: vi.fn().mockReturnValue(of(supplier)) };
    const bridge = { editorInit$: of(null), supplierSaved: vi.fn(), cancel: vi.fn(), resize: vi.fn() };
    await TestBed.configureTestingModule({
      imports: [SupplierEditorComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { data: { mode: 'edit' }, paramMap: { get: () => '42' } } } },
        { provide: SupplierApiService, useValue: api },
        { provide: ParentBridgeService, useValue: bridge },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(SupplierEditorComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;
    expect(api.get).toHaveBeenCalledWith(42);
    expect(component.form.controls.code.value).toBe(supplier.code);
    component.submit();
    expect(api.update).toHaveBeenCalledWith(42, {
      code: supplier.code,
      name: supplier.name,
      contactName: supplier.contactName,
      email: supplier.email,
      phone: supplier.phone,
    });
    expect(api.create).not.toHaveBeenCalled();
  });
});