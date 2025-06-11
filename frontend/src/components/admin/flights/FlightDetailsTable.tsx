import React from 'react';
import { Table, Button, InputGroup, Form, Card } from 'react-bootstrap';
import { FlightDetailWithIndex } from '../../../hooks/useFlightDetails';
import TypeAhead from '../../common/TypeAhead';
import { Parameter } from '../../../models';

interface FlightDetailsTableProps {
  flightDetails: FlightDetailWithIndex[];
  airportOptions: any[];
  selectedDepartureAirport: number | '';
  selectedArrivalAirport: number | '';
  onAddRow: () => void;
  onRemoveRow: (index: number) => void;
  onDetailChange: (index: number, field: string, value: any) => void;
  parameters: Parameter | null;
}

const FlightDetailsTable: React.FC<FlightDetailsTableProps> = ({
  flightDetails,
  airportOptions,
  selectedDepartureAirport,
  selectedArrivalAirport,
  onAddRow,
  onRemoveRow,
  onDetailChange,
  parameters
}) => {
  // Check if we've reached the maximum number of layovers
  const maxLayovers = parameters?.maxMediumAirport || 5;
  const isMaxLayoversReached = flightDetails.length >= maxLayovers;
  
  return (
    <Card className="py-2" style={{ overflow: 'visible' }}>
      <Card.Header className="d-flex justify-content-between align-items-center" style={{ overflow: 'visible' }}>
        <h6 className="mb-0">
          Sân bay trung gian
          <small className="text-muted ms-2">
            ({flightDetails.length}/{maxLayovers})
          </small>
        </h6>
        <Button
          variant="outline-primary"
          size="sm"
          onClick={onAddRow}
          disabled={isMaxLayoversReached}
          title={isMaxLayoversReached ? `Đã đạt giới hạn tối đa ${maxLayovers} sân bay trung gian` : undefined}
        >
          <i className="bi bi-plus-circle me-1"></i> Thêm sân bay trung gian
        </Button>
      </Card.Header>
      
      <Card.Body className="overflow-visible p-0">
        {flightDetails.length > 0 ? (
          <Table bordered hover size="sm" className="mb-0 overflow-visible">
            <thead className="bg-light">
              <tr>
                <th className="text-center" style={{ width: '5%' }}>#</th>
                <th>Sân bay</th>
                <th style={{ width: '18%' }}>Thời gian dừng</th>
                <th style={{ width: '15%' }}>Thời gian đến</th>
                <th className="text-center" style={{ width: '12%' }}>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {flightDetails.map((detail, index) => (
                <tr key={index} className='align-items-center overflow-visible'>
                  <td className="text-center align-middle">{detail.index}</td>
                  <td className="position-relative" style={{ overflow: 'visible', zIndex: 100 - index }}>
                    <TypeAhead
                      options={airportOptions.filter(airport =>
                        Number(airport.value) !== Number(selectedDepartureAirport) &&
                        Number(airport.value) !== Number(selectedArrivalAirport) &&
                        !flightDetails.some((d, i) => i !== index && d.mediumAirportId === Number(airport.value))
                      )}
                      allowClear={true}
                      value={detail.mediumAirportId}
                      onChange={(option) => {
                        const airportId = option?.value as number || '';
                        onDetailChange(index, 'mediumAirportId', airportId);
                      }}
                      placeholder="Chọn sân bay..."
                      error={false}
                    />
                  </td>
                  <td>
                    <InputGroup>
                      <Form.Control
                        type="number"
                        min="0"
                        value={detail.layoverDuration}
                        onChange={(e) => onDetailChange(index, 'layoverDuration', parseInt(e.target.value) || 0)}
                        placeholder="Duration"
                        style={{ height: '38px' }}
                      />
                      <InputGroup.Text style={{ height: '38px' }}>phút</InputGroup.Text>
                    </InputGroup>
                  </td>
                  <td>
                    <Form.Control
                      type="datetime-local"
                      value={detail.arrivalTime?.slice(0, 16) || ''}
                      onChange={(e) => onDetailChange(index, 'arrivalTime', e.target.value)}
                      style={{ height: '38px' }}
                    />
                  </td>
                  <td className="text-center align-middle">
                    <Button
                      size="sm"
                      variant="outline-danger"
                      onClick={() => onRemoveRow(index)}
                    >
                      <i className="bi bi-trash"></i>
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <div className="text-center py-3 text-muted">
            <i className="bi bi-info-circle me-2"></i>
            Không có sân bay trung gian. Thêm sân bay trung gian cho chuyến bay nếu có.
          </div>
        )}
      </Card.Body>
      
      {flightDetails.length > 0 && (
        <Card.Footer className="bg-light small text-muted">
          <i className="bi bi-info-circle me-1"></i>
          Sân bay trung gian là các điểm dừng giữa sân bay đi và sân bay đến.
          {parameters && (
            <>
              <br />
              <i className="bi bi-info-circle me-1 mt-1"></i>
              Thời gian dừng phải trong khoảng từ {parameters?.minLayoverDuration} đến {parameters?.maxLayoverDuration} phút.
            </>
          )}
        </Card.Footer>
      )}
    </Card>
  );
};

export default FlightDetailsTable;
