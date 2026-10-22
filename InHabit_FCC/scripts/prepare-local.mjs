import { randomBytes } from 'node:crypto';
import { existsSync, readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const serverDir = path.join(root, 'server');
const envPath = path.join(serverDir, '.env');
const examplePath = path.join(serverDir, '.env.example');

mkdirSync(serverDir, { recursive: true });
if (!existsSync(envPath)) {
  if (!existsSync(examplePath)) {
    console.error('Cannot find server/.env.example. Re-extract the full InHabit FCC ZIP.');
    process.exit(1);
  }
  const secret = randomBytes(32).toString('hex');
  const contents = readFileSync(examplePath, 'utf8')
    .replace(/^JWT_SECRET=.*$/m, `JWT_SECRET=${secret}`);
  writeFileSync(envPath, contents, { encoding: 'utf8', mode: 0o600 });
  console.log('Created server/.env and generated a local JWT secret.');
} else {
  let contents = readFileSync(envPath, 'utf8');
  if (/^JWT_SECRET=replace-with-a-long-random-secret\s*$/m.test(contents)) {
    contents = contents.replace(/^JWT_SECRET=.*$/m, `JWT_SECRET=${randomBytes(32).toString('hex')}`);
    writeFileSync(envPath, contents, 'utf8');
    console.log('Replaced the example JWT secret with a generated local secret.');
  } else {
    console.log('Keeping your existing server/.env settings.');
  }
}
console.log('MongoDB URI is read from server/.env (Atlas or local mongod). Standalone MongoDB engine is automatically enabled if no external daemon is running.');
