import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { bookingConfirmationService, BookingConfirmation } from '../../services/bookingConfirmationService';
import './BookingLookup.css';

const BookingLookup: React.FC = () => {
  const navigate = useNavigate();
  const [searchData, setSearchData] = useState({
    confirmationCode: '',
    email: ''
  });
  const [booking, setBooking] = useState<BookingConfirmation | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!searchData.confirmationCode.trim()) {
      setError('Please enter a confirmation code');
      return;
    }

    setLoading(true);
    setError('');
    setBooking(null);

    try {
      const result = await bookingConfirmationService.lookupBooking({
        confirmationCode: searchData.confirmationCode.trim(),
        email: searchData.email.trim() || undefined
      });

      if (result) {
        setBooking(result);
      } else {
        setError('Booking not found. Please check your confirmation code and try again.');
      }
    } catch (err: any) {
      setError(err.message || 'An error occurred while looking up your booking.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async () => {
    if (!booking) return;

    const confirmed = window.confirm(
      'Are you sure you want to cancel this booking? This action cannot be undone.'
    );

    if (confirmed) {
      try {
        await bookingConfirmationService.cancelBooking(booking.confirmationCode);
        alert('Booking cancelled successfully.');
        setBooking(null);
        setSearchData({ confirmationCode: '', email: '' });
      } catch (err: any) {
        alert('Failed to cancel booking: ' + (err.message || 'Unknown error'));
      }
    }
  };

  const handlePrintBooking = () => {
    window.print();
  };

  return (
    <div className="booking-lookup">
      <div className="lookup-header">
        <h1>Manage Your Booking</h1>
        <p>Enter your booking confirmation code to view and manage your reservation</p>
      </div>

      <div className="lookup-form-container">
        <form onSubmit={handleSearch} className="lookup-form">
          <div className="form-group">
            <label htmlFor="confirmationCode">Confirmation Code *</label>
            <input
              type="text"
              id="confirmationCode"
              value={searchData.confirmationCode}
              onChange={(e) => setSearchData(prev => ({ 
                ...prev, 
                confirmationCode: e.target.value.toUpperCase() 
              }))}
              placeholder="FMS-YYYYMMDD-XXXX"
              className="form-control"
              required
            />
            <small className="form-help">
              Format: FMS-YYYYMMDD-XXXX (e.g., FMS-20240527-A1B2)
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="email">Email (Optional)</label>
            <input
              type="email"
              id="email"
              value={searchData.email}
              onChange={(e) => setSearchData(prev => ({ 
                ...prev, 
                email: e.target.value 
              }))}
              placeholder="Enter email for verification"
              className="form-control"
            />
            <small className="form-help">
              Enter email for additional verification (recommended)
            </small>
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="form-actions">
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Searching...' : 'Find Booking'}
            </button>
            <button 
              type="button" 
              className="btn btn-secondary"
              onClick={() => navigate('/')}
            >
              Back to Home
            </button>
          </div>
        </form>
      </div>

      {booking && (
        <div className="booking-details">
          <div className="booking-header">
            <h2>Booking Found</h2>
            <div className="confirmation-code">
              Code: {booking.confirmationCode}
            </div>
          </div>

          <div className="booking-info">
            <div className="flight-section">
              <h3>Flight Information</h3>
              <div className="info-grid">
                <div className="info-item">
                  <span className="label">Flight:</span>
                  <span className="value">{booking.flightInfo.flightCode}</span>
                </div>
                <div className="info-item">
                  <span className="label">Route:</span>
                  <span className="value">
                    {booking.flightInfo.departureCity} → {booking.flightInfo.arrivalCity}
                  </span>
                </div>
                <div className="info-item">
                  <span className="label">Departure:</span>
                  <span className="value">
                    {new Date(booking.flightInfo.departureTime).toLocaleString()}
                  </span>
                </div>
                {booking.flightInfo.arrivalTime && (
                  <div className="info-item">
                    <span className="label">Arrival:</span>
                    <span className="value">
                      {new Date(booking.flightInfo.arrivalTime).toLocaleString()}
                    </span>
                  </div>
                )}
              </div>
            </div>

            <div className="passengers-section">
              <h3>Passenger Information</h3>
              <div className="passengers-list">
                {booking.tickets.map((ticket, index) => (
                  <div key={index} className="passenger-card">
                    <div className="passenger-info">
                      <span className="passenger-number">Passenger {index + 1}</span>
                      <span className="seat-info">Seat: {ticket.seatNumber}</span>
                    </div>
                    <div className="ticket-info">
                      <span className="fare">${ticket.fare}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="booking-summary">
              <h3>Booking Summary</h3>
              <div className="summary-grid">
                <div className="summary-item">
                  <span className="label">Booking Date:</span>
                  <span className="value">
                    {new Date(booking.bookingDate).toLocaleDateString()}
                  </span>
                </div>
                <div className="summary-item">
                  <span className="label">Total Passengers:</span>
                  <span className="value">{booking.tickets.length}</span>
                </div>
                <div className="summary-item total">
                  <span className="label">Total Amount:</span>
                  <span className="value">${booking.totalAmount}</span>
                </div>
              </div>
            </div>
          </div>

          <div className="booking-actions">
            <button 
              onClick={handlePrintBooking}
              className="btn btn-secondary"
            >
              Print Booking
            </button>
            <button 
              onClick={() => navigate('/booking-confirmation', {
                state: {
                  confirmationCode: booking.confirmationCode,
                  confirmationData: booking,
                  message: 'Booking retrieved successfully!'
                }
              })}
              className="btn btn-primary"
            >
              View Full Details
            </button>
            <button 
              onClick={handleCancelBooking}
              className="btn btn-danger"
            >
              Cancel Booking
            </button>
          </div>
        </div>
      )}

      <div className="lookup-help">
        <h3>Need Help?</h3>
        <ul>
          <li>Make sure you enter the confirmation code exactly as shown</li>
          <li>The confirmation code format is: FMS-YYYYMMDD-XXXX</li>
          <li>If you can't find your booking, contact customer service</li>
          <li>Guest bookings are stored locally for up to 10 recent bookings</li>
        </ul>
      </div>
    </div>
  );
};

export default BookingLookup;
