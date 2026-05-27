import { Link } from 'react-router-dom';

const ProductCard = ({ product }) => {
  const stockQuantity = product.stockQty || 0;
  const inStock = stockQuantity > 0;

  const fallbackImage = `https://placehold.co/400x400/4f46e5/ffffff?text=${encodeURIComponent(product.name)}`;

  return (
    <div className="product-card">
      <Link to={`/products/${product.id}`} className="product-card-image-link">
        <img
          src={product.imageUrl || fallbackImage}
          alt={product.name}
          className="product-image"
          onError={(e) => { e.target.src = fallbackImage; }}
          loading="lazy"
        />
      </Link>
      <div className="product-info">
        <div className="product-brand">{product.brand}</div>
        <h3 className="product-name">{product.name}</h3>
        <p className="product-description">{product.description}</p>
        <div className="product-footer">
          <span className="product-price">${Number(product.price).toFixed(2)}</span>
          <span className={`product-stock ${inStock ? 'in-stock' : 'out-of-stock'}`}>
            {inStock ? 'In Stock' : 'Out of Stock'}
          </span>
        </div>
        <Link to={`/products/${product.id}`} className="btn btn-primary btn-block" style={{ textAlign: 'center', display: 'block' }}>
          View Details
        </Link>
      </div>
    </div>
  );
};

export default ProductCard;
