import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { planeService } from '../../services';
import { Plane } from '../../models';
import './PlaneManagement.css';
import TypeAhead from '../common/TypeAhead';
import { usePermissions } from '../../hooks/useAuth';

interface PlaneFormData {
    planeCode: string;
    planeType: string;
    seatQuantity: number;
}

const PlaneManagement: React.FC = () => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <div className="unauthorized">
                <h2>Access Denied</h2>
                <p>You do not have permission to access aircraft management.</p>
            </div>
        );
    }

    const [planes, setPlanes] = useState<Plane[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingPlane, setEditingPlane] = useState<Plane | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterType, setFilterType] = useState('');
    const [selectedPlaneType, setSelectedPlaneType] = useState<string>('');

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm<PlaneFormData>();

    // Aircraft type options for TypeAhead
    const aircraftTypeOptions = [
        { value: "Boeing 737-800", label: "Boeing 737-800" },
        { value: "Boeing 737-900", label: "Boeing 737-900" },
        { value: "Boeing 787-8", label: "Boeing 787-8" },
        { value: "Boeing 787-9", label: "Boeing 787-9" },
        { value: "Boeing 777-200", label: "Boeing 777-200" },
        { value: "Boeing 777-300ER", label: "Boeing 777-300ER" },
        { value: "Airbus A320", label: "Airbus A320" },
        { value: "Airbus A321", label: "Airbus A321" },
        { value: "Airbus A330", label: "Airbus A330" },
        { value: "Airbus A350", label: "Airbus A350" },
        { value: "Airbus A380", label: "Airbus A380" },
        { value: "ATR 72", label: "ATR 72" },
        { value: "Embraer E170", label: "Embraer E170" },
        { value: "Embraer E190", label: "Embraer E190" },
        { value: "Bombardier Q400", label: "Bombardier Q400" }
    ];

    useEffect(() => {
        loadPlanes();
    }, []);

    const loadPlanes = async () => {
        try {
            setLoading(true);
            const data = await planeService.getAllPlanes();
            setPlanes(data);
        } catch (err: any) {
            setError('Failed to load planes');
        } finally {
            setLoading(false);
        }
    };

    const onSubmit = async (data: PlaneFormData) => {
        try {
            if (editingPlane) {
                await planeService.updatePlane(editingPlane.planeId!, data);
            } else {
                await planeService.createPlane(data);
            }

            loadPlanes();
            handleCancel();
        } catch (err: any) {
            setError(err.message || 'Failed to save plane');
        }
    };

    const handleEdit = (plane: Plane) => {
        setEditingPlane(plane);
        setSelectedPlaneType(plane.planeType);
        reset({
            planeCode: plane.planeCode,
            planeType: plane.planeType,
            seatQuantity: plane.seatQuantity
        });
        setShowForm(true);
    };

    const handleDelete = async (planeId: number) => {
        if (!window.confirm('Are you sure you want to delete this plane? This will affect all associated flights.')) return;

        try {
            await planeService.deletePlane(planeId);
            loadPlanes();
        } catch (err: any) {
            setError(err.message || 'Failed to delete plane');
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingPlane(null);
        setSelectedPlaneType('');
        reset();
        setError('');
    };

    const handlePlaneTypeChange = (option: any) => {
        const planeType = option?.value || '';
        setSelectedPlaneType(planeType);
        // Update form value
        if (planeType) {
            register('planeType').onChange({ target: { value: planeType } });
        }
    };

    // Filter planes based on search term and type
    const filteredPlanes = planes.filter(plane => {
        const matchesSearch = plane.planeCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
                             plane.planeType.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesType = !filterType || plane.planeType.toLowerCase().includes(filterType.toLowerCase());
        return matchesSearch && matchesType;
    });

    // Get unique plane types for filter - combine existing fleet types with predefined options
    const existingPlaneTypes = [...new Set(planes.map(plane => plane.planeType))];
    const predefinedTypes = aircraftTypeOptions.map(option => option.value);
    const allPlaneTypes = [...new Set([...existingPlaneTypes, ...predefinedTypes])].sort();

    if (loading) {
        return <div className="loading">Loading plane data...</div>;
    }

    return (
        <div className="plane-management">
            <div className="management-header">
                <h2>Aircraft Fleet Management</h2>
                <button
                    className="btn btn-primary"
                    onClick={() => setShowForm(true)}
                >
                    Add New Aircraft
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            {/* Search and Filter Controls */}
            <div className="controls-section">
                <div className="search-controls">
                    <div className="search-group">
                        <label>Search Aircraft</label>
                        <input
                            type="text"
                            placeholder="Search by code or type..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="search-input"
                        />
                    </div>
                    
                    <div className="filter-group">
                        <label>Filter by Type</label>
                        <select
                            value={filterType}
                            onChange={(e) => setFilterType(e.target.value)}
                            className="filter-select"
                        >
                            <option value="">All Types</option>
                            {allPlaneTypes.map(type => (
                                <option key={type} value={type}>{type}</option>
                            ))}
                        </select>
                    </div>

                    <div className="stats-group">
                        <div className="stat-item">
                            <span className="stat-label">Total Aircraft:</span>
                            <span className="stat-value">{planes.length}</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Total Seats:</span>
                            <span className="stat-value">
                                {planes.reduce((sum, plane) => sum + plane.seatQuantity, 0)}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Add/Edit Form Modal */}
            {showForm && (
                <div className="form-modal">
                    <div className="form-container">
                        <h3>{editingPlane ? 'Edit Aircraft' : 'Add New Aircraft'}</h3>

                        <form onSubmit={handleSubmit(onSubmit)}>
                            <div className="form-row">
                                <div className="form-group">
                                    <label>Aircraft Code</label>
                                    <input
                                        type="text"
                                        {...register('planeCode', {
                                            required: 'Aircraft code is required',
                                            pattern: {
                                                value: /^[A-Z0-9-]+$/,
                                                message: 'Use only uppercase letters, numbers, and hyphens'
                                            }
                                        })}
                                        className={errors.planeCode ? 'error' : ''}
                                        placeholder="e.g., VN-A001, B737-001"
                                    />
                                    {errors.planeCode && (
                                        <span className="field-error">{errors.planeCode.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Aircraft Type</label>
                                    <TypeAhead
                                        options={aircraftTypeOptions}
                                        value={selectedPlaneType}
                                        onChange={handlePlaneTypeChange}
                                        placeholder="Search aircraft type..."
                                        className={errors.planeType ? 'error' : ''}
                                    />
                                    <input
                                        type="hidden"
                                        {...register('planeType', {
                                            required: 'Aircraft type is required'
                                        })}
                                        value={selectedPlaneType}
                                    />
                                    {errors.planeType && (
                                        <span className="field-error">{errors.planeType.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Seat Capacity</label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="850"
                                        {...register('seatQuantity', {
                                            required: 'Seat quantity is required',
                                            min: {
                                                value: 1,
                                                message: 'Must have at least 1 seat'
                                            },
                                            max: {
                                                value: 850,
                                                message: 'Maximum 850 seats allowed'
                                            },
                                            valueAsNumber: true
                                        })}
                                        className={errors.seatQuantity ? 'error' : ''}
                                        placeholder="e.g., 180"
                                    />
                                    {errors.seatQuantity && (
                                        <span className="field-error">{errors.seatQuantity.message}</span>
                                    )}
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingPlane ? 'Update Aircraft' : 'Add Aircraft'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Aircraft Grid */}
            <div className="planes-grid">
                {filteredPlanes.map(plane => (
                    <div key={plane.planeId} className="plane-card">
                        <div className="plane-header">
                            <div className="plane-code">{plane.planeCode}</div>
                            <div className="plane-actions">
                                <button 
                                    className="btn btn-sm btn-secondary"
                                    onClick={() => handleEdit(plane)}
                                >
                                    Edit
                                </button>
                                <button 
                                    className="btn btn-sm btn-danger"
                                    onClick={() => handleDelete(plane.planeId!)}
                                >
                                    Delete
                                </button>
                            </div>
                        </div>

                        <div className="plane-content">
                            <div className="plane-type">{plane.planeType}</div>
                            
                            <div className="plane-details">
                                <div className="detail-row">
                                    <span className="label">Total Seats:</span>
                                    <span className="value">{plane.seatQuantity}</span>
                                </div>
                                
                                <div className="detail-row">
                                    <span className="label">Capacity Category:</span>
                                    <span className="value">
                                        {plane.seatQuantity < 100 ? 'Regional' : 
                                         plane.seatQuantity < 200 ? 'Narrow-body' : 
                                         plane.seatQuantity < 350 ? 'Wide-body' : 'Large Wide-body'}
                                    </span>
                                </div>

                                <div className="seat-layout">
                                    <div className="seat-layout-label">Estimated Layout:</div>
                                    <div className="seat-breakdown">
                                        <div className="class-breakdown">
                                            <span className="class-name">Economy:</span>
                                            <span className="class-seats">~{Math.floor(plane.seatQuantity * 0.8)}</span>
                                        </div>
                                        <div className="class-breakdown">
                                            <span className="class-name">Business:</span>
                                            <span className="class-seats">~{Math.floor(plane.seatQuantity * 0.15)}</span>
                                        </div>
                                        <div className="class-breakdown">
                                            <span className="class-name">First:</span>
                                            <span className="class-seats">~{Math.floor(plane.seatQuantity * 0.05)}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {filteredPlanes.length === 0 && (
                <div className="no-data">
                    {searchTerm || filterType ? (
                        <p>No aircraft found matching your search criteria.</p>
                    ) : (
                        <p>No aircraft in the fleet. Add your first aircraft to get started.</p>
                    )}
                </div>
            )}
        </div>
    );
};

export default PlaneManagement;
