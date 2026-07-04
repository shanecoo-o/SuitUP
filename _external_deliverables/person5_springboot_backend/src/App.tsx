import { useState, useEffect } from "react";
import {
  ActiveScreen,
  SuitConfiguration,
  SuitStyle,
  LapelType,
  ButtonType,
  PocketType,
  LiningType,
  SavedMeasurements,
  Order,
  OrderStatus,
  DeliveryType,
  DeliveryDetails
} from "./types";
import { FABRICS, MOCK_ORDERS, DEFAULT_MEASUREMENTS, PICKUP_POINTS } from "./data";
import { SuitVisualizer } from "./components/SuitVisualizer";
import { Mannequin3D } from "./components/Mannequin3D";
import { TechnicalChecklist } from "./components/TechnicalChecklist";
import { OrderTracking } from "./components/OrderTracking";
import {
  Scissors,
  Smartphone,
  Sparkles,
  Award,
  BookOpen,
  ChevronLeft,
  ChevronRight,
  TrendingUp,
  CreditCard,
  User,
  ShoppingBag,
  MapPin,
  Heart,
  Plus,
  Compass,
  Database,
  Wifi,
  WifiOff,
  Clock,
  LogOut,
  Sliders,
  Maximize2,
  Check
} from "lucide-react";

export default function App() {
  // Navigation inside simulated mobile
  const [activeScreen, setActiveScreen] = useState<ActiveScreen>("SPLASH");
  const [isOffline, setIsOffline] = useState<boolean>(false);
  const [loggedInUser, setLoggedInUser] = useState<string | null>(null);

  // Custom Suit Config State
  const [suitConfig, setSuitConfig] = useState<SuitConfiguration>({
    model: SuitStyle.SLIM_FIT,
    lapel: LapelType.NOTCH,
    buttons: ButtonType.TWO,
    pockets: PocketType.FLAP,
    lining: LiningType.PLAIN,
    fabricId: "la-120-navy",
    primaryColor: "#1B2A47"
  });

  // Client dimensions state
  const [measurements, setMeasurements] = useState<SavedMeasurements>(DEFAULT_MEASUREMENTS);

  // Delivery configuration
  const [delivery, setDelivery] = useState<DeliveryDetails>({
    type: DeliveryType.PICKUP,
    address: PICKUP_POINTS[0].name,
    city: "Maputo",
    phone: "+258 84 999 0011",
    pickupPointId: PICKUP_POINTS[0].id
  });

  // Persistent Orders List (synced with localStorage)
  const [orders, setOrders] = useState<Order[]>(() => {
    const saved = localStorage.getItem("suitup_orders");
    if (saved) {
      try { return JSON.parse(saved); } catch (e) { return MOCK_ORDERS; }
    }
    return MOCK_ORDERS;
  });

  // Keep LocalStorage up to date
  useEffect(() => {
    localStorage.setItem("suitup_orders", JSON.stringify(orders));
  }, [orders]);

  // Handle splash delay automatically on first turn
  useEffect(() => {
    if (activeScreen === "SPLASH") {
      const timer = setTimeout(() => {
        setActiveScreen("ONBOARDING");
      }, 2500);
      return () => clearTimeout(timer);
    }
  }, [activeScreen]);

  // Sincronizar pedidos efetuados offline
  const handleSyncOfflineOrders = () => {
    setOrders(prev =>
      prev.map(o => (o.syncPending ? { ...o, syncPending: false, status: OrderStatus.VALIDATED } : o))
    );
  };

  // Trigger manual admin updates
  const handleUpdateOrderStatus = (orderId: string, status: OrderStatus) => {
    setOrders(prev => prev.map(o => (o.id === orderId ? { ...o, status } : o)));
  };

  // Submit payment proof
  const handleUploadPaymentProof = (orderId: string, fileName: string, fileDataUrl: string) => {
    setOrders(prev =>
      prev.map(o =>
        o.id === orderId
          ? {
              ...o,
              paymentProofName: fileName,
              paymentProofUrl: fileDataUrl,
              status: OrderStatus.VALIDATED
            }
          : o
      )
    );
  };

  // Admin approval simulation
  const handleSimulateAdminApproval = (orderId: string) => {
    setOrders(prev =>
      prev.map(o => (o.id === orderId ? { ...o, status: OrderStatus.VALIDATED } : o))
    );
  };

  // Create active order
  const handleCreateOrder = () => {
    const activeFabric = FABRICS.find(f => f.id === suitConfig.fabricId) || FABRICS[0];
    const newOrder: Order = {
      id: `ORD-${Math.floor(1000 + Math.random() * 9000)}`,
      timestamp: new Date().toISOString(),
      configuration: { ...suitConfig },
      measurements: { ...measurements },
      delivery: { ...delivery },
      status: OrderStatus.PENDING_PAYMENT,
      paidAmount: 25000 + activeFabric.priceModifier, // Base price + fabric modifier
      syncPending: isOffline
    };

    setOrders(prev => [newOrder, ...prev]);
    setActiveScreen("TRACKING");
  };

  return (
    <div className="min-h-screen bg-black-deep text-white-soft flex flex-col font-sans selection:bg-gold-premium selection:text-black-deep">
      
      {/* LUXURY SLATE HEADER */}
      <header className="border-b border-gray-dark px-6 py-4 bg-zinc-950 flex flex-col sm:flex-row justify-between items-center gap-4 z-40">
        <div className="flex items-center gap-3">
          <div className="h-9 w-9 rounded-md bg-gradient-to-tr from-gold-premium to-yellow-600 flex items-center justify-center shadow-lg">
            <Scissors className="w-5.3 h-5.3 text-black" />
          </div>
          <div>
            <h1 className="brand-title text-2xl sm:text-3xl tracking-tight">
              SuitUP <span className="text-zinc-400 not-italic font-sans text-[10px] font-black tracking-[0.2em] bg-zinc-900 border border-gray-dark px-2 py-0.5 rounded ml-2.5 uppercase">ACADEMIC PRESS</span>
            </h1>
            <p className="text-[10px] text-zinc-500 font-mono tracking-wider mt-0.5">
              Plataforma Móvel de Personalização • Universidade Zambeze / Contexto Moçambique
            </p>
          </div>
        </div>

        {/* Sync indicators and options */}
        <div className="flex items-center gap-4 text-xs font-mono bg-zinc-900 border border-gray-dark rounded-lg px-4 py-1.5 shadow-inner">
          <span className="text-gray-500 uppercase tracking-wider text-[10px] font-bold hidden md:inline">Status Global:</span>
          <div className="flex items-center gap-1.5 font-bold text-gray-300">
            {isOffline ? (
              <>
                <WifiOff className="w-4 h-4 text-amber-500" />
                <span className="text-amber-500 uppercase tracking-widest text-[10px]">Modo Offline</span>
              </>
            ) : (
              <>
                <Wifi className="w-4 h-4 text-emerald-400 animate-pulse" />
                <span className="text-emerald-400 uppercase tracking-widest text-[10px]">CONECTADO</span>
              </>
            )}
          </div>
        </div>
      </header>

      {/* WORKSPACE BENTO GRID */}
      <main className="flex-1 grid grid-cols-1 xl:grid-cols-12 gap-6 p-6">
        
        {/* LEFT COMPONENT (6 Cols): Bespoke presentation cockpit / Quick screen jumpers */}
        <section className="xl:col-span-4 space-y-6 flex flex-col">
          {/* Brief project index banner */}
          <div className="bg-zinc-950 border border-gray-dark rounded-xl p-6 space-y-4 shadow-2xl">
            <span className="section-label">Resumo do Projecto</span>
            <h2 className="text-white-soft text-2xl font-serif italic font-black tracking-tight leading-none">Alfaiataria Bespoke Premium</h2>
            <p className="text-xs text-zinc-400 leading-relaxed font-sans">
              O SuitUP reinventa o processo tradicional de confecção de fatos em Moçambique, substituindo a comunicação manual do WhatsApp por um fluxo automatizado, visualização fidedigna em 3D e acompanhamento estrito do status do pedido com cache de transações offline.
            </p>
            
            {/* Rapid jumpers block to jump across phone flow */}
            <div className="border-t border-gray-dark pt-4">
              <span className="text-[10px] text-zinc-500 font-mono uppercase tracking-[0.15em] font-bold block mb-3">Acesso Rápido a Ecrãs do Fluxo</span>
              <div className="grid grid-cols-3 gap-1.5 text-xs">
                {(["ONBOARDING", "DASHBOARD", "MODEL_SELECTION", "EDITOR_2D", "PREVIEW_3D", "TRACKING"] as ActiveScreen[]).map(sc => (
                  <button
                    key={sc}
                    onClick={() => setActiveScreen(sc)}
                    className={`py-2 px-2.5 rounded font-mono text-[9px] uppercase border transition-all truncate text-left font-bold tracking-wider ${
                      activeScreen === sc
                        ? "bg-gold-premium/15 text-gold-premium border-gold-premium"
                        : "bg-transparent border-gray-dark text-gray-500 hover:text-white-soft hover:border-gray-500"
                    }`}
                  >
                    ⚡ {sc.replace("_", " ")}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Sytem components details and backend emulation */}
          <div className="flex-1">
            <TechnicalChecklist
              orders={orders}
              isOffline={isOffline}
              onToggleOffline={() => setIsOffline(!isOffline)}
              onSyncOfflineOrders={handleSyncOfflineOrders}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </div>
        </section>

        {/* MIDDLE COMPONENT (4 Cols): Highly Polished Smartphone Device Simulator Housing */}
        <section className="xl:col-span-4 flex justify-center items-center">
          <div className="relative w-full max-w-[340px] h-[670px] bg-[#09090A] border-[7px] border-zinc-800 rounded-[38px] shadow-[0_25px_60px_-15px_rgba(0,0,0,0.95)] overflow-hidden flex flex-col">
            
            {/* Physical Island/Speaker/Camera bar */}
            <div className="absolute top-0 inset-x-0 h-6 bg-zinc-800 flex items-center justify-center z-50">
              <div className="w-20 h-3.5 rounded-full bg-black flex items-center justify-center">
                <div className="w-1.5 h-1.5 rounded-full bg-zinc-800 mr-2" />
                <div className="w-8 h-1 rounded-full bg-zinc-900" />
              </div>
            </div>

            {/* Simulated App Screen Canvas */}
            <div className="flex-1 mt-6 flex flex-col bg-black-deep relative select-none">
              
              {/* Dynamic Status Bar */}
              <div className="px-5 py-1.5 flex justify-between items-center text-[10px] font-mono text-gray-500 select-none pointer-events-none z-30 bg-zinc-950/20 backdrop-blur">
                <span>08:35 Z</span>
                <span className="text-zinc-500">M-Cel LTE</span>
                <div className="flex items-center gap-1">
                  {isOffline ? <WifiOff className="w-3 h-3 text-amber-500" /> : <Wifi className="w-3 h-3 text-emerald-400" />}
                  <span>100%</span>
                </div>
              </div>

              {/* SCREEN NAVIGATION ROUTING SWITCHBOARD */}
              <div className="flex-1 flex flex-col overflow-y-auto overflow-x-hidden scrollbar-none pb-4">
                
                {/* 1. SPLASH SCREEN */}
                {activeScreen === "SPLASH" && (
                  <div className="flex-1 flex flex-col justify-center items-center p-6 text-center bg-zinc-950 h-full animate-fade-in">
                    <div className="h-16 w-16 rounded-2xl bg-gradient-to-tr from-gold-premium to-yellow-600 flex items-center justify-center shadow-xl mb-4 animate-scale-up">
                      <Scissors className="w-9 h-9 text-black-deep" />
                    </div>
                    <h1 className="font-serif italic text-3xl tracking-wide text-white-soft">SuitUP</h1>
                    <div className="h-0.5 w-12 bg-gold-premium/40 my-3" />
                    <p className="text-[10px] text-zinc-500 uppercase tracking-widest font-mono">Bespoke Couture</p>
                    <div className="absolute bottom-10 flex gap-1.5 items-center">
                      <div className="w-2 h-2 rounded-full bg-gold-premium animate-pulse" />
                      <span className="text-[10px] text-zinc-600 font-mono">A Carregar Ateliê...</span>
                    </div>
                  </div>
                )}
                {/* 2. ONBOARDING SCREEN */}
                {activeScreen === "ONBOARDING" && (
                  <div className="flex-1 flex flex-col justify-between p-6 bg-zinc-950/80 h-full animate-fade-in text-left">
                    <div className="flex justify-between items-center pt-2">
                      <span className="font-serif italic text-gold-premium font-black text-base tracking-tighter">SuitUP</span>
                      <button onClick={() => setActiveScreen("LOGIN")} className="text-[10px] font-mono text-zinc-500 hover:text-white uppercase tracking-wider font-bold">Saltar</button>
                    </div>

                    <div className="space-y-4 my-auto text-left">
                      <span className="text-[9px] text-gold-premium font-mono uppercase tracking-[0.2em] font-bold block">A Revolução Digital</span>
                      <h2 className="text-white-soft text-2xl font-serif italic tracking-tight font-black leading-tight">
                        Vista o seu percurso sob medida.
                      </h2>
                      <p className="text-[11px] text-zinc-400 leading-relaxed font-sans">
                        Personalize fatos de alfaiataria fina italiana de forma totalmente digital. Escolha tecidos, lapelas e faça os seus pedidos offline com sincronização automática.
                      </p>
                    </div>

                    <div className="space-y-3">
                      <button
                        onClick={() => setActiveScreen("LOGIN")}
                        className="w-full bg-gold-premium text-black-deep font-bold text-xs py-3.5 rounded-xl transition-all hover:bg-opacity-95 text-center flex items-center justify-center gap-1.5 uppercase tracking-widest shadow-lg"
                      >
                        Iniciar Experiência
                        <ChevronRight className="w-4.5 h-4.5" />
                      </button>
                      <span className="text-[9px] text-zinc-650 font-mono text-center block tracking-widest uppercase font-bold">Zara & Boss Minimalist Experience</span>
                    </div>
                  </div>
                )}

                {/* 3. LOGIN SCREEN */}
                {activeScreen === "LOGIN" && (
                  <div className="flex-1 flex flex-col justify-center p-6 space-y-6 animate-fade-in text-left">
                    <div className="space-y-1.5">
                      <span className="text-gold-premium text-[10px] font-mono tracking-[0.2em] uppercase font-bold block">Conta Bespoke</span>
                      <h3 className="font-serif italic text-2xl font-black text-white-soft tracking-tight">Entrar no Ateliê</h3>
                      <p className="text-[11px] text-gray-500 font-sans">Aceda à sua carteira de medidas exclusivas.</p>
                    </div>

                    <div className="space-y-4">
                      <div className="space-y-1.5">
                        <label className="text-[9px] text-zinc-500 font-mono uppercase tracking-wider font-bold">E-mail Alfaiataria</label>
                        <input
                          type="email"
                          placeholder="ex: cliente@mpesa.co.mz"
                          defaultValue="carlos.bespoke@gmail.com"
                          className="w-full bg-zinc-900 border border-gray-dark rounded-lg py-2.5 px-3.5 text-xs text-white-soft focus:border-gold-premium focus:outline-none"
                        />
                      </div>
                      <div className="space-y-1.5">
                        <label className="text-[9px] text-zinc-500 font-mono uppercase tracking-wider font-bold">Palavra-passe</label>
                        <input
                          type="password"
                          placeholder="••••••••"
                          defaultValue="secret123"
                          className="w-full bg-zinc-900 border border-gray-dark rounded-lg py-2.5 px-3.5 text-xs text-white-soft focus:border-gold-premium focus:outline-none"
                        />
                      </div>
                    </div>

                    <div className="space-y-3.5 pt-2">
                      <button
                        onClick={() => {
                          setLoggedInUser("Carlos M.");
                          setActiveScreen("DASHBOARD");
                        }}
                        className="w-full bg-gold-premium text-black-deep font-bold text-xs py-3 rounded-xl transition-all uppercase tracking-widest shadow-lg"
                      >
                        Entrar com Segurança
                      </button>
                      
                      <button
                        onClick={() => {
                          setLoggedInUser("Convidado");
                          setActiveScreen("DASHBOARD");
                        }}
                        className="w-full text-center text-zinc-400 hover:text-white text-xs font-serif italic font-bold tracking-wide"
                      >
                        Continuar como Convidado
                      </button>
                    </div>
                  </div>
                )}

                {/* 4. HOME DASHBOARD */}
                {activeScreen === "DASHBOARD" && (
                  <div className="flex-1 p-5 space-y-5 animate-fade-in relative z-20 text-left">
                    {/* Header */}
                    <div className="flex justify-between items-center">
                      <div>
                        <span className="text-zinc-500 text-[9px] font-mono uppercase tracking-widest font-bold block">BEM-VINDO AO ATELIÊ</span>
                        <h3 className="font-serif italic text-lg text-gold-premium font-black tracking-tight mt-0.5">Olá, {loggedInUser || "Carlos M."}</h3>
                      </div>
                      <button
                        onClick={() => {
                          setLoggedInUser(null);
                          setActiveScreen("LOGIN");
                        }}
                        className="p-1 px-2 rounded bg-zinc-900 text-zinc-500 hover:text-white-soft border border-gray-dark transition-all"
                      >
                        <LogOut className="w-3.5 h-3.5" />
                      </button>
                    </div>

                    {/* Fast Create Suits Hero Card */}
                    <div className="bg-gradient-to-tr from-zinc-900 to-black-deep border border-gold-premium/40 p-5 rounded-xl space-y-4 text-center shadow-2xl relative overflow-hidden">
                      <div className="absolute top-0 right-0 w-24 h-24 bg-gold-premium/5 rounded-full blur-xl pointer-events-none" />
                      <div className="space-y-1">
                        <span className="text-[9px] font-mono text-gold-premium uppercase tracking-[0.2em] font-bold block">Novo Fato</span>
                        <h4 className="font-serif italic text-base text-white-soft font-black tracking-tight">Desenhar Peça Exclusiva</h4>
                        <p className="text-[10px] text-zinc-400 font-sans leading-normal">Escolha lapelas refinadas, tecidos nobres e forros italianos personalizados.</p>
                      </div>
                      <button
                        onClick={() => setActiveScreen("MODEL_SELECTION")}
                        className="w-full bg-gold-premium text-black-deep font-bold text-[11px] py-2.5 rounded-lg cursor-pointer flex items-center justify-center gap-1.5 uppercase tracking-wider shadow-lg"
                      >
                        <Plus className="w-4 h-4" />
                        Iniciar Setup 2D/3D
                      </button>
                    </div>

                    {/* Summary statistics */}
                    <div className="grid grid-cols-2 gap-2 text-xs font-mono">
                      <div className="bg-zinc-900 p-3 rounded-lg border border-gray-dark/50">
                        <span className="text-gray-500 text-[9px] uppercase tracking-wider block font-bold">Fila Local</span>
                        <div className="text-sm font-black text-gold-premium font-mono mt-0.5">{orders.filter(o => o.syncPending).length} pendente</div>
                      </div>
                      <div className="bg-zinc-900 p-3 rounded-lg border border-gray-dark/50">
                        <span className="text-gray-500 text-[9px] uppercase tracking-wider block font-bold">Pedidos Total</span>
                        <div className="text-sm font-black text-gray-300 font-mono mt-0.5">{orders.length} pedidos</div>
                      </div>
                    </div>

                    {/* Saved measures status */}
                    <div className="bg-zinc-900/40 p-4 rounded-lg border border-gray-dark/40 space-y-2.5">
                      <div className="flex justify-between items-center text-xs">
                        <span className="text-gray-400 font-serif italic font-extrabold">Como Medimos o Seu Caimento</span>
                        <button onClick={() => setActiveScreen("MEASUREMENTS")} className="text-gold-premium text-[10px] uppercase font-mono tracking-wider font-bold">Editar</button>
                      </div>
                      <div className="grid grid-cols-3 gap-1.5 text-[10px] text-zinc-500 font-mono font-medium">
                        <div>Ombro: <span className="text-zinc-300 font-bold">{measurements.shoulders}</span></div>
                        <div>Peito: <span className="text-zinc-300 font-bold">{measurements.chest}</span></div>
                        <div>Manga: <span className="text-zinc-300 font-bold">{measurements.sleeves}</span></div>
                      </div>
                    </div>

                    {/* Real-time Order Tracker quick shortcut if orders exist */}
                    {orders.length > 0 && (
                      <div className="space-y-2.5">
                        <div className="flex justify-between items-center text-xs">
                          <span className="text-zinc-500 font-mono text-[9px] uppercase tracking-widest font-bold">ÚLTIMAS ENCOMENDAS</span>
                          <button onClick={() => setActiveScreen("TRACKING")} className="text-gold-premium text-[10px] uppercase font-mono tracking-wider font-bold">Ver Todos</button>
                        </div>
                        <div
                          onClick={() => setActiveScreen("TRACKING")}
                          className="bg-zinc-900 p-3 rounded-lg border border-gray-dark hover:border-gold-premium/40 transition-all flex items-center justify-between cursor-pointer shadow-md"
                        >
                          <div>
                            <span className="text-gold-premium text-xs font-mono font-black tracking-wider">{orders[0].id}</span>
                            <span className="text-zinc-400 text-[10px] block mt-0.5 font-mono truncate max-w-[140px]">{orders[0].configuration.model}</span>
                          </div>
                          <span className="text-[10px] font-mono bg-zinc-800 text-gray-300 py-1 px-2.5 rounded font-bold uppercase tracking-wider">
                            {orders[0].status}
                          </span>
                        </div>
                      </div>
                    )}
                  </div>
                )}

                {/* 5. MODEL SELECTION */}
                {activeScreen === "MODEL_SELECTION" && (
                  <div className="flex-1 p-5 space-y-4 animate-fade-in relative z-20 text-left">
                    <div className="flex items-center gap-2">
                      <button onClick={() => setActiveScreen("DASHBOARD")} className="p-1 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white-soft">
                        <ChevronLeft className="w-4 h-4" />
                      </button>
                      <span className="text-[9px] uppercase tracking-[0.15em] font-mono text-zinc-500 font-bold">Módulo 1 de 4</span>
                    </div>

                    <div className="space-y-1">
                      <h4 className="font-serif italic text-lg text-white-soft font-black tracking-tight">Selecione o Modelo Base</h4>
                      <p className="text-[10px] text-zinc-450 font-sans leading-normal">Cada estilo dita as linhas de costura mestra</p>
                    </div>

                    <div className="space-y-2.5">
                      {[
                        { style: SuitStyle.SLIM_FIT, tag: "Mais Executivo / Moderno", desc: "Lapelas ligeiramente mais finas, silhueta acentuada e costas cintadas.", ratio: "Slim-Fit" },
                        { style: SuitStyle.CLASSIC, tag: "Tradicional / Atemporal", desc: "Folga confortável nos ombros, caimento perfeito e trespasse duplo tradicional.", ratio: "Executivo" },
                        { style: SuitStyle.TUXEDO, tag: "Gala / Casamento / Tuxedo", desc: "Lapela xaile revestida em cetim acetinado refinado. O auge da sofisticação.", ratio: "Gala" },
                        { style: SuitStyle.MODERN, tag: "Linha Contemporânea minimalista", desc: "Afastamento de forros pesados para leveza e frescor. Inspirado nas tendências Zara.", ratio: "Minimal" }
                      ].map(modelObj => (
                        <div
                          key={modelObj.style}
                          onClick={() => {
                            setSuitConfig(prev => ({ ...prev, model: modelObj.style }));
                            setActiveScreen("EDITOR_2D");
                          }}
                          className={`p-3.5 rounded-xl border transition-all text-left cursor-pointer group ${
                            suitConfig.model === modelObj.style
                              ? "bg-gold-premium/[0.04] border-gold-premium"
                              : "bg-zinc-900/50 border-gray-dark/60 hover:border-gold-premium/40 hover:bg-zinc-900"
                          }`}
                        >
                          <div className="flex justify-between items-start">
                            <span className="text-xs font-serif font-bold text-white-soft group-hover:text-gold-premium transition-colors">{modelObj.style}</span>
                            <span className="text-[8px] font-mono text-gold-premium bg-gold-premium/15 border border-gold-premium/30 px-1.5 py-0.5 rounded uppercase tracking-widest font-bold">{modelObj.ratio}</span>
                          </div>
                          <p className="text-[10px] text-zinc-400 mt-1.5 leading-relaxed font-sans">{modelObj.desc}</p>
                          <span className="text-[9px] text-zinc-500 italic block mt-1.5 font-mono font-bold tracking-wide">{modelObj.tag}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* 6. EDITOR 2D */}
                {activeScreen === "EDITOR_2D" && (
                  <div className="flex-1 p-5 space-y-4 animate-fade-in relative z-20">
                    <div className="flex justify-between items-center">
                      <div className="flex items-center gap-2">
                        <button onClick={() => setActiveScreen("MODEL_SELECTION")} className="p-1 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white">
                          <ChevronLeft className="w-4 h-4" />
                        </button>
                        <span className="text-xs uppercase tracking-wider font-mono text-zinc-500">Editor 2D</span>
                      </div>
                      
                      {/* Jump and swap button to 3D */}
                      <button
                        onClick={() => setActiveScreen("PREVIEW_3D")}
                        className="py-1 px-2.5 rounded bg-zinc-900 border border-gold-premium/30 hover:border-gold-premium hover:text-gold-premium transition-all text-[10px] font-mono text-zinc-300 flex items-center gap-1 cursor-pointer"
                      >
                        Ver 3D
                        <ChevronRight className="w-3 h-3" />
                      </button>
                    </div>

                    {/* Integrated visualizer inside phone viewport */}
                    <SuitVisualizer
                      config={suitConfig}
                      selectedFabricName={FABRICS.find(f => f.id === suitConfig.fabricId)?.name || ""}
                      onChangeConfig={(updated) => setSuitConfig(prev => ({ ...prev, ...updated }))}
                    />

                    {/* Quick Config Collapsible controls for phone */}
                    <div className="space-y-3.5 bg-zinc-900/60 p-3 rounded-lg border border-gray-dark/50" style={{ contentVisibility: "auto" }}>
                      
                      {/* Fabric choosing */}
                      <div className="space-y-1.5 animate-fade-in">
                        <span className="text-[9px] text-zinc-500 font-mono uppercase tracking-wider block">Escolha de Tecidos</span>
                        <div className="grid grid-cols-6 gap-1.5">
                          {FABRICS.map(fabric => (
                            <button
                              key={fabric.id}
                              onClick={() => setSuitConfig(prev => ({ ...prev, fabricId: fabric.id, primaryColor: fabric.colorHex }))}
                              className={`h-7 rounded-md border text-[11px] relative flex items-center justify-center transition-all ${
                                suitConfig.fabricId === fabric.id
                                  ? "border-gold-premium ring-1 ring-gold-premium/30 scale-105"
                                  : "border-gray-dark"
                              }`}
                              style={{ backgroundColor: fabric.colorHex }}
                              title={fabric.name}
                            >
                              {suitConfig.fabricId === fabric.id && (
                                <div className="absolute inset-0 bg-black/25 flex items-center justify-center rounded-md">
                                  <div className="w-1.5 h-1.5 rounded-full bg-white animate-pulse" />
                                </div>
                              )}
                            </button>
                          ))}
                        </div>
                        <span className="text-[10px] text-gold-premium block font-serif italic font-medium truncate">
                          {FABRICS.find(f => f.id === suitConfig.fabricId)?.name}
                        </span>
                      </div>

                      {/* Lapel details */}
                      <div className="space-y-1">
                        <span className="text-[9px] text-zinc-500 font-mono uppercase tracking-wider block">Estilo da Lapela</span>
                        <div className="grid grid-cols-3 gap-1">
                          {Object.values(LapelType).map(lapel => (
                            <button
                              key={lapel}
                              onClick={() => setSuitConfig(prev => ({ ...prev, lapel }))}
                              className={`py-1 rounded text-[10px] font-mono transition-all border text-center ${
                                suitConfig.lapel === lapel
                                  ? "bg-gold-premium text-black-deep border-gold-premium font-semibold"
                                  : "bg-transparent border-gray-dark text-gray-400 hover:text-white-soft"
                              }`}
                            >
                              {lapel.split(" ")[0]}
                            </button>
                          ))}
                        </div>
                      </div>

                      {/* Lining interior styled */}
                      <div className="space-y-1">
                        <span className="text-[9px] text-zinc-500 font-mono uppercase tracking-wider block">Forro Interno</span>
                        <div className="grid grid-cols-2 gap-1 text-[10px]">
                          {Object.values(LiningType).map(lining => (
                            <button
                              key={lining}
                              onClick={() => setSuitConfig(prev => ({ ...prev, lining }))}
                              className={`py-1 rounded font-mono transition-all border truncate text-center px-1 ${
                                suitConfig.lining === lining
                                  ? "bg-gold-premium/10 text-gold-premium border-gold-premium font-semibold"
                                  : "bg-transparent border-gray-dark text-gray-400 hover:text-white-soft"
                              }`}
                            >
                              {lining.split(" ")[1] || "Liso"}
                            </button>
                          ))}
                        </div>
                      </div>
                    </div>

                    {/* Step forward to 3D */}
                    <button
                      onClick={() => setActiveScreen("PREVIEW_3D")}
                      className="w-full bg-gold-premium text-black-deep font-semibold text-xs py-2.5 rounded-xl transition-all hover:bg-opacity-95 text-center flex items-center justify-center gap-1.5 cursor-pointer shadow-lg"
                    >
                      Avançar para Manequim 3D
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                )}

                {/* 7. PREVIEW 3D */}
                {activeScreen === "PREVIEW_3D" && (
                  <div className="flex-1 p-5 space-y-4 animate-fade-in relative z-20">
                    <div className="flex justify-between items-center">
                      <div className="flex items-center gap-2">
                        <button onClick={() => setActiveScreen("EDITOR_2D")} className="p-1 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white">
                          <ChevronLeft className="w-4 h-4" />
                        </button>
                        <span className="text-xs uppercase tracking-wider font-mono text-zinc-500">Mapeamento 3D</span>
                      </div>
                    </div>

                    {/* Real structured Mannequin rotational canvas inside phone viewport */}
                    <Mannequin3D config={suitConfig} />

                    <div className="bg-zinc-900/60 p-3 rounded-lg border border-gray-dark/50 text-xs text-gray-400 space-y-2">
                      <span className="text-[9px] text-zinc-500 font-mono uppercase block">Sintetizador Bespoke</span>
                      <p className="leading-relaxed text-[11px]">
                        Arraste o slider no simulador para avaliar as costuras de ombros e caimento sob diferentes ângulos e iluminações de estúdio.
                      </p>
                    </div>

                    <button
                      onClick={() => setActiveScreen("MEASUREMENTS")}
                      className="w-full bg-gold-premium text-black-deep font-semibold text-xs py-2.5 rounded-xl transition-all hover:bg-opacity-95 text-center flex items-center justify-center gap-1.5 cursor-pointer shadow-lg"
                    >
                      Avançar para Medições
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                )}

                {/* 8. MEASUREMENTS FORM (MEDIDAS) */}
                {activeScreen === "MEASUREMENTS" && (
                  <div className="flex-1 p-5 space-y-4 animate-fade-in relative z-20">
                    <div className="flex items-center gap-2">
                      <button onClick={() => setActiveScreen("PREVIEW_3D")} className="p-1 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white">
                        <ChevronLeft className="w-4 h-4" />
                      </button>
                      <span className="text-xs uppercase tracking-wider font-mono text-zinc-500">Módulo 3 de 4</span>
                    </div>

                    <div className="space-y-1">
                      <h4 className="font-serif italic text-base text-white-soft">Caderno de Medidas</h4>
                      <p className="text-[10px] text-zinc-400">Guarde as suas dimensões corporais (em centímetros)</p>
                    </div>

                    {/* Medidas input variables */}
                    <div className="grid grid-cols-2 gap-2 text-xs" style={{ contentVisibility: "auto" }}>
                      {[
                        { label: "Pescoço", key: "neck", min: 30, max: 55 },
                        { label: "Ombros (Largura)", key: "shoulders", min: 38, max: 60 },
                        { label: "Perímetro Torácico", key: "chest", min: 80, max: 130 },
                        { label: "Cintura", key: "waist", min: 70, max: 120 },
                        { label: "Comprimento Mangas", key: "sleeves", min: 50, max: 75 },
                        { label: "Altura Tronco (cm)", key: "height", min: 140, max: 210 }
                      ].map(field => (
                        <div key={field.key} className="bg-zinc-900 p-2 rounded-lg border border-gray-dark/50 space-y-1 text-left">
                          <label className="text-[9px] text-zinc-500 font-mono uppercase block">{field.label}</label>
                          <div className="flex items-center justify-between">
                            <input
                              type="number"
                              value={measurements[field.key as keyof SavedMeasurements]}
                              onChange={(e) => {
                                const val = parseInt(e.target.value) || field.min;
                                setMeasurements(prev => ({ ...prev, [field.key]: val }));
                              }}
                              className="w-12 bg-transparent border-none text-xs text-white-soft font-mono focus:outline-none"
                            />
                            <span className="text-gold-premium font-mono text-[10px]">CM</span>
                          </div>
                        </div>
                      ))}
                    </div>

                    {/* Simple quick presets */}
                    <div className="bg-zinc-950 p-2.5 rounded-lg border border-gray-dark/40">
                      <span className="text-[8px] text-zinc-500 font-mono uppercase tracking-wider block mb-1">Predefinições Rápidas</span>
                      <div className="grid grid-cols-3 gap-1.5 text-[10px] font-mono">
                        <button
                          onClick={() => setMeasurements({ neck: 37, shoulders: 42, chest: 92, waist: 78, sleeves: 59, height: 172 })}
                          className="py-1 px-2 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white"
                        >
                          Slim M
                        </button>
                        <button
                          onClick={() => setMeasurements({ neck: 39, shoulders: 44, chest: 98, waist: 86, sleeves: 61, height: 180 })}
                          className="py-1 px-2 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white"
                        >
                          Slim L
                        </button>
                        <button
                          onClick={() => setMeasurements({ neck: 41, shoulders: 47, chest: 106, waist: 94, sleeves: 63, height: 186 })}
                          className="py-1 px-2 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white"
                        >
                          L Confort
                        </button>
                      </div>
                    </div>

                    <button
                      onClick={() => setActiveScreen("DELIVERY")}
                      className="w-full bg-gold-premium text-black-deep font-semibold text-xs py-2.5 rounded-xl transition-all hover:bg-opacity-95 text-center flex items-center justify-center gap-1.5 cursor-pointer shadow-lg"
                    >
                      Avançar para o Envio
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                )}

                {/* 9. DELIVERY & CHECKOUT STAGE */}
                {activeScreen === "DELIVERY" && (
                  <div className="flex-1 p-5 space-y-4 animate-fade-in relative z-20">
                    <div className="flex items-center gap-2">
                      <button onClick={() => setActiveScreen("MEASUREMENTS")} className="p-1 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white">
                        <ChevronLeft className="w-4 h-4" />
                      </button>
                      <span className="text-xs uppercase tracking-wider font-mono text-zinc-500">Módulo Final</span>
                    </div>

                    <div className="space-y-1">
                      <h4 className="font-serif italic text-base text-white-soft">Envio e Entrega</h4>
                      <p className="text-[10px] text-zinc-400">Escolha o seu ponto físico ou indique a morada</p>
                    </div>

                    {/* Delivery type selection tabs */}
                    <div className="flex bg-zinc-900 border border-gray-dark p-0.5 rounded-lg text-xs font-mono">
                      <button
                        onClick={() => setDelivery(prev => ({ ...prev, type: DeliveryType.PICKUP, address: PICKUP_POINTS[0].name }))}
                        className={`flex-1 py-1 px-1 rounded-md text-center transition-all ${
                          delivery.type === DeliveryType.PICKUP
                            ? "bg-gold-premium text-black-deep font-semibold"
                            : "text-gray-400 hover:text-white"
                        }`}
                      >
                        Recolha Física
                      </button>
                      <button
                        onClick={() => setDelivery(prev => ({ ...prev, type: DeliveryType.DELIVERY, address: "Bairro Coop, Av. Vladimir Lenine" }))}
                        className={`flex-1 py-1 px-1 rounded-md text-center transition-all ${
                          delivery.type === DeliveryType.DELIVERY
                            ? "bg-gold-premium text-black-deep font-semibold"
                            : "text-gray-400 hover:text-white"
                        }`}
                      >
                        Domicílio Moç.
                      </button>
                    </div>

                    {/* Form rendering according to selection */}
                    {delivery.type === DeliveryType.PICKUP ? (
                      <div className="space-y-2 text-xs" style={{ contentVisibility: "auto" }}>
                        <label className="text-[9px] text-zinc-500 font-mono uppercase">Escolha o Pilar Físico</label>
                        <div className="space-y-1.5">
                          {PICKUP_POINTS.map(p => (
                            <div
                              key={p.id}
                              onClick={() => setDelivery(prev => ({ ...prev, address: p.name, pickupPointId: p.id }))}
                              className={`p-2.5 rounded-lg border text-left cursor-pointer transition-all ${
                                delivery.pickupPointId === p.id
                                  ? "bg-gold-premium/[0.03] border-gold-premium"
                                  : "bg-zinc-900/60 border-zinc-900"
                              }`}
                            >
                              <span className="text-white-soft font-serif italic text-xs block">{p.name}</span>
                              <span className="text-[10px] text-zinc-500 font-mono block mt-0.5">{p.phone}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    ) : (
                      <div className="space-y-3.5 text-xs font-mono" style={{ contentVisibility: "auto" }}>
                        <div className="space-y-1">
                          <label className="text-[9px] text-zinc-500 font-mono uppercase block">Cidade</label>
                          <select
                            value={delivery.city}
                            onChange={(e) => setDelivery(prev => ({ ...prev, city: e.target.value }))}
                            className="w-full bg-zinc-900 border border-gray-dark rounded-lg py-1.5 px-3 focus:outline-none focus:border-gold-premium"
                          >
                            <option value="Maputo">Maputo</option>
                            <option value="Matola">Matola</option>
                            <option value="Beira">Beira</option>
                            <option value="Nampula">Nampula</option>
                          </select>
                        </div>
                        <div className="space-y-1">
                          <label className="text-[9px] text-zinc-500 font-mono uppercase block font-medium">Endereço de Entrega</label>
                          <input
                            type="text"
                            placeholder="ex: Bairro de Sommerschield, Av. Mao Tsé Tung, Prédio 2"
                            value={delivery.address}
                            onChange={(e) => setDelivery(prev => ({ ...prev, address: e.target.value }))}
                            className="w-full bg-zinc-900 border border-gray-dark rounded-lg py-1.5 px-3 focus:outline-none focus:border-gold-premium placeholder-zinc-650 text-xs"
                          />
                        </div>
                      </div>
                    )}

                    {/* Contact Phone */}
                    <div className="space-y-1 text-xs font-mono">
                      <label className="text-[9px] text-zinc-500 font-mono uppercase block">Telemóvel Contacto</label>
                      <input
                        type="text"
                        placeholder="ex: +258 84 123 4567"
                        value={delivery.phone}
                        onChange={(e) => setDelivery(prev => ({ ...prev, phone: e.target.value }))}
                        className="w-full bg-zinc-900 border border-gray-dark rounded-lg py-1.5 px-3 focus:outline-none focus:border-gold-premium text-xs"
                      />
                    </div>

                    {/* Submit checkout and order preview */}
                    <button
                      onClick={handleCreateOrder}
                      className="w-full bg-gold-premium text-black-deep font-semibold text-xs py-3 rounded-xl transition-all hover:bg-opacity-95 text-center flex items-center justify-center gap-1.5 cursor-pointer shadow-lg mt-4"
                    >
                      Fechar Pedido & Pagar Manual
                    </button>
                  </div>
                )}

                {/* 10. TRACKING SCREEN */}
                {activeScreen === "TRACKING" && (
                  <div className="flex-1 p-5 space-y-4 animate-fade-in relative z-20">
                    <div className="flex justify-between items-center">
                      <button onClick={() => setActiveScreen("DASHBOARD")} className="p-1 rounded bg-zinc-900 border border-gray-dark text-gray-400 hover:text-white">
                        <ChevronLeft className="w-4 h-4" />
                      </button>
                      <span className="text-[10px] text-gold-premium font-mono uppercase tracking-widest">Portal de Rastreio</span>
                    </div>

                    <div className="space-y-1.5">
                      <h4 className="font-serif italic text-base text-white-soft">Estado das Minhas Custas</h4>
                      <p className="text-[10px] text-zinc-500 font-sans">Acompanhe a sua alfaiataria em tempo real</p>
                    </div>

                    {/* Mini embedded scroll wrapper */}
                    <div className="max-h-[360px] overflow-y-auto pr-1">
                      <OrderTracking
                        orders={orders}
                        onUploadProof={handleUploadPaymentProof}
                        onSimulateAdminApproval={handleSimulateAdminApproval}
                      />
                    </div>
                  </div>
                )}
              </div>

              {/* SIMULATED EMBEDDED PHONE NAV RAIL BAR */}
              <div className="h-12 border-t border-gray-dark/50 bg-zinc-950 flex justify-around items-center px-4 self-end w-full z-30">
                {[
                  { label: "Ateliê", icon: Compass, screen: "DASHBOARD" as ActiveScreen },
                  { label: "Alfaiatar", icon: Scissors, screen: "MODEL_SELECTION" as ActiveScreen },
                  { label: "Rastreio", icon: ShoppingBag, screen: "TRACKING" as ActiveScreen }
                ].map(item => {
                  const Icon = item.icon;
                  return (
                    <button
                      key={item.label}
                      onClick={() => setActiveScreen(item.screen)}
                      className={`flex flex-col items-center justify-center text-center py-1 transition-all flex-1 cursor-pointer ${
                        activeScreen === item.screen ||
                        (item.screen === "MODEL_SELECTION" && (activeScreen === "EDITOR_2D" || activeScreen === "PREVIEW_3D" || activeScreen === "MEASUREMENTS"))
                          ? "text-gold-premium"
                          : "text-zinc-550 hover:text-white-soft"
                      }`}
                    >
                      <Icon className="w-4.5 h-4.5" />
                      <span className="text-[9px] font-sans mt-0.5">{item.label}</span>
                    </button>
                  );
                })}
              </div>
            </div>
          </div>
        </section>

        {/* RIGHT COMPONENT (4 Cols): Academic Defense Showcase Details / Presentation Cards */}
        <section className="xl:col-span-4 space-y-6 flex flex-col">
          
          {/* Active Configuration Overview Card */}
          <div className="bg-zinc-950 border border-gray-dark rounded-xl p-5 space-y-4">
            <div className="flex justify-between items-center border-b border-gray-dark pb-3">
              <div>
                <span className="text-zinc-500 text-[10px] font-mono uppercase block">Visualização de Amostra</span>
                <h3 className="font-serif italic text-sm text-gold-premium">Especificações de Fato Activas</h3>
              </div>
              <Sparkles className="w-4 h-4 text-gold-premium" />
            </div>

            {/* Quick visualization panel */}
            <div className="space-y-3.5 text-xs font-mono">
              <div className="flex justify-between">
                <span className="text-gray-500">Modelo Selecionado:</span>
                <span className="text-gray-200">{suitConfig.model}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Ombros & Lapela:</span>
                <span className="text-gray-200">{suitConfig.lapel}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Disposição Botões:</span>
                <span className="text-gray-200">{suitConfig.buttons}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Tipo de Bolso:</span>
                <span className="text-gray-200">{suitConfig.pockets}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Gola e Forros:</span>
                <span className="text-gray-200 text-right">{suitConfig.lining}</span>
              </div>
              
              <div className="bg-zinc-900 p-3 rounded-lg border border-gray-dark/50">
                <span className="text-[10px] text-zinc-500 uppercase tracking-wider block font-medium mb-1.5">Tecido Ativo</span>
                <div className="flex gap-2.5 items-center">
                  <div className="w-5 h-5 rounded border border-gray-dark shrink-0" style={{ backgroundColor: suitConfig.primaryColor }} />
                  <div>
                    <span className="text-white-soft text-xs font-serif italic block font-semibold">
                      {FABRICS.find(f => f.id === suitConfig.fabricId)?.name || "Lã Fria"}
                    </span>
                    <span className="text-[10px] text-zinc-500 block">
                      {FABRICS.find(f => f.id === suitConfig.fabricId)?.description || ""}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Academic / Architectural Principles Information Box with M-Pesa Maputo specific flow */}
          <div className="bg-zinc-950 border border-gray-dark rounded-xl p-5 space-y-4 flex-1 flex flex-col justify-between">
            <div className="space-y-3">
              <span className="text-zinc-500 text-[10px] font-mono uppercase tracking-widest block">Valores do Esboço de Negócios</span>
              <h3 className="text-white-soft text-base font-serif italic">Alfaiataria Organizada p/ Moçambique</h3>
              <p className="text-xs text-gray-400 leading-relaxed font-sans">
                Para superar limitações de redes nacionais e dependências de APIs bancárias dispendiosas, o SuitUP apoia-se em:
              </p>
              
              <ul className="space-y-2.5 text-xs text-gray-500">
                <li className="flex gap-2.5 items-start">
                  <Check className="w-4 h-4 text-gold-premium shrink-0 mt-0.5" />
                  <span><strong>M-Pesa / Emola / Conta BCI Manual</strong>: Eliminação da necessidade de gateways externos, processamento através de comprovativo de foto e verificação humana do alfaiate.</span>
                </li>
                <li className="flex gap-2.5 items-start">
                  <Check className="w-4 h-4 text-gold-premium shrink-0 mt-0.5" />
                  <span><strong>Design Luxo Minimalista</strong>: Cores profundas (#0D0D0D) e detalhes em ouro de alfaiataria (#C8A96A), inspirados nas maiores grifes de alta costura contemporânea mundial.</span>
                </li>
              </ul>
            </div>

            {/* Simulated REST status endpoint response box */}
            <div className="bg-zinc-900/60 p-3 rounded-lg border border-gray-dark/40 font-mono text-[10px] space-y-1 mt-4">
              <span className="text-zinc-500 font-mono block uppercase text-[8px] tracking-wider">REST Spring Boot API Server Simulation</span>
              <div className="flex justify-between text-zinc-400">
                <span>GET /api/orders/{orders[0]?.id || "ORD-9281"}</span>
                <span className="text-emerald-400 font-bold">200 OK</span>
              </div>
              <pre className="text-zinc-500 overflow-x-auto text-[9px] pt-1">
                {`{
  "id": "${orders[0]?.id || "ORD-9281"}",
  "status": "${orders[0]?.status || "Pagamento pendente"}",
  "comprovativo": "${orders[0]?.paymentProofName || "NULL"}",
  "owner": "carlos.m@zambeze.edu.mz"
}`}
              </pre>
            </div>
          </div>
        </section>

      </main>

      {/* LUXURY STATUS FOOTER */}
      <footer className="border-t border-gray-dark p-4 bg-zinc-950 text-center text-xs text-zinc-500 font-mono">
        SuitUP Applet Demo • Desenvolvido em React / Vite com suporte para múltiplos ecrãs e cache persistente.
      </footer>
    </div>
  );
}
