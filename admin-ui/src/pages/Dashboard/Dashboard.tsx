import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Button,
  TextField,
  Paper,
  Chip,
  LinearProgress,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  SmartToy as AgentIcon,
  PlayArrow as PlayIcon,
  Stop as StopIcon,
  Refresh as RefreshIcon,
  Add as AddIcon,
  TrendingUp,
  TrendingDown,
  Schedule,
  CheckCircle,
  Error,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';

import apiService from '../../services/api';
import { ProcessPromptRequest, DashboardStats, ExecutionPlanStatus, ExecutionStatus } from '../../types';

const Dashboard: React.FC = () => {
  const [promptText, setPromptText] = useState('');
  const [promptDialogOpen, setPromptDialogOpen] = useState(false);
  const queryClient = useQueryClient();

  // Fetch dashboard stats
  const { data: stats, isLoading: statsLoading } = useQuery(
    'dashboardStats',
    apiService.getDashboardStats,
    {
      refetchInterval: 30000, // Refresh every 30 seconds
    }
  );

  // Fetch active executions
  const { data: activeExecutions, isLoading: executionsLoading } = useQuery(
    'activeExecutions',
    apiService.getActiveExecutions,
    {
      refetchInterval: 5000, // Refresh every 5 seconds
    }
  );

  // Process prompt mutation
  const processPromptMutation = useMutation(
    (request: ProcessPromptRequest) => apiService.processPrompt(request),
    {
      onSuccess: (data) => {
        toast.success('Prompt processed successfully!');
        setPromptText('');
        setPromptDialogOpen(false);
        queryClient.invalidateQueries('activeExecutions');
        queryClient.invalidateQueries('dashboardStats');
      },
      onError: (error: any) => {
        toast.error(`Error processing prompt: ${error.message}`);
      },
    }
  );

  const handleProcessPrompt = () => {
    if (!promptText.trim()) {
      toast.error('Please enter a prompt');
      return;
    }

    processPromptMutation.mutate({
      prompt: promptText,
      context: {
        source: 'dashboard',
        timestamp: new Date().toISOString(),
      },
    });
  };

  const getStatusColor = (status: ExecutionStatus) => {
    switch (status) {
      case ExecutionStatus.RUNNING:
        return 'primary';
      case ExecutionStatus.COMPLETED:
        return 'success';
      case ExecutionStatus.FAILED:
        return 'error';
      case ExecutionStatus.PAUSED:
        return 'warning';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status: ExecutionStatus) => {
    switch (status) {
      case ExecutionStatus.RUNNING:
        return <PlayIcon />;
      case ExecutionStatus.COMPLETED:
        return <CheckCircle />;
      case ExecutionStatus.FAILED:
        return <Error />;
      case ExecutionStatus.PAUSED:
        return <Schedule />;
      default:
        return <Schedule />;
    }
  };

  // Mock data for charts
  const executionTrendData = [
    { name: 'Mon', executions: 12, success: 10, failed: 2 },
    { name: 'Tue', executions: 19, success: 17, failed: 2 },
    { name: 'Wed', executions: 15, success: 13, failed: 2 },
    { name: 'Thu', executions: 22, success: 20, failed: 2 },
    { name: 'Fri', executions: 18, success: 16, failed: 2 },
    { name: 'Sat', executions: 8, success: 7, failed: 1 },
    { name: 'Sun', executions: 5, success: 5, failed: 0 },
  ];

  const agentUtilizationData = [
    { name: 'Data Agents', value: 45 },
    { name: 'Tool Agents', value: 30 },
    { name: 'Control Agents', value: 15 },
    { name: 'Human Interface', value: 10 },
  ];

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Nebula Dashboard
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setPromptDialogOpen(true)}
            size="large"
          >
            Create New Execution
          </Button>
          <Tooltip title="Refresh Data">
            <IconButton
              onClick={() => {
                queryClient.invalidateQueries('dashboardStats');
                queryClient.invalidateQueries('activeExecutions');
              }}
            >
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Agents
                  </Typography>
                  <Typography variant="h4">
                    {statsLoading ? '-' : stats?.totalAgents || 0}
                  </Typography>
                </Box>
                <AgentIcon color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Active Executions
                  </Typography>
                  <Typography variant="h4">
                    {statsLoading ? '-' : stats?.activeExecutions || 0}
                  </Typography>
                </Box>
                <PlayIcon color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Completed Today
                  </Typography>
                  <Typography variant="h4">
                    {statsLoading ? '-' : stats?.completedExecutions || 0}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                    <TrendingUp color="success" sx={{ fontSize: 16, mr: 0.5 }} />
                    <Typography variant="body2" color="success.main">
                      +12%
                    </Typography>
                  </Box>
                </Box>
                <CheckCircle color="success" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Failed Today
                  </Typography>
                  <Typography variant="h4">
                    {statsLoading ? '-' : stats?.failedExecutions || 0}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                    <TrendingDown color="error" sx={{ fontSize: 16, mr: 0.5 }} />
                    <Typography variant="body2" color="error.main">
                      -5%
                    </Typography>
                  </Box>
                </Box>
                <Error color="error" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Execution Trends (Last 7 Days)
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={executionTrendData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <RechartsTooltip />
                  <Line type="monotone" dataKey="executions" stroke="#FFD700" strokeWidth={2} />
                  <Line type="monotone" dataKey="success" stroke="#4CAF50" strokeWidth={2} />
                  <Line type="monotone" dataKey="failed" stroke="#F44336" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Agent Utilization
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={agentUtilizationData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <RechartsTooltip />
                  <Bar dataKey="value" fill="#FFD700" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Active Executions */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Active Executions
          </Typography>
          {executionsLoading ? (
            <LinearProgress />
          ) : (
            <Grid container spacing={2}>
              {activeExecutions?.map((execution) => (
                <Grid item xs={12} md={6} lg={4} key={execution.planId}>
                  <Paper sx={{ p: 2, border: '1px solid', borderColor: 'divider' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="subtitle1" noWrap>
                        {execution.planId}
                      </Typography>
                      <Chip
                        icon={getStatusIcon(execution.status)}
                        label={execution.status}
                        color={getStatusColor(execution.status)}
                        size="small"
                      />
                    </Box>
                    
                    <Typography variant="body2" color="textSecondary" gutterBottom>
                      Progress: {execution.completedSteps}/{execution.totalSteps} steps
                    </Typography>
                    
                    <LinearProgress
                      variant="determinate"
                      value={(execution.completedSteps / execution.totalSteps) * 100}
                      sx={{ mb: 1 }}
                    />
                    
                    <Typography variant="body2" color="textSecondary" gutterBottom>
                      Agents: {execution.activeAgents}/{execution.totalAgents} active
                    </Typography>
                    
                    {execution.currentStep && (
                      <Typography variant="body2" color="textSecondary">
                        Current: {execution.currentStep}
                      </Typography>
                    )}
                    
                    <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
                      <Button size="small" startIcon={<StopIcon />}>
                        Stop
                      </Button>
                      <Button size="small" variant="outlined">
                        View Details
                      </Button>
                    </Box>
                  </Paper>
                </Grid>
              ))}
              
              {!activeExecutions?.length && (
                <Grid item xs={12}>
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="body1" color="textSecondary">
                      No active executions
                    </Typography>
                  </Box>
                </Grid>
              )}
            </Grid>
          )}
        </CardContent>
      </Card>

      {/* Create Execution Dialog */}
      <Dialog
        open={promptDialogOpen}
        onClose={() => setPromptDialogOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Create New Execution</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Describe what you want to accomplish"
            fullWidth
            multiline
            rows={4}
            variant="outlined"
            value={promptText}
            onChange={(e) => setPromptText(e.target.value)}
            placeholder="e.g., Create a data pipeline to process customer orders and send notifications..."
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPromptDialogOpen(false)}>
            Cancel
          </Button>
          <Button
            onClick={handleProcessPrompt}
            variant="contained"
            disabled={processPromptMutation.isLoading || !promptText.trim()}
          >
            {processPromptMutation.isLoading ? 'Processing...' : 'Create Execution'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Dashboard;