import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthStore } from '@org/shared-auth';
import { AuthApiService } from '@org/shared-api';

type Mode = 'login' | 'register';
@Component({
  selector: 'app-auth-page',
  imports: [CommonModule, FormsModule],
  templateUrl: './auth-page.html',
  styleUrl: './auth-page.scss',
})
export class AuthPage {
  mode: Mode = 'login';
  username = '';
  password = '';
  loading = false;
  error: string | null = null;

  constructor(
    private api: AuthApiService,
    private auth: AuthStore,
    private router: Router
  ) {}

  toggle(e: Event) {
    e.preventDefault();
    this.error = null;
    this.mode = this.mode === 'login' ? 'register' : 'login';
  }

  submit() {
    this.error = null;
    this.loading = true;

    const req$ =
      this.mode === 'login'
        ? this.api.login({ username: this.username, password: this.password })
        : this.api.register({ username: this.username, password: this.password });

    req$.subscribe({
      next: (jwt) => {
        this.auth.setToken(jwt.token);
        this.router.navigateByUrl('/dashboard');
      },
      error: (err) => {
        this.error =
          err?.error?.message ??
          'Authentication failed. Please check your credentials.';
        this.loading = false;
      },
      complete: () => (this.loading = false),
    });
  }

}
