import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { lightTheme, darkTheme } from './styles/theme';
import { ThemeMode } from './types';
import Layout from './components/Layout/Layout';
import Dashboard from './pages/Dashboard/Dashboard';
import AgentManagement from './pages/AgentManagement/AgentManagement';
import ExecutionPlans from './pages/ExecutionPlans/ExecutionPlans';
import ExecutionMonitoring from './pages/ExecutionMonitoring/ExecutionMonitoring';
import TeamsIntegration from './pages/TeamsIntegration/TeamsIntegration';
import Configuration from './pages/Configuration/Configuration';
import Analytics from './pages/Analytics/Analytics';

// Create a client for React Query
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

const App: React.FC = () => {
  const [themeMode, setThemeMode] = useState<ThemeMode>(ThemeMode.LIGHT);

  const toggleTheme = () => {
    setThemeMode(prev => prev === ThemeMode.LIGHT ? ThemeMode.DARK : ThemeMode.LIGHT);
  };

  const currentTheme = themeMode === ThemeMode.LIGHT ? lightTheme : darkTheme;

  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={currentTheme}>
        <CssBaseline />
        <Router>
          <Layout themeMode={themeMode} onToggleTheme={toggleTheme}>
            <Routes>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/agents" element={<AgentManagement />} />
              <Route path="/execution-plans" element={<ExecutionPlans />} />
              <Route path="/monitoring" element={<ExecutionMonitoring />} />
              <Route path="/teams" element={<TeamsIntegration />} />
              <Route path="/configuration" element={<Configuration />} />
              <Route path="/analytics" element={<Analytics />} />
              <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </Layout>
        </Router>
        <ToastContainer
          position="top-right"
          autoClose={5000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme={themeMode === ThemeMode.LIGHT ? 'light' : 'dark'}
        />
      </ThemeProvider>
    </QueryClientProvider>
  );
};

export default App;