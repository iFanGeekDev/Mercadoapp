import React, { useEffect, useState } from 'react';
import { 
  DollarSign,
  ShoppingCart,
  Users,
  ArrowUpRight,
  ArrowDownRight,
  Package,
  Clock,
  ExternalLink
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';

interface Order {
  id: string;
  total: number;
  status: string;
  created_at: string;
  user_name?: string;
}

const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState({
    products: 0,
    orders: 0,
    revenue: 0
  });
  const [recentOrders, setRecentOrders] = useState<Order[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const productsRes = await api.get('/products');
        // Para demo, generamos órdenes aleatorias o si existieran las traeríamos
        // simulamos datos para un dashboard "vivo"
        setStats({
          products: productsRes.data.total_pages * 20, // aproximado
          orders: 142,
          revenue: 12450.80
        });

        // Mock de órdenes recientes con nombres reales para el diseño premium
        setRecentOrders([
          { id: 'ORD-2024-9921', total: 899.00, status: 'Completado', created_at: 'Hace 5 min', user_name: 'Carlos Mendoza' },
          { id: 'ORD-2024-9920', total: 1240.50, status: 'Procesando', created_at: 'Hace 12 min', user_name: 'Elena Rivas' },
          { id: 'ORD-2024-9919', total: 299.99, status: 'Pendiente', created_at: 'Hace 45 min', user_name: 'Juan Pérez' },
          { id: 'ORD-2024-9918', total: 1560.00, status: 'Completado', created_at: 'Hace 2 horas', user_name: 'Ana García' },
          { id: 'ORD-2024-9917', total: 450.00, status: 'Cancelado', created_at: 'Hace 3 horas', user_name: 'Roberto Díaz' },
        ]);
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      }
    };
    fetchData();
  }, []);

  return (
    <div className="space-y-10 animate-in fade-in duration-700">
      {/* Welcome Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 bg-gradient-to-r from-brand-600/10 to-transparent p-8 rounded-[2.5rem] border border-brand-500/10">
        <div>
          <div className="flex items-center gap-3 mb-1">
            <h2 className="text-3xl font-black text-white tracking-tight">¡Hola de nuevo, Admin! 👋</h2>
            <span className="bg-accent-500/20 text-accent-400 text-[10px] font-black px-2 py-0.5 rounded-md border border-accent-500/30">LIVE</span>
          </div>
          <p className="text-dark-400 mt-1">Aquí tienes un resumen de lo que ha pasado en YapaMarket hoy.</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="px-5 py-3 bg-dark-800 border border-dark-700 rounded-2xl text-white font-bold text-sm hover:bg-dark-700 transition-all">
            Ver Reportes
          </button>
          <button 
            onClick={() => navigate('/productos/nuevo')}
            className="px-5 py-3 bg-brand-500 text-white rounded-2xl font-bold text-sm hover:bg-brand-600 transition-all shadow-lg shadow-brand-500/20"
          >
            Añadir Producto
          </button>
        </div>
      </div>

      {/* Primary Metrics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <MetricCard 
          icon={DollarSign} 
          label="Ingresos Mensuales" 
          value={`$${stats.revenue.toLocaleString()}`} 
          trend="+12.5%" 
          isPositive={true} 
          color="text-white" 
        />
        <MetricCard 
          icon={ShoppingCart} 
          label="Órdenes Totales" 
          value={stats.orders.toString()} 
          trend="+8.2%" 
          isPositive={true} 
          color="text-white" 
        />
        <MetricCard 
          icon={Package} 
          label="Productos Activos" 
          value={stats.products.toString()} 
          trend="+3" 
          isPositive={true} 
          color="text-white" 
        />
        <MetricCard 
          icon={Users} 
          label="Usuarios Nuevos" 
          value="48" 
          trend="-2.4%" 
          isPositive={false} 
          color="text-white" 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Recent Activity Table */}
        <div className="lg:col-span-2 bg-dark-800 border border-dark-700 rounded-[2.5rem] overflow-hidden shadow-2xl">
          <div className="p-8 border-b border-dark-700 flex items-center justify-between">
            <div>
              <h3 className="text-xl font-black text-white">Órdenes Recientes</h3>
              <p className="text-dark-500 text-xs mt-1 font-bold uppercase tracking-widest">Últimas transacciones del sistema</p>
            </div>
            <button className="text-brand-400 hover:text-brand-300 font-bold text-sm flex items-center gap-1.5 transition-colors">
              Ver Todo <ArrowUpRight className="w-4 h-4" />
            </button>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="bg-dark-900/40 text-dark-500 text-[10px] font-black uppercase tracking-[0.2em]">
                  <th className="px-8 py-5">Cliente</th>
                  <th className="px-8 py-5">Orden ID</th>
                  <th className="px-8 py-5">Total</th>
                  <th className="px-8 py-5">Estado</th>
                  <th className="px-8 py-5 text-right">Tiempo</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-dark-700/50">
                {recentOrders.map((order) => (
                  <tr key={order.id} className="hover:bg-dark-700/20 transition-all group">
                    <td className="px-8 py-5">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-gradient-to-br from-dark-700 to-dark-900 rounded-xl flex items-center justify-center font-bold text-dark-300 border border-dark-700">
                          {order.user_name?.charAt(0)}
                        </div>
                        <span className="font-bold text-white text-sm">{order.user_name}</span>
                      </div>
                    </td>
                    <td className="px-8 py-5 font-mono text-xs text-dark-400">{order.id}</td>
                    <td className="px-8 py-5 font-black text-white text-sm">${order.total.toFixed(2)}</td>
                    <td className="px-8 py-5">
                      <StatusBadge status={order.status} />
                    </td>
                    <td className="px-8 py-5 text-right">
                      <span className="text-xs text-dark-500 flex items-center justify-end gap-1.5">
                        <Clock className="w-3.5 h-3.5" />
                        {order.created_at}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* System Health / Quick Actions */}
        <div className="space-y-6">
          <div className="bg-dark-800 border border-dark-700 rounded-[2.5rem] p-8 shadow-2xl">
            <h3 className="text-lg font-black text-white mb-6">Estado del Sistema</h3>
            <div className="space-y-5">
              <HealthItem label="API Backend" status="Online" color="bg-accent-500" />
              <HealthItem label="Base de Datos" status="Estable" color="bg-accent-500" />
              <HealthItem label="Almacenamiento" status="85% Libre" color="bg-accent-500" />
              <HealthItem label="Notificaciones" status="Activas" color="bg-accent-500" />
            </div>
            <div className="mt-8 pt-8 border-t border-dark-700">
              <button className="w-full py-4 bg-dark-900 border border-dark-700 rounded-2xl text-dark-400 font-bold hover:text-white transition-all flex items-center justify-center gap-2">
                <ExternalLink className="w-4 h-4" />
                <span>Logs de Sistema</span>
              </button>
            </div>
          </div>

          <div className="bg-gradient-to-br from-brand-600 to-brand-400 rounded-[2.5rem] p-8 shadow-2xl shadow-brand-500/20 text-white">
            <h3 className="text-lg font-black mb-2">Meta de Ventas</h3>
            <p className="text-brand-100 text-sm mb-6">Estás al 82% de tu objetivo mensual.</p>
            <div className="h-3 bg-white/20 rounded-full overflow-hidden mb-4 border border-white/10">
              <div className="h-full bg-white w-[82%] shadow-[0_0_15px_rgba(255,255,255,0.5)]" />
            </div>
            <div className="flex justify-between text-xs font-black uppercase tracking-widest">
              <span>$10.2k</span>
              <span className="opacity-60">$12.5k</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const MetricCard = ({ icon: Icon, label, value, trend, isPositive, color }: any) => (
  <div className="bg-dark-800 border border-dark-700 p-7 rounded-[2.5rem] shadow-xl relative overflow-hidden group hover:border-brand-500/30 transition-all">
    <div className="flex items-center justify-between mb-4">
      <div className="p-3 bg-dark-900 rounded-2xl border border-dark-700">
        <Icon className="w-6 h-6 text-brand-400" />
      </div>
      <div className={`flex items-center gap-1 text-xs font-black px-2.5 py-1 rounded-lg ${isPositive ? 'bg-accent-500/10 text-accent-500' : 'bg-red-500/10 text-red-500'}`}>
        {isPositive ? <ArrowUpRight className="w-3 h-3" /> : <ArrowDownRight className="w-3 h-3" />}
        {trend}
      </div>
    </div>
    <div className="space-y-1">
      <p className="text-dark-400 text-xs font-bold uppercase tracking-[0.1em]">{label}</p>
      <h4 className={`text-3xl font-black ${color}`}>{value}</h4>
    </div>
  </div>
);

const HealthItem = ({ label, status, color }: any) => (
  <div className="flex items-center justify-between">
    <span className="text-sm text-dark-400 font-medium">{label}</span>
    <div className="flex items-center gap-2">
      <span className="text-xs font-bold text-white">{status}</span>
      <div className={`w-2 h-2 ${color} rounded-full animate-pulse shadow-[0_0_8px_rgba(16,185,129,0.5)]`} />
    </div>
  </div>
);

const StatusBadge = ({ status }: { status: string }) => {
  const colors: any = {
    'Completado': 'bg-accent-500/10 text-accent-500 border-accent-500/20',
    'Procesando': 'bg-brand-500/10 text-brand-400 border-brand-500/20',
    'Pendiente': 'bg-amber-500/10 text-amber-500 border-amber-500/20',
    'Cancelado': 'bg-red-500/10 text-red-500 border-red-500/20',
  };
  return (
    <span className={`px-3 py-1 rounded-xl text-[10px] font-black uppercase tracking-wider border ${colors[status] || colors['Pendiente']}`}>
      {status}
    </span>
  );
};

export default DashboardPage;
