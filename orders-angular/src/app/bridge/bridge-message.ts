export const BRIDGE_CHANNEL = 'orders-angular';
export const BRIDGE_VERSION = 1;

export type SupplierEditorMode = 'create' | 'edit';

export interface BridgeEnvelope {
  channel: typeof BRIDGE_CHANNEL;
  version: typeof BRIDGE_VERSION;
  type: string;
  sessionId: string;
  requestId?: string;
}

export interface SupplierEditorInit extends BridgeEnvelope {
  type: 'SUPPLIER_EDITOR_INIT';
  mode: SupplierEditorMode;
  supplierId?: number;
}

export interface SupplierSaved extends BridgeEnvelope {
  type: 'SUPPLIER_SAVED';
  supplierId: number;
}

export interface SupplierResize extends BridgeEnvelope {
  type: 'RESIZE';
  height: number;
}

export function isBridgeEnvelope(value: unknown): value is BridgeEnvelope {
  if (typeof value !== 'object' || value === null) {
    return false;
  }
  const message = value as Partial<BridgeEnvelope>;
  return message.channel === BRIDGE_CHANNEL
    && message.version === BRIDGE_VERSION
    && typeof message.type === 'string'
    && typeof message.sessionId === 'string'
    && message.sessionId.length > 0;
}