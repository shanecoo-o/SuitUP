import React, { useState } from "react";
import { SuitConfiguration, LapelType, ButtonType, PocketType, LiningType } from "../types";
import { FABRICS } from "../data";
import { Info, Sparkles, ZoomIn, ZoomOut } from "lucide-react";

interface SuitVisualizerProps {
  config: SuitConfiguration;
  selectedFabricName: string;
  onChangeConfig?: (config: Partial<SuitConfiguration>) => void;
  interactive?: boolean;
}

export const SuitVisualizer: React.FC<SuitVisualizerProps> = ({
  config,
  selectedFabricName,
  onChangeConfig,
  interactive = true
}) => {
  const [zoom, setZoom] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<"front" | "lining">("front");
  const [hoveredSection, setHoveredSection] = useState<string | null>(null);

  const selectedFabric = FABRICS.find(f => f.id === config.fabricId) || FABRICS[0];
  const suitColor = selectedFabric.colorHex;

  // Let's define the paths and coordinates for high quality vector blazer rendering.
  // We'll draw beautiful geometric lines that adapt nicely to selections.
  return (
    <div className="relative w-full flex flex-col items-center bg-black-deep border border-gray-dark rounded-xl p-4 overflow-hidden shadow-2xl">
      {/* Header Info Banner */}
      <div className="w-full flex justify-between items-center border-b border-gray-dark pb-3 mb-4">
        <div>
          <span className="section-label text-[9px] mb-0.5 text-zinc-550">Editor Interativo 2D</span>
          <h4 className="text-gold-premium text-base font-serif italic font-black">Configurando: {config.model}</h4>
        </div>
        <div className="flex bg-zinc-900 border border-gray-dark rounded-lg p-0.5 text-xs font-mono">
          <button
            onClick={() => setActiveTab("front")}
            className={`px-3 py-1 rounded transition-all tracking-wider uppercase text-[10px] font-bold ${
              activeTab === "front"
                ? "bg-gold-premium text-black-deep font-black"
                : "text-gray-400 hover:text-white"
            }`}
          >
            Exterior
          </button>
          <button
            onClick={() => setActiveTab("lining")}
            className={`px-3 py-1 rounded transition-all tracking-wider uppercase text-[10px] font-bold ${
              activeTab === "lining"
                ? "bg-gold-premium text-black-deep font-black"
                : "text-gray-400 hover:text-white"
            }`}
          >
            Forro
          </button>
        </div>
      </div>

      {/* Main Canvas Container */}
      <div
        className={`relative w-full transition-all duration-500 flex items-center justify-center bg-gradient-to-b from-zinc-900/60 to-black-deep rounded-lg ${
          zoom ? "scale-105 h-[360px]" : "h-[320px]"
        }`}
        style={{ contentVisibility: "auto" }}
      >
        {/* Subtle grid pattern for precision bespoke feel */}
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#1f29370f_1px,transparent_1px),linear-gradient(to_bottom,#1f29370f_1px,transparent_1px)] bg-[size:14px_24px] pointer-events-none" />

        {/* Vector SVG Model */}
        <svg
          viewBox="0 0 240 280"
          className="w-auto h-full max-h-[300px] drop-shadow-[0_20px_40px_rgba(0,0,0,0.85)]"
        >
          {/* Defs block for gradients and shadows */}
          <defs>
            <radialGradient id="shadow" cx="50%" cy="50%" r="50%">
              <stop offset="0%" stopColor="#000000" stopOpacity="0.8" />
              <stop offset="100%" stopColor="#000000" stopOpacity="0" />
            </radialGradient>
            <linearGradient id="fabricGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor={suitColor} />
              <stop offset="50%" stopColor={suitColor} />
              <stop offset="100%" stopColor={lightenColor(suitColor, -20)} />
            </linearGradient>
            
            {/* Lining pattern representation */}
            <pattern id="paisleyPattern" x="0" y="0" width="20" height="20" patternUnits="userSpaceOnUse">
              <rect width="20" height="20" fill="#4a0e17" />
              <path d="M5 5 C 5 2, 8 2, 8 5 C 8 8, 3 10, 5 15 C 6 17, 10 18, 12 15" fill="none" stroke="#C8A96A" strokeWidth="0.8" />
              <circle cx="15" cy="8" r="1.5" fill="#C8A96A" />
            </pattern>
            <pattern id="stripePattern" x="0" y="0" width="10" height="20" patternUnits="userSpaceOnUse">
              <rect width="10" height="20" fill="#2d2215" />
              <line x1="2" y1="0" x2="2" y2="20" stroke="#C8A96A" strokeWidth="0.8" />
              <line x1="8" y1="0" x2="8" y2="20" stroke="#c8a96a33" strokeWidth="0.4" />
            </pattern>
          </defs>

          {/* Under shadow */}
          <ellipse cx="120" cy="270" rx="70" ry="8" fill="url(#shadow)" />

          {activeTab === "front" ? (
            /* Exterior view */
            <g id="blazer-front">
              {/* Shoulders and Main Torso Body */}
              <path
                id="suit-body"
                className="transition-all duration-300"
                d="M 50 80 
                   Q 80 75 120 78 
                   Q 160 75 190 80
                   L 200 160
                   Q 195 240 185 260
                   L 55 260
                   Q 45 240 40 160 Z"
                fill="url(#fabricGrad)"
                stroke="#1c1917"
                strokeWidth="1.5"
                onMouseEnter={() => setHoveredSection("corpo")}
                onMouseLeave={() => setHoveredSection(null)}
              />

              {/* Lapels representation - changes depending on config */}
              {config.lapel === LapelType.NOTCH && (
                <g id="notch-lapel" className="transition-all duration-300">
                  {/* Left notch lapel */}
                  <path
                    d="M 120 78 L 90 120 L 72 118 L 102 180 L 120 180 Z"
                    fill="url(#fabricGrad)"
                    stroke={hoveredSection === "lapela" ? "#C8A96A" : "#1c1917"}
                    strokeWidth={hoveredSection === "lapela" ? "2" : "1"}
                    className="cursor-pointer"
                    onClick={() => onChangeConfig?.({ lapel: LapelType.PEAK })}
                    onMouseEnter={() => setHoveredSection("lapela")}
                    onMouseLeave={() => setHoveredSection(null)}
                  />
                  {/* Right notch lapel */}
                  <path
                    d="M 120 78 L 150 120 L 168 118 L 138 180 L 120 180 Z"
                    fill="url(#fabricGrad)"
                    stroke={hoveredSection === "lapela" ? "#C8A96A" : "#1c1917"}
                    strokeWidth={hoveredSection === "lapela" ? "2" : "1"}
                    className="cursor-pointer"
                    onClick={() => onChangeConfig?.({ lapel: LapelType.PEAK })}
                    onMouseEnter={() => setHoveredSection("lapela")}
                    onMouseLeave={() => setHoveredSection(null)}
                  />
                </g>
              )}

              {config.lapel === LapelType.PEAK && (
                <g id="peak-lapel" className="transition-all duration-300">
                  {/* Left peak lapel (sharp upward point) */}
                  <path
                    d="M 120 78 L 84 110 L 64 96 L 100 180 L 120 180 Z"
                    fill="url(#fabricGrad)"
                    stroke={hoveredSection === "lapela" ? "#C8A96A" : "#1c1917"}
                    strokeWidth={hoveredSection === "lapela" ? "2" : "1"}
                    className="cursor-pointer"
                    onClick={() => onChangeConfig?.({ lapel: LapelType.SHAWL })}
                    onMouseEnter={() => setHoveredSection("lapela")}
                    onMouseLeave={() => setHoveredSection(null)}
                  />
                  {/* Right peak lapel */}
                  <path
                    d="M 120 78 L 156 110 L 176 96 L 140 180 L 120 180 Z"
                    fill="url(#fabricGrad)"
                    stroke={hoveredSection === "lapela" ? "#C8A96A" : "#1c1917"}
                    strokeWidth={hoveredSection === "lapela" ? "2" : "1"}
                    className="cursor-pointer"
                    onClick={() => onChangeConfig?.({ lapel: LapelType.SHAWL })}
                    onMouseEnter={() => setHoveredSection("lapela")}
                    onMouseLeave={() => setHoveredSection(null)}
                  />
                </g>
              )}

              {config.lapel === LapelType.SHAWL && (
                <g id="shawl-lapel" className="transition-all duration-300">
                  {/* Left shawl lapel (smooth continuous curve) */}
                  <path
                    d="M 120 78 C 88 100 78 125 100 180 L 120 180 Z"
                    fill="url(#fabricGrad)"
                    stroke={hoveredSection === "lapela" ? "#C8A96A" : "#1c1917"}
                    strokeWidth={hoveredSection === "lapela" ? "2" : "1"}
                    className="cursor-pointer"
                    onClick={() => onChangeConfig?.({ lapel: LapelType.NOTCH })}
                    onMouseEnter={() => setHoveredSection("lapela")}
                    onMouseLeave={() => setHoveredSection(null)}
                  />
                  {/* Right shawl lapel */}
                  <path
                    d="M 120 78 C 152 100 162 125 140 180 L 120 180 Z"
                    fill="url(#fabricGrad)"
                    stroke={hoveredSection === "lapela" ? "#C8A96A" : "#1c1917"}
                    strokeWidth={hoveredSection === "lapela" ? "2" : "1"}
                    className="cursor-pointer"
                    onClick={() => onChangeConfig?.({ lapel: LapelType.NOTCH })}
                    onMouseEnter={() => setHoveredSection("lapela")}
                    onMouseLeave={() => setHoveredSection(null)}
                  />
                </g>
              )}

              {/* V-Neck opening shirt & subtle necktie */}
              <g id="shirt-and-tie" className="transition-all duration-300">
                <polygon points="120,78 102,126 138,126" fill="#F8FAFC" />
                <path d="M 120 126 L 115 145 L 120 180 L 125 145 Z" fill="#991B1B" /> {/* Red Tie */}
                <path d="M 112 105 L 120 114 L 128 105 L 120 98 Z" fill="#E2E8F0" stroke="#CBD5E1" /> {/* Collar Folded */}
              </g>

              {/* Sleeves */}
              <g id="sleeves" className="transition-all duration-300">
                {/* Left Sleeve */}
                <path
                  d="M 50 80 Q 30 140 34 230 L 48 232 L 46 160 L 53 115 Z"
                  fill="url(#fabricGrad)"
                  stroke="#1c1917"
                  strokeWidth="1.2"
                />
                {/* Right Sleeve */}
                <path
                  d="M 190 80 Q 210 140 206 230 L 192 232 L 194 160 L 187 115 Z"
                  fill="url(#fabricGrad)"
                  stroke="#1c1917"
                  strokeWidth="1.2"
                />
              </g>

              {/* Decorative Breast Pocket + Handkerchief */}
              <g id="breast-pocket" className="cursor-pointer" onMouseEnter={() => setHoveredSection("pocket-breast")} onMouseLeave={() => setHoveredSection(null)}>
                <polygon points="75,115 90,113 88,123 73,125" fill={lightenColor(suitColor, -15)} stroke="#272522" strokeWidth="0.8" />
                {/* Golden/White Pocket Square */}
                <polygon points="78,114 81,105 84,113" fill="#C8A96A" />
                <polygon points="81,113 84,103 87,112" fill="#FFFFFF" />
              </g>

              {/* Pockets Setup - Flap vs Welt vs Patch */}
              {config.pockets === PocketType.FLAP && (
                <g id="flap-pockets" className="transition-all duration-300 cursor-pointer" onMouseEnter={() => setHoveredSection("bolsos")} onMouseLeave={() => setHoveredSection(null)}>
                  {/* Left Flap */}
                  <rect x="58" y="195" width="28" height="10" rx="2" fill={lightenColor(suitColor, -10)} stroke={hoveredSection === "bolsos" ? "#C8A96A" : "#191715"} strokeWidth="1" />
                  <line x1="58" y1="195" x2="86" y2="195" stroke="#000000" strokeWidth="1" />
                  {/* Right Flap */}
                  <rect x="154" y="195" width="28" height="10" rx="2" fill={lightenColor(suitColor, -10)} stroke={hoveredSection === "bolsos" ? "#C8A96A" : "#191715"} strokeWidth="1" />
                  <line x1="154" y1="195" x2="182" y2="195" stroke="#000000" strokeWidth="1" />
                </g>
              )}

              {config.pockets === PocketType.WELT && (
                <g id="welt-pockets" className="transition-all duration-300 cursor-pointer" onMouseEnter={() => setHoveredSection("bolsos")} onMouseLeave={() => setHoveredSection(null)}>
                  {/* Left Welt slit */}
                  <rect x="58" y="195" width="28" height="4" rx="1" fill="#1C1917" stroke={hoveredSection === "bolsos" ? "#C8A96A" : "#3F3F46"} strokeWidth="0.8" />
                  {/* Right Welt slit */}
                  <rect x="154" y="195" width="28" height="4" rx="1" fill="#1C1917" stroke={hoveredSection === "bolsos" ? "#C8A96A" : "#3F3F46"} strokeWidth="0.8" />
                </g>
              )}

              {config.pockets === PocketType.PATCH && (
                <g id="patch-pockets" className="transition-all duration-300 cursor-pointer" onMouseEnter={() => setHoveredSection("bolsos")} onMouseLeave={() => setHoveredSection(null)}>
                  {/* Rounded bottom shield pockets */}
                  <path d="M 56 193 L 88 193 L 86 215 Q 84 225 72 225 Q 60 225 58 215 Z" fill={lightenColor(suitColor, -15)} stroke={hoveredSection === "bolsos" ? "#C8A96A" : "#1C1917"} strokeWidth="1" />
                  <path d="M 152 193 L 184 193 L 182 215 Q 180 225 168 225 Q 156 225 154 215 Z" fill={lightenColor(suitColor, -15)} stroke={hoveredSection === "bolsos" ? "#C8A96A" : "#1C1917"} strokeWidth="1" />
                </g>
              )}

              {/* Buttons Representation */}
              <g id="suit-buttons" className="transition-all duration-300 cursor-pointer" onMouseEnter={() => setHoveredSection("botões")} onMouseLeave={() => setHoveredSection(null)}>
                {config.buttons === ButtonType.ONE && (
                  <circle cx="120" cy="188" r="3.5" fill="#18181B" stroke="#C8A96A" strokeWidth={hoveredSection === "botões" ? "1.5" : "0.5"} />
                )}

                {config.buttons === ButtonType.TWO && (
                  <>
                    <circle cx="120" cy="180" r="3" fill="#18181B" stroke="#C8A96A" strokeWidth={hoveredSection === "botões" ? "1.5" : "0.5"} />
                    <circle cx="120" cy="198" r="3" fill="#18181B" stroke="#C8A96A" strokeWidth={hoveredSection === "botões" ? "1.5" : "0.5"} />
                  </>
                )}

                {(config.buttons === ButtonType.DOUBLE_4 || config.buttons === ButtonType.DOUBLE_6) && (
                  <g id="double-breasted-grid">
                    {/* Column Left */}
                    <circle cx="110" cy="178" r="3" fill="#18181B" stroke="#C8A96A" strokeWidth="0.5" />
                    <circle cx="110" cy="194" r="3" fill="#18181B" stroke="#C8A96A" strokeWidth="0.5" />
                    {/* Column Right */}
                    <circle cx="130" cy="178" r="3" fill="#18181B" stroke="#C8A96A" strokeWidth="0.5" />
                    <circle cx="130" cy="194" r="3" fill="#18181B" stroke="#C8A96A" strokeWidth="0.5" />
                    
                    {config.buttons === ButtonType.DOUBLE_6 && (
                      <>
                        <circle cx="110" cy="162" r="3.5" fill="#111" stroke="#C8A96A" strokeWidth="1" />
                        <circle cx="130" cy="162" r="3.5" fill="#111" stroke="#C8A96A" strokeWidth="1" />
                      </>
                    )}
                  </g>
                )}
              </g>

              {/* Sleek Stitch lines / Highlights */}
              <path d="M 52 82 Q 80 84 120 84" stroke="rgba(200, 169, 106, 0.2)" strokeWidth="0.8" fill="none" />
              <path d="M 120 180 L 120 260" stroke="#101012" strokeWidth="1" />
            </g>
          ) : (
            /* Lining Inside View */
            <g id="blazer-lining" className="animate-fade-in">
              {/* Suit Inner Silhouette */}
              <path
                d="M 50 80 Q 80 75 120 78 Q 160 75 190 80 L 200 160 L 185 260 L 55 260 L 40 160 Z"
                fill={getLiningFill(config.lining)}
                stroke="#451a03"
                strokeWidth="1.5"
              />

              {/* Left exterior lapel flap folded back to show interior cut */}
              <path d="M 50 80 L 102 180 L 55 260 Z" fill="url(#fabricGrad)" opacity="0.4" />
              <path d="M 190 80 L 138 180 L 185 260 Z" fill="url(#fabricGrad)" opacity="0.4" />

              {/* Bespoke Tailor label printed on internal left chest pocket */}
              <g id="tailor-label" className="drop-shadow-md">
                <rect x="65" y="140" width="35" height="20" rx="1.5" fill="#1C1917" stroke="#C8A96A" strokeWidth="0.8" />
                <text x="82.5" y="150" fill="#C8A96A" fontSize="4.5" textAnchor="middle" fontFamily="Playfair Display" fontStyle="italic">SuitUP</text>
                <text x="82.5" y="156" fill="#A1A1AA" fontSize="3" textAnchor="middle" letterSpacing="0.2">MAPUTO</text>
              </g>

              {/* Internal passport pocket right side */}
              <path d="M 140 145 L 165 145 L 165 147 L 140 147 Z" fill="#27272A" />
              <rect x="141" y="148" width="23" height="15" fill={lightenColor(suitColor, -15)} opacity="0.6" stroke="#444" strokeWidth="0.5" />
            </g>
          )}
        </svg>

        {/* Hover Highlight Tooltips overlay */}
        {interactive && hoveredSection && (
          <div className="absolute bottom-4 left-4 bg-zinc-950/95 border border-gold-premium/40 px-3 py-1.5 rounded-lg text-xs flex items-center gap-1.5 backdrop-blur shadow-xl animate-fade-in">
            <Sparkles className="w-3.5 h-3.5 text-gold-premium animate-pulse" />
            <span className="text-gray-300">Parte:</span>
            <span className="text-gold-premium font-medium capitalize font-mono">{hoveredSection}</span>
          </div>
        )}

        {/* Zoom controls */}
        <div className="absolute right-3 bottom-3 flex flex-col gap-1.5">
          <button
            onClick={() => setZoom(!zoom)}
            className="p-1.5 rounded-lg bg-zinc-900/90 border border-gray-dark hover:border-gold-premium/40 text-gray-300 hover:text-gold-premium transition-all backdrop-blur"
            title="Toggle Zoom"
          >
            {zoom ? <ZoomOut className="w-4 h-4" /> : <ZoomIn className="w-4 h-4" />}
          </button>
        </div>
      </div>

      <div className="w-full text-center mt-2">
        <p className="text-xs text-gray-400 font-sans">
          Toque para alternar • Paleta: <span className="text-gold-premium">{selectedFabricName}</span>
        </p>
      </div>
    </div>
  );
};

// Utilities for colors in pure canvas JS
function lightenColor(col: string, amt: number): string {
  let useHash = false;
  if (col[0] === "#") {
    col = col.slice(1);
    useHash = true;
  }
  let num = parseInt(col, 16);
  let r = (num >> 16) + amt;
  if (r > 255) r = 255;
  else if (r < 0) r = 0;
  let b = ((num >> 8) & 0x00ff) + amt;
  if (b > 255) b = 255;
  else if (b < 0) b = 0;
  let g = (num & 0x0000ff) + amt;
  if (g > 255) g = 255;
  else if (g < 0) g = 0;
  return (useHash ? "#" : "") + ((g | (b << 8) | (r << 16)).toString(16)).padStart(6, "0");
}

function getLiningFill(type: LiningType): string {
  switch (type) {
    case LiningType.PAISLEY:
      return "url(#paisleyPattern)";
    case LiningType.STRIPE:
      return "url(#stripePattern)";
    case LiningType.CRIMSON:
      return "#7F1D1D"; // Rich crimson red
    default:
      return "#3F3F46"; // Clean slate gray satin
  }
}
