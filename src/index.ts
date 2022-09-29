import { registerPlugin } from '@capacitor/core';

import type { GreatDayCameraLocationPlugin } from './definitions';

const GreatDayCameraLocation = registerPlugin<GreatDayCameraLocationPlugin>('GreatDayCameraLocation', {
  web: () => import('./web').then(m => new m.GreatDayCameraLocationWeb()),
});

export * from './definitions';
export { GreatDayCameraLocation };
