import React, { useState, useMemo } from 'react';
import { Table, Badge, Button, Card, Row, Col, Form, InputGroup, Pagination } from 'react-bootstrap';
import { Flight } from '../../../models';

interface FlightTableProps {
    flights: Flight[];
    onEdit: (flight: Flight) => void;
    onDelete: (flightId: number) => void;
    onManageTicketClasses: (flight: Flight) => void;
}

const FlightTable: React.FC<FlightTableProps> = ({
    flights,
    onEdit,
    onDelete,
    onManageTicketClasses
}) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);

    // Filter flights based on search term
    const filteredFlights = useMemo(() => {
        if (!searchTerm.trim()) return flights;

        const searchLower = searchTerm.toLowerCase();
        return flights.filter(flight => {
            // Search across all flight fields
            return (
                flight.flightCode?.toLowerCase().includes(searchLower) ||
                flight.departureCityName?.toLowerCase().includes(searchLower) ||
                flight.arrivalCityName?.toLowerCase().includes(searchLower) ||
                flight.planeCode?.toLowerCase().includes(searchLower) ||
                new Date(flight.departureTime).toLocaleString().toLowerCase().includes(searchLower) ||
                new Date(flight.arrivalTime).toLocaleString().toLowerCase().includes(searchLower) ||
                `${flight.departureCityName} → ${flight.arrivalCityName}`.toLowerCase().includes(searchLower)
            );
        });
    }, [flights, searchTerm]);

    // Calculate pagination
    const totalPages = Math.ceil(filteredFlights.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentFlights = filteredFlights.slice(startIndex, endIndex);

    // Reset to first page when search changes
    React.useEffect(() => {
        setCurrentPage(1);
    }, [searchTerm, itemsPerPage]);

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    const generatePaginationItems = () => {
        const items = [];
        const maxVisible = 5;
        
        if (totalPages <= maxVisible) {
            for (let i = 1; i <= totalPages; i++) {
                items.push(
                    <Pagination.Item
                        key={i}
                        active={i === currentPage}
                        onClick={() => handlePageChange(i)}
                    >
                        {i}
                    </Pagination.Item>
                );
            }
        } else {
            // Always show first page
            items.push(
                <Pagination.Item
                    key={1}
                    active={1 === currentPage}
                    onClick={() => handlePageChange(1)}
                >
                    1
                </Pagination.Item>
            );

            // Show ellipsis if current page is far from start
            if (currentPage > 3) {
                items.push(<Pagination.Ellipsis key="start-ellipsis" />);
            }

            // Show pages around current page
            const start = Math.max(2, currentPage - 1);
            const end = Math.min(totalPages - 1, currentPage + 1);

            for (let i = start; i <= end; i++) {
                items.push(
                    <Pagination.Item
                        key={i}
                        active={i === currentPage}
                        onClick={() => handlePageChange(i)}
                    >
                        {i}
                    </Pagination.Item>
                );
            }

            // Show ellipsis if current page is far from end
            if (currentPage < totalPages - 2) {
                items.push(<Pagination.Ellipsis key="end-ellipsis" />);
            }

            // Always show last page
            if (totalPages > 1) {
                items.push(
                    <Pagination.Item
                        key={totalPages}
                        active={totalPages === currentPage}
                        onClick={() => handlePageChange(totalPages)}
                    >
                        {totalPages}
                    </Pagination.Item>
                );
            }
        }

        return items;
    };

    return (
        <Card>
            <Card.Header>
                <Row className="align-items-center">
                    <Col>
                        <Card.Title className="mb-0">Tất cả chuyến bay</Card.Title>
                    </Col>
                    <Col md="auto">
                        <Row className="g-2">
                            <Col>
                                <InputGroup>
                                    <InputGroup.Text>
                                        <i className="bi bi-search"></i>
                                    </InputGroup.Text>
                                    <Form.Control
                                        type="text"
                                        placeholder="Tìm kiếm chuyến bay..."
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                    />
                                </InputGroup>
                            </Col>
                            <Col md="auto">
                                <Form.Select
                                    value={itemsPerPage}
                                    onChange={(e) => setItemsPerPage(Number(e.target.value))}
                                    size="sm"
                                >
                                    <option value={5}>5 / trang</option>
                                    <option value={10}>10 / trang</option>
                                    <option value={25}>25 / trang</option>
                                    <option value={50}>50 / trang</option>
                                </Form.Select>
                            </Col>
                        </Row>
                    </Col>
                </Row>
                
                {/* Results summary */}
                <Row className="mt-2">
                    <Col>
                        <small className="text-muted">
                            Hiển thị {currentFlights.length > 0 ? startIndex + 1 : 0}
                            -{Math.min(endIndex, filteredFlights.length)} trong tổng số {filteredFlights.length} chuyến bay
                            {searchTerm && ` (lọc từ ${flights.length} chuyến bay)`}
                        </small>
                    </Col>
                </Row>
            </Card.Header>
            
            <Card.Body className="p-0">
                {filteredFlights.length === 0 ? (
                    <div className="text-center py-5">
                        {searchTerm ? (
                            <div>
                                <i className="bi bi-search text-muted" style={{ fontSize: '2rem' }}></i>
                                <p className="text-muted mt-2 mb-1">Không tìm thấy chuyến bay nào phù hợp</p>
                                <p className="text-muted small">Thử tìm kiếm với từ khóa khác</p>
                                <Button 
                                    variant="outline-secondary" 
                                    size="sm"
                                    onClick={() => setSearchTerm('')}
                                >
                                    Xóa bộ lọc
                                </Button>
                            </div>
                        ) : (
                            <p className="text-muted mb-0">Không tìm thấy chuyến bay nào. Thêm chuyến bay đầu tiên để bắt đầu.</p>
                        )}
                    </div>
                ) : (
                    <>
                        <Table responsive striped hover className="mb-0">
                            <thead>
                                <tr>
                                    <th>Mã chuyến bay</th>
                                    <th>Tuyến bay</th>
                                    <th>Khởi hành</th>
                                    <th>Đến</th>
                                    <th>Máy bay</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                {currentFlights.map(flight => (
                                    <tr key={flight.flightId}>
                                        <td>
                                            <Badge bg="primary">{flight.flightCode}</Badge>
                                        </td>
                                        <td>{flight.departureCityName} → {flight.arrivalCityName}</td>
                                        <td>{new Date(flight.departureTime).toLocaleString()}</td>
                                        <td>{new Date(flight.arrivalTime).toLocaleString()}</td>
                                        <td>{flight.planeCode}</td>
                                        <td>
                                            <div className="d-flex gap-1 flex-wrap">
                                                <Button
                                                    size="sm"
                                                    variant="outline-secondary"
                                                    onClick={() => onEdit(flight)}
                                                    title="Chỉnh sửa"
                                                >
                                                    <i className="bi bi-pencil"></i>
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="outline-primary"
                                                    onClick={() => onManageTicketClasses(flight)}
                                                    title="Quản lý hạng vé"
                                                >
                                                    <i className="bi bi-ticket"></i>
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="outline-danger"
                                                    onClick={() => onDelete(flight.flightId!)}
                                                    title="Xóa"
                                                >
                                                    <i className="bi bi-trash"></i>
                                                </Button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                        
                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div className="d-flex justify-content-between align-items-center p-3 border-top">
                                <div>
                                    <small className="text-muted">
                                        Trang {currentPage} / {totalPages}
                                    </small>
                                </div>
                                <Pagination className="mb-0">
                                    <Pagination.First 
                                        disabled={currentPage === 1}
                                        onClick={() => handlePageChange(1)}
                                    />
                                    <Pagination.Prev 
                                        disabled={currentPage === 1}
                                        onClick={() => handlePageChange(currentPage - 1)}
                                    />
                                    
                                    {generatePaginationItems()}
                                    
                                    <Pagination.Next 
                                        disabled={currentPage === totalPages}
                                        onClick={() => handlePageChange(currentPage + 1)}
                                    />
                                    <Pagination.Last 
                                        disabled={currentPage === totalPages}
                                        onClick={() => handlePageChange(totalPages)}
                                    />
                                </Pagination>
                            </div>
                        )}
                    </>
                )}
            </Card.Body>
        </Card>
    );
};

export default FlightTable;
