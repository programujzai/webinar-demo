"use client"

import * as React from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { DayPicker } from "react-day-picker"

import { cn } from "@/lib/utils"
import { buttonVariants } from "@/components/design_system/atoms/Button"

export type CalendarProps = React.ComponentProps<typeof DayPicker>

function Calendar({
  className,
  classNames,
  showOutsideDays = true,
  ...props
}: CalendarProps) {
  return (
    <DayPicker
      showOutsideDays={showOutsideDays}
      className={cn("p-4", className)}
      classNames={{
        months: "flex flex-col sm:flex-row space-y-4 sm:space-x-4 sm:space-y-0",
        month: "space-y-4",
        caption: "flex justify-center pt-3 pb-4 relative items-center",
        caption_label: "text-lg font-bold text-gray-900 tracking-tight",
        nav: "space-x-1 flex items-center",
        nav_button: cn(
          buttonVariants({ variant: "outline" }),
          "h-8 w-8 bg-white p-0 opacity-100 hover:bg-gray-50 hover:border-gray-300 transition-all duration-200 border-gray-200 shadow-sm"
        ),
        nav_button_previous: "absolute left-1",
        nav_button_next: "absolute right-1",
        table: "w-full border-collapse space-y-1",
        head_row: "flex",
        head_cell:
          "text-gray-500 rounded-md w-10 font-semibold text-[0.75rem] uppercase tracking-wider text-center py-2",
        row: "flex w-full mt-1 gap-0.5",
        cell: "h-10 w-10 text-center text-sm p-0 relative [&:has([aria-selected].day-range-end)]:rounded-r-md [&:has([aria-selected].day-outside)]:bg-blue-50/50 [&:has([aria-selected])]:bg-blue-50 first:[&:has([aria-selected])]:rounded-l-md last:[&:has([aria-selected])]:rounded-r-md focus-within:relative focus-within:z-20",
        day: cn(
          "h-10 w-10 p-0 font-medium aria-selected:opacity-100 hover:bg-gray-50 hover:scale-105 hover:shadow-sm hover:border hover:border-gray-200 transition-all duration-200 rounded-lg text-gray-800 flex items-center justify-center cursor-pointer"
        ),
        day_range_end: "day-range-end",
        day_selected:
          "bg-blue-600 text-white hover:bg-blue-700 hover:text-white focus:bg-blue-700 focus:text-white font-semibold shadow-lg ring-2 ring-blue-200 ring-offset-1",
        day_today: "bg-blue-100 text-blue-700 font-bold border-2 border-blue-300 shadow-sm ring-1 ring-blue-200",
        day_outside:
          "day-outside text-gray-400 opacity-40 aria-selected:bg-blue-50/30 aria-selected:text-gray-400 aria-selected:opacity-40",
        day_disabled: "text-gray-300 opacity-50 cursor-not-allowed hover:bg-transparent hover:border-0",
        day_range_middle:
          "aria-selected:bg-blue-50 aria-selected:text-blue-900",
        day_hidden: "invisible",
        ...classNames,
      }}
      components={{
        IconLeft: ({ ...props }) => <ChevronLeft className="h-4 w-4 text-gray-600" />,
        IconRight: ({ ...props }) => <ChevronRight className="h-4 w-4 text-gray-600" />,
      }}
      {...props}
    />
  )
}

export { Calendar }
