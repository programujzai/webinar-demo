"use client"

import * as React from "react"
import { format } from 'date-fns';
import { Calendar as CalendarIcon, ChevronLeft, ChevronRight } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '../atoms/Button';
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
  const [currentMonth, setCurrentMonth] = React.useState(() => {
    const date = value || new Date();
    return new Date(date.getFullYear(), date.getMonth(), 1);
  });
  
  const handleSelect = React.useCallback((date: Date) => {
    onChange?.(date);
    setOpen(false);
  }, [onChange]);
  
  const getDaysInMonth = (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();
    
    const days: (Date | null)[] = [];
    
    // Add empty cells for days before month starts
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }
    
    // Add all days of the month
    for (let i = 1; i <= daysInMonth; i++) {
      days.push(new Date(year, month, i));
    }
    
    return days;
  };
  
  const isDateDisabled = (date: Date) => {
    if (disabled) return true;
    if (minDate && date < minDate) return true;
    if (maxDate && date > maxDate) return true;
    return false;
  };
  
  const isToday = (date: Date) => {
    const today = new Date();
    return date.getDate() === today.getDate() &&
           date.getMonth() === today.getMonth() &&
           date.getFullYear() === today.getFullYear();
  };
  
  const isSelected = (date: Date) => {
    if (!value) return false;
    return date.getDate() === value.getDate() &&
           date.getMonth() === value.getMonth() &&
           date.getFullYear() === value.getFullYear();
  };
  
  const goToPreviousMonth = () => {
    setCurrentMonth(prev => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
  };
  
  const goToNextMonth = () => {
    setCurrentMonth(prev => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
  };
  
  const days = getDaysInMonth(currentMonth);
  const weekDays = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];
  
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
          className="w-auto p-3 bg-white rounded-lg shadow-xl border border-gray-200" 
          align="start"
        >
          <div className="space-y-3">
            {/* Month navigation */}
            <div className="flex items-center justify-between px-1">
              <Button
                type="button"
                onClick={goToPreviousMonth}
                variant="ghost"
                size="sm"
                className="h-7 w-7 p-0 hover:bg-gray-100"
                aria-label="Previous month"
              >
                <ChevronLeft className="h-4 w-4" />
              </Button>
              
              <h3 className="text-sm font-semibold text-gray-900">
                {format(currentMonth, 'MMMM yyyy')}
              </h3>
              
              <Button
                type="button"
                onClick={goToNextMonth}
                variant="ghost"
                size="sm"
                className="h-7 w-7 p-0 hover:bg-gray-100"
                aria-label="Next month"
              >
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
            
            {/* Calendar grid */}
            <div className="grid grid-cols-7 gap-0">
              {/* Week days header */}
              {weekDays.map(day => (
                <div
                  key={day}
                  className="text-xs font-medium text-gray-500 text-center py-1.5 px-1"
                >
                  {day}
                </div>
              ))}
              
              {/* Calendar days */}
              {days.map((date, index) => {
                if (!date) {
                  return <div key={`empty-${index}`} className="h-9 w-9" />;
                }
                
                const isDisabled = isDateDisabled(date);
                const selected = isSelected(date);
                const today = isToday(date);
                
                return (
                  <Button
                    key={date.toISOString()}
                    type="button"
                    onClick={() => !isDisabled && handleSelect(date)}
                    disabled={isDisabled}
                    variant={selected ? "default" : "ghost"}
                    size="sm"
                    className={cn(
                      "h-9 w-9 p-0 font-normal",
                      "hover:bg-gray-100 hover:text-gray-900",
                      "focus-visible:ring-1 focus-visible:ring-gray-400",
                      isDisabled && "opacity-50 cursor-not-allowed hover:bg-transparent text-gray-300",
                      selected && "bg-blue-600 text-white hover:bg-blue-700 focus-visible:ring-blue-600",
                      today && !selected && "bg-blue-50 text-blue-700 font-semibold border border-blue-200",
                      !isDisabled && !selected && !today && "text-gray-700"
                    )}
                    aria-label={format(date, 'EEEE, MMMM do, yyyy')}
                    aria-selected={selected}
                    aria-disabled={isDisabled}
                  >
                    {date.getDate()}
                  </Button>
                );
              })}
            </div>
            
            {/* Today button */}
            <div className="flex justify-center pt-2 border-t border-gray-100">
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => {
                  const today = new Date();
                  setCurrentMonth(new Date(today.getFullYear(), today.getMonth(), 1));
                  if (!isDateDisabled(today)) {
                    handleSelect(today);
                  }
                }}
                className="text-xs h-7"
              >
                Today
              </Button>
            </div>
          </div>
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