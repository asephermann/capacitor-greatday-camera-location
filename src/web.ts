import { WebPlugin } from '@capacitor/core';

import type { GreatDayCameraLocationPlugin } from './definitions';

export class GreatDayCameraLocationWeb extends WebPlugin implements GreatDayCameraLocationPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
