import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

type JwtPayload = {
  sub?: string;     // common
  username?: string; // if you use this instead
};

@Injectable({ providedIn: 'root' })
export class AuthStore {
  private readonly TOKEN_KEY = 'access_token';

  private tokenSubject = new BehaviorSubject<string | null>(this.readToken());
  readonly token$ = this.tokenSubject.asObservable();

  // derive username from token
  readonly username$ = this.token$.pipe(
    map((t) => (t ? this.extractUsername(t) : null))
  );
  get token(): string | null {
    return this.tokenSubject.value;
  }

  get username(): string | null {
    const t = this.tokenSubject.value;
    return t ? this.extractUsername(t) : null;
  }

  setToken(token: string | null) {
    if (token) {
      localStorage.setItem(this.TOKEN_KEY, token);
    } else {
      localStorage.removeItem(this.TOKEN_KEY);
    }
    this.tokenSubject.next(token);
  }

  logout() {
    this.setToken(null);
  }

  private readToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private extractUsername(token: string): string | null {
    try {
      const payload = jwtDecode<JwtPayload>(token);
      return payload.sub ?? payload.username ?? null;
    } catch {
      return null;
    }
  }
}
