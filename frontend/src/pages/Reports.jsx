import { useState } from 'react';
import { downloadReportPdf, downloadReportExcel, getSpecialHireReport, getDaladalaReport, getExpenseReport, getMonthlySummary, getVehicleProfitability } from '../api/client';
import { Download, BarChart3, FileText, FileSpreadsheet, TrendingUp, TrendingDown, DollarSign } from 'lucide-react';

const fmt = (n) => new Intl.NumberFormat('en-TZ').format(n || 0);

export default function Reports() {
  const today = new Date().toISOString().slice(0, 10);
  const monthStart = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().slice(0, 10);
  const [from, setFrom] = useState(monthStart);
  const [to, setTo] = useState(today);
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [report, setReport] = useState(null);
  const [reportType, setReportType] = useState('');
  const [loading, setLoading] = useState(false);

  const loadReport = async (type) => {
    setReportType(type); setLoading(true); setReport(null);
    try {
      let data;
      if (type === 'special-hire') { const r = await getSpecialHireReport(from, to); data = r.data; }
      else if (type === 'daladala') { const r = await getDaladalaReport(from, to); data = r.data; }
      else if (type === 'expenses') { const r = await getExpenseReport(from, to); data = r.data; }
      else if (type === 'monthly') { const r = await getMonthlySummary(year, month); data = r.data; }
      else if (type === 'profitability') { const r = await getVehicleProfitability(); data = r.data; }
      setReport(data);
    } catch (err) { alert('Error loading report'); }
    setLoading(false);
  };

  const reports = [
    { id: 'special-hire', title: 'Special Hire Report', desc: 'Bookings, revenue, profit analysis', icon: FileText, color: 'blue' },
    { id: 'daladala', title: 'Daladala Report', desc: 'Route performance, daily operations', icon: BarChart3, color: 'amber' },
    { id: 'expenses', title: 'Expense Report', desc: 'Cost breakdown by category', icon: DollarSign, color: 'red' },
    { id: 'monthly', title: 'Monthly P&L', desc: 'Profit and loss by module', icon: TrendingUp, color: 'green' },
    { id: 'profitability', title: 'Vehicle Profitability', desc: 'Per-vehicle cost analysis', icon: FileSpreadsheet, color: 'blue' },
  ];

  const months = Array.from({length:12}, (_,i) => new Date(2024, i).toLocaleString('en', {month: 'long'}));

  return (
    <div>
      <h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px', marginBottom: 20 }}>Reports & Analytics</h2>

      {/* Filters */}
      <div className="card" style={{ marginBottom: 20 }}>
        <div className="card-body">
          <div style={{ display: 'flex', gap: 12, alignItems: 'end', flexWrap: 'wrap' }}>
            <div className="form-group" style={{ marginBottom: 0 }}><label>From</label><input className="form-control" type="date" value={from} onChange={e => setFrom(e.target.value)} /></div>
            <div className="form-group" style={{ marginBottom: 0 }}><label>To</label><input className="form-control" type="date" value={to} onChange={e => setTo(e.target.value)} /></div>
            <div className="form-group" style={{ marginBottom: 0 }}><label>Year</label><select className="form-control" value={year} onChange={e => setYear(parseInt(e.target.value))}>{[2024, 2025, 2026, 2027].map(y => <option key={y} value={y}>{y}</option>)}</select></div>
            <div className="form-group" style={{ marginBottom: 0 }}><label>Month</label><select className="form-control" value={month} onChange={e => setMonth(parseInt(e.target.value))}>{months.map((m, i) => <option key={i+1} value={i+1}>{m}</option>)}</select></div>
          </div>
        </div>
      </div>

      {/* Report Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: 16, marginBottom: 24 }}>
        {reports.map(r => (
          <div key={r.id} className="card" style={{ cursor: 'pointer' }} onClick={() => loadReport(r.id)}>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 14 }}>
                <div className={`stat-card-icon ${r.color}`}><r.icon size={20} /></div>
                <div>
                  <div style={{ fontWeight: 600, fontSize: 13 }}>{r.title}</div>
                  <div style={{ fontSize: 11, color: 'var(--slate-400)' }}>{r.desc}</div>
                </div>
              </div>
              <div style={{ display: 'flex', gap: 6 }}>
                <button className="btn btn-ghost btn-xs" onClick={e => { e.stopPropagation(); downloadReportPdf(r.id, { from, to, year, month }); }}><Download size={12} /> PDF</button>
                <button className="btn btn-ghost btn-xs" onClick={e => { e.stopPropagation(); downloadReportExcel(r.id, { from, to, year, month }); }}><Download size={12} /> Excel</button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Results */}
      {loading && <div className="card"><div className="card-body" style={{ textAlign: 'center', padding: 40, color: 'var(--slate-400)' }}>Loading report...</div></div>}

      {report && reportType === 'special-hire' && (
        <div className="card">
          <div className="card-header"><div><h3><FileText size={16} style={{ marginRight: 6 }} /> Special Hire Report</h3><p>{from} to {to}</p></div></div>
          <div className="card-body">
            <div className="stat-grid">
              <div className="stat-card"><div className="stat-card-icon blue"><BarChart3 size={20} /></div><div className="stat-card-label">Total Bookings</div><div className="stat-card-value">{report.totalBookings}</div></div>
              <div className="stat-card"><div className="stat-card-icon green"><TrendingUp size={20} /></div><div className="stat-card-label">Completed</div><div className="stat-card-value">{report.completedTrips}</div></div>
              <div className="stat-card"><div className="stat-card-label">Revenue</div><div className="stat-card-value" style={{ color: 'var(--green-600)' }}>TZS {fmt(report.totalRevenue)}</div></div>
              <div className="stat-card"><div className="stat-card-label">Expenses</div><div className="stat-card-value" style={{ color: 'var(--red-600)' }}>TZS {fmt(report.totalExpenses)}</div></div>
              <div className="stat-card"><div className="stat-card-label">Net Profit</div><div className="stat-card-value" style={{ color: (report.totalProfit||0) >= 0 ? 'var(--green-600)' : 'var(--red-600)' }}>TZS {fmt(report.totalProfit)}</div></div>
              <div className="stat-card"><div className="stat-card-label">Margin</div><div className="stat-card-value">{(report.profitMargin||0).toFixed(1)}%</div></div>
            </div>
          </div>
        </div>
      )}

      {report && reportType === 'expenses' && report.byCategory && (
        <div className="card">
          <div className="card-header"><div><h3><DollarSign size={16} style={{ marginRight: 6 }} /> Expense Report</h3></div></div>
          <div className="card-body">
            <div className="stat-card" style={{ marginBottom: 20, borderLeft: '4px solid var(--red-500)' }}><div className="stat-card-label">Total Expenses</div><div className="stat-card-value" style={{ color: 'var(--red-600)' }}>TZS {fmt(report.totalExpenses)}</div></div>
            <table style={{ width: '100%' }}><thead><tr><th>Category</th><th style={{ textAlign: 'right' }}>Amount</th></tr></thead>
              <tbody>{Object.entries(report.byCategory).filter(([k]) => k !== 'Total').map(([k, v]) => <tr key={k}><td style={{ fontWeight: 500 }}>{k}</td><td style={{ textAlign: 'right', fontWeight: 600 }}>TZS {fmt(v)}</td></tr>)}</tbody>
            </table>
          </div>
        </div>
      )}

      {report && reportType === 'monthly' && (
        <div className="card">
          <div className="card-header"><div><h3><BarChart3 size={16} style={{ marginRight: 6 }} /> Monthly P&L - {months[month-1]} {year}</h3></div></div>
          <div className="card-body">
            <table style={{ width: '100%' }}><thead><tr><th>Module</th><th style={{ textAlign: 'right' }}>Revenue</th><th style={{ textAlign: 'right' }}>Expenses</th><th style={{ textAlign: 'right' }}>Profit</th></tr></thead>
              <tbody>
                {['specialHire', 'daladala', 'privateCars'].map(k => (
                  <tr key={k}><td style={{ fontWeight: 500, textTransform: 'capitalize' }}>{k.replace(/([A-Z])/g, ' $1')}</td>
                  <td style={{ textAlign: 'right' }}>TZS {fmt(report[k]?.revenue)}</td>
                  <td style={{ textAlign: 'right' }}>TZS {fmt(report[k]?.expenses)}</td>
                  <td style={{ textAlign: 'right', fontWeight: 700, color: (report[k]?.profit||0) >= 0 ? 'var(--green-600)' : 'var(--red-600)' }}>TZS {fmt(report[k]?.profit)}</td></tr>
                ))}
                <tr style={{ fontWeight: 700, background: 'var(--slate-50)' }}><td>TOTAL</td><td style={{ textAlign: 'right' }}>TZS {fmt(report.totalRevenue)}</td><td style={{ textAlign: 'right' }}>TZS {fmt(report.totalExpenses)}</td><td style={{ textAlign: 'right' }}>TZS {fmt(report.netProfit)}</td></tr>
              </tbody>
            </table>
          </div>
        </div>
      )}

      {report && reportType === 'daladala' && (
        <div className="card">
          <div className="card-header"><div><h3><BarChart3 size={16} style={{ marginRight: 6 }} /> Daladala Report</h3><p>{from} to {to}</p></div></div>
          <div className="card-body">
            <div className="stat-grid">
              <div className="stat-card"><div className="stat-card-label">Operations</div><div className="stat-card-value">{report.totalOperations}</div></div>
              <div className="stat-card"><div className="stat-card-label">Revenue</div><div className="stat-card-value" style={{ color: 'var(--green-600)' }}>TZS {fmt(report.totalRevenue)}</div></div>
              <div className="stat-card"><div className="stat-card-label">Expenses</div><div className="stat-card-value" style={{ color: 'var(--red-600)' }}>TZS {fmt(report.totalExpenses)}</div></div>
              <div className="stat-card"><div className="stat-card-label">Passengers</div><div className="stat-card-value">{report.totalPassengers}</div></div>
            </div>
          </div>
        </div>
      )}

      {report && reportType === 'profitability' && report.vehicles && (
        <div className="card">
          <div className="card-header"><div><h3><FileSpreadsheet size={16} style={{ marginRight: 6 }} /> Vehicle Profitability</h3></div></div>
          <div className="card-body">
            <div className="stat-grid" style={{ marginBottom: 20 }}>
              <div className="stat-card"><div className="stat-card-label">Total Revenue</div><div className="stat-card-value" style={{ color: 'var(--green-600)' }}>TZS {fmt(report.totalRevenue)}</div></div>
              <div className="stat-card"><div className="stat-card-label">Total Expenses</div><div className="stat-card-value" style={{ color: 'var(--red-600)' }}>TZS {fmt(report.totalExpenses)}</div></div>
              <div className="stat-card"><div className="stat-card-label">Net Profit</div><div className="stat-card-value" style={{ color: (report.totalProfit||0) >= 0 ? 'var(--green-600)' : 'var(--red-600)' }}>TZS {fmt(report.totalProfit)}</div></div>
            </div>
            <table style={{ width: '100%' }}><thead><tr><th>Vehicle</th><th>Type</th><th style={{ textAlign: 'right' }}>Revenue</th><th style={{ textAlign: 'right' }}>Fuel</th><th style={{ textAlign: 'right' }}>Maintenance</th><th style={{ textAlign: 'right' }}>Profit</th></tr></thead>
              <tbody>{report.vehicles.map((v, i) => <tr key={i}><td><strong>{v.regNumber}</strong><br /><span style={{ fontSize: 11, color: 'var(--slate-400)' }}>{v.make} {v.model}</span></td><td><span className="badge badge-primary">{v.moduleType}</span></td><td style={{ textAlign: 'right' }}>TZS {fmt(v.revenue)}</td><td style={{ textAlign: 'right' }}>TZS {fmt(v.fuelCost)}</td><td style={{ textAlign: 'right' }}>TZS {fmt(v.maintenanceCost)}</td><td style={{ textAlign: 'right', fontWeight: 700, color: (v.profit||0) >= 0 ? 'var(--green-600)' : 'var(--red-600)' }}>TZS {fmt(v.profit)}</td></tr>)}</tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
