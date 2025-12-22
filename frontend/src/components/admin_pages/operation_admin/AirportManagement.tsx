import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Form,
  Alert,
  Spinner,
  Badge,
  Modal,
  Table,
} from "react-bootstrap";
import { airportService } from "../../../services";
import { Airport } from "../../../models";
import TypeAhead from "../../common/TypeAhead";
import Pagination from "../../common/Pagination";
import { usePermissions } from "../../../hooks/useAuth";

interface AirportFormData {
  airportName: string;
  cityName: string;
  countryName: string;
}

const AirportManagement: React.FC<{
  showAddModal?: boolean;
  onCloseAddModal?: () => void;
  readOnly?: boolean;
}> = ({ showAddModal = false, onCloseAddModal, readOnly = false }) => {
  const { canViewAdmin } = usePermissions();

  const [airports, setAirports] = useState<Airport[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingAirport, setEditingAirport] = useState<Airport | null>(null);
  const [selectedCountry, setSelectedCountry] = useState<string>("");
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [airportToDelete, setAirportToDelete] = useState<Airport | null>(null);
  const [deleting, setDeleting] = useState(false);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AirportFormData>();

  useEffect(() => {
    if (showAddModal && !readOnly) {
      setShowForm(true);
    }
  }, [showAddModal, readOnly]);

  if (!canViewAdmin) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Truy cập bị từ chối</Alert.Heading>
              <p>Bạn không có quyền truy cập quản lý sân bay.</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  // Country options for TypeAhead
  const countryOptions = [
    { value: "Vietnam", label: "Vietnam" },
    { value: "United States", label: "United States" },
    { value: "United Kingdom", label: "United Kingdom" },
    { value: "China", label: "China" },
    { value: "Japan", label: "Japan" },
    { value: "South Korea", label: "South Korea" },
    { value: "Singapore", label: "Singapore" },
    { value: "Thailand", label: "Thailand" },
    { value: "Malaysia", label: "Malaysia" },
    { value: "Indonesia", label: "Indonesia" },
    { value: "Philippines", label: "Philippines" },
    { value: "Australia", label: "Australia" },
    { value: "France", label: "France" },
    { value: "Germany", label: "Germany" },
    { value: "Canada", label: "Canada" },
  ];

  const loadAirports = async (page: number = currentPage) => {
    try {
      setLoading(true);
      const response = await airportService.getAllAirportsPaged(page, pageSize);
      setAirports(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setCurrentPage(response.data.number);
      setError("");
    } catch (error) {
      console.error("Error loading airports:", error);
      setError("Không thể tải danh sách sân bay");
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    loadAirports(page);
  };

  const handlePageSizeChange = (newSize: number) => {
    setPageSize(newSize);
    setCurrentPage(0);
    loadAirports(0);
  };

  useEffect(() => {
    loadAirports();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (currentPage === 0) {
      loadAirports(0);
    } else {
      setCurrentPage(0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageSize]);

  const onSubmit = async (data: AirportFormData) => {
    try {
      if (editingAirport) {
        await airportService.updateAirport(editingAirport.airportId!, data);
      } else {
        await airportService.createAirport(data);
      }

      loadAirports();
      handleCancel();
    } catch (err: any) {
      setError(err.message || "Không thể lưu sân bay");
    }
  };

  const handleEdit = (airport: Airport) => {
    setEditingAirport(airport);
    setSelectedCountry(airport.countryName || "");
    reset({
      airportName: airport.airportName,
      cityName: airport.cityName,
      countryName: airport.countryName,
    });
    setShowForm(true);
  };

  const handleDeleteClick = (airport: Airport) => {
    setAirportToDelete(airport);
    setShowDeleteModal(true);
  };

  const handleConfirmDelete = async () => {
    if (!airportToDelete) return;

    try {
      setDeleting(true);
      await airportService.deleteAirport(airportToDelete.airportId!);
      await loadAirports();
      setShowDeleteModal(false);
      setAirportToDelete(null);
    } catch (err: any) {
      setError(err.message || "Không thể xóa sân bay");
    } finally {
      setDeleting(false);
    }
  };

  const handleCancelDelete = () => {
    setShowDeleteModal(false);
    setAirportToDelete(null);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingAirport(null);
    setSelectedCountry("");
    reset();
    setError("");

    // Call the external close handler if provided
    if (onCloseAddModal) {
      onCloseAddModal();
    }
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Đang tải...</span>
        </Spinner>
        <p className="mt-3">Đang tải dữ liệu sân bay...</p>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4">
      {/* Read-only mode alert */}
      {readOnly && (
        <Row className="mb-4">
          <Col>
            <Alert variant="info" className="text-center">
              <Alert.Heading>Chế độ chỉ xem</Alert.Heading>
              <p className="mb-0">
                Bạn đang xem danh sách sân bay. Không thể chỉnh sửa trong chế độ
                này.
              </p>
            </Alert>
          </Col>
        </Row>
      )}

      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Header className="d-flex justify-content-between align-items-center">
              <div className="d-flex align-items-center gap-3">
                <Card.Title className="mb-0">
                  🏢 {readOnly ? "Danh sách sân bay" : "Quản lý sân bay"}
                </Card.Title>
                <div className="d-flex align-items-center gap-2">
                  <Form.Label className="mb-0 text-muted small">
                    Kích thước trang:
                  </Form.Label>
                  <Form.Select
                    size="sm"
                    value={pageSize}
                    onChange={(e) =>
                      handlePageSizeChange(Number(e.target.value))
                    }
                    style={{ width: "auto" }}
                  >
                    <option value={5}>5</option>
                    <option value={10}>10</option>
                    <option value={20}>20</option>
                    <option value={50}>50</option>
                  </Form.Select>
                  <span className="text-muted small">
                    ({totalElements} sân bay)
                  </span>
                </div>
              </div>
              {!readOnly && (
                <Button variant="primary" onClick={() => setShowForm(true)}>
                  Thêm sân bay mới
                </Button>
              )}
            </Card.Header>
          </Card>
        </Col>
      </Row>

      {error && (
        <Row className="mb-4">
          <Col>
            <Alert variant="danger" className="text-center">
              {error}
            </Alert>
          </Col>
        </Row>
      )}

      {/* Add/Edit Airport Modal */}
      <Modal show={showForm && !readOnly} onHide={handleCancel} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingAirport ? "Chỉnh sửa sân bay" : "Thêm sân bay mới"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form id="airport-form" onSubmit={handleSubmit(onSubmit)}>
            <Row className="mb-3">
              <Col>
                <Form.Group>
                  <Form.Label>Tên sân bay</Form.Label>
                  <Form.Control
                    type="text"
                    {...register("airportName", {
                      required: "Tên sân bay là bắt buộc",
                    })}
                    isInvalid={!!errors.airportName}
                    placeholder="ví dụ: Sân bay quốc tế Tân Sơn Nhất"
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.airportName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
            </Row>

            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Tên thành phố</Form.Label>
                  <Form.Control
                    type="text"
                    {...register("cityName", {
                      required: "Tên thành phố là bắt buộc",
                    })}
                    isInvalid={!!errors.cityName}
                    placeholder="ví dụ: Hồ Chí Minh"
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.cityName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Quốc gia</Form.Label>
                  <TypeAhead
                    options={countryOptions}
                    value={selectedCountry}
                    onChange={(option) => {
                      const country = option?.value || "";
                      setSelectedCountry(String(country));
                    }}
                    placeholder="Tìm kiếm quốc gia..."
                    error={!!errors.countryName}
                  />
                  <input
                    type="hidden"
                    {...register("countryName", {
                      required: "Quốc gia là bắt buộc",
                    })}
                    value={selectedCountry}
                  />
                  {errors.countryName && (
                    <div className="text-danger small mt-1">
                      {errors.countryName.message}
                    </div>
                  )}
                </Form.Group>
              </Col>
            </Row>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCancel}>
            Hủy
          </Button>
          <Button variant="primary" onClick={handleSubmit(onSubmit)}>
            {editingAirport ? "Cập nhật sân bay" : "Tạo sân bay"}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Delete Confirmation Modal */}
      {!readOnly && (
        <Modal show={showDeleteModal} onHide={handleCancelDelete} centered>
          <Modal.Header closeButton className="bg-danger text-white">
            <Modal.Title>
              <i className="bi bi-exclamation-triangle me-2"></i>
              Xác nhận xóa sân bay
            </Modal.Title>
          </Modal.Header>
          <Modal.Body className="p-4">
            <div className="text-center mb-3">
              <i
                className="bi bi-exclamation-circle text-danger"
                style={{ fontSize: "3rem" }}
              ></i>
            </div>
            <h5 className="text-center mb-3">
              Bạn có chắc chắn muốn xóa sân bay này không?
            </h5>
            {airportToDelete && (
              <div className="p-3 bg-light rounded mb-3">
                <div className="text-center">
                  <strong>{airportToDelete.airportName}</strong>
                  <br />
                  <span className="text-muted">
                    {airportToDelete.cityName}, {airportToDelete.countryName}
                  </span>
                </div>
              </div>
            )}
            <p className="text-center text-muted mb-0">
              Hành động này không thể hoàn tác. Sân bay và tất cả dữ liệu liên
              quan sẽ bị xóa vĩnh viễn.
            </p>
          </Modal.Body>
          <Modal.Footer>
            <Button
              variant="secondary"
              onClick={handleCancelDelete}
              disabled={deleting}
            >
              Hủy
            </Button>
            <Button
              variant="danger"
              onClick={handleConfirmDelete}
              disabled={deleting}
            >
              {deleting ? (
                <>
                  <Spinner animation="border" size="sm" className="me-2" />
                  Đang xóa...
                </>
              ) : (
                <>
                  <i className="bi bi-trash me-2"></i>
                  Có, xóa sân bay
                </>
              )}
            </Button>
          </Modal.Footer>
        </Modal>
      )}

      {/* Airport Table */}
      <Row>
        <Col>
          <Card>
            <Card.Header>
              <Card.Title className="mb-0">
                {readOnly ? "Danh sách sân bay" : "Tất cả sân bay"}
              </Card.Title>
            </Card.Header>
            <Card.Body className="p-0">
              {airports.length === 0 ? (
                <div className="text-center py-5">
                  <p className="text-muted mb-0">
                    {readOnly
                      ? "Không tìm thấy sân bay nào."
                      : "Không tìm thấy sân bay nào. Thêm sân bay đầu tiên để bắt đầu."}
                  </p>
                </div>
              ) : (
                <Table responsive striped hover>
                  <thead>
                    <tr>
                      <th>Tên sân bay</th>
                      <th>Thành phố</th>
                      <th>Quốc gia</th>
                      {!readOnly && <th>Thao tác</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {airports.map((airport) => (
                      <tr key={airport.airportId}>
                        <td>{airport.airportName}</td>
                        <td>
                          <Badge bg="info">{airport.cityName}</Badge>
                        </td>
                        <td>{airport.countryName}</td>
                        {!readOnly && (
                          <td>
                            <Button
                              size="sm"
                              variant="outline-secondary"
                              className="me-2"
                              onClick={() => handleEdit(airport)}
                              disabled={deleting}
                            >
                              Sửa
                            </Button>
                            <Button
                              size="sm"
                              variant="outline-danger"
                              onClick={() => handleDeleteClick(airport)}
                              disabled={deleting}
                            >
                              Xóa
                            </Button>
                          </td>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </Table>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Pagination */}
      {!loading && totalPages > 1 && (
        <Row className="mt-4">
          <Col className="d-flex justify-content-center">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default AirportManagement;
