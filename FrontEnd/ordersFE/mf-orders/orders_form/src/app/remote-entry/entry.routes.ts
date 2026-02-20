import { Route } from '@angular/router';
import { RemoteEntry } from './entry';
import { NewOrderPage } from '../new-order-page/new-order-page';

export const remoteRoutes: Route[] = [{ path: '', component: NewOrderPage }];
