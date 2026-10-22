import { EventEmitter } from 'node:events';
import { appendFile, mkdir } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const logDirectory = path.resolve(__dirname, '../../logs');
const eventBus = new EventEmitter();

// Node.js Events + File System modules: domain activity is appended to a local audit log.
eventBus.on('audit', async (record) => {
  try {
    await mkdir(logDirectory, { recursive: true });
    await appendFile(path.join(logDirectory, 'audit.jsonl'), `${JSON.stringify(record)}\n`, 'utf8');
  } catch (error) {
    // Keep the main API working even when the platform's filesystem is read-only/ephemeral.
    console.warn('Audit log unavailable:', error.message);
  }
});

export function publishAudit(type, details = {}) {
  eventBus.emit('audit', { type, at: new Date().toISOString(), ...details });
}

export { eventBus };
