import { WebPlugin } from '@capacitor/core';

import type { GreatDayCameraLocationPlugin, LocationCameraOptions, LocationRadiusCameraOptions } from './definitions';

export class GreatDayCameraLocationWeb extends WebPlugin implements GreatDayCameraLocationPlugin {
  async getLocationCamera(_options?: LocationCameraOptions | undefined): Promise<any> {
    throw new Error('Method not implemented.');
  }
  async getLocationCameraSwap(_options?: LocationCameraOptions | undefined): Promise<any> {
    throw new Error('Method not implemented.');
  }
  async getLocationRadiusCamera(_options?: LocationRadiusCameraOptions | undefined): Promise<any> {
    throw new Error('Method not implemented.');
  }
  async getLocationRadiusCameraSwap(_options?: LocationRadiusCameraOptions | undefined): Promise<any> {
    throw new Error('Method not implemented.');
  }
}
