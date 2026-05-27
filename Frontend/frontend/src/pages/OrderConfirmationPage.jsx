import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderAPI } from '../api/axios';

const OrderConfirmationPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    orderAPI
      .getById(id)
      .then((r) => setOrder(r.data))
      .catch(() => setError('Order not found'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="loading">Loading order...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="confirmation-page">
      <div className="confirmation-card">
        <div className="confirmation-icon">✓</div>
        <h1>Order Placed!</h1>
        <p className="confirmation-subtitle">
          Thank you for your order. We'll have it shipped soon.
        </p>

        <div className="confirmation-order-id">
          <span>Order ID</span>
          <strong>#{order.id.toUpperCase()}</strong>
        </div>

        {/* Items */}
        <div className="confirmation-section">
          <h3>Items Ordered</h3>
          <div className="confirmation-items">
            {order.items.map((item, idx) => (
              <div key={idx} className="confirmation-item">
                <div className="conf-item-details">
                  <span className="conf-item-name">{item.productName}</span>
                  <span className="conf-item-meta">Size: US {item.size} · Qty: {item.quantity}</span>
                </div>
                <span className="conf-item-price">
                  ${Number(item.priceAtOrder).toFixed(2)}
                </span>
              </div>
            ))}
          </div>
          <div className="confirmation-total">
            <span>Total</span>
            <strong>${Number(order.totalAmount).toFixed(2)}</strong>
          </div>
        </div>

        {/* Shipping & Payment */}
        <div className="confirmation-section">
          <h3>Delivery Details</h3>
          <div className="confirmation-meta-grid">
            <div className="conf-meta-item">
              <span className="conf-meta-label">Shipping Address</span>
              <span className="conf-meta-value">{order.shippingAddress}</span>
            </div>
            <div className="conf-meta-item">
              <span className="conf-meta-label">Payment Method</span>
              <span className="conf-meta-value">
                {order.paymentMode === 'COD' ? 'Cash on Delivery' : 'Mock UPI'}
              </span>
            </div>
            <div className="conf-meta-item">
              <span className="conf-meta-label">Order Status</span>
              <span className="order-status conf-status">{order.status}</span>
            </div>
          </div>
        </div>

        {/* Actions */}
        <div className="confirmation-actions">
          <button onClick={() => navigate('/orders')} className="btn btn-secondary">
            View My Orders
          </button>
          <button onClick={() => navigate('/products')} className="btn btn-primary">
            Continue Shopping
          </button>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmationPage;
