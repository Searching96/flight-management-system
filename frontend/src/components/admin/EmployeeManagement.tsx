// EmployeeManagement.tsx
import { useState } from 'react';
import { Employee, RegisterRequest } from '../../models';
import { useEmployees } from '../../hooks/useEmployees';
import { EmployeeForm} from '../employee/EmployeeForm';
import { EmployeeTable } from '../employee/EmployeeTable';

export const EmployeeManagement = () => {
  const { employees, loading, error, addEmployee, updateEmployee, deleteEmployee } = useEmployees();
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
  const [showForm, setShowForm] = useState(false);

  const handleSubmit = (data: any) => {
    if (selectedEmployee?.employeeId) {
      updateEmployee(selectedEmployee.employeeId, data);
    } else {
      addEmployee(data as RegisterRequest);
    }
    setShowForm(false);
  };

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Employee Management</h1>
        <button 
          onClick={() => {
            setSelectedEmployee(null);
            setShowForm(true);
          }}
          className="btn btn-primary"
        >
          Add Employee
        </button>
      </div>

      {showForm && (
        <div className="modal modal-open">
          <div className="modal-box">
            <EmployeeForm
              initialData={selectedEmployee || undefined}
              onSubmit={handleSubmit}
              isAdding={!selectedEmployee}
            />
          </div>
        </div>
      )}

      {error && <div className="alert alert-error mb-4">{error}</div>}
      
      {loading ? (
        <div className="loading">Loading...</div>
      ) : (
        <EmployeeTable
          employees={employees}
          onEdit={(employee) => {
            setSelectedEmployee(employee);
            setShowForm(true);
          }}
          onDelete={deleteEmployee}
        />
      )}
    </div>
  );
};

export default EmployeeManagement;
