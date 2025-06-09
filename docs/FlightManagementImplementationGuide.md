# Flight Management Feature Implementation Guide

## Table of Contents
1. [Overview](#overview)
2. [Architecture Blueprint](#architecture-blueprint)
3. [Component Structure](#component-structure)
4. [Data Models](#data-models)
5. [Service Layer](#service-layer)
6. [State Management](#state-management)
7. [Validation Logic](#validation-logic)
8. [UI/UX Patterns](#uiux-patterns)
9. [Permission System](#permission-system)
10. [Testing Strategy](#testing-strategy)
11. [Implementation Checklist](#implementation-checklist)
12. [Code Templates](#code-templates)

## Overview

The Flight Management feature is a comprehensive CRUD system that demonstrates best practices for:
- Multi-level component architecture (Main → Sub-components → Forms)
- Complex validation with react-hook-form + Zod
- Permission-based access control
- Modal-based workflows
- Data table management with search/filter
- Related entity management (Flight Ticket Classes, Flight Details)

### Key Features Implemented:
- Flight CRUD operations
- Flight Ticket Class management (junction entity)
- Flight Details management
- Search and filtering
- Bulk operations
- Data validation
- Permission checks
- Error handling
- Loading states

## Architecture Blueprint

### 1. Folder Structure Pattern
```
components/admin/
├── FlightManagement.tsx           # Main management component
├── FlightTicketClassManagement.tsx # Sub-component for junction entity
└── flights/
    ├── FlightForm.tsx             # Form component
    ├── FlightDetailsForm.tsx      # Sub-form component
    └── FlightFilters.tsx          # Filter component
```

### 2. File Dependencies
```
Main Component Dependencies:
├── Models (Flight.ts, FlightTicketClass.ts, etc.)
├── Services (FlightService.ts, flightTicketClassService.ts)
├── Hooks (useFlights.ts, useFlightDetails.ts)
├── Validation Schemas (Zod schemas)
└── UI Components (DataTable, Modal, Form components)
```

## Component Structure

### Main Management Component Pattern

```typescript
// FlightManagement.tsx - Main Component Structure
export default function FlightManagement() {
  // 1. Permission Checks
  const { user } = useAuth();
  const canManageFlights = user?.role === 'ADMIN' || user?.role === 'EMPLOYEE';

  // 2. State Management
  const [flights, setFlights] = useState<Flight[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedFlight, setSelectedFlight] = useState<Flight | null>(null);
  
  // 3. Modal States
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showTicketClassModal, setShowTicketClassModal] = useState(false);

  // 4. Search and Filter States
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({});

  // 5. Custom Hooks
  const { 
    flights: hookFlights, 
    loading: hookLoading, 
    error: hookError,
    refetch 
  } = useFlights();

  // 6. Data Fetching Effects
  useEffect(() => {
    fetchData();
  }, []);

  // 7. CRUD Operations
  const handleCreate = async (data: FlightFormData) => { /* ... */ };
  const handleUpdate = async (data: FlightFormData) => { /* ... */ };
  const handleDelete = async (id: string) => { /* ... */ };

  // 8. UI Event Handlers
  const openCreateModal = () => setShowCreateModal(true);
  const openEditModal = (flight: Flight) => { /* ... */ };
  const openDeleteModal = (flight: Flight) => { /* ... */ };

  // 9. Render Structure
  return (
    <div className="flight-management">
      {/* Header Section */}
      <Header />
      
      {/* Search and Filters */}
      <SearchAndFilters />
      
      {/* Data Table */}
      <DataTable />
      
      {/* Modals */}
      <CreateModal />
      <EditModal />
      <DeleteModal />
      <SubComponentModals />
    </div>
  );
}
```

### Sub-Component Pattern (Flight Ticket Class Management)

```typescript
// FlightTicketClassManagement.tsx - Sub-Component Pattern
interface FlightTicketClassManagementProps {
  flightId: string;
  isOpen: boolean;
  onClose: () => void;
  onUpdate?: () => void;
}

export default function FlightTicketClassManagement({
  flightId,
  isOpen,
  onClose,
  onUpdate
}: FlightTicketClassManagementProps) {
  // 1. Local State for Sub-Component
  const [ticketClasses, setTicketClasses] = useState<FlightTicketClass[]>([]);
  const [selectedClass, setSelectedClass] = useState<FlightTicketClass | null>(null);
  
  // 2. Sub-Component Specific Operations
  const handleAddTicketClass = async (data: TicketClassData) => { /* ... */ };
  const handleUpdateTicketClass = async (data: TicketClassData) => { /* ... */ };
  const handleRemoveTicketClass = async (id: string) => { /* ... */ };

  // 3. Data Synchronization with Parent
  useEffect(() => {
    if (isOpen && flightId) {
      fetchTicketClasses();
    }
  }, [isOpen, flightId]);

  // 4. Render Sub-Component UI
  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      {/* Sub-component content */}
    </Modal>
  );
}
```

## Data Models

### Core Entity Model Pattern

```typescript
// Flight.ts - Main Entity Model
export interface Flight {
  // Primary Key
  id: string;
  
  // Basic Information
  flightNumber: string;
  departureTime: Date;
  arrivalTime: Date;
  
  // Foreign Keys
  departureAirportId: string;
  arrivalAirportId: string;
  planeId: string;
  
  // Related Entities (populated)
  departureAirport?: Airport;
  arrivalAirport?: Airport;
  plane?: Plane;
  
  // Junction Entities
  flightTicketClasses?: FlightTicketClass[];
  flightDetails?: FlightDetail[];
  
  // Status and Metadata
  status: FlightStatus;
  createdAt: Date;
  updatedAt: Date;
}

// Enums
export enum FlightStatus {
  SCHEDULED = 'SCHEDULED',
  BOARDING = 'BOARDING',
  DEPARTED = 'DEPARTED',
  ARRIVED = 'ARRIVED',
  CANCELLED = 'CANCELLED',
  DELAYED = 'DELAYED'
}
```

### Junction Entity Model Pattern

```typescript
// FlightTicketClass.ts - Junction Entity Model
export interface FlightTicketClass {
  // Composite Key
  flightId: string;
  ticketClassId: string;
  
  // Junction-Specific Data
  price: number;
  availableSeats: number;
  totalSeats: number;
  
  // Related Entities
  flight?: Flight;
  ticketClass?: TicketClass;
  
  // Metadata
  createdAt: Date;
  updatedAt: Date;
}
```

## Service Layer

### Service Pattern Template

```typescript
// FlightService.ts - Service Layer Pattern
class FlightService {
  private readonly baseURL = '/api/flights';

  // CRUD Operations
  async getAll(filters?: FlightFilters): Promise<Flight[]> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value.toString());
      });
    }
    
    const response = await api.get(`${this.baseURL}?${params}`);
    return response.data;
  }

  async getById(id: string): Promise<Flight> {
    const response = await api.get(`${this.baseURL}/${id}`);
    return response.data;
  }

  async create(data: CreateFlightData): Promise<Flight> {
    const response = await api.post(this.baseURL, data);
    return response.data;
  }

  async update(id: string, data: UpdateFlightData): Promise<Flight> {
    const response = await api.put(`${this.baseURL}/${id}`, data);
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await api.delete(`${this.baseURL}/${id}`);
  }

  // Search Operations
  async search(query: string): Promise<Flight[]> {
    const response = await api.get(`${this.baseURL}/search`, {
      params: { q: query }
    });
    return response.data;
  }

  // Bulk Operations
  async bulkDelete(ids: string[]): Promise<void> {
    await api.delete(`${this.baseURL}/bulk`, {
      data: { ids }
    });
  }
}

export const flightService = new FlightService();
```

### Junction Entity Service Pattern

```typescript
// flightTicketClassService.ts - Junction Service Pattern
class FlightTicketClassService {
  private readonly baseURL = '/api/flight-ticket-classes';

  async getByFlightId(flightId: string): Promise<FlightTicketClass[]> {
    const response = await api.get(`${this.baseURL}/flight/${flightId}`);
    return response.data;
  }

  async create(data: CreateFlightTicketClassData): Promise<FlightTicketClass> {
    const response = await api.post(this.baseURL, data);
    return response.data;
  }

  async update(
    flightId: string,
    ticketClassId: string,
    data: UpdateFlightTicketClassData
  ): Promise<FlightTicketClass> {
    const response = await api.put(
      `${this.baseURL}/${flightId}/${ticketClassId}`,
      data
    );
    return response.data;
  }

  async delete(flightId: string, ticketClassId: string): Promise<void> {
    await api.delete(`${this.baseURL}/${flightId}/${ticketClassId}`);
  }
}

export const flightTicketClassService = new FlightTicketClassService();
```

## State Management

### Custom Hooks Pattern

```typescript
// useFlights.ts - Custom Hook Pattern
export function useFlights(filters?: FlightFilters) {
  const [flights, setFlights] = useState<Flight[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchFlights = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await flightService.getAll(filters);
      setFlights(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchFlights();
  }, [fetchFlights]);

  const createFlight = useCallback(async (data: CreateFlightData) => {
    try {
      const newFlight = await flightService.create(data);
      setFlights(prev => [...prev, newFlight]);
      return newFlight;
    } catch (err) {
      throw err;
    }
  }, []);

  const updateFlight = useCallback(async (id: string, data: UpdateFlightData) => {
    try {
      const updatedFlight = await flightService.update(id, data);
      setFlights(prev => prev.map(f => f.id === id ? updatedFlight : f));
      return updatedFlight;
    } catch (err) {
      throw err;
    }
  }, []);

  const deleteFlight = useCallback(async (id: string) => {
    try {
      await flightService.delete(id);
      setFlights(prev => prev.filter(f => f.id !== id));
    } catch (err) {
      throw err;
    }
  }, []);

  const refetch = useCallback(() => {
    fetchFlights();
  }, [fetchFlights]);

  return {
    flights,
    loading,
    error,
    createFlight,
    updateFlight,
    deleteFlight,
    refetch
  };
}
```

## Validation Logic

### Zod Schema Pattern

```typescript
// flightValidation.ts - Validation Schema Pattern
import { z } from 'zod';

// Base Flight Schema
export const flightSchema = z.object({
  flightNumber: z
    .string()
    .min(1, 'Flight number is required')
    .regex(/^[A-Z]{2}\d{3,4}$/, 'Flight number must be in format: AB123 or AB1234'),
  
  departureTime: z
    .date()
    .refine(date => date > new Date(), 'Departure time must be in the future'),
  
  arrivalTime: z
    .date(),
  
  departureAirportId: z
    .string()
    .min(1, 'Departure airport is required'),
  
  arrivalAirportId: z
    .string()
    .min(1, 'Arrival airport is required'),
  
  planeId: z
    .string()
    .min(1, 'Plane is required'),
  
  status: z.nativeEnum(FlightStatus)
}).refine(
  data => data.arrivalTime > data.departureTime,
  {
    message: 'Arrival time must be after departure time',
    path: ['arrivalTime']
  }
).refine(
  data => data.departureAirportId !== data.arrivalAirportId,
  {
    message: 'Departure and arrival airports must be different',
    path: ['arrivalAirportId']
  }
);

// Form-specific schemas
export const createFlightSchema = flightSchema;
export const updateFlightSchema = flightSchema.partial();

// Junction entity schema
export const flightTicketClassSchema = z.object({
  flightId: z.string().min(1, 'Flight ID is required'),
  ticketClassId: z.string().min(1, 'Ticket class is required'),
  price: z.number().min(0, 'Price must be non-negative'),
  availableSeats: z.number().int().min(0, 'Available seats must be non-negative'),
  totalSeats: z.number().int().min(1, 'Total seats must be at least 1')
}).refine(
  data => data.availableSeats <= data.totalSeats,
  {
    message: 'Available seats cannot exceed total seats',
    path: ['availableSeats']
  }
);

// Type inference
export type FlightFormData = z.infer<typeof flightSchema>;
export type CreateFlightData = z.infer<typeof createFlightSchema>;
export type UpdateFlightData = z.infer<typeof updateFlightSchema>;
export type FlightTicketClassData = z.infer<typeof flightTicketClassSchema>;
```

### Form Implementation Pattern

```typescript
// FlightForm.tsx - Form Component Pattern
interface FlightFormProps {
  flight?: Flight;
  onSubmit: (data: FlightFormData) => Promise<void>;
  onCancel: () => void;
  isLoading?: boolean;
}

export default function FlightForm({
  flight,
  onSubmit,
  onCancel,
  isLoading
}: FlightFormProps) {
  // Form setup with react-hook-form + Zod
  const form = useForm<FlightFormData>({
    resolver: zodResolver(flight ? updateFlightSchema : createFlightSchema),
    defaultValues: flight ? {
      flightNumber: flight.flightNumber,
      departureTime: new Date(flight.departureTime),
      arrivalTime: new Date(flight.arrivalTime),
      departureAirportId: flight.departureAirportId,
      arrivalAirportId: flight.arrivalAirportId,
      planeId: flight.planeId,
      status: flight.status
    } : {
      flightNumber: '',
      departureTime: new Date(),
      arrivalTime: new Date(),
      departureAirportId: '',
      arrivalAirportId: '',
      planeId: '',
      status: FlightStatus.SCHEDULED
    }
  });

  // Form submission handler
  const handleSubmit = async (data: FlightFormData) => {
    try {
      await onSubmit(data);
      form.reset();
    } catch (error) {
      // Handle form-level errors
      form.setError('root', {
        message: error instanceof Error ? error.message : 'An error occurred'
      });
    }
  };

  return (
    <form onSubmit={form.handleSubmit(handleSubmit)}>
      {/* Form fields with validation */}
      <FormField
        control={form.control}
        name="flightNumber"
        render={({ field }) => (
          <FormItem>
            <FormLabel>Flight Number</FormLabel>
            <FormControl>
              <Input {...field} placeholder="e.g., AA123" />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />

      {/* Date/Time fields */}
      <FormField
        control={form.control}
        name="departureTime"
        render={({ field }) => (
          <FormItem>
            <FormLabel>Departure Time</FormLabel>
            <FormControl>
              <DateTimePicker
                value={field.value}
                onChange={field.onChange}
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />

      {/* Select fields */}
      <FormField
        control={form.control}
        name="departureAirportId"
        render={({ field }) => (
          <FormItem>
            <FormLabel>Departure Airport</FormLabel>
            <Select onValueChange={field.onChange} value={field.value}>
              <FormControl>
                <SelectTrigger>
                  <SelectValue placeholder="Select departure airport" />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                {airports.map(airport => (
                  <SelectItem key={airport.id} value={airport.id}>
                    {airport.name} ({airport.code})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        )}
      />

      {/* Form actions */}
      <div className="flex justify-end space-x-2">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Saving...' : flight ? 'Update' : 'Create'}
        </Button>
      </div>

      {/* Root error display */}
      {form.formState.errors.root && (
        <Alert variant="destructive">
          <AlertDescription>
            {form.formState.errors.root.message}
          </AlertDescription>
        </Alert>
      )}
    </form>
  );
}
```

## UI/UX Patterns

### Data Table Pattern

```typescript
// DataTable component usage pattern
const columns: ColumnDef<Flight>[] = [
  {
    accessorKey: 'flightNumber',
    header: 'Flight Number',
    cell: ({ row }) => (
      <div className="font-medium">{row.getValue('flightNumber')}</div>
    )
  },
  {
    accessorKey: 'departureTime',
    header: 'Departure',
    cell: ({ row }) => {
      const date = new Date(row.getValue('departureTime'));
      return (
        <div>
          <div>{format(date, 'MMM dd, yyyy')}</div>
          <div className="text-sm text-muted-foreground">
            {format(date, 'HH:mm')}
          </div>
        </div>
      );
    }
  },
  {
    accessorKey: 'route',
    header: 'Route',
    cell: ({ row }) => {
      const flight = row.original;
      return (
        <div className="flex items-center space-x-2">
          <span>{flight.departureAirport?.code}</span>
          <ArrowRight className="h-4 w-4" />
          <span>{flight.arrivalAirport?.code}</span>
        </div>
      );
    }
  },
  {
    accessorKey: 'status',
    header: 'Status',
    cell: ({ row }) => {
      const status = row.getValue('status') as FlightStatus;
      return (
        <Badge variant={getStatusVariant(status)}>
          {status}
        </Badge>
      );
    }
  },
  {
    id: 'actions',
    cell: ({ row }) => {
      const flight = row.original;
      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => openEditModal(flight)}>
              Edit
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => openTicketClassModal(flight)}>
              Manage Ticket Classes
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => openDeleteModal(flight)}>
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    }
  }
];
```

### Modal Management Pattern

```typescript
// Modal state management pattern
const [modals, setModals] = useState({
  create: false,
  edit: false,
  delete: false,
  ticketClass: false,
  details: false
});

const openModal = (modalName: keyof typeof modals, data?: any) => {
  setModals(prev => ({ ...prev, [modalName]: true }));
  if (data) setSelectedFlight(data);
};

const closeModal = (modalName: keyof typeof modals) => {
  setModals(prev => ({ ...prev, [modalName]: false }));
  setSelectedFlight(null);
};

const closeAllModals = () => {
  setModals({
    create: false,
    edit: false,
    delete: false,
    ticketClass: false,
    details: false
  });
  setSelectedFlight(null);
};
```

### Search and Filter Pattern

```typescript
// Search and filter implementation
const [searchTerm, setSearchTerm] = useState('');
const [filters, setFilters] = useState<FlightFilters>({
  status: '',
  departureAirportId: '',
  arrivalAirportId: '',
  dateRange: {
    from: undefined,
    to: undefined
  }
});

// Debounced search
const debouncedSearch = useMemo(
  () => debounce((term: string) => {
    // Perform search
    refetch();
  }, 300),
  [refetch]
);

useEffect(() => {
  debouncedSearch(searchTerm);
}, [searchTerm, debouncedSearch]);

// Filter application
const filteredFlights = useMemo(() => {
  return flights.filter(flight => {
    // Apply search term
    if (searchTerm && !flight.flightNumber.toLowerCase().includes(searchTerm.toLowerCase())) {
      return false;
    }
    
    // Apply status filter
    if (filters.status && flight.status !== filters.status) {
      return false;
    }
    
    // Apply airport filters
    if (filters.departureAirportId && flight.departureAirportId !== filters.departureAirportId) {
      return false;
    }
    
    // Apply date range filter
    if (filters.dateRange.from && new Date(flight.departureTime) < filters.dateRange.from) {
      return false;
    }
    
    return true;
  });
}, [flights, searchTerm, filters]);
```

## Permission System

### Permission Check Pattern

```typescript
// Permission hooks and checks
export function usePermissions() {
  const { user } = useAuth();
  
  const hasPermission = useCallback((permission: string) => {
    if (!user) return false;
    
    const rolePermissions: Record<string, string[]> = {
      ADMIN: ['ALL'],
      EMPLOYEE: ['FLIGHT_READ', 'FLIGHT_WRITE', 'FLIGHT_DELETE'],
      CUSTOMER: ['FLIGHT_READ']
    };
    
    const userPermissions = rolePermissions[user.role] || [];
    return userPermissions.includes('ALL') || userPermissions.includes(permission);
  }, [user]);

  return {
    canRead: hasPermission('FLIGHT_READ'),
    canWrite: hasPermission('FLIGHT_WRITE'),
    canDelete: hasPermission('FLIGHT_DELETE'),
    canManageTicketClasses: hasPermission('FLIGHT_WRITE'),
    hasPermission
  };
}

// Usage in components
export default function FlightManagement() {
  const { canRead, canWrite, canDelete } = usePermissions();

  if (!canRead) {
    return <div>Access denied</div>;
  }

  return (
    <div>
      {/* Conditional rendering based on permissions */}
      {canWrite && (
        <Button onClick={openCreateModal}>
          Create Flight
        </Button>
      )}
      
      {/* Table with conditional actions */}
      <DataTable
        columns={columns.filter(col => {
          if (col.id === 'actions' && !canWrite && !canDelete) {
            return false;
          }
          return true;
        })}
        data={flights}
      />
    </div>
  );
}
```

## Testing Strategy

### Component Testing Pattern

```typescript
// FlightManagement.test.tsx - Component test pattern
describe('FlightManagement', () => {
  const mockFlights: Flight[] = [
    {
      id: '1',
      flightNumber: 'AA123',
      departureTime: new Date('2024-01-01T10:00:00Z'),
      arrivalTime: new Date('2024-01-01T12:00:00Z'),
      departureAirportId: 'airport1',
      arrivalAirportId: 'airport2',
      planeId: 'plane1',
      status: FlightStatus.SCHEDULED,
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    (flightService.getAll as jest.Mock).mockResolvedValue(mockFlights);
  });

  it('should render flight list', async () => {
    render(<FlightManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('AA123')).toBeInTheDocument();
    });
  });

  it('should open create modal when create button is clicked', async () => {
    render(<FlightManagement />);
    
    const createButton = screen.getByText('Create Flight');
    fireEvent.click(createButton);
    
    expect(screen.getByText('Create New Flight')).toBeInTheDocument();
  });

  it('should handle flight creation', async () => {
    const newFlight = { ...mockFlights[0], id: '2', flightNumber: 'BB456' };
    (flightService.create as jest.Mock).mockResolvedValue(newFlight);
    
    render(<FlightManagement />);
    
    // Open create modal
    fireEvent.click(screen.getByText('Create Flight'));
    
    // Fill form
    fireEvent.change(screen.getByLabelText('Flight Number'), {
      target: { value: 'BB456' }
    });
    
    // Submit form
    fireEvent.click(screen.getByText('Create'));
    
    await waitFor(() => {
      expect(flightService.create).toHaveBeenCalledWith(
        expect.objectContaining({ flightNumber: 'BB456' })
      );
    });
  });
});
```

### Service Testing Pattern

```typescript
// FlightService.test.ts - Service test pattern
describe('FlightService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAll', () => {
    it('should fetch all flights', async () => {
      const mockFlights = [mockFlight];
      (api.get as jest.Mock).mockResolvedValue({ data: mockFlights });

      const result = await flightService.getAll();

      expect(api.get).toHaveBeenCalledWith('/api/flights?');
      expect(result).toEqual(mockFlights);
    });

    it('should apply filters', async () => {
      const filters = { status: FlightStatus.SCHEDULED };
      (api.get as jest.Mock).mockResolvedValue({ data: [] });

      await flightService.getAll(filters);

      expect(api.get).toHaveBeenCalledWith('/api/flights?status=SCHEDULED');
    });
  });

  describe('create', () => {
    it('should create a new flight', async () => {
      const flightData = { flightNumber: 'AA123' };
      const createdFlight = { ...flightData, id: '1' };
      (api.post as jest.Mock).mockResolvedValue({ data: createdFlight });

      const result = await flightService.create(flightData);

      expect(api.post).toHaveBeenCalledWith('/api/flights', flightData);
      expect(result).toEqual(createdFlight);
    });
  });
});
```

### Hook Testing Pattern

```typescript
// useFlights.test.ts - Hook test pattern
describe('useFlights', () => {
  it('should fetch flights on mount', async () => {
    const mockFlights = [mockFlight];
    (flightService.getAll as jest.Mock).mockResolvedValue(mockFlights);

    const { result } = renderHook(() => useFlights());

    expect(result.current.loading).toBe(true);

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
      expect(result.current.flights).toEqual(mockFlights);
    });
  });

  it('should handle errors', async () => {
    const error = new Error('Network error');
    (flightService.getAll as jest.Mock).mockRejectedValue(error);

    const { result } = renderHook(() => useFlights());

    await waitFor(() => {
      expect(result.current.error).toBe('Network error');
    });
  });
});
```

## Implementation Checklist

### Phase 1: Setup and Foundation
- [ ] Create folder structure following the pattern
- [ ] Define data models with TypeScript interfaces
- [ ] Set up Zod validation schemas
- [ ] Create service layer with API integration
- [ ] Implement custom hooks for state management

### Phase 2: Core Components
- [ ] Build main management component skeleton
- [ ] Implement data table with columns definition
- [ ] Add search and filter functionality
- [ ] Create form components with validation
- [ ] Implement modal management system

### Phase 3: CRUD Operations
- [ ] Implement create functionality
- [ ] Implement read/list functionality
- [ ] Implement update functionality
- [ ] Implement delete functionality
- [ ] Add bulk operations support

### Phase 4: Sub-Components
- [ ] Create junction entity management component
- [ ] Implement related entity forms
- [ ] Add sub-component modal integration
- [ ] Implement data synchronization

### Phase 5: Advanced Features
- [ ] Add permission-based access control
- [ ] Implement error handling and loading states
- [ ] Add data export/import functionality
- [ ] Implement advanced search and filtering

### Phase 6: Testing and Documentation
- [ ] Write unit tests for components
- [ ] Write tests for services and hooks
- [ ] Add integration tests
- [ ] Document API endpoints
- [ ] Create user documentation

## Code Templates

### Main Management Component Template

```typescript
// [Entity]Management.tsx Template
import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { use[Entity] } from '@/hooks/use[Entity]';
import { [Entity], [Entity]Status } from '@/models/[Entity]';
import { [entity]Service } from '@/services/[entity]Service';
import { DataTable } from '@/components/ui/data-table';
import { Button } from '@/components/ui/button';
import { Modal } from '@/components/ui/modal';
import { Alert } from '@/components/ui/alert';
import { [Entity]Form } from './[entity]/[Entity]Form';
import { [Entity]Filters } from './[entity]/[Entity]Filters';
import { columns } from './[entity]/[entity]Columns';

export default function [Entity]Management() {
  // Authentication and permissions
  const { user } = useAuth();
  const canManage[Entity] = user?.role === 'ADMIN' || user?.role === 'EMPLOYEE';

  // Main state
  const {
    [entity]s,
    loading,
    error,
    create[Entity],
    update[Entity],
    delete[Entity],
    refetch
  } = use[Entity]();

  // UI state
  const [selected[Entity], setSelected[Entity]] = useState<[Entity] | null>(null);
  const [modals, setModals] = useState({
    create: false,
    edit: false,
    delete: false
  });

  // Search and filter state
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState<[Entity]Filters>({});

  // Modal management
  const openModal = (modalName: keyof typeof modals, [entity]?: [Entity]) => {
    setModals(prev => ({ ...prev, [modalName]: true }));
    if ([entity]) setSelected[Entity]([entity]);
  };

  const closeModal = (modalName: keyof typeof modals) => {
    setModals(prev => ({ ...prev, [modalName]: false }));
    setSelected[Entity](null);
  };

  // CRUD handlers
  const handleCreate = async (data: Create[Entity]Data) => {
    try {
      await create[Entity](data);
      closeModal('create');
    } catch (error) {
      console.error('Error creating [entity]:', error);
    }
  };

  const handleUpdate = async (data: Update[Entity]Data) => {
    if (!selected[Entity]) return;
    try {
      await update[Entity](selected[Entity].id, data);
      closeModal('edit');
    } catch (error) {
      console.error('Error updating [entity]:', error);
    }
  };

  const handleDelete = async () => {
    if (!selected[Entity]) return;
    try {
      await delete[Entity](selected[Entity].id);
      closeModal('delete');
    } catch (error) {
      console.error('Error deleting [entity]:', error);
    }
  };

  // Permission check
  if (!canManage[Entity]) {
    return (
      <Alert variant="destructive">
        <AlertDescription>
          You don't have permission to manage [entity]s.
        </AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="[entity]-management">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">[Entity] Management</h1>
        <Button onClick={() => openModal('create')}>
          Create [Entity]
        </Button>
      </div>

      {/* Search and Filters */}
      <[Entity]Filters
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
        filters={filters}
        onFiltersChange={setFilters}
      />

      {/* Data Table */}
      <DataTable
        columns={columns({
          onEdit: ([entity]) => openModal('edit', [entity]),
          onDelete: ([entity]) => openModal('delete', [entity])
        })}
        data={[entity]s}
        loading={loading}
        error={error}
      />

      {/* Create Modal */}
      <Modal
        isOpen={modals.create}
        onClose={() => closeModal('create')}
        title="Create [Entity]"
      >
        <[Entity]Form
          onSubmit={handleCreate}
          onCancel={() => closeModal('create')}
        />
      </Modal>

      {/* Edit Modal */}
      <Modal
        isOpen={modals.edit}
        onClose={() => closeModal('edit')}
        title="Edit [Entity]"
      >
        {selected[Entity] && (
          <[Entity]Form
            [entity]={selected[Entity]}
            onSubmit={handleUpdate}
            onCancel={() => closeModal('edit')}
          />
        )}
      </Modal>

      {/* Delete Modal */}
      <Modal
        isOpen={modals.delete}
        onClose={() => closeModal('delete')}
        title="Delete [Entity]"
      >
        <div className="space-y-4">
          <p>Are you sure you want to delete this [entity]?</p>
          <p className="font-semibold">{selected[Entity]?.name}</p>
          <div className="flex justify-end space-x-2">
            <Button variant="outline" onClick={() => closeModal('delete')}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={handleDelete}>
              Delete
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
```

### Service Template

```typescript
// [entity]Service.ts Template
import { api } from './api';
import { [Entity], Create[Entity]Data, Update[Entity]Data, [Entity]Filters } from '@/models/[Entity]';

class [Entity]Service {
  private readonly baseURL = '/api/[entity]s';

  async getAll(filters?: [Entity]Filters): Promise<[Entity][]> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== '') {
          params.append(key, value.toString());
        }
      });
    }
    
    const response = await api.get(`${this.baseURL}?${params}`);
    return response.data;
  }

  async getById(id: string): Promise<[Entity]> {
    const response = await api.get(`${this.baseURL}/${id}`);
    return response.data;
  }

  async create(data: Create[Entity]Data): Promise<[Entity]> {
    const response = await api.post(this.baseURL, data);
    return response.data;
  }

  async update(id: string, data: Update[Entity]Data): Promise<[Entity]> {
    const response = await api.put(`${this.baseURL}/${id}`, data);
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await api.delete(`${this.baseURL}/${id}`);
  }

  async search(query: string): Promise<[Entity][]> {
    const response = await api.get(`${this.baseURL}/search`, {
      params: { q: query }
    });
    return response.data;
  }
}

export const [entity]Service = new [Entity]Service();
```

### Custom Hook Template

```typescript
// use[Entity].ts Template
import { useState, useEffect, useCallback } from 'react';
import { [Entity], Create[Entity]Data, Update[Entity]Data, [Entity]Filters } from '@/models/[Entity]';
import { [entity]Service } from '@/services/[entity]Service';

export function use[Entity](filters?: [Entity]Filters) {
  const [[entity]s, set[Entity]s] = useState<[Entity][]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetch[Entity]s = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await [entity]Service.getAll(filters);
      set[Entity]s(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetch[Entity]s();
  }, [fetch[Entity]s]);

  const create[Entity] = useCallback(async (data: Create[Entity]Data) => {
    try {
      const new[Entity] = await [entity]Service.create(data);
      set[Entity]s(prev => [...prev, new[Entity]]);
      return new[Entity];
    } catch (err) {
      throw err;
    }
  }, []);

  const update[Entity] = useCallback(async (id: string, data: Update[Entity]Data) => {
    try {
      const updated[Entity] = await [entity]Service.update(id, data);
      set[Entity]s(prev => prev.map(item => item.id === id ? updated[Entity] : item));
      return updated[Entity];
    } catch (err) {
      throw err;
    }
  }, []);

  const delete[Entity] = useCallback(async (id: string) => {
    try {
      await [entity]Service.delete(id);
      set[Entity]s(prev => prev.filter(item => item.id !== id));
    } catch (err) {
      throw err;
    }
  }, []);

  const refetch = useCallback(() => {
    fetch[Entity]s();
  }, [fetch[Entity]s]);

  return {
    [entity]s,
    loading,
    error,
    create[Entity],
    update[Entity],
    delete[Entity],
    refetch
  };
}
```

### Form Component Template

```typescript
// [Entity]Form.tsx Template
import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { [Entity], [Entity]FormData } from '@/models/[Entity]';
import { [entity]Schema } from '@/schemas/[entity]Schema';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { FormField, FormItem, FormLabel, FormControl, FormMessage } from '@/components/ui/form';
import { Alert, AlertDescription } from '@/components/ui/alert';

interface [Entity]FormProps {
  [entity]?: [Entity];
  onSubmit: (data: [Entity]FormData) => Promise<void>;
  onCancel: () => void;
  isLoading?: boolean;
}

export default function [Entity]Form({
  [entity],
  onSubmit,
  onCancel,
  isLoading
}: [Entity]FormProps) {
  const form = useForm<[Entity]FormData>({
    resolver: zodResolver([entity]Schema),
    defaultValues: [entity] ? {
      // Map [entity] properties to form fields
      name: [entity].name,
      // ... other fields
    } : {
      // Default values for create mode
      name: '',
      // ... other fields
    }
  });

  const handleSubmit = async (data: [Entity]FormData) => {
    try {
      await onSubmit(data);
      if (![entity]) {
        form.reset();
      }
    } catch (error) {
      form.setError('root', {
        message: error instanceof Error ? error.message : 'An error occurred'
      });
    }
  };

  return (
    <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
      {/* Form fields */}
      <FormField
        control={form.control}
        name="name"
        render={({ field }) => (
          <FormItem>
            <FormLabel>Name</FormLabel>
            <FormControl>
              <Input {...field} placeholder="Enter name" />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />

      {/* Add more form fields as needed */}

      {/* Form actions */}
      <div className="flex justify-end space-x-2">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Saving...' : [entity] ? 'Update' : 'Create'}
        </Button>
      </div>

      {/* Root error display */}
      {form.formState.errors.root && (
        <Alert variant="destructive">
          <AlertDescription>
            {form.formState.errors.root.message}
          </AlertDescription>
        </Alert>
      )}
    </form>
  );
}
```

---

## Quick Start Guide

To implement a new management feature using this guide:

1. **Replace placeholders**: Replace `[Entity]`, `[entity]`, etc. with your actual entity names
2. **Define your data model**: Create the TypeScript interface in `models/`
3. **Create validation schema**: Set up Zod schema in `schemas/`
4. **Implement service**: Create API service following the template
5. **Build custom hook**: Implement state management hook
6. **Create form component**: Build the form with validation
7. **Implement main component**: Create the management component
8. **Add routing**: Register the component in your router
9. **Write tests**: Add comprehensive test coverage
10. **Document**: Update API and user documentation

This guide provides a comprehensive blueprint for replicating the Flight Management feature structure across any similar entity management system. The patterns are designed to be consistent, maintainable, and scalable.