import React, { useState } from 'react';
import { Lock, Mail, Eye, EyeOff, Loader2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      const response = await api.post('/auth/login', { email, password });
      const { access_token, user } = response.data;

      if (user.role !== 'ADMIN') {
        throw new Error('No tienes permisos de administrador.');
      }

      login(access_token, user);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.error || err.message || 'Error al conectar con el servidor.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-dark-900 flex items-center justify-center p-4 relative overflow-hidden">
      {/* Decorative background elements */}
      <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-brand-500/10 rounded-full blur-[120px]" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-accent-500/10 rounded-full blur-[120px]" />

      <div className="w-full max-w-md z-10">
        <div className="text-center mb-10">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-brand-600 to-brand-400 rounded-2xl mb-4 shadow-lg shadow-brand-500/20">
            <span className="text-white text-3xl font-black">M</span>
          </div>
          <h1 className="text-3xl font-bold text-white mb-2">YapaMarket Admin</h1>
          <p className="text-dark-400">Panel de control y gestión de ecosistema</p>
        </div>

        <div className="bg-dark-800/50 backdrop-blur-xl border border-dark-700 p-8 rounded-3xl shadow-2xl">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <label className="text-sm font-medium text-dark-400 ml-1">Correo Electrónico</label>
              <div className="relative">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-dark-500" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="admin@yapamarket.dev"
                  required
                  className="w-full bg-dark-900/50 border border-dark-700 text-white pl-12 pr-4 py-3 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent outline-none transition-all placeholder:text-dark-600"
                />
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium text-dark-400 ml-1">Contraseña</label>
              <div className="relative">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-dark-500" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  className="w-full bg-dark-900/50 border border-dark-700 text-white pl-12 pr-12 py-3 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent outline-none transition-all placeholder:text-dark-600"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-dark-500 hover:text-dark-400"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            {error && (
              <div className="bg-red-500/10 border border-red-500/20 text-red-400 px-4 py-3 rounded-xl text-sm flex items-center gap-3">
                <div className="w-1.5 h-1.5 bg-red-500 rounded-full" />
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-brand-500 hover:bg-brand-600 disabled:bg-brand-500/50 text-white font-bold py-3.5 rounded-xl shadow-lg shadow-brand-500/25 transition-all flex items-center justify-center gap-2"
            >
              {isLoading ? (
                <Loader2 className="w-5 h-5 animate-spin" />
              ) : (
                'INICIAR SESIÓN'
              )}
            </button>
          </form>
        </div>

        <p className="text-center mt-8 text-dark-500 text-sm">
          &copy; 2026 MercadoApp Ecosystem. Todos los derechos reservados.
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
