package com.flightmanagement.service.impl;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.*;
import com.flightmanagement.enums.AccountType;
import com.flightmanagement.mapper.TicketMapper;
import com.flightmanagement.repository.*;
import com.flightmanagement.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    private final FlightTicketClassService flightTicketClassService;

    private final PassengerService passengerService;

    private final FlightRepository flightRepository;

    private final TicketClassRepository ticketClassRepository;

    private final CustomerRepository customerRepository;

    private final PassengerRepository passengerRepository;

    private final EmailService emailService;

    private final AccountRepository accountRepository;

    public TicketServiceImpl(TicketRepository ticketRepository,
            TicketMapper ticketMapper,
            FlightTicketClassService flightTicketClassService,
            PassengerService passengerService,
            FlightRepository flightRepository,
            TicketClassRepository ticketClassRepository,
            CustomerRepository customerRepository,
            PassengerRepository passengerRepository,
            EmailService emailService,
            AccountRepository accountRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.flightTicketClassService = flightTicketClassService;
        this.passengerService = passengerService;
        this.flightRepository = flightRepository;
        this.ticketClassRepository = ticketClassRepository;
        this.customerRepository = customerRepository;
        this.passengerRepository = passengerRepository;
        this.emailService = emailService;
        this.accountRepository = accountRepository;
    }

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
    @Transactional
    public TicketDto createTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();

        ticket.setSeatNumber(ticketDto.getSeatNumber());
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
                    .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + ticketDto.getTicketClassId()));
            ticket.setTicketClass(ticketClass);
        }

        // For guest bookings (customer ID 0 or null), bookingCustomer remains null
        if (ticketDto.getBookCustomerId() != null && ticketDto.getBookCustomerId() != 0) {
            Customer bookingCustomer = customerRepository.findById(ticketDto.getBookCustomerId())
                    .orElseGet(() -> createCustomerFromAccount(ticketDto.getBookCustomerId()));
            ticket.setBookCustomer(bookingCustomer);
        }

        if (ticketDto.getPassengerId() != null) {
            Passenger passenger = passengerRepository.findById(ticketDto.getPassengerId())
                    .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + ticketDto.getPassengerId()));
            ticket.setPassenger(passenger);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        return ticketMapper.toDto(savedTicket);
    }
    /**
     * Send multi-passenger booking confirmation email
     */
    private void sendPassengersBookingConfirmation(List<TicketDto> tickets) {
        for (TicketDto ticketDto : tickets) {
            emailService.sendBookingConfirmation(ticketDto);
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

        String confirmationCode = generateConfirmationCode();

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
            ticketDto.setBookCustomerId(bookingDto.getCustomerId()); // can be null for guest
            ticketDto.setPassengerId(existingPassenger.getPassengerId());
            ticketDto.setSeatNumber(seatNumber);
            ticketDto.setFare(flightTicketClass.getSpecifiedFare());
            ticketDto.setTicketStatus((byte) 0); // 0: unpaid (default)
            ticketDto.setConfirmationCode(confirmationCode);

            TicketDto createdTicket = createTicket(ticketDto);
            bookedTickets.add(createdTicket);
        }

        // Update remaining ticket quantity
        flightTicketClassService.updateRemainingTickets(
                bookingDto.getFlightId(),
                bookingDto.getTicketClassId(),
                bookingDto.getPassengers().size());

        System.out.println("Booking successful - Created " + bookedTickets.size() + " tickets");

        // Send consolidated booking confirmation email for multiple passengers
        if (!bookedTickets.isEmpty()) {
            try {
                sendPassengersBookingConfirmation(bookedTickets);
            } catch (Exception e) {
                System.err.println("Failed to send multi-passenger booking confirmation email: " + e.getMessage());
            }
        }

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
        // Try to find existing passenger
        PassengerDto existingPassenger = passengerService.getPassengerByCitizenId(passengerDto.getCitizenId());
        
        // If not found, create new passenger
        if (existingPassenger == null) {
            return passengerService.createPassenger(passengerDto);
        }
        
        return existingPassenger;
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

        // Customer customer = ticket.getBookCustomer();
        // customer.setScore(customer.getScore() + ticket.getFare());

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
        if (account.getAccountType() != AccountType.CUSTOMER) {
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
