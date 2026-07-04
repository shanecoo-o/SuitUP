import React, { useState } from "react";
import { SuitConfiguration } from "../types";
import { FABRICS } from "../data";
import { Maximize, RotateCw, Sun, Layout, Eye, Camera, ZoomIn } from "lucide-react";

interface Mannequin3DProps {
  config: SuitConfiguration;
}

type StudioLight = "spot" | "ambient" | "natural";
type StudioBackground = "showroom" | "concrete" | "palace" | "dark";

export const Mannequin3D: React.FC<Mannequin3DProps> = ({ config }) => {
  const [rotation, setRotation] = useState<number>(0); // 0 to 360 degrees
  const [light, setLight] = useState<StudioLight>("ambient");
  const [background, setBackground] = useState<StudioBackground>("concrete");
  const [zoomLevel, setZoomLevel] = useState<number>(1);

  const selectedFabric = FABRICS.find(f => f.id === config.fabricId) || FABRICS[0];
  const suitColor = selectedFabric.colorHex;

  // Render a responsive 3D model styled according to the parameters
  const getBackgroundClass = () => {
    switch (background) {
      case "concrete":
        return "bg-gradient-to-tr from-stone-900 via-zinc-800 to-stone-900";
      case "showroom":
        return "bg-gradient-to-tr from-neutral-900 via-neutral-950 to-amber-950/20";
      case "palace":
        return "bg-gradient-to-tr from-[#1E1B18] via-[#2F2923] to-[#1E1B18]";
      case "dark":
        return "bg-zinc-950";
    }
  };

  const getLightOverlayStyle = () => {
    switch (light) {
      case "spot":
        return "radial-gradient(circle at 50% 10%, rgba(200, 169, 106, 0.45) 0%, rgba(0,0,0,0) 70%)";
      case "natural":
        return "linear-gradient(135deg, rgba(255,255,255,0.15) 0%, rgba(0,0,0,0) 50%)";
      default:
        return "radial-gradient(circle at 10% 20%, rgba(255,255,255,0.1) 0%, rgba(0,0,0,0) 100%)";
    }
  };

  // Convert rotation to descriptive angle names
  const getAngleLabel = () => {
    const r = rotation % 360;
    if (r >= 337.5 || r < 22.5) return "Vista Frontal (0°)";
    if (r >= 22.5 && r < 67.5) return "Semi-Lateral Esquerda (45°)";
    if (r >= 67.5 && r < 112.5) return "Perfil Esquerdo (90°)";
    if (r >= 112.5 && r < 157.5) return "Fração Traseira Esquerda (135°)";
    if (r >= 157.5 && r < 202.5) return "Vista de Costas (180°)";
    if (r >= 202.5 && r < 247.5) return "Fração Traseira Direita (225°)";
    if (r >= 247.5 && r < 292.5) return "Perfil Direito (270°)";
    return "Semi-Lateral Direita (315°)";
  };

  return (
    <div className="relative w-full flex flex-col items-center bg-black-deep border border-gray-dark rounded-xl p-4 overflow-hidden shadow-2xl">
      {/* 3D Header Controls */}
      <div className="w-full flex justify-between items-center border-b border-gray-dark pb-3 mb-4">
        <div>
          <span className="section-label text-[9px] mb-0.5 flex items-center gap-1.5">
            <span className="w-1.5 h-1.5 rounded-full bg-gold-premium animate-pulse" />
            Simulador 3D Premium
          </span>
          <h4 className="text-gold-premium text-base font-serif italic font-black">Manequim Digital Bespoke</h4>
        </div>
        <div className="text-[10px] font-mono font-bold tracking-wider uppercase text-gray-400 bg-zinc-900 border border-gray-dark px-3 py-1 rounded-md">
          {getAngleLabel()}
        </div>
      </div>

      {/* Main 3D Stage Canvas */}
      <div className={`relative w-full h-[320px] rounded-lg transition-all overflow-hidden ${getBackgroundClass()}`}>
        {/* Customized lighting shadow overlay */}
        <div className="absolute inset-0 pointer-events-none" style={{ backgroundImage: getLightOverlayStyle() }} />

        {/* Studio spotlight glow background */}
        {light === "spot" && (
          <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[220px] h-[220px] rounded-full bg-gold-premium/5 blur-[80px] pointer-events-none" />
        )}

        {/* 3D Mannequin Body Representation */}
        <div
          className="absolute inset-0 flex items-center justify-center transition-transform duration-300"
          style={{ transform: `scale(${zoomLevel})` }}
        >
          <svg
            viewBox="0 0 200 240"
            className="w-auto h-full max-h-[280px]"
            style={{
              transform: `rotateY(${rotation}deg)`,
              transition: "transform 0.1s ease"
            }}
          >
            <defs>
              <linearGradient id="mannequinBody" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0%" stopColor="#404040" />
                <stop offset="50%" stopColor="#1C1C1E" />
                <stop offset="100%" stopColor="#0B0B0C" />
              </linearGradient>
              <linearGradient id="fabricLit" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" stopColor={suitColor} />
                <stop offset="50%" stopColor={lightenColor(suitColor, 15)} />
                <stop offset="100%" stopColor={lightenColor(suitColor, -15)} />
              </linearGradient>
            </defs>

            {/* Mannequin stand bar */}
            <rect x="98.5" y="195" width="3" height="40" fill="#2d2d2d" />
            <ellipse cx="100" cy="235" rx="36" ry="5" fill="#1f1f1f" stroke="#c8a96a33" strokeWidth="1" />

            {/* Mannequin neck block */}
            <ellipse cx="100" cy="55" rx="10" ry="4" fill="#C8A96A" />
            <path d="M 94 56 Q 100 70 106 56 Z" fill="#C8A96A" />

            {/* Rendering model based on rotation ranges */}
            {rotation >= 110 && rotation <= 250 ? (
              /* BACK VIEW OF SUIT */
              <g id="suit-back">
                {/* Torso back */}
                <path
                  d="M 54 85 Q 100 80 146 85 L 150 170 Q 146 205 138 210 L 62 210 Q 54 205 50 170 Z"
                  fill="url(#fabricLit)"
                  stroke="#111"
                />
                {/* Vent stitches */}
                <line x1="100" y1="140" x2="100" y2="210" stroke="#111" strokeWidth="1.5" strokeDasharray="2,2" />
                {/* Back collar strip */}
                <path d="M 85 85 Q 100 90 115 85 L 120 78 Q 100 82 80 78 Z" fill="#1C1C1C" />
                {/* Sleeves back perspective */}
                <path d="M 54 85 Q 40 140 44 210 L 52 210 M 146 85 Q 160 140 156 210 L 148 210" stroke="url(#fabricLit)" strokeWidth="11" fill="none" strokeLinecap="round" />
              </g>
            ) : (
              /* FRONT / PROFILE VIEW */
              <g id="suit-front-3d">
                {/* Main front jacket block */}
                <path
                  d="M 54 85 Q 100 80 146 85 L 150 170 Q 146 205 138 210 L 62 210 Q 54 205 50 170 Z"
                  fill="url(#fabricLit)"
                  stroke="#111"
                  strokeWidth="1"
                />

                {/* Left/Right lapel visual projection adjusted by rotation for 3D feel */}
                <path d="M 100 82 L 82 122 L 68 118 L 92 170 L 100 170 Z" fill="url(#fabricLit)" stroke="#111" />
                <path d="M 100 82 L 118 122 L 132 118 L 108 170 L 100 170 Z" fill="url(#fabricLit)" stroke="#111" />

                {/* V neck cutout */}
                <polygon points="100,82 86,128 114,128" fill="#F8FAFC" />
                <path d="M100,128 L98,142 L100,170 L102,142 Z" fill="#991B1B" /> {/* Premium Tie */}

                {/* Dynamic Sleeves front */}
                <path d="M 54 85 Q 38 140 42 210 L 52 210" stroke="url(#fabricLit)" strokeWidth="12" fill="none" strokeLinecap="round" />
                <path d="M 146 85 Q 162 140 158 210 L 148 210" stroke="url(#fabricLit)" strokeWidth="12" fill="none" strokeLinecap="round" />

                {/* Shadow contours to give depth */}
                <path d="M 100 170 L 100 210" stroke="rgba(0,0,0,0.5)" strokeWidth="1.5" />
                {/* Button */}
                <circle cx="100" cy="173" r="3.5" fill="#111" stroke="#C8A96A" strokeWidth="0.8" />
              </g>
            )}

            {/* Mannequin header label */}
            <text x="100" y="24" fill="#A1A1AA" fontSize="6" textAnchor="middle" letterSpacing="2" fontFamily="Inter">MANNEQUIN</text>
          </svg>
        </div>

        {/* 3D Hud Controls inside stage */}
        <div className="absolute left-3 top-3 flex flex-col gap-2">
          {/* Light toggle button */}
          <button
            onClick={() => {
              const lights: StudioLight[] = ["ambient", "spot", "natural"];
              const idx = lights.indexOf(light);
              setLight(lights[(idx + 1) % lights.length]);
            }}
            className="p-1.5 rounded-lg bg-zinc-900/90 border border-gray-dark hover:border-gold-premium/40 text-gray-300 hover:text-gold-premium transition-all backdrop-blur flex items-center gap-1 text-xs"
            title="Mudar Iluminação"
          >
            <Sun className="w-3.5 h-3.5 text-gold-premium" />
            <span className="capitalize hidden md:inline">{light}</span>
          </button>

          {/* Background switcher */}
          <button
            onClick={() => {
              const bgStyles: StudioBackground[] = ["concrete", "showroom", "palace", "dark"];
              const idx = bgStyles.indexOf(background);
              setBackground(bgStyles[(idx + 1) % bgStyles.length]);
            }}
            className="p-1.5 rounded-lg bg-zinc-900/90 border border-gray-dark hover:border-gold-premium/40 text-gray-300 hover:text-gold-premium transition-all backdrop-blur flex items-center gap-1 text-xs"
            title="Mudar Cenário"
          >
            <Layout className="w-3.5 h-3.5 text-gold-premium" />
            <span className="capitalize hidden md:inline">{background}</span>
          </button>
        </div>

        {/* Zoom adjustment on screen */}
        <div className="absolute right-3 top-3">
          <button
            onClick={() => setZoomLevel(zoomLevel === 1 ? 1.25 : 1)}
            className="p-1.5 rounded-lg bg-zinc-900/90 border border-gray-dark hover:border-gold-premium/40 text-gray-300 hover:text-gold-premium transition-all backdrop-blur text-xs flex items-center gap-1"
          >
            <ZoomIn className="w-3.5 h-3.5" />
            <span>{zoomLevel === 1 ? "Aproximar" : "Afastar"}</span>
          </button>
        </div>

        {/* Overlay instructions to drag or play */}
        <div className="absolute bottom-3 left-3 bg-black/75 border border-gray-dark text-[10px] text-gray-400 font-mono py-1 px-2.5 rounded backdrop-blur">
          Arraste o slider para girar 360°
        </div>
      </div>

      {/* 360 degree Rotation Controller */}
      <div className="w-full mt-4 flex items-center gap-3 bg-zinc-900/40 border border-gray-dark rounded-lg p-3">
        <RotateCw className="w-4 h-4 text-gold-premium animate-spin-slow" />
        <div className="flex-1 flex flex-col gap-1">
          <div className="flex justify-between text-xs font-mono text-gray-400">
            <span>Rotação 3D</span>
            <span className="text-gold-premium">{rotation}°</span>
          </div>
          <input
            type="range"
            min="0"
            max="360"
            value={rotation}
            onChange={(e) => setRotation(parseInt(e.target.value))}
            className="w-full accent-gold-premium cursor-pointer bg-zinc-950 rounded-lg height-2"
          />
        </div>
      </div>

      <div className="w-full flex justify-around mt-3 border-t border-gray-dark pt-3">
        {[{ label: "Frente", rot: 0 }, { label: "Perfil", rot: 90 }, { label: "Trás", rot: 180 }, { label: "3/4", rot: 45 }].map((view) => (
          <button
            key={view.label}
            onClick={() => setRotation(view.rot)}
            className={`text-xs font-serif italic py-1 px-2.5 rounded border transition-all ${
              rotation === view.rot
                ? "bg-gold-premium text-black-deep border-gold-premium font-semibold"
                : "bg-transparent border-gray-dark hover:border-gold-premium/40 text-gray-400 hover:text-white"
            }`}
          >
            {view.label}
          </button>
        ))}
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
