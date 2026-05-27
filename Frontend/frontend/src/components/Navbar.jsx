import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout, cart, isAuthenticated, isAdmin, openAuthModal } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const cartItemsCount = cart.reduce((total, item) => total + item.quantity, 0);

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          MyStreeT
        </Link>

        <ul className="navbar-menu">
          <li><Link to="/">Home</Link></li>
          <li><Link to="/products">Products</Link></li>
          {isAuthenticated && (
            <>
              <li><Link to="/orders">Orders</Link></li>
              <li>
                <Link to="/cart" className="cart-link">
                  Cart{' '}
                  {cartItemsCount > 0 && (
                    <span className="cart-badge">{cartItemsCount}</span>
                  )}
                </Link>
              </li>
              {isAdmin && (
                <li><Link to="/admin" className="admin-link">Admin</Link></li>
              )}
            </>
          )}
        </ul>

        <div className="navbar-auth">
          {isAuthenticated ? (
            <>
              <span className="user-name">{user?.email}</span>
              <button onClick={handleLogout} className="btn btn-secondary">
                Logout
              </button>
            </>
          ) : (
            <>
              <button onClick={() => openAuthModal('login')} className="btn btn-secondary">
                Login
              </button>
              <button onClick={() => openAuthModal('register')} className="btn btn-primary">
                Register
              </button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
