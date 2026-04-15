import { createContext, useContext, useState, useEffect } from 'react';
import { getToken, getUserInfo, setToken, setUserInfo, clearAuth } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [authenticated, setAuthenticated] = useState(!!getToken());

  useEffect(() => {
    if (authenticated) {
      setUser(getUserInfo());
    }
  }, [authenticated]);

  const login = (data) => {
    setToken(data.token);
    setUserInfo({ username: data.username, displayName: data.displayName, role: data.role });
    setUser({ username: data.username, displayName: data.displayName, role: data.role });
    setAuthenticated(true);
  };

  const logout = () => {
    clearAuth();
    setUser(null);
    setAuthenticated(false);
  };

  const displayName = user?.displayName || user?.username || '';

  return (
    <AuthContext.Provider value={{ user, authenticated, displayName, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
