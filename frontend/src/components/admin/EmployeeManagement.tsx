import React, { useState, useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal } from 'react-bootstrap';
import { authService, employeeService } from '../../services';
import { usePermissions } from '../../hooks/useAuth';
import { Employee, RegisterRequest, UpdateEmployeeRequest } from '../../models';
import * as XLSX from 'xlsx';

interface EmployeeFormData {
  accountName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
  employeeType: number;
}

const EmployeeManagement: React.FC<{
  showAddModal?: boolean;
  onCloseAddModal?: () => void;
}> = ({ showAddModal = false, onCloseAddModal }) => {
  const { canViewAdmin } = usePermissions();

  if (!canViewAdmin) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Từ chối truy cập</Alert.Heading>
              <p>Bạn không có quyền truy cập quản lý nhân viên.</p>
              <p className="text-muted small">
                Cập nhật lúc: 2025-06-11 06:58:25 UTC bởi thinh0704hcm
              </p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [toggleLoading, setToggleLoading] = useState<Set<number>>(new Set());
  const [showToggleModal, setShowToggleModal] = useState(false);
  const [showResetPasswordModal, setShowResetPasswordModal] = useState(false);
  const [employeeToToggle, setEmployeeToToggle] = useState<Employee | null>(null);
  const [employeeToResetPassword, setEmployeeToResetPassword] = useState<Employee | null>(null);
  const [showImportModal, setShowImportModal] = useState(false);
  const [importFile, setImportFile] = useState<File | null>(null);
  const [importData, setImportData] = useState<EmployeeFormData[]>([]);
  const [importErrors, setImportErrors] = useState<string[]>([]);
  const [importing, setImporting] = useState(false);
  const [showImportResultModal, setShowImportResultModal] = useState(false);
  const [importResults, setImportResults] = useState<{
    successCount: number;
    errorCount: number;
    results: string[];
  } | null>(null);
  const [showPasswordResetConfirmModal, setShowPasswordResetConfirmModal] = useState(false);
  const [passwordResetEmployee, setPasswordResetEmployee] = useState<Employee | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm<EmployeeFormData>();

  // Employee type options
const employeeTypeOptions = [
  { value: 1, label: "Lập lịch chuyến bay", color: "warning", description: "Quản lý lịch trình bay" },
  { value: 2, label: "Bán vé", color: "success", description: "Hỗ trợ khách hàng đặt vé" },
  { value: 3, label: "Hỗ trợ khách hàng", color: "info", description: "Giải quyết thắc mắc khách hàng" },
  { value: 4, label: "Kế toán", color: "secondary", description: "Quản lý tài chính và kế toán" },
  { value: 5, label: "Vận hành chuyến bay", color: "primary", description: "Quản lý vận hành máy bay" },
  { value: 6, label: "Nhân sự", color: "dark", description: "Quản lý nhân lực và tuyển dụng" },
  { value: 7, label: "Quản trị viên", color: "danger", description: "Toàn quyền hệ thống" }
];

  useEffect(() => {
    loadEmployees();
  }, []);

  // Effect to handle external modal trigger
  useEffect(() => {
    if (showAddModal) {
      setShowForm(true);
    }
  }, [showAddModal]);

  const loadEmployees = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('Loading employees at 2025-06-11 06:58:25 UTC by thinh0704hcm');
      const response = await employeeService.getAllEmployees();
      setEmployees(response);
    } catch (err: any) {
      console.error('Load employees error:', err);
      setError('Không thể tải danh sách nhân viên: ' + (err.message || 'Lỗi không xác định'));
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: EmployeeFormData) => {
    try {
      setSubmitting(true);
      setError('');

      if (editingEmployee) {
        // Update existing employee
        const updateData: UpdateEmployeeRequest = {
          accountName: data.accountName,
          email: data.email,
          phoneNumber: data.phoneNumber,
          employeeType: data.employeeType
        };

        console.log('Updating employee:', editingEmployee.employeeId, 'at 2025-06-11 06:58:25 UTC by thinh0704hcm');
        await employeeService.updateEmployee(editingEmployee.employeeId!, updateData);
      } else {
        // Create new employee - password will be generated in backend
        const registerData: RegisterRequest = {
          accountName: data.accountName,
          password: 'temp', // Temporary password, backend will generate actual password
          email: data.email,
          citizenId: data.citizenId,
          phoneNumber: data.phoneNumber,
          accountType: 2, // Employee account type
          employeeType: data.employeeType
        };

        console.log('Creating new employee at 2025-06-11 06:58:25 UTC by thinh0704hcm');
        await authService.createEmployee(registerData);
      }

      await loadEmployees();
      handleCancel();
    } catch (err: any) {
      console.error('Save employee error:', err);
      setError(err.message || 'Không thể lưu thông tin nhân viên');
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (employee: Employee) => {
    setEditingEmployee(employee);
    reset({
      accountName: employee.accountName || '',
      email: employee.email || '',
      citizenId: employee.citizenId || '',
      phoneNumber: employee.phoneNumber || '',
      employeeType: employee.employeeType
    });
    setShowForm(true);
  };

  const handleToggleEmployeeClick = (employee: Employee) => {
    setEmployeeToToggle(employee);
    setShowToggleModal(true);
  };

  const handleConfirmToggle = async () => {
    if (!employeeToToggle) return;
    
    const employeeId = getEmployeeId(employeeToToggle);
    const isActive = employeeToToggle.deletedAt === null;

    try {
      setError('');
      setToggleLoading(prev => new Set(prev).add(employeeId));
      
      if (isActive) {
        console.log('Deactivating employee:', employeeId, 'at 2025-06-11 06:58:25 UTC by thinh0704hcm');
        await employeeService.deactivateEmployee(employeeId);
      } else {
        console.log('Activating employee:', employeeId, 'at 2025-06-11 06:58:25 UTC by thinh0704hcm');
        await employeeService.activateEmployee(employeeId);
      }
      
      await loadEmployees();
      setShowToggleModal(false);
      setEmployeeToToggle(null);
    } catch (err: any) {
      console.error('Toggle employee status error:', err);
      setError(err.message || (isActive ? 'Không thể vô hiệu hóa nhân viên' : 'Không thể kích hoạt nhân viên'));
    } finally {
      setToggleLoading(prev => {
        const newSet = new Set(prev);
        newSet.delete(employeeId);
        return newSet;
      });
    }
  };

  const handleResetPasswordClick = (employee: Employee) => {
    setEmployeeToResetPassword(employee);
    setShowResetPasswordModal(true);
  };

  const handleConfirmResetPassword = async () => {
    if (!employeeToResetPassword) return;

    try {
      setError('');
      const employeeId = getEmployeeId(employeeToResetPassword);
      console.log('Resetting password for employee:', employeeId, 'at 2025-06-11 06:58:25 UTC by thinh0704hcm');
      await authService.forgetPassword((await employeeService.getEmployeeById(employeeId)).email!);
      
      // Show success modal instead of alert
      setPasswordResetEmployee(employeeToResetPassword);
      setShowResetPasswordModal(false);
      setEmployeeToResetPassword(null);
      setShowPasswordResetConfirmModal(true);
    } catch (err: any) {
      console.error('Reset password error:', err);
      setError(err.message || 'Không thể đặt lại mật khẩu');
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingEmployee(null);
    reset();
    setError('');

    // Call the external close handler if provided
    if (onCloseAddModal) {
      onCloseAddModal();
    }
  };

  // Filter employees based on search term and type
  const filteredEmployees = employees.filter(employee => {
    const matchesSearch = (employee.accountName?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (employee.email?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (employee.citizenId || '').includes(searchTerm) ||
      (employee.phoneNumber || '').includes(searchTerm);
    const matchesType = !filterType || employee.employeeType.toString() === filterType;
    return matchesSearch && matchesType;
  });

  // Get employee type info
  const getEmployeeTypeInfo = (employeeType: number) => {
    return employeeTypeOptions.find(option => option.value === employeeType) ||
      { label: "Không xác định", color: "secondary", description: "" };
  };

  // Check if employee has ID property (for compatibility)
  const getEmployeeId = (employee: Employee): number => {
    return employee.employeeId || (employee as any).id || 0;
  };

  const exportToExcel = () => {
    try {
      // Prepare data for export
      const exportData = employees.map(employee => ({
        accountName: employee.accountName || '',
        email: employee.email || '',
        citizenId: employee.citizenId || '',
        phoneNumber: employee.phoneNumber || '',
        employeeType: employee.employeeType,
        employeeTypeName: getEmployeeTypeInfo(employee.employeeType).label,
        status: employee.deletedAt === null ? 'Hoạt động' : 'Tạm dừng',
        id: getEmployeeId(employee)
      }));

      // Create workbook and worksheet
      const wb = XLSX.utils.book_new();
      const ws = XLSX.utils.json_to_sheet(exportData);

      // Set column widths
      const colWidths = [
        { wch: 20 }, // accountName
        { wch: 30 }, // email
        { wch: 15 }, // citizenId
        { wch: 15 }, // phoneNumber
        { wch: 10 }, // employeeType
        { wch: 25 }, // employeeTypeName
        { wch: 12 }, // status
        { wch: 8 }   // id
      ];
      ws['!cols'] = colWidths;

      // Add worksheet to workbook
      XLSX.utils.book_append_sheet(wb, ws, 'Employees');

      // Generate filename with current date
      const date = new Date().toISOString().split('T')[0];
      const filename = `employees_${date}.xlsx`;

      // Save file
      XLSX.writeFile(wb, filename);
    } catch (error) {
      console.error('Export error:', error);
      setError('Không thể xuất file Excel. Vui lòng thử lại.');
    }
  };

  const downloadTemplate = () => {
    try {
      // Create template data with sample row - no password field
      const templateData = [
        {
          accountName: 'Nguyễn Văn A',
          email: 'nguyen.van.a@company.com',
          citizenId: '123456789012',
          phoneNumber: '0901234567',
          employeeType: 2,
          employeeTypeName: 'Bán vé (chỉ tham khảo - sẽ tự động tính)'
        }
      ];

      const wb = XLSX.utils.book_new();
      const ws = XLSX.utils.json_to_sheet(templateData);

      // Set column widths
      const colWidths = [
        { wch: 20 }, // accountName
        { wch: 30 }, // email
        { wch: 15 }, // citizenId
        { wch: 15 }, // phoneNumber
        { wch: 15 }, // employeeType
        { wch: 35 }  // employeeTypeName
      ];
      ws['!cols'] = colWidths;

      XLSX.utils.book_append_sheet(wb, ws, 'Template');
      XLSX.writeFile(wb, 'employee_import_template.xlsx');
    } catch (error) {
      console.error('Template download error:', error);
      setError('Không thể tải template. Vui lòng thử lại.');
    }
  };

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setImportFile(file);
      setImportData([]);
      setImportErrors([]);
    }
  };

  const parseExcelFile = () => {
    if (!importFile) return;

    try {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const data = new Uint8Array(e.target?.result as ArrayBuffer);
          const workbook = XLSX.read(data, { type: 'array' });
          const sheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[sheetName];
          const jsonData = XLSX.utils.sheet_to_json(worksheet);

          const errors: string[] = [];
          const validData: EmployeeFormData[] = [];

          jsonData.forEach((row: any, index) => {
            const rowNumber = index + 2; // Excel row number (1-indexed + header)
            const employeeData: Partial<EmployeeFormData> = {};

            // Validate and parse each field
            if (!row.accountName || typeof row.accountName !== 'string') {
              errors.push(`Dòng ${rowNumber}: Họ và tên không hợp lệ`);
            } else if (!/^[\p{L}\p{M}\s'.-]+$/u.test(row.accountName)) {
              errors.push(`Dòng ${rowNumber}: Họ và tên chỉ được chứa chữ cái, khoảng trắng, dấu nháy đơn, gạch ngang và dấu chấm`);
            } else {
              employeeData.accountName = row.accountName.trim();
            }

            if (!row.email || typeof row.email !== 'string' || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(row.email)) {
              errors.push(`Dòng ${rowNumber}: Email không hợp lệ`);
            } else {
              employeeData.email = row.email.trim();
            }

            if (!row.citizenId || !/^[0-9]{9,12}$/.test(row.citizenId.toString())) {
              errors.push(`Dòng ${rowNumber}: CCCD/CMND phải có 9-12 chữ số`);
            } else {
              employeeData.citizenId = row.citizenId.toString();
            }

            if (!row.phoneNumber || !/^[0-9]{10,11}$/.test(row.phoneNumber.toString())) {
              errors.push(`Dòng ${rowNumber}: Số điện thoại phải có 10-11 chữ số`);
            } else {
              employeeData.phoneNumber = row.phoneNumber.toString();
            }

            const employeeType = parseInt(row.employeeType);
            if (!employeeType || !employeeTypeOptions.find(opt => opt.value === employeeType)) {
              errors.push(`Dòng ${rowNumber}: Loại nhân viên không hợp lệ (1-7)`);
            } else {
              employeeData.employeeType = employeeType;
            }

            // Only add to valid data if all required fields are present (no password needed)
            if (employeeData.accountName && employeeData.email && 
                employeeData.citizenId && employeeData.phoneNumber && employeeData.employeeType) {
              validData.push(employeeData as EmployeeFormData);
            }
          });

          setImportData(validData);
          setImportErrors(errors);
        } catch (error) {
          console.error('Parse error:', error);
          setImportErrors(['Không thể đọc file Excel. Vui lòng kiểm tra định dạng file.']);
        }
      };
      reader.readAsArrayBuffer(importFile);
    } catch (error) {
      console.error('File read error:', error);
      setImportErrors(['Không thể đọc file. Vui lòng thử lại.']);
    }
  };

  const handleImport = async () => {
    if (importData.length === 0) return;

    setImporting(true);
    try {
      let successCount = 0;
      let errorCount = 0;
      const importResults: string[] = [];

      for (const employeeData of importData) {
        try {
          const registerData: RegisterRequest = {
            accountName: employeeData.accountName,
            password: 'temp', // Temporary password, backend will generate actual password
            email: employeeData.email,
            citizenId: employeeData.citizenId,
            phoneNumber: employeeData.phoneNumber,
            accountType: 2, // Employee account type
            employeeType: employeeData.employeeType
          };

          await authService.createEmployee(registerData);
          successCount++;
          importResults.push(`✓ ${employeeData.accountName} - ${employeeData.email}`);
        } catch (error: any) {
          errorCount++;
          importResults.push(`✗ ${employeeData.accountName} - ${error.message || 'Lỗi không xác định'}`);
        }
      }

      await loadEmployees();
      setShowImportModal(false);
      setImportFile(null);
      setImportData([]);
      setImportErrors([]);

      // Show results modal instead of alert
      setImportResults({
        successCount,
        errorCount,
        results: importResults
      });
      setShowImportResultModal(true);
    } catch (error) {
      console.error('Import error:', error);
      setError('Có lỗi xảy ra trong quá trình import. Vui lòng thử lại.');
    } finally {
      setImporting(false);
    }
  };

  // Check permissions
  if (!canViewAdmin) {
    return null;
  }

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Đang tải...</span>
        </Spinner>
        <p className="mt-3">Đang tải danh sách nhân viên...</p>
        <p className="text-muted small">
          Cập nhật lúc: 2025-06-11 06:58:25 UTC bởi thinh0704hcm
        </p>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4">
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Header className="d-flex justify-content-between align-items-center">
              <Card.Title className="mb-0">👥 Quản lý nhân viên</Card.Title>
              <div className="d-flex gap-2">
                <Button
                  variant="outline-success"
                  onClick={exportToExcel}
                  disabled={submitting}
                >
                  <i className="bi bi-file-earmark-excel me-2"></i>
                  Xuất Excel
                </Button>
                <Button
                  variant="outline-primary"
                  onClick={() => setShowImportModal(true)}
                  disabled={submitting}
                >
                  <i className="bi bi-upload me-2"></i>
                  Import Excel
                </Button>
                <Button
                  variant="primary"
                  onClick={() => setShowForm(true)}
                  disabled={submitting}
                >
                  <i className="bi bi-person-plus me-2"></i>
                  Thêm nhân viên mới
                </Button>
              </div>
            </Card.Header>
          </Card>
        </Col>
      </Row>

      {error && (
        <Row className="mb-4">
          <Col>
            <Alert variant="danger" className="text-center" dismissible onClose={() => setError('')}>
              <i className="bi bi-exclamation-triangle me-2"></i>
              {error}
            </Alert>
          </Col>
        </Row>
      )}

      {/* Search and Filter Controls */}
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Header>
              <Card.Title className="mb-0">
                <i className="bi bi-search me-2"></i>
                Tìm kiếm & Lọc
              </Card.Title>
            </Card.Header>
            <Card.Body>
              <Row className="align-items-end">
                <Col md={4}>
                  <Form.Group>
                    <Form.Label>Tìm kiếm nhân viên</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Tìm theo tên, email, CCCD, SĐT..."
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                    />
                  </Form.Group>
                </Col>
                <Col md={3}>
                  <Form.Group>
                    <Form.Label>Lọc theo loại nhân viên</Form.Label>
                    <Form.Select
                      value={filterType}
                      onChange={(e) => setFilterType(e.target.value)}
                    >
                      <option value="">Tất cả loại</option>
                      {employeeTypeOptions.map(type => (
                        <option key={type.value} value={type.value.toString()}>
                          {type.label}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={5}>
                  <Row className="text-center">
                    <Col>
                      <Badge bg="primary" className="p-2">
                        <i className="bi bi-people me-1"></i>
                        Tổng nhân viên: <strong>{employees.length}</strong>
                      </Badge>
                    </Col>
                    <Col>
                      <Badge bg="success" className="p-2">
                        <i className="bi bi-person-check me-1"></i>
                        Đang hiển thị: <strong>{filteredEmployees.length}</strong>
                      </Badge>
                    </Col>
                  </Row>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Add/Edit Employee Modal */}
      <Modal show={showForm} onHide={handleCancel} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            <i className={`bi ${editingEmployee ? 'bi-person-gear' : 'bi-person-plus'} me-2`}></i>
            {editingEmployee ? 'Chỉnh sửa nhân viên' : 'Thêm nhân viên mới'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form id="employee-form" onSubmit={handleSubmit(onSubmit)}>
            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>
                    <i className="bi bi-person me-1"></i>
                    Họ và tên
                  </Form.Label>
                  <Form.Control
                    type="text"
                    {...register('accountName', {
                      required: 'Họ và tên là bắt buộc',
                      minLength: {
                        value: 2,
                        message: 'Họ và tên phải có ít nhất 2 ký tự'
                      },
                      pattern: {
                        value: /^[\p{L}\p{M}\s'.-]+$/u,
                        message: 'Họ và tên chỉ được chứa chữ cái, khoảng trắng, dấu nháy đơn, gạch ngang và dấu chấm'
                      }
                    })}
                    isInvalid={!!errors.accountName}
                    placeholder="ví dụ: Nguyễn Văn A"
                    disabled={submitting}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.accountName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col md={6}>
                <div className="p-3 bg-light rounded">
                  <div className="d-flex align-items-center">
                    <i className="bi bi-info-circle text-info me-2"></i>
                    <div>
                      <small className="text-muted">
                        <strong>Mật khẩu tự động:</strong><br />
                        Mật khẩu sẽ được tự động tạo và gửi qua email
                      </small>
                    </div>
                  </div>
                </div>
              </Col>
            </Row>

            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>
                    <i className="bi bi-envelope me-1"></i>
                    Email
                  </Form.Label>
                  <Form.Control
                    type="email"
                    {...register('email', {
                      required: 'Email là bắt buộc',
                      pattern: {
                        value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                        message: 'Email không hợp lệ'
                      }
                    })}
                    isInvalid={!!errors.email}
                    placeholder="ví dụ: nguyen.van.a@thinhuit.id.vn"
                    disabled={submitting}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.email?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>
                    <i className="bi bi-telephone me-1"></i>
                    Số điện thoại
                  </Form.Label>
                  <Form.Control
                    type="tel"
                    {...register('phoneNumber', {
                      required: 'Số điện thoại là bắt buộc',
                      pattern: {
                        value: /^[0-9]{10,11}$/,
                        message: 'Số điện thoại phải có 10-11 chữ số'
                      }
                    })}
                    isInvalid={!!errors.phoneNumber}
                    placeholder="ví dụ: 0901234567"
                    disabled={submitting}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.phoneNumber?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
            </Row>

            {!editingEmployee && (
              <Row className="mb-3">
                <Col md={6}>
                  <Form.Group>
                    <Form.Label>
                      <i className="bi bi-card-text me-1"></i>
                      Số CCCD/CMND
                    </Form.Label>
                    <Form.Control
                      type="text"
                      {...register('citizenId', {
                        required: 'Số CCCD/CMND là bắt buộc',
                        pattern: {
                          value: /^[0-9]{9,12}$/,
                          message: 'CCCD/CMND phải có 9-12 chữ số'
                        }
                      })}
                      isInvalid={!!errors.citizenId}
                      placeholder="ví dụ: 123456789012"
                      disabled={submitting}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.citizenId?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group>
                    <Form.Label>
                      <i className="bi bi-briefcase me-1"></i>
                      Loại nhân viên
                    </Form.Label>
                    <Form.Select
                      {...register('employeeType', {
                        required: 'Loại nhân viên là bắt buộc',
                        valueAsNumber: true
                      })}
                      isInvalid={!!errors.employeeType}
                      disabled={submitting}
                    >
                      <option value="">Chọn loại nhân viên...</option>
                      {employeeTypeOptions.map(type => (
                        <option key={type.value} value={type.value}>
                          {type.label} - {type.description}
                        </option>
                      ))}
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">
                      {errors.employeeType?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </Col>
              </Row>
            )}

            {editingEmployee && (
              <Row className="mb-3">
                <Col md={12}>
                  <Form.Group>
                    <Form.Label>
                      <i className="bi bi-briefcase me-1"></i>
                      Loại nhân viên
                    </Form.Label>
                    <Form.Select
                      {...register('employeeType', {
                        required: 'Loại nhân viên là bắt buộc',
                        valueAsNumber: true
                      })}
                      isInvalid={!!errors.employeeType}
                      disabled={submitting}
                    >
                      <option value="">Chọn loại nhân viên...</option>
                      {employeeTypeOptions.map(type => (
                        <option key={type.value} value={type.value}>
                          {type.label} - {type.description}
                        </option>
                      ))}
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">
                      {errors.employeeType?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </Col>
              </Row>
            )}

            {/* Password information alert for new employees */}
            {!editingEmployee && (
              <Alert variant="info" className="mb-3">
                <Alert.Heading className="h6">
                  <i className="bi bi-key me-2"></i>
                  Thông tin mật khẩu
                </Alert.Heading>
                <p className="mb-0">
                  Mật khẩu tạm thời sẽ được tự động tạo bởi hệ thống và gửi đến email của nhân viên. 
                  Nhân viên có thể đổi mật khẩu sau khi đăng nhập lần đầu.
                </p>
              </Alert>
            )}
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCancel} disabled={submitting}>
            <i className="bi bi-x-circle me-2"></i>
            Hủy
          </Button>
          <Button variant="primary" onClick={handleSubmit(onSubmit)} disabled={submitting}>
            {submitting ? (
              <>
                <Spinner animation="border" size="sm" className="me-2" />
                Đang xử lý...
              </>
            ) : (
              <>
                <i className={`bi ${editingEmployee ? 'bi-check-circle' : 'bi-plus-circle'} me-2`}></i>
                {editingEmployee ? 'Cập nhật nhân viên' : 'Thêm nhân viên'}
              </>
            )}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Employee Status Toggle Modal */}
      <Modal show={showToggleModal} onHide={() => setShowToggleModal(false)} centered>
        <Modal.Header closeButton className="bg-warning text-dark">
          <Modal.Title>
            <i className="bi bi-toggle-on me-2"></i>
            Thay đổi trạng thái nhân viên
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          <div className="text-center mb-3">
            <i className="bi bi-person-gear text-warning" style={{ fontSize: '3rem' }}></i>
          </div>
          {employeeToToggle && (
            <>
              <h5 className="text-center mb-3">
                {employeeToToggle.deletedAt === null 
                  ? `Vô hiệu hóa nhân viên ${employeeToToggle.accountName}?`
                  : `Kích hoạt nhân viên ${employeeToToggle.accountName}?`
                }
              </h5>
              <div className="p-3 bg-light rounded mb-3">
                <div className="text-center">
                  <strong>{employeeToToggle.accountName}</strong><br />
                  <span className="text-muted">{employeeToToggle.email}</span>
                </div>
              </div>
              <p className="text-center text-muted mb-0">
                {employeeToToggle.deletedAt === null 
                  ? 'Nhân viên sẽ không thể đăng nhập vào hệ thống sau khi bị vô hiệu hóa.'
                  : 'Nhân viên sẽ có thể đăng nhập trở lại sau khi được kích hoạt.'
                }
              </p>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowToggleModal(false)}
          >
            Hủy
          </Button>
          <Button
            variant={employeeToToggle?.deletedAt === null ? "danger" : "success"}
            onClick={handleConfirmToggle}
          >
            <i className={`bi ${employeeToToggle?.deletedAt === null ? 'bi-pause-circle' : 'bi-play-circle'} me-2`}></i>
            {employeeToToggle?.deletedAt === null ? 'Vô hiệu hóa' : 'Kích hoạt'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Reset Password Modal */}
      <Modal show={showResetPasswordModal} onHide={() => setShowResetPasswordModal(false)} centered>
        <Modal.Header closeButton className="bg-info text-white">
          <Modal.Title>
            <i className="bi bi-key me-2"></i>
            Đặt lại mật khẩu
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          <div className="text-center mb-3">
            <i className="bi bi-shield-lock text-info" style={{ fontSize: '3rem' }}></i>
          </div>
          {employeeToResetPassword && (
            <>
              <h5 className="text-center mb-3">
                Đặt lại mật khẩu cho {employeeToResetPassword.accountName}?
              </h5>
              <div className="p-3 bg-light rounded mb-3">
                <div className="text-center">
                  <strong>{employeeToResetPassword.accountName}</strong><br />
                  <span className="text-muted">{employeeToResetPassword.email}</span>
                </div>
              </div>
              <p className="text-center text-muted mb-0">
                Mật khẩu tạm thời mới sẽ được gửi đến email của nhân viên.
              </p>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowResetPasswordModal(false)}
          >
            Hủy
          </Button>
          <Button
            variant="info"
            onClick={handleConfirmResetPassword}
          >
            <i className="bi bi-envelope me-2"></i>
            Gửi mật khẩu mới
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Excel Import Modal */}
      <Modal show={showImportModal} onHide={() => setShowImportModal(false)} size="xl">
        <Modal.Header closeButton>
          <Modal.Title>
            <i className="bi bi-upload me-2"></i>
            Import nhân viên từ Excel
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {/* Step 1: Download Template */}
          <Card className="mb-4">
            <Card.Header>
              <h6 className="mb-0">
                <i className="bi bi-download me-2"></i>
                Bước 1: Tải template Excel
              </h6>
            </Card.Header>
            <Card.Body>
              <p className="text-muted mb-3">
                Tải file template để đảm bảo định dạng dữ liệu chính xác.
              </p>
              <Button variant="outline-info" onClick={downloadTemplate}>
                <i className="bi bi-file-earmark-excel me-2"></i>
                Tải template
              </Button>
            </Card.Body>
          </Card>

          {/* Step 2: Upload File */}
          <Card className="mb-4">
            <Card.Header>
              <h6 className="mb-0">
                <i className="bi bi-cloud-upload me-2"></i>
                Bước 2: Chọn file Excel
              </h6>
            </Card.Header>
            <Card.Body>
              <Form.Group>
                <Form.Control
                  ref={fileInputRef}
                  type="file"
                  accept=".xlsx,.xls"
                  onChange={handleFileSelect}
                  className="mb-3"
                />
              </Form.Group>
              {importFile && (
                <div className="d-flex align-items-center justify-content-between">
                  <span className="text-muted">
                    <i className="bi bi-file-earmark-excel me-2"></i>
                    {importFile.name}
                  </span>
                  <Button variant="primary" onClick={parseExcelFile}>
                    <i className="bi bi-search me-2"></i>
                    Phân tích file
                  </Button>
                </div>
              )}
            </Card.Body>
          </Card>

          {/* Step 3: Review Data */}
          {(importData.length > 0 || importErrors.length > 0) && (
            <Card className="mb-4">
              <Card.Header>
                <h6 className="mb-0">
                  <i className="bi bi-eye me-2"></i>
                  Bước 3: Xem trước dữ liệu
                </h6>
              </Card.Header>
              <Card.Body>
                {importErrors.length > 0 && (
                  <Alert variant="danger" className="mb-3">
                    <Alert.Heading className="h6">Lỗi trong dữ liệu:</Alert.Heading>
                    <ul className="mb-0">
                      {importErrors.map((error, index) => (
                        <li key={index}>{error}</li>
                      ))}
                    </ul>
                  </Alert>
                )}

                {importData.length > 0 && (
                  <>
                    <Alert variant="success" className="mb-3">
                      <i className="bi bi-check-circle me-2"></i>
                      Tìm thấy {importData.length} nhân viên hợp lệ để import
                    </Alert>
                    
                    <div className="table-responsive" style={{ maxHeight: '300px', overflowY: 'auto' }}>
                      <table className="table table-sm table-striped">
                        <thead className="table-dark sticky-top">
                          <tr>
                            <th>Tên tài khoản</th>
                            <th>Email</th>
                            <th>CCCD</th>
                            <th>SĐT</th>
                            <th>Loại NV</th>
                          </tr>
                        </thead>
                        <tbody>
                          {importData.map((emp, index) => (
                            <tr key={index}>
                              <td>{emp.accountName}</td>
                              <td>{emp.email}</td>
                              <td>{emp.citizenId}</td>
                              <td>{emp.phoneNumber}</td>
                              <td>
                                <Badge bg={getEmployeeTypeInfo(emp.employeeType).color}>
                                  {getEmployeeTypeInfo(emp.employeeType).label}
                                </Badge>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </>
                )}
              </Card.Body>
            </Card>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowImportModal(false)}
            disabled={importing}
          >
            Hủy
          </Button>
          <Button
            variant="success"
            onClick={handleImport}
            disabled={importData.length === 0 || importing}
          >
            {importing ? (
              <>
                <Spinner animation="border" size="sm" className="me-2" />
                Đang import...
              </>
            ) : (
              <>
                <i className="bi bi-cloud-upload me-2"></i>
                Import {importData.length} nhân viên
              </>
            )}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Password Reset Success Modal */}
      <Modal show={showPasswordResetConfirmModal} onHide={() => setShowPasswordResetConfirmModal(false)} centered>
        <Modal.Header closeButton className="bg-success text-white">
          <Modal.Title>
            <i className="bi bi-check-circle me-2"></i>
            Đặt lại mật khẩu thành công
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          <div className="text-center mb-3">
            <i className="bi bi-envelope-check text-success" style={{ fontSize: '3rem' }}></i>
          </div>
          <h5 className="text-center mb-3">Mật khẩu đã được đặt lại thành công!</h5>
          {passwordResetEmployee && (
            <div className="p-3 bg-light rounded mb-3">
              <div className="text-center">
                <strong>{passwordResetEmployee.accountName}</strong><br />
                <span className="text-muted">{passwordResetEmployee.email}</span>
              </div>
            </div>
          )}
          <Alert variant="success" className="mb-0">
            <Alert.Heading className="h6">
              <i className="bi bi-info-circle me-2"></i>
              Thông báo quan trọng
            </Alert.Heading>
            <p className="mb-0">
              Mật khẩu tạm thời mới đã được gửi đến email của nhân viên. 
              Vui lòng thông báo cho nhân viên kiểm tra email và đăng nhập bằng mật khẩu mới.
            </p>
          </Alert>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="success"
            onClick={() => setShowPasswordResetConfirmModal(false)}
          >
            <i className="bi bi-check me-2"></i>
            Đã hiểu
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Import Results Modal */}
      <Modal show={showImportResultModal} onHide={() => setShowImportResultModal(false)} size="lg" centered>
        <Modal.Header closeButton className="bg-primary text-white">
          <Modal.Title>
            <i className="bi bi-file-earmark-check me-2"></i>
            Kết quả Import
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          {importResults && (
            <>
              <div className="text-center mb-4">
                <div className="row g-3">
                  <div className="col-6">
                    <div className="p-3 bg-success text-white rounded">
                      <div className="h3 mb-1">{importResults.successCount}</div>
                      <small>Thành công</small>
                    </div>
                  </div>
                  <div className="col-6">
                    <div className="p-3 bg-danger text-white rounded">
                      <div className="h3 mb-1">{importResults.errorCount}</div>
                      <small>Thất bại</small>
                    </div>
                  </div>
                </div>
              </div>

              {importResults.successCount > 0 && (
                <Alert variant="info" className="mb-3">
                  <Alert.Heading className="h6">
                    <i className="bi bi-key me-2"></i>
                    Thông tin mật khẩu
                  </Alert.Heading>
                  <p className="mb-0">
                    Mật khẩu sẽ được tự động tạo và gửi qua email cho từng nhân viên thành công.
                  </p>
                </Alert>
              )}

              <Card>
                <Card.Header>
                  <h6 className="mb-0">Chi tiết kết quả</h6>
                </Card.Header>
                <Card.Body style={{ maxHeight: '300px', overflowY: 'auto' }}>
                  {importResults.results.map((result, index) => (
                    <div 
                      key={index} 
                      className={`p-2 mb-2 rounded ${result.startsWith('✓') ? 'bg-light text-success' : 'bg-light text-danger'}`}
                    >
                      <small className="font-monospace">{result}</small>
                    </div>
                  ))}
                </Card.Body>
              </Card>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="primary"
            onClick={() => setShowImportResultModal(false)}
          >
            <i className="bi bi-check me-2"></i>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Employee Grid */}
      <Row>
        {filteredEmployees.length === 0 ? (
          <Col>
            <Card>
              <Card.Body className="text-center py-5">
                <i className="bi bi-people fs-1 text-muted mb-3"></i>
                <p className="text-muted mb-0">
                  {searchTerm || filterType ?
                    'Không tìm thấy nhân viên phù hợp với tiêu chí tìm kiếm.' :
                    'Chưa có nhân viên nào trong hệ thống. Thêm nhân viên đầu tiên để bắt đầu.'
                  }
                </p>
              </Card.Body>
            </Card>
          </Col>
        ) : (
          filteredEmployees.map(employee => {
            const typeInfo = getEmployeeTypeInfo(employee.employeeType);
            const employeeId = getEmployeeId(employee);
            const isToggling = toggleLoading.has(employeeId);
            const isActive = employee.deletedAt === null;
            
            return (
              <Col key={employeeId} md={6} lg={4} className="mb-4">
                <Card className="h-100">
                  <Card.Header className="d-flex justify-content-between align-items-center">
                    <div className="d-flex align-items-center">
                      <Badge bg={typeInfo.color} className="me-2">
                        {typeInfo.label}
                      </Badge>
                    </div>
                    <div>
                      <Button
                        size="sm"
                        variant="outline-secondary"
                        className="me-1"
                        onClick={() => handleEdit(employee)}
                        disabled={submitting || isToggling}
                      >
                        <i className="bi bi-pencil me-1"></i>
                        Sửa
                      </Button>
                      <Button
                        size="sm"
                        variant="outline-warning"
                        className="me-1"
                        onClick={() => handleResetPasswordClick(employee)}
                        disabled={submitting || isToggling}
                        title="Đặt lại mật khẩu"
                      >
                        <i className="bi bi-key"></i>
                      </Button>
                    </div>
                  </Card.Header>
                  
                  <Card.Body>
                    <Card.Title className="text-center mb-3">
                      <i className="bi bi-person-circle me-2"></i>
                      {employee.accountName}
                    </Card.Title>

                    <div className="mb-3">
                      <Row className="mb-2">
                        <Col xs={4}>
                          <strong>
                            <i className="bi bi-envelope me-1"></i>
                            Email:
                          </strong>
                        </Col>
                        <Col xs={8} className="text-break">
                          <small>{employee.email}</small>
                        </Col>
                      </Row>

                      <Row className="mb-2">
                        <Col xs={4}>
                          <strong>
                            <i className="bi bi-telephone me-1"></i>
                            SĐT:
                          </strong>
                        </Col>
                        <Col xs={8}>
                          <small>{employee.phoneNumber}</small>
                        </Col>
                      </Row>

                      {employee.citizenId && (
                        <Row className="mb-2">
                          <Col xs={4}>
                            <strong>
                              <i className="bi bi-card-text me-1"></i>
                              CCCD:
                            </strong>
                          </Col>
                          <Col xs={8}>
                            <small>{employee.citizenId}</small>
                          </Col>
                        </Row>
                      )}

                      <Row className="mb-2">
                        <Col xs={4}>
                          <strong>
                            <i className="bi bi-hash me-1"></i>
                            ID:
                          </strong>
                        </Col>
                        <Col xs={8}>
                          <small>{employeeId}</small>
                        </Col>
                      </Row>
                    </div>

                    <Card className="mt-3">
                      <Card.Header as="h6" className="bg-light">
                        <i className="bi bi-info-circle me-2"></i>
                        Mô tả công việc
                      </Card.Header>
                      <Card.Body className="py-2">
                        <small className="text-muted">
                          {typeInfo.description}
                        </small>
                      </Card.Body>
                    </Card>
                  </Card.Body>

                  {/* Employee Status Toggle Footer */}
                  <Card.Footer className="d-flex justify-content-between align-items-center">
                    <div className="d-flex align-items-center">
                      <small className="text-muted me-2">Trạng thái tài khoản:</small>
                      <Badge 
                        bg={isActive ? "success" : "secondary"} 
                        className="small"
                      >
                        <i 
                          className={`bi ${isActive ? 'bi-circle-fill' : 'bi-pause-circle-fill'} me-1`} 
                          style={{ fontSize: '0.5rem' }}
                        ></i>
                        {isActive ? 'Hoạt động' : 'Tạm dừng'}
                      </Badge>
                    </div>
                    
                    <Button
                      size="sm"
                      variant={isActive ? "outline-danger" : "outline-success"}
                      onClick={() => handleToggleEmployeeClick(employee)}
                      disabled={submitting || isToggling}
                      className="px-3"
                    >
                      {isToggling ? (
                        <>
                          <Spinner animation="border" size="sm" className="me-1" />
                          <small>Đang xử lý...</small>
                        </>
                      ) : (
                        <>
                          <i className={`bi ${isActive ? 'bi-pause-circle' : 'bi-play-circle'} me-1`}></i>
                          <small>{isActive ? 'Tạm dừng' : 'Kích hoạt'}</small>
                        </>
                      )}
                    </Button>
                  </Card.Footer>
                </Card>
              </Col>
            );
          })
        )}
      </Row>

      {/* Summary Footer */}
      <Row className="mt-4">
        <Col>
          <Card>
            <Card.Footer className="text-center text-muted">
              <small>
                <i className="bi bi-people me-1"></i>
                Tổng {employees.length} nhân viên
                <span className="mx-2">•</span>
                <i className="bi bi-eye me-1"></i>
                Hiển thị {filteredEmployees.length}
              </small>
            </Card.Footer>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default EmployeeManagement;