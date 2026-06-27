import React, { useState } from "react";
import { Order, OrderStatus } from "../types";
import { Wifi, WifiOff, RefreshCw, Layers, CheckCircle, Clock, Database, Cloud, FileText, Smartphone } from "lucide-react";

interface TechnicalChecklistProps {
  orders: Order[];
  isOffline: boolean;
  onToggleOffline: () => void;
  onSyncOfflineOrders: () => void;
  onUpdateOrderStatus: (orderId: string, status: OrderStatus) => void;
}

export const TechnicalChecklist: React.FC<TechnicalChecklistProps> = ({
  orders,
  isOffline,
  onToggleOffline,
  onSyncOfflineOrders,
  onUpdateOrderStatus
}) => {
  const [activeTab, setActiveTab] = useState<"architecture" | "roadmap">("architecture");
  const [syncing, setSyncing] = useState<boolean>(false);

  const pendingSyncCount = orders.filter(o => o.syncPending).length;

  const handleSyncClick = () => {
    setSyncing(true);
    setTimeout(() => {
      onSyncOfflineOrders();
      setSyncing(false);
    }, 1800);
  };

  return (
    <div className="bg-zinc-950 border border-gray-dark rounded-xl p-5 shadow-2xl h-full flex flex-col">
      {/* Tab Switcher */}
      <div className="flex border-b border-gray-dark pb-3 mb-4 justify-between items-center">
        <div className="flex gap-2.5">
          <button
            onClick={() => setActiveTab("architecture")}
            className={`text-[10px] uppercase tracking-widest font-mono px-3 py-1.5 rounded transition-all font-bold ${
              activeTab === "architecture"
                ? "bg-gold-premium/15 text-gold-premium border border-gold-premium/45"
                : "text-gray-400 hover:text-white"
            }`}
          >
            Arquit. KMP
          </button>
          <button
            onClick={() => setActiveTab("roadmap")}
            className={`text-[10px] uppercase tracking-widest font-mono px-3 py-1.5 rounded transition-all font-bold ${
              activeTab === "roadmap"
                ? "bg-gold-premium/15 text-gold-premium border border-gold-premium/45"
                : "text-gray-400 hover:text-white"
            }`}
          >
            Checklist & Estado
          </button>
        </div>

        {/* Live Simulator Header Badge */}
        <span className="section-label text-[9px] hidden sm:inline text-zinc-650">Debug & Simulador</span>
      </div>

      {activeTab === "architecture" ? (
        <div className="space-y-4 flex-1 overflow-y-auto pr-1">
          {/* Concept 1: Partially Connected Switcher */}
          <div className="bg-zinc-900/60 border border-gray-dark rounded-lg p-3.5 space-y-2.5">
            <div className="flex justify-between items-center">
              <span className="text-gold-premium font-serif italic text-sm flex items-center gap-1.5">
                <Smartphone className="w-4 h-4 text-gold-premium" />
                Operação Parcialmente Conectada
              </span>
              <button
                onClick={onToggleOffline}
                className={`py-1 px-3.5 rounded-full text-xs font-mono font-medium flex items-center gap-1.5 transition-all ${
                  isOffline
                    ? "bg-amber-600/20 text-amber-500 border border-amber-500/40"
                    : "bg-emerald-600/20 text-emerald-400 border border-emerald-400/40"
                }`}
              >
                {isOffline ? (
                  <>
                    <WifiOff className="w-3.5 h-3.5" />
                    Modo Offline
                  </>
                ) : (
                  <>
                    <Wifi className="w-3.5 h-3.5 animate-pulse" />
                    Online (Sincr.)
                  </>
                )}
              </button>
            </div>
            <p className="text-xs text-gray-400 font-sans leading-relaxed">
              O SuitUP permite desenhar e salvar fatos mesmo sem internet. Os pedidos concluídos offline entram em uma fila local (SQLite/SQLDelight no KMP).
            </p>
            {isOffline ? (
              <div className="bg-amber-950/20 border border-amber-500/20 p-2.5 rounded text-xs text-amber-400/90 font-mono">
                ⚠ Simulação ativa: Pedidos efetuados agora ficarão guardados no dispositivo e pendentes de sincronização.
              </div>
            ) : (
              <div className="bg-emerald-950/20 border border-emerald-500/20 p-2.5 rounded text-xs text-emerald-400/90 font-mono">
                ✔ Dispositivo sincronizado com o servidor central (Java Spring Boot + PostgreSQL).
              </div>
            )}
          </div>

          {/* Concept 2: Store and Forward */}
          <div className="bg-zinc-900/60 border border-gray-dark rounded-lg p-3.5 space-y-2.5">
            <div className="flex justify-between items-center">
              <span className="text-gold-premium font-serif italic text-sm flex items-center gap-1.5">
                <Database className="w-4 h-4 text-gold-premium" />
                Armazenar e Encaminhar
              </span>
              <button
                disabled={isOffline || pendingSyncCount === 0 || syncing}
                onClick={handleSyncClick}
                className={`py-1 px-3 rounded-md text-xs font-mono flex items-center gap-1.5 transition-all ${
                  pendingSyncCount > 0 && !isOffline
                    ? "bg-gold-premium text-black-deep font-semibold hover:opacity-95 cursor-pointer"
                    : "bg-zinc-800 text-gray-500 cursor-not-allowed border border-gray-dark"
                }`}
              >
                <RefreshCw className={`w-3.5 h-3.5 ${syncing ? "animate-spin" : ""}`} />
                {syncing ? "A Sincronizar..." : "Sincronizar"}
              </button>
            </div>
            <p className="text-xs text-gray-400 font-sans leading-relaxed">
              Registos pendentes de sincronização: <span className="font-mono text-gold-premium font-semibold">{pendingSyncCount}</span>. Tente mudar para o Modo Online para liberar a fila.
            </p>

            {pendingSyncCount > 0 && (
              <div className="space-y-1.5">
                <span className="text-[10px] text-zinc-500 font-mono uppercase tracking-wider block">Fila de Sincronização Local</span>
                <div className="space-y-1">
                  {orders
                    .filter(o => o.syncPending)
                    .map(o => (
                      <div key={o.id} className="bg-black/40 border border-gray-dark rounded p-1.5 flex justify-between items-center text-xs font-mono">
                        <span className="text-amber-500">{o.id}</span>
                        <span className="text-gray-400">{o.configuration.model}</span>
                        <span className="text-gray-500 text-[10px]">Aguardando Online</span>
                      </div>
                    ))}
                </div>
              </div>
            )}
          </div>

          {/* Concept 3: Fat Client (Cliente Inteligente) */}
          <div className="bg-zinc-900/60 border border-gray-dark rounded-lg p-3.5 space-y-1.5">
            <span className="text-gold-premium font-serif italic text-sm flex items-center gap-1.5">
              <Layers className="w-4 h-4 text-gold-premium" />
              Arquitectura Fat Client / Inteligente
            </span>
            <p className="text-xs text-gray-400 leading-relaxed">
              A lógica de precificação de tecidos (ex: Lã Fria +15.000 MT, Veludo Imperial +21.000 MT), cálculo métrico do manequim 2D/3D e validações de upload ocorrem localmente no dispositivo móvel do cliente, maximizando performance e reduzindo cargas no servidor REST Spring Boot.
            </p>
          </div>

          {/* Administrative Manual Verification System */}
          <div className="bg-zinc-900/40 border border-zinc-900 rounded-lg p-3.5 space-y-1.5">
            <span className="text-white-soft font-serif italic text-xs flex items-center gap-1.5">
              <Cloud className="w-3.5 h-3.5 text-gold-premium" />
              Painel do Alfaiate Admin (Back-Office)
            </span>
            <p className="text-xs text-gray-500">
              Gerencie os estados dos pedidos em tempo real. Útil para validar o comprovativo de pagamento carregado pelo cliente (M-Pesa/BCI) e atualizar o tracking.
            </p>
            <div className="space-y-1.5 pt-1 max-h-[140px] overflow-y-auto pr-1">
              {orders.map(order => (
                <div key={order.id} className="bg-black-deep/60 border border-gray-dark rounded p-1.5 flex justify-between items-center text-xs font-mono">
                  <div>
                    <span className="text-gold-premium text-xs">{order.id}</span>
                    <span className="text-gray-400 text-[10px] block truncate max-w-[120px]">{order.configuration.model}</span>
                  </div>
                  <select
                    value={order.status}
                    onChange={(e) => onUpdateOrderStatus(order.id, e.target.value as OrderStatus)}
                    className="bg-zinc-900 border border-gray-dark rounded text-[11px] text-gray-300 py-0.5 px-1 font-mono focus:border-gold-premium focus:outline-none"
                  >
                    {Object.values(OrderStatus).map(st => (
                      <option key={st} value={st}>{st}</option>
                    ))}
                  </select>
                </div>
              ))}
            </div>
          </div>
        </div>
      ) : (
        <div className="space-y-4 flex-1 overflow-y-auto pr-1">
          {/* Current State / RoadMap details */}
          <div className="bg-zinc-900/60 border border-gray-dark rounded-lg p-3 text-xs">
            <h5 className="font-serif italic text-gold-premium text-sm mb-1.5 flex items-center gap-1">
              <CheckCircle className="w-4 h-4 text-emerald-500" />
              Módulo UX/UI de Alta Fidelidade Concluído
            </h5>
            <p className="text-gray-400 mb-2 leading-relaxed">
              Planeamento de arquitectura e os 16 fluxos estão definidos pela disciplina. Abaixo encontra-se a checklist de implementação técnica.
            </p>
            <div className="flex gap-4 border-t border-gray-dark pt-2 font-mono text-[10px] text-gray-500">
              <div>Realizado: <span className="text-emerald-400">100% (Design)</span></div>
              <div>Simulador: <span className="text-gold-premium">Ativo</span></div>
            </div>
          </div>

          {/* Checklist Tree representing original Document */}
          <div className="space-y-3">
            <div>
              <span className="text-gray-400 font-mono text-[10px] uppercase tracking-wider block mb-1">📱 FRONTEND MOBILE (KMP)</span>
              <ul className="space-y-1 text-xs">
                <li className="flex items-center gap-2 text-emerald-400 font-mono">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                  [✔] Design System & Identidade Visual
                </li>
                <li className="flex items-center gap-2 text-emerald-400 font-mono">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                  [✔] Editor 2D interativo de fatos
                </li>
                <li className="flex items-center gap-2 text-emerald-400 font-mono">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                  [✔] Visualização do Manequim 3D
                </li>
                <li className="flex items-center gap-2 text-amber-500/90 font-mono">
                  <span className="w-1.5 h-1.5 bg-amber-500/80 rounded-full" />
                  [ ] Integração nativa SQLDelight (Local Cache)
                </li>
                <li className="flex items-center gap-2 text-gray-500 font-mono">
                  <span className="w-1.5 h-1.5 bg-gray-700 rounded-full" />
                  [ ] Shared transitions e gestos KMP
                </li>
              </ul>
            </div>

            <div>
              <span className="text-gray-400 font-mono text-[10px] uppercase tracking-wider block mb-1">🌐 BACKEND (Spring Boot + PSQL)</span>
              <ul className="space-y-1 text-xs">
                <li className="flex items-center gap-2 text-emerald-400 font-mono">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                  [✔] Simulação API do Tracking
                </li>
                <li className="flex items-center gap-2 text-amber-500/90 font-mono">
                  <span className="w-1.5 h-1.5 bg-amber-500/80 rounded-full" />
                  [ ] Configuração JWT & Auth API
                </li>
                <li className="flex items-center gap-2 text-gray-500 font-mono">
                  <span className="w-1.5 h-1.5 bg-gray-700 rounded-full" />
                  [ ] Upload real de comprovativos em S3/Disk
                </li>
                <li className="flex items-center gap-2 text-gray-500 font-mono">
                  <span className="w-1.5 h-1.5 bg-gray-700 rounded-full" />
                  [ ] Tabela PostgreSQL no Servidor Cloud
                </li>
              </ul>
            </div>
          </div>
        </div>
      )}

      {/* Footer Design details */}
      <div className="border-t border-gray-dark pt-3 mt-4 text-center">
        <span className="text-[10px] text-gray-500 font-mono block">
          SuitUP • Categoria Premium de Computação Móvel
        </span>
      </div>
    </div>
  );
};
