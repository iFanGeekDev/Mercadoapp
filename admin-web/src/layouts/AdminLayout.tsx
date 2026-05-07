import React from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Package, 
  ClipboardCheck, 
  Settings, 
  LogOut, 
  ChevronRight,
  User
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const AdminLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    { icon: LayoutDashboard, label: 'Dashboard', path: '/dashboard' },
    { icon: Package, label: 'Productos', path: '/productos' },
    { icon: ClipboardCheck, label: 'Inspecciones', path: '/inspecciones' },
    { icon: Settings, label: 'Ajustes', path: '/ajustes' },
  ];

  return (
    <div className="flex min-h-screen bg-dark-900 text-white">
      {/* Sidebar */}
      <aside className="w-72 bg-dark-800 border-r border-dark-700 flex flex-col">
        <div className="p-8">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-brand-500 rounded-xl flex items-center justify-center font-black text-xl">M</div>
            <span className="font-bold text-xl tracking-tight">YapaMarket <span className="text-brand-400">Admin</span></span>
          </div>
        </div>

        <nav className="flex-1 px-4 space-y-2">
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${
                  isActive 
                  ? 'bg-brand-500/10 text-brand-400 border border-brand-500/20' 
                  : 'text-dark-400 hover:bg-dark-700/50 hover:text-white'
                }`}
              >
                <item.icon className="w-5 h-5" />
                <span className="font-medium">{item.label}</span>
                {isActive && <ChevronRight className="w-4 h-4 ml-auto" />}
              </Link>
            );
          })}
        </nav>

        <div className="p-4 border-t border-dark-700">
          <div className="bg-dark-900/50 rounded-2xl p-4 mb-4 border border-dark-700">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-dark-700 rounded-full flex items-center justify-center overflow-hidden">
                {user?.avatar_url ? (
                  <img src={user.avatar_url} alt={user.name} className="w-full h-full object-cover" />
                ) : (
                  <User className="w-5 h-5 text-brand-400" />
                )}
              </div>
              <div className="overflow-hidden">
                <p className="text-sm font-bold truncate">{user?.name}</p>
                <p className="text-xs text-dark-500 truncate">{user?.email}</p>
              </div>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-400 hover:bg-red-500/10 transition-all"
          >
            <LogOut className="w-5 h-5" />
            <span className="font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col overflow-hidden">
        <header className="h-20 bg-dark-800/50 backdrop-blur-md border-b border-dark-700 flex items-center justify-between px-10">
          <h2 className="text-xl font-bold text-white">
            {menuItems.find(i => i.path === location.pathname)?.label || 'Panel'}
          </h2>
          <div className="flex items-center gap-4">
            <div className="h-10 w-10 bg-dark-700 rounded-xl flex items-center justify-center">
              <span className="text-xs font-bold text-accent-500">LIVE</span>
            </div>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto p-10">
          {children}
        </div>
      </main>
    </div>
  );
};

export default AdminLayout;
