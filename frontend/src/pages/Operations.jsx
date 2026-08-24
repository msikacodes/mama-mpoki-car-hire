import { useState, useEffect } from 'react';
import { getOperations, getOperation, getRoutes, getVehicles, getDrivers, getConductors, createOperation, completeOperation, getRevenues, addRevenue, getExpenses, addExpense } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, CheckCircle, Eye, Users, TrendingUp, TrendingDown } from 'lucide-react';

const fmt = (n) => new Intl.NumberFormat('en-TZ').format(n || 0);

export default function Operations() {
  const [ops, setOps] = useState([]);
  const [routes, setRoutes] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [conductors, setConductors] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [showDetail, setShowDetail] = useState(null);
  const [detail, setDetail] = useState(null);
  const [detailRevenues, setDetailRevenues] = useState([]);
  const [detailExpenses, setDetailExpenses] = useState([]);
  const [form, setForm] = useState({ vehicleId: '', routeId: '', driverId: '', conductorId: '', operationDate: new Date().toISOString().slice(0, 10), departureTime: '' });
  const [revForm, setRevForm] = useState({ source: 'FARE', amount: '', description: '', revenueDate: new Date().toISOString().slice(0, 10) });
  const [expForm, setExpForm] = useState({ expenseType: 'FUEL', amount: '', description: '', expenseDate: new Date().toISOString().slice(0, 10) });

  const load = () => getOperations({ size: 200 }).then(r => setOps(r.data?.content || [])).catch(console.error);
  useEffect(() => { load(); }, []);
  useEffect(() => {
    getRoutes({ size: 200 }).then(r => setRoutes(r.data?.content || [])).catch(console.error);
    getVehicles({ size: 200 }).then(r => setVehicles(r.data?.content || [])).catch(console.error);
    getDrivers({ size: 200 }).then(r => setDrivers(r.data?.content || [])).catch(console.error);
    getConductors({ size: 200 }).then(r => setConductors(r.data?.content || [])).catch(console.error);
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await createOperation({ ...form, vehicleId: parseInt(form.vehicleId), routeId: parseInt(form.routeId), driverId: parseInt(form.driverId), conductorId: form.conductorId ? parseInt(form.conductorId) : null });
      setShowCreate(false); load();
    } catch (err) { alert(err.message || 'Failed to create operation'); }
  };

  const handleComplete = async (id) => {
    const p = prompt('Total passengers?');
    if (p === null) return;
    const passengers = parseInt(p);
    if (isNaN(passengers) || passengers < 0) { alert('Please enter a valid number'); return; }
    try {
      await completeOperation(id, { totalPassengers: passengers });
      load();
    } catch (err) { alert(err.message || 'Failed to complete operation'); }
  };

  const viewDetail = async (id) => {
    try {
      const [opRes, revRes, expRes] = await Promise.all([
        getOperation(id),
        getRevenues(id).catch(() => ({ data: [] })),
        getExpenses(id).catch(() => ({ data: [] }))
      ]);
      setDetail(opRes.data);
      setDetailRevenues(revRes.data || []);
      setDetailExpenses(expRes.data || []);
      setShowDetail(id);
    } catch (err) { alert('Failed to load operation details'); }
  };

  const handleRevenue = async (e) => {
    e.preventDefault();
    try {
      await addRevenue(showDetail, { ...revForm, amount: parseFloat(revForm.amount) });
      const revRes = await getRevenues(showDetail);
      setDetailRevenues(revRes.data || []);
      setRevForm({ source: 'FARE', amount: '', description: '', revenueDate: new Date().toISOString().slice(0, 10) });
      load();
    } catch (err) { alert(err.message || 'Failed to add revenue'); }
  };

  const handleExpense = async (e) => {
    e.preventDefault();
    try {
      await addExpense(showDetail, { ...expForm, amount: parseFloat(expForm.amount) });
      const expRes = await getExpenses(showDetail);
      setDetailExpenses(expRes.data || []);
      setExpForm({ expenseType: 'FUEL', amount: '', description: '', expenseDate: new Date().toISOString().slice(0, 10) });
      load();
    } catch (err) { alert(err.message || 'Failed to add expense'); }
  };

  const statusBadge = (s) => {
    const m = { SCHEDULED: 'warning', IN_PROGRESS: 'primary', COMPLETED: 'success', CANCELLED: 'danger' };
    return <span className={`badge badge-${m[s] || 'neutral'}`}><span className="badge-dot"></span>{s}</span>;
  };

  const columns = [
    { header: 'Op #', render: r => <span style={{ fontWeight: 600 }}>#{r.id}</span> },
    { header: 'Date', accessor: 'operationDate' },
    { header: 'Route', render: r => r.routeName || `Route #${r.routeId}` },
    { header: 'Vehicle', render: r => r.vehicleRegNumber || `Vehicle #${r.vehicleId}` },
    { header: 'Driver', render: r => r.driverName || '-' },
    { header: 'Passengers', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Users size={13} style={{ color: 'var(--slate-400)' }} /> {r.totalPassengers || '-'}</span> },
    { header: 'Status', render: r => statusBadge(r.status) },
    { header: '', render: r => (
      <div className="btn-group">
        {r.status === 'SCHEDULED' && <button className="btn btn-success btn-xs" onClick={() => handleComplete(r.id)}><CheckCircle size={12} /> Complete</button>}
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => viewDetail(r.id)}><Eye size={14} /></button>
      </div>
    )}
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div><h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px' }}>Daladala Operations</h2><p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{ops.length} daily operations</p></div>
        <button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> New Operation</button>
      </div>
      <div className="card"><DataTable columns={columns} data={ops} emptyText="No operations recorded" /></div>

      {showCreate && (
        <Modal title="New Daily Operation" onClose={() => setShowCreate(false)}>
          <form onSubmit={handleCreate}>
            <div className="form-row">
              <div className="form-group"><label>Vehicle</label><select className="form-control" value={form.vehicleId} onChange={e => setForm({...form, vehicleId: e.target.value})} required><option value="">Select</option>{vehicles.filter(v => v.moduleType === 'DALADALA').map(v => <option key={v.id} value={v.id}>{v.regNumber} - {v.make}</option>)}</select></div>
              <div className="form-group"><label>Route</label><select className="form-control" value={form.routeId} onChange={e => setForm({...form, routeId: e.target.value})} required><option value="">Select</option>{routes.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}</select></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Driver</label><select className="form-control" value={form.driverId} onChange={e => setForm({...form, driverId: e.target.value})} required><option value="">Select</option>{drivers.map(d => <option key={d.id} value={d.id}>{d.fullName}</option>)}</select></div>
              <div className="form-group"><label>Conductor</label><select className="form-control" value={form.conductorId} onChange={e => setForm({...form, conductorId: e.target.value})}><option value="">Select</option>{conductors.map(c => <option key={c.id} value={c.id}>{c.fullName}</option>)}</select></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Date</label><input className="form-control" type="date" value={form.operationDate} onChange={e => setForm({...form, operationDate: e.target.value})} required /></div>
              <div className="form-group"><label>Departure Time</label><input className="form-control" type="time" value={form.departureTime} onChange={e => setForm({...form, departureTime: e.target.value})} /></div>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowCreate(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Create</button>
            </div>
          </form>
        </Modal>
      )}

      {showDetail && detail && (
        <Modal title={`Operation #${showDetail} Details`} onClose={() => setShowDetail(null)}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 10 }}><TrendingUp size={14} style={{ color: 'var(--green-600)' }} /><h3 style={{ fontSize: 13, fontWeight: 600 }}>Revenue</h3></div>
              {detailRevenues.length > 0 ? (
                <table style={{ width: '100%', fontSize: 12 }}><thead><tr><th>Source</th><th>Amount</th></tr></thead><tbody>{detailRevenues.map((r, i) => <tr key={i}><td>{r.source}</td><td style={{ fontWeight: 600 }}>TZS {fmt(r.amount)}</td></tr>)}</tbody></table>
              ) : <p style={{ fontSize: 12, color: 'var(--slate-400)' }}>No revenue recorded</p>}
              <form onSubmit={handleRevenue} style={{ marginTop: 10, display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                <select className="form-control" style={{ width: 'auto', flex: 1, fontSize: 12, padding: '6px 8px' }} value={revForm.source} onChange={e => setRevForm({...revForm, source: e.target.value})}>{['FARE', 'CHARTER', 'ADVERTISING', 'OTHER'].map(s => <option key={s} value={s}>{s}</option>)}</select>
                <input className="form-control" style={{ width: 100, fontSize: 12, padding: '6px 8px' }} type="number" value={revForm.amount} onChange={e => setRevForm({...revForm, amount: e.target.value})} placeholder="Amount" required />
                <button type="submit" className="btn btn-success btn-xs">Add</button>
              </form>
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 10 }}><TrendingDown size={14} style={{ color: 'var(--red-600)' }} /><h3 style={{ fontSize: 13, fontWeight: 600 }}>Expenses</h3></div>
              {detailExpenses.length > 0 ? (
                <table style={{ width: '100%', fontSize: 12 }}><thead><tr><th>Type</th><th>Amount</th></tr></thead><tbody>{detailExpenses.map((r, i) => <tr key={i}><td>{r.expenseType?.replace('_', ' ')}</td><td style={{ fontWeight: 600 }}>TZS {fmt(r.amount)}</td></tr>)}</tbody></table>
              ) : <p style={{ fontSize: 12, color: 'var(--slate-400)' }}>No expenses recorded</p>}
              <form onSubmit={handleExpense} style={{ marginTop: 10, display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                <select className="form-control" style={{ width: 'auto', flex: 1, fontSize: 12, padding: '6px 8px' }} value={expForm.expenseType} onChange={e => setExpForm({...expForm, expenseType: e.target.value})}>{['FUEL', 'REPAIR', 'TOLL', 'MAINTENANCE', 'CONDUCTOR_ALLOWANCE', 'OTHER'].map(t => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}</select>
                <input className="form-control" style={{ width: 100, fontSize: 12, padding: '6px 8px' }} type="number" value={expForm.amount} onChange={e => setExpForm({...expForm, amount: e.target.value})} placeholder="Amount" required />
                <button type="submit" className="btn btn-primary btn-xs">Add</button>
              </form>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
