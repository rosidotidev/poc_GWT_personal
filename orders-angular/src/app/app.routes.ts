import { Routes } from '@angular/router';
import { SupplierEditorComponent } from './supplier-editor/supplier-editor.component';

export const routes: Routes = [
	{ path: 'supplier/create', component: SupplierEditorComponent, data: { mode: 'create' } },
	{ path: 'supplier/:id/edit', component: SupplierEditorComponent, data: { mode: 'edit' } },
	{ path: '**', redirectTo: 'supplier/create' },
];
