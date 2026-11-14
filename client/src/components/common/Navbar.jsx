import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { Share2, Package, User, LogOut, PlusCircle } from 'lucide-react';

export default function Navbar() {
  const { user, logout, isAuthenticated } = useContext(AuthContext);

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center text-white shadow-sm">
            <Share2 className="w-5 h-5" />
          </div>
          <span className="font-bold text-xl tracking-tight text-slate-900">
            Share<span className="text-blue-600">Sphere</span>
          </span>
        </Link>

        <nav className="hidden md:flex items-center gap-6">
          <Link to="/catalog" className="text-sm font-medium text-slate-600 hover:text-blue-600 transition">
            Explore Items
          </Link>
          {isAuthenticated && (
            <>
              <Link to="/dashboard" className="text-sm font-medium text-slate-600 hover:text-blue-600 transition">
                Dashboard
              </Link>
              {user?.role === 'DELIVERY_PARTNER' && (
                <Link to="/delivery" className="text-sm font-medium text-amber-600 hover:text-amber-700 transition">
                  Delivery Tasks
                </Link>
              )}
            </>
          )}
        </nav>

        <div className="flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <Link
                to="/create-listing"
                className="hidden sm:inline-flex items-center gap-1.5 px-3.5 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg shadow-sm transition"
              >
                <PlusCircle className="w-4 h-4" />
                List an Item
              </Link>
              <button
                onClick={logout}
                className="inline-flex items-center gap-1 px-3 py-2 text-sm font-medium text-slate-600 hover:text-red-600 rounded-lg hover:bg-slate-50 transition"
              >
                <LogOut className="w-4 h-4" />
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="px-4 py-2 text-sm font-medium text-slate-700 hover:text-blue-600 transition"
              >
                Log In
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg shadow-sm transition"
              >
                Get Started
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
