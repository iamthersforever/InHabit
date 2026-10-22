export const CATEGORIES = ['Internet', 'Water', 'Electricity', 'Cleanliness', 'Mess/Food', 'Security', 'Plumbing', 'Electrical', 'Mess', 'Other'];
export const COMPLAINT_STATUSES = ['Open', 'Under Review', 'In Progress', 'Resolved', 'Closed'];
export const LOST_FOUND_TYPES = ['Lost', 'Found'];

export function normalizeEmail(value = '') {
  return String(value).trim().toLowerCase();
}

export function cleanText(value = '', maxLength = 2000) {
  return String(value).replace(/\u0000/g, '').trim().slice(0, maxLength);
}

export function isEmail(value = '') {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(normalizeEmail(value));
}

// Escape user-entered search text before passing it to MongoDB's regular-expression engine.
export function escapeRegex(value = '') {
  return String(value).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

export function isAllowedStatus(value) {
  return COMPLAINT_STATUSES.includes(value);
}

export function makeTicketNumber() {
  const stamp = Date.now().toString(36).toUpperCase();
  const salt = Math.random().toString(36).slice(2, 5).toUpperCase();
  return `INH-${stamp.slice(-5)}-${salt}`;
}
