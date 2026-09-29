import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {
  BRIDGE_CHANNEL,
  BRIDGE_VERSION,
  BridgeEnvelope,
  isBridgeEnvelope,
  SupplierEditorInit,
  SupplierResize,
} from './bridge-message';

@Injectable({ providedIn: 'root' })
export class ParentBridgeService {
  private readonly sessionId = new URLSearchParams(window.location.hash.split('?')[1] ?? window.location.search)
    .get('sessionId') ?? 'preview';
  private readonly origin = window.location.origin;
  private readonly editorInitSubject = new BehaviorSubject<SupplierEditorInit | null>(null);
  readonly editorInit$ = this.editorInitSubject.asObservable();

  constructor() {
    window.addEventListener('message', this.onMessage);
    if (window.parent !== window) {
      this.send({ type: 'READY' });
    }
  }

  supplierSaved(supplierId: number): void {
    this.send({ type: 'SUPPLIER_SAVED', supplierId });
  }

  cancel(): void {
    this.send({ type: 'CANCEL' });
  }

  resize(height: number): void {
    const message: SupplierResize = {
      ...this.envelope('RESIZE'),
      height: Math.max(240, Math.min(900, Math.ceil(height))),
    };
    this.post(message);
  }

  private readonly onMessage = (event: MessageEvent<unknown>): void => {
    if (event.origin !== this.origin || event.source !== window.parent || !isBridgeEnvelope(event.data)) {
      return;
    }
    if (event.data.sessionId !== this.sessionId || event.data.type !== 'SUPPLIER_EDITOR_INIT') {
      return;
    }

    const init = event.data as Partial<SupplierEditorInit>;
    const validCreate = init.mode === 'create' && init.supplierId === undefined;
    const validEdit = init.mode === 'edit' && Number.isInteger(init.supplierId) && (init.supplierId ?? 0) > 0;
    if (validCreate || validEdit) {
      this.editorInitSubject.next(init as SupplierEditorInit);
    }
  };

  private send(message: { type: string; [key: string]: unknown }): void {
    this.post({ ...this.envelope(message.type), ...message } as BridgeEnvelope);
  }

  private envelope<T extends string>(type: T): BridgeEnvelope & { type: T } {
    return { channel: BRIDGE_CHANNEL, version: BRIDGE_VERSION, type, sessionId: this.sessionId };
  }

  private post(message: BridgeEnvelope): void {
    if (window.parent !== window) {
      window.parent.postMessage(message, this.origin);
    }
  }
}