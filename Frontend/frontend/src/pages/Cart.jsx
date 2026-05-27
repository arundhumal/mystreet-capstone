import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { orderAPI } from '../api/axios';

const Cart = () => {
  const { cart, removeFromCart, updateCartQuantity, clearCart } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showCheckout, setShowCheckout] = useState(false);
  const [shippingAddress, setShippingAddress] = useState('');
  const [paymentMode, setPaymentMode] = useState('');
  const navigate = useNavigate();

  const calculateTotal = () => {
    return cart.reduce((total, item) => {
      return total + (Number(item.price) * item.quantity);
    }, 0).toFixed(2);
  };

  const handleQuantityChange = (productId, newQuantity) => {
    if (newQuantity < 1) return;
    updateCartQuantity(productId, newQuantity);
  };

  const handleCheckout = async (e) => {
    e.preventDefault();
    setError('');

    if (!shippingAddress.trim()) {
      const msg = 'Please enter a shipping address';
      alert(msg);
      setError(msg);
      return;
    }

    if (!paymentMode) {
      const msg = 'Please select a payment mode';
      alert(msg);
      setError(msg);
      return;
    }

    if (cart.length === 0) {
      const msg = 'Your cart is empty';
      alert(msg);
      setError(msg);
      return;
    }

    setLoading(true);

    try {
      const orderData = {
        items: cart.map(item => ({
          productId: item.id,
          quantity: item.quantity,
          price: item.price
        })),
        totalAmount: calculateTotal(),
        shippingAddress: shippingAddress.trim(),
        paymentMode: paymentMode
      };

      console.log('Sending order:', orderData);

      const response = await orderAPI.create(orderData);
      console.log('Order created:', response.data);

      alert('Order placed successfully! 🎉');
      clearCart();
      navigate('/orders');
    } catch (err) {
      console.error('Checkout error:', err);
      
      let errorMessage = 'Failed to place order. Please try again.';
      
      if (err.response) {
        if (err.response.data?.message) {
          errorMessage = err.response.data.message;
        } else if (err.response.data?.error) {
          errorMessage = err.response.data.error;
        }
        
        // Show validation errors if present
        if (err.response.data?.errors) {
          const validationErrors = Object.entries(err.response.data.errors)
            .map(([field, message]) => `${field}: ${message}`)
            .join('\n');
          errorMessage = `Validation errors:\n${validationErrors}`;
        }
      } else if (err.request) {
        errorMessage = 'No response from server. Please check your connection.';
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      alert(errorMessage);
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (cart.length === 0) {
    return (
      <div className="cart-empty">
        <h2>Your Cart is Empty</h2>
        <p>Add some products to your cart to get started!</p>
        <button onClick={() => navigate('/products')} className="btn btn-primary">
          Browse Products
        </button>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <h1>Shopping Cart</h1>

      {error && (
        <div className="error-message">
          ⚠️ {error}
        </div>
      )}

      <div className="cart-content">
        <div className="cart-items">
          {cart.map((item) => (
            <div key={item.id} className="cart-item">
              <img src={item.imageUrl} alt={item.name} className="cart-item-image" />
              <div className="cart-item-details">
                <h3>{item.name}</h3>
                <p className="cart-item-brand">{item.brand}</p>
                <p className="cart-item-price">${Number(item.price).toFixed(2)}</p>
              </div>
              <div className="cart-item-quantity">
                <button
                  onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                  className="btn btn-sm"
                  disabled={item.quantity <= 1}
                >
                  -
                </button>
                <span>{item.quantity}</span>
                <button
                  onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                  className="btn btn-sm"
                >
                  +
                </button>
              </div>
              <div className="cart-item-total">
                ${(Number(item.price) * item.quantity).toFixed(2)}
              </div>
              <button
                onClick={() => removeFromCart(item.id)}
                className="btn btn-danger btn-sm"
              >
                Remove
              </button>
            </div>
          ))}
        </div>

        <div className="cart-summary">
          <h2>Order Summary</h2>
          <div className="summary-row">
            <span>Subtotal:</span>
            <span>${calculateTotal()}</span>
          </div>
          <div className="summary-row">
            <span>Shipping:</span>
            <span>Free</span>
          </div>
          <div className="summary-row total">
            <span>Total:</span>
            <span>${calculateTotal()}</span>
          </div>

          {!showCheckout ? (
            <button
              onClick={() => setShowCheckout(true)}
              className="btn btn-primary btn-block"
            >
              Proceed to Checkout
            </button>
          ) : (
            <form onSubmit={handleCheckout} className="checkout-form">
              <h3>Checkout Details</h3>
              
              <div className="form-group">
                <label htmlFor="shippingAddress">Shipping Address *</label>
                <textarea
                  id="shippingAddress"
                  value={shippingAddress}
                  onChange={(e) => setShippingAddress(e.target.value)}
                  placeholder="Enter your complete shipping address"
                  rows="4"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="paymentMode">Payment Mode *</label>
                <select
                  id="paymentMode"
                  value={paymentMode}
                  onChange={(e) => setPaymentMode(e.target.value)}
                  required
                >
                  <option value="">Select payment mode</option>
                  <option value="CREDIT_CARD">Credit Card</option>
                  <option value="DEBIT_CARD">Debit Card</option>
                  <option value="UPI">UPI</option>
                  <option value="NET_BANKING">Net Banking</option>
                  <option value="CASH_ON_DELIVERY">Cash on Delivery</option>
                </select>
              </div>

              <div className="checkout-actions">
                <button
                  type="button"
                  onClick={() => setShowCheckout(false)}
                  className="btn btn-secondary"
                  disabled={loading}
                >
                  Back
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Placing Order...' : 'Place Order'}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default Cart;