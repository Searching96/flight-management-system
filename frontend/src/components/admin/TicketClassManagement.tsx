import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { ticketClassService } from '../../services';
import { TicketClass } from '../../models';
import './TicketClassManagement.css';
import { usePermissions } from '../../hooks/useAuth';

interface TicketClassFormData {
  ticketClassName: string;
  color: string;
}

const TicketClassManagement: React.FC = () => {
  const { canViewAdmin } = usePermissions();
  if (!canViewAdmin) {
    return (
      <div className="unauthorized">
        <h2>Access Denied</h2>
        <p>You do not have permission to access ticket class management.</p>
      </div>
    );
  }

  const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingClass, setEditingClass] = useState<TicketClass | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm<TicketClassFormData>();

  useEffect(() => {
    loadTicketClasses();
  }, []);

  const loadTicketClasses = async () => {
    try {
      setLoading(true);
      const data = await ticketClassService.getAllTicketClasses();
      setTicketClasses(data);
    } catch (err: any) {
      setError('Failed to load ticket classes');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: TicketClassFormData) => {
    try {
      if (editingClass) {
        await ticketClassService.updateTicketClass(editingClass.ticketClassId!, data);
      } else {
        await ticketClassService.createTicketClass(data);
      }

      loadTicketClasses();
      handleCancel();
    } catch (err: any) {
      setError(err.message || 'Failed to save ticket class');
    }
  };

  const handleEdit = (ticketClass: TicketClass) => {
    setEditingClass(ticketClass);
    reset({
      ticketClassName: ticketClass.ticketClassName,
      color: ticketClass.color
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this ticket class?')) return;

    try {
      await ticketClassService.deleteTicketClass(id);
      loadTicketClasses();
    } catch (err: any) {
      setError(err.message || 'Failed to delete ticket class');
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingClass(null);
    reset();
    setError('');
  };

  if (loading) {
    return <div className="loading">Loading ticket classes...</div>;
  }

  return (
    <div className="ticket-class-management">
      <div className="management-header">
        <h2>Ticket Class Management</h2>
        <button
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
        >
          Add New Ticket Class
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {showForm && (
        <div className="form-modal">
          <div className="form-container">
            <h3>{editingClass ? 'Edit Ticket Class' : 'Add New Ticket Class'}</h3>

            <form onSubmit={handleSubmit(onSubmit)}>
              <div className="form-row">
                <div className="form-group">
                  <label>Class Name</label>
                  <input
                    type="text"
                    {...register('ticketClassName', {
                      required: 'Class name is required'
                    })}
                    className={errors.ticketClassName ? 'error' : ''}
                    placeholder="e.g., Economy, Business, First Class"
                  />
                  {errors.ticketClassName && (
                    <span className="field-error">{errors.ticketClassName.message}</span>
                  )}
                </div>

                <div className="form-group">
                  <label>Color</label>
                  <input
                    type="color"
                    {...register('color', {
                      required: 'Color is required'
                    })}
                    className={errors.color ? 'error' : ''}
                  />
                  {errors.color && (
                    <span className="field-error">{errors.color.message}</span>
                  )}
                </div>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingClass ? 'Update Class' : 'Create Class'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="ticket-classes-grid">
        {ticketClasses.map(ticketClass => (
          <div key={ticketClass.ticketClassId} className="ticket-class-card">
            <div className="class-header">
              <span 
                className="class-badge" 
                style={{ backgroundColor: ticketClass.color }}
              >
                {ticketClass.ticketClassName}
              </span>
              <div className="class-actions">
                <button 
                  className="btn btn-sm btn-secondary"
                  onClick={() => handleEdit(ticketClass)}
                >
                  Edit
                </button>
                <button 
                  className="btn btn-sm btn-danger"
                  onClick={() => handleDelete(ticketClass.ticketClassId!)}
                >
                  Delete
                </button>
              </div>
            </div>
            <div className="class-details">
              <div className="detail-row">
                <span className="label">ID:</span>
                <span className="value">{ticketClass.ticketClassId}</span>
              </div>
              <div className="detail-row">
                <span className="label">Name:</span>
                <span className="value">{ticketClass.ticketClassName}</span>
              </div>
              <div className="detail-row">
                <span className="label">Color:</span>
                <div className="color-display">
                  <span 
                    className="color-swatch"
                    style={{ backgroundColor: ticketClass.color }}
                  ></span>
                  <span className="value">{ticketClass.color}</span>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {ticketClasses.length === 0 && (
        <div className="no-data">
          <p>No ticket classes found. Add your first ticket class to get started.</p>
        </div>
      )}
    </div>
  );
};

export default TicketClassManagement;
