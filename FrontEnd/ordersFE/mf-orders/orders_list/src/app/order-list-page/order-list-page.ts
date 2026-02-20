import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { OrdersApiService, Order, NotificationsSseService, OrderCreatedEvent } from '@org/shared-api';

@Component({
  selector: 'app-order-list-page',
  imports: [CommonModule],
  templateUrl: './order-list-page.html',
  styleUrl: './order-list-page.scss',
})
export class OrderListPage implements OnInit, OnDestroy {
  orders = signal<Order[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  private sub = new Subscription();

  constructor(
    private ordersApi: OrdersApiService,
    private sse: NotificationsSseService
  ) {}

  ngOnInit() {
    this.reload();

    const sseSub = this.sse.connectToOrderEvents().subscribe({
      next: (evt) => {
        const newOrder = this.mapEventToOrder(evt);

        this.orders.update((current) => {
          const idx = current.findIndex((o) => o.orderId === newOrder.orderId);

          // not found -> prepend
          if (idx === -1) return [newOrder, ...current];

          // found -> replace / update
          const copy = current.slice();
          copy[idx] = { ...copy[idx], ...newOrder };
          return copy;
        });
      },
      error: () => {
        this.error.set('Live updates disconnected.');
      },
    });

    this.sub.add(sseSub);
  }

  reload(showSpinner = true) {
    if (showSpinner) this.loading.set(true);
    this.error.set(null);

    this.ordersApi.listMyOrders().subscribe({
      next: (orders) => this.orders.set(orders),
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load orders.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  private mapEventToOrder(evt: OrderCreatedEvent): Order {
    const total = (evt.price * evt.quantity).toFixed(2);

    return {
      orderId: evt.orderId,
      customerId: evt.userId,
      productId: evt.item,
      quantity: evt.quantity,
      price: evt.price.toFixed(2),
      totalAmount: total,
      createdAt: evt.timestamp,
      processedAt: evt.timestamp,
      status: evt.status,
    };
  }
}
