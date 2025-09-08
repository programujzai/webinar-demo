/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: 'var(--color-primary-50)',
          100: 'var(--color-primary-100)',
          200: 'var(--color-primary-200)',
          300: 'var(--color-primary-300)',
          400: 'var(--color-primary-400)',
          500: 'var(--color-primary-500)',
          600: 'var(--color-primary-600)',
          700: 'var(--color-primary-700)',
          800: 'var(--color-primary-800)',
          900: 'var(--color-primary-900)',
          950: 'var(--color-primary-950)',
        },
        dark: 'var(--color-dark)',
        border: '#e5e7eb',
        ring: '#3b82f6',
        background: '#ffffff',
        foreground: '#111827',
      },
      fontFamily: {
        primary: ['var(--font-primary)'],
      },
      animation: {
        flicker: 'var(--animate-flicker)',
        shimmer: 'var(--animate-shimmer)',
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}