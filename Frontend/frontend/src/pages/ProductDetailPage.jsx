import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productAPI } from '../api/axios';
import { useAuth } from '../context/AuthContext';

const ProductDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart, isAuthenticated, openAuthModal } = useAuth();

  const [product, setProduct] = useState(null);
  const [selectedSize, setSelectedSize] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [added, setAdded] = useState(false);
  const [imgError, setImgError] = useState(false);

  useEffect(() => {
    productAPI
      .getById(id)
      .then((r) => setProduct(r.data))
      .catch(() => setError('Product not found'))
      .finally(() => setLoading(false));
  }, [id]);

  const sizes = product?.sizesCsv
    ? product.sizesCsv.split(',').map((s) => s.trim()).filter(Boolean)
    : [];

  const inStock = (product?.stockQty || 0) > 0;

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      openAuthModal();
      return;
    }
    if (!selectedSize) {
      alert('Please select a size first');
      return;
    }
    addToCart(product, selectedSize);
    setAdded(true);
    setTimeout(() => setAdded(false), 2500);
  };

  const fallback = `https://placehold.co/600x600/4f46e5/ffffff?text=${encodeURIComponent(product?.name || 'Product')}`;

  if (loading) return <div className="loading">Loading product...</div>;
  if (error) return (
    <div className="page">
      <div className="error">{error}</div>
      <button onClick={() => navigate('/products')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
        Back to Products
      </button>
    </div>
  );

  return (
    <div className="page">
      <button onClick={() => navigate('/products')} className="back-link">
        ← Back to Products
      </button>

      <div className="product-detail-container">
        {/* Image */}
        <div className="product-detail-image-wrap">
          <img
            src={imgError ? fallback : (product.imageUrl || fallback)}
            alt={product.name}
            className="product-detail-image"
            onError={() => setImgError(true)}
          />
          {!inStock && <div className="out-of-stock-overlay">Out of Stock</div>}
        </div>

        {/* Info */}
        <div className="product-detail-info">
          <div className="product-brand" style={{ fontSize: '1rem', marginBottom: '0.5rem' }}>
            {product.brand}
          </div>
          <h1 className="detail-product-name">{product.name}</h1>
          <p className="detail-product-price">${Number(product.price).toFixed(2)}</p>

          <p className="detail-product-description">{product.description}</p>

          <div className="detail-stock-info">
            <span className={`product-stock ${inStock ? 'in-stock' : 'out-of-stock'}`}>
              {inStock ? `In Stock (${product.stockQty} available)` : 'Out of Stock'}
            </span>
          </div>

          {/* Size Selector */}
          {sizes.length > 0 && (
            <div className="size-selector">
              <p className="size-label">
                Select Size <span className="size-unit">(US)</span>
                {!selectedSize && <span className="size-required"> — required</span>}
              </p>
              <div className="size-grid">
                {sizes.map((s) => (
                  <button
                    key={s}
                    onClick={() => setSelectedSize(s)}
                    className={`size-btn ${selectedSize === s ? 'selected' : ''}`}
                    disabled={!inStock}
                  >
                    {s}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Add to Cart */}
          <div className="detail-actions">
            {added ? (
              <div className="added-banner">
                ✓ Added to cart!{' '}
                <button onClick={() => navigate('/cart')} className="go-to-cart-link">
                  View Cart
                </button>
              </div>
            ) : (
              <button
                onClick={handleAddToCart}
                className="btn btn-primary btn-block add-to-cart-btn"
                disabled={!inStock}
              >
                {!isAuthenticated
                  ? 'Sign in to Add to Cart'
                  : !inStock
                  ? 'Out of Stock'
                  : 'Add to Cart'}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;
