import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal } from 'react-bootstrap';
import { employeeService } from '../../services';
import { Employee, CreateEmployeeRequest, UpdateEmployeeRequest } from '../../models';
import TypeAhead from '../common/TypeAhead';

interface EmployeeFormData {
    accountName: string;
    email: string;
    phoneNumber: string;
    citizenId: string;
    employeeType: number;
    password?: string;
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
    };    const onSubmit = async (data: EmployeeFormData) => {
        try {
            if (editingEmployee) {
                // For updates, we don't need citizenId or password
                const updateData: UpdateEmployeeRequest = {
                    accountName: data.accountName,
                    email: data.email,
                    phoneNumber: data.phoneNumber,
                    employeeType: data.employeeType
                };
                await employeeService.updateEmployee(editingEmployee.employeeId!, updateData);
            } else {
                // For creation, we need password and citizenId
                const createData: CreateEmployeeRequest = {
                    ...data,
                    password: data.password || 'defaultPassword123' // Temporary default password
                };
                await employeeService.createEmployee(createData);
            }
            loadEmployees();
            handleCancel();
        } catch (err: any) {
            setError(err.message || 'Failed to save employee');
        }
    };    const handleEdit = (employee: Employee) => {
        setEditingEmployee(employee);
        setSelectedEmployeeType(employee.employeeType || '');
        reset({
            accountName: employee.accountName || '',
            email: employee.email || '',
            phoneNumber: employee.phoneNumber || '',
            citizenId: '', // For the form structure only, not used in updates
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
    const employeeTypes = [...new Set(employees.map(employee => employee.employeeType))];    if (loading) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8} className="text-center">
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                        <p className="mt-3">Loading employee data...</p>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            <Card className="mb-4">
                <Card.Header>
                    <Row className="align-items-center">
                        <Col>
                            <Card.Title as="h2" className="mb-0">👥 Employee Management</Card.Title>
                        </Col>
                        <Col md="auto">
                            <Button
                                variant="primary"
                                onClick={() => setShowForm(true)}
                            >
                                Add New Employee
                            </Button>
                        </Col>
                    </Row>
                </Card.Header>
            </Card>

            {error && (
                <Alert variant="danger" className="mb-4">
                    {error}
                </Alert>
            )}

            {/* Search and Filter Controls */}
            <Card className="mb-4">
                <Card.Body>
                    <Row className="g-3 align-items-end">
                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>Search Employee</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="Search by name or email..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </Form.Group>
                        </Col>
                        
                        <Col md={3}>
                            <Form.Group>
                                <Form.Label>Filter by Type</Form.Label>
                                <Form.Select
                                    value={filterType}
                                    onChange={(e) => setFilterType(e.target.value)}
                                >
                                    <option value="">All Types</option>
                                    {employeeTypes.map(type => (
                                        <option key={type} value={type}>{type}</option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>

                        <Col md={3}>
                            <div className="d-flex align-items-center">
                                <Badge bg="info" className="fs-6 px-3 py-2">
                                    Total Employees: {employees.length}
                                </Badge>
                            </div>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>            {/* Add/Edit Form Modal */}
            <Modal show={showForm} onHide={handleCancel} size="lg" centered backdrop="static">
                <Modal.Header closeButton>
                    <Modal.Title>{editingEmployee ? 'Edit Employee' : 'Add New Employee'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleSubmit(onSubmit)}>
                        <Row className="mb-3">
                            <Col md={6}>
                                <Form.Group controlId="formAccountName">
                                    <Form.Label>Account Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('accountName', {
                                            required: 'Account name is required'
                                        })}
                                        isInvalid={!!errors.accountName}
                                        placeholder="e.g., John Doe"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.accountName?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group controlId="formEmail">
                                    <Form.Label>Email</Form.Label>
                                    <Form.Control
                                        type="email"
                                        {...register('email', {
                                            required: 'Email is required',
                                            pattern: {
                                                value: /^\S+@\S+$/i,
                                                message: 'Invalid email format'
                                            }
                                        })}
                                        isInvalid={!!errors.email}
                                        placeholder="e.g., john.doe@email.com"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.email?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row className="mb-3">
                            <Col md={6}>
                                <Form.Group controlId="formPhoneNumber">
                                    <Form.Label>Phone Number</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('phoneNumber', {
                                            required: 'Phone number is required',
                                            pattern: {
                                                value: /^[0-9-+]+$/,
                                                message: 'Invalid phone number format'
                                            }
                                        })}
                                        isInvalid={!!errors.phoneNumber}
                                        placeholder="e.g., +1234567890"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.phoneNumber?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group controlId="formCitizenId">
                                    <Form.Label>Citizen ID</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('citizenId', {
                                            required: !editingEmployee ? 'Citizen ID is required' : false,
                                            pattern: {
                                                value: /^[0-9]+$/,
                                                message: 'Invalid Citizen ID format'
                                            }
                                        })}
                                        isInvalid={!!errors.citizenId}
                                        placeholder="e.g., 123456789"
                                        disabled={!!editingEmployee}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.citizenId?.message}
                                    </Form.Control.Feedback>
                                    {editingEmployee && (
                                        <Form.Text muted>
                                            Citizen ID cannot be updated after creation
                                        </Form.Text>
                                    )}
                                </Form.Group>
                            </Col>
                        </Row>

                        <Form.Group className="mb-3" controlId="formEmployeeType">
                            <Form.Label>Employee Type</Form.Label>
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
                                <div className="invalid-feedback d-block">
                                    {errors.employeeType.message}
                                </div>
                            )}
                        </Form.Group>

                        {!editingEmployee && (
                            <Form.Group className="mb-3">
                                <Form.Text className="text-muted">
                                    A default password will be generated for the new employee. They will need to change it after first login.
                                </Form.Text>
                            </Form.Group>
                        )}
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCancel}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={handleSubmit(onSubmit)}>
                        {editingEmployee ? 'Update Employee' : 'Add Employee'}
                    </Button>
                </Modal.Footer>
            </Modal>            {/* Employee Grid */}
            <Row className="g-4">
                {filteredEmployees.map(employee => (
                    <Col key={employee.employeeId} lg={4} md={6} sm={12}>
                        <Card className="h-100 shadow-sm">
                            <Card.Header className="d-flex justify-content-between align-items-center bg-light">
                                <div className="fw-bold">{employee.accountName}</div>
                                <div>
                                    <Button 
                                        variant="outline-secondary" 
                                        size="sm" 
                                        className="me-2"
                                        onClick={() => handleEdit(employee)}
                                    >
                                        Edit
                                    </Button>
                                    <Button 
                                        variant="outline-danger" 
                                        size="sm"
                                        onClick={() => handleDelete(employee.employeeId!)}
                                    >
                                        Delete
                                    </Button>
                                </div>
                            </Card.Header>

                            <Card.Body>
                                <Card.Subtitle className="mb-3 text-muted">
                                    {employee.email}
                                </Card.Subtitle>
                                
                                <Card.Text as="div">
                                    <Row className="mb-2">
                                        <Col xs={4} className="text-muted">Phone:</Col>
                                        <Col>{employee.phoneNumber}</Col>
                                    </Row>

                                    <Row className="mb-2">
                                        <Col xs={4} className="text-muted">Type:</Col>
                                        <Col>
                                            {employee.employeeType === 1 && 'Flight Schedule Reception'}
                                            {employee.employeeType === 2 && 'Ticket Sales/Booking'}
                                            {employee.employeeType === 3 && 'Customer Service'}
                                            {employee.employeeType === 4 && 'Accounting'}
                                            {employee.employeeType === 5 && 'System Administrator'}
                                        </Col>
                                    </Row>
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>

            {filteredEmployees.length === 0 && (
                <Alert variant="info" className="text-center my-4">
                    {searchTerm || filterType ? (
                        <p className="mb-0">No employees found matching your search criteria.</p>
                    ) : (
                        <p className="mb-0">No employees in the system. Add your first employee to get started.</p>
                    )}
                </Alert>
            )}
        </Container>
    );
};

export default EmployeeManagement;