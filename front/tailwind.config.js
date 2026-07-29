/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Bleu "froid" -> identité climatisation
        brand: {
          50: '#eff8ff',
          100: '#dbeefe',
          200: '#bfe2fe',
          300: '#93d0fd',
          400: '#60b6fa',
          500: '#3b98f6',
          600: '#2579eb',
          700: '#1d62d8',
          800: '#1e51af',
          900: '#1e468a',
          950: '#172c54',
        },
        // Ambre "chaud" -> identité chauffage
        heat: {
          50: '#fff8ed',
          100: '#ffefd4',
          200: '#ffdaa8',
          300: '#ffc071',
          400: '#ff9b38',
          500: '#fe7d12',
          600: '#ef6008',
          700: '#c64709',
          800: '#9d3810',
          900: '#7e3010',
          950: '#441606',
        },
        ink: {
          50: '#f6f7f9',
          100: '#eceef2',
          200: '#d5dae3',
          300: '#b0bbcb',
          400: '#8596ae',
          500: '#667894',
          600: '#51617b',
          700: '#424f64',
          800: '#3a4354',
          900: '#333b48',
          950: '#22272f',
        },
      },
      fontFamily: {
        sans: [
          'Inter',
          'ui-sans-serif',
          'system-ui',
          '-apple-system',
          'Segoe UI',
          'Roboto',
          'Helvetica Neue',
          'Arial',
          'sans-serif',
        ],
        mono: ['ui-monospace', 'SFMono-Regular', 'Menlo', 'Consolas', 'monospace'],
      },
      boxShadow: {
        card: '0 1px 2px 0 rgb(16 24 40 / 0.04), 0 1px 3px 0 rgb(16 24 40 / 0.06)',
        pop: '0 12px 32px -8px rgb(16 24 40 / 0.18), 0 4px 10px -4px rgb(16 24 40 / 0.10)',
      },
      keyframes: {
        'fade-in': {
          from: { opacity: '0', transform: 'translateY(4px)' },
          to: { opacity: '1', transform: 'translateY(0)' },
        },
        'slide-in-right': {
          from: { opacity: '0', transform: 'translateX(16px)' },
          to: { opacity: '1', transform: 'translateX(0)' },
        },
        'scale-in': {
          from: { opacity: '0', transform: 'scale(.97)' },
          to: { opacity: '1', transform: 'scale(1)' },
        },
      },
      animation: {
        'fade-in': 'fade-in .18s ease-out',
        'slide-in-right': 'slide-in-right .22s cubic-bezier(.16,1,.3,1)',
        'scale-in': 'scale-in .16s ease-out',
      },
    },
  },
  plugins: [],
};
