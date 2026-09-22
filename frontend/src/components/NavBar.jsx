import { Link, useNavigate } from 'react-router-dom';
import { isAuthenticated, isAdmin, clearToken, getUsername } from '../auth/auth.js';

export default function NavBar() {
  const navigate = useNavigate();
  if (!isAuthenticated()) return null;

  const logout = () => {
    clearToken();
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container">
        <Link className="navbar-brand" to="/dashboard">Task Manager</Link>
        <div className="navbar-nav me-auto">
          <Link className="nav-link" to="/dashboard">Dashboard</Link>
          {isAdmin() && (
            <>
              <Link className="nav-link" to="/admin/users">Users</Link>
              <Link className="nav-link" to="/admin/tasks">All Tasks</Link>
              <Link className="nav-link" to="/admin/audit">Audit Log</Link>
              <Link className="nav-link" to="/admin/analytics">Analytics</Link>
            </>
          )}
        </div>
        <span className="navbar-text text-white me-3">{getUsername()}</span>
        <button className="btn btn-outline-light btn-sm" onClick={logout}>Logout</button>
      </div>
    </nav>
  );
}