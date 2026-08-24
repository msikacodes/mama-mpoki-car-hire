import { useState, useEffect } from 'react';
import { getVehicles, createVehicle, updateVehicle, deleteVehicle, updateVehicleStatus } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, Edit2, Trash2, Search, Filter, Car, MoreVertical } from 'lucide-react';

const MODULE_TYPES = ['SPECIAL_HIRE', 'DALADALA', 'PRIVATE'];
const VEHICLE_TYPES = ['COASTER', 'MINIBUS', 'DALADALA_BUS', 'PRIVATE_CAR'];
const FUEL_TYPES = ['DIESEL', 'PETROL', 'HYBRID', 'ELECTRIC'];

export default function Vehicles() {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [filter, setFilter] = useState('');
  const [search, setSearch] = useState('');
  const [form, setForm] = useState({ vehicleType: 'COASTER', moduleType: 'SPECIAL_HIRE', make: '', model: '', year: 2024, regNumber: '', color: '', capacity: 30, fuelType: 'DIESEL' });

  const load = () => {
    setLoading(true);
    const params = { size: 200 };
    if (filter) params.moduleType = filter;
    getVehicles(params).then(r => setVehicles(r.data?.content || [])).catch(console.error).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [filter]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editItem) await updateVehicle(editItem.id, form);
      else await createVehicle(form);
      setShowModal(false); setEditItem(null); load();
    } catch (err) { alert(err.message || 'Error'); }
  };

  const handleDelete = async (id) => {
    if (confirm('Are you sure you want to delete this vehicle?')) {
      await deleteVehicle(id); load();
    }
  };

  const openEdit = (v) => {
    setForm({ vehicleType: v.vehicleType, moduleType: v.moduleType, make: v.make || '', model: v.model || '', year: v.year || 2024, regNumber: v.regNumber, color: v.color || '', capacity: v.capacity || 16, fuelType: v.fuelType || 'DIESEL' });
    setEditItem(v); setShowModal(true);
  };

  const openNew = () => {
    setForm({ vehicleType: 'COASTER', moduleType: 'SPECIAL_HIRE', make: '', model: '', year: 2024, regNumber: '', color: '', capacity: 30, fuelType: 'DIESEL' });
    setEditItem(null); setShowModal(true);
  };

  const statusBadge = (s) => {
    const m = { ACTIVE: 'success', MAINTENANCE: 'warning', INACTIVE: 'danger' };
    return <span className={`badge badge-${m[s] || 'neutral'}`}><span className="badge-dot"></span>{s}</span>;
  };

  const moduleBadge = (m) => {
    const c = { SPECIAL_HIRE: 'primary', DALADALA: 'warning', PRIVATE: 'danger' };
    return <span className={`badge badge-${c[m] || 'neutral'}`}>{m}</span>;
  };

  const filtered = vehicles.filter(v => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (v.regNumber || '').toLowerCase().includes(q) || (v.make || '').toLowerCase().includes(q) || (v.model || '').toLowerCase().includes(q);
  });

  const columns = [
    { header: 'Registration', render: r => (
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{ width: 34, height: 34, borderRadius: 'var(--radius)', background: 'var(--primary-50)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Car size={16} style={{ color: 'var(--primary-600)' }} />
        </div>
        <div>
          <div style={{ fontWeight: 600, color: 'var(--slate-900)' }}>{r.regNumber}</div>
          <div style={{ fontSize: 11, color: 'var(--slate-400)' }}>{r.color || 'N/A'}</div>
        </div>
      </div>
    )},
    { header: 'Make / Model', render: r => <span style={{ fontWeight: 500 }}>{r.make || '-'} {r.model || ''}</span> },
    { header: 'Year', accessor: 'year' },
    { header: 'Type', render: r => moduleBadge(r.moduleType) },
    { header: 'Capacity', render: r => <span>{r.capacity || '-'} seats</span> },
    { header: 'Status', render: r => statusBadge(r.status) },
    { header: '', render: r => (
      <div className="btn-group">
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => openEdit(r)} title="Edit"><Edit2 size={14} /></button>
        <button className="btn btn-icon btn-ghost btn-sm" style={{ color: 'var(--red-500)' }} onClick={() => handleDelete(r.id)} title="Delete"><Trash2 size={14} /></button>
      </div>
    )}
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div>
          <h2 style={{ fontSize: 22, fontWeight: 800, color: 'var(--slate-900)', letterSpacing: '-0.5px' }}>Vehicles</h2>
          <p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{vehicles.length} vehicles in your fleet</p>
        </div>
        <button className="btn btn-primary" onClick={openNew}><Plus size={16} /> Add Vehicle</button>
      </div>

      <div className="card">
        <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--slate-100)', display: 'flex', gap: 10, alignItems: 'center' }}>
          <div style={{ position: 'relative', flex: 1, maxWidth: 300 }}>
            <Search size={15} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--slate-400)' }} />
            <input className="form-control" style={{ paddingLeft: 34 }} placeholder="Search vehicles..." value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          <div style={{ display: 'flex', gap: 4 }}>
            <button className={`btn btn-xs ${!filter ? 'btn-primary' : 'btn-ghost'}`} onClick={() => setFilter('')}>All</button>
            {MODULE_TYPES.map(t => (
              <button key={t} className={`btn btn-xs ${filter === t ? 'btn-primary' : 'btn-ghost'}`} onClick={() => setFilter(t)}>{t}</button>
            ))}
          </div>
        </div>
        <DataTable columns={columns} data={filtered} emptyText="No vehicles found" />
      </div>

      {showModal && (
        <Modal title={editItem ? 'Edit Vehicle' : 'Add New Vehicle'} onClose={() => setShowModal(false)}>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group"><label>Registration Number</label><input className="form-control" value={form.regNumber} onChange={e => setForm({...form, regNumber: e.target.value})} required placeholder="T 123 ABC" /></div>
              <div className="form-group"><label>Fleet Type</label><select className="form-control" value={form.moduleType} onChange={e => setForm({...form, moduleType: e.target.value})}>{MODULE_TYPES.map(t => <option key={t} value={t}>{t}</option>)}</select></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Vehicle Type</label><select className="form-control" value={form.vehicleType} onChange={e => setForm({...form, vehicleType: e.target.value})}>{VEHICLE_TYPES.map(t => <option key={t} value={t}>{t}</option>)}</select></div>
              <div className="form-group"><label>Fuel Type</label><select className="form-control" value={form.fuelType} onChange={e => setForm({...form, fuelType: e.target.value})}>{FUEL_TYPES.map(t => <option key={t} value={t}>{t}</option>)}</select></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Make</label><input className="form-control" value={form.make} onChange={e => setForm({...form, make: e.target.value})} placeholder="Toyota" /></div>
              <div className="form-group"><label>Model</label><input className="form-control" value={form.model} onChange={e => setForm({...form, model: e.target.value})} placeholder="HiAce" /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Year</label><input className="form-control" type="number" value={form.year} onChange={e => setForm({...form, year: parseInt(e.target.value)})} /></div>
              <div className="form-group"><label>Capacity</label><input className="form-control" type="number" value={form.capacity} onChange={e => setForm({...form, capacity: parseInt(e.target.value)})} /></div>
            </div>
            <div className="form-group"><label>Color</label><input className="form-control" value={form.color} onChange={e => setForm({...form, color: e.target.value})} placeholder="White" /></div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">{editItem ? 'Update Vehicle' : 'Add Vehicle'}</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
