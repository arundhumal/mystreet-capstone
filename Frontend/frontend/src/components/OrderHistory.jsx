import { useState, useEffect } from 'react';
import { orderAPI } from '../api/axios';
import { useNavigate } from 'react-router-dom';

const OrderHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    orderAPI
      .getMyOrders()
      .then((r) => setOrders(r.data))
      .catch(() => setError('Failed to load orders'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Loading orders...</div>;
  if (error) return <div className="error">{error}</div>;

  if (orders.length === 0) {
    return (
      <div className="orders-empty">
        <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>📦</div>
        <h2>No orders yet</h2>
        <p>Start shopping to see your orders here!</p>
        <button onClick={() => navigate('/products')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
          Shop Now
        </button>
      </div>
    );
  }

  const statusColor = (status) => {
    switch (status) {
      case 'PLACED': return '#1e40af';
      case 'SHIPPED': return '#7c3aed';
      case 'DELIVERED': return '#059669';
      default: return '#374151';
    }
  };

  const statusBg = (status) => {
    switch (status) {
      case 'PLACED': return '#dbeafe';
      case 'SHIPPED': return '#ede9fe';
      case 'DELIVERED': return '#d1fae5';
      default: return '#f3f4f6';
    }
  };

  return (
    <div className="orders-container">
      <h2>My Orders</h2>
      <div className="orders-list">
        {orders.map((order) => (
          <div key={order.id} className="order-card">
            <div className="order-header">
              <div>
                <h3>Order #{order.id.slice(0, 8).toUpperCase()}</h3>
                <span className="order-date">
                  {new Date(order.createdAt).toLocaleDateString('en-US', {
                    year: 'numeric', month: 'long', day: 'numeric',
                  })}
                </span>
              </div>
              <span
                className="order-status"
                style={{ color: statusColor(order.status), background: statusBg(order.status) }}
              >
                {order.status}
              </span>
            </div>

            <div className="order-items">
              {order.items.map((item, idx) => (
                <div key={idx} className="order-item">
                  <span className="order-item-name">{item.productName}</span>
                  <span className="order-item-size">Size: US {item.size}</span>
                  <span className="order-item-quantity">×{item.quantity}</span>
                  <span className="order-item-price">
                    ${Number(item.priceAtOrder).toFixed(2)}
                  </span>
                </div>
              ))}
            </div>

            <div className="order-footer">
              <div className="order-shipping">
                <span>📍 {order.shippingAddress}</span>
                <span className="order-payment">{order.paymentMode === 'COD' ? 'Cash on Delivery' : 'Mock UPI'}</span>
              </div>
              <span className="order-total">
                Total: ${Number(order.totalAmount).toFixed(2)}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default OrderHistory;
