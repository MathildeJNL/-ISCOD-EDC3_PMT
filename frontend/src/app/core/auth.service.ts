import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { tap } from 'rxjs';

import { AuthResponse, User } from './models';

const API_URL = 'http://localhost:8080/api';
const USER_KEY = 'pmt_user';
const TOKEN_KEY = 'pmt_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly userState = signal<User | null>(this.restoreUser());

  readonly user = this.userState.asReadonly();
  readonly isAuthenticated = computed(() => this.userState() !== null);

  register(payload: { username: string; email: string; password: string }) {
    return this.http.post<AuthResponse>(`${API_URL}/auth/register`, payload).pipe(
      tap((response) => this.store(response)),
    );
  }

  login(payload: { email: string; password: string }) {
    return this.http.post<AuthResponse>(`${API_URL}/auth/login`, payload).pipe(
      tap((response) => this.store(response)),
    );
  }

  logout() {
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
    this.userState.set(null);
  }

  userId(): number {
    const user = this.userState();
    if (!user) {
      throw new Error('User must be authenticated');
    }
    return user.id;
  }

  private store(response: AuthResponse) {
    localStorage.setItem(USER_KEY, JSON.stringify(response.user));
    localStorage.setItem(TOKEN_KEY, response.accessToken);
    this.userState.set(response.user);
  }

  private restoreUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as User) : null;
  }
}
