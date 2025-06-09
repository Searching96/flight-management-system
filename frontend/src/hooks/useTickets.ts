import { useState, useEffect, useCallback } from 'react';
import { Ticket, TicketRequest, TicketFilters } from '../models/Ticket';
import { ticketService } from '../services/ticketService';

export function useTickets(filters?: TicketFilters) {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchTickets = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await ticketService.getAllTickets(filters);
      setTickets(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchTickets();
  }, [fetchTickets]);

  const createTicket = useCallback(async (data: TicketRequest) => {
    try {
      const newTicket = await ticketService.createTicket(data);
      setTickets(prev => [...prev, newTicket]);
      return newTicket;
    } catch (err) {
      throw err;
    }
  }, []);

  const updateTicket = useCallback(async (id: number, data: Partial<TicketRequest>) => {
    try {
      const updatedTicket = await ticketService.updateTicket(id, data);
      setTickets(prev => prev.map(ticket => ticket.ticketId === id ? updatedTicket : ticket));
      return updatedTicket;
    } catch (err) {
      throw err;
    }
  }, []);

  const deleteTicket = useCallback(async (id: number) => {
    try {
      await ticketService.deleteTicket(id);
      setTickets(prev => prev.filter(ticket => ticket.ticketId !== id));
    } catch (err) {
      throw err;
    }
  }, []);

  const refetch = useCallback(() => {
    fetchTickets();
  }, [fetchTickets]);

  return {
    tickets,
    loading,
    error,
    createTicket,
    updateTicket,
    deleteTicket,
    refetch
  };
}
