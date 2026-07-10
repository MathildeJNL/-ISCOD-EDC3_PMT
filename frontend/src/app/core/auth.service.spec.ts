import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { AuthService } from './auth.service';
import { AuthResponse } from './models';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  const response: AuthResponse = {
    accessToken: 'demo-token',
    user: {
      id: 7,
      username: 'mathilde',
      email: 'mathilde@example.com',
      createdAt: '2026-07-08T10:00:00Z',
    },
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('stores the authenticated user after login', () => {
    service.login({ email: 'mathilde@example.com', password: 'Password123!' }).subscribe();

    const request = http.expectOne('http://localhost:8080/api/auth/login');
    expect(request.request.method).toBe('POST');
    request.flush(response);

    expect(service.user()).toEqual(response.user);
    expect(service.isAuthenticated()).toBe(true);
  });

  it('clears the session on logout', () => {
    service.register({ username: 'mathilde', email: 'mathilde@example.com', password: 'Password123!' }).subscribe();
    http.expectOne('http://localhost:8080/api/auth/register').flush(response);

    service.logout();

    expect(service.user()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
  });
});
