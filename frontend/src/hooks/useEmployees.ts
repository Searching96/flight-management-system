// hooks/useEmployee.ts
import { useState } from 'react';
import { employeeService, authService } from '../services';
import { Employee, UpdateEmployeeRequest, RegisterRequest } from '../models';

export const useEmployees = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const fetchEmployees = async () => {
        try {
            setLoading(true);
            const data = await employeeService.getAllEmployees();
            setEmployees(data);
        } catch (err) {
            setError('Failed to load employees');
        } finally {
            setLoading(false);
        }
    };

    const addEmployee = async (formData: RegisterRequest) => {
        try {
            setLoading(true);
            await authService.register({
                ...formData,
                accountType: 2 // Employee account type
            });
            await fetchEmployees(); // Refresh employee list
        } catch (err) {
            setError('Failed to add employee');
        } finally {
            setLoading(false);
        }
    };

    const updateEmployee = async (id: number, data: UpdateEmployeeRequest) => {
        try {
            setLoading(true);
            const updated = await employeeService.updateEmployee(id, data);
            setEmployees(prev => prev.map(e => e.employeeId === id ? updated : e));
        } catch (err) {
            setError('Failed to update employee');
        } finally {
            setLoading(false);
        }
    };

    const deleteEmployee = async (id: number) => {
        try {
            setLoading(true);
            await employeeService.deleteEmployee(id);
            setEmployees(prev => prev.filter(e => e.employeeId !== id));
        } catch (err) {
            setError('Failed to delete employee');
        } finally {
            setLoading(false);
        }
    };

    return {
        employees,
        loading,
        error,
        fetchEmployees,
        addEmployee,
        updateEmployee,
        deleteEmployee
    };
};
