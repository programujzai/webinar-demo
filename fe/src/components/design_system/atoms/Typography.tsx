import { cn } from '@/lib/utils';
import { forwardRef, HTMLAttributes } from 'react';

export type TypographyVariant = 
  | 'h1'
  | 'h2'
  | 'h3'
  | 'h4'
  | 'h5'
  | 'h6'
  | 'body'
  | 'body-sm'
  | 'caption'
  | 'overline';

interface TypographyProps extends HTMLAttributes<HTMLElement> {
  variant?: TypographyVariant;
  as?: keyof JSX.IntrinsicElements;
  weight?: 'normal' | 'medium' | 'semibold' | 'bold';
  color?: 'default' | 'muted' | 'primary' | 'error' | 'success';
}

const variantStyles: Record<TypographyVariant, string> = {
  h1: 'text-4xl font-bold leading-tight',
  h2: 'text-3xl font-bold leading-tight',
  h3: 'text-2xl font-semibold leading-tight',
  h4: 'text-xl font-semibold',
  h5: 'text-lg font-semibold',
  h6: 'text-base font-semibold',
  body: 'text-base',
  'body-sm': 'text-sm',
  caption: 'text-xs',
  overline: 'text-xs uppercase tracking-wider',
};

const weightStyles: Record<string, string> = {
  normal: 'font-normal',
  medium: 'font-medium',
  semibold: 'font-semibold',
  bold: 'font-bold',
};

const colorStyles: Record<string, string> = {
  default: 'text-foreground',
  muted: 'text-muted-foreground',
  primary: 'text-primary',
  error: 'text-destructive',
  success: 'text-green-600',
};

const defaultElements: Record<TypographyVariant, keyof JSX.IntrinsicElements> = {
  h1: 'h1',
  h2: 'h2',
  h3: 'h3',
  h4: 'h4',
  h5: 'h5',
  h6: 'h6',
  body: 'p',
  'body-sm': 'p',
  caption: 'span',
  overline: 'span',
};

export const Typography = forwardRef<HTMLElement, TypographyProps>(
  ({ 
    variant = 'body', 
    as,
    weight,
    color = 'default',
    className, 
    children, 
    ...props 
  }, ref) => {
    const Component = as || defaultElements[variant];
    
    return (
      <Component
        ref={ref as any}
        className={cn(
          variantStyles[variant],
          weight && weightStyles[weight],
          colorStyles[color],
          className
        )}
        {...props}
      >
        {children}
      </Component>
    );
  }
);

Typography.displayName = 'Typography';