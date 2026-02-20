import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NxWelcome } from './nx-welcome';
import { CommonModule } from '@angular/common';
import { AuthStore } from '@org/shared-auth';

@Component({
  imports: [CommonModule, RouterModule],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  constructor(
    public auth: AuthStore,
    public router: Router,
  ) {}

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/auth');
  }
}
