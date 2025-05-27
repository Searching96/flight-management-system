import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { employeeService } from '../../services';
import { Employee } from '../../models';
import './EmployeeManagement.css';
import TypeAhead from '../common/TypeAhead';

interface EmployeeFormData {
    accountName: string;
    email: string;
    phoneNumber: string;
    citizenId: string;
    employeeType: number;
}

const EmployeeManagement: React.FC = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterType, setFilterType] = useState('');
    const [selectedEmployeeType, setSelectedEmployeeType] = useState<number | ''>('');

    const {
        register,
        handleSubmit,
        reset,
        setValue,
        formState: { errors }
    } = useForm<EmployeeFormData>();

    // Employee type options for TypeAhead
    const employeeTypeOptions = [
        { value: 1, label: 'Flight Schedule Reception' },
        { value: 2, label: 'Ticket Sales/Booking' },
        { value: 3, label: 'Customer Service' },
        { value: 4, label: 'Accounting' },
        { value: 5, label: 'System Administrator' }
    ];

    useEffect(() => {
        loadEmployees();
    }, []);

    const loadEmployees = async () => {
        try {
            setLoading(true);
            const data = await employeeService.getAllEmployees();
            setEmployees(data);
        } catch (err: any) {
            setError('Failed to load employees');
        } finally {
            setLoading(false);
        }
    };

    const onSubmit = async (data: EmployeeFormData) => {
        try {
            if (editingEmployee) {
                await employeeService.updateEmployee(editingEmployee.employeeId!, data);
            } else {
                await employeeService.createEmployee(data);
            }

            loadEmployees();
            handleCancel();
        } catch (err: any) {
            setError(err.message || 'Failed to save employee');
        }
    };

    const handleEdit = (employee: Employee) => {
        setEditingEmployee(employee);
        setSelectedEmployeeType(employee.employeeType || '');
        reset({
            accountName: employee.accountName || '',
            email: employee.email || '',
            phoneNumber: employee.phoneNumber || '',
            citizenId: employee.citizenId || '',
            employeeType: employee.employeeType || 1
        });
        setShowForm(true);
    };

    const handleDelete = async (employeeId: number) => {
        if (!window.confirm('Are you sure you want to delete this employee?')) return;

        try {
            await employeeService.deleteEmployee(employeeId);
            loadEmployees();
        } catch (err: any) {
            setError(err.message || 'Failed to delete employee');
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingEmployee(null);
        setSelectedEmployeeType('');
        reset();
        setError('');
    };

    // Filter employees based on search term and type
    const filteredEmployees = employees.filter(employee => {
        const matchesSearch = (employee.accountName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                             employee.email?.toLowerCase().includes(searchTerm.toLowerCase()));
        const matchesType = !filterType || employee.employeeType.toString() === filterType;
        return matchesSearch && matchesType;
    });

    // Get unique employee types for filter
    const employeeTypes = [...new Set(employees.map(employee => employee.employeeType))];

    if (loading) {
        return <div className="loading">Loading employee data...</div>;
    }

    return (
        <div className="employee-management">
            <div className="management-header">
                <h2>Employee Management</h2>
                <button
                    className="btn btn-primary"
                    onClick={() => setShowForm(true)}
                >
                    Add New Employee
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            {/* Search and Filter Controls */}
            <div className="controls-section">
                <div className="search-controls">
                    <div className="search-group">
                        <label>Search Employee</label>
                        <input
                            type="text"
                            placeholder="Search by name or email..."
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
                            {employeeTypes.map(type => (
                                <option key={type} value={type}>{type}</option>
                            ))}
                        </select>
                    </div>

                    <div className="stats-group">
                        <div className="stat-item">
                            <span className="stat-label">Total Employees:</span>
                            <span className="stat-value">{employees.length}</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Add/Edit Form Modal */}
            {showForm && (
                <div className="form-modal">
                    <div className="form-container">
                        <h3>{editingEmployee ? 'Edit Employee' : 'Add New Employee'}</h3>

                        <form onSubmit={handleSubmit(onSubmit)}>
                            <div className="form-row">
                                <div className="form-group">
                                    <label>Account Name</label>
                                    <input
                                        type="text"
                                        {...register('accountName', {
                                            required: 'Account name is required'
                                        })}
                                        className={errors.accountName ? 'error' : ''}
                                        placeholder="e.g., John Doe"
                                    />
                                    {errors.accountName && (
                                        <span className="field-error">{errors.accountName.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Email</label>
                                    <input
                                        type="email"
                                        {...register('email', {
                                            required: 'Email is required',
                                            pattern: {
                                                value: /^\S+@\S+$/i,
                                                message: 'Invalid email format'
                                            }
                                        })}
                                        className={errors.email ? 'error' : ''}
                                        placeholder="e.g., john.doe@email.com"
                                    />
                                    {errors.email && (
                                        <span className="field-error">{errors.email.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Phone Number</label>
                                    <input
                                        type="text"
                                        {...register('phoneNumber', {
                                            required: 'Phone number is required',
                                            pattern: {
                                                value: /^[0-9-+]+$/,
                                                message: 'Invalid phone number format'
                                            }
                                        })}
                                        className={errors.phoneNumber ? 'error' : ''}
                                        placeholder="e.g., +1234567890"
                                    />
                                    {errors.phoneNumber && (
                                        <span className="field-error">{errors.phoneNumber.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Citizen ID</label>
                                    <input
                                        type="text"
                                        {...register('citizenId', {
                                            required: 'Citizen ID is required',
                                            pattern: {
                                                value: /^[0-9]+$/,
                                                message: 'Invalid Citizen ID format'
                                            }
                                        })}
                                        className={errors.citizenId ? 'error' : ''}
                                        placeholder="e.g., 123456789"
                                    />
                                    {errors.citizenId && (
                                        <span className="field-error">{errors.citizenId.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Employee Type</label>
                                    <TypeAhead
                                        options={employeeTypeOptions}
                                        value={selectedEmployeeType}
                                        onChange={(option) => {
                                            const employeeType = option?.value as number || '';
                                            setSelectedEmployeeType(employeeType);
                                            setValue('employeeType', Number(employeeType));
                                        }}
                                        placeholder="Select employee type..."
                                        error={!!errors.employeeType}
                                    />
                                    <input
                                        type="hidden"
                                        {...register('employeeType', {
                                            required: 'Employee type is required',
                                            valueAsNumber: true
                                        })}
                                    />
                                    {errors.employeeType && (
                                        <span className="field-error">{errors.employeeType.message}</span>
                                    )}
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingEmployee ? 'Update Employee' : 'Add Employee'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Employee Grid */}
            <div className="employees-grid">
                {filteredEmployees.map(employee => (
                    <div key={employee.employeeId} className="employee-card">
                        <div className="employee-header">
                            <div className="employee-name">{employee.accountName}</div>
                            <div className="employee-actions">
                                <button 
                                    className="btn btn-sm btn-secondary"
                                    onClick={() => handleEdit(employee)}
                                >
                                    Edit
                                </button>
                                <button 
                                    className="btn btn-sm btn-danger"
                                    onClick={() => handleDelete(employee.employeeId!)}
                                >
                                    Delete
                                </button>
                            </div>
                        </div>

                        <div className="employee-content">
                            <div className="employee-email">{employee.email}</div>
                            
                            <div className="employee-details">
                                <div className="detail-row">
                                    <span className="label">Phone:</span>
                                    <span className="value">{employee.phoneNumber}</span>
                                </div>
                                
                                <div className="detail-row">
                                    <span className="label">Citizen ID:</span>
                                    <span className="value">{employee.citizenId}</span>
                                </div>

                                <div className="detail-row">
                                    <span className="label">Employee Type:</span>
                                    <span className="value">
                                        {employee.employeeType === 1 && 'Flight Schedule Reception'}
                                        {employee.employeeType === 2 && 'Ticket Sales/Booking'}
                                        {employee.employeeType === 3 && 'Customer Service'}
                                        {employee.employeeType === 4 && 'Accounting'}
                                        {employee.employeeType === 5 && 'System Administrator'}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {filteredEmployees.length === 0 && (
                <div className="no-data">
                    {searchTerm || filterType ? (
                        <p>No employees found matching your search criteria.</p>
                    ) : (
                        <p>No employees in the system. Add your first employee to get started.</p>
                    )}
                </div>
            )}
        </div>
    );
};

export default EmployeeManagement;