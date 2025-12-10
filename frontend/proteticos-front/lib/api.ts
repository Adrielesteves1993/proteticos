const API_BASE = 'http://localhost:8080/api';

export async function apiGet(endpoint: string) {
  const response = await fetch(`${API_BASE}${endpoint}`);
  if (!response.ok) {
    throw new Error(`Erro API: ${response.status}`);
  }
  return response.json();
}

export async function apiPost(endpoint: string, data: any) {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    throw new Error(`Erro API: ${response.status}`);
  }
  return response.json();
}

export async function apiPut(endpoint: string, data: any) {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    throw new Error(`Erro API: ${response.status}`);
  }
  return response.json();
}

export async function apiDelete(endpoint: string) {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    throw new Error(`Erro API: ${response.status}`);
  }
  return response.ok;
}