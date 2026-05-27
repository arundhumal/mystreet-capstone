export interface User {
  id: string;
  email: string;
  isAdmin: boolean;
}

export interface Product {
  id: string;
  name: string;
  brand: string;
  description: string;
  price: number;
  imageUrl: string;
  sizesCsv: string;
  stockQty: number;
}

export interface CartItem {
  product: Product;
  size: string;
  quantity: number;
}

export interface Order {
  id: string;
  items: OrderItem[];
  shippingAddress: string;
  paymentMode: string;
  status: string;
  totalAmount: number;
  createdAt: string;
}

export interface OrderItem {
  productId: string;
  productName: string;
  size: string;
  quantity: number;
  priceAtOrder: number;
}

export interface AuthResponse {
  token: string;
  user: User;
}