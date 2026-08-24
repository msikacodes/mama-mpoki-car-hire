import { useState, useEffect } from 'react';
import { getConductors, createConductor, updateConductor, deleteConductor } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, Edit2, Trash2, Search, UserCheck, Phone } from 'lucide-react';

export default function Conductors() {
  const [list, setList] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [search, setSearch] = useState('');
  const [form, setForm] = useState({ fullName: '', phone: '', nationalId: '', address: '', dailyRate: 15000 });

  const load = () => getConductors({ size: 200 }).then(r => setList(r.data?.content || [])).catch(console.error);
  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editItem) await updateConductor(editItem.id, form); else await createConductor(form);
      setShowModal(false); setEditItem(null); load();
    } catch (err) { alert(err.message || 'Error saving conductor'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this conductor?')) return;
    try { await deleteConductor(id); load(); } catch (err) { alert(err.message || 'Error deleting conductor'); }
  };

  const openEdit = (c) => { setForm({ fullName: c.fullName, phone: c.phone || '', nationalId: c.nationalId || '', address: c.address || '', dailyRate: c.dailyRate || 15000 }); setEditItem(c); setShowModal(true); };
  const openNew = () => { setForm({ fullName: '', phone: '', nationalId: '', address: '', dailyRate: 15000 }); setEditItem(null); setShowModal(true); };

  const filtered = list.filter(c => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (c.fullName || '').toLowerCase().includes(q) || (c.phone || '').includes(q);
  });

  const columns = [
    { header: 'Conductor', render: r => (
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{ width: 34, height: 34, borderRadius: '50%', background: 'var(--amber-50)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: 13, color: 'var(--amber-600)' }}>{(r.fullName || 'C')[0].toUpperCase()}</div>
        <div>
          <div style={{ fontWeight: 600, color: 'var(--slate-900)' }}>{r.fullName}</div>
          <div style={{ fontSize: 11, color: 'var(--slate-400)' }}>{r.nationalId || 'No ID'}</div>
        </div>
      </div>
    )},
    { header: 'Phone', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Phone size={13} style={{ color: 'var(--slate-400)' }} /> {r.phone || '-'}</span> },
    { header: 'Address', accessor: 'address' },
    { header: 'Daily Rate', render: r => <span style={{ fontWeight: 600 }}>TZS {new Intl.NumberFormat().format(r.dailyRate || 0)}</span> },
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
        <div><h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px' }}>Conductors</h2><p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{list.length} conductors</p></div>
        <button className="btn btn-primary" onClick={openNew}><Plus size={16} /> Add Conductor</button>
      </div>
      <div className="card">
        <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--slate-100)' }}>
          <div style={{ position: 'relative', maxWidth: 300 }}><Search size={15} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--slate-400)' }} /><input className="form-control" style={{ paddingLeft: 34 }} placeholder="Search..." value={search} onChange={e => setSearch(e.target.value)} /></div>
        </div>
        <DataTable columns={columns} data={filtered} emptyText="No conductors found" />
      </div>
      {showModal && (
        <Modal title={editItem ? 'Edit Conductor' : 'Add New Conductor'} onClose={() => setShowModal(false)}>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group"><label>Full Name</label><input className="form-control" value={form.fullName || ''} onChange={e => setForm({...form, fullName: e.target.value})} required /></div>
              <div className="form-group"><label>Phone</label><input className="form-control" value={form.phone || ''} onChange={e => setForm({...form, phone: e.target.value})} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>National ID</label><input className="form-control" value={form.nationalId || ''} onChange={e => setForm({...form, nationalId: e.target.value})} /></div>
              <div className="form-group"><label>Daily Rate (TZS)</label><input className="form-control" type="number" value={form.dailyRate || 15000} onChange={e => setForm({...form, dailyRate: parseFloat(e.target.value)})} /></div>
            </div>
            <div className="form-group"><label>Address</label><input className="form-control" value={form.address || ''} onChange={e => setForm({...form, address: e.target.value})} /></div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">{editItem ? 'Update' : 'Add'}</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
