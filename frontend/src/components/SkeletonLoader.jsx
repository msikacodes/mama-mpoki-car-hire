export function SkeletonRow({ cols = 5 }) {
  return (
    <tr className="skeleton-row">
      {Array.from({ length: cols }).map((_, i) => (
        <td key={i}>
          <div className="skeleton skeleton-text" />
        </td>
      ))}
    </tr>
  );
}

export function SkeletonTable({ rows = 5, cols = 5 }) {
  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            {Array.from({ length: cols }).map((_, i) => (
              <th key={i}><div className="skeleton skeleton-th" /></th>
            ))}
          </tr>
        </thead>
        <tbody>
          {Array.from({ length: rows }).map((_, i) => (
            <SkeletonRow key={i} cols={cols} />
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function SkeletonCard() {
  return (
    <div className="stat-card">
      <div className="skeleton skeleton-icon" />
      <div className="skeleton skeleton-label" />
      <div className="skeleton skeleton-value" />
      <div className="skeleton skeleton-sub" />
    </div>
  );
}

export function SkeletonStatGrid({ count = 4 }) {
  return (
    <div className="stat-grid">
      {Array.from({ length: count }).map((_, i) => (
        <SkeletonCard key={i} />
      ))}
    </div>
  );
}

export function SkeletonPage({ title = true, table = true, rows = 5, cols = 5 }) {
  return (
    <div>
      {title && (
        <div style={{ marginBottom: 20 }}>
          <div className="skeleton skeleton-title" />
          <div className="skeleton skeleton-subtitle" style={{ marginTop: 8 }} />
        </div>
      )}
      {table && (
        <div className="card">
          <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--slate-100)' }}>
            <div className="skeleton skeleton-search" />
          </div>
          <SkeletonTable rows={rows} cols={cols} />
        </div>
      )}
    </div>
  );
}
