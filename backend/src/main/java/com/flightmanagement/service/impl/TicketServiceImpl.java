package com.flightmanagement.service.impl;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.*;
import com.flightmanagement.mapper.TicketMapper;
import com.flightmanagement.repository.*;
import com.flightmanagement.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private FlightTicketClassService flightTicketClassService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TicketClassRepository ticketClassRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<TicketDto> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAllActive();
        return ticketMapper.toDtoList(tickets);
    }

    @Override
    public TicketDto getTicketById(Integer id) {
        Ticket ticket = ticketRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        return ticketMapper.toDto(ticket);
    }

    @Override
    public TicketDto createTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setSeatNumber(ticketDto.getSeatNumber());
        // ticket.setTicketStatus(ticketDto.getTicketStatus());
        ticket.setPaymentTime(ticketDto.getPaymentTime());
        ticket.setFare(ticketDto.getFare());
        ticket.setConfirmationCode(ticketDto.getConfirmationCode());
        ticket.setDeletedAt(null);

        // Set entity relationships
        if (ticketDto.getFlightId() != null) {
            Flight flight = flightRepository.findById(ticketDto.getFlightId())
                    .orElseThrow(() -> new RuntimeException("Flight not found with id: " + ticketDto.getFlightId()));
            ticket.setFlight(flight);
        }

        if (ticketDto.getTicketClassId() != null) {
            TicketClass ticketClass = ticketClassRepository.findById(ticketDto.getTicketClassId())
                    .orElseThrow(() -> new RuntimeException(
                            "TicketClass not found with id: " + ticketDto.getTicketClassId()));
            ticket.setTicketClass(ticketClass);
        }

        Customer bookingCustomer = null;
        if (ticketDto.getBookCustomerId() != null) {
            bookingCustomer = customerRepository.findById(ticketDto.getBookCustomerId())
                    .orElseGet(() -> createCustomerFromAccount(ticketDto.getBookCustomerId()));
            ticket.setBookCustomer(bookingCustomer);
        }

        Passenger passenger = null;
        if (ticketDto.getPassengerId() != null) {
            passenger = passengerRepository.findById(ticketDto.getPassengerId())
                    .orElseThrow(
                            () -> new RuntimeException("Passenger not found with id: " + ticketDto.getPassengerId()));
            ticket.setPassenger(passenger);
        }


        Ticket savedTicket = ticketRepository.save(ticket);
        TicketDto savedTicketDto = ticketMapper.toDto(savedTicket);

        // Send single ticket confirmation email after successful ticket creation
        if (savedTicket.getTicketStatus() == 0) {
            System.out.println("Ticket info: " + savedTicketDto +
                    " at 2025-06-11 07:34:18 UTC by thinh0704hcm");
            try {
                sendSingleTicketConfirmation(savedTicketDto, bookingCustomer, passenger);
            } catch (Exception e) {
                System.err.println("Failed to send ticket confirmation email for ticket: " +
                        savedTicketDto.getTicketId() + " - " + e.getMessage());
            }
        }

        return savedTicketDto;
    }

    /**
     * Send single ticket confirmation email to customer
     */
    private void sendSingleTicketConfirmation(TicketDto ticket, Customer customer, Passenger passenger) {
        try {
            if (ticket.getBookCustomerId() != null) {
                // Customer booking
                if (customer == null || customer.getAccount() == null) {
                    System.err.println("Cannot send ticket confirmation: customer or account is null at 2025-06-11 10:47:59 UTC by thinh0704hcm");
                    return;
                }

                // Get flight information
                Flight flight = flightRepository.findById(ticket.getFlightId())
                        .orElseThrow(() -> new RuntimeException("Flight not found"));

                String customerEmail = customer.getAccount().getEmail();
                String customerName = customer.getAccount().getAccountName();
                String passengerName = passenger != null ? passenger.getPassengerName() : "Hành khách";

                // Format departure time
                String formattedDepartureTime = flight.getDepartureTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                // Check if payment is needed
                boolean needsPayment = ticket.getTicketStatus() == null || ticket.getTicketStatus() == 0;

                // Send single ticket confirmation email for customer booking
                emailService.sendSingleTicketConfirmation(
                        customerEmail,
                        customerName,
                        passengerName,
                        ticket.getConfirmationCode(),
                        flight.getFlightCode(),
                        flight.getDepartureAirport().getCityName(),
                        flight.getArrivalAirport().getCityName(), // Fixed: was departure twice
                        formattedDepartureTime,
                        ticket.getSeatNumber(),
                        ticket.getFare(),
                        needsPayment
                );
            } else {
                // Guest booking - use passenger information
                if (passenger == null) {
                    System.err.println("Cannot send ticket confirmation: passenger is null for guest booking at 2025-06-11 10:47:59 UTC by thinh0704hcm");
                    return;
                }

                // Get flight information
                Flight flight = flightRepository.findById(ticket.getFlightId())
                        .orElseThrow(() -> new RuntimeException("Flight not found"));

                String guestEmail = passenger.getEmail(); // Assuming passenger has email field
                String guestName = passenger.getPassengerName();
                String passengerName = passenger.getPassengerName();

                // Format departure time
                String formattedDepartureTime = flight.getDepartureTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                // Check if payment is needed
                boolean needsPayment = ticket.getTicketStatus() == null || ticket.getTicketStatus() == 0;

                // Send single ticket confirmation email for guest booking
                emailService.sendSingleTicketConfirmation(
                        guestEmail,
                        guestName,
                        passengerName,
                        ticket.getConfirmationCode(),
                        flight.getFlightCode(),
                        flight.getDepartureAirport().getCityName(),
                        flight.getArrivalAirport().getCityName(),
                        formattedDepartureTime,
                        ticket.getSeatNumber(),
                        ticket.getFare(),
                        needsPayment
                );
            }
        } catch (Exception e) {
            System.err.println("Error sending single ticket confirmation email: " + e.getMessage());
            throw new RuntimeException("Failed to send ticket confirmation email", e);
        }
    }

    @Override
    public TicketDto updateTicket(Integer id, TicketDto ticketDto) {
        Ticket existingTicket = ticketRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        System.out.println("Updating ticket with details: " + ticketDto);
        existingTicket.setSeatNumber(ticketDto.getSeatNumber());
        existingTicket.setTicketStatus(ticketDto.getTicketStatus());
        existingTicket.setFare(ticketDto.getFare());
        existingTicket.setPaymentTime(ticketDto.getPaymentTime());

        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public void deleteTicket(Integer id) {
        Ticket ticket = ticketRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        ticket.setDeletedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Override
    public List<TicketDto> getTicketsByFlightId(Integer flightId) {
        List<Ticket> tickets = ticketRepository.findByFlightId(flightId);
        return ticketMapper.toDtoList(tickets);
    }

    @Override
    public List<TicketDto> getTicketsByCustomerId(Integer customerId) {
        List<Ticket> tickets = ticketRepository.findByCustomerId(customerId);
        return ticketMapper.toDtoList(tickets);
    }

    @Override
    public List<TicketDto> getTicketsByPassengerId(Integer passengerId) {
        List<Ticket> tickets = ticketRepository.findByPassengerId(passengerId);
        return ticketMapper.toDtoList(tickets);
    }

    @Override
    public List<TicketDto> getTicketsByStatus(Byte ticketStatus) {
        List<Ticket> tickets = ticketRepository.findByTicketStatus(ticketStatus);
        return ticketMapper.toDtoList(tickets);
    }

    @Override
    @Transactional
    public List<TicketDto> bookTickets(BookingDto bookingDto) {
        validateBookingRequest(bookingDto);

        List<TicketDto> bookedTickets = new ArrayList<>();

        // Log booking attempt for debugging
        System.out.println("Booking attempt - Flight: " + bookingDto.getFlightId() +
                ", Class: " + bookingDto.getTicketClassId() +
                ", Passengers: " + bookingDto.getPassengers().size());

        // Check availability first
        FlightTicketClassDto flightTicketClass = flightTicketClassService.getFlightTicketClassById(
                bookingDto.getFlightId(), bookingDto.getTicketClassId());

        System.out.println("Available seats: " + flightTicketClass.getRemainingTicketQuantity());

        if (flightTicketClass.getRemainingTicketQuantity() < bookingDto.getPassengers().size()) {
            throw new RuntimeException("Not enough tickets available. Requested: " +
                    bookingDto.getPassengers().size() +
                    ", Available: " + flightTicketClass.getRemainingTicketQuantity());
        }

        // Create tickets for each passenger
        for (int i = 0; i < bookingDto.getPassengers().size(); i++) {
            PassengerDto passengerDto = bookingDto.getPassengers().get(i);
            String seatNumber = (bookingDto.getSeatNumbers() != null && i < bookingDto.getSeatNumbers().size())
                    ? bookingDto.getSeatNumbers().get(i)
                    : generateSeatNumber(flightTicketClass.getTicketClassName(), i);

            // Ensure passenger exists or create new one
            PassengerDto existingPassenger = getOrCreatePassenger(passengerDto);

            // Create ticket
            TicketDto ticketDto = new TicketDto();
            ticketDto.setFlightId(bookingDto.getFlightId());
            ticketDto.setTicketClassId(bookingDto.getTicketClassId());
            ticketDto.setBookCustomerId(bookingDto.getCustomerId());
            ticketDto.setPassengerId(existingPassenger.getPassengerId());
            ticketDto.setSeatNumber(seatNumber);
            ticketDto.setFare(flightTicketClass.getSpecifiedFare());
            ticketDto.setTicketStatus((byte) 0); // 0: unpaid (default)

            TicketDto createdTicket = createTicket(ticketDto);
            bookedTickets.add(createdTicket);
        }

        // Update remaining ticket quantity
        flightTicketClassService.updateRemainingTickets(
                bookingDto.getFlightId(),
                bookingDto.getTicketClassId(),
                bookingDto.getPassengers().size());

        System.out.println("Booking successful - Created " + bookedTickets.size() + " tickets");

        return bookedTickets;
    }

    private String generateSeatNumber(String className, int index) {
        String prefix = className.equals("Economy") ? "E" : className.equals("Business") ? "B" : "F";
        return prefix + String.format("%02d", index + 1);
    }

    private void validateBookingRequest(BookingDto bookingDto) {
        if (bookingDto.getPassengers() == null || bookingDto.getPassengers().isEmpty()) {
            throw new IllegalArgumentException("At least one passenger is required");
        }

        // Only validate seat numbers if they are provided
        if (bookingDto.getSeatNumbers() != null && !bookingDto.getSeatNumbers().isEmpty()) {
            if (bookingDto.getSeatNumbers().size() != bookingDto.getPassengers().size()) {
                throw new IllegalArgumentException("Number of seat numbers must match number of passengers");
            }

            // Check seat availability only for provided seat numbers
            for (String seatNumber : bookingDto.getSeatNumbers()) {
                if (!isSeatAvailable(bookingDto.getFlightId(), seatNumber)) {
                    throw new IllegalArgumentException("Seat " + seatNumber + " is already taken");
                }
            }
        }

        // Validate flight and ticket class availability
        if (!isFlightAvailable(bookingDto.getFlightId(), bookingDto.getTicketClassId(),
                bookingDto.getPassengers().size())) {
            throw new IllegalArgumentException(
                    "Flight or ticket class not available for the requested number of passengers");
        }
    }

    private boolean isFlightAvailable(Integer flightId, Integer ticketClassId, int passengerCount) {
        try {
            FlightTicketClassDto flightTicketClass = flightTicketClassService.getFlightTicketClassById(flightId,
                    ticketClassId);
            return flightTicketClass.getRemainingTicketQuantity() >= passengerCount;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private PassengerDto getOrCreatePassenger(PassengerDto passengerDto) {
        try {
            return passengerService.getPassengerByCitizenId(passengerDto.getCitizenId());
        } catch (RuntimeException e) {
            return passengerService.createPassenger(passengerDto);
        }
    }

    @Override
    public String generateConfirmationCode() {
        LocalDateTime today = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        // Generate random 4-character suffix
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int idx = (int) (Math.random() * chars.length());
            suffix.append(chars.charAt(idx));
        }

        return "FMS-" + dateStr + "-" + suffix;
    }

    @Override
    public List<TicketDto> getTicketsOnConfirmationCode(String code) {
        List<Ticket> tickets = ticketRepository.findByConfirmationCode(code);
        return ticketMapper.toDtoList(tickets);
    }

    @Override
    public TicketDto payTicket(Integer ticketId, String orderId) {
        Ticket ticket = ticketRepository.findActiveById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        ticket.setTicketStatus((byte) 1); // 1: paid
        ticket.setPaymentTime(LocalDateTime.now());
        ticket.setOrderId(orderId);

//        Customer customer = ticket.getBookCustomer();
//        customer.setScore(customer.getScore() + ticket.getFare());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public void cancelTicket(Integer ticketId) {
        Ticket ticket = ticketRepository.findActiveById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        ticket.setDeletedAt(LocalDateTime.now()); // 3: canceled
        ticketRepository.save(ticket);
    }

    @Override
    public boolean isSeatAvailable(Integer flightId, String seatNumber) {
        return ticketRepository.findByFlightIdAndSeatNumber(flightId, seatNumber).isEmpty();
    }

    /**
     * Creates a Customer entity from an existing Account
     */
    private Customer createCustomerFromAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        // Verify this is a customer account (accountType = 1)
        if (account.getAccountType() != 1) {
            throw new RuntimeException("Account " + accountId + " is not a customer account");
        }

        Customer customer = new Customer();
        customer.setCustomerId(accountId);
        customer.setAccount(account);
        customer.setScore(0); // Default score
        customer.setDeletedAt(null);

        return customerRepository.save(customer);
    }
}
