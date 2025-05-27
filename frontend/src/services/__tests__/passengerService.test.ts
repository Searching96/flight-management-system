import { describe, it, expect, beforeEach, vi } from 'vitest';
import { passengerService } from '../passengerService';
import { apiClient } from '../api';
import { CreatePassengerRequest, Passenger } from '../../models';

// Mock the API client
vi.mock('../api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

const mockApiClient = vi.mocked(apiClient);

describe('PassengerService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const mockPassenger: Passenger = {
    passengerId: 1,
    passengerName: 'John Doe',
    email: 'john.doe@example.com',
    citizenId: 'ID123456789',
    phoneNumber: '+1 555-0123',
  };

  const mockCreateRequest: CreatePassengerRequest = {
    passengerName: 'John Doe',
    email: 'john.doe@example.com',
    citizenId: 'ID123456789',
    phoneNumber: '+1 555-0123',
  };

  describe('findExistingPassenger', () => {
    it('should return passenger when found', async () => {
      mockApiClient.get.mockResolvedValue(mockPassenger);

      const result = await passengerService.findExistingPassenger('ID123456789');

      expect(result).toEqual(mockPassenger);
      expect(mockApiClient.get).toHaveBeenCalledWith('/passengers/citizen-id/ID123456789');
    });

    it('should return null when passenger not found (404)', async () => {
      const error = new Error('Not found');
      (error as any).response = { status: 404 };
      mockApiClient.get.mockRejectedValue(error);

      const result = await passengerService.findExistingPassenger('ID999999999');

      expect(result).toBeNull();
      expect(mockApiClient.get).toHaveBeenCalledWith('/passengers/citizen-id/ID999999999');
    });

    it('should throw error for non-404 errors', async () => {
      const error = new Error('Server error');
      (error as any).response = { status: 500 };
      mockApiClient.get.mockRejectedValue(error);

      await expect(passengerService.findExistingPassenger('ID123456789'))
        .rejects.toThrow('Server error');
    });
  });

  describe('createOrUpdatePassenger', () => {
    it('should create new passenger when not exists', async () => {
      // Mock findExistingPassenger to return null (not found)
      const error = new Error('Not found');
      (error as any).response = { status: 404 };
      mockApiClient.get.mockRejectedValue(error);
      
      // Mock createPassenger
      mockApiClient.post.mockResolvedValue(mockPassenger);

      const result = await passengerService.createOrUpdatePassenger(mockCreateRequest);

      expect(result).toEqual(mockPassenger);
      expect(mockApiClient.get).toHaveBeenCalledWith('/passengers/citizen-id/ID123456789');
      expect(mockApiClient.post).toHaveBeenCalledWith('/passengers', mockCreateRequest);
    });

    it('should update existing passenger when found', async () => {
      // Mock findExistingPassenger to return existing passenger
      mockApiClient.get.mockResolvedValue(mockPassenger);
      
      // Mock updatePassenger
      const updatedPassenger = { ...mockPassenger, email: 'updated@example.com' };
      mockApiClient.put.mockResolvedValue(updatedPassenger);

      const updateRequest = { ...mockCreateRequest, email: 'updated@example.com' };
      const result = await passengerService.createOrUpdatePassenger(updateRequest);

      expect(result).toEqual(updatedPassenger);
      expect(mockApiClient.get).toHaveBeenCalledWith('/passengers/citizen-id/ID123456789');
      expect(mockApiClient.put).toHaveBeenCalledWith('/passengers/1', {
        passengerName: updateRequest.passengerName,
        email: updateRequest.email,
        phoneNumber: updateRequest.phoneNumber,
      });
    });
  });

  describe('validatePassengerData', () => {
    it('should return empty array for valid data', () => {
      const validData: CreatePassengerRequest = {
        passengerName: 'John Doe',
        email: 'john.doe@example.com',
        citizenId: 'ID123456789',
        phoneNumber: '+1 555-0123',
      };

      const errors = passengerService.validatePassengerData(validData);

      expect(errors).toEqual([]);
    });

    it('should return errors for missing required fields', () => {
      const invalidData: CreatePassengerRequest = {
        passengerName: '',
        email: '',
        citizenId: '',
        phoneNumber: '',
      };

      const errors = passengerService.validatePassengerData(invalidData);

      expect(errors).toContain('Passenger name is required');
      expect(errors).toContain('Email is required');
      expect(errors).toContain('Citizen ID is required');
      expect(errors).toContain('Phone number is required');
      expect(errors).toHaveLength(4);
    });

    it('should return error for invalid email format', () => {
      const invalidData: CreatePassengerRequest = {
        passengerName: 'John Doe',
        email: 'invalid-email',
        citizenId: 'ID123456789',
        phoneNumber: '+1 555-0123',
      };

      const errors = passengerService.validatePassengerData(invalidData);

      expect(errors).toContain('Invalid email format');
      expect(errors).toHaveLength(1);
    });

    it('should handle whitespace-only values', () => {
      const invalidData: CreatePassengerRequest = {
        passengerName: '   ',
        email: '   ',
        citizenId: '   ',
        phoneNumber: '   ',
      };

      const errors = passengerService.validatePassengerData(invalidData);

      expect(errors).toContain('Passenger name is required');
      expect(errors).toContain('Email is required');
      expect(errors).toContain('Citizen ID is required');
      expect(errors).toContain('Phone number is required');
    });
  });

  describe('transformPassengerData', () => {
    it('should transform frontend data to backend format', () => {
      const frontendData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        citizenId: 'ID123456789',
        phoneNumber: '+1 555-0123',
      };

      const result = passengerService.transformPassengerData(frontendData);

      expect(result).toEqual({
        passengerName: 'John Doe',
        email: 'john.doe@example.com',
        citizenId: 'ID123456789',
        phoneNumber: '+1 555-0123',
      });
    });

    it('should trim whitespace from all fields', () => {
      const frontendData = {
        firstName: '  John  ',
        lastName: '  Doe  ',
        email: '  john.doe@example.com  ',
        citizenId: '  ID123456789  ',
        phoneNumber: '  +1 555-0123  ',
      };

      const result = passengerService.transformPassengerData(frontendData);

      expect(result).toEqual({
        passengerName: 'John Doe',
        email: 'john.doe@example.com',
        citizenId: 'ID123456789',
        phoneNumber: '+1 555-0123',
      });
    });

    it('should handle empty names correctly', () => {
      const frontendData = {
        firstName: '',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        citizenId: 'ID123456789',
        phoneNumber: '+1 555-0123',
      };

      const result = passengerService.transformPassengerData(frontendData);

      expect(result.passengerName).toBe(' Doe');
    });
  });

  describe('API method calls', () => {
    it('should call correct endpoints for getAllPassengers', async () => {
      const mockPassengers = [mockPassenger];
      mockApiClient.get.mockResolvedValue(mockPassengers);

      const result = await passengerService.getAllPassengers();

      expect(result).toEqual(mockPassengers);
      expect(mockApiClient.get).toHaveBeenCalledWith('/passengers');
    });

    it('should call correct endpoints for getPassengerById', async () => {
      mockApiClient.get.mockResolvedValue(mockPassenger);

      const result = await passengerService.getPassengerById(1);

      expect(result).toEqual(mockPassenger);
      expect(mockApiClient.get).toHaveBeenCalledWith('/passengers/1');
    });

    it('should call correct endpoints for createPassenger', async () => {
      mockApiClient.post.mockResolvedValue(mockPassenger);

      const result = await passengerService.createPassenger(mockCreateRequest);

      expect(result).toEqual(mockPassenger);
      expect(mockApiClient.post).toHaveBeenCalledWith('/passengers', mockCreateRequest);
    });

    it('should call correct endpoints for deletePassenger', async () => {
      mockApiClient.delete.mockResolvedValue(undefined);

      await passengerService.deletePassenger(1);

      expect(mockApiClient.delete).toHaveBeenCalledWith('/passengers/1');
    });
  });
});
