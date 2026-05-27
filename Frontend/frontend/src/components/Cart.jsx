import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Cart = () => {
  const { cart, removeFromCart, updateCartQuantity } = useAuth();
  const navigate = useNavigate();

  const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handleQuantityChange = (cartItemId, newQty) => {
    if (newQty < 1) return;
    updateCartQuantity(cartItemId, newQty);
  };

  if (cart.length === 0) {
    return (
      <div className="cart-empty">
        <div className="cart-empty-icon">🛒</div>
        <h2>Your cart is empty</h2>
        <p>Browse our collection and add some sneakers!</p>
        <button onClick={() => navigate('/products')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
          Shop Now
        </button>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <h2>Shopping Cart</h2>

      <div className="cart-layout">
        <div className="cart-items">
          {cart.map((item) => (
            <div key={item.cartItemId} className="cart-item">
              <img
                src={item.imageUrl || `https://placehold.co/100x100/4f46e5/ffffff?text=${encodeURIComponent(item.name)}`}
                alt={item.name}
                className="cart-item-image"
                onError={(e) => { e.target.src = `https://placehold.co/100x100/4f46e5/ffffff?text=IMG`; }}
              />
              <div className="cart-item-details">
                <h3>{item.name}</h3>
                <p className="cart-item-brand">{item.brand}</p>
                <p className="cart-item-size">Size: <strong>US {item.size}</strong></p>
                <p className="cart-item-price">${Number(item.price).toFixed(2)} each</p>
              </div>
              <div className="cart-item-quantity">
                <button
                  onClick={() => handleQuantityChange(item.cartItemId, item.quantity - 1)}
                  className="qty-btn"
                >
                  −
                </button>
                <span className="qty-value">{item.quantity}</span>
                <button
                  onClick={() => handleQuantityChange(item.cartItemId, item.quantity + 1)}
                  className="qty-btn"
                >
                  +
                </button>
              </div>
              <div className="cart-item-total">
                ${(Number(item.price) * item.quantity).toFixed(2)}
              </div>
              <button
                onClick={() => removeFromCart(item.cartItemId)}
                className="cart-remove-btn"
                title="Remove item"
              >
                ✕
              </button>
            </div>
          ))}
        </div>

        <div className="cart-summary">
          <h3>Order Summary</h3>
          <div className="cart-summary-row">
            <span>Items ({cart.reduce((s, i) => s + i.quantity, 0)})</span>
            <span>${total.toFixed(2)}</span>
          </div>
          <div className="cart-summary-row">
            <span>Shipping</span>
            <span className="free-shipping">Free</span>
          </div>
          <div className="cart-summary-row total">
            <span>Total</span>
            <span>${total.toFixed(2)}</span>
          </div>
          <button
            onClick={() => navigate('/checkout')}
            className="btn btn-primary btn-block checkout-btn"
          >
            Proceed to Checkout
          </button>
          <button
            onClick={() => navigate('/products')}
            className="btn btn-secondary btn-block"
            style={{ marginTop: '0.75rem' }}
          >
            Continue Shopping
          </button>
        </div>
      </div>
    </div>
  );
};

export default Cart;
