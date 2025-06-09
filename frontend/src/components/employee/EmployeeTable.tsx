// components/EmployeeTable.tsx
import { Employee } from '../../models';

interface EmployeeTableProps {
  employees: Employee[];
  onEdit: (employee: Employee) => void;
  onDelete: (id: number) => void;
}

export const EmployeeTable = ({ employees, onEdit, onDelete }: EmployeeTableProps) => (
  <div className="overflow-x-auto">
    <table className="table w-full">
      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Type</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {employees.map(employee => (
          <tr key={employee.employeeId}>
            <td>{employee.accountName}</td>
            <td>{employee.email}</td>
            <td>{employee.employeeTypeName}</td>
            <td>
              <button 
                onClick={() => onEdit(employee)}
                className="btn btn-sm btn-primary mr-2"
              >
                Edit
              </button>
              <button
                onClick={() => employee.employeeId && onDelete(employee.employeeId)}
                className="btn btn-sm btn-error"
              >
                Delete
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
