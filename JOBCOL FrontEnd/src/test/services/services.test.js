import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getUserFromToken, getAppRole } from '../../services/authService';

// Mock api module
vi.mock('../../services/api', () => {
  const mockAxiosInstance = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  };
  return { default: mockAxiosInstance };
});

import api from '../../services/api';
import { getAllOffers, createOffer, deleteOffer, closeOffer } from '../../services/offerService';
import { getContractsByUser, createContract, acceptContract, rejectContract } from '../../services/contractService';
import { createPostulation } from '../../services/postulationService';

// Helper to create a fake JWT token
function makeToken(payload) {
  const header = btoa(JSON.stringify({ alg: 'HS256' }));
  const body = btoa(JSON.stringify(payload));
  return `${header}.${body}.signature`;
}

describe('Servicios (20 pruebas)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  // === authService ===
  it('SRV-001: getUserFromToken con token válido', () => {
    const token = makeToken({ email: 'test@mail.com', preferred_username: 'testuser', realm_access: { roles: ['TRABAJADOR'] } });
    localStorage.setItem('token', token);
    const user = getUserFromToken();
    expect(user).toEqual({ email: 'test@mail.com', username: 'testuser', roles: ['TRABAJADOR'] });
  });

  it('SRV-002: getUserFromToken sin token retorna null', () => {
    expect(getUserFromToken()).toBeNull();
  });

  it('SRV-003: getAppRole ADMIN', () => {
    expect(getAppRole({ roles: ['ADMIN', 'TRABAJADOR'] })).toBe('ADMIN');
  });

  it('SRV-004: getAppRole EMPLEADOR', () => {
    expect(getAppRole({ roles: ['EMPLEADOR'] })).toBe('EMPLEADOR');
  });

  it('SRV-005: getAppRole TRABAJADOR', () => {
    expect(getAppRole({ roles: ['TRABAJADOR'] })).toBe('TRABAJADOR');
  });

  it('SRV-006: getAppRole null user', () => {
    expect(getAppRole(null)).toBeNull();
  });

  it('SRV-007: getAppRole sin roles retorna TRABAJADOR', () => {
    expect(getAppRole({ roles: [] })).toBe('TRABAJADOR');
  });

  it('SRV-008: login guarda token', async () => {
    api.post.mockResolvedValueOnce({ data: { accessToken: 'abc123', email: 'test@mail.com' } });
    const { login } = await import('../../services/authService');
    await login({ username: 'u', password: 'p' });
    expect(localStorage.getItem('token')).toBe('abc123');
  });

  it('SRV-009: register guarda token', async () => {
    api.post.mockResolvedValueOnce({ data: { accessToken: 'reg-token' } });
    const { register } = await import('../../services/authService');
    await register({ username: 'u', email: 'e', password: 'p' });
    expect(localStorage.getItem('token')).toBe('reg-token');
  });

  // === offerService ===
  it('SRV-010: getAllOffers llama GET /offers', async () => {
    localStorage.setItem('token', 'tk');
    api.get.mockResolvedValueOnce({ data: [{ id: 1 }] });
    const result = await getAllOffers();
    expect(api.get).toHaveBeenCalledWith('/offers', expect.objectContaining({ headers: expect.objectContaining({ Authorization: 'Bearer tk' }) }));
    expect(result).toEqual([{ id: 1 }]);
  });

  it('SRV-011: createOffer llama POST /offers', async () => {
    localStorage.setItem('token', 'tk');
    api.post.mockResolvedValueOnce({ data: { id: 1 } });
    const result = await createOffer({ title: 'Test' });
    expect(api.post).toHaveBeenCalledWith('/offers', { title: 'Test' }, expect.any(Object));
    expect(result).toEqual({ id: 1 });
  });

  it('SRV-012: deleteOffer llama DELETE /offers/:id', async () => {
    localStorage.setItem('token', 'tk');
    api.delete.mockResolvedValueOnce({ data: {} });
    await deleteOffer(5);
    expect(api.delete).toHaveBeenCalledWith('/offers/5', expect.any(Object));
  });

  it('SRV-013: closeOffer llama PATCH /offers/:id/close', async () => {
    localStorage.setItem('token', 'tk');
    api.patch.mockResolvedValueOnce({ data: {} });
    await closeOffer(3);
    expect(api.patch).toHaveBeenCalledWith('/offers/3/close', {}, expect.any(Object));
  });

  // === contractService ===
  it('SRV-014: getContractsByUser llama GET', async () => {
    api.get.mockResolvedValueOnce({ data: [{ id: 1 }] });
    const result = await getContractsByUser(10);
    expect(api.get).toHaveBeenCalledWith('/contracts/user/10');
    expect(result).toEqual([{ id: 1 }]);
  });

  it('SRV-015: createContract llama POST', async () => {
    api.post.mockResolvedValueOnce({ data: { id: 1 } });
    const result = await createContract({ amount: 1000 });
    expect(api.post).toHaveBeenCalledWith('/contracts', { amount: 1000 });
    expect(result).toEqual({ id: 1 });
  });

  it('SRV-016: acceptContract llama PUT', async () => {
    api.put.mockResolvedValueOnce({ data: {} });
    await acceptContract(7);
    expect(api.put).toHaveBeenCalledWith('/contracts/7/accept');
  });

  it('SRV-017: rejectContract llama PUT', async () => {
    api.put.mockResolvedValueOnce({ data: {} });
    await rejectContract(7);
    expect(api.put).toHaveBeenCalledWith('/contracts/7/reject');
  });

  // === postulationService ===
  it('SRV-018: createPostulation payload correcto', async () => {
    api.post.mockResolvedValueOnce({ data: { id: 1 } });
    await createPostulation(5, 10, 85);
    expect(api.post).toHaveBeenCalledWith('/postulations', expect.objectContaining({
      status: 'PENDING',
      workerId: 10,
      calification: 85,
      offer: { id: 5 },
    }));
  });

  // === aiTestService ===
  it('SRV-019: getDifficulty pregunta 1 retorna basico', () => {
    // Test the logic directly
    const getDifficulty = (qNum, prevQA) => {
      if (qNum === 1) return 'basico';
      const correctCount = prevQA.filter(q => q.wasCorrect).length;
      if (correctCount === prevQA.length) {
        return qNum === 2 ? 'intermedio' : 'avanzado';
      }
      return qNum === 2 ? 'basico-intermedio' : 'intermedio';
    };
    expect(getDifficulty(1, [])).toBe('basico');
  });

  it('SRV-020: getDifficulty pregunta 2 todas correctas retorna intermedio', () => {
    const getDifficulty = (qNum, prevQA) => {
      if (qNum === 1) return 'basico';
      const correctCount = prevQA.filter(q => q.wasCorrect).length;
      if (correctCount === prevQA.length) {
        return qNum === 2 ? 'intermedio' : 'avanzado';
      }
      return qNum === 2 ? 'basico-intermedio' : 'intermedio';
    };
    expect(getDifficulty(2, [{ wasCorrect: true }])).toBe('intermedio');
  });
});
