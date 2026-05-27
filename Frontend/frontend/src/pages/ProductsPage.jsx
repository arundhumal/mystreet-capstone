import ProductList from '../components/ProductList';

const ProductsPage = () => {
  return (
    <div className="page">
      <div className="page-header">
        <h1>Sneaker Catalog</h1>
        <p style={{ color: 'var(--text-light)', marginTop: '0.25rem' }}>
          Find your perfect pair
        </p>
      </div>
      <ProductList />
    </div>
  );
};

export default ProductsPage;
