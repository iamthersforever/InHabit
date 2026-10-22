const configuredBase = import.meta.env.VITE_API_URL || '/api';
export const API_BASE = configuredBase.replace(/\/+$/, '');

export function assetUrl(value) {
  if (!value) return '';
  if (/^https?:\/\//i.test(value)) return value;
  const origin = API_BASE.endsWith('/api') ? API_BASE.slice(0, -4) : API_BASE;
  return `${origin}${value.startsWith('/') ? value : `/${value}`}`;
}

export async function api(path, options = {}) {
  const { body, headers = {}, ...rest } = options;
  const requestHeaders = { ...headers };
  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('inhabit_token') : '';
  if (token) requestHeaders.Authorization = `Bearer ${token}`;
  let requestBody = body;
  if (body !== undefined && body !== null && !(body instanceof FormData) && typeof body !== 'string') {
    requestHeaders['Content-Type'] = 'application/json';
    requestBody = JSON.stringify(body);
  }
  const response = await fetch(`${API_BASE}${path.startsWith('/') ? path : `/${path}`}`, {
    ...rest, headers: requestHeaders, body: requestBody
  });
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(payload.message || `Request failed (${response.status})`);
  return payload;
}
