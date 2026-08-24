import { useState, useEffect } from 'react';
import { getBookings, createBooking, updateBookingStatus, getBookingFinancials, getVehicles, getCustomers, getPayments, addPayment } from '../api/client';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import { Plus, DollarSign, Eye, CheckCircle, Clock, ArrowRight, CreditCard } from 'lucide-react';

const fmt = (n) => new Intl.NumberFormat('en-TZ').format(n || 0);

export default function Bookings() {
  const [bookings, setBookings] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [showFinancials, setShowFinancials] = useState(null);
  const [showPayment, setShowPayment] = useState(null);
  const [payments, setPayments] = useState([]);
  const [financials, setFinancials] = useState(null);
  const [form, setForm] = useState({ vehicleId: '', customerId: '', hireDate: '', endDate: '', destination: '', tripPurpose: '', agreedPrice: '', depositPaid: '' });
  const [paymentForm, setPaymentForm] = useState({ amount: '', paymentMethod: 'MOBILE_MONEY', paymentDate: new Date().toISOString().slice(0, 10), referenceNumber: '', notes: '' });

  const load = () => getBookings({ size: 200 }).then(r => setBookings(r.data?.content || [])).catch(console.error);
  useEffect(() => { load(); }, []);
  useEffect(() => {
    getVehicles({ size: 200 }).then(r => setVehicles(r.data?.content || [])).catch(console.error);
    getCustomers({ size: 200 }).then(r => setCustomers(r.data?.content || [])).catch(console.error);
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await createBooking({ ...form, vehicleId: parseInt(form.vehicleId), customerId: form.customerId ? parseInt(form.customerId) : null, agreedPrice: parseFloat(form.agreedPrice), depositPaid: form.depositPaid ? parseFloat(form.depositPaid) : null });
      setShowCreate(false); load();
    } catch (err) { alert(err.message || 'Failed to create booking'); }
  };

  const handleStatus = async (id, status) => {
    try { await updateBookingStatus(id, status); load(); } catch (err) { alert(err.message || 'Failed to update status'); }
  };

  const viewFinancials = async (id) => {
    try { const f = await getBookingFinancials(id); setFinancials(f.data); setShowFinancials(id); } catch (err) { alert('Failed to load financials'); }
  };
  const viewPayments = async (id) => {
    try { const p = await getPayments(id); setPayments(p.data || []); setShowPayment(id); } catch (err) { alert('Failed to load payments'); }
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    try {
      await addPayment(showPayment, { ...paymentForm, amount: parseFloat(paymentForm.amount) });
      const p = await getPayments(showPayment); setPayments(p.data || []);
      const f = await getBookingFinancials(showPayment); setFinancials(f.data);
      setPaymentForm({ amount: '', paymentMethod: 'MOBILE_MONEY', paymentDate: new Date().toISOString().slice(0, 10), referenceNumber: '', notes: '' });
      load();
    } catch (err) { alert(err.message || 'Failed to record payment'); }
  };

  const statusBadge = (s) => {
    const m = { PENDING: 'warning', CONFIRMED: 'primary', IN_PROGRESS: 'success', COMPLETED: 'success', CANCELLED: 'danger' };
    return <span className={`badge badge-${m[s] || 'neutral'}`}><span className="badge-dot"></span>{s?.replace('_', ' ')}</span>;
  };

  const columns = [
    { header: 'Booking', render: r => <span style={{ fontWeight: 600, color: 'var(--slate-900)' }}>#{r.id}</span> },
    { header: 'Vehicle', render: r => <span style={{ fontWeight: 500 }}>{r.vehicleRegNumber || `Vehicle #${r.vehicleId}`}</span> },
    { header: 'Customer', render: r => r.customerName || '-' },
    { header: 'Destination', render: r => <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><ArrowRight size={13} style={{ color: 'var(--slate-400)' }} /> {r.destination || '-'}</span> },
    { header: 'Price', render: r => <span style={{ fontWeight: 600 }}>TZS {fmt(r.agreedPrice)}</span> },
    { header: 'Paid', render: r => <span style={{ color: (r.totalPaid || 0) > 0 ? 'var(--green-600)' : 'var(--slate-400)' }}>TZS {fmt(r.totalPaid)}</span> },
    { header: 'Status', render: r => statusBadge(r.status) },
    { header: '', render: r => (
      <div className="btn-group">
        {r.status === 'PENDING' && <button className="btn btn-success btn-xs" onClick={() => handleStatus(r.id, 'CONFIRMED')}><CheckCircle size={12} /> Confirm</button>}
        {r.status === 'CONFIRMED' && <button className="btn btn-success btn-xs" onClick={() => handleStatus(r.id, 'IN_PROGRESS')}><Clock size={12} /> Start</button>}
        {r.status === 'IN_PROGRESS' && <button className="btn btn-success btn-xs" onClick={() => handleStatus(r.id, 'COMPLETED')}>Complete</button>}
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => viewFinancials(r.id)} title="Financials"><DollarSign size={14} /></button>
        <button className="btn btn-icon btn-ghost btn-sm" onClick={() => viewPayments(r.id)} title="Payments"><CreditCard size={14} /></button>
      </div>
    )}
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div><h2 style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.5px' }}>Bookings</h2><p style={{ fontSize: 13, color: 'var(--slate-500)' }}>{bookings.length} hire bookings</p></div>
        <button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> New Booking</button>
      </div>
      <div className="card"><DataTable columns={columns} data={bookings} emptyText="No bookings yet" /></div>

      {showCreate && (
        <Modal title="New Hire Booking" onClose={() => setShowCreate(false)}>
          <form onSubmit={handleCreate}>
            <div className="form-row">
              <div className="form-group"><label>Vehicle</label><select className="form-control" value={form.vehicleId} onChange={e => setForm({...form, vehicleId: e.target.value})} required><option value="">Select vehicle</option>{vehicles.filter(v => v.moduleType === 'SPECIAL_HIRE').map(v => <option key={v.id} value={v.id}>{v.regNumber} - {v.make} {v.model}</option>)}</select></div>
              <div className="form-group"><label>Customer</label><select className="form-control" value={form.customerId} onChange={e => setForm({...form, customerId: e.target.value})}><option value="">Select customer</option>{customers.map(c => <option key={c.id} value={c.id}>{c.fullName}</option>)}</select></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Hire Date</label><input className="form-control" type="date" value={form.hireDate} onChange={e => setForm({...form, hireDate: e.target.value})} required /></div>
              <div className="form-group"><label>End Date</label><input className="form-control" type="date" value={form.endDate} onChange={e => setForm({...form, endDate: e.target.value})} /></div>
            </div>
            <div className="form-group"><label>Destination</label><input className="form-control" value={form.destination} onChange={e => setForm({...form, destination: e.target.value})} placeholder="Dar es Salaam" /></div>
            <div className="form-group"><label>Trip Purpose</label><input className="form-control" value={form.tripPurpose} onChange={e => setForm({...form, tripPurpose: e.target.value})} placeholder="Corporate event" /></div>
            <div className="form-row">
              <div className="form-group"><label>Agreed Price (TZS)</label><input className="form-control" type="number" value={form.agreedPrice} onChange={e => setForm({...form, agreedPrice: e.target.value})} required placeholder="1500000" /></div>
              <div className="form-group"><label>Deposit (TZS)</label><input className="form-control" type="number" value={form.depositPaid} onChange={e => setForm({...form, depositPaid: e.target.value})} placeholder="500000" /></div>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={() => setShowCreate(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Create Booking</button>
            </div>
          </form>
        </Modal>
      )}

      {showFinancials && financials && (
        <Modal title={`Booking #${showFinancials} - Financial Summary`} onClose={() => setShowFinancials(null)}>
          <div style={{ display: 'grid', gap: 12 }}>
            {[['Agreed Price', financials.agreedPrice], ['Total Paid', financials.totalPaid]].map(([label, val]) => (
              <div key={label} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid var(--slate-100)' }}>
                <span style={{ color: 'var(--slate-500)', fontWeight: 500 }}>{label}</span>
                <span style={{ fontWeight: 700 }}>TZS {fmt(val)}</span>
              </div>
            ))}
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0' }}>
              <span style={{ fontWeight: 600 }}>Outstanding Balance</span>
              <span style={{ fontWeight: 800, color: 'var(--red-600)', fontSize: 16 }}>TZS {fmt(financials.outstandingBalance)}</span>
            </div>
          </div>
        </Modal>
      )}

      {showPayment && (
        <Modal title={`Payments - Booking #${showPayment}`} onClose={() => setShowPayment(null)}>
          {payments.length > 0 && (
            <div style={{ marginBottom: 20 }}>
              <table style={{ width: '100%' }}>
                <thead><tr><th>Date</th><th>Amount</th><th>Method</th><th>Reference</th></tr></thead>
                <tbody>
                  {payments.map((p, i) => <tr key={i}><td>{p.paymentDate}</td><td style={{ fontWeight: 600 }}>TZS {fmt(p.amount)}</td><td><span className="badge badge-primary">{p.paymentMethod}</span></td><td>{p.referenceNumber || '-'}</td></tr>)}
                </tbody>
              </table>
            </div>
          )}
          <h3 style={{ fontSize: 13, fontWeight: 600, marginBottom: 12, color: 'var(--slate-700)' }}>Record Payment</h3>
          <form onSubmit={handlePayment}>
            <div className="form-row">
              <div className="form-group"><label>Amount (TZS)</label><input className="form-control" type="number" value={paymentForm.amount} onChange={e => setPaymentForm({...paymentForm, amount: e.target.value})} required /></div>
              <div className="form-group"><label>Method</label><select className="form-control" value={paymentForm.paymentMethod} onChange={e => setPaymentForm({...paymentForm, paymentMethod: e.target.value})}><option value="CASH">Cash</option><option value="MOBILE_MONEY">M-Pesa</option><option value="BANK_TRANSFER">Bank Transfer</option></select></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Date</label><input className="form-control" type="date" value={paymentForm.paymentDate} onChange={e => setPaymentForm({...paymentForm, paymentDate: e.target.value})} required /></div>
              <div className="form-group"><label>Reference</label><input className="form-control" value={paymentForm.referenceNumber} onChange={e => setPaymentForm({...paymentForm, referenceNumber: e.target.value})} placeholder="MP260824" /></div>
            </div>
            <button type="submit" className="btn btn-primary">Record Payment</button>
          </form>
        </Modal>
      )}
    </div>
  );
}
