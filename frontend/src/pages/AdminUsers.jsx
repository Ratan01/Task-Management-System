import { useEffect, useState } from 'react';
import api from '../api/axios.jsx';

const empty = { username: '', email: '', role: 'USER' };

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const res = await api.get('/users');
      setUsers(res.data);
    } catch {
      setError('Failed to load users');
    }
  };

  useEffect(() => { load(); }, []);

  const save = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (editingId) {
        await api.put(`/users/${editingId}`, { email: form.email, role: form.role });
      } else {
        await api.post('/users', form);
      }
      setForm(empty);
      setEditingId(null);
      load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Save failed');
    }
  };

  const edit = (u) => {
    setEditingId(u.id);
    setForm({ username: u.username, email: u.email || '', role: u.role });
  };

  const remove = async (id) => {
    if (!confirm('Delete user?')) return;
    await api.delete(`/users/${id}`);
    load();
  };

  return (
    <div className="row g-4">
      <div className="col-lg-4">
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">{editingId ? 'Edit user' : 'Create user'}</h5>
            {error && <div className="alert alert-danger py-2">{error}</div>}
            <form onSubmit={save}>
              <div className="mb-2">
                <input className="form-control" placeholder="Username"
                       value={form.username}
                       onChange={(e) => setForm({ ...form, username: e.target.value })}
                       disabled={!!editingId} required />
              </div>
              <div className="mb-2">
                <input className="form-control" placeholder="Email" type="email"
                       value={form.email}
                       onChange={(e) => setForm({ ...form, email: e.target.value })} />
              </div>
              <div className="mb-2">
                <select className="form-select" value={form.role}
                        onChange={(e) => setForm({ ...form, role: e.target.value })}>
                  <option value="USER">USER</option>
                  <option value="ADMIN">ADMIN</option>
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
            <h5 className="card-title">Users</h5>
            <div className="table-responsive">
              <table className="table table-striped align-middle">
                <thead><tr><th>ID</th><th>Username</th><th>Email</th><th>Role</th><th></th></tr></thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.id}>
                      <td>{u.id}</td>
                      <td>{u.username}</td>
                      <td>{u.email || '—'}</td>
                      <td><span className="badge bg-info text-dark">{u.role}</span></td>
                      <td className="text-end">
                        <button className="btn btn-sm btn-outline-primary me-1"
                                onClick={() => edit(u)}>Edit</button>
                        <button className="btn btn-sm btn-outline-danger"
                                onClick={() => remove(u.id)}>Delete</button>
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
