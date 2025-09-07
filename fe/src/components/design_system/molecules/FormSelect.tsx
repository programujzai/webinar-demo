import { forwardRef } from 'react';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './select';
import { Label } from '../atoms/label';

interface SelectOption {
  value: string;
  label: string;
}

interface FormSelectProps {
  label?: string;
  placeholder?: string;
  options: SelectOption[];
  value?: string;
  onValueChange?: (value: string) => void;
  error?: string;
  helperText?: string;
  required?: boolean;
  disabled?: boolean;
}

export const FormSelect = forwardRef<HTMLDivElement, FormSelectProps>(
  ({ label, placeholder, options, value, onValueChange, error, helperText, required, disabled }, ref) => {
    const selectId = `select-${Math.random().toString(36).substr(2, 9)}`;
    
    return (
      <div className="space-y-2" ref={ref}>
        {label && (
          <Label htmlFor={selectId}>
            {label}
            {required && <span className="text-destructive ml-1">*</span>}
          </Label>
        )}
        <Select value={value} onValueChange={onValueChange} disabled={disabled}>
          <SelectTrigger 
            id={selectId}
            className={error ? "border-destructive focus:ring-destructive" : ""}
          >
            <SelectValue placeholder={placeholder} />
          </SelectTrigger>
          <SelectContent>
            {options.map((option) => (
              <SelectItem key={option.value} value={option.value}>
                {option.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        {error && (
          <p className="text-sm text-destructive">
            {error}
          </p>
        )}
        {helperText && !error && (
          <p className="text-sm text-muted-foreground">
            {helperText}
          </p>
        )}
      </div>
    );
  }
);

FormSelect.displayName = 'FormSelect';