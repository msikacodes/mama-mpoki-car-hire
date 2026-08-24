import { useState, useEffect } from 'react';
import { getDashboard } from '../api/client';
import {
  Car, AlertTriangle, TrendingUp, TrendingDown, Bus, Route,
  DollarSign, BarChart3, CheckCircle, Clock, Activity, Users,
  ArrowUpRight, ArrowDownRight
} from 'lucide-react';

const fmt = (n) => new Intl.NumberFormat('en-TZ').format(n || 0);

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDashboard().then(res => setData(res.data)).catch(console.error).finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '60vh' }}>
      <div style={{ textAlign: 'center', color: 'var(--slate-400)' }}>
        <Activity size={32} style={{ marginBottom: 12, opacity: 0.4 }} />
        <p>Loading dashboard...</p>
      </div>
    </div>
  );
  if (!data) return <div className="empty"><h4>Failed to load dashboard</h4><p>Please try again later.</p></div>;

  const { fleet, specialHire, daladala, alerts } = data;

  return (
    <div>
      {/* Fleet Stats */}
      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-card-icon blue"><Car size={20} /></div>
          <div className="stat-card-label">Total Vehicles</div>
          <div className="stat-card-value">{fleet?.totalVehicles || 0}</div>
          <div className="stat-card-sub">{fleet?.activeVehicles || 0} active now</div>
        </div>
        <div className="stat-card">
          <div className="stat-card-icon green"><TrendingUp size={20} /></div>
          <div className="stat-card-label">Special Hire</div>
          <div className="stat-card-value">{fleet?.specialHire || 0}</div>
          <div className="stat-card-sub">Coasters & minibuses</div>
        </div>
        <div className="stat-card">
          <div className="stat-card-icon amber"><Bus size={20} /></div>
          <div className="stat-card-label">Daladala Fleet</div>
          <div className="stat-card-value">{fleet?.daladala || 0}</div>
          <div className="stat-card-sub">{daladala?.totalRoutes || 0} routes</div>
        </div>
        <div className="stat-card">
          <div className="stat-card-icon red"><Users size={20} /></div>
          <div className="stat-card-label">Private Cars</div>
          <div className="stat-card-value">{fleet?.privateCars || 0}</div>
          <div className="stat-card-sub">Owner's vehicles</div>
        </div>
      </div>

      {/* Modules Overview */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 24 }}>
        {/* Special Hire Card */}
        <div className="card">
          <div className="card-header">
            <div>
              <h3><BarChart3 size={16} /> Special Hire Overview</h3>
              <p>Booking and trip performance</p>
            </div>
          </div>
          <div className="card-body">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 16 }}>
              <div style={{ padding: 12, background: 'var(--slate-50)', borderRadius: 'var(--radius)' }}>
                <div style={{ fontSize: 11, color: 'var(--slate-500)', fontWeight: 600 }}>PENDING</div>
                <div style={{ fontSize: 20, fontWeight: 700, color: 'var(--amber-600)' }}>{specialHire?.pendingBookings || 0}</div>
              </div>
              <div style={{ padding: 12, background: 'var(--slate-50)', borderRadius: 'var(--radius)' }}>
                <div style={{ fontSize: 11, color: 'var(--slate-500)', fontWeight: 600 }}>ACTIVE TRIPS</div>
                <div style={{ fontSize: 20, fontWeight: 700, color: 'var(--primary-600)' }}>{specialHire?.activeTrips || 0}</div>
              </div>
            </div>
            <table style={{ width: '100%' }}>
              <tbody>
                <tr><td style={{ color: 'var(--slate-500)' }}>Monthly Revenue</td><td style={{ textAlign: 'right', fontWeight: 600, color: 'var(--green-600)' }}>TZS {fmt(specialHire?.monthlyRevenue)}</td></tr>
                <tr><td style={{ color: 'var(--slate-500)' }}>Monthly Expenses</td><td style={{ textAlign: 'right', fontWeight: 600, color: 'var(--red-600)' }}>TZS {fmt(specialHire?.monthlyExpenses)}</td></tr>
                <tr><td style={{ fontWeight: 600 }}>Net Profit</td><td style={{ textAlign: 'right', fontWeight: 700, color: (specialHire?.monthlyProfit || 0) >= 0 ? 'var(--green-600)' : 'var(--red-600)' }}>TZS {fmt(specialHire?.monthlyProfit)}</td></tr>
              </tbody>
            </table>
          </div>
        </div>

        {/* Daladala Card */}
        <div className="card">
          <div className="card-header">
            <div>
              <h3><Activity size={16} /> Daladala Operations</h3>
              <p>Daily route performance</p>
            </div>
          </div>
          <div className="card-body">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 16 }}>
              <div style={{ padding: 12, background: 'var(--slate-50)', borderRadius: 'var(--radius)' }}>
                <div style={{ fontSize: 11, color: 'var(--slate-500)', fontWeight: 600 }}>TODAY</div>
                <div style={{ fontSize: 20, fontWeight: 700, color: 'var(--primary-600)' }}>{daladala?.todayOperations || 0}</div>
              </div>
              <div style={{ padding: 12, background: 'var(--slate-50)', borderRadius: 'var(--radius)' }}>
                <div style={{ fontSize: 11, color: 'var(--slate-500)', fontWeight: 600 }}>MONTHLY</div>
                <div style={{ fontSize: 20, fontWeight: 700, color: 'var(--slate-800)' }}>{daladala?.monthlyOperations || 0}</div>
              </div>
            </div>
            <table style={{ width: '100%' }}>
              <tbody>
                <tr><td style={{ color: 'var(--slate-500)' }}>Active Routes</td><td style={{ textAlign: 'right', fontWeight: 600 }}>{daladala?.activeRoutes || 0}</td></tr>
                <tr><td style={{ color: 'var(--slate-500)' }}>Monthly Revenue</td><td style={{ textAlign: 'right', fontWeight: 600, color: 'var(--green-600)' }}>TZS {fmt(daladala?.monthlyRevenue)}</td></tr>
                <tr><td style={{ color: 'var(--slate-500)' }}>Monthly Expenses</td><td style={{ textAlign: 'right', fontWeight: 600, color: 'var(--red-600)' }}>TZS {fmt(daladala?.monthlyExpenses)}</td></tr>
                <tr><td style={{ fontWeight: 600 }}>Net Profit</td><td style={{ textAlign: 'right', fontWeight: 700, color: (daladala?.monthlyProfit || 0) >= 0 ? 'var(--green-600)' : 'var(--red-600)' }}>TZS {fmt(daladala?.monthlyProfit)}</td></tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Alerts */}
      {alerts && alerts.length > 0 && (
        <div className="card">
          <div className="card-header">
            <div>
              <h3><AlertTriangle size={16} /> System Alerts</h3>
              <p>{alerts.length} items require your attention</p>
            </div>
            <span className="badge badge-warning">{alerts.length}</span>
          </div>
          <div className="card-body">
            {alerts.map((alert, i) => (
              <div key={i} className={`alert-item alert-${alert.severity?.toLowerCase() || 'info'}`}>
                <AlertTriangle size={16} style={{ flexShrink: 0 }} />
                <div style={{ flex: 1 }}>
                  <strong>{alert.type?.replace(/_/g, ' ')}</strong>
                  <span style={{ margin: '0 6px' }}>-</span>
                  {alert.message}
                </div>
                {alert.vehicleRegNumber && (
                  <span className="badge badge-neutral">{alert.vehicleRegNumber}</span>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {(!alerts || alerts.length === 0) && (
        <div className="card">
          <div className="card-body" style={{ textAlign: 'center', padding: 32 }}>
            <CheckCircle size={36} style={{ color: 'var(--green-500)', marginBottom: 12 }} />
            <h4 style={{ color: 'var(--green-700)', fontWeight: 600, marginBottom: 4 }}>All Systems Operational</h4>
            <p style={{ fontSize: 13, color: 'var(--slate-500)' }}>No alerts at this time. Your fleet is running smoothly.</p>
          </div>
        </div>
      )}
    </div>
  );
}
