"use client"

import * as React from "react"
import { format } from 'date-fns';
import { Calendar as CalendarIcon } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '../atoms/Button';
import { Calendar } from '../atoms/Calendar';
import { Popover, PopoverContent, PopoverTrigger } from './popover';
import { Label } from '../atoms/label';

interface DatePickerProps {
  label?: string;
  value?: Date;
  onChange?: (date: Date | undefined) => void;
  placeholder?: string;
  error?: string;
  helperText?: string;
  required?: boolean;
  disabled?: boolean;
  minDate?: Date;
  maxDate?: Date;
  className?: string;
  formatStr?: string;
  id?: string;
  name?: string;
}

export function DatePicker({
  label,
  value,
  onChange,
  placeholder = "Pick a date",
  error,
  helperText,
  required,
  disabled,
  minDate,
  maxDate,
  className,
  formatStr = "PPP",
  id,
  name,
}: DatePickerProps) {
  const generatedId = React.useId();
  const pickerId = id || generatedId;
  const [open, setOpen] = React.useState(false);
  
  const handleSelect = React.useCallback((date: Date | undefined) => {
    onChange?.(date);
    setOpen(false);
  }, [onChange]);
  
  return (
    <div className={cn("flex flex-col gap-2", className)}>
      {label && (
        <Label 
          htmlFor={pickerId}
          className={cn(
            "text-sm font-medium leading-none",
            disabled && "cursor-not-allowed opacity-70",
            error && "text-red-500"
          )}
        >
          {label}
          {required && <span className="text-red-500 ml-0.5">*</span>}
        </Label>
      )}
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            id={pickerId}
            name={name}
            type="button"
            variant="outline"
            className={cn(
              "w-full justify-start text-left font-normal",
              "h-10 px-3 py-2",
              "bg-white hover:bg-gray-50",
              "border border-gray-300 hover:border-gray-400",
              "transition-colors duration-200",
              !value && "text-gray-500",
              error && "border-red-500 focus:ring-red-500 focus:border-red-500",
              disabled && "cursor-not-allowed opacity-50 bg-gray-50",
            )}
            disabled={disabled}
            aria-invalid={!!error}
            aria-describedby={
              error ? `${pickerId}-error` : 
              helperText ? `${pickerId}-helper` : 
              undefined
            }
            aria-label={label || "Select date"}
            aria-haspopup="dialog"
            aria-expanded={open}
          >
            <CalendarIcon className="mr-2 h-4 w-4 shrink-0 text-gray-400" />
            {value ? (
              <span className="truncate text-gray-900">{format(value, formatStr)}</span>
            ) : (
              <span className="truncate">{placeholder}</span>
            )}
          </Button>
        </PopoverTrigger>
        <PopoverContent 
          className="w-auto p-0 bg-white rounded-lg shadow-xl border border-gray-200" 
          align="start"
        >
          <Calendar
            mode="single"
            selected={value}
            onSelect={handleSelect}
            disabled={(date) => {
              if (disabled) return true;
              
              if (minDate && date < minDate) return true;
              if (maxDate && date > maxDate) return true;
              
              return false;
            }}
            initialFocus
            className="rounded-lg"
          />
        </PopoverContent>
      </Popover>
      
      {error && (
        <p 
          id={`${pickerId}-error`}
          className="text-sm text-red-500 mt-1"
          role="alert"
        >
          {error}
        </p>
      )}
      
      {helperText && !error && (
        <p 
          id={`${pickerId}-helper`}
          className="text-sm text-gray-500 mt-1"
        >
          {helperText}
        </p>
      )}
    </div>
  );
}