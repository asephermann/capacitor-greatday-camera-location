export interface GreatDayCameraLocationPlugin {
  getLocationCamera(options?: LocationCameraOptions): Promise<any>;
  getLocationCameraSwap(options?: LocationCameraOptions): Promise<any>;
  getLocationRadiusCamera(options?: LocationRadiusCameraOptions): Promise<any>;
  getLocationRadiusCameraSwap(options?: LocationRadiusCameraOptions): Promise<any>;
}

export interface LocationCameraOptions {
  photoName: string;
  quality: string;
  maxSize: string;
}

export interface LocationRadiusCameraOptions {
  photoName: string;
  quality: string;
  maxSize: string;
  location: string;
  label1: string;
  label2: string;
  showAddress: boolean;
}