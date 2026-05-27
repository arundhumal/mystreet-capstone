import { Link } from 'react-router-dom';

const BRANDS = ['Nike', 'Adidas', 'Puma', 'New Balance', 'Vans', 'Converse'];

const FEATURES = [
  { icon: '🚚', title: 'Free Shipping', desc: 'On all orders over $50' },
  { icon: '✅', title: 'Authentic Only', desc: '100% genuine sneakers' },
  { icon: '🔄', title: 'Easy Returns', desc: '30-day hassle-free returns' },
  { icon: '🔒', title: 'Secure Checkout', desc: 'Your data is protected' },
];

const HomePage = () => {
  return (
    <div className="home-page">
      {/* Hero */}
      <div className="hero-section">
        <div className="hero-badge">✦ New Arrivals Every Week</div>
        <h1>
          Step Into Your<br />
          <span>Perfect Pair</span>
        </h1>
        <p>
          Discover the latest sneakers from top brands. Exclusive styles,
          unbeatable prices, delivered to your door.
        </p>
        <div className="hero-actions">
          <Link to="/products" className="hero-btn-primary">
            Shop Collection →
          </Link>
          <Link to="/products" className="hero-btn-secondary">
            View New Arrivals
          </Link>
        </div>
      </div>

      {/* Brands Strip */}
      <div className="brands-strip">
        <p>Featuring top brands</p>
        <div className="brand-pills">
          {BRANDS.map((b) => (
            <span key={b} className="brand-pill">{b}</span>
          ))}
        </div>
      </div>

      {/* Features */}
      <div className="features-section">
        {FEATURES.map((f) => (
          <div key={f.title} className="feature">
            <div className="feature-icon">{f.icon}</div>
            <h3>{f.title}</h3>
            <p>{f.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default HomePage;
