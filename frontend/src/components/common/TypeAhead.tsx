import React, { useState, useRef, useEffect } from 'react';
import './TypeAhead.css';

interface Option {
  value: string | number;
  label: string;
  [key: string]: any;
}

interface TypeAheadProps {
  options: Option[];
  value?: string | number;
  onChange: (option: Option | null) => void;
  placeholder?: string;
  disabled?: boolean;
  className?: string;
  filterProperty?: string;
  displayProperty?: string;
  noOptionsMessage?: string;
  allowClear?: boolean;
  error?: boolean;
}

const TypeAhead: React.FC<TypeAheadProps> = ({
  options,
  value,
  onChange,
  placeholder = "Search...",
  disabled = false,
  className = "",
  filterProperty = "label",
  displayProperty = "label",
  noOptionsMessage = "No options found",
  allowClear = true,
  error = false
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredOptions, setFilteredOptions] = useState<Option[]>(options);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  
  const inputRef = useRef<HTMLInputElement>(null);
  const listRef = useRef<HTMLUListElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // Find selected option
  const selectedOption = options.find(option => option.value === value);

  useEffect(() => {
    if (selectedOption) {
      setSearchTerm(selectedOption[displayProperty]);
    } else {
      setSearchTerm('');
    }
  }, [selectedOption, displayProperty]);

  useEffect(() => {
    const filtered = options.filter(option =>
      option[filterProperty]
        ?.toString()
        .toLowerCase()
        .includes(searchTerm.toLowerCase())
    );
    setFilteredOptions(filtered);
    setHighlightedIndex(-1);
  }, [searchTerm, options, filterProperty]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
        if (selectedOption) {
          setSearchTerm(selectedOption[displayProperty]);
        } else {
          setSearchTerm('');
        }
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [selectedOption, displayProperty]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const term = e.target.value;
    setSearchTerm(term);
    setIsOpen(true);
    
    if (term === '' && allowClear) {
      onChange(null);
    }
  };

  const handleOptionClick = (option: Option) => {
    onChange(option);
    setSearchTerm(option[displayProperty]);
    setIsOpen(false);
    inputRef.current?.blur();
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (!isOpen) {
      if (e.key === 'ArrowDown' || e.key === 'Enter') {
        setIsOpen(true);
        return;
      }
    }

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setHighlightedIndex(prev => 
          prev < filteredOptions.length - 1 ? prev + 1 : 0
        );
        break;
      case 'ArrowUp':
        e.preventDefault();
        setHighlightedIndex(prev => 
          prev > 0 ? prev - 1 : filteredOptions.length - 1
        );
        break;
      case 'Enter':
        e.preventDefault();
        if (highlightedIndex >= 0 && filteredOptions[highlightedIndex]) {
          handleOptionClick(filteredOptions[highlightedIndex]);
        }
        break;
      case 'Escape':
        setIsOpen(false);
        if (selectedOption) {
          setSearchTerm(selectedOption[displayProperty]);
        } else {
          setSearchTerm('');
        }
        inputRef.current?.blur();
        break;
    }
  };

  const handleClear = (e: React.MouseEvent) => {
    e.stopPropagation();
    onChange(null);
    setSearchTerm('');
    setIsOpen(false);
    inputRef.current?.focus();
  };

  return (
    <div className={`typeahead ${className} ${error ? 'error' : ''}`} ref={containerRef}>
      <div className="typeahead-input-wrapper">
        <input
          ref={inputRef}
          type="text"
          value={searchTerm}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          onFocus={() => setIsOpen(true)}
          placeholder={placeholder}
          disabled={disabled}
          className={`typeahead-input ${isOpen ? 'open' : ''}`}
          autoComplete="off"
        />
        
        {allowClear && searchTerm && !disabled && (
          <button
            type="button"
            className="typeahead-clear"
            onClick={handleClear}
            tabIndex={-1}
          >
            ×
          </button>
        )}
        
        <div className={`typeahead-arrow ${isOpen ? 'open' : ''}`}>
          ▼
        </div>
      </div>

      {isOpen && (
        <ul className="typeahead-options" ref={listRef}>
          {filteredOptions.length > 0 ? (
            filteredOptions.map((option, index) => (
              <li
                key={option.value}
                className={`typeahead-option ${
                  index === highlightedIndex ? 'highlighted' : ''
                } ${option.value === value ? 'selected' : ''}`}
                onClick={() => handleOptionClick(option)}
                onMouseEnter={() => setHighlightedIndex(index)}
              >
                {option[displayProperty]}
              </li>
            ))
          ) : (
            <li className="typeahead-no-options">{noOptionsMessage}</li>
          )}
        </ul>
      )}
    </div>
  );
};

export default TypeAhead;
