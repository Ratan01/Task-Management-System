import { Navigate } from 'react-router-dom';
import { isAuthenticated, isAdmin } from '../auth/auth.js';

export default function AdminRoute({ children }) {
  if (!isAuthenticated()) return <Navigate to="/login" replace />;
  if (!isAdmin()) return <Navigate to="/dashboard" replace />;
  return children;
}