import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthStore } from '@org/shared-auth'; // adjust import path if Nx gives a different alias

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private auth: AuthStore) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.auth.token;

    // Don’t attach to auth endpoints if you don’t want to
    const isAuthEndpoint =
      req.url.includes('/auth/login') || req.url.includes('/auth/register');

    if (!token || isAuthEndpoint) {
      return next.handle(req);
    }

    return next.handle(
      req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    );
  }
}
