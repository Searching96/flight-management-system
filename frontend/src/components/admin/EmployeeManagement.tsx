import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal } from 'react-bootstrap';
import { authService, employeeService } from '../../services';
import { usePermissions } from '../../hooks/useAuth';
import { Employee, RegisterRequest, UpdateEmployeeRequest } from '../../models';

interface EmployeeFormData {
  accountName: string;
  password?: string;
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

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm<EmployeeFormData>();

  // Employee type options
  const employeeTypeOptions = [
    { value: 1, label: "Vận hành chuyến bay", color: "primary", description: "Quản lý vận hành máy bay" },
    { value: 2, label: "Bán vé", color: "success", description: "Hỗ trợ khách hàng đặt vé" },
    { value: 3, label: "Hỗ trợ khách hàng", color: "info", description: "Giải quyết thắc mắc khách hàng" },
    { value: 4, label: "Lập lịch chuyến bay", color: "warning", description: "Quản lý lịch trình bay" },
    { value: 5, label: "Quản trị viên", color: "danger", description: "Toàn quyền hệ thống" }
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
        // Create new employee
        const registerData: RegisterRequest = {
          accountName: data.accountName,
          password: data.password!,
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
      employeeType: employee.employeeType,
      password: '' // Don't populate password for security
    });
    setShowForm(true);
  };

  const handleToggleEmployeeStatus = async (employee: Employee) => {
    const employeeId = getEmployeeId(employee);
    const isActive = employee.deletedAt === null;
    
    if (!isActive) {
      // Activating employee
      if (!window.confirm(`Bạn có chắc chắn muốn kích hoạt nhân viên ${employee.accountName}?`)) return;
    } else {
      // Deactivating employee
      if (!window.confirm(`Bạn có chắc chắn muốn vô hiệu hóa nhân viên ${employee.accountName}? Nhân viên sẽ không thể đăng nhập vào hệ thống.`)) return;
    }

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

  const handleResetPassword = async (employeeId: number, employeeName: string) => {
    if (!window.confirm(`Bạn có chắc chắn muốn đặt lại mật khẩu cho nhân viên ${employeeName}?`)) return;

    try {
      setError('');
      console.log('Resetting password for employee:', employeeId, 'at 2025-06-11 06:58:25 UTC by thinh0704hcm');
      await authService.forgetPassword((await employeeService.getEmployeeById(employeeId)).email!);
      alert(`Mật khẩu tạm thời mới đã được gửi đến email của nhân viên\nVui lòng lưu lại và thông báo cho nhân viên.`);
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
              <Button
                variant="primary"
                onClick={() => setShowForm(true)}
                disabled={submitting}
              >
                <i className="bi bi-person-plus me-2"></i>
                Thêm nhân viên mới
              </Button>
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
                    Tên tài khoản
                  </Form.Label>
                  <Form.Control
                    type="text"
                    {...register('accountName', {
                      required: 'Tên tài khoản là bắt buộc',
                      minLength: {
                        value: 3,
                        message: 'Tên tài khoản phải có ít nhất 3 ký tự'
                      },
                      pattern: {
                        value: /^[a-zA-Z0-9_]+$/,
                        message: 'Chỉ sử dụng chữ cái, số và dấu gạch dưới'
                      }
                    })}
                    isInvalid={!!errors.accountName}
                    placeholder="ví dụ: nguyenvana"
                    disabled={submitting}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.accountName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>
                    <i className="bi bi-lock me-1"></i>
                    Mật khẩu {editingEmployee && <small className="text-muted">(để trống nếu không đổi)</small>}
                  </Form.Label>
                  <Form.Control
                    type="password"
                    {...register('password', {
                      required: editingEmployee ? false : 'Mật khẩu là bắt buộc',
                      minLength: {
                        value: 6,
                        message: 'Mật khẩu phải có ít nhất 6 ký tự'
                      }
                    })}
                    isInvalid={!!errors.password}
                    placeholder={editingEmployee ? "Nhập mật khẩu mới..." : "Nhập mật khẩu..."}
                    disabled={submitting}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.password?.message}
                  </Form.Control.Feedback>
                </Form.Group>
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
                        onClick={() => handleResetPassword(employeeId, employee.accountName || '')}
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
                        className="px-2 py-1"
                      >
                        {isActive ? 'Đang hoạt động' : 'Đã tạm dừng'}
                      </Badge>
                    </div>
                    
                    <Button
                      size="sm"
                      variant={isActive ? "outline-danger" : "outline-success"}
                      onClick={() => handleToggleEmployeeStatus(employee)}
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
                <i className="bi bi-clock me-1"></i>
                Cập nhật lần cuối: 2025-06-11 06:58:25 UTC bởi thinh0704hcm
                <span className="mx-2">•</span>
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