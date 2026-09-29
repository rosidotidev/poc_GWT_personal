import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface SupplierWriteRequest {
  code: string;
  name: string;
  contactName: string;
  email: string;
  phone: string;
}

export interface SupplierRepresentation extends SupplierWriteRequest {
  id: number;
  active: boolean;
}

export interface SupplierProblem {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  code: string;
  errors?: Record<string, string>;
}

@Injectable({ providedIn: 'root' })
export class SupplierApiService {
  private readonly http = inject(HttpClient);
  private readonly resource = '/bff/api/v1/suppliers';

  get(id: number): Observable<SupplierRepresentation> {
    return this.http.get<SupplierRepresentation>(`${this.resource}/${id}`).pipe(catchError(this.normalizeError));
  }

  create(request: SupplierWriteRequest): Observable<SupplierRepresentation> {
    return this.http.post<SupplierRepresentation>(this.resource, request).pipe(catchError(this.normalizeError));
  }

  update(id: number, request: SupplierWriteRequest): Observable<SupplierRepresentation> {
    return this.http.put<SupplierRepresentation>(`${this.resource}/${id}`, request).pipe(catchError(this.normalizeError));
  }

  private readonly normalizeError = (error: HttpErrorResponse): Observable<never> => {
    if (error.error && typeof error.error === 'object' && 'detail' in error.error) {
      return throwError(() => error.error as SupplierProblem);
    }
    return throwError(() => ({
      type: 'about:blank',
      title: 'Request failed',
      status: error.status,
      detail: error.status === 0 ? 'The supplier service cannot be reached.' : 'The request could not be completed.',
      instance: '',
      code: 'request_failed',
    } satisfies SupplierProblem));
  };
}