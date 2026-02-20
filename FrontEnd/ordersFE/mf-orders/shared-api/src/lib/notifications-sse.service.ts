import { Inject, Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { API_BASE_URL } from './api.config';
import { AuthStore } from '@org/shared-auth';

export interface OrderCreatedEvent {
  orderId: string;
  userId: string;
  item: string;
  quantity: number;
  price: number;
  status: string;
  timestamp: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationsSseService {
  constructor(
    @Inject(API_BASE_URL) private baseUrl: string,
    private auth: AuthStore,
    private zone: NgZone
  ) {}

  connectToOrderEvents(): Observable<OrderCreatedEvent> {
    return new Observable((subscriber) => {
      const token = this.auth.token;
      const username = this.auth.username;

      if (!token || !username) {
        subscriber.error(new Error('Missing token/username for SSE.'));
        return;
      }

      const url = `${this.baseUrl}/api/notifications/stream/${encodeURIComponent(username)}`;
      const controller = new AbortController();

      let attempt = 0;
      let stopped = false;

      const connect = () => {
        if (stopped) return;

        fetchEventSource(url, {
          method: 'GET',
          signal: controller.signal,
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: 'text/event-stream',
          },

          onopen: async (res) => {
            if (res.ok) {
              attempt = 0; // reset backoff on successful connect
              return;
            }

            if (res.status === 401 || res.status === 403) {
              const text = await res.text().catch(() => '');
              throw new Error(`SSE unauthorized (${res.status}). ${text}`);
            }

            const text = await res.text().catch(() => '');
            throw new Error(`SSE open failed: ${res.status} ${res.statusText} ${text}`);
          },

          onmessage: (msg) => {
            this.zone.run(() => {
              try {
                subscriber.next(JSON.parse(msg.data));
              } catch {
                // ignore malformed messages or pings
              }
            });
          },

          onerror: (err) => {
            // reconnect with backoff (unless aborted)
            if (controller.signal.aborted) return;

            attempt += 1;
            const delayMs = Math.min(30_000, 500 * Math.pow(2, attempt)); // 0.5s -> 30s cap

            // optional: surface a “disconnected” state
            this.zone.run(() => {
              // don’t complete; keep trying
              // subscriber.error(err); // <- don’t error, or it stops the stream for subscribers
            });

            setTimeout(connect, delayMs);
          },
        }).catch((err) => {
          // fetchEventSource can throw from onopen; decide what to do
          if (controller.signal.aborted) return;

          // If auth failed, surface it and stop retrying
          if (String(err?.message ?? '').includes('unauthorized')) {
            this.zone.run(() => subscriber.error(err));
            controller.abort();
            return;
          }

          attempt += 1;
          const delayMs = Math.min(30_000, 500 * Math.pow(2, attempt));
          setTimeout(connect, delayMs);
        });
      };

      connect();

      return () => {
        stopped = true;
        controller.abort();
      };
    });
  }
}
