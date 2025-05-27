import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { BookingConfirmation as BookingConfirmationType } from '../../services/bookingConfirmationService';
import './BookingConfirmation.css';

const BookingConfirmation: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  const { confirmationCode, confirmationData, message } = location.state || {};

  if (!confirmationCode || !confirmationData) {
    return (
      <div className="booking-confirmation">
        <div className="error-message">
          No booking confirmation data found. Please check your booking details.
        </div>
        <button onClick={() => navigate('/')} className="btn btn-primary">
          Return to Home
        </button>
      </div>
    );
  }

  const booking: BookingConfirmationType = confirmationData;

  const handlePrint = () => {
    window.print();
  };

  const handleEmailCopy = () => {
    const emailText = `
Booking Confirmation: ${booking.confirmationCode}

Flight: ${booking.flightInfo.flightCode}
Route: ${booking.flightInfo.departureCity} → ${booking.flightInfo.arrivalCity}
Departure: ${new Date(booking.flightInfo.departureTime).toLocaleString()}
Passengers: ${booking.passengerEmails.length}
Total Amount: $${booking.totalAmount}

Please save this confirmation code for future reference.
    `;
    
    navigator.clipboard.writeText(emailText).then(() => {
      alert('Booking details copied to clipboard!');
    });
  };

  return (
    <div className="booking-confirmation">
      <div className="confirmation-header">
        <div className="success-icon">✅</div>
        <h1>Booking Confirmed!</h1>
        {message && <p className="success-message">{message}</p>}
      </div>

      <div className="confirmation-details">
        <div className="confirmation-code-section">
          <h2>Your Confirmation Code</h2>
          <div className="confirmation-code">
            {booking.confirmationCode}
          </div>
          <p className="code-warning">
            ⚠️ <strong>Important:</strong> Please save this confirmation code. 
            You'll need it to retrieve or manage your booking later.
          </p>
        </div>

        <div className="booking-summary">
          <h3>Booking Details</h3>
          
          <div className="detail-section">
            <h4>Flight Information</h4>
            <div className="detail-grid">
              <div className="detail-item">
                <span className="label">Flight:</span>
                <span className="value">{booking.flightInfo.flightCode}</span>
              </div>
              <div className="detail-item">
                <span className="label">Route:</span>
                <span className="value">
                  {booking.flightInfo.departureCity} → {booking.flightInfo.arrivalCity}
                </span>
              </div>
              <div className="detail-item">
                <span className="label">Departure:</span>
                <span className="value">
                  {new Date(booking.flightInfo.departureTime).toLocaleDateString()} at{' '}
                  {new Date(booking.flightInfo.departureTime).toLocaleTimeString()}
                </span>
              </div>
              {booking.flightInfo.arrivalTime && (
                <div className="detail-item">
                  <span className="label">Arrival:</span>
                  <span className="value">
                    {new Date(booking.flightInfo.arrivalTime).toLocaleDateString()} at{' '}
                    {new Date(booking.flightInfo.arrivalTime).toLocaleTimeString()}
                  </span>
                </div>
              )}
            </div>
          </div>

          <div className="detail-section">
            <h4>Passenger Information</h4>
            <div className="passenger-list">
              {booking.tickets.map((ticket, index) => (
                <div key={index} className="passenger-item">
                  <span className="passenger-name">
                    Passenger {index + 1}
                  </span>
                  <span className="seat-number">Seat: {ticket.seatNumber}</span>
                  <span className="ticket-fare">${ticket.fare}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="detail-section">
            <h4>Booking Summary</h4>
            <div className="detail-grid">
              <div className="detail-item">
                <span className="label">Booking Date:</span>
                <span className="value">
                  {new Date(booking.bookingDate).toLocaleDateString()}
                </span>
              </div>
              <div className="detail-item">
                <span className="label">Total Passengers:</span>
                <span className="value">{booking.tickets.length}</span>
              </div>
              <div className="detail-item total-amount">
                <span className="label">Total Amount:</span>
                <span className="value">${booking.totalAmount}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="confirmation-actions">
        <button onClick={handlePrint} className="btn btn-secondary">
          Print Confirmation
        </button>
        <button onClick={handleEmailCopy} className="btn btn-secondary">
          Copy Details
        </button>
        <button 
          onClick={() => navigate('/booking-lookup')} 
          className="btn btn-primary"
        >
          Manage Booking
        </button>
        <button 
          onClick={() => navigate('/')} 
          className="btn btn-primary"
        >
          Book Another Flight
        </button>
      </div>

      <div className="next-steps">
        <h3>What's Next?</h3>
        <ul>
          <li>Save your confirmation code: <strong>{booking.confirmationCode}</strong></li>
          <li>Arrive at the airport at least 2 hours before departure</li>
          <li>Bring valid ID and your confirmation code</li>
          <li>You can manage your booking using the "Manage Booking" button above</li>
        </ul>
      </div>
    </div>
  );
};

export default BookingConfirmation;
