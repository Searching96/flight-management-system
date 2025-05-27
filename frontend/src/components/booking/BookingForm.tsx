import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { useAuth } from '../../hooks/useAuth';
import { flightService, ticketService, passengerService, bookingConfirmationService } from '../../services';
import { Flight } from '../../models';
import './BookingForm.css';
import TypeAhead from '../common/TypeAhead';

interface BookingFormData {
  passengers: {
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    gender: string;
    citizenId: string;
    phoneNumber: string;
    email: string;
  }[];
  ticketClassId: number;
  useFrequentFlyer: boolean;
}

const BookingForm: React.FC = () => {
  const { flightId } = useParams<{ flightId: string }>();
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [flight, setFlight] = useState<Flight | null>(null);  const [ticketClasses, setTicketClasses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);  const [error, setError] = useState('');
  const [validationWarnings, setValidationWarnings] = useState<string[]>([]);

  const passengerCount = location.state?.passengerCount || 1;

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
    control  } = useForm<BookingFormData>({
    defaultValues: {
      passengers: Array(passengerCount).fill(null).map(() => ({
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        gender: '',
        citizenId: '',
        phoneNumber: '',
        email: ''
      })),
      ticketClassId: 0,
      useFrequentFlyer: false
    }
  });

  const { fields } = useFieldArray({
    control,
    name: 'passengers'
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
  };  const onSubmit = async (data: BookingFormData) => {
    try {
      setSubmitting(true);
      setError('');
      setValidationWarnings([]);

      // Validate passenger data using the service
      const validationErrors: string[] = [];
      for (const passenger of data.passengers) {
        const passengerData = passengerService.transformPassengerData(passenger);
        const errors = passengerService.validatePassengerData(passengerData);
        validationErrors.push(...errors);
      }
      
      if (validationErrors.length > 0) {
        setError('Please correct the following errors: ' + validationErrors.join(', '));
        return;
      }

      // Check for existing passengers and show warnings
      const warnings: string[] = [];
      for (const passenger of data.passengers) {
        if (passenger.citizenId) {
          const existing = await passengerService.findExistingPassenger(passenger.citizenId);
          if (existing) {
            warnings.push(`Passenger with Citizen ID ${passenger.citizenId} already exists in the system.`);
          }
        }
      }

      if (warnings.length > 0) {
        setValidationWarnings(warnings);
      }      // Transform passenger data for validation only
      // const transformedPassengers = data.passengers.map(p => 
      //   passengerService.transformPassengerData(p)
      // );

      // Generate seat numbers for demonstration
      const seatNumbers = data.passengers.map((_, index) => {
        const selectedClass = ticketClasses.find(tc => tc.ticketClassId === data.ticketClassId);
        const seatPrefix = selectedClass?.ticketClassName === 'Economy' ? 'A' : 
                          selectedClass?.ticketClassName === 'Business' ? 'B' : 'C';
        return `${seatPrefix}${index + 1}`;
      });      const booking = {
        flightId: Number(flightId),
        passengers: data.passengers, // Keep original format for BookingRequest
        customerId: data.useFrequentFlyer ? user!.accountId! : null,
        ticketClassId: data.ticketClassId,
        seatNumbers: seatNumbers
      };

      // Book the tickets
      await ticketService.bookTickets(booking);

      // Handle guest booking confirmation
      if (!data.useFrequentFlyer) {
        const confirmationCode = bookingConfirmationService.generateConfirmationCode();
          // Create confirmation data compatible with BookingConfirmation interface
        const tickets = data.passengers.map((_, index) => ({
          ticketId: undefined, // Will be assigned by backend
          flightId: Number(flightId),
          bookCustomerId: null,
          passengerId: undefined,
          ticketClassId: data.ticketClassId,
          seatNumber: seatNumbers[index],
          fare: selectedClass?.specifiedFare || 0
        }));

        const confirmationData = bookingConfirmationService.createConfirmation(
          tickets,
          data.passengers.map(p => p.email),
          flight!
        );
        
        bookingConfirmationService.storeGuestBookingConfirmation(confirmationData);
        await bookingConfirmationService.sendConfirmationEmail(confirmationData);

        // Navigate to confirmation page for guest bookings
        navigate('/booking-confirmation', {
          state: { 
            confirmationCode,
            confirmationData,
            message: 'Guest booking successful! Please save your confirmation code for future reference.'
          }
        });
      } else {
        // Navigate to dashboard for logged-in users
        navigate('/dashboard', {
          state: { 
            message: 'Booking successful! Your tickets have been confirmed and linked to your account.' 
          }
        });
      }
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

  // Gender options for TypeAhead
  const genderOptions = [
    { value: 'MALE', label: 'Male' },
    { value: 'FEMALE', label: 'Female' },
    { value: 'OTHER', label: 'Other' }
  ];

  // Country code options for TypeAhead (for phone numbers)
  const countryCodeOptions = [
    { value: '+84', label: '+84 (Vietnam)' },
    { value: '+1', label: '+1 (US/Canada)' },
    { value: '+44', label: '+44 (UK)' },
    { value: '+86', label: '+86 (China)' },
    { value: '+81', label: '+81 (Japan)' },
    { value: '+82', label: '+82 (South Korea)' },
    { value: '+65', label: '+65 (Singapore)' },
    { value: '+66', label: '+66 (Thailand)' },
    { value: '+60', label: '+60 (Malaysia)' },
    { value: '+62', label: '+62 (Indonesia)' }
  ];

  const renderPassengerForm = (index: number) => {
    const [selectedGender, setSelectedGender] = useState('');
    const [selectedCountryCode, setSelectedCountryCode] = useState('+84');

    return (
      <div key={index} className="passenger-form">
        <h4>Passenger {index + 1}</h4>
        
        <div className="form-row">
          <div className="form-group">
            <label>First Name *</label>
            <input
              type="text"
              {...register(`passengers.${index}.firstName`, {
                required: 'First name is required'
              })}
              placeholder="Enter first name"
            />
            {errors.passengers?.[index]?.firstName && (
              <span className="field-error">{errors.passengers[index]?.firstName?.message}</span>
            )}
          </div>

          <div className="form-group">
            <label>Last Name *</label>
            <input
              type="text"
              {...register(`passengers.${index}.lastName`, {
                required: 'Last name is required'
              })}
              placeholder="Enter last name"
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
              {...register(`passengers.${index}.dateOfBirth`)}
              max={new Date().toISOString().split('T')[0]}
            />
          </div>

          <div className="form-group">
            <label>Gender</label>
            <TypeAhead
              options={genderOptions}
              value={selectedGender}
              onChange={(option) => {
                const gender = option?.value as string || '';
                setSelectedGender(gender);
                setValue(`passengers.${index}.gender`, gender);
              }}
              placeholder="Select gender..."
              allowClear={true}
            />
            <input
              type="hidden"
              {...register(`passengers.${index}.gender`)}
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Email *</label>
            <input
              type="email"
              {...register(`passengers.${index}.email`, {
                required: 'Email is required',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Invalid email address'
                }
              })}
              placeholder="Enter email address"
            />
            {errors.passengers?.[index]?.email && (
              <span className="field-error">{errors.passengers[index]?.email?.message}</span>
            )}
          </div>

          <div className="form-group">
            <label>Citizen ID *</label>
            <input
              type="text"
              {...register(`passengers.${index}.citizenId`, {
                required: 'Citizen ID is required'
              })}
              placeholder="Enter citizen ID number"
            />
            {errors.passengers?.[index]?.citizenId && (
              <span className="field-error">{errors.passengers[index]?.citizenId?.message}</span>
            )}
          </div>
        </div>

        <div className="form-row">
          <div className="form-group phone-group">
            <label>Phone Number</label>
            <div className="phone-input-group">
              <TypeAhead
                options={countryCodeOptions}
                value={selectedCountryCode}
                onChange={(option) => {
                  const newCountryCode = option?.value as string || '+84';
                  setSelectedCountryCode(newCountryCode);
                  // Update the phone number with new country code
                  const currentPhone = watch(`passengers.${index}.phoneNumber`) || '';
                  const phoneWithoutCode = currentPhone.replace(/^\+\d+\s*/, '');
                  setValue(`passengers.${index}.phoneNumber`, `${newCountryCode} ${phoneWithoutCode}`);
                }}
                placeholder="Code"
                className="country-code-select"
              />
              <input
                type="tel"
                placeholder="Phone number"
                className="phone-number-input"
                onChange={(e) => {
                  const phoneNumber = `${selectedCountryCode} ${e.target.value}`;
                  setValue(`passengers.${index}.phoneNumber`, phoneNumber);
                }}
              />
              <input
                type="hidden"
                {...register(`passengers.${index}.phoneNumber`)}
              />
            </div>
          </div>
        </div>
      </div>
    );
  };

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
      </div>      <form onSubmit={handleSubmit(onSubmit)} className="booking-form-content">
        {error && <div className="error-message">{error}</div>}
        {validationWarnings.length > 0 && (
          <div className="warning-message">
            <h4>⚠️ Warnings:</h4>
            <ul>
              {validationWarnings.map((warning, index) => (
                <li key={index}>{warning}</li>
              ))}
            </ul>
          </div>
        )}

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
        </div>        {/* Passenger Information */}
        <div className="form-section">
          <h3>Passenger Information</h3>
          {fields.map((_, index) => renderPassengerForm(index))}
        </div>

        {/* Frequent Flyer Program */}
        {user && (
          <div className="form-section">
            <h3>Frequent Flyer Program</h3>
            <div className="form-group">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  {...register('useFrequentFlyer')}
                />
                <span className="checkmark"></span>
                Join frequent flyer program and link this booking to your account
              </label>
              <p className="form-note">
                Checking this option will link your booking to your customer profile for frequent flyer benefits.
              </p>
            </div>
          </div>
        )}

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
