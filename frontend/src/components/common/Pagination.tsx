import React from "react";
import { Button, ButtonGroup } from "react-bootstrap";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  onPageChange,
}) => {
  if (totalPages <= 1) return null;

  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxVisible = 5;

    if (totalPages <= maxVisible) {
      // Show all pages if total is small
      for (let i = 0; i < totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Always show first page
      pages.push(0);

      if (currentPage > 2) {
        pages.push("...");
      }

      // Show pages around current
      const start = Math.max(1, currentPage - 1);
      const end = Math.min(totalPages - 2, currentPage + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (currentPage < totalPages - 3) {
        pages.push("...");
      }

      // Always show last page
      pages.push(totalPages - 1);
    }

    return pages;
  };

  return (
    <ButtonGroup aria-label="Pagination">
      {/* First page */}
      <Button
        variant="outline-primary"
        onClick={() => onPageChange(0)}
        disabled={currentPage === 0}
        aria-label="First page"
      >
        &laquo;
      </Button>

      {/* Previous page */}
      <Button
        variant="outline-primary"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
        aria-label="Previous page"
      >
        &lsaquo;
      </Button>

      {/* Page numbers */}
      {getPageNumbers().map((page, index) => {
        if (page === "...") {
          return (
            <Button
              key={`ellipsis-${index}`}
              variant="outline-primary"
              disabled
            >
              ...
            </Button>
          );
        }

        const pageNum = page as number;
        return (
          <Button
            key={pageNum}
            variant={currentPage === pageNum ? "primary" : "outline-primary"}
            onClick={() => onPageChange(pageNum)}
            aria-label={`Page ${pageNum + 1}`}
            aria-current={currentPage === pageNum ? "page" : undefined}
          >
            {pageNum + 1}
          </Button>
        );
      })}

      {/* Next page */}
      <Button
        variant="outline-primary"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage >= totalPages - 1}
        aria-label="Next page"
      >
        &rsaquo;
      </Button>

      {/* Last page */}
      <Button
        variant="outline-primary"
        onClick={() => onPageChange(totalPages - 1)}
        disabled={currentPage >= totalPages - 1}
        aria-label="Last page"
      >
        &raquo;
      </Button>
    </ButtonGroup>
  );
};

export default Pagination;
