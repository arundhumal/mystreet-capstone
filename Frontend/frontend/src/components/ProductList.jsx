import { useState, useEffect } from 'react';
import { productAPI } from '../api/axios';
import ProductCard from './ProductCard';

const SIZES = ['6', '7', '8', '9', '10', '11', '12', '13'];

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [allBrands, setAllBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [brand, setBrand] = useState('');
  const [size, setSize] = useState('');

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const response = await productAPI.getAll(brand || undefined, size || undefined);
        setProducts(response.data);
        // Populate brand list from the first (unfiltered) fetch only
        if (!brand && !size) {
          const unique = [...new Set(response.data.map((p) => p.brand).filter(Boolean))].sort();
          setAllBrands(unique);
        }
        setError(null);
      } catch (err) {
        setError('Failed to load products');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, [brand, size]);

  const clearFilters = () => {
    setBrand('');
    setSize('');
  };

  const hasFilters = brand || size;

  return (
    <div>
      {/* Filters Bar */}
      <div className="filters-bar">
        <div className="filter-group">
          <label htmlFor="brand-filter">Brand</label>
          <select
            id="brand-filter"
            value={brand}
            onChange={(e) => setBrand(e.target.value)}
          >
            <option value="">All Brands</option>
            {allBrands.map((b) => (
              <option key={b} value={b}>{b}</option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="size-filter">Size</label>
          <select
            id="size-filter"
            value={size}
            onChange={(e) => setSize(e.target.value)}
          >
            <option value="">All Sizes</option>
            {SIZES.map((s) => (
              <option key={s} value={s}>US {s}</option>
            ))}
          </select>
        </div>

        {hasFilters && (
          <button onClick={clearFilters} className="btn btn-secondary clear-filters-btn">
            Clear Filters
          </button>
        )}
      </div>

      {/* Results */}
      <div className="product-count-bar">
        {!loading && (
          <span className="product-count">
            {products.length} product{products.length !== 1 ? 's' : ''} found
            {hasFilters && ` (filtered)`}
          </span>
        )}
      </div>

      {loading && <div className="loading">Loading products...</div>}
      {error && <div className="error">{error}</div>}

      {!loading && !error && products.length === 0 && (
        <div className="no-products">
          <p>No products match your filters.</p>
          <button onClick={clearFilters} className="btn btn-primary" style={{ marginTop: '1rem' }}>
            Clear Filters
          </button>
        </div>
      )}

      {!loading && !error && products.length > 0 && (
        <div className="product-list">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </div>
  );
};

export default ProductList;
