import { useEffect, useState } from 'react';
import api from '../api/axios.js';

const empty = { title: '', description: '', dueDate: '', status: 'PENDING' };

export default function Dashboard() {
  const [tasks, setTasks] = useState([]);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [error, setError] = useState('');

  const loadTasks = async () => {
    try {
      const res = await api.get('/tasks');
      setTasks(res.data);
    } catch (err) {
      setError('Failed to load tasks');
    }
  };

  const loadNotifications = async () => {
    try {
      const res = await api.get('/notifications/due-today');
      setNotifications(res.data);
    } catch {
      // ignore
    }
  };

  useEffect(() => {
    loadTasks();
    loadNotifications();
  }, []);

  const save = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const payload = { ...form };
      if (!payload.dueDate) payload.dueDate = null;
      if (editingId) {
        await api.put(`/tasks/${editingId}`, payload);
      } else {
        await api.post('/tasks', payload);
      }
      setForm(empty);
      setEditingId(null);
      loadTasks();
    } catch (err) {
      setError(err?.response?.data?.message || 'Save failed');
    }
  };

  const edit = (t) => {
    setEditingId(t.id);
    setForm({
      title: t.title, description: t.description || '',
      dueDate: t.dueDate || '', status: t.status
    });
  };

  const remove = async (id) => {
    if (!confirm('Delete this task?')) return;
    await api.delete(`/tasks/${id}`);
    loadTasks();
  };

  return (
    <div className="row g-4">
      <div className="col-lg-5">
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">{editingId ? 'Edit task' : 'New task'}</h5>
            {error && <div className="alert alert-danger py-2">{error}</div>}
            <form onSubmit={save}>
              <div className="mb-2">
                <input className="form-control" placeholder="Title"
                       value={form.title}
                       onChange={(e) => setForm({ ...form, title: e.target.value })} required />
              </div>
              <div className="mb-2">
                <textarea className="form-control" placeholder="Description"
                          value={form.description}
                          onChange={(e) => setForm({ ...form, description: e.target.value })} />
              </div>
              <div className="mb-2">
                <input type="date" className="form-control"
                       value={form.dueDate}
                       onChange={(e) => setForm({ ...form, dueDate: e.target.value })} />
              </div>
              <div className="mb-2">
                <select className="form-select" value={form.status}
                        onChange={(e) => setForm({ ...form, status: e.target.value })}>
                  <option value="PENDING">PENDING</option>
                  <option value="IN_PROGRESS">IN_PROGRESS</option>
                  <option value="COMPLETED">COMPLETED</option>
                </select>
              </div>
              <button className="btn btn-primary w-100" type="submit">
                {editingId ? 'Update' : 'Create'}
              </button>
              {editingId && (
                <button type="button" className="btn btn-secondary w-100 mt-2"
                        onClick={() => { setEditingId(null); setForm(empty); }}>
                  Cancel
                </button>
              )}
            </form>
          </div>
        </div>
      </div>

      <div className="col-lg-7">
        <div className="card shadow-sm mb-3">
          <div className="card-body">
            <h5 className="card-title">Due today / overdue</h5>
            {notifications.length === 0 ? (
              <p className="text-muted mb-0">No notifications.</p>
            ) : (
              <ul className="list-group list-group-flush">
                {notifications.map((n) => (
                  <li key={n.id} className="list-group-item">
                    <strong>{n.title}</strong> — due {n.dueDate} ({n.status})
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>

        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">My tasks</h5>
            <div className="table-responsive">
              <table className="table table-striped align-middle">
                <thead>
                  <tr>
                    <th>Title</th><th>Due</th><th>Status</th><th></th>
                  </tr>
                </thead>
                <tbody>
                  {tasks.map((t) => (
                    <tr key={t.id}>
                      <td>{t.title}</td>
                      <td>{t.dueDate || '—'}</td>
                      <td><span className="badge bg-secondary">{t.status}</span></td>
                      <td className="text-end">
                        <button className="btn btn-sm btn-outline-primary me-1"
                                onClick={() => edit(t)}>Edit</button>
                        <button className="btn btn-sm btn-outline-danger"
                                onClick={() => remove(t.id)}>Delete</button>
                      </td>
                    </tr>
                  ))}
                  {tasks.length === 0 && (
                    <tr><td colSpan="4" className="text-muted text-center">No tasks yet.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}