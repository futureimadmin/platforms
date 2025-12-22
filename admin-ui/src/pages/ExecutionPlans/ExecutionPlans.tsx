import React, { useState, useEffect } from 'react';
import {
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  List,
  ListItem,
  ListItemText,
  ListItemButton,
  Chip,
  CircularProgress,
  Alert,
  Divider,
  Paper,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Refresh as RefreshIcon,
  Visibility as VisibilityIcon,
  PlayArrow as PlayArrowIcon,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import { apiService } from '../../services/api';
import { ExecutionPlan, ExecutionFlowResponse, ExecutionStepInput } from '../../types';
import ExecutionFlowViewer from '../../components/ExecutionFlowViewer';

const ExecutionPlans: React.FC = () => {
  const [executionPlans, setExecutionPlans] = useState<ExecutionPlan[]>([]);
  const [selectedPlan, setSelectedPlan] = useState<ExecutionPlan | null>(null);
  const [selectedFlowData, setSelectedFlowData] = useState<ExecutionFlowResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [flowLoading, setFlowLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchExecutionPlans = async () => {
    try {
      setLoading(true);
      setError(null);
      const plans = await apiService.getExecutionPlans();
      setExecutionPlans(plans);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch execution plans';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const fetchExecutionFlow = async (planId: string) => {
    try {
      setFlowLoading(true);
      const flowData = await apiService.getExecutionFlow(planId);
      setSelectedFlowData(flowData);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch execution flow';
      toast.error(errorMessage);
    } finally {
      setFlowLoading(false);
    }
  };

  const handlePlanSelect = async (plan: ExecutionPlan) => {
    setSelectedPlan(plan);
    setSelectedFlowData(null);
    await fetchExecutionFlow(plan.planId);
  };

  const handleExecuteStep = async (stepId: string, inputs: ExecutionStepInput) => {
    try {
      // For now, just show a success message
      // In the future, this would trigger actual step execution
      toast.success(`Step ${stepId} execution initiated with provided inputs`);
      console.log('Executing step:', stepId, 'with inputs:', inputs);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to execute step';
      toast.error(errorMessage);
    }
  };

  const handleExecuteFlow = async (planId: string) => {
    try {
      const response = await apiService.executeExecutionPlan(planId);
      if (response.success) {
        toast.success(response.message || 'Execution plan started successfully');
      } else {
        toast.error(response.message || 'Failed to start execution plan');
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to execute plan';
      toast.error(errorMessage);
    }
  };

  useEffect(() => {
    fetchExecutionPlans();
  }, []);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Execution Plans
        </Typography>
        <Tooltip title="Refresh execution plans">
          <IconButton onClick={fetchExecutionPlans} disabled={loading}>
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Execution Plans List */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Available Plans ({executionPlans.length})
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              {executionPlans.length === 0 ? (
                <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
                  No execution plans found. Create a plan using the prompt interface.
                </Typography>
              ) : (
                <List>
                  {executionPlans.map((plan) => (
                    <ListItem key={plan.planId} disablePadding>
                      <ListItemButton
                        selected={selectedPlan?.planId === plan.planId}
                        onClick={() => handlePlanSelect(plan)}
                      >
                        <ListItemText
                          primary={
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                              <Typography variant="subtitle2">
                                {plan.metadata.name}
                              </Typography>
                              <Chip
                                label={plan.version}
                                size="small"
                                color="primary"
                                variant="outlined"
                              />
                            </Box>
                          }
                          secondary={
                            <Box>
                              <Typography variant="caption" color="text.secondary">
                                {plan.metadata.description}
                              </Typography>
                              <br />
                              <Typography variant="caption" color="text.secondary">
                                Created: {formatDate(plan.metadata.createdAt)}
                              </Typography>
                              <br />
                              <Typography variant="caption" color="text.secondary">
                                By: {plan.metadata.createdBy}
                              </Typography>
                            </Box>
                          }
                        />
                      </ListItemButton>
                    </ListItem>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Execution Flow Viewer */}
        <Grid item xs={12} md={8}>
          {selectedPlan ? (
            <Box>
              {flowLoading ? (
                <Paper sx={{ p: 4, textAlign: 'center' }}>
                  <CircularProgress />
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                    Loading execution flow...
                  </Typography>
                </Paper>
              ) : selectedFlowData ? (
                <ExecutionFlowViewer
                  flowData={selectedFlowData}
                  onExecuteStep={handleExecuteStep}
                  onExecuteFlow={handleExecuteFlow}
                />
              ) : (
                <Paper sx={{ p: 4, textAlign: 'center' }}>
                  <Typography variant="body1" color="text.secondary">
                    Failed to load execution flow data
                  </Typography>
                </Paper>
              )}
            </Box>
          ) : (
            <Paper sx={{ p: 4, textAlign: 'center' }}>
              <VisibilityIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                Select an Execution Plan
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Choose a plan from the list to view its execution flow and configure step inputs.
              </Typography>
            </Paper>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

export default ExecutionPlans;