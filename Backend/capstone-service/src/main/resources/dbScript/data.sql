-- Insert Admin User (password: admin123)
INSERT INTO users (id, email, password_hash, is_admin, created_at) 
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'admin@mystreet.com', '$2a$10$0VagX4/9vp7pT0g.qKCzUuSyUQ7Gwmw2U0T4B22auXIqJOaG1zD1W', true, CURRENT_TIMESTAMP);

-- Insert Regular User (password: user123)
INSERT INTO users (id, email, password_hash, is_admin, created_at) 
VALUES ('550e8400-e29b-41d4-a716-446655440002', 'user@mystreet.com', '$2a$10$6N3bFtPPrNCjk7JpnSYNmO6PB.LwUoSITVFKlRI2V8LvabiLQU0hK', false, CURRENT_TIMESTAMP);

-- Insert Products with Unsplash Images
INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440001', 'Air Max 90', 'Nike', 'Classic retro vibe with visible Air cushioning. Perfect for everyday wear with iconic design.', 119.99, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=400&q=80', '7,8,9,10,11', 50, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440002', 'Ultraboost 22', 'Adidas', 'Responsive cushioning for ultimate comfort. Energy-returning boost technology.', 139.99, 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400&h=400&fit=crop', '7,8,9,10,11,12', 35, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440003', 'Chuck Taylor All Star', 'Converse', 'Iconic canvas sneaker. Timeless style that never goes out of fashion.', 59.99, 'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=400&h=400&fit=crop', '6,7,8,9,10,11', 100, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440004', 'Classic Leather', 'Reebok', 'Timeless leather design with superior comfort. A wardrobe essential.', 79.99, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400&h=400&fit=crop', '7,8,9,10,11', 45, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440005', '574 Core', 'New Balance', 'Versatile everyday sneaker with ENCAP midsole technology.', 89.99, 'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400&h=400&fit=crop', '7,8,9,10,11,12', 60, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440006', 'Suede Classic', 'Puma', 'Iconic suede sneaker. Street style meets comfort.', 69.99, 'https://images.unsplash.com/photo-1584735175315-9d5df23860e6?w=400&h=400&fit=crop', '7,8,9,10,11', 55, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440007', 'Air Jordan 1', 'Nike', 'Legendary basketball sneaker. The shoe that started it all.', 169.99, 'https://images.unsplash.com/photo-1556906781-9a412961c28c?w=400&h=400&fit=crop', '8,9,10,11,12', 30, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440008', 'Stan Smith', 'Adidas', 'Clean and minimalist tennis shoe. A true classic.', 94.99, 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=400&h=400&fit=crop', '7,8,9,10,11', 70, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440009', 'Cortez', 'Nike', 'Retro running shoe with a sleek profile. Vintage vibes.', 84.99, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop', '7,8,9,10,11', 40, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440010', 'Gazelle', 'Adidas', 'Vintage-inspired sneaker with premium suede upper.', 99.99, 'https://images.unsplash.com/photo-1552346154-21d32810aba3?w=400&h=400&fit=crop', '7,8,9,10,11,12', 50, CURRENT_TIMESTAMP);

-- Add more sneaker products
INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440011', 'Blazer Mid', 'Nike', 'Vintage basketball style with modern comfort. Elevated street style.', 99.99, 'https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=400&h=400&fit=crop', '7,8,9,10,11', 65, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440012', 'Superstar', 'Adidas', 'Iconic shell-toe design. Hip-hop heritage meets modern style.', 89.99, 'https://via.placeholder.com/400x400/FBE7C6/000000?text=Superstar', '7,8,9,10,11,12', 80, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440013', 'Old Skool', 'Vans', 'Classic skate shoe with signature side stripe. Timeless design.', 64.99, 'https://images.unsplash.com/photo-1543508282-6319a3e2621f?w=400&h=400&fit=crop', '7,8,9,10,11', 90, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440014', 'Gel-Lyte III', 'Asics', 'Split-tongue design with premium comfort. Running heritage.', 119.99, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400&h=400&fit=crop', '7,8,9,10,11', 40, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440015', 'ZX 2K Boost', 'Adidas', 'Futuristic design with boost cushioning. Next-gen comfort.', 129.99, 'https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400&h=400&fit=crop', '7,8,9,10,11,12', 55, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, brand, description, price, image_url, sizes_csv, stock_qty, created_at) 
VALUES ('650e8400-e29b-41d4-a716-446655440016', 'React Infinity Run', 'Nike', 'Maximum cushioning for long-distance running. Injury prevention design.', 159.99, 'https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=400&h=400&fit=crop', '7,8,9,10,11,12', 45, CURRENT_TIMESTAMP);
