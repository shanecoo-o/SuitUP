import { FabricOption, SuitStyle, LapelType, ButtonType, PocketType, LiningType, SavedMeasurements, OrderStatus, Order, DeliveryType } from "./types";

export const FABRICS: FabricOption[] = [
  {
    id: "la-120-charcoal",
    name: "Lã Fria Super 120s Cinza",
    description: "Lã fria respirável, ideal para o clima tropical moçambicano. Toque leve e queda impecável.",
    category: "Lã",
    colorHex: "#2D2E32",
    priceModifier: 15000
  },
  {
    id: "la-120-navy",
    name: "Lã Fria Super 120s Azul Real",
    description: "Azul marinho clássico e versátil. O fato definitivo para casamentos ou reuniões importantes.",
    category: "Lã",
    colorHex: "#1B2A47",
    priceModifier: 16500
  },
  {
    id: "la-super-black",
    name: "Lã Fria Italiana Preto Profundo",
    description: "O requinte definitivo. Linhas limpas e absorção perfeita de luz para gala ou negócios.",
    category: "Lã",
    colorHex: "#0F0F11",
    priceModifier: 18000
  },
  {
    id: "linho-gold-cream",
    name: "Linho Fino Creme Imperial",
    description: "Linho natural texturizado premium, arejado e leve. Excelente para eventos de dia ao livre.",
    category: "Linho",
    colorHex: "#EAE0D5",
    priceModifier: 13000
  },
  {
    id: "velvet-bordeaux",
    name: "Veludo Imperial Bordeaux",
    description: "Toque extremamente macio com brilho sofisticado. Recomendado para smokings elegantes.",
    category: "Veludo",
    colorHex: "#4C1C24",
    priceModifier: 21000
  },
  {
    id: "cashmere-emerald",
    name: "Caxemira Mista Verde Esmeralda",
    description: "Lã e caxemira italiana de alta densidade num matiz de esmeralda ousado e sofisticado.",
    category: "Misto",
    colorHex: "#1D3528",
    priceModifier: 24000
  }
];

export const PICKUP_POINTS = [
  { id: "maputo_main", name: "Ateliê Principal Maputo — Av. Julius Nyerere", phone: "+258 84 999 0011" },
  { id: "matola_hub", name: "Pilar Matola — Av. União Moçambicana", phone: "+258 84 999 0022" },
  { id: "beira_office", name: "Ponto de Entrega Beira — Centro da Cidade", phone: "+258 84 999 0033" }
];

export const MOCK_ORDERS: Order[] = [
  {
    id: "ORD-9281",
    timestamp: "2026-06-03T11:20:00Z",
    configuration: {
      model: SuitStyle.SLIM_FIT,
      lapel: LapelType.PEAK,
      buttons: ButtonType.TWO,
      pockets: PocketType.FLAP,
      lining: LiningType.PAISLEY,
      fabricId: "la-120-navy",
      primaryColor: "#1B2A47"
    },
    measurements: {
      neck: 39,
      shoulders: 45,
      chest: 98,
      waist: 84,
      sleeves: 62,
      height: 180
    },
    delivery: {
      type: DeliveryType.PICKUP,
      address: "Ateliê Maputo",
      city: "Maputo",
      phone: "+258 82 123 4567",
      pickupPointId: "maputo_main"
    },
    status: OrderStatus.READY,
    paymentProofName: "screenshot-mpesa-312.png",
    paidAmount: 16500,
    syncPending: false
  },
  {
    id: "ORD-1839",
    timestamp: "2026-06-07T09:45:00Z",
    configuration: {
      model: SuitStyle.TUXEDO,
      lapel: LapelType.SHAWL,
      buttons: ButtonType.ONE,
      pockets: PocketType.WELT,
      lining: LiningType.CRIMSON,
      fabricId: "velvet-bordeaux",
      primaryColor: "#4C1C24"
    },
    measurements: {
      neck: 41,
      shoulders: 47,
      chest: 104,
      waist: 92,
      sleeves: 64,
      height: 185
    },
    delivery: {
      type: DeliveryType.DELIVERY,
      address: "Bairro de Coop, Av. Vladimir Lenine 1230, 4º Esq",
      city: "Maputo",
      phone: "+258 84 765 4321"
    },
    status: OrderStatus.VALIDATED,
    paymentProofName: "screenshot_banco_bci.png",
    paidAmount: 21000,
    syncPending: false
  }
];

export const DEFAULT_MEASUREMENTS: SavedMeasurements = {
  neck: 38,
  shoulders: 44,
  chest: 96,
  waist: 82,
  sleeves: 61,
  height: 178
};
