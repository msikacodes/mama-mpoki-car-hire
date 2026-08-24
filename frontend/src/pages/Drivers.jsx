import { useState, useEffect } from 'react';
import { getDrivers, createDriver, updateDriver, deleteDriver } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, Edit2, Trash2, Search, Users, Phone, CreditCard } from 'lucide-react';
import { SkeletonTable } from '../components/SkeletonLoader';

export default function Drivers() {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [search, setSearch] = useState('');
  const [form, setForm] = useState({ fullName: '', phone: '', licenseNumber: '', licenseExpiry: '', nationalId: '', address: '', dailyRate: 30000 });

  const load = () => {
    setLoading(true);
    getDrivers({ size: 200 }).then(r => setDrivers(r.data?.content || [])).catch(console.error).finally(() => setLoading(false));
  };
  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editItem) await updateDriver(editItem.id, form); else await createDriver(form);
      setShowModal(false); setEditItem(null); load();
    } catch (err) { alert(err.message || 'Error'); }
  };

  const openEdit = (d) => { setForm({ fullName: d.fullName, phone: d.phone || '', licenseNumber: d.licenseNumber || '', licenseExpiry: d.licenseExpiry || '', nationalId: d.nationalId || '', address: d.address || '', dailyRate: d.dailyRate || 30000 }); setEditItem(d); setShowModal(true); };
  const openNew = () => { setForm({ fullName: '', phone: '', licenseNumber: '', licenseExpiry: '', nationalId: '', address: '', dailyRate: 30000 }); setEditItem(null); setShowModal(true); };

  const filtered = drivers.filter(d => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (d.fullName || '').toLowerCase().includes(q) || (d.phone || '').includes(q);
  });

  const columns = [
    { header: 'Driver', render: r => (
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{ width: 34, height: 34, borderRadius: '50%', background: 'var(--primary-50)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: 13, color: 'var(--primary-600)' }}>{(r.fullName || 'D')[0].toUpperCase()}</div>
        <div>
          <div style={{ fontWeight: 600, color: 'var(--slate-900)' }}>{r.fullName}</div>
          <div style={{ fontSize: 11, color: 'var(--slate-400)' }}>{r.nationalId || 'No ID'}</div>
        </div>
      </div>
    )},
    { header: 'Phone', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Phone size={13} style={{ color: 'var(--slate-400)' }} /> {r.phone || '-'}</span> },
    { header: 'License No.', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><CreditCard size={13} style={{ color: 'var(--slate-400)' }} /> {r.licenseNumber || '-'}</span> },
    { header: 'Expiry', accessor: 'licenseExpiry' },
    { header: 'Daily Rate', render: r => <span style={{ fontWeight: 600 }}>TZS {new Intl.NumberFormat().format(r.dailyRate || 0)}</span> },
    { header: '', render: r => (
      <div className="btn-group">
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => openEdit(r)}><Edit2 size={14} /></button>
        <button className="btn btn-icon btn-ghost btn-sm" style={{ color: 'var(--red-500)' }} onClick={() => { if (confirm('Delete this driver?')) deleteDriver(r.id).then(load); }}><Trash2 size={14} /></button>
      </div>
    )}
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div><h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px' }}>Drivers</h2><p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{drivers.length} registered drivers</p></div>
        <button className="btn btn-primary" onClick={openNew}><Plus size={16} /> Add Driver</button>
      </div>
      <div className="card">
        <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--slate-100)' }}>
          <div style={{ position: 'relative', maxWidth: 300 }}><Search size={15} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--slate-400)' }} /><input className="form-control" style={{ paddingLeft: 34 }} placeholder="Search by name or phone..." value={search} onChange={e => setSearch(e.target.value)} /></div>
        </div>
        {loading ? <SkeletonTable rows={5} cols={6} /> : <DataTable columns={columns} data={filtered} emptyText="No drivers found" />}
      </div>
      {showModal && (
        <Modal title={editItem ? 'Edit Driver' : 'Add New Driver'} onClose={() => setShowModal(false)}>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group"><label>Full Name</label><input className="form-control" value={form.fullName} onChange={e => setForm({...form, fullName: e.target.value})} required /></div>
              <div className="form-group"><label>Phone</label><input className="form-control" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} placeholder="+255700000000" /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>License Number</label><input className="form-control" value={form.licenseNumber} onChange={e => setForm({...form, licenseNumber: e.target.value})} /></div>
              <div className="form-group"><label>License Expiry</label><input className="form-control" type="date" value={form.licenseExpiry} onChange={e => setForm({...form, licenseExpiry: e.target.value})} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>National ID</label><input className="form-control" value={form.nationalId} onChange={e => setForm({...form, nationalId: e.target.value})} /></div>
              <div className="form-group"><label>Daily Rate (TZS)</label><input className="form-control" type="number" value={form.dailyRate} onChange={e => setForm({...form, dailyRate: parseFloat(e.target.value)})} /></div>
            </div>
            <div className="form-group"><label>Address</label><input className="form-control" value={form.address} onChange={e => setForm({...form, address: e.target.value})} /></div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">{editItem ? 'Update' : 'Add Driver'}</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
