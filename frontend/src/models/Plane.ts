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

// Keep legacy interface for backward compatibility
export interface PlaneModel {
  planeId?: number;
  planeCode: string;
  planeModel: string;
  manufacturer?: string;
  capacity?: number;
}

export interface PlaneUtilization extends Plane {
  totalFlights?: number;
  upcomingFlights?: number;
  utilizationRate?: number;
}
