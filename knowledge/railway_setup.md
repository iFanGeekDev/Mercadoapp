# Railway Integration Knowledge

## Project: MercadoApp / YapaMarket

### Connection Details
The Railway production database is used for the backend deployment.
- **External Connection String**: `postgresql://postgres:OHAmrhVGrNDsdLTymQUKxOfnYUCLmhtV@trolley.proxy.rlwy.net:55654/railway`
- **Internal Connection String**: `postgresql://postgres:OHAmrhVGrNDsdLTymQUKxOfnYUCLmhtV@postgres.railway.internal:5432/railway`

### Deployment Workflow
Whenever a change is made to the database schema in `backend/database.sql`, it must be applied to both:
1. **Local Database**: `localhost:5432/yapamarket`
2. **Railway Database**: Using the external connection string above.

### Configuration
The backend is configured to use the `DATABASE_URL` environment variable. A `.env` file has been created in the `backend` directory to store this URL locally for maintenance tasks.
