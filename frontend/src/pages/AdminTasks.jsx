import { useEffect, useState } from 'react';
import api from '../api/axios.js';

const empty = { title: '', description: '', dueDate: '', status: 'PENDING', ownerId: '' };

export default function AdminTasks() {
  const [tasks, setTasks] = useState([]);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const res = await api.get('/tasks');
      setTasks(res.data);
    } catch {
      setError('Failed to load tasks');
    }
  };

  useEffect(() => { load(); }, []);

  const save = async (e) => {
    e.preventDefault();
    setError('');
    const payload = {
      title: form.title,
      description: form.description,
      dueDate: form.dueDate || null,
      status: form.status,
      ownerId: form.ownerId ? Number(form.ownerId) : null
    };
    try {
      if (editingId) {
        await api.put(`/tasks/${editingId}`, payload);
      } else {
        await api.post('/tasks', payload);
      }
      setForm(empty);
      setEditingId(null);
      load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Save failed');
    }
  };

  const edit = (t) => {
    setEditingId(t.id);
    setForm({
      title: t.title, description: t.description || '',
      dueDate: t.dueDate || '', status: t.status,
      ownerId: t.ownerId ?? ''
    });
  };

  const remove = async (id) => {
    if (!confirm('Delete task?')) return;
    await api.delete(`/tasks/${id}`);
    load();
  };

  return (
    <div className="row g-4">
      <div className="col-lg-4">
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
                <label className="form-label small mb-0">Owner ID</label>
                <input className="form-control" type="number"
                       value={form.ownerId}
                       onChange={(e) => setForm({ ...form, ownerId: e.target.value })} />
              </div>
              <div className="mb-2">
                <label className="form-label small mb-0">Due date</label>
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

      <div className="col-lg-8">
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">All tasks</h5>
            <div className="table-responsive">
              <table className="table table-striped align-middle">
                <thead><tr><th>ID</th><th>Title</th><th>Owner</th><th>Due</th><th>Status</th><th></th></tr></thead>
                <tbody>
                  {tasks.map((t) => (
                    <tr key={t.id}>
                      <td>{t.id}</td>
                      <td>{t.title}</td>
                      <td>{t.ownerId}</td>
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
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}