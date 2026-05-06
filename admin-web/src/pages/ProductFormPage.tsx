import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { 
  Save, 
  ArrowLeft, 
  Trash2, 
  Upload, 
  CheckCircle2, 
  Smartphone,
  Cpu,
  Database,
  Palette,
  Loader2
} from 'lucide-react';
import api from '../api/client';

const ProductFormPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;

  const [isLoading, setIsLoading] = useState(false);
  const [isUploading, setIsUploading] = useState(false);

  // Form State
  const [formData, setFormData] = useState({
    name: '',
    short_description: '',
    image_url: '',
    is_offer: false,
    is_new_arrival: false,
    technical_specs: {
      processor: [''],
      ram_gb: [4],
      storage_gb: [128],
      colors: ['']
    },
    variants: [
      { condition: 'NORMAL', processor: '', ram_gb: 4, storage_gb: 128, color: '', price: 0, stock: 0 }
    ],
    inspection_checklist: [
      { category: 'Pantalla', item: 'Sin rayaduras ni pixeles muertos', status: 'PENDING', note: '' },
      { category: 'Batería', item: 'Salud superior al 85%', status: 'PENDING', note: '' },
      { category: 'Cámaras', item: 'Enfoque y flash funcionales', status: 'PENDING', note: '' },
      { category: 'Botones', item: 'Respuesta táctica correcta', status: 'PENDING', note: '' },
      { category: 'Conectividad', item: 'Wi-Fi, Bluetooth y Red móvil', status: 'PENDING', note: '' },
    ]
  });

  const [visibleSpecs, setVisibleSpecs] = useState({
    processor: true,
    ram: true,
    storage: true
  });

  const DESC_LIMIT = 200;
  const ramOptions = [4, 8, 16, 24, 32, 40, 48, 64, 128];

  useEffect(() => {
    if (isEditing) {
      const fetchProduct = async () => {
        try {
          const res = await api.get(`/products/${id}`);
          setFormData(res.data);
        } catch (error) {
          console.error(error);
        }
      };
      fetchProduct();
    }
  }, [id]);

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploading(true);
    const data = new FormData();
    data.append('image', file);

    try {
      const res = await api.post('/upload', data, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setFormData({ ...formData, image_url: res.data.image_url });
    } catch (error) {
      alert('Error al subir imagen');
    } finally {
      setIsUploading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validación de imagen obligatoria
    if (!formData.image_url) {
      alert('¡Atención! La imagen del producto es obligatoria. Sube una foto real antes de continuar.');
      return;
    }

    setIsLoading(true);
    
    // Preparar datos limpios según la visibilidad
    const cleanedFormData = {
      ...formData,
      technical_specs: {
        ...formData.technical_specs,
        processor: visibleSpecs.processor ? formData.technical_specs.processor : [],
        ram_gb: visibleSpecs.ram ? formData.technical_specs.ram_gb : [],
        storage_gb: visibleSpecs.storage ? formData.technical_specs.storage_gb : [],
      },
      variants: formData.variants.map(v => ({
        ...v,
        processor: visibleSpecs.processor ? v.processor : '',
        ram_gb: visibleSpecs.ram ? v.ram_gb : 0,
        storage_gb: visibleSpecs.storage ? v.storage_gb : 0,
      }))
    };

    try {
      if (isEditing) {
        await api.put(`/products/${id}`, cleanedFormData);
      } else {
        await api.post('/products', cleanedFormData);
      }
      navigate('/dashboard');
    } catch (error) {
      alert('Error al guardar producto');
    } finally {
      setIsLoading(false);
    }
  };

  const addVariant = () => {
    setFormData({
      ...formData,
      variants: [...formData.variants, { ...formData.variants[0], stock: 0, price: 0 }]
    });
  };

  const removeVariant = (index: number) => {
    if (formData.variants.length > 1) {
      const newVariants = formData.variants.filter((_, i) => i !== index);
      setFormData({ ...formData, variants: newVariants });
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-8 pb-20">
      <div className="flex items-center justify-between">
        <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-dark-400 hover:text-white transition-colors">
          <ArrowLeft className="w-5 h-5" />
          <span>Volver al listado</span>
        </button>
        <h2 className="text-2xl font-black text-white">{isEditing ? 'Editar Equipo' : 'Nuevo Equipo'}</h2>
      </div>

      <form onSubmit={handleSubmit} className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column: Main Info */}
        <div className="lg:col-span-2 space-y-8">
          {/* Basic Info */}
          <section className="bg-dark-800 border border-dark-700 p-8 rounded-3xl shadow-xl space-y-6">
            <h3 className="text-lg font-bold flex items-center gap-2">
              <Smartphone className="w-5 h-5 text-brand-400" />
              Información Básica
            </h3>
            <div className="grid grid-cols-1 gap-6">
              <div className="space-y-2">
                <label className="text-sm text-dark-400 ml-1">Nombre del Modelo</label>
                <input 
                  type="text" required value={formData.name}
                  onChange={e => setFormData({...formData, name: e.target.value})}
                  className="w-full bg-dark-900 border border-dark-700 p-3 rounded-xl outline-none focus:ring-2 focus:ring-brand-500"
                  placeholder="ej. iPhone 15 Pro Max"
                />
              </div>
              <div className="space-y-2">
                <div className="flex justify-between items-center ml-1">
                  <label className="text-sm text-dark-400">Descripción Corta</label>
                  <span className={`text-[10px] font-bold ${formData.short_description.length >= DESC_LIMIT ? 'text-red-500' : 'text-dark-500'}`}>
                    {DESC_LIMIT - formData.short_description.length} caracteres restantes
                  </span>
                </div>
                <textarea 
                  rows={3} required 
                  maxLength={DESC_LIMIT}
                  value={formData.short_description}
                  onChange={e => setFormData({...formData, short_description: e.target.value})}
                  className="w-full bg-dark-900 border border-dark-700 p-3 rounded-xl outline-none focus:ring-2 focus:ring-brand-500"
                  placeholder="Resumen de características principales..."
                />
              </div>
            </div>
          </section>

          {/* Technical Specs */}
          <section className="bg-dark-800 border border-dark-700 p-8 rounded-3xl shadow-xl space-y-6">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-bold flex items-center gap-2">
                <Cpu className="w-5 h-5 text-accent-500" />
                Especificaciones Técnicas
              </h3>
              <div className="flex gap-4">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input 
                    type="checkbox" checked={visibleSpecs.processor} 
                    onChange={e => setVisibleSpecs({...visibleSpecs, processor: e.target.checked})}
                    className="w-4 h-4 rounded border-dark-700 bg-dark-900 text-brand-500 focus:ring-0" 
                  />
                  <span className="text-[10px] font-black uppercase text-dark-400">Procesador</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input 
                    type="checkbox" checked={visibleSpecs.ram} 
                    onChange={e => setVisibleSpecs({...visibleSpecs, ram: e.target.checked})}
                    className="w-4 h-4 rounded border-dark-700 bg-dark-900 text-brand-500 focus:ring-0" 
                  />
                  <span className="text-[10px] font-black uppercase text-dark-400">RAM</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input 
                    type="checkbox" checked={visibleSpecs.storage} 
                    onChange={e => setVisibleSpecs({...visibleSpecs, storage: e.target.checked})}
                    className="w-4 h-4 rounded border-dark-700 bg-dark-900 text-brand-500 focus:ring-0" 
                  />
                  <span className="text-[10px] font-black uppercase text-dark-400">Almac.</span>
                </label>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {visibleSpecs.processor && (
                <div className="space-y-2">
                  <label className="text-sm text-dark-400 ml-1">Procesador</label>
                  <input 
                    type="text" value={formData.technical_specs.processor[0]}
                    onChange={e => setFormData({...formData, technical_specs: {...formData.technical_specs, processor: [e.target.value]}})}
                    className="w-full bg-dark-900 border border-dark-700 p-3 rounded-xl"
                  />
                </div>
              )}
              {visibleSpecs.ram && (
                <div className="space-y-2">
                  <label className="text-sm text-dark-400 ml-1">RAM (GB)</label>
                  <select 
                    value={formData.technical_specs.ram_gb[0]}
                    onChange={e => setFormData({...formData, technical_specs: {...formData.technical_specs, ram_gb: [parseInt(e.target.value)]}})}
                    className="w-full bg-dark-900 border border-dark-700 p-3 rounded-xl outline-none focus:ring-2 focus:ring-brand-500 appearance-none"
                  >
                    {ramOptions.map(ram => (
                      <option key={ram} value={ram}>{ram} GB</option>
                    ))}
                  </select>
                </div>
              )}
            </div>
          </section>

          {/* Variants */}
          <section className="bg-dark-800 border border-dark-700 p-8 rounded-3xl shadow-xl space-y-6">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-bold flex items-center gap-2">
                <Database className="w-5 h-5 text-brand-400" />
                Variantes y Stock
              </h3>
              <button type="button" onClick={addVariant} className="text-sm bg-brand-500/10 text-brand-400 px-4 py-2 rounded-lg font-bold border border-brand-500/20">
                + Añadir Variante
              </button>
            </div>
            
            <div className="space-y-6">
              {formData.variants.map((variant, index) => (
                <div 
                  key={index} 
                  className="grid grid-cols-1 md:grid-cols-12 gap-y-6 md:gap-x-8 bg-dark-900/40 p-6 rounded-3xl border border-dark-700/50 hover:border-brand-500/30 transition-all items-end group"
                >
                  {/* Condición */}
                  <div className="md:col-span-3 space-y-2">
                    <label className="text-[10px] uppercase font-black text-dark-500 tracking-wider ml-1">Condición</label>
                    <select 
                      value={variant.condition}
                      onChange={e => {
                        const newVariants = [...formData.variants];
                        newVariants[index].condition = e.target.value;
                        setFormData({...formData, variants: newVariants});
                      }}
                      className="w-full bg-dark-800 border border-dark-700 p-2.5 rounded-xl text-sm outline-none focus:ring-1 focus:ring-brand-500/50 appearance-none"
                    >
                      <option value="EXCELLENT">Excelente Condición</option>
                      <option value="NORMAL">Uso Normal</option>
                      <option value="FAIR">Desgastado (Regular)</option>
                    </select>
                  </div>
                  
                  {/* Color */}
                  <div className="md:col-span-2 space-y-2">
                    <label className="text-[10px] uppercase font-black text-dark-500 tracking-wider ml-1">Color (Hex)</label>
                    <div className="flex items-center gap-2 bg-dark-800 border border-dark-700 p-1.5 rounded-xl">
                      <div className="relative w-7 h-7 rounded-lg overflow-hidden border border-dark-700 shrink-0">
                        <input
                          type="color" 
                          value={variant.color.startsWith('#') ? variant.color : '#6C63FF'}
                          onChange={e => {
                            const newVariants = [...formData.variants];
                            newVariants[index].color = e.target.value.toUpperCase();
                            setFormData({...formData, variants: newVariants});
                          }}
                          className="absolute -inset-2 w-12 h-12 bg-transparent border-none cursor-pointer"
                        />
                      </div>
                      <input 
                        type="text" value={variant.color}
                        placeholder="#FFFFFF"
                        onChange={e => {
                          const newVariants = [...formData.variants];
                          newVariants[index].color = e.target.value;
                          setFormData({...formData, variants: newVariants});
                        }}
                        className="w-full bg-transparent border-none p-1 text-[10px] font-mono outline-none text-dark-200"
                      />
                    </div>
                  </div>

                  {/* Almacenamiento */}
                  {visibleSpecs.storage && (
                    <div className="md:col-span-2 space-y-2">
                      <label className="text-[10px] uppercase font-black text-dark-500 tracking-wider ml-1">Almac.</label>
                      <select
                        value={variant.storage_gb}
                        onChange={e => {
                          const newVariants = [...formData.variants];
                          newVariants[index].storage_gb = parseInt(e.target.value);
                          setFormData({...formData, variants: newVariants});
                        }}
                        className="w-full bg-dark-800 border border-dark-700 p-2.5 rounded-xl text-sm outline-none focus:ring-1 focus:ring-brand-500/50 appearance-none"
                      >
                        {[8, 16, 32, 64, 128, 256, 512, 1024].map(size => (
                          <option key={size} value={size}>
                            {size >= 1024 ? '1 TB' : `${size} GB`}
                          </option>
                        ))}
                      </select>
                    </div>
                  )}

                  {/* Precio */}
                  <div className={`${visibleSpecs.storage ? 'md:col-span-2' : 'md:col-span-3'} space-y-2`}>
                    <label className="text-[10px] uppercase font-black text-dark-500 tracking-wider ml-1">Precio</label>
                    <div className="relative">
                      <input 
                        type="number" value={variant.price}
                        onChange={e => {
                          const newVariants = [...formData.variants];
                          newVariants[index].price = parseFloat(e.target.value);
                          setFormData({...formData, variants: newVariants});
                        }}
                        className="w-full bg-dark-800 border border-dark-700 p-2.5 pl-6 rounded-xl text-sm outline-none focus:ring-1 focus:ring-brand-500/50"
                      />
                      <span className="absolute left-2.5 top-1/2 -translate-y-1/2 text-dark-500 text-xs">$</span>
                    </div>
                  </div>

                  {/* Stock */}
                  <div className="md:col-span-1 space-y-2">
                    <label className="text-[10px] uppercase font-black text-dark-500 tracking-wider ml-1">Stock</label>
                    <input 
                      type="number" value={variant.stock}
                      onChange={e => {
                        const newVariants = [...formData.variants];
                        newVariants[index].stock = parseInt(e.target.value);
                        setFormData({...formData, variants: newVariants});
                      }}
                      className="w-full bg-dark-800 border border-dark-700 p-2.5 rounded-xl text-sm outline-none focus:ring-1 focus:ring-brand-500/50"
                    />
                  </div>

                  {/* Eliminar */}
                  <div className="md:col-span-1 flex justify-end pb-1">
                    <button 
                      type="button" 
                      onClick={() => removeVariant(index)}
                      disabled={formData.variants.length <= 1}
                      className="p-2.5 text-dark-500 hover:text-red-500 hover:bg-red-500/10 rounded-xl disabled:opacity-0 disabled:pointer-events-none transition-all duration-200"
                      title="Eliminar variante"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>

        {/* Right Column: Media & Checklist */}
        <div className="space-y-8">
          {/* Image Upload */}
          <section className="bg-dark-800 border border-dark-700 p-8 rounded-3xl shadow-xl space-y-6">
            <h3 className="text-lg font-bold flex items-center gap-2">
              <Palette className="w-5 h-5 text-brand-400" />
              Imagen del Equipo <span className="text-red-500">*</span>
            </h3>
            <div 
              className={`relative h-64 rounded-2xl border-2 border-dashed flex flex-col items-center justify-center p-4 transition-all ${
                formData.image_url ? 'border-brand-500/50 bg-dark-900' : 'border-dark-700 bg-dark-900/30'
              }`}
            >
              {isUploading ? (
                <Loader2 className="w-8 h-8 text-brand-400 animate-spin" />
              ) : formData.image_url ? (
                <div className="relative w-full h-full group">
                  <img src={formData.image_url} className="w-full h-full object-contain rounded-xl" alt="Preview" />
                  <div className="absolute inset-0 bg-dark-900/60 opacity-0 group-hover:opacity-100 flex items-center justify-center rounded-xl transition-opacity">
                    <label className="cursor-pointer bg-white text-black px-4 py-2 rounded-lg font-bold text-sm">Cambiar Imagen</label>
                  </div>
                </div>
              ) : (
                <>
                  <Upload className="w-10 h-10 text-dark-600 mb-3" />
                  <p className="text-sm text-dark-400 text-center">Haz click para subir o arrastra una imagen</p>
                </>
              )}
              <input 
                type="file" 
                onChange={handleImageUpload}
                className="absolute inset-0 opacity-0 cursor-pointer"
                accept="image/*"
              />
            </div>
          </section>

          {/* Inspection Checklist */}
          <section className="bg-dark-800 border border-dark-700 p-8 rounded-3xl shadow-xl space-y-6">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-bold flex items-center gap-2">
                <CheckCircle2 className="w-5 h-5 text-accent-500" />
                Checklist Técnica
              </h3>
              <span className="text-[10px] bg-accent-500/10 text-accent-500 px-2 py-1 rounded border border-accent-500/20 font-black">ESTÁNDAR</span>
            </div>
            
            <div className="space-y-4">
              {formData.inspection_checklist.map((check, i) => (
                <div key={i} className="flex flex-col gap-2 p-3 bg-dark-900/40 rounded-xl border border-dark-700/50">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-white">{check.category}</span>
                    <select 
                      value={check.status}
                      onChange={e => {
                        const newChecklist = [...formData.inspection_checklist];
                        newChecklist[i].status = e.target.value;
                        setFormData({...formData, inspection_checklist: newChecklist});
                      }}
                      className={`text-[10px] font-black px-2 py-1 rounded outline-none ${
                        check.status === 'APPROVED' ? 'bg-green-500/10 text-green-500' :
                        check.status === 'FAILED' ? 'bg-red-500/10 text-red-500' : 
                        check.status === 'NA' ? 'bg-blue-500/10 text-blue-400' : 'bg-dark-700 text-dark-400'
                      }`}
                    >
                      <option value="PENDING">PENDIENTE</option>
                      <option value="APPROVED">APROBADO</option>
                      <option value="FAILED">FALLIDO</option>
                      <option value="NA">NO APLICA</option>
                    </select>
                  </div>
                  <p className="text-[11px] text-dark-500 leading-tight">{check.item}</p>
                </div>
              ))}
            </div>
          </section>

          <button 
            type="submit" 
            disabled={isLoading || isUploading}
            className="w-full bg-brand-500 hover:bg-brand-600 disabled:bg-brand-500/50 text-white font-black py-4 rounded-2xl shadow-xl shadow-brand-500/20 transition-all flex items-center justify-center gap-3"
          >
            {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : <Save className="w-6 h-6" />}
            {isEditing ? 'GUARDAR CAMBIOS' : 'CREAR PRODUCTO'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProductFormPage;
