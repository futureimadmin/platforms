# Nebula - AI Agent Creation Platform

Nebula is a sophisticated multi-agent system that leverages Large Language Models (LLMs) to dynamically create and orchestrate AI agents for complex task execution.

## 🌟 Overview

Nebula provides a comprehensive platform for creating, managing, and executing AI agents that can work together to accomplish complex tasks. The system uses a control plane/data plane architecture with Microsoft Teams integration for human-in-the-loop interactions.

## ✨ Key Features

- **🤖 Dynamic Agent Creation**: Uses LLMs (Gemini, Claude) to generate agents based on user prompts
- **🔄 Multi-Agent Orchestration**: Supports sequential, parallel, conditional, and loop execution patterns
- **👥 Human-in-the-Loop**: Microsoft Teams integration with speech-to-text and text-to-speech
- **📊 Real-time Monitoring**: Live execution tracking and performance metrics
- **🚀 Scalable Architecture**: Microservices-based design with GCP Cloud Run deployment
- **🎨 Admin UI**: React TypeScript interface with FutureIM.com golden yellow theme

## 🏗️ Architecture

### Control Plane (Master Agent)
- **Master Agent Service**: Central orchestrator for the entire platform
- **LLM Integration**: Gemini and Claude support for agent generation
- **Execution Plan Management**: Create, manage, and execute complex workflows
- **Human Approval Workflows**: Teams integration for human oversight
- **REST API**: Comprehensive API for all platform operations

### Data Plane (Dynamic Agents)
- **Agent Execution Framework**: Runtime environment for generated agents
- **Inter-Agent Communication**: Event-driven messaging system
- **Tool Generation**: Dynamic tool creation for database, API, and file operations
- **Context Sharing**: Shared state management across agents

### Admin UI
- **Dashboard**: Real-time monitoring and control interface
- **Agent Management**: Create, edit, and monitor agents
- **Execution Plans**: Visual workflow designer and management
- **Teams Integration**: Meeting controls and speech interaction
- **Configuration**: System settings and API configurations

## 🛠️ Technology Stack

### Backend
- **Java 17** with Spring Boot 3.2
- **Maven** for dependency management
- **PostgreSQL** for production, H2 for development
- **Google Cloud AI Platform** for LLM integration
- **Microsoft Graph API** for Teams integration

### Frontend
- **React 18** with TypeScript
- **Material-UI** with custom FutureIM.com theme
- **React Query** for state management
- **Recharts** for data visualization
- **Monaco Editor** for code editing

