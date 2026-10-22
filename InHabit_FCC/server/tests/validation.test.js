import test from 'node:test';
import assert from 'node:assert/strict';
import { CATEGORIES, COMPLAINT_STATUSES, cleanText, escapeRegex, isAllowedStatus, isEmail, makeTicketNumber, normalizeEmail } from '../src/utils/validation.js';

test('email input is trimmed and normalized', () => {
  assert.equal(normalizeEmail('  RAGHAV@EXAMPLE.COM '), 'raghav@example.com');
  assert.equal(isEmail('student@example.edu'), true);
  assert.equal(isEmail('not-an-email'), false);
});

test('input text is trimmed, length-limited, and has NUL bytes removed', () => {
  assert.equal(cleanText('  hello\u0000 world  '), 'hello world');
  assert.equal(cleanText('123456', 4), '1234');
});

test('search text is escaped as literal text rather than regex instructions', () => {
  const literal = new RegExp(escapeRegex('.*[ticket]'));
  assert.equal(literal.test('prefix .*[ticket] suffix'), true);
  assert.equal(literal.test('prefix ticket suffix'), false);
});

test('status/category allow-lists are defined and ticket IDs are shaped correctly', () => {
  assert.equal(isAllowedStatus('In Progress'), true);
  assert.equal(isAllowedStatus('DROP TABLE'), false);
  assert.ok(CATEGORIES.includes('Internet'));
  assert.ok(COMPLAINT_STATUSES.includes('Resolved'));
  assert.match(makeTicketNumber(), /^INH-[A-Z0-9]+-[A-Z0-9]{3}$/);
});
