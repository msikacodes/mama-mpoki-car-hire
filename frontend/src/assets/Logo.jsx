export default function Logo({ size = 38, className = '' }) {
  return (
    <svg width={size} height={size} viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
      <defs>
        <linearGradient id="logoBg" x1="0" y1="0" x2="48" y2="48">
          <stop offset="0%" stopColor="#2563eb"/>
          <stop offset="100%" stopColor="#1e40af"/>
        </linearGradient>
        <linearGradient id="logoCar" x1="10" y1="16" x2="38" y2="36">
          <stop offset="0%" stopColor="#ffffff"/>
          <stop offset="100%" stopColor="#e0e7ff"/>
        </linearGradient>
      </defs>
      <rect width="48" height="48" rx="12" fill="url(#logoBg)"/>
      <g transform="translate(6, 10)">
        {/* Car body */}
        <path d="M4 22 L8 12 L16 12 L20 8 L32 8 L36 12 L40 12 L40 22 L36 22 L36 24 L34 26 L14 26 L12 24 L4 24 Z" fill="url(#logoCar)" opacity="0.95"/>
        {/* Windows */}
        <path d="M10 12 L14 8 L26 8 L30 12 Z" fill="#2563eb" opacity="0.25"/>
        <line x1="20" y1="8" x2="20" y2="12" stroke="#2563eb" strokeWidth="0.5" opacity="0.25"/>
        {/* Front wheel */}
        <circle cx="12" cy="26" r="4" fill="#1e293b"/>
        <circle cx="12" cy="26" r="2" fill="#475569"/>
        <circle cx="12" cy="26" r="0.8" fill="#94a3b8"/>
        {/* Rear wheel */}
        <circle cx="36" cy="26" r="4" fill="#1e293b"/>
        <circle cx="36" cy="26" r="2" fill="#475569"/>
        <circle cx="36" cy="26" r="0.8" fill="#94a3b8"/>
        {/* Headlight */}
        <rect x="38" y="14" width="3" height="2" rx="1" fill="#fbbf24" opacity="0.9"/>
        {/* Tail light */}
        <rect x="3" y="14" width="3" height="2" rx="1" fill="#ef4444" opacity="0.9"/>
        {/* Roof rack */}
        <line x1="14" y1="7" x2="28" y2="7" stroke="#ffffff" strokeWidth="1" strokeLinecap="round" opacity="0.4"/>
      </g>
    </svg>
  );
}
