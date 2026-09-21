export function getToken() {
  return localStorage.getItem('token');
}

export function setToken(token) {
  localStorage.setItem('token', token);
}

export function clearToken() {
  localStorage.removeItem('token');
}

export function decodeToken() {
  const token = getToken();
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload;
  } catch {
    return null;
  }
}

export function isAuthenticated() {
  const payload = decodeToken();
  if (!payload) return false;
  if (payload.exp && payload.exp * 1000 < Date.now()) {
    clearToken();
    return false;
  }
  return true;
}

export function isAdmin() {
  const payload = decodeToken();
  return payload?.role === 'ADMIN';
}

export function getUserId() {
  return decodeToken()?.userId;
}

export function getUsername() {
  return decodeToken()?.username;
}