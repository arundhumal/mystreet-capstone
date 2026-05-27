import { useState, useEffect } from 'react';
import { productAPI } from '../api/axios';

const EMPTY_FORM = {
  name: '',
  brand: '',
  description: '',
  price: '',
  imageUrl: '',
  sizesCsv: '',
  stockQty: '',
};

const AdminPage = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null); // null = create mode
  const [formData, setFormData] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState('');
  const [formLoading, setFormLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await productAPI.getAll();
      setProducts(response.data);
      setError(null);
    } catch {
      setError('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const openCreateForm = () => {
    setEditingProduct(null);
    setFormData(EMPTY_FORM);
    setFormError('');
    setShowForm(true);
  };

  const openEditForm = (product) => {
    setEditingProduct(product);
    setFormData({
      name: product.name || '',
      brand: product.brand || '',
      description: product.description || '',
      price: product.price?.toString() || '',
      imageUrl: product.imageUrl || '',
      sizesCsv: product.sizesCsv || '',
      stockQty: product.stockQty?.toString() || '',
    });
    setFormError('');
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditingProduct(null);
    setFormData(EMPTY_FORM);
    setFormError('');
  };

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    if (formError) setFormError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormLoading(true);
    setFormError('');
    try {
      const payload = {
        ...formData,
        price: parseFloat(formData.price),
        stockQty: parseInt(formData.stockQty, 10),
      };
      if (editingProduct) {
        await productAPI.update(editingProduct.id, payload);
        flash('Product updated successfully');
      } else {
        await productAPI.create(payload);
        flash('Product created successfully');
      }
      closeForm();
      fetchProducts();
    } catch (err) {
      setFormError(err?.response?.data?.message || 'Failed to save product');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async (product) => {
    if (!window.confirm(`Delete "${product.name}"? This cannot be undone.`)) return;
    try {
      await productAPI.delete(product.id);
      flash('Product deleted');
      fetchProducts();
    } catch {
      alert('Failed to delete product');
    }
  };

  const flash = (msg) => {
    setSuccessMsg(msg);
    setTimeout(() => setSuccessMsg(''), 3000);
  };

  return (
    <div className="page">
      <div className="admin-header">
        <div>
          <h1>Product Management</h1>
          <p className="admin-subtitle">Manage your sneaker catalog</p>
        </div>
        <button onClick={openCreateForm} className="btn btn-primary">
          + Add Product
        </button>
      </div>

      {successMsg && <div className="success-message">{successMsg}</div>}
      {error && <div className="error">{error}</div>}

      {loading ? (
        <div className="loading">Loading products...</div>
      ) : (
        <div className="admin-table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Image</th>
                <th>Name</th>
                <th>Brand</th>
                <th>Price</th>
                <th>Sizes</th>
                <th>Stock</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.length === 0 ? (
                <tr>
                  <td colSpan="7" className="table-empty">No products yet. Click "Add Product" to create one.</td>
                </tr>
              ) : (
                products.map((p) => (
                  <tr key={p.id}>
                    <td>
                      <img
                        src={p.imageUrl || `https://placehold.co/60x60/4f46e5/ffffff?text=${encodeURIComponent(p.name)}`}
                        alt={p.name}
                        className="admin-product-img"
                        onError={(e) => { e.target.src = `https://placehold.co/60x60/4f46e5/ffffff?text=IMG`; }}
                      />
                    </td>
                    <td className="admin-product-name">{p.name}</td>
                    <td>{p.brand}</td>
                    <td>${Number(p.price).toFixed(2)}</td>
                    <td className="admin-sizes">{p.sizesCsv}</td>
                    <td>
                      <span className={`stock-badge ${p.stockQty > 0 ? 'in-stock' : 'out-of-stock'}`}>
                        {p.stockQty}
                      </span>
                    </td>
                    <td className="admin-actions">
                      <button onClick={() => openEditForm(p)} className="btn btn-secondary btn-small">
                        Edit
                      </button>
                      <button onClick={() => handleDelete(p)} className="btn btn-danger btn-small">
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Product Form Modal */}
      {showForm && (
        <div className="admin-modal-overlay" onClick={(e) => e.target.className === 'admin-modal-overlay' && closeForm()}>
          <div className="admin-modal">
            <div className="admin-modal-header">
              <h2>{editingProduct ? 'Edit Product' : 'Add New Product'}</h2>
              <button className="modal-close" onClick={closeForm}>&times;</button>
            </div>

            <form onSubmit={handleSubmit} className="admin-form">
              <div className="admin-form-row">
                <div className="form-group">
                  <label>Product Name *</label>
                  <input name="name" value={formData.name} onChange={handleChange} placeholder="e.g. Air Max 90" required />
                </div>
                <div className="form-group">
                  <label>Brand</label>
                  <input name="brand" value={formData.brand} onChange={handleChange} placeholder="e.g. Nike" />
                </div>
              </div>

              <div className="form-group">
                <label>Description</label>
                <textarea name="description" value={formData.description} onChange={handleChange} placeholder="Product description" rows={3} />
              </div>

              <div className="admin-form-row">
                <div className="form-group">
                  <label>Price ($) *</label>
                  <input type="number" step="0.01" min="0" name="price" value={formData.price} onChange={handleChange} placeholder="0.00" required />
                </div>
                <div className="form-group">
                  <label>Stock Quantity *</label>
                  <input type="number" min="0" name="stockQty" value={formData.stockQty} onChange={handleChange} placeholder="0" required />
                </div>
              </div>

              <div className="form-group">
                <label>Sizes (comma-separated) *</label>
                <input name="sizesCsv" value={formData.sizesCsv} onChange={handleChange} placeholder="e.g. 7,8,9,10,11" required />
                <small className="form-hint">Enter US shoe sizes separated by commas</small>
              </div>

              <div className="form-group">
                <label>Image URL</label>
                <input name="imageUrl" value={formData.imageUrl} onChange={handleChange} placeholder="https://..." />
              </div>

              {formError && <div className="error-message">{formError}</div>}

              <div className="admin-form-actions">
                <button type="button" onClick={closeForm} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={formLoading}>
                  {formLoading ? 'Saving...' : editingProduct ? 'Update Product' : 'Create Product'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPage;
