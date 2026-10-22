import mongoose from 'mongoose';

let memoryServerInstance = null;

export async function connectDatabase() {
  let uri = process.env.MONGODB_URI;
  if (!uri) {
    uri = 'mongodb://127.0.0.1:27017/inhabit_fcc';
  }
  mongoose.set('strictQuery', true);

  // 1. Attempt connection to configured URI (Atlas or local mongod)
  try {
    await mongoose.connect(uri, { serverSelectionTimeoutMS: 2000 });
    console.log(`✓ MongoDB connected: ${mongoose.connection.name} (${uri.includes('@') ? 'Cloud Atlas' : 'Local mongod'})`);
    return;
  } catch (err) {
    // If it's a remote Atlas URI that failed, throw so the user knows credentials/IP issue
    if (uri.startsWith('mongodb+srv://') || (uri.includes('@') && !uri.includes('127.0.0.1') && !uri.includes('localhost'))) {
      throw new Error(`Could not connect to MongoDB Atlas cluster: ${err.message}`);
    }

    console.log(`ℹ Local MongoDB daemon not active at ${uri}.`);
    console.log(`⚡ Starting standalone MongoDB engine for evaluation...`);
  }

  // 2. Standalone fallback: start embedded MongoDB engine with persistent storage
  try {
    const { MongoMemoryServer } = await import('mongodb-memory-server');
    const path = await import('node:path');
    const fs = await import('node:fs');
    const { fileURLToPath } = await import('node:url');

    const __dirname = path.dirname(fileURLToPath(import.meta.url));
    const dbPath = path.resolve(__dirname, '../../.mongo_data');
    fs.mkdirSync(dbPath, { recursive: true });

    memoryServerInstance = await MongoMemoryServer.create({
      instance: {
        dbPath,
        storageEngine: 'wiredTiger'
      }
    });

    const fallbackUri = `${memoryServerInstance.getUri()}inhabit_fcc`;
    await mongoose.connect(fallbackUri);
    console.log(`✓ Standalone MongoDB engine online: ${fallbackUri}`);
    console.log(`✓ Persistent data stored in server/.mongo_data`);
  } catch (fallbackErr) {
    throw new Error(`Failed to initialize MongoDB connection or standalone engine: ${fallbackErr.message}`);
  }
}

export async function stopDatabase() {
  try {
    await mongoose.disconnect();
  } catch {}
  if (memoryServerInstance) {
    try {
      await memoryServerInstance.stop();
    } catch {}
  }
}
