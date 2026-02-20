import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { AuthStore } from '@org/shared-auth';

export interface Order {
  orderId: string;
  customerId: string;
  productId: string;
  quantity: number;
  price: string;       // BigDecimal often serialized as string
  totalAmount: string; // BigDecimal
  createdAt: string;
  processedAt: string;
  status: string;      // enum as string
}

export interface OrderRequest {
  customerId: string;
  productId: string;
  quantity: number;
  price: string; // send as string to avoid float issues
}

@Injectable({ providedIn: 'root' })
export class OrdersApiService {
  constructor(
    private http: HttpClient,
    @Inject(API_BASE_URL) private baseUrl: string,
    private auth: AuthStore
  ) {}

  listMyOrders(): Observable<Order[]> {
    const username = this.auth.username;
    if (!username) {
      return new Observable<Order[]>((sub) => {
        sub.next([]);
        sub.complete();
      });
    }

    return this.http.get<Order[]>(
      `${this.baseUrl}/api/processing/customer/${encodeURIComponent(username)}`
    );
  }

  createOrder(body: OrderRequest): Observable<Order> {
    return this.http.post<Order>(`${this.baseUrl}/api/orders`, body);
  }
}
