import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Search, Tag, MapPin, Star } from 'lucide-react';

export default function Catalog() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');

  const categories = [
    'All',
    'Power Tools',
    'Electronics & Photography',
    'Academic Books',
    'Camping & Outdoor',
    'Event & Party'
  ];

  useEffect(() => {
    async function fetchItems() {
      setLoading(true);
      try {
        const params = {};
        if (selectedCategory && selectedCategory !== 'All') {
          params.category = selectedCategory;
        }
        if (search) {
          params.search = search;
        }
        const res = await api.get('/items', { params });
        setItems(res.data.items || []);
      } catch (err) {
        console.error('Failed to fetch items:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchItems();
  }, [selectedCategory, search]);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      <div className="flex flex-col md:flex-row items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Explore Available Items</h1>
          <p className="text-sm text-slate-500">Discover items available for short-term borrow in your campus or neighborhood.</p>
        </div>

        {/* Search Input */}
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search power tools, books..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-sm bg-white border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      {/* Category Pills */}
      <div className="flex items-center gap-2 overflow-x-auto pb-2">
        {categories.map((cat) => (
          <button
            key={cat}
            onClick={() => setSelectedCategory(cat === 'All' ? '' : cat)}
            className={`px-3.5 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition ${
              (cat === 'All' && !selectedCategory) || selectedCategory === cat
                ? 'bg-blue-600 text-white'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Item Grid */}
      {loading ? (
        <div className="py-20 text-center text-slate-500">Loading catalog...</div>
      ) : items.length === 0 ? (
        <div className="py-20 text-center bg-white rounded-2xl border border-slate-200">
          <p className="text-slate-500 text-sm">No items found matching your filters.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {items.map((item) => (
            <div key={item.id} className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-md transition">
              <div className="h-48 bg-slate-100 relative">
                {item.images && item.images.length > 0 ? (
                  <img src={item.images[0].url} alt={item.title} className="w-full h-full object-cover" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-slate-400 text-xs">No image</div>
                )}
                {item.isFree ? (
                  <span className="absolute top-3 left-3 bg-emerald-500 text-white text-xs font-bold px-2 py-0.5 rounded-md">FREE</span>
                ) : (
                  <span className="absolute top-3 left-3 bg-blue-600 text-white text-xs font-bold px-2 py-0.5 rounded-md">₹{item.dailyRate}/day</span>
                )}
              </div>
              <div className="p-4 space-y-2">
                <div className="flex items-center gap-1 text-xs text-slate-500">
                  <Tag className="w-3.5 h-3.5" />
                  <span>{item.category}</span>
                </div>
                <h3 className="font-semibold text-slate-900 text-sm line-clamp-1">{item.title}</h3>
                <p className="text-xs text-slate-500 line-clamp-2">{item.description}</p>
                <div className="pt-2 flex items-center justify-between border-t border-slate-100 text-xs text-slate-500">
                  <span className="flex items-center gap-1">
                    <MapPin className="w-3 h-3 text-slate-400" />
                    <span className="truncate max-w-[120px]">{item.address}</span>
                  </span>
                  <span className="flex items-center gap-1 font-medium text-amber-600">
                    <Star className="w-3 h-3 fill-amber-400 stroke-amber-400" />
                    {item.owner?.rating || '5.0'}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
