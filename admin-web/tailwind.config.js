export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        dark: {
          900: '#0D0D14',
          800: '#13131F',
          750: '#181828',
          700: '#1E1E2E',
          600: '#252538',
          500: '#2E2E45',
          400: '#3D3D5C',
        },
        brand: {
          500: '#6C63FF',
          400: '#8B83FF',
          600: '#4D45E0',
        },
        accent: {
          500: '#00D4AA',
        }
      }
    },
  },
  plugins: [],
}
