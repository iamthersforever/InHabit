import 'dotenv/config';
import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { connectDatabase } from './config/db.js';
import authRoutes from './routes/auth.routes.js';
import complaintRoutes from './routes/complaints.routes.js';
import lostFoundRoutes from './routes/lostFound.routes.js';
import dashboardRoutes from './routes/dashboard.routes.js';
import { publishAudit } from './services/audit.js';

const app = express();
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const allowedOrigins = new Set([
  'http://localhost:5173', 'http://127.0.0.1:5173',
  ...(process.env.FRONTEND_URL || '').split(',').map((value) => value.trim()).filter(Boolean)
]);

app.disable('x-powered-by');
app.use(helmet({ crossOriginResourcePolicy: { policy: 'cross-origin' } }));
app.use(cors({
  origin(origin, callback) {
    // Postman and server-to-server requests may omit Origin.
    if (!origin || allowedOrigins.has(origin)) return callback(null, true);
    return callback(new Error('Origin is not allowed by InHabit API CORS policy.'));
  },
  credentials: false
}));
app.use(express.json({ limit: '1mb' }));
app.use(express.urlencoded({ extended: false, limit: '1mb' }));
app.use('/uploads', express.static(path.resolve(__dirname, '../uploads'), { maxAge: '1h', immutable: false }));

// The URL core module is used to normalise the request path for the audit record.
app.use((req, _res, next) => {
  try {
    const parsed = new URL(req.originalUrl, `http://${req.headers.host || 'localhost'}`);
    publishAudit('http.request', { method: req.method, path: parsed.pathname });
  } catch { /* malformed URLs are rejected by Express routing */ }
  next();
});

app.get('/', (_req, res) => res.json({
  name: 'InHabit FCC API', status: 'running',
  health: '/api/health', endpoints: ['/api/auth', '/api/complaints', '/api/lost-found', '/api/dashboard']
}));
app.get('/api/health', (_req, res) => res.json({ status: 'ok', database: 'check /api/health/db', at: new Date().toISOString() }));
app.get('/api/health/db', async (_req, res) => {
  const mongoose = await import('mongoose');
  const ready = mongoose.default.connection.readyState === 1;
  return res.status(ready ? 200 : 503).json({ database: ready ? 'connected' : 'disconnected' });
});

app.use('/api/auth', authRoutes);
app.use('/api/complaints', complaintRoutes);
app.use('/api/lost-found', lostFoundRoutes);
app.use('/api/dashboard', dashboardRoutes);

app.use((_req, res) => res.status(404).json({ message: 'Endpoint not found.' }));
app.use((error, _req, res, _next) => {
  if (error?.name === 'MulterError') return res.status(400).json({ message: error.code === 'LIMIT_FILE_SIZE' ? 'Image must be 5 MB or smaller.' : error.message });
  if (error?.message?.startsWith('Only JPG')) return res.status(400).json({ message: error.message });
  if (error?.message?.includes('CORS policy')) return res.status(403).json({ message: error.message });
  if (error?.code === 11000) return res.status(409).json({ message: 'A record with one of these unique fields already exists.' });
  console.error(error);
  return res.status(error.status || 500).json({ message: process.env.NODE_ENV === 'production' ? 'The server could not complete that request.' : error.message || 'Internal server error.' });
});

const port = Number(process.env.PORT || 5000);
connectDatabase()
  .then(() => app.listen(port, () => console.log(`✓ InHabit API listening on http://localhost:${port}`)))
  .catch((error) => {
    console.error(`✗ Could not connect to MongoDB: ${error.message}`);
    console.error('Configure MONGODB_URI in .env. The FCC build intentionally requires MongoDB instead of falling back to MySQL/in-memory data.');
    process.exit(1);
  });

export default app;
