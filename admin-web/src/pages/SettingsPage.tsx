import React, { useState } from 'react';
import { 
  User, 
  Lock, 
  Globe, 
  Bell, 
  Shield, 
  Camera, 
  Save,
  Percent,
  AlertTriangle,
  Mail,
  Smartphone,
  ChevronRight,
  Loader2,
  CheckCircle2,
  XCircle
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';

const SettingsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'perfil' | 'marketplace' | 'seguridad' | 'notificaciones'>('perfil');

  const tabs = [
    { id: 'perfil', label: 'Cuenta', icon: User },
    { id: 'marketplace', label: 'Marketplace', icon: Globe },
    { id: 'seguridad', label: 'Seguridad', icon: Shield },
    { id: 'notificaciones', label: 'Notificaciones', icon: Bell },
  ];

  return (
    <div className="max-w-5xl mx-auto space-y-8 animate-in fade-in duration-500">
      {/* Header & Tabs */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 border-b border-dark-700 pb-2">
        <div className="flex items-center gap-2 overflow-x-auto no-scrollbar">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center gap-2 px-6 py-4 font-bold text-sm transition-all relative whitespace-nowrap ${
                activeTab === tab.id 
                ? 'text-brand-400' 
                : 'text-dark-400 hover:text-white'
              }`}
            >
              <tab.icon className={`w-4 h-4 ${activeTab === tab.id ? 'text-brand-400' : 'text-dark-500'}`} />
              {tab.label}
              {activeTab === tab.id && (
                <div className="absolute bottom-0 left-0 right-0 h-1 bg-brand-500 rounded-t-full shadow-[0_0_15px_rgba(108,99,255,0.5)]" />
              )}
            </button>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Form Content */}
        <div className="lg:col-span-2 space-y-6">
          {activeTab === 'perfil' && <AccountSection />}
          {activeTab === 'marketplace' && <MarketplaceSection />}
          {activeTab === 'seguridad' && <SecuritySection />}
          {activeTab === 'notificaciones' && <NotificationsSection />}
        </div>

        {/* Sidebar Info */}
        <div className="space-y-6">
          <div className="bg-dark-800 border border-dark-700 rounded-3xl p-6 shadow-xl">
            <h4 className="font-bold text-white mb-4 flex items-center gap-2">
              <Shield className="w-4 h-4 text-accent-500" />
              Estado del Sistema
            </h4>
            <div className="space-y-4">
              <div className="flex justify-between items-center text-sm">
                <span className="text-dark-400">Versión del Panel</span>
                <span className="text-white font-medium">v2.4.0</span>
              </div>
              <div className="flex justify-between items-center text-sm">
                <span className="text-dark-400">Último backup</span>
                <span className="text-white font-medium">Hace 2 horas</span>
              </div>
              <div className="flex justify-between items-center text-sm">
                <span className="text-dark-400">Database</span>
                <span className="text-accent-500 font-bold flex items-center gap-1">
                  <div className="w-1.5 h-1.5 bg-accent-500 rounded-full" />
                  Conectado
                </span>
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-brand-600/20 to-brand-500/5 border border-brand-500/20 rounded-3xl p-6 shadow-xl relative overflow-hidden group">
            <div className="absolute top-0 right-0 p-4 opacity-10 group-hover:scale-110 transition-transform">
              <Globe className="w-20 h-20" />
            </div>
            <h4 className="font-bold text-white mb-2 relative z-10">¿Necesitas ayuda?</h4>
            <p className="text-sm text-dark-300 mb-4 relative z-10">Consulta la documentación técnica para administradores de YapaMarket.</p>
            <button className="text-xs font-bold text-brand-400 flex items-center gap-1 hover:gap-2 transition-all relative z-10">
              Ir al centro de soporte <ChevronRight className="w-3 h-3" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

const SectionHeader = ({ title, description }: { title: string, description: string }) => (
  <div className="mb-6">
    <h3 className="text-xl font-bold text-white">{title}</h3>
    <p className="text-dark-400 text-sm">{description}</p>
  </div>
);

const AccountSection = () => {
  const { user, updateUser } = useAuth();
  const [isUploading, setIsUploading] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [status, setStatus] = useState<{ type: 'success' | 'error' | 'idle', message?: string }>({ type: 'idle' });
  
  const [name, setName] = useState(user?.name || '');
  const [email, setEmail] = useState(user?.email || '');

  const fileInputRef = React.useRef<HTMLInputElement>(null);

  const handleAvatarClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validar tamaño (máximo 800KB como dice el UI)
    if (file.size > 800 * 1024) {
      setStatus({ type: 'error', message: 'El archivo es demasiado grande (Máx 800KB)' });
      return;
    }

    const formData = new FormData();
    formData.append('avatar', file);

    setIsUploading(true);
    setStatus({ type: 'idle' });

    try {
      const response = await api.post('/users/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      updateUser(response.data);
      setStatus({ type: 'success', message: 'Foto de perfil actualizada' });
    } catch (error: any) {
      setStatus({ type: 'error', message: 'Error al subir la imagen' });
    } finally {
      setIsUploading(false);
    }
  };

  const handleProfileUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsUpdating(true);
    setStatus({ type: 'idle' });

    try {
      const response = await api.put('/users/profile', { name, email });
      updateUser(response.data);
      setStatus({ type: 'success', message: 'Información actualizada correctamente' });
    } catch (error: any) {
      const msg = error.response?.data?.error || 'Error al actualizar el perfil';
      setStatus({ type: 'error', message: msg });
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="bg-dark-800 border border-dark-700 rounded-3xl p-8 shadow-xl">
      <SectionHeader title="Perfil del Administrador" description="Gestiona la información pública de tu cuenta corporativa." />
      
      {status.type !== 'idle' && (
        <div className={`mb-6 p-4 rounded-2xl flex items-center gap-3 animate-in slide-in-from-top-2 ${
          status.type === 'success' ? 'bg-accent-500/10 text-accent-500 border border-accent-500/20' : 'bg-red-500/10 text-red-400 border border-red-500/20'
        }`}>
          {status.type === 'success' ? <CheckCircle2 className="w-5 h-5" /> : <XCircle className="w-5 h-5" />}
          <span className="text-sm font-bold">{status.message}</span>
        </div>
      )}

      <div className="flex items-center gap-8 mb-8 pb-8 border-b border-dark-700/50">
        <div className="relative group cursor-pointer" onClick={handleAvatarClick}>
          <div className="w-24 h-24 bg-dark-700 rounded-full flex items-center justify-center border-2 border-dashed border-dark-600 group-hover:border-brand-500 transition-colors overflow-hidden">
            {isUploading ? (
              <Loader2 className="w-8 h-8 text-brand-400 animate-spin" />
            ) : user?.avatar_url ? (
              <img src={user.avatar_url} className="w-full h-full object-cover" alt="Profile" />
            ) : (
              <User className="w-10 h-10 text-dark-500" />
            )}
          </div>
          <div className="absolute bottom-0 right-0 bg-brand-500 p-2 rounded-full shadow-lg border-4 border-dark-800 group-hover:scale-110 transition-transform">
            <Camera className="w-4 h-4 text-white" />
          </div>
          <input 
            type="file" 
            ref={fileInputRef} 
            onChange={handleFileChange} 
            className="hidden" 
            accept="image/*" 
          />
        </div>
        <div>
          <h4 className="font-bold text-white mb-1">Tu Avatar</h4>
          <p className="text-xs text-dark-400 mb-3">JPG o PNG. Tamaño máximo 800KB.</p>
          <button 
            onClick={handleAvatarClick}
            disabled={isUploading}
            className="text-xs font-bold text-brand-400 hover:text-brand-300 transition-colors disabled:opacity-50"
          >
            {isUploading ? 'Subiendo...' : 'Subir nueva imagen'}
          </button>
        </div>
      </div>

      <form className="space-y-6" onSubmit={handleProfileUpdate}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <label className="text-sm font-bold text-dark-400">Nombre Completo</label>
            <input 
              type="text" 
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full bg-dark-900 border border-dark-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-brand-500 transition-all text-white font-medium"
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-bold text-dark-400">Correo Electrónico</label>
            <input 
              type="email" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-dark-900 border border-dark-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-brand-500 transition-all text-white font-medium"
            />
          </div>
        </div>
        <button 
          type="submit"
          disabled={isUpdating}
          className="bg-brand-500 hover:bg-brand-600 disabled:opacity-50 text-white font-bold px-8 py-3 rounded-xl flex items-center gap-2 transition-all shadow-lg shadow-brand-500/20"
        >
          {isUpdating ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />} 
          Guardar Cambios
        </button>
      </form>
    </div>
  );
};

const MarketplaceSection = () => (
  <div className="bg-dark-800 border border-dark-700 rounded-3xl p-8 shadow-xl">
    <SectionHeader title="Ajustes del Marketplace" description="Controla los parámetros globales de la aplicación." />
    
    <div className="space-y-8">
      <div className="flex items-center justify-between p-6 bg-dark-900/50 rounded-2xl border border-dark-700">
        <div className="flex gap-4">
          <div className="p-3 bg-red-500/10 rounded-xl">
            <AlertTriangle className="w-6 h-6 text-red-400" />
          </div>
          <div>
            <h4 className="font-bold text-white">Modo Mantenimiento</h4>
            <p className="text-xs text-dark-400">Bloquea el acceso a la app para usuarios externos.</p>
          </div>
        </div>
        <label className="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" className="sr-only peer" />
          <div className="w-11 h-6 bg-dark-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-red-500"></div>
        </label>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="space-y-2">
          <label className="text-sm font-bold text-dark-400 flex items-center gap-2">
            <Percent className="w-4 h-4 text-brand-400" /> Comisión por Venta
          </label>
          <div className="relative">
            <input 
              type="number" 
              defaultValue="5" 
              className="w-full bg-dark-900 border border-dark-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-brand-500 transition-all text-white font-medium pl-10"
            />
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-dark-500 font-bold">%</span>
          </div>
        </div>
        <div className="space-y-2">
          <label className="text-sm font-bold text-dark-400">Moneda por Defecto</label>
          <select className="w-full bg-dark-900 border border-dark-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-brand-500 transition-all text-white font-medium appearance-none">
            <option>Soles (PEN)</option>
            <option>Dólares (USD)</option>
          </select>
        </div>
      </div>
      
      <button className="bg-brand-500 hover:bg-brand-600 text-white font-bold px-8 py-3 rounded-xl flex items-center gap-2 transition-all shadow-lg shadow-brand-500/20">
        <Save className="w-4 h-4" /> Actualizar Globales
      </button>
    </div>
  </div>
);

const SecuritySection = () => {
  const [formData, setFormData] = useState({
    old_password: '',
    new_password: '',
    confirm_password: ''
  });
  const [status, setStatus] = useState<{ type: 'idle' | 'loading' | 'success' | 'error', message?: string }>({ type: 'idle' });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (formData.new_password !== formData.confirm_password) {
      setStatus({ type: 'error', message: 'Las contraseñas nuevas no coinciden' });
      return;
    }

    if (formData.new_password.length < 6) {
      setStatus({ type: 'error', message: 'La nueva contraseña debe tener al menos 6 caracteres' });
      return;
    }

    setStatus({ type: 'loading' });
    try {
      await api.put('/users/change-password', {
        old_password: formData.old_password,
        new_password: formData.new_password
      });
      setStatus({ type: 'success', message: 'Contraseña actualizada correctamente' });
      setFormData({ old_password: '', new_password: '', confirm_password: '' });
      setTimeout(() => setStatus({ type: 'idle' }), 5000);
    } catch (error: any) {
      const errorMsg = error.response?.data?.error || 'Error al actualizar la contraseña';
      setStatus({ type: 'error', message: errorMsg });
    }
  };

  return (
    <div className="bg-dark-800 border border-dark-700 rounded-3xl p-8 shadow-xl">
      <SectionHeader title="Seguridad de la Cuenta" description="Asegura tu acceso y mantén el control de tus credenciales." />
      
      {status.type !== 'idle' && (
        <div className={`mb-6 p-4 rounded-2xl flex items-center gap-3 animate-in slide-in-from-top-4 duration-300 ${
          status.type === 'success' ? 'bg-accent-500/10 text-accent-500 border border-accent-500/20' : 
          status.type === 'error' ? 'bg-red-500/10 text-red-400 border border-red-500/20' : 
          'bg-brand-500/10 text-brand-400 border border-brand-500/20'
        }`}>
          {status.type === 'loading' ? <Loader2 className="w-5 h-5 animate-spin" /> : 
           status.type === 'success' ? <CheckCircle2 className="w-5 h-5" /> : 
           <XCircle className="w-5 h-5" />}
          <span className="text-sm font-bold">{status.message || 'Procesando...'}</span>
        </div>
      )}

      <form className="space-y-6" onSubmit={handleSubmit}>
        <div className="space-y-2">
          <label className="text-sm font-bold text-dark-400">Contraseña Actual</label>
          <div className="relative">
            <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-dark-500" />
            <input 
              type="password" 
              required
              value={formData.old_password}
              onChange={(e) => setFormData({...formData, old_password: e.target.value})}
              placeholder="••••••••••••"
              className="w-full bg-dark-900 border border-dark-700 rounded-xl px-4 py-3 pl-12 outline-none focus:ring-2 focus:ring-brand-500 transition-all text-white font-medium"
            />
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <label className="text-sm font-bold text-dark-400">Nueva Contraseña</label>
            <input 
              type="password" 
              required
              value={formData.new_password}
              onChange={(e) => setFormData({...formData, new_password: e.target.value})}
              className="w-full bg-dark-900 border border-dark-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-brand-500 transition-all text-white font-medium"
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-bold text-dark-400">Confirmar Contraseña</label>
            <input 
              type="password" 
              required
              value={formData.confirm_password}
              onChange={(e) => setFormData({...formData, confirm_password: e.target.value})}
              className="w-full bg-dark-900 border border-dark-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-brand-500 transition-all text-white font-medium"
            />
          </div>
        </div>
        <button 
          type="submit"
          disabled={status.type === 'loading'}
          className="bg-brand-500 hover:bg-brand-600 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold px-8 py-3 rounded-xl flex items-center gap-2 transition-all shadow-lg shadow-brand-500/20"
        >
          {status.type === 'loading' ? <Loader2 className="w-4 h-4 animate-spin" /> : <Lock className="w-4 h-4" />} 
          Cambiar Contraseña
        </button>
      </form>
    </div>
  );
};

const NotificationsSection = () => (
  <div className="bg-dark-800 border border-dark-700 rounded-3xl p-8 shadow-xl">
    <SectionHeader title="Preferencias de Alerta" description="Elige qué eventos quieres monitorear en tiempo real." />
    
    <div className="space-y-4">
      <ToggleItem 
        icon={Smartphone} 
        title="Nuevos Productos" 
        description="Recibe una alerta cuando un equipo sea publicado para inspección." 
        defaultChecked={true}
      />
      <ToggleItem 
        icon={Mail} 
        title="Reportes de Usuarios" 
        description="Notificaciones sobre quejas o reportes de fraude." 
        defaultChecked={true}
      />
      <ToggleItem 
        icon={Bell} 
        title="Stock Crítico" 
        description="Avisar cuando un producto estrella se quede sin unidades." 
        defaultChecked={false}
      />
    </div>
  </div>
);

const ToggleItem = ({ icon: Icon, title, description, defaultChecked }: any) => (
  <div className="flex items-center justify-between p-5 hover:bg-dark-900/40 rounded-2xl transition-colors border border-transparent hover:border-dark-700">
    <div className="flex gap-4">
      <div className="p-2.5 bg-dark-700 rounded-xl">
        <Icon className="w-5 h-5 text-brand-400" />
      </div>
      <div>
        <h4 className="font-bold text-white text-sm">{title}</h4>
        <p className="text-xs text-dark-500">{description}</p>
      </div>
    </div>
    <label className="relative inline-flex items-center cursor-pointer">
      <input type="checkbox" defaultChecked={defaultChecked} className="sr-only peer" />
      <div className="w-10 h-5 bg-dark-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-accent-500"></div>
    </label>
  </div>
);

export default SettingsPage;
