import React, { useEffect, useState } from 'react';
import { 
  Plus, 
  Search, 
  AlertCircle,
  MoreVertical,
  Filter,
  Package,
  Layers,
  Archive
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';

interface Product {
  id: string;
  name: string;
  image_url: string;
  is_offer: boolean;
  is_new_arrival: boolean;
  category: string;
  variants: any[];
}

const ProductImage = ({ src, alt }: { src: string, alt: string }) => {
  const [error, setError] = useState(false);

  if (error || !src) {
    return (
      <div className="w-14 h-14 rounded-2xl bg-dark-900 flex items-center justify-center border border-dark-700 shadow-inner overflow-hidden">
        <Package className="w-7 h-7 text-dark-600 opacity-50" />
      </div>
    );
  }

  return (
    <img 
      src={src} 
      className="w-14 h-14 rounded-2xl object-cover bg-dark-900 border border-dark-700 shadow-inner" 
      alt={alt}
      onError={() => setError(true)}
    />
  );
};

const ProductsPage: React.FC = () => {
  console.log('🚀 ProductsPage: VERSIÓN CORREGIDA 2.0');
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortConfig, setSortConfig] = useState<{ key: string; direction: 'asc' | 'desc' } | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await api.get('/products?size=100');
        console.log('📦 ProductsPage: Data received', response.data.items);
        setProducts(response.data.items || []);
      } catch (error) {
        console.error('📦 ProductsPage: Error fetching products:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchProducts();
  }, []);

  const handleSort = (key: string) => {
    let direction: 'asc' | 'desc' = 'asc';
    if (sortConfig && sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const getSortedProducts = (items: Product[]) => {
    if (!sortConfig) return items;

    return [...items].sort((a, b) => {
      let aValue: any;
      let bValue: any;

      switch (sortConfig.key) {
        case 'name':
          aValue = (a.name || '').toLowerCase();
          bValue = (b.name || '').toLowerCase();
          break;
        case 'category':
          aValue = (a.category || '').toLowerCase();
          bValue = (b.category || '').toLowerCase();
          break;
        case 'price':
          aValue = Number(a.variants?.[0]?.price || 0);
          bValue = Number(b.variants?.[0]?.price || 0);
          break;
        case 'stock':
          aValue = a.variants?.reduce((acc, v) => acc + Number(v.stock || 0), 0) || 0;
          bValue = b.variants?.reduce((acc, v) => acc + Number(v.stock || 0), 0) || 0;
          break;
        default:
          return 0;
      }

      if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
      if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
      return 0;
    });
  };

  const filteredProducts = getSortedProducts((products || []).filter(p => 
    (p.name || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (p.category || '').toLowerCase().includes(searchTerm.toLowerCase())
  ));

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h2 className="text-3xl font-black text-white tracking-tight">Gestión de Productos</h2>
          <p className="text-dark-400 mt-2">Control total sobre tu inventario, variantes y disponibilidad.</p>
        </div>
        <button 
          onClick={() => navigate('/productos/nuevo')}
          className="bg-brand-500 hover:bg-brand-600 text-white px-8 py-4 rounded-2xl font-bold flex items-center gap-3 transition-all shadow-xl shadow-brand-500/25 active:scale-95"
        >
          <Plus className="w-6 h-6" />
          <span>Añadir Producto</span>
        </button>
      </div>

      {/* Inventory Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <InventoryStat icon={Package} label="Total SKUs" value={(products || []).length.toString()} color="text-white" />
        <InventoryStat icon={Layers} label="Variantes Activas" value={(products || []).reduce((acc, p) => acc + (p.variants?.length || 0), 0).toString()} color="text-brand-400" />
        <InventoryStat icon={AlertCircle} label="PRODUCTOS EN ALERTA" value={(products || []).filter(p => (p.variants || []).some(v => Number(v.stock || 0) < 10)).length.toString()} color="text-red-500" />
      </div>

      {/* Table Container */}
      <div className="bg-dark-800 border border-dark-700 rounded-[2.5rem] overflow-hidden shadow-2xl">
        <div className="p-8 border-b border-dark-700 bg-dark-800/50 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-dark-500" />
            <input 
              type="text" 
              placeholder="Buscar por nombre o categoría..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full bg-dark-900 border border-dark-700 text-white pl-12 pr-4 py-3.5 rounded-2xl outline-none focus:ring-2 focus:ring-brand-500 transition-all placeholder:text-dark-600"
            />
          </div>
          <div className="flex items-center gap-3">
            <button className="flex items-center gap-2 px-4 py-3.5 bg-dark-900 border border-dark-700 rounded-2xl text-dark-400 hover:text-white hover:border-dark-600 transition-all">
              <Filter className="w-5 h-5" />
              <span className="text-sm font-bold">Filtros</span>
            </button>
            <button className="flex items-center gap-2 px-4 py-3.5 bg-dark-900 border border-dark-700 rounded-2xl text-dark-400 hover:text-white hover:border-dark-600 transition-all">
              <Archive className="w-5 h-5" />
              <span className="text-sm font-bold">Exportar</span>
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-dark-900/50 text-dark-500 text-[11px] font-black uppercase tracking-[0.2em]">
                <th 
                  className="px-10 py-5 cursor-pointer hover:text-white transition-colors"
                  onClick={() => handleSort('name')}
                >
                  <div className="flex items-center gap-2">
                    Producto
                    {sortConfig?.key === 'name' && (
                      <span className="text-brand-400">{sortConfig.direction === 'asc' ? '↑' : '↓'}</span>
                    )}
                  </div>
                </th>
                <th 
                  className="px-10 py-5 cursor-pointer hover:text-white transition-colors"
                  onClick={() => handleSort('category')}
                >
                  <div className="flex items-center gap-2">
                    Categoría
                    {sortConfig?.key === 'category' && (
                      <span className="text-brand-400">{sortConfig.direction === 'asc' ? '↑' : '↓'}</span>
                    )}
                  </div>
                </th>
                <th 
                  className="px-10 py-5 cursor-pointer hover:text-white transition-colors"
                  onClick={() => handleSort('price')}
                >
                  <div className="flex items-center gap-2">
                    Precio
                    {sortConfig?.key === 'price' && (
                      <span className="text-brand-400">{sortConfig.direction === 'asc' ? '↑' : '↓'}</span>
                    )}
                  </div>
                </th>
                <th 
                  className="px-10 py-5 cursor-pointer hover:text-white transition-colors"
                  onClick={() => handleSort('stock')}
                >
                  <div className="flex items-center gap-2">
                    Stock Total
                    {sortConfig?.key === 'stock' && (
                      <span className="text-brand-400">{sortConfig.direction === 'asc' ? '↑' : '↓'}</span>
                    )}
                  </div>
                </th>
                <th className="px-10 py-5">Estado</th>
                <th className="px-10 py-5 text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-700/50">
              {isLoading ? (
                Array(5).fill(0).map((_, i) => <SkeletonRow key={i} />)
              ) : filteredProducts.length > 0 ? (
                filteredProducts.map((product) => (
                  <tr 
                    key={product.id} 
                    onClick={() => navigate(`/productos/${product.id}`)}
                    className="hover:bg-dark-700/30 transition-all group cursor-pointer"
                  >
                    <td className="px-10 py-6">
                      <div className="flex items-center gap-4">
                        <div className="relative">
                          <ProductImage src={product.image_url} alt={product.name} />
                          {product.is_offer && (
                            <div className="absolute -top-1 -right-1 w-4 h-4 bg-brand-500 rounded-full flex items-center justify-center border-2 border-dark-800 shadow-lg">
                              <div className="w-1.5 h-1.5 bg-white rounded-full animate-pulse" />
                            </div>
                          )}
                        </div>
                        <div>
                          <p className="font-bold text-white text-lg group-hover:text-brand-400 transition-colors leading-tight">{product.name || 'Producto sin nombre'}</p>
                          <p className="text-xs text-dark-500 mt-1 font-mono uppercase">{(product.id || '').slice(0, 13)}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-10 py-6">
                      <span className="px-3 py-1.5 bg-dark-900 border border-dark-700 text-dark-400 rounded-xl text-xs font-black uppercase tracking-wider">
                        {product.category || 'PHONES'}
                      </span>
                    </td>
                    <td className="px-10 py-6">
                      <div className="space-y-0.5">
                        <p className="text-white font-black text-lg">${Number(product.variants[0]?.price || 0).toFixed(2)}</p>
                        <p className="text-[10px] text-dark-500 font-bold uppercase tracking-tighter">Precio Base</p>
                      </div>
                    </td>
                    <td className="px-10 py-6">
                      <div className="space-y-1.5">
                        <div className="flex items-center justify-between">
                          <span className="text-[10px] text-dark-500 font-bold uppercase tracking-widest">Disponibilidad</span>
                          <span className="text-xs font-black text-white">{product.variants?.reduce((acc, v) => acc + Number(v.stock || 0), 0) || 0}</span>
                        </div>
                        <div className="flex items-center gap-3">
                          <div className="flex-1 h-2 bg-dark-900 rounded-full overflow-hidden max-w-[120px] border border-dark-700/50">
                            <div 
                              className={`h-full transition-all duration-1000 ${(product.variants || []).some(v => Number(v.stock || 0) < 10) ? 'animate-pulse' : ''} ${getStockColor(
                                product.variants?.reduce((acc, v) => acc + Number(v.stock || 0), 0) || 0,
                                (product.variants || []).some(v => Number(v.stock || 0) < 10)
                              )}`}
                              style={{ width: `${Math.min((product.variants?.reduce((acc, v) => acc + Number(v.stock || 0), 0) || 0) * 1.5, 100)}%` }}
                            />
                          </div>
                          {(product.variants || []).some(v => Number(v.stock || 0) < 10) && (
                            <span className="text-[9px] font-black bg-red-500/20 text-red-400 px-2 py-0.5 rounded-full border border-red-500/30 animate-pulse">REVISAR</span>
                          )}
                        </div>
                      </div>
                    </td>
                    <td className="px-10 py-6">
                      <div className="flex gap-2">
                        {product.is_new_arrival && <Badge label="Nuevo" color="bg-accent-500/10 text-accent-500 border-accent-500/20" />}
                        {product.is_offer && <Badge label="Oferta" color="bg-brand-500/10 text-brand-400 border-brand-500/20" />}
                      </div>
                    </td>
                    <td className="px-10 py-6 text-right">
                      <button className="w-10 h-10 flex items-center justify-center bg-dark-900 border border-dark-700 rounded-xl text-dark-500 hover:text-white hover:border-dark-600 transition-all ml-auto">
                        <MoreVertical className="w-5 h-5" />
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-10 py-20 text-center">
                    <div className="flex flex-col items-center gap-4">
                      <div className="w-20 h-20 bg-dark-900 rounded-full flex items-center justify-center border border-dark-700">
                        <Search className="w-10 h-10 text-dark-700" />
                      </div>
                      <p className="text-dark-500 font-bold">No se encontraron productos para "{searchTerm}"</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

const InventoryStat = ({ icon: Icon, label, value, color }: any) => (
  <div className="bg-dark-800 border border-dark-700 p-8 rounded-[2.5rem] shadow-xl relative overflow-hidden group hover:border-dark-600 transition-all">
    <div className="absolute -top-4 -right-4 p-4 opacity-[0.03] group-hover:opacity-[0.06] transition-opacity">
      <Icon className="w-32 h-32" />
    </div>
    <div className="flex items-center gap-4 mb-4">
      <div className="p-3 bg-dark-900 rounded-2xl border border-dark-700">
        <Icon className="w-6 h-6 text-brand-400" />
      </div>
      <span className="text-dark-400 text-sm font-black uppercase tracking-widest">{label}</span>
    </div>
    <h4 className={`text-4xl font-black ${color}`}>{value}</h4>
  </div>
);

const Badge = ({ label, color }: any) => (
  <span className={`px-3 py-1 rounded-xl text-[10px] font-black uppercase tracking-wider border ${color}`}>
    {label}
  </span>
);

const getStockColor = (totalStock: number, hasLowStockVariant: boolean) => {
  if (totalStock <= 0 || hasLowStockVariant) return 'bg-red-500 shadow-[0_0_15px_rgba(239,68,68,0.6)]';
  if (totalStock < 20) return 'bg-amber-500 shadow-[0_0_10px_rgba(245,158,11,0.5)]';
  return 'bg-accent-500 shadow-[0_0_10px_rgba(16,185,129,0.5)]';
};

const SkeletonRow = () => (
  <tr>
    <td className="px-10 py-6 animate-pulse">
      <div className="flex items-center gap-4">
        <div className="w-14 h-14 bg-dark-700 rounded-2xl" />
        <div className="space-y-2">
          <div className="h-5 w-40 bg-dark-700 rounded" />
          <div className="h-3 w-24 bg-dark-700 rounded" />
        </div>
      </div>
    </td>
    <td colSpan={5} className="px-10 py-6"><div className="h-6 bg-dark-700 rounded-xl w-full animate-pulse" /></td>
  </tr>
);

export default ProductsPage;
