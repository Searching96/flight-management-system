# Pagination Implementation Guide

## Completed

✅ Pagination component created at `components/common/Pagination.tsx`
✅ Service methods added with `getAllXPaged(page, size)` for:

- AirportService
- PlaneService
- EmployeeService
- TicketClassService
- FlightService

✅ Full pagination implemented in:

- AirportManagement.tsx

## Pattern Used

### 1. State Management

```typescript
const [currentPage, setCurrentPage] = useState(0);
const [pageSize, setPageSize] = useState(10);
const [totalPages, setTotalPages] = useState(0);
const [totalElements, setTotalElements] = useState(0);
```

### 2. Load Function

```typescript
const loadItems = async (page: number = currentPage) => {
  try {
    setLoading(true);
    const response = await service.getAllItemsPaged(page, pageSize);
    setItems(response.data.content);
    setTotalPages(response.data.totalPages);
    setTotalElements(response.data.totalElements);
    setCurrentPage(response.data.number);
    setError("");
  } catch (error) {
    console.error("Error loading items:", error);
    setError("Failed to load items");
  } finally {
    setLoading(false);
  }
};
```

### 3. Page Handlers

```typescript
const handlePageChange = (page: number) => {
  setCurrentPage(page);
  loadItems(page);
};

const handlePageSizeChange = (newSize: number) => {
  setPageSize(newSize);
  setCurrentPage(0);
  loadItems(0);
};
```

### 4. UseEffect

```typescript
useEffect(() => {
  loadItems();
  // eslint-disable-next-line react-hooks/exhaustive-deps
}, []);

useEffect(() => {
  if (currentPage === 0) {
    loadItems(0);
  } else {
    setCurrentPage(0);
  }
  // eslint-disable-next-line react-hooks/exhaustive-deps
}, [pageSize]);
```

### 5. Header UI

```tsx
<Card.Header className="d-flex justify-content-between align-items-center">
  <div className="d-flex align-items-center gap-3">
    <Card.Title className="mb-0">Title</Card.Title>
    <div className="d-flex align-items-center gap-2">
      <Form.Label className="mb-0 text-muted small">
        Kích thước trang:
      </Form.Label>
      <Form.Select
        size="sm"
        value={pageSize}
        onChange={(e) => handlePageSizeChange(Number(e.target.value))}
        style={{ width: "auto" }}
      >
        <option value={5}>5</option>
        <option value={10}>10</option>
        <option value={20}>20</option>
        <option value={50}>50</option>
      </Form.Select>
      <span className="text-muted small">({totalElements} items)</span>
    </div>
  </div>
  <Button>Add New</Button>
</Card.Header>
```

### 6. Pagination Controls

```tsx
{
  !loading && totalPages > 1 && (
    <Row className="mt-4">
      <Col className="d-flex justify-content-center">
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </Col>
    </Row>
  );
}
```

## To Apply to Remaining Components

- PlaneManagement.tsx
- EmployeeManagement.tsx
- TicketClassManagement.tsx
- FlightManagement.tsx (needs hook update)
