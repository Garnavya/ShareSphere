require('dotenv').config();
const app = require('./app');
const prisma = require('./config/db');

const PORT = process.env.PORT || 5000;

async function startServer() {
  try {
    // Verify database connectivity
    await prisma.$connect();
    console.log('✓ Successfully connected to MySQL database via Prisma');

    app.listen(PORT, () => {
      console.log(`🚀 ShareSphere REST API running on port ${PORT} [${process.env.NODE_ENV || 'development'}]`);
      console.log(`📡 Health Check: http://localhost:${PORT}/health`);
    });
  } catch (error) {
    console.error('Failed to start server:', error);
    process.exit(1);
  }
}

startServer();
