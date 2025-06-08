export interface PlaneDto {
  planeId: number;
  planeCode: string;
  planeType: string;
  seatQuantity: number;
  manufacturer?: string;
  yearManufactured?: number;
  status?: 'ACTIVE' | 'MAINTENANCE' | 'RETIRED';
}

export interface Plane {
  planeId?: number;
  planeCode: string;
  planeType: string;
  seatQuantity: number;
}

export interface PlaneUtilization extends Plane {
  totalFlights?: number;
  upcomingFlights?: number;
  utilizationRate?: number;
}
