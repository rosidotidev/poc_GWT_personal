import { Component, DestroyRef, ElementRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ParentBridgeService } from '../bridge/parent-bridge.service';
import { SupplierApiService, SupplierProblem, SupplierWriteRequest } from './supplier-api.service';

type EditorMode = 'create' | 'edit';

@Component({
  selector: 'app-supplier-editor',
  standalone: true,
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatProgressSpinnerModule],
  templateUrl: './supplier-editor.component.html',
  styleUrl: './supplier-editor.component.scss',
})
export class SupplierEditorComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(SupplierApiService);
  private readonly bridge = inject(ParentBridgeService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly host = inject(ElementRef<HTMLElement>);
  private supplierId: number | null = null;

  readonly mode = signal<EditorMode>('create');
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly problem = signal<SupplierProblem | null>(null);
  readonly form = new FormGroup({
    code: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(40)] }),
    name: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(160)] }),
    contactName: new FormControl('', { nonNullable: true, validators: [Validators.maxLength(160)] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.maxLength(200), Validators.email] }),
    phone: new FormControl('', { nonNullable: true, validators: [Validators.maxLength(60)] }),
  });

  ngOnInit(): void {
    this.mode.set(this.route.snapshot.data['mode'] === 'edit' ? 'edit' : 'create');
    if (this.mode() === 'edit') {
      this.supplierId = Number(this.route.snapshot.paramMap.get('id'));
      if (!Number.isInteger(this.supplierId) || this.supplierId <= 0) {
        this.showProblem({
          type: 'about:blank', title: 'Invalid supplier', status: 400,
          detail: 'The supplier identifier is invalid.', instance: '', code: 'invalid_supplier_id',
        });
      } else {
        this.loadSupplier(this.supplierId);
      }
    }

    this.bridge.editorInit$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((init) => {
      if (!init) return;
      if (init.mode !== this.mode() || (init.mode === 'edit' && init.supplierId !== this.supplierId)) {
        this.showProblem({
          type: 'about:blank', title: 'Editor context mismatch', status: 400,
          detail: 'The editor context does not match the requested supplier.', instance: '', code: 'context_mismatch',
        });
      }
    });

    if (window.parent !== window && 'ResizeObserver' in window) {
      const observer = new ResizeObserver(() => this.bridge.resize(
        this.host.nativeElement.getBoundingClientRect().height,
      ));
      observer.observe(this.host.nativeElement);
      this.destroyRef.onDestroy(() => observer.disconnect());
    }
  }

  submit(): void {
    this.problem.set(null);
    this.form.patchValue(this.trimPayload(this.form.getRawValue()));
    if (this.form.invalid || this.saving() || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }

    const request = this.form.getRawValue();
    this.saving.set(true);
    const save = this.mode() === 'create'
      ? this.api.create(request)
      : this.api.update(this.supplierId!, request);

    save.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (supplier) => {
        this.saving.set(false);
        if (window.parent !== window) this.bridge.supplierSaved(supplier.id);
      },
      error: (problem: SupplierProblem) => this.showProblem(problem),
    });
  }

  cancel(): void {
    if (window.parent !== window) this.bridge.cancel();
  }

  fieldError(field: keyof SupplierWriteRequest): string | null {
    const control = this.form.controls[field];
    if (!control.touched || !control.errors) return null;
    if (control.hasError('required')) return 'This field is required.';
    if (control.hasError('email')) return 'Enter a valid email address.';
    const maxLength = control.getError('maxlength')?.requiredLength;
    return maxLength ? `Use ${maxLength} characters or fewer.` : null;
  }

  private loadSupplier(id: number): void {
    this.loading.set(true);
    this.api.get(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (supplier) => {
        this.form.patchValue(supplier);
        this.loading.set(false);
      },
      error: (problem: SupplierProblem) => this.showProblem(problem),
    });
  }

  private showProblem(problem: SupplierProblem): void {
    this.loading.set(false);
    this.saving.set(false);
    this.problem.set(problem);
  }

  private trimPayload(value: SupplierWriteRequest): SupplierWriteRequest {
    return {
      code: value.code.trim(),
      name: value.name.trim(),
      contactName: value.contactName.trim(),
      email: value.email.trim(),
      phone: value.phone.trim(),
    };
  }
}