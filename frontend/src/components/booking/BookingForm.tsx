import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useAuth } from '../../hooks/useAuth';
import { flightService, ticketService } from '../../services';
import { Flight } from '../../models';
import './BookingForm.css';

interface BookingFormData {
  passengers: {
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    gender: 'Male' | 'Female' | 'Other';
    citizenId: string;
    phoneNumber: string;
    email: string;
  }[];
  ticketClassId: number;
}

const BookingForm: React.FC = () => {
  const { flightId } = useParams<{ flightId: string }>();
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [flight, setFlight] = useState<Flight | null>(null);
  const [ticketClasses, setTicketClasses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const passengerCount = location.state?.passengerCount || 1;

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch
  } = useForm<BookingFormData>({
    defaultValues: {
      passengers: Array(passengerCount).fill(null).map(() => ({
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        gender: 'Male' as const,
        citizenId: '',
        phoneNumber: '',
        email: ''
      })),
      ticketClassId: 0
    }
  });

  useEffect(() => {
    if (flightId) {
      loadBookingData();
    }
  }, [flightId]);

  const loadBookingData = async () => {
    try {
      setLoading(true);
      const [flightData, ticketClassData] = await Promise.all([
        flightService.getFlightById(Number(flightId)),
        flightService.getFlightTicketClassesByFlightId(Number(flightId))
      ]);
      
      setFlight(flightData);
      setTicketClasses(ticketClassData);
    } catch (err: any) {
      setError('Failed to load booking information');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: BookingFormData) => {
    try {
      setSubmitting(true);
      setError('');

      // Use simplified booking service
      const booking = {
        flightId: Number(flightId),
        passengers: data.passengers,
        customerId: user!.accountId!,
        ticketClassId: data.ticketClassId
      };

      await ticketService.bookTickets(booking);

      navigate('/dashboard', {
        state: { 
          message: 'Booking successful! Your tickets have been confirmed.' 
        }
      });
    } catch (err: any) {
      setError(err.message || 'Booking failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const selectedTicketClass = watch('ticketClassId');
  const selectedClass = ticketClasses.find(tc => tc.ticketClassId === selectedTicketClass);

  const calculateTotalPrice = () => {
    if (!selectedClass) return 0;
    return selectedClass.specifiedFare * passengerCount;
  };

  if (loading) {
    return (
      <div className="booking-form">
        <div className="loading">Loading booking form...</div>
      </div>
    );
  }

  if (!flight) {
    return (
      <div className="booking-form">
        <div className="error-message">Flight not found</div>
      </div>
    );
  }

  return (
    <div className="booking-form">
      <div className="booking-header">
        <h1>Complete Your Booking</h1>
        <div className="flight-summary">
          <div className="flight-info">
            <span className="flight-code">{flight.flightCode}</span>
            <span className="route">
              {flight.departureCityName} → {flight.arrivalCityName}
            </span>
            <span className="date">
              {new Date(flight.departureTime).toLocaleDateString()}
            </span>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="booking-form-content">
        {error && <div className="error-message">{error}</div>}

        {/* Ticket Class Selection */}
        <div className="form-section">
          <h3>Select Ticket Class</h3>
          <div className="ticket-classes">
            {ticketClasses.map(ticketClass => (
              <label key={ticketClass.ticketClassId} className="ticket-class-option">
                <input
                  type="radio"
                  value={ticketClass.ticketClassId}
                  {...register('ticketClassId', {
                    required: 'Please select a ticket class',
                    valueAsNumber: true
                  })}
                />
                <div className="class-info">
                  <span className="class-name">{ticketClass.ticketClassName}</span>
                  <span className="class-price">${ticketClass.specifiedFare}</span>
                </div>
              </label>
            ))}
          </div>
          {errors.ticketClassId && (
            <span className="field-error">{errors.ticketClassId.message}</span>
          )}
        </div>

        {/* Passenger Information */}
        <div className="form-section">
          <h3>Passenger Information</h3>
          {Array(passengerCount).fill(null).map((_, index) => (
            <div key={index} className="passenger-form">
              <h4>Passenger {index + 1}</h4>
              
              <div className="form-row">
                <div className="form-group">
                  <label>First Name</label>
                  <input
                    type="text"
                    {...register(`passengers.${index}.firstName`, {
                      required: 'First name is required'
                    })}
                    className={errors.passengers?.[index]?.firstName ? 'error' : ''}
                  />
                  {errors.passengers?.[index]?.firstName && (
                    <span className="field-error">{errors.passengers[index]?.firstName?.message}</span>
                  )}
                </div>

                <div className="form-group">
                  <label>Last Name</label>
                  <input
                    type="text"
                    {...register(`passengers.${index}.lastName`, {
                      required: 'Last name is required'
                    })}
                    className={errors.passengers?.[index]?.lastName ? 'error' : ''}
                  />
                  {errors.passengers?.[index]?.lastName && (
                    <span className="field-error">{errors.passengers[index]?.lastName?.message}</span>
                  )}
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Date of Birth</label>
                  <input
                    type="date"
                    {...register(`passengers.${index}.dateOfBirth`, {
                      required: 'Date of birth is required'
                    })}
                    className={errors.passengers?.[index]?.dateOfBirth ? 'error' : ''}
                  />
                  {errors.passengers?.[index]?.dateOfBirth && (
                    <span className="field-error">{errors.passengers[index]?.dateOfBirth?.message}</span>
                  )}
                </div>

                <div className="form-group">
                  <label>Gender</label>
                  <select
                    {...register(`passengers.${index}.gender`, {
                      required: 'Gender is required'
                    })}
                    className={errors.passengers?.[index]?.gender ? 'error' : ''}
                  >
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                  {errors.passengers?.[index]?.gender && (
                    <span className="field-error">{errors.passengers[index]?.gender?.message}</span>
                  )}
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Citizen ID</label>
                  <input
                    type="text"
                    {...register(`passengers.${index}.citizenId`, {
                      required: 'Citizen ID is required'
                    })}
                    className={errors.passengers?.[index]?.citizenId ? 'error' : ''}
                  />
                  {errors.passengers?.[index]?.citizenId && (
                    <span className="field-error">{errors.passengers[index]?.citizenId?.message}</span>
                  )}
                </div>

                <div className="form-group">
                  <label>Phone Number</label>
                  <input
                    type="tel"
                    {...register(`passengers.${index}.phoneNumber`, {
                      required: 'Phone number is required'
                    })}
                    className={errors.passengers?.[index]?.phoneNumber ? 'error' : ''}
                  />
                  {errors.passengers?.[index]?.phoneNumber && (
                    <span className="field-error">{errors.passengers[index]?.phoneNumber?.message}</span>
                  )}
                </div>
              </div>

              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  {...register(`passengers.${index}.email`, {
                    required: 'Email is required'
                  })}
                  className={errors.passengers?.[index]?.email ? 'error' : ''}
                />
                {errors.passengers?.[index]?.email && (
                  <span className="field-error">{errors.passengers[index]?.email?.message}</span>
                )}
              </div>
            </div>
          ))}
        </div>

        {/* Booking Summary */}
        <div className="booking-summary">
          <h3>Booking Summary</h3>
          <div className="summary-details">
            <div className="summary-row">
              <span>Flight:</span>
              <span>{flight.flightCode}</span>
            </div>
            <div className="summary-row">
              <span>Passengers:</span>
              <span>{passengerCount}</span>
            </div>
            {selectedClass && (
              <>
                <div className="summary-row">
                  <span>Class:</span>
                  <span>{selectedClass.ticketClassName}</span>
                </div>
                <div className="summary-row">
                  <span>Price per ticket:</span>
                  <span>${selectedClass.specifiedFare}</span>
                </div>
                <div className="summary-row total">
                  <span>Total:</span>
                  <span>${calculateTotalPrice()}</span>
                </div>
              </>
            )}
          </div>
        </div>

        <div className="form-actions">
          <button 
            type="button" 
            className="btn btn-secondary"
            onClick={() => navigate(-1)}
          >
            Back
          </button>
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={submitting || !selectedClass}
          >
            {submitting ? 'Processing...' : `Book Flight - $${calculateTotalPrice()}`}
          </button>
        </div>
      </form>
    </div>
  );
};

export default BookingForm;
