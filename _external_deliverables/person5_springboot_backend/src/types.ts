export enum SuitStyle {
  SLIM_FIT = "Slim-Fit Italiano",
  CLASSIC = "Clássico Executivo",
  TUXEDO = "Tuxedo Imperial",
  MODERN = "Estilo Zara Minimalist"
}

export enum LapelType {
  NOTCH = "Entalhada (Notch)",
  PEAK = "Ponta (Peak)",
  SHAWL = "Xaile (Shawl)"
}

export enum ButtonType {
  ONE = "1 Botão Premium",
  TWO = "2 Botões Clássicos",
  DOUBLE_4 = "Trespasse Fila Dupla (4 Botões)",
  DOUBLE_6 = "Trespasse Fila Dupla (6 Botões)"
}

export enum PocketType {
  FLAP = "Com Pala (Flap)",
  WELT = "Bolso Metido (Welt)",
  PATCH = "Bolso Aplicado (Patch)"
}

export enum LiningType {
  PLAIN = "Cetin Liso Premium",
  PAISLEY = "Padrão Paisley Imperial",
  STRIPE = "Listrado de Seda Dourada",
  CRIMSON = "Seda Vermelha Real"
}

export interface FabricOption {
  id: string;
  name: string;
  description: string;
  category: "Lã" | "Linho" | "Veludo" | "Misto";
  colorHex: string;
  priceModifier: number;
}

export interface SuitConfiguration {
  model: SuitStyle;
  lapel: LapelType;
  buttons: ButtonType;
  pockets: PocketType;
  lining: LiningType;
  fabricId: string;
  primaryColor: string; // Hex matching the selected fabric or customizer
}

export interface SavedMeasurements {
  neck: number;      // cma
  shoulders: number; // cm
  chest: number;     // cm
  waist: number;     // cm
  sleeves: number;   // cm
  height: number;    // cm
}

export enum DeliveryType {
  DELIVERY = "Delivery (Entrega ao domicílio)",
  PICKUP = "Levantamento (Pilar Físico / Ateliê)"
}

export interface DeliveryDetails {
  type: DeliveryType;
  address: string;
  city: string;
  phone: string;
  pickupPointId?: string;
}

export enum OrderStatus {
  PENDING_PAYMENT = "Pagamento pendente",
  VALIDATED = "Validado por Alfaiate",
  IN_PRODUCTION = "Em produção",
  READY = "Pronto para entrega/levantamento",
  IN_TRANSIT = "Em entrega",
  DELIVERED = "Entregue com Sucesso"
}

export interface Order {
  id: string;
  timestamp: string;
  configuration: SuitConfiguration;
  measurements: SavedMeasurements;
  delivery: DeliveryDetails;
  status: OrderStatus;
  paymentProofUrl?: string; // Data URL or filename
  paymentProofName?: string;
  paidAmount: number;
  syncPending: boolean; // For computing concepts "Offline Store & Forward"
  notes?: string;
}

export type ActiveScreen =
  | "SPLASH"
  | "ONBOARDING"
  | "LOGIN"
  | "DASHBOARD"
  | "MODEL_SELECTION"
  | "EDITOR_2D"
  | "PREVIEW_3D"
  | "MEASUREMENTS"
  | "DELIVERY"
  | "PAYMENT"
  | "TRACKING";
