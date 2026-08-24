import { useState, useEffect } from 'react';
import { getRoutes, createRoute, updateRoute, deleteRoute } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, Edit2, Trash2, Route, MapPin, Navigation } from 'lucide-react';

const fmt = (n) => new Intl.NumberFormat('en-TZ').format(n || 0);

export default function Routes() {
  const [routes, setRoutes] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [form, setForm] = useState({ name: '', startPoint: '', endPoint: '', distanceKm: '', fareAmount: '' });

  const load = () => getRoutes({ size: 200 }).then(r => setRoutes(r.data?.content || [])).catch(console.error);
  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = { ...form, distanceKm: parseFloat(form.distanceKm), fareAmount: parseFloat(form.fareAmount) };
      if (editItem) await updateRoute(editItem.id, data); else await createRoute(data);
      setShowModal(false); setEditItem(null); load();
    } catch (err) { alert(err.message || 'Error saving route'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this route?')) return;
    try { await deleteRoute(id); load(); } catch (err) { alert(err.message || 'Error deleting route'); }
  };

  const openEdit = (r) => { setForm({ name: r.name, startPoint: r.startPoint || '', endPoint: r.endPoint || '', distanceKm: r.distanceKm || '', fareAmount: r.fareAmount || '' }); setEditItem(r); setShowModal(true); };
  const openNew = () => { setForm({ name: '', startPoint: '', endPoint: '', distanceKm: '', fareAmount: '' }); setEditItem(null); setShowModal(true); };

  const columns = [
    { header: 'Route', render: r => (
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{ width: 34, height: 34, borderRadius: 'var(--radius)', background: 'var(--primary-50)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Route size={16} style={{ color: 'var(--primary-600)' }} /></div>
        <span style={{ fontWeight: 600, color: 'var(--slate-900)' }}>{r.name}</span>
      </div>
    )},
    { header: 'Start Point', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><MapPin size={13} style={{ color: 'var(--green-500)' }} /> {r.startPoint || '-'}</span> },
    { header: 'End Point', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Navigation size={13} style={{ color: 'var(--red-500)' }} /> {r.endPoint || '-'}</span> },
    { header: 'Distance', render: r => <span>{r.distanceKm} km</span> },
    { header: 'Fare', render: r => <span style={{ fontWeight: 600 }}>TZS {fmt(r.fareAmount)}</span> },
    { header: 'Status', render: r => <span className={`badge badge-${r.status === 'ACTIVE' ? 'success' : 'neutral'}`}><span className="badge-dot"></span>{r.status}</span> },
    { header: '', render: r => (
      <div className="btn-group">
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => openEdit(r)}><Edit2 size={14} /></button>
        <button className="btn btn-icon btn-ghost btn-sm" style={{ color: 'var(--red-500)' }} onClick={() => handleDelete(r.id)}><Trash2 size={14} /></button>
      </div>
    )}
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div><h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px' }}>Daladala Routes</h2><p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{routes.length} routes</p></div>
        <button className="btn btn-primary" onClick={openNew}><Plus size={16} /> Add Route</button>
      </div>
      <div className="card"><DataTable columns={columns} data={routes} emptyText="No routes defined" /></div>
      {showModal && (
        <Modal title={editItem ? 'Edit Route' : 'Add New Route'} onClose={() => setShowModal(false)}>
          <form onSubmit={handleSubmit}>
            <div className="form-group"><label>Route Name</label><input className="form-control" value={form.name || ''} onChange={e => setForm({...form, name: e.target.value})} required placeholder="Dodoma - Ihumwa" /></div>
            <div className="form-row">
              <div className="form-group"><label>Start Point</label><input className="form-control" value={form.startPoint || ''} onChange={e => setForm({...form, startPoint: e.target.value})} placeholder="Dodoma Town Centre" /></div>
              <div className="form-group"><label>End Point</label><input className="form-control" value={form.endPoint || ''} onChange={e => setForm({...form, endPoint: e.target.value})} placeholder="Ihumwa" /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Distance (km)</label><input className="form-control" type="number" step="0.1" value={form.distanceKm || ''} onChange={e => setForm({...form, distanceKm: e.target.value})} /></div>
              <div className="form-group"><label>Fare (TZS)</label><input className="form-control" type="number" value={form.fareAmount || ''} onChange={e => setForm({...form, fareAmount: e.target.value})} /></div>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">{editItem ? 'Update' : 'Create'}</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