### Cloud & Deployment
- **Google Cloud Platform** (Cloud Run, AI Platform, Pub/Sub, Storage)
- **Docker** for containerization
- **Kubernetes** for orchestration
- **Terraform** for infrastructure as code

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Google Cloud SDK
- Maven 3.9+

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/futureimadmin/platforms.git
   cd platforms/nebula
   ```

2. **Set up environment variables**
   ```bash
   cp deployment/config/development.yml.example deployment/config/development.yml
   # Edit the configuration file with your API keys and settings
   ```

3. **Start the development environment**
   ```bash
   # Start infrastructure services
   cd deployment/docker
   docker-compose up -d postgres redis
   
   # Build and run control plane
   cd ../../control-plane
   mvn clean install
   mvn spring-boot:run
   
   # In another terminal, start the admin UI
   cd ../admin-ui
   npm install
   npm start
   ```

4. **Access the application**
   - Admin UI: http://localhost:3000
   - Control Plane API: http://localhost:8080/nebula-control-plane/api/v1
   - Health Check: http://localhost:8080/nebula-control-plane/actuator/health

### Production Deployment

Deploy to Google Cloud Platform using the automated script:

```bash
cd deployment/gcp
./deploy.sh
```

This script will:
- Build and push Docker images to GCR
- Create necessary GCP resources
- Deploy to Cloud Run
- Set up secrets and service accounts
- Optionally deploy the Admin UI

## 📋 Project Structure

```
nebula/
├── shared/                 # Shared models and utilities
│   ├── src/main/java/     # Java shared classes
│   └── pom.xml            # Maven configuration
├── control-plane/         # Master Agent (Control Plane)
│   ├── src/main/java/     # Spring Boot application
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml            # Maven configuration
├── data-plane/            # Dynamic Agent Framework
│   └── (to be implemented)
├── admin-ui/              # React TypeScript Admin Interface
│   ├── src/               # React components and pages
│   ├── public/            # Static assets
│   └── package.json       # NPM configuration
├── deployment/            # Deployment configurations
│   ├── config/            # Environment configurations
│   ├── docker/            # Docker files and compose
│   ├── gcp/               # Google Cloud Platform deployment
│   ├── kubernetes/        # Kubernetes manifests
│   └── terraform/         # Infrastructure as code
└── schemas/               # JSON schemas for execution plans
```

## 🔧 Configuration

### Environment Variables

#### Control Plane
- `GEMINI_API_KEY`: Google Gemini API key
- `CLAUDE_API_KEY`: Anthropic Claude API key
- `DATABASE_URL`: PostgreSQL connection string
- `TEAMS_CLIENT_ID`: Microsoft Teams application ID
- `TEAMS_CLIENT_SECRET`: Microsoft Teams application secret
- `JWT_SECRET`: Secret key for JWT token generation

#### Admin UI
- `REACT_APP_API_BASE_URL`: Control Plane API base URL

### Configuration Files

- **Development**: `deployment/config/development.yml`
- **Production**: `deployment/config/production.yml`

## 📊 Usage Examples

### Creating an Execution Plan via API

```bash
curl -X POST http://localhost:8080/nebula-control-plane/api/v1/master-agent/process \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Create a data pipeline to process customer orders and send email notifications",
    "context": {
      "priority": "high",
      "department": "sales"
    }
  }'
```

### Using the Admin UI

1. **Dashboard**: Monitor active executions and system metrics
2. **Agent Management**: Create and configure agents
3. **Execution Plans**: Design complex workflows
4. **Teams Integration**: Set up human-in-the-loop interactions

## 🔍 Monitoring & Observability

### Health Checks
- Control Plane: `/nebula-control-plane/actuator/health`
- Metrics: `/nebula-control-plane/actuator/metrics`
- Prometheus: `/nebula-control-plane/actuator/prometheus`

### Logging
- Structured JSON logging with SLF4J
- Centralized log aggregation in production
- Real-time log streaming in Admin UI

### Metrics
- Execution success/failure rates
- Agent utilization statistics
- Response time percentiles
- Resource consumption metrics

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding standards and Spring Boot best practices
- Use TypeScript for all React components
- Write comprehensive tests for new features
- Update documentation for API changes
- Follow the existing code style and patterns

## 📝 API Documentation

### Master Agent Endpoints

- `POST /api/v1/master-agent/process` - Process user prompt
- `GET /api/v1/master-agent/execution/{planId}/status` - Get execution status
- `POST /api/v1/master-agent/execution/{planId}/stop` - Stop execution
- `POST /api/v1/master-agent/execution/{planId}/steps/{stepId}/approval` - Human approval

### Agent Management Endpoints

- `GET /api/v1/agents` - List all agents
- `POST /api/v1/agents` - Create new agent
- `PUT /api/v1/agents/{agentId}` - Update agent
- `DELETE /api/v1/agents/{agentId}` - Delete agent

## 🔒 Security

- JWT-based authentication
- API key management for external services
- Secure secret storage with Google Secret Manager
- CORS configuration for cross-origin requests
- Input validation and sanitization

## 📈 Roadmap

- [ ] Complete Data Plane implementation
- [ ] Advanced agent communication protocols
- [ ] Visual workflow designer
- [ ] Multi-tenant support
- [ ] Advanced analytics and reporting
- [ ] Plugin system for custom tools
- [ ] Integration with more LLM providers

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Google Cloud Platform for AI services
- Microsoft Teams for collaboration features
- Spring Boot and React communities
- Open source contributors

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact the development team
- Check the documentation wiki

---

**Built with ❤️ by the FutureIM team**