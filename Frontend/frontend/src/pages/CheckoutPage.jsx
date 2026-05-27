import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { orderAPI } from '../api/axios';

const CheckoutPage = () => {
  const { cart, clearCart } = useAuth();
  const navigate = useNavigate();

  const [shippingAddress, setShippingAddress] = useState('');
  const [paymentMode, setPaymentMode] = useState('COD');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (cart.length === 0) navigate('/cart');
  }, [cart, navigate]);

  const subtotal = cart.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0);

  const handlePlaceOrder = async (e) => {
    e.preventDefault();
    if (!shippingAddress.trim()) {
      setError('Shipping address is required');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const orderData = {
        items: cart.map((item) => ({
          productId: item.id,
          size: item.size,
          quantity: item.quantity,
        })),
        shippingAddress: shippingAddress.trim(),
        paymentMode,
      };
      const response = await orderAPI.create(orderData);
      clearCart();
      navigate(`/order-confirmation/${response.data.id}`);
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to place order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (cart.length === 0) return null;

  return (
    <div className="page">
      <button onClick={() => navigate('/cart')} className="back-link">
        ← Back to Cart
      </button>
      <h1 className="page-title">Checkout</h1>

      <div className="checkout-layout">
        {/* Left — Form */}
        <div className="checkout-form-section">
          <form onSubmit={handlePlaceOrder}>
            {/* Shipping Address */}
            <div className="checkout-section-card">
              <h2 className="checkout-section-title">
                <span className="section-num">1</span> Shipping Address
              </h2>
              <div className="form-group">
                <label htmlFor="address">Full Address</label>
                <textarea
                  id="address"
                  value={shippingAddress}
                  onChange={(e) => setShippingAddress(e.target.value)}
                  placeholder="Enter your full delivery address&#10;(Street, City, State, PIN)"
                  rows={4}
                  required
                  className="address-textarea"
                />
              </div>
            </div>

            {/* Payment Mode */}
            <div className="checkout-section-card">
              <h2 className="checkout-section-title">
                <span className="section-num">2</span> Payment Method
              </h2>
              <div className="payment-options">
                <label className={`payment-option ${paymentMode === 'COD' ? 'selected' : ''}`}>
                  <input
                    type="radio"
                    name="payment"
                    value="COD"
                    checked={paymentMode === 'COD'}
                    onChange={() => setPaymentMode('COD')}
                  />
                  <div className="payment-option-content">
                    <span className="payment-icon">💵</span>
                    <div>
                      <strong>Cash on Delivery</strong>
                      <p>Pay when your order arrives</p>
                    </div>
                  </div>
                </label>

                <label className={`payment-option ${paymentMode === 'MOCK' ? 'selected' : ''}`}>
                  <input
                    type="radio"
                    name="payment"
                    value="MOCK"
                    checked={paymentMode === 'MOCK'}
                    onChange={() => setPaymentMode('MOCK')}
                  />
                  <div className="payment-option-content">
                    <span className="payment-icon">📱</span>
                    <div>
                      <strong>Mock UPI</strong>
                      <p>Simulated UPI payment (always succeeds)</p>
                    </div>
                  </div>
                </label>
              </div>
            </div>

            {error && <div className="error-message">{error}</div>}

            <button
              type="submit"
              className="btn btn-primary btn-block place-order-btn"
              disabled={loading}
            >
              {loading ? 'Placing Order...' : `Place Order — $${subtotal.toFixed(2)}`}
            </button>
          </form>
        </div>

        {/* Right — Order Summary */}
        <div className="checkout-summary-section">
          <div className="checkout-section-card">
            <h2 className="checkout-section-title">Order Summary</h2>
            <div className="checkout-items-list">
              {cart.map((item) => (
                <div key={item.cartItemId} className="checkout-item">
                  <img
                    src={item.imageUrl || `https://placehold.co/60x60/4f46e5/ffffff?text=IMG`}
                    alt={item.name}
                    className="checkout-item-img"
                    onError={(e) => { e.target.src = `https://placehold.co/60x60/4f46e5/ffffff?text=IMG`; }}
                  />
                  <div className="checkout-item-info">
                    <p className="checkout-item-name">{item.name}</p>
                    <p className="checkout-item-meta">Size: US {item.size} · Qty: {item.quantity}</p>
                  </div>
                  <span className="checkout-item-price">
                    ${(Number(item.price) * item.quantity).toFixed(2)}
                  </span>
                </div>
              ))}
            </div>
            <div className="checkout-total-row">
              <span>Subtotal</span>
              <span>${subtotal.toFixed(2)}</span>
            </div>
            <div className="checkout-total-row">
              <span>Shipping</span>
              <span className="free-shipping">Free</span>
            </div>
            <div className="checkout-total-row grand-total">
              <span>Total</span>
              <span>${subtotal.toFixed(2)}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
