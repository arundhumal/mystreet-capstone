import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import './AuthModal.css';

const AuthModal = () => {
  const { login, register, authModalOpen, authModalMode, closeAuthModal } = useAuth();
  const [isSignIn, setIsSignIn] = useState(true);

  // Sync mode whenever the modal opens
  useEffect(() => {
    if (authModalOpen) {
      setIsSignIn(authModalMode === 'login');
      resetForm();
    }
  }, [authModalOpen, authModalMode]);
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      if (isSignIn) {
        await login(formData.email, formData.password);
      } else {
        await register(formData.email, formData.password);
      }
      resetForm();
      closeAuthModal();
    } catch (err) {
      setError(err?.response?.data?.message || err?.message || 'Something went wrong');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({ email: '', password: '' });
    setError('');
  };

  const toggleMode = () => {
    setIsSignIn(!isSignIn);
    resetForm();
  };

  const handleOverlayClick = (e) => {
    if (e.target.className === 'modal-overlay') {
      closeAuthModal();
      resetForm();
    }
  };

  if (!authModalOpen) return null;

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="modal-container">
        <button
          className="modal-close"
          onClick={() => {
            closeAuthModal();
            resetForm();
          }}
        >
          &times;
        </button>

        <div className="modal-header">
          <h2>{isSignIn ? 'Sign In' : 'Sign Up'}</h2>
          <p>{isSignIn ? 'Welcome back!' : 'Create your account'}</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
              minLength="6"
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="submit-btn" disabled={loading}>
            {loading
              ? isSignIn ? 'Signing in...' : 'Signing up...'
              : isSignIn ? 'Sign In' : 'Sign Up'}
          </button>
        </form>

        <div className="modal-footer">
          <p>
            {isSignIn ? "Don't have an account? " : 'Already have an account? '}
            <button onClick={toggleMode} className="toggle-btn">
              {isSignIn ? 'Sign Up' : 'Sign In'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default AuthModal;
