import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios.js';
import { setToken } from '../auth/auth.js';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/auth/login', { username, password });
      setToken(res.data.token);
      navigate('/dashboard');
    } catch (err) {
      setError(err?.response?.data?.message || 'Login failed');
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-5">
        <div className="card shadow-sm">
          <div className="card-body">
            <h3 className="card-title mb-3">Sign in</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={submit}>
              <div className="mb-3">
                <label className="form-label">Username</label>
                <input className="form-control" value={username}
                       onChange={(e) => setUsername(e.target.value)} required />
              </div>
              <div className="mb-3">
                <label className="form-label">Password</label>
                <input type="password" className="form-control" value={password}
                       onChange={(e) => setPassword(e.target.value)} required />
              </div>
              <button className="btn btn-primary w-100" type="submit">Login</button>
            </form>
            <p className="mt-3 mb-0 text-center">
              No account? <Link to="/register">Register</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}