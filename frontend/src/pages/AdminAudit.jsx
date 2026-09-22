import { useEffect, useState } from 'react';
import api from '../api/axios.jsx';

export default function AdminAudit() {
  const [logs, setLogs] = useState([]);
  const [since, setSince] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    setError('');
    try {
      const params = since ? { since: new Date(since).toISOString() } : {};
      const res = await api.get('/audit', { params });
      setLogs(res.data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load audit logs');
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, []);

  return (
    <div className="card shadow-sm">
      <div className="card-body">
        <h5 className="card-title">Audit Log</h5>
        <div className="row g-2 mb-3">
          <div className="col-md-4">
            <input type="datetime-local" className="form-control"
                   value={since} onChange={(e) => setSince(e.target.value)} />
          </div>
          <div className="col-md-2">
            <button className="btn btn-primary w-100" onClick={load}>Filter</button>
          </div>
        </div>
        {error && <div className="alert alert-danger py-2">{error}</div>}
        <div className="table-responsive">
          <table className="table table-sm table-striped align-middle">
            <thead>
              <tr>
                <th>When</th><th>Actor</th><th>Action</th>
                <th>Resource</th><th>Details</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((l) => (
                <tr key={l.id}>
                  <td>{new Date(l.occurredAt).toLocaleString()}</td>
                  <td>{l.actorUsername || l.actorId}</td>
                  <td><span className="badge bg-secondary">{l.action}</span></td>
                  <td>{l.resourceType}{l.resourceId ? `#${l.resourceId}` : ''}</td>
                  <td className="text-truncate" style={{ maxWidth: 320 }}>{l.details}</td>
                </tr>
              ))}
              {logs.length === 0 && (
                <tr><td colSpan="5" className="text-muted text-center">No entries.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
