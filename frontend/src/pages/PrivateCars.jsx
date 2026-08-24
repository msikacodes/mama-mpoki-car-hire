import { useState, useEffect } from 'react';
import { getPrivateCars, createPrivateCar, deletePrivateCar, getFuelRecords, addFuelRecord, getMaintenanceRecords, addMaintenanceRecord } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, Eye, Trash2, Fuel, Wrench, Shield, Calendar, Car } from 'lucide-react';

const fmt = (n) => new Intl.NumberFormat('en-TZ').format(n || 0);

export default function PrivateCars() {
  const [cars, setCars] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [showDetail, setShowDetail] = useState(null);
  const [detail, setDetail] = useState(null);
  const [fuelRecords, setFuelRecords] = useState([]);
  const [maintenanceRecords, setMaintenanceRecords] = useState([]);
  const [form, setForm] = useState({ vehicleType: 'PRIVATE_CAR', moduleType: 'PRIVATE', make: '', model: '', year: 2024, regNumber: '', color: '', fuelType: 'DIESEL', capacity: 7, insuranceNumber: '', insuranceProvider: '', insuranceExpiry: '', registrationExpiry: '' });
  const [fuelForm, setFuelForm] = useState({ fuelDate: new Date().toISOString().slice(0, 10), liters: '', costPerLiter: '', totalCost: '', odometer: '' });
  const [maintForm, setMaintForm] = useState({ maintenanceDate: new Date().toISOString().slice(0, 10), maintenanceType: 'SERVICE', description: '', cost: '', garageName: '', odometer: '' });

  const load = () => getPrivateCars().then(r => setCars(r.data || [])).catch(console.error);
  useEffect(() => { load(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try { await createPrivateCar(form); setShowCreate(false); load(); } catch (err) { alert(err.message || 'Failed to register car'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this car?')) return;
    try { await deletePrivateCar(id); load(); } catch (err) { alert(err.message || 'Failed to delete car'); }
  };

  const viewDetail = async (car) => {
    setDetail(car); setShowDetail(car.id);
    try {
      const [f, m] = await Promise.all([
        getFuelRecords(car.id).catch(() => ({ data: [] })),
        getMaintenanceRecords(car.id).catch(() => ({ data: [] }))
      ]);
      setFuelRecords(f.data || []);
      setMaintenanceRecords(m.data || []);
    } catch (err) { console.error(err); }
  };

  const handleFuel = async (e) => {
    e.preventDefault();
    try {
      await addFuelRecord(showDetail, { ...fuelForm, liters: parseFloat(fuelForm.liters), costPerLiter: parseFloat(fuelForm.costPerLiter), totalCost: parseFloat(fuelForm.totalCost), odometer: fuelForm.odometer ? parseInt(fuelForm.odometer) : null });
      const f = await getFuelRecords(showDetail); setFuelRecords(f.data || []);
      setFuelForm({ fuelDate: new Date().toISOString().slice(0, 10), liters: '', costPerLiter: '', totalCost: '', odometer: '' });
      load();
    } catch (err) { alert(err.message || 'Failed to add fuel record'); }
  };

  const handleMaintenance = async (e) => {
    e.preventDefault();
    try {
      await addMaintenanceRecord(showDetail, { ...maintForm, cost: parseFloat(maintForm.cost), odometer: maintForm.odometer ? parseInt(maintForm.odometer) : null });
      const m = await getMaintenanceRecords(showDetail); setMaintenanceRecords(m.data || []);
      setMaintForm({ maintenanceDate: new Date().toISOString().slice(0, 10), maintenanceType: 'SERVICE', description: '', cost: '', garageName: '', odometer: '' });
    } catch (err) { alert(err.message || 'Failed to add maintenance record'); }
  };

  const columns = [
    { header: 'Vehicle', render: r => (
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{ width: 34, height: 34, borderRadius: 'var(--radius)', background: 'var(--slate-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Car size={16} style={{ color: 'var(--slate-600)' }} /></div>
        <div><div style={{ fontWeight: 600 }}>{r.regNumber}</div><div style={{ fontSize: 11, color: 'var(--slate-400)' }}>{r.make} {r.model}</div></div>
      </div>
    )},
    { header: 'Insurance', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Shield size={13} style={{ color: r.insuranceExpiry ? 'var(--green-500)' : 'var(--slate-300)' }} /> {r.insuranceExpiry || 'None'}</span> },
    { header: 'Reg Expiry', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Calendar size={13} style={{ color: 'var(--slate-400)' }} /> {r.registrationExpiry || '-'}</span> },
    { header: 'Fuel Cost', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Fuel size={13} style={{ color: 'var(--amber-500)' }} /> TZS {fmt(r.totalFuelCost)}</span> },
    { header: 'Maintenance', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Wrench size={13} style={{ color: 'var(--slate-400)' }} /> TZS {fmt(r.totalMaintenanceCost)}</span> },
    { header: '', render: r => (
      <div className="btn-group">
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => viewDetail(r)}><Eye size={14} /></button>
        <button className="btn btn-icon btn-ghost btn-sm" style={{ color: 'var(--red-500)' }} onClick={() => handleDelete(r.id)}><Trash2 size={14} /></button>
      </div>
    )}
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div><h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px' }}>Private Cars</h2><p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{cars.length} registered</p></div>
        <button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> Register Car</button>
      </div>
      <div className="card"><DataTable columns={columns} data={cars} emptyText="No private cars registered" /></div>

      {showCreate && (
        <Modal title="Register Private Car" onClose={() => setShowCreate(false)}>
          <form onSubmit={handleCreate}>
            <div className="form-row">
              <div className="form-group"><label>Registration Number</label><input className="form-control" value={form.regNumber} onChange={e => setForm({...form, regNumber: e.target.value})} required placeholder="T 789 STU" /></div>
              <div className="form-group"><label>Make</label><input className="form-control" value={form.make} onChange={e => setForm({...form, make: e.target.value})} placeholder="Toyota" /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Model</label><input className="form-control" value={form.model} onChange={e => setForm({...form, model: e.target.value})} placeholder="Land Cruiser" /></div>
              <div className="form-group"><label>Year</label><input className="form-control" type="number" value={form.year} onChange={e => setForm({...form, year: parseInt(e.target.value)})} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Insurance Number</label><input className="form-control" value={form.insuranceNumber} onChange={e => setForm({...form, insuranceNumber: e.target.value})} /></div>
              <div className="form-group"><label>Insurance Provider</label><input className="form-control" value={form.insuranceProvider} onChange={e => setForm({...form, insuranceProvider: e.target.value})} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Insurance Expiry</label><input className="form-control" type="date" value={form.insuranceExpiry} onChange={e => setForm({...form, insuranceExpiry: e.target.value})} /></div>
              <div className="form-group"><label>Registration Expiry</label><input className="form-control" type="date" value={form.registrationExpiry} onChange={e => setForm({...form, registrationExpiry: e.target.value})} /></div>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowCreate(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Register</button>
            </div>
          </form>
        </Modal>
      )}

      {showDetail && detail && (
        <Modal title={`${detail.make} ${detail.model} - ${detail.regNumber}`} onClose={() => setShowDetail(null)}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 10 }}><Fuel size={14} style={{ color: 'var(--amber-600)' }} /><h3 style={{ fontSize: 13, fontWeight: 600 }}>Fuel Records</h3></div>
              {fuelRecords.length > 0 ? (
                <table style={{ width: '100%', fontSize: 12 }}><thead><tr><th>Date</th><th>L</th><th>Cost/L</th><th>Total</th></tr></thead><tbody>{fuelRecords.map((r, i) => <tr key={i}><td>{r.fuelDate}</td><td>{r.liters}</td><td>TZS {fmt(r.costPerLiter)}</td><td style={{ fontWeight: 600 }}>TZS {fmt(r.totalCost)}</td></tr>)}</tbody></table>
              ) : <p style={{ fontSize: 12, color: 'var(--slate-400)' }}>No records</p>}
              <form onSubmit={handleFuel} style={{ marginTop: 10, display: 'flex', gap: 4, flexWrap: 'wrap' }}>
                <input className="form-control" style={{ width: 65, fontSize: 11, padding: '5px 6px' }} type="number" value={fuelForm.liters} onChange={e => setFuelForm({...fuelForm, liters: e.target.value})} placeholder="Liters" required />
                <input className="form-control" style={{ width: 80, fontSize: 11, padding: '5px 6px' }} type="number" value={fuelForm.costPerLiter} onChange={e => setFuelForm({...fuelForm, costPerLiter: e.target.value})} placeholder="Cost/L" required />
                <input className="form-control" style={{ width: 90, fontSize: 11, padding: '5px 6px' }} type="number" value={fuelForm.totalCost} onChange={e => setFuelForm({...fuelForm, totalCost: e.target.value})} placeholder="Total" required />
                <button type="submit" className="btn btn-success btn-xs">Add</button>
              </form>
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 10 }}><Wrench size={14} style={{ color: 'var(--slate-500)' }} /><h3 style={{ fontSize: 13, fontWeight: 600 }}>Maintenance</h3></div>
              {maintenanceRecords.length > 0 ? (
                <table style={{ width: '100%', fontSize: 12 }}><thead><tr><th>Date</th><th>Type</th><th>Cost</th></tr></thead><tbody>{maintenanceRecords.map((r, i) => <tr key={i}><td>{r.maintenanceDate}</td><td>{r.maintenanceType?.replace('_', ' ')}</td><td style={{ fontWeight: 600 }}>TZS {fmt(r.cost)}</td></tr>)}</tbody></table>
              ) : <p style={{ fontSize: 12, color: 'var(--slate-400)' }}>No records</p>}
              <form onSubmit={handleMaintenance} style={{ marginTop: 10, display: 'flex', gap: 4, flexWrap: 'wrap' }}>
                <select className="form-control" style={{ width: 'auto', flex: 1, fontSize: 11, padding: '5px 6px' }} value={maintForm.maintenanceType} onChange={e => setMaintForm({...maintForm, maintenanceType: e.target.value})}>{['SERVICE', 'REPAIR', 'TIRE_CHANGE', 'OIL_CHANGE', 'INSPECTION', 'OTHER'].map(t => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}</select>
                <input className="form-control" style={{ width: 90, fontSize: 11, padding: '5px 6px' }} type="number" value={maintForm.cost} onChange={e => setMaintForm({...maintForm, cost: e.target.value})} placeholder="Cost" required />
                <button type="submit" className="btn btn-primary btn-xs">Add</button>
              </form>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
