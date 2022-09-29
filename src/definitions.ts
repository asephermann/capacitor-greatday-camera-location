export interface GreatDayCameraLocationPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
