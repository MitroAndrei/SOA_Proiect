import { Route } from '@angular/router';
import { RemoteEntry } from './entry';
import { AuthPage } from '../auth-page/auth-page';

export const remoteRoutes: Route[] = [{ path: '', component: AuthPage }];
