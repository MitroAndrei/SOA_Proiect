import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthStore } from '@org/shared-auth';
import { OrdersApiService, OrderRequest } from '@org/shared-api';

@Component({
  selector: 'app-new-order-page',
  imports: [CommonModule, FormsModule],
  templateUrl: './new-order-page.html',
  styleUrl: './new-order-page.scss',
})
export class NewOrderPage {
  productId = signal<string>('');
  quantity = signal<number>(1);
  price = signal<number>(1);

  loading = signal(false);
  success = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthStore, private ordersApi: OrdersApiService) {}

  get username() {
    return this.auth.username;
  }

  submit() {
    this.success.set(false);
    this.error.set(null);

    const customerId = this.auth.username;
    if (!customerId) {
      this.error.set('Not logged in.');
      return;
    }

    const body: OrderRequest = {
      customerId,
      productId: this.productId(),
      quantity: Number(this.quantity()),
      price: Number(this.price()).toFixed(2),
    };

    this.loading.set(true);

    this.ordersApi.createOrder(body).subscribe({
      next: () => {
        this.success.set(true);
        this.productId.set('');
        this.quantity.set(1);
        this.price.set(1);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to create order.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }
}
