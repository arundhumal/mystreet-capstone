# Free Cloud Deployment Guide (Demo)

This setup is the fastest low-cost path for your demo:

- Backend (Spring Boot): Render (free tier / free credits)
- Frontend (Vite React): Netlify (free)

## 1) Deploy Backend on Render

1. Push your project to GitHub.
2. In Render, click New + and choose Web Service.
3. Connect your GitHub repo.
4. Configure service:
	- Root Directory: Backend/capstone-service
	- Runtime: Java
	- Build Command: mvn clean package -DskipTests
	- Start Command: java -jar target/capstone-service-0.0.1.jar
5. Add environment variables:
	- JWT_SECRET = any long random string (64+ chars)
6. Deploy.

After deploy, copy backend URL, for example:

- https://your-backend.onrender.com

Swagger will be available at:

- https://your-backend.onrender.com/swagger-ui/index.html

## 2) Deploy Frontend on Netlify

1. In Netlify, click Add new site and import from Git.
2. Select your repo.
3. Configure build:
	- Base directory: Frontend/frontend
	- Build command: npm run build
	- Publish directory: dist
4. Add environment variable:
	- VITE_API_BASE_URL = https://your-backend.onrender.com/api
5. Deploy.

## 3) Verify Demo Flow

1. Open frontend URL from Netlify.
2. Register a user.
3. Login.
4. Browse products.
5. Add to cart and place order.
6. Open backend Swagger URL and verify endpoints are live.

## Notes for Free Tier

- First request can be slow if backend was idle (cold start).
- H2 database is in-memory, so data resets when service restarts.
- For demo reliability, pre-warm backend by opening Swagger a few minutes before presentation.

