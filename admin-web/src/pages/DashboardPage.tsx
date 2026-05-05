import React, { useEffect, useState } from 'react';
import { 
  Plus, 
  Search, 
  Smartphone, 
  TrendingUp, 
  AlertCircle,
  MoreVertical,
  Filter
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';

interface Product {
  id: string;
  name: string;
  imageUrl: string;
  isOffer: boolean;
  isNewArrival: boolean;
  variants: any[];
}

const DashboardPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await api.get('/products');
        setProducts(response.data.items);
      } catch (error) {
        console.error('Error fetching products:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchProducts();
  }, []);

  return (
    <div className="space-y-8">
      {/* Metrics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <MetricCard icon={Smartphone} label="Total Equipos" value={products.length.toString()} trend="+4% vs mes anterior" />
        <MetricCard icon={TrendingUp} label="Ventas Hoy" value="$1,240.00" trend="+12% vs ayer" color="text-brand-400" />
        <MetricCard icon={AlertCircle} label="Stock Bajo" value="3" trend="Requiere atención" color="text-amber-400" />
        <MetricCard icon={TrendingUp} label="Conversión" value="3.2%" trend="-0.5% vs mes anterior" />
      </div>

      {/* Main Table Section */}
      <div className="bg-dark-800 border border-dark-700 rounded-3xl overflow-hidden shadow-xl">
        <div className="p-8 border-b border-dark-700 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h3 className="text-xl font-bold text-white">Inventario de Productos</h3>
            <p className="text-dark-400 text-sm mt-1">Gestiona tus equipos, variantes y precios.</p>
          </div>
          <div className="flex items-center gap-3">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-dark-500" />
              <input 
                type="text" 
                placeholder="Buscar equipo..." 
                className="bg-dark-900 border border-dark-700 text-sm pl-10 pr-4 py-2.5 rounded-xl w-64 outline-none focus:ring-2 focus:ring-brand-500 transition-all"
              />
            </div>
            <button className="bg-dark-700 p-2.5 rounded-xl hover:bg-dark-600 transition-colors">
              <Filter className="w-5 h-5" />
            </button>
            <button 
              onClick={() => navigate('/productos/nuevo')}
              className="bg-brand-500 hover:bg-brand-600 text-white px-5 py-2.5 rounded-xl font-bold flex items-center gap-2 transition-all shadow-lg shadow-brand-500/20"
            >
              <Plus className="w-5 h-5" />
              <span>Añadir</span>
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-dark-900/30 text-dark-400 text-xs font-bold uppercase tracking-wider">
                <th className="px-8 py-4">Producto</th>
                <th className="px-8 py-4">Estado</th>
                <th className="px-8 py-4">Precio Base</th>
                <th className="px-8 py-4">Stock</th>
                <th className="px-8 py-4">Inspección</th>
                <th className="px-8 py-4 text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-700">
              {isLoading ? (
                Array(5).fill(0).map((_, i) => <SkeletonRow key={i} />)
              ) : (
                products.map((product) => (
                  <tr 
                    key={product.id} 
                    onClick={() => navigate(`/productos/${product.id}`)}
                    className="hover:bg-dark-700/20 transition-colors group cursor-pointer"
                  >
                    <td className="px-8 py-5">
                      <div className="flex items-center gap-4">
                        <img src={product.imageUrl} className="w-12 h-12 rounded-xl object-cover bg-dark-900 border border-dark-700" alt="" />
                        <div>
                          <p className="font-bold text-white group-hover:text-brand-400 transition-colors">{product.name}</p>
                          <p className="text-xs text-dark-500">ID: {product.id.slice(0, 8)}...</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-8 py-5">
                      <div className="flex gap-2">
                        {product.isNewArrival && <Badge label="Nuevo" color="bg-accent-500/10 text-accent-500 border-accent-500/20" />}
                        {product.isOffer && <Badge label="Oferta" color="bg-brand-500/10 text-brand-400 border-brand-500/20" />}
                      </div>
                    </td>
                    <td className="px-8 py-5 font-medium text-white">
                      ${product.variants[0]?.price || '0.00'}
                    </td>
                    <td className="px-8 py-5">
                      <span className="bg-dark-900 px-3 py-1 rounded-lg border border-dark-700 text-sm">
                        {product.variants.reduce((acc, v) => acc + v.stock, 0)} u.
                      </span>
                    </td>
                    <td className="px-8 py-5 text-sm">
                      <span className="text-dark-500 flex items-center gap-1.5">
                        <div className="w-2 h-2 bg-accent-500 rounded-full" />
                        Aprobada
                      </span>
                    </td>
                    <td className="px-8 py-5 text-right">
                      <button className="text-dark-500 hover:text-white transition-colors">
                        <MoreVertical className="w-5 h-5" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

const MetricCard = ({ icon: Icon, label, value, trend, color = 'text-white' }: any) => (
  <div className="bg-dark-800 border border-dark-700 p-6 rounded-3xl shadow-lg relative overflow-hidden">
    <div className="absolute top-0 right-0 p-4 opacity-5">
      <Icon className="w-24 h-24" />
    </div>
    <div className="flex items-center gap-3 mb-4">
      <div className="p-2.5 bg-dark-900 rounded-xl border border-dark-700">
        <Icon className="w-5 h-5 text-brand-400" />
      </div>
      <span className="text-dark-400 text-sm font-medium">{label}</span>
    </div>
    <div className="space-y-1">
      <h4 className={`text-2xl font-black ${color}`}>{value}</h4>
      <p className="text-xs text-accent-500 font-bold">{trend}</p>
    </div>
  </div>
);

const Badge = ({ label, color }: any) => (
  <span className={`px-2.5 py-1 rounded-lg text-[10px] font-black uppercase tracking-wider border ${color}`}>
    {label}
  </span>
);

const SkeletonRow = () => (
  <tr>
    <td className="px-8 py-5 animate-pulse">
      <div className="flex items-center gap-4">
        <div className="w-12 h-12 bg-dark-700 rounded-xl" />
        <div className="space-y-2">
          <div className="h-4 w-32 bg-dark-700 rounded" />
          <div className="h-3 w-20 bg-dark-700 rounded" />
        </div>
      </div>
    </td>
    <td colSpan={5} className="px-8 py-5"><div className="h-4 bg-dark-700 rounded w-full animate-pulse" /></td>
  </tr>
);

export default DashboardPage;
