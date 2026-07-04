import React, { useState, useRef } from "react";
import { Order, OrderStatus } from "../types";
import { FABRICS } from "../data";
import { CheckCircle2, Circle, Clock, Upload, ShieldCheck, CreditCard, ChevronRight, FileText, Check, PhoneCall, MapPin } from "lucide-react";

interface OrderTrackingProps {
  orders: Order[];
  onUploadProof: (orderId: string, fileName: string, fileDataUrl: string) => void;
  onSimulateAdminApproval: (orderId: string) => void;
}

export const OrderTracking: React.FC<OrderTrackingProps> = ({
  orders,
  onUploadProof,
  onSimulateAdminApproval
}) => {
  const [selectedOrderId, setSelectedOrderId] = useState<string>(orders[0]?.id || "");
  const [dragActive, setDragActive] = useState<boolean>(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const activeOrder = orders.find(o => o.id === selectedOrderId) || orders[0];

  if (!activeOrder) {
    return (
      <div className="bg-zinc-950 border border-gray-dark rounded-xl p-6 text-center text-gray-400">
        Nenhum pedido efetuado ainda. Vá ao editor para configurar o seu fato!
      </div>
    );
  }

  const selectedFabric = FABRICS.find(f => f.id === activeOrder.configuration.fabricId) || FABRICS[0];

  const stepsList = [
    { status: OrderStatus.PENDING_PAYMENT, label: "Pagamento Pendente", desc: "Faça o envio do comprovativo M-Pesa ou BCI" },
    { status: OrderStatus.VALIDATED, label: "Validado por Alfaiate", desc: "Verificação e correção manual das suas medidas" },
    { status: OrderStatus.IN_PRODUCTION, label: "Em Produção", desc: "O seu fato está a ser costurado no nosso ateliê" },
    { status: OrderStatus.READY, label: "Pronto para Levantamento", desc: "Fato pronto para entrega ou recolha no ponto físico" },
    { status: OrderStatus.IN_TRANSIT, label: "Em Entrega", desc: "O estafeta está a caminho do seu endereço" },
    { status: OrderStatus.DELIVERED, label: "Entregue com Sucesso", desc: "Bespoke SuitUP entregue e validado" }
  ];

  const getStepIndex = (currentStatus: OrderStatus) => {
    return stepsList.findIndex(s => s.status === currentStatus);
  };

  const activeStepIdx = getStepIndex(activeOrder.status);

  // Drag and drop for proof image upload
  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const file = e.dataTransfer.files[0];
      processFile(file);
    }
  };

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      processFile(file);
    }
  };

  const processFile = (file: File) => {
    const reader = new FileReader();
    reader.onload = (event) => {
      if (event.target?.result) {
        onUploadProof(activeOrder.id, file.name, event.target.result as string);
      }
    };
    reader.readAsDataURL(file);
  };

  const triggerFileSelect = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className="bg-black-deep border border-gray-dark rounded-xl p-5 space-y-6 shadow-2xl">
      {/* Horizontal Order Selector Tabs */}
      <div className="flex gap-2.5 overflow-x-auto pb-2 scrollbar-none border-b border-gray-dark/40">
        {orders.map(order => (
          <button
            key={order.id}
            onClick={() => setSelectedOrderId(order.id)}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-lg border text-xs font-mono transition-all text-nowrap cursor-pointer ${
              selectedOrderId === order.id
                ? "bg-gold-premium/10 border-gold-premium text-gold-premium font-semibold"
                : "bg-zinc-900/40 border-gray-dark text-gray-400 hover:text-white-soft hover:bg-zinc-900"
            }`}
          >
            <span className={`w-2 h-2 rounded-full ${order.syncPending ? "bg-amber-500 animate-pulse" : "bg-emerald-500"}`} />
            {order.id}
            {order.syncPending && <span className="text-[9px] text-amber-500 bg-amber-950/40 px-1 rounded uppercase">Offline</span>}
          </button>
        ))}
      </div>

      {/* Main Grid: Tracking Status List + Details Panel */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6" style={{ contentVisibility: "auto" }}>
        {/* LEFT COLUMN: Vertical tracking steps */}
        <div className="lg:col-span-7 space-y-4">
          <div className="flex justify-between items-center bg-zinc-900/40 p-3.5 rounded-lg border border-gray-dark/50">
            <div>
              <span className="section-label text-[9px] mb-0.5">Acompanhar Pedido</span>
              <h4 className="text-gold-premium text-lg font-serif italic font-black tracking-tight">{activeOrder.id}</h4>
            </div>
            <span className="px-3 py-1 bg-gold-premium/15 border border-gold-premium/45 text-gold-premium rounded text-xs font-serif italic font-black uppercase tracking-wider">
              {activeOrder.status}
            </span>
          </div>

          {/* Stepper list */}
          <div className="relative pl-5 space-y-6 py-2">
            {/* Visual connector line */}
            <div className="absolute left-[9px] top-4 bottom-4 w-0.5 bg-zinc-800" />
            <div
              className="absolute left-[9px] top-4 w-0.5 bg-gold-premium transition-all duration-700"
              style={{ height: `${(activeStepIdx / (stepsList.length - 1)) * 90}%` }}
            />

            {stepsList.map((step, idx) => {
              const isCompleted = idx < activeStepIdx;
              const isCurrent = idx === activeStepIdx;
              const isUpcoming = idx > activeStepIdx;

              return (
                <div key={idx} className="relative flex gap-4 items-start group">
                  {/* Indicator Icon */}
                  <div className="absolute -left-5 top-0.5 z-10 flex items-center justify-center bg-black-deep rounded-full p-0.5">
                    {isCompleted && (
                      <CheckCircle2 className="w-4.5 h-4.5 text-gold-premium fill-black" />
                    )}
                    {isCurrent && (
                      <div className="w-4.5 h-4.5 rounded-full bg-gold-premium border-2 border-dashed border-black flex items-center justify-center">
                        <div className="w-1.5 h-1.5 rounded-full bg-black animate-scale-up" />
                      </div>
                    )}
                    {isUpcoming && (
                      <Circle className="w-4.5 h-4.5 text-zinc-700 fill-zinc-950" />
                    )}
                  </div>

                  <div className="flex-1">
                    <h5 className={`text-xs font-serif italic transition-colors ${
                      isCurrent ? "text-gold-premium font-black text-sm" : isCompleted ? "text-gray-300 font-bold" : "text-gray-500"
                    }`}>
                      {step.label}
                    </h5>
                    <p className={`text-[11px] mt-0.5 leading-relaxed font-sans ${isCurrent ? "text-gray-400" : "text-zinc-600"}`}>
                      {step.desc}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* RIGHT COLUMN: Proof and order overview */}
        <div className="lg:col-span-5 space-y-5">
          {/* Checkout Details Breakdown */}
          <div className="bg-zinc-900/50 border border-gray-dark rounded-xl p-4.5 space-y-4">
            <h5 className="text-white-soft text-xs font-serif border-b border-gray-dark pb-2 font-medium tracking-wide uppercase">Detalhes da Personalização</h5>
            
            <div className="space-y-2 text-xs font-mono">
              <div className="flex justify-between">
                <span className="text-gray-500">Modelo Base:</span>
                <span className="text-gray-300">{activeOrder.configuration.model}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Tecido Eleito:</span>
                <span className="text-gray-300 shrink-0 max-w-[120px] truncate">{selectedFabric.name}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Lapela:</span>
                <span className="text-gray-300 truncate max-w-[120px]">{activeOrder.configuration.lapel}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Forro Interior:</span>
                <span className="text-gray-300 truncate max-w-[120px]">{activeOrder.configuration.lining}</span>
              </div>
              <div className="flex justify-between border-t border-gray-dark/50 pt-2 text-sm">
                <span className="text-gray-400 font-serif italic">Valor Total:</span>
                <span className="text-gold-premium font-bold font-mono">{(activeOrder.paidAmount).toLocaleString('pt-PT')} MT</span>
              </div>
            </div>

            {/* Medidas resumo */}
            <div className="bg-zinc-950 p-2.5 rounded border border-gray-dark/40 text-[10px] font-mono text-gray-500 space-y-1">
              <span className="text-gray-400 text-xs uppercase font-serif font-semibold block pb-1 border-b border-gray-dark/20">Saved Dimensions (Medidas)</span>
              <div className="grid grid-cols-3 gap-1 pt-1">
                <div>Ombro: <span className="text-gray-300">{activeOrder.measurements.shoulders}cm</span></div>
                <div>Peito: <span className="text-gray-300">{activeOrder.measurements.chest}cm</span></div>
                <div>Pescoço: <span className="text-gray-300">{activeOrder.measurements.neck}cm</span></div>
                <div>Manga: <span className="text-gray-300">{activeOrder.measurements.sleeves}cm</span></div>
                <div>Cintura: <span className="text-gray-300">{activeOrder.measurements.waist}cm</span></div>
                <div>Altura: <span className="text-gray-300">{activeOrder.measurements.height}cm</span></div>
              </div>
            </div>

            {/* Delivery address mockup */}
            <div className="text-[11px] space-y-1 text-gray-400 pt-1.5 font-sans border-t border-gray-dark/30">
              <span className="text-[10px] text-zinc-500 uppercase font-mono block">Instruções de Envio / Levantamento</span>
              <div className="flex gap-1.5 items-start mt-1">
                <MapPin className="w-3.5 h-3.5 text-gold-premium shrink-0" />
                <span className="text-gray-300 text-xs">
                  {activeOrder.delivery.pickupPointId ? `Pilar Físico: ${activeOrder.delivery.address}` : `${activeOrder.delivery.address}, ${activeOrder.delivery.city}`}
                </span>
              </div>
            </div>
          </div>

          {/* Payment proof handler */}
          {activeOrder.status === OrderStatus.PENDING_PAYMENT ? (
            <div className="border border-gold-premium/40 bg-zinc-900/80 rounded-xl p-4 space-y-3.5 animate-fade-in">
              <div className="flex gap-2 items-center">
                <div className="p-1.5 rounded bg-gold-premium/10 text-gold-premium">
                  <CreditCard className="w-4 h-4" />
                </div>
                <div>
                  <h5 className="text-white-soft text-xs font-serif font-medium uppercase tracking-wide">Pagamento Manual</h5>
                  <p className="text-[10px] text-gray-400">Suporta pagamentos nacionais moçambicanos</p>
                </div>
              </div>

              {/* Instructions text */}
              <div className="bg-zinc-950 p-3 rounded-lg border border-gray-dark/50 text-xs font-mono space-y-1.5 text-gray-300">
                <div className="flex justify-between">
                  <span>M-Pesa Conta:</span>
                  <span className="text-gold-premium font-semibold font-mono">+258 84 999 0011</span>
                </div>
                <div className="flex justify-between border-t border-gray-dark/20 pt-1.5">
                  <span>BCI NIB:</span>
                  <span className="text-gold-premium font-mono">0008 0000 1234 5678 901</span>
                </div>
                <div className="text-[9px] text-zinc-500 italic mt-1 leading-normal pt-1 border-t border-gray-dark/20 text-center">
                  Após a transação, envie o screenshot/comprovativo abaixo para validação instantânea do alfaiate.
                </div>
              </div>

              {/* Visual file uploader box (Drag and Drop / Manual selection) */}
              <div
                onDragEnter={handleDrag}
                onDragOver={handleDrag}
                onDragLeave={handleDrag}
                onDrop={handleDrop}
                onClick={triggerFileSelect}
                className={`border-2 border-dashed rounded-lg p-5 text-center cursor-pointer transition-all ${
                  dragActive
                    ? "border-gold-premium bg-gold-premium/5"
                    : "border-gray-dark hover:border-gold-premium/40 hover:bg-zinc-900/60"
                }`}
                style={{ contentVisibility: "auto" }}
              >
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileInput}
                  className="hidden"
                  accept="image/*"
                />
                
                <Upload className="w-6 h-6 text-gold-premium/80 mx-auto mb-2" />
                <span className="text-xs font-serif font-medium text-white-soft block">
                  Escolher Comprovativo
                </span>
                <span className="text-[10px] text-gray-500 font-mono mt-1 block">
                  Arraste e solte o comprovativo ou clique para procurar (.png, .jpg)
                </span>
              </div>
            </div>
          ) : (
            <div className="bg-zinc-900/40 border border-gray-dark rounded-xl p-4 flex gap-3.5 items-center">
              <div className="p-2.5 rounded-full bg-emerald-600/15 text-emerald-400 border border-emerald-400/20">
                <ShieldCheck className="w-5 h-5 flex-shrink-0" />
              </div>
              <div className="flex-1 min-w-0">
                <span className="text-emerald-400 font-mono text-[9px] uppercase tracking-wider block">Sucesso</span>
                <h5 className="text-white-soft font-serif italic text-xs truncate">Pagamento Recebido & Validado</h5>
                <p className="text-[10px] text-gray-500 truncate font-mono mt-0.5">
                  Ficheiro: {activeOrder.paymentProofName || "comprovativo_arquivado_auto.png"}
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
