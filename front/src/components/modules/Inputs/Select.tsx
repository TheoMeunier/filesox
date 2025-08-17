import React, {useEffect, useRef, useState} from 'react';

export interface Option {
    value: string | number;
    label: string;
}

interface CustomSelectProps {
    options?: Option[];
    value?: (string | number)[] | string | number | null;
    onChange: (value: any) => void;
    placeholder?: string;
    className?: string;
    disabled?: boolean;
    loading?: boolean;
    error?: boolean;
    noOptionsMessage?: string;
    searchPlaceholder?: string;
    isMultiple?: boolean;
    isSearchable?: boolean;
}

const CustomSelect: React.FC<CustomSelectProps> = ({
                                                       options = [],
                                                       value,
                                                       onChange,
                                                       placeholder = "Sélectionnez...",
                                                       className = "",
                                                       disabled = false,
                                                       loading = false,
                                                       error = false,
                                                       noOptionsMessage = "Aucune option trouvée",
                                                       searchPlaceholder = "Rechercher...",
                                                       isMultiple = false,
                                                       isSearchable = true
                                                   }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const dropdownRef = useRef<HTMLDivElement>(null);

    const normalizedValue = isMultiple
        ? Array.isArray(value) ? value : []
        : value;

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false);
                setSearchTerm(''); // Reset search when closing
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    useEffect(() => {
        const handleEscape = (event: KeyboardEvent) => {
            if (event.key === 'Escape') {
                setIsOpen(false);
                setSearchTerm('');
            }
        };
        document.addEventListener('keydown', handleEscape);
        return () => document.removeEventListener('keydown', handleEscape);
    }, []);

    const getAvailableOptions = () => {
        let availableOptions = options;

        if (isMultiple && Array.isArray(normalizedValue)) {
            availableOptions = options.filter(option => !normalizedValue.includes(option.value));
        } else if (!isMultiple && normalizedValue !== null && normalizedValue !== undefined) {
            availableOptions = options.filter(option => option.value !== normalizedValue);
        }

        return availableOptions;
    };

    const filteredOptions = (() => {
        const availableOptions = getAvailableOptions();

        return isSearchable
            ? availableOptions.filter(option =>
                option.label.toLowerCase().includes(searchTerm.toLowerCase())
            )
            : availableOptions;
    })();

    const handleOptionClick = (option: Option) => {
        if (disabled) return;

        if (isMultiple) {
            const currentValue = Array.isArray(normalizedValue) ? normalizedValue : [];
            const newValue = [...currentValue, option.value];
            onChange(newValue);
        } else {
            onChange(option.value);
            setIsOpen(false);
            setSearchTerm('');
        }
    };

    const handleRemoveItem = (valueToRemove: string | number, event: React.MouseEvent) => {
        event.stopPropagation();
        if (disabled || !isMultiple) return;
        const currentValue = Array.isArray(normalizedValue) ? normalizedValue : [];
        onChange(currentValue.filter(v => v !== valueToRemove));
    };

    const handleClearAll = (event: React.MouseEvent) => {
        event.stopPropagation();
        if (disabled) return;
        onChange(isMultiple ? [] : null);
    };

    const selectedOptions = isMultiple
        ? options.filter(option => Array.isArray(normalizedValue) && normalizedValue.includes(option.value))
        : options.filter(option => normalizedValue === option.value);

    const containerClasses = `
        relative w-full
        ${className}
    `.trim();

    const inputClasses = `
        border rounded-lg px-4 py-2 cursor-pointer bg-white min-h-[32px] 
        flex items-center justify-between transition-all duration-200
        focus:outline-none focus:ring focus:ring-indigo-500 focus:border-indigo-500
        ${error ? 'border-red-300 focus:ring-red-500 focus:border-red-500' : 'border-gray-300'}
        ${disabled ? 'bg-gray-50 cursor-not-allowed opacity-60' : 'hover:border-gray-400'}
        ${isOpen ? 'ring ring-indigo-500 border-indigo-500' : ''}
    `.trim();

    const dropdownClasses = `
        absolute z-50 w-full mt-2 bg-white border border-gray-200 rounded-lg 
        shadow-lg max-h-64 overflow-hidden animate-in slide-in-from-top-2 duration-200
    `.trim();

    return (
        <div className={containerClasses} ref={dropdownRef}>
            {/* Input principal */}
            <div
                className={inputClasses}
                onClick={() => !disabled && setIsOpen(!isOpen)}
                tabIndex={disabled ? -1 : 0}
                onKeyDown={(e) => {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        !disabled && setIsOpen(!isOpen);
                    }
                }}
            >
                <div className="flex flex-wrap gap-2 flex-1 min-w-0">
                    {selectedOptions.length > 0 ? (
                        isMultiple ? (
                            selectedOptions.map((option) => (
                                <span
                                    key={option.value}
                                    className="inline-flex items-center gap-1 bg-indigo-100 text-indigo-500 px-2 py-1 rounded-md text-sm"
                                >
                                    <span className="truncate max-w-[120px]">{option.label}</span>
                                    {!disabled && (
                                        <button
                                            type="button"
                                            onClick={(e) => handleRemoveItem(option.value, e)}
                                            className="hover:bg-red-100 hover:text-red-500 focus:outline-none p-1 ml-2 rounded h-full cursor-pointer  focus:text-indigo-600"
                                        >
                                            <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                                                <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                                            </svg>
                                        </button>
                                    )}
                                </span>
                            ))
                        ) : (
                            <span className="text-gray-900">{selectedOptions[0]?.label}</span>
                        )
                    ) : (
                        <span className="text-gray-500">{placeholder}</span>
                    )}
                </div>

                <div className="flex items-center gap-2 ml-2">
                    {/* Loading spinner */}
                    {loading && (
                        <svg className="animate-spin w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                            <path className="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                        </svg>
                    )}

                    {/* Clear all button */}
                    {selectedOptions.length > 0 && !disabled && !loading && (
                        <button
                            type="button"
                            onClick={handleClearAll}
                            className="text-gray-400 hover:text-gray-600 focus:outline-none focus:text-gray-600"
                        >
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                            </svg>
                        </button>
                    )}

                    {/* Dropdown arrow */}
                    <svg
                        className={`w-5 h-5 text-gray-400 transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`}
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                </div>
            </div>

            {/* Dropdown */}
            {isOpen && !disabled && (
                <div className={dropdownClasses}>
                    {/* Champ de recherche */}
                    {isSearchable && (
                        <div className="p-3 border-b border-gray-100">
                            <div className="relative">
                                <svg className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                </svg>
                                <input
                                    type="text"
                                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                    placeholder={searchPlaceholder}
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    onClick={(e) => e.stopPropagation()}
                                />
                            </div>
                        </div>
                    )}

                    {/* Liste des options */}
                    <div className="max-h-48 overflow-y-auto">
                        {filteredOptions.length > 0 ? (
                            filteredOptions.map((option) => (
                                <div
                                    key={option.value}
                                    className="px-4 py-3 cursor-pointer transition-colors duration-150 hover:bg-gray-50 text-gray-700"
                                    onClick={() => handleOptionClick(option)}
                                >
                                    <span className="truncate">{option.label}</span>
                                </div>
                            ))
                        ) : (
                            <div className="px-4 py-8 text-center text-gray-500">
                                <svg className="mx-auto w-8 h-8 text-gray-300 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.172 16.172a4 4 0 015.656 0M9 12h6m-6-4h6m2 5.291A7.962 7.962 0 0112 20a7.962 7.962 0 01-5-1.709M15 3H9a2 2 0 00-2 2v1.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 000 1.414l6.414 6.414A1 1 0 008 21.414V21a2 2 0 002 2h6a2 2 0 002-2v-.586a1 1 0 01.293-.707l6.414-6.414a1 1 0 000-1.414L18.293 7.293A1 1 0 0018 6.586V5a2 2 0 00-2-2z" />
                                </svg>
                                {noOptionsMessage}
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default CustomSelect;