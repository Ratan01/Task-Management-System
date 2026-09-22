import { useEffect, useState } from 'react';
import api from '../api/axios.jsx';

export default function AdminAnalytics() {
  const [metrics, setMetrics] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/analytics/metrics');
      setMetrics(res.data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load metrics');
    } finally {
      setLoading(false);
    }
  };

  const refresh = async () => {
    setLoading(true);
    try {
      const res = await api.post('/analytics/refresh');
      setMetrics(res.data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Refresh failed');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 className="mb-0">Analytics</h4>
        <button className="btn btn-outline-primary btn-sm" onClick={refresh} disabled={loading}>
          {loading ? 'Refreshing…' : 'Force refresh'}
        </button>
      </div>
      {error && <div className="alert alert-danger py-2">{error}</div>}
      {metrics && (
        <>
          <div className="row g-3 mb-3">
            <div className="col-md-3">
              <div className="card text-center"><div className="card-body">
                <div className="display-6">{metrics.totalUsers}</div>
                <div className="text-muted">Users</div>
              </div></div>
            </div>
            <div className="col-md-3">
              <div className="card text-center"><div className="card-body">
                <div className="display-6">{metrics.totalTasks}</div>
                <div className="text-muted">Tasks</div>
              </div></div>
            </div>
            <div className="col-md-3">
              <div className="card text-center"><div className="card-body">
                <div className="display-6">{metrics.tasksDueTodayOrOverdue}</div>
                <div className="text-muted">Due / overdue</div>
              </div></div>
            </div>
            <div className="col-md-3">
              <div className="card text-center"><div className="card-body">
                <div className="display-6">
                  {Object.values(metrics.auditCountsLast7Days || {}).reduce((a, b) => a + b, 0)}
                </div>
                <div className="text-muted">Audit events (7d)</div>
              </div></div>
            </div>
          </div>
          <div className="row g-3">
            <div className="col-md-6">
              <div className="card"><div className="card-body">
                <h6>Tasks by status</h6>
                <ul className="list-group list-group-flush">
                  {Object.entries(metrics.tasksByStatus || {}).map(([k, v]) => (
                    <li key={k} className="list-group-item d-flex justify-content-between">
                      <span>{k}</span><strong>{v}</strong>
                    </li>
                  ))}
                </ul>
              </div></div>
            </div>
            <div className="col-md-6">
              <div className="card"><div className="card-body">
                <h6>Audit events (last 7 days)</h6>
                <ul className="list-group list-group-flush">
                  {Object.entries(metrics.auditCountsLast7Days || {}).map(([k, v]) => (
                    <li key={k} className="list-group-item d-flex justify-content-between">
                      <span>{k}</span><strong>{v}</strong>
                    </li>
                  ))}
                </ul>
              </div></div>
            </div>
          </div>
          <p className="text-muted small mt-3 mb-0">
            Generated at {new Date(metrics.generatedAt).toLocaleString()}
          </p>
        </>
      )}
    </div>
  );
}
