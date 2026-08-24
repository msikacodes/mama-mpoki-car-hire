import { useState, useEffect } from 'react';
import { getTrips, getBookings, getDrivers, createTrip, completeTrip, addTripExpense } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, CheckCircle, DollarSign, MapPin, Calendar, Gauge } from 'lucide-react';

export default function Trips() {
  const [trips, setTrips] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [showExpense, setShowExpense] = useState(null);
  const [form, setForm] = useState({ bookingId: '', driverId: '', startDate: '', destination: '', odometerStart: '' });
  const [expForm, setExpForm] = useState({ expenseType: 'FUEL', amount: '', description: '', expenseDate: new Date().toISOString().slice(0, 10) });

  const load = () => getTrips({ size: 200 }).then(r => setTrips(r.data?.content || [])).catch(console.error);
  useEffect(() => { load(); }, []);
  useEffect(() => {
    getBookings({ size: 200 }).then(r => setBookings(r.data?.content || [])).catch(console.error);
    getDrivers({ size: 200 }).then(r => setDrivers(r.data?.content || [])).catch(console.error);
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await createTrip({ ...form, bookingId: parseInt(form.bookingId), driverId: parseInt(form.driverId), odometerStart: form.odometerStart ? parseInt(form.odometerStart) : null });
      setShowCreate(false); load();
    } catch (err) { alert(err.message || 'Failed to create trip'); }
  };

  const handleComplete = async (id) => {
    const endOdometer = prompt('Enter end odometer reading:');
    if (endOdometer === null) return;
    const odometer = parseInt(endOdometer);
    if (isNaN(odometer)) { alert('Please enter a valid number'); return; }
    try {
      await completeTrip(id, { odometerEnd: odometer, endDate: new Date().toISOString().slice(0, 10) });
      load();
    } catch (err) { alert(err.message || 'Failed to complete trip'); }
  };

  const handleExpense = async (e) => {
    e.preventDefault();
    try {
      await addTripExpense(showExpense, { ...expForm, amount: parseFloat(expForm.amount) });
      setShowExpense(null); load();
    } catch (err) { alert(err.message || 'Failed to add expense'); }
  };

  const statusBadge = (s) => {
    const m = { SCHEDULED: 'warning', IN_PROGRESS: 'primary', COMPLETED: 'success', CANCELLED: 'danger' };
    return <span className={`badge badge-${m[s] || 'neutral'}`}><span className="badge-dot"></span>{s}</span>;
  };

  const columns = [
    { header: 'Trip', render: r => <span style={{ fontWeight: 600 }}>#{r.id}</span> },
    { header: 'Booking', render: r => `#${r.bookingId}` },
    { header: 'Driver', render: r => r.driverName || `Driver #${r.driverId}` },
    { header: 'Destination', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><MapPin size={13} style={{ color: 'var(--slate-400)' }} /> {r.destination || '-'}</span> },
    { header: 'Date', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Calendar size={13} style={{ color: 'var(--slate-400)' }} /> {r.startDate || '-'}</span> },
    { header: 'Odometer', render: r => r.odometerStart ? <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Gauge size={13} style={{ color: 'var(--slate-400)' }} /> {r.odometerStart} - {r.odometerEnd || '?'}</span> : '-' },
    { header: 'Status', render: r => statusBadge(r.status) },
    { header: '', render: r => (
      <div className="btn-group">
        {r.status === 'SCHEDULED' && <button className="btn btn-success btn-xs" onClick={() => handleComplete(r.id)}><CheckCircle size={12} /> Complete</button>}
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => setShowExpense(r.id)} title="Add Expense"><DollarSign size={14} /></button>
      </div>
    )}
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div><h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px' }}>Trips</h2><p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{trips.length} trips</p></div>
        <button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> New Trip</button>
      </div>
      <div className="card"><DataTable columns={columns} data={trips} emptyText="No trips yet" /></div>

      {showCreate && (
        <Modal title="Create New Trip" onClose={() => setShowCreate(false)}>
          <form onSubmit={handleCreate}>
            <div className="form-group"><label>Booking</label><select className="form-control" value={form.bookingId} onChange={e => setForm({...form, bookingId: e.target.value})} required><option value="">Select booking</option>{bookings.filter(b => b.status !== 'CANCELLED').map(b => <option key={b.id} value={b.id}>#{b.id} - {b.destination || 'No destination'}</option>)}</select></div>
            <div className="form-group"><label>Driver</label><select className="form-control" value={form.driverId} onChange={e => setForm({...form, driverId: e.target.value})} required><option value="">Select driver</option>{drivers.map(d => <option key={d.id} value={d.id}>{d.fullName}</option>)}</select></div>
            <div className="form-row">
              <div className="form-group"><label>Start Date</label><input className="form-control" type="date" value={form.startDate} onChange={e => setForm({...form, startDate: e.target.value})} required /></div>
              <div className="form-group"><label>Start Odometer</label><input className="form-control" type="number" value={form.odometerStart} onChange={e => setForm({...form, odometerStart: e.target.value})} placeholder="100000" /></div>
            </div>
            <div className="form-group"><label>Destination</label><input className="form-control" value={form.destination} onChange={e => setForm({...form, destination: e.target.value})} placeholder="Dar es Salaam" /></div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowCreate(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Create Trip</button>
            </div>
          </form>
        </Modal>
      )}

      {showExpense && (
        <Modal title="Add Trip Expense" onClose={() => setShowExpense(null)}>
          <form onSubmit={handleExpense}>
            <div className="form-row">
              <div className="form-group"><label>Category</label><select className="form-control" value={expForm.expenseType} onChange={e => setExpForm({...expForm, expenseType: e.target.value})}>{['FUEL', 'DRIVER_ALLOWANCE', 'TOLL', 'FOOD', 'ACCOMMODATION', 'REPAIR', 'OTHER'].map(t => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}</select></div>
              <div className="form-group"><label>Amount (TZS)</label><input className="form-control" type="number" value={expForm.amount} onChange={e => setExpForm({...expForm, amount: e.target.value})} required /></div>
            </div>
            <div className="form-group"><label>Description</label><input className="form-control" value={expForm.description} onChange={e => setExpForm({...expForm, description: e.target.value})} /></div>
            <div className="form-group"><label>Date</label><input className="form-control" type="date" value={expForm.expenseDate} onChange={e => setExpForm({...expForm, expenseDate: e.target.value})} /></div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowExpense(null)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Add Expense</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
