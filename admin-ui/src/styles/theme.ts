import { createTheme, ThemeOptions } from '@mui/material/styles';
import type {} from '@mui/x-data-grid/themeAugmentation';

// FutureIM.com Golden Yellow Theme
const futureImColors = {
  primary: {
    main: '#FFD700', // Golden Yellow
    light: '#FFED4E',
    dark: '#B8860B',
    contrastText: '#000000',
  },
  secondary: {
    main: '#FFA500', // Orange
    light: '#FFB84D',
    dark: '#CC8400',
    contrastText: '#000000',
  },
  background: {
    default: '#FFFACD', // Light Golden Yellow
    paper: '#FFFFFF',
    dark: '#F5F5DC', // Beige
  },
  text: {
    primary: '#2C2C2C',
    secondary: '#5A5A5A',
    disabled: '#9E9E9E',
  },
  success: {
    main: '#4CAF50',
    light: '#81C784',
    dark: '#388E3C',
  },
  warning: {
    main: '#FF9800',
    light: '#FFB74D',
    dark: '#F57C00',
  },
  error: {
    main: '#F44336',
    light: '#EF5350',
    dark: '#D32F2F',
  },
  info: {
    main: '#2196F3',
    light: '#64B5F6',
    dark: '#1976D2',
  },
};

const baseTheme: ThemeOptions = {
  palette: {
    primary: futureImColors.primary,
    secondary: futureImColors.secondary,
    background: futureImColors.background,
    text: futureImColors.text,
    success: futureImColors.success,
    warning: futureImColors.warning,
    error: futureImColors.error,
    info: futureImColors.info,
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontSize: '2.5rem',
      fontWeight: 600,
      color: futureImColors.text.primary,
    },
    h2: {
      fontSize: '2rem',
      fontWeight: 600,
      color: futureImColors.text.primary,
    },
    h3: {
      fontSize: '1.75rem',
      fontWeight: 600,
      color: futureImColors.text.primary,
    },
    h4: {
      fontSize: '1.5rem',
      fontWeight: 600,
      color: futureImColors.text.primary,
    },
    h5: {
      fontSize: '1.25rem',
      fontWeight: 600,
      color: futureImColors.text.primary,
    },
    h6: {
      fontSize: '1rem',
      fontWeight: 600,
      color: futureImColors.text.primary,
    },
    body1: {
      fontSize: '1rem',
      color: futureImColors.text.primary,
    },
    body2: {
      fontSize: '0.875rem',
      color: futureImColors.text.secondary,
    },
  },
  shape: {
    borderRadius: 8,
  },
  spacing: 8,
};

export const lightTheme = createTheme({
  ...baseTheme,
  palette: {
    ...baseTheme.palette,
    mode: 'light',
  },
  components: {
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: futureImColors.primary.main,
          color: futureImColors.primary.contrastText,
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: futureImColors.background.paper,
          borderRight: `1px solid ${futureImColors.primary.light}`,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
          borderRadius: 12,
          border: `1px solid ${futureImColors.primary.light}`,
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          borderRadius: 8,
        },
        contained: {
          boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
          '&:hover': {
            boxShadow: '0 4px 8px rgba(0,0,0,0.3)',
          },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 16,
        },
        colorPrimary: {
          backgroundColor: futureImColors.primary.light,
          color: futureImColors.primary.contrastText,
        },
      },
    },
    MuiDataGrid: {
      styleOverrides: {
        root: {
          border: `1px solid ${futureImColors.primary.light}`,
          '& .MuiDataGrid-columnHeaders': {
            backgroundColor: futureImColors.primary.main,
            color: futureImColors.primary.contrastText,
          },
          '& .MuiDataGrid-cell': {
            borderRight: `1px solid ${futureImColors.background.dark}`,
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: futureImColors.primary.light,
            },
            '&:hover fieldset': {
              borderColor: futureImColors.primary.main,
            },
            '&.Mui-focused fieldset': {
              borderColor: futureImColors.primary.dark,
            },
          },
        },
      },
    },
    MuiTabs: {
      styleOverrides: {
        root: {
          '& .MuiTabs-indicator': {
            backgroundColor: futureImColors.primary.dark,
          },
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          backgroundColor: futureImColors.primary.light,
          '& .MuiLinearProgress-bar': {
            backgroundColor: futureImColors.primary.dark,
          },
        },
      },
    },
  },
});

export const darkTheme = createTheme({
  ...baseTheme,
  palette: {
    ...baseTheme.palette,
    mode: 'dark',
    background: {
      default: '#1a1a1a',
      paper: '#2d2d2d',
    },
    text: {
      primary: '#ffffff',
      secondary: '#cccccc',
      disabled: '#888888',
    },
  },
  components: {
    ...lightTheme.components,
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#2d2d2d',
          color: futureImColors.primary.main,
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: '#2d2d2d',
          borderRight: `1px solid ${futureImColors.primary.dark}`,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#2d2d2d',
          border: `1px solid ${futureImColors.primary.dark}`,
        },
      },
    },
  },
});

export default lightTheme;