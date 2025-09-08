"use client"

import * as React from "react"
import { cn } from "@/lib/utils"

export interface IconProps extends React.SVGAttributes<SVGElement> {
  size?: number
}

const Icon = React.forwardRef<SVGSVGElement, IconProps>(
  ({ className, size = 16, ...props }, ref) => {
    return (
      <svg
        ref={ref}
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className={cn("flex-shrink-0", className)}
        {...props}
      />
    )
  }
)
Icon.displayName = "Icon"

export { Icon }