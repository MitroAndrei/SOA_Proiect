import { Component } from '@angular/core';
import { NxWelcome } from './nx-welcome';

@Component({
  imports: [NxWelcome],
  selector: 'app-auth-entry',
  template: `<h1>AUTH</h1>`,
})
export class RemoteEntry {}
