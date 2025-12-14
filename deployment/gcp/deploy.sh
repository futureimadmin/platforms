#!/bin/bash

# Nebula Platform GCP Deployment Script
# This script deploys the Nebula platform to Google Cloud Platform

set -e

# Configuration
PROJECT_ID="intelligentmachines"
REGION="us-central1"
SERVICE_NAME="nebula-control-plane"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting Nebula Platform Deployment to GCP${NC}"

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo -e "${RED}❌ gcloud CLI is not installed. Please install it first.${NC}"
    exit 1
fi

# Check if docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed. Please install it first.${NC}"
    exit 1
fi

# Set the project
echo -e "${YELLOW}📋 Setting GCP project to ${PROJECT_ID}${NC}"
gcloud config set project ${PROJECT_ID}

# Enable required APIs
echo -e "${YELLOW}🔧 Enabling required GCP APIs${NC}"
gcloud services enable \
    cloudbuild.googleapis.com \
    run.googleapis.com \
    containerregistry.googleapis.com \
    sqladmin.googleapis.com \
    secretmanager.googleapis.com \
    aiplatform.googleapis.com \
    pubsub.googleapis.com \
    storage.googleapis.com

# Build and push the Docker image
echo -e "${YELLOW}🏗️  Building Docker image${NC}"
cd ../../
docker build -f deployment/docker/Dockerfile.control-plane -t ${IMAGE_NAME}:latest .

echo -e "${YELLOW}📤 Pushing Docker image to GCR${NC}"
docker push ${IMAGE_NAME}:latest

# Create secrets if they don't exist
echo -e "${YELLOW}🔐 Creating secrets${NC}"
create_secret_if_not_exists() {
    local secret_name=$1
    local secret_value=$2
    
    if ! gcloud secrets describe ${secret_name} &> /dev/null; then
        echo "Creating secret: ${secret_name}"
        echo -n "${secret_value}" | gcloud secrets create ${secret_name} --data-file=-
    else
        echo "Secret ${secret_name} already exists"
    fi
}

# Create default secrets (replace with actual values)
create_secret_if_not_exists "database-url" "jdbc:postgresql://localhost:5432/nebula"
create_secret_if_not_exists "database-username" "nebula"
create_secret_if_not_exists "database-password" "changeme"
create_secret_if_not_exists "gemini-api-key" "your-gemini-api-key"
create_secret_if_not_exists "teams-client-id" "your-teams-client-id"
create_secret_if_not_exists "teams-client-secret" "your-teams-client-secret"
create_secret_if_not_exists "teams-tenant-id" "your-teams-tenant-id"
create_secret_if_not_exists "jwt-secret" "your-jwt-secret-key"
create_secret_if_not_exists "admin-api-key" "your-admin-api-key"

# Create service account
echo -e "${YELLOW}👤 Creating service account${NC}"
SERVICE_ACCOUNT="nebula-service-account"
if ! gcloud iam service-accounts describe ${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com &> /dev/null; then
    gcloud iam service-accounts create ${SERVICE_ACCOUNT} \
        --display-name="Nebula Service Account" \
        --description="Service account for Nebula platform"
    
    # Grant necessary permissions
    gcloud projects add-iam-policy-binding ${PROJECT_ID} \
        --member="serviceAccount:${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com" \
        --role="roles/secretmanager.secretAccessor"
    
    gcloud projects add-iam-policy-binding ${PROJECT_ID} \
        --member="serviceAccount:${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com" \
        --role="roles/aiplatform.user"
    
    gcloud projects add-iam-policy-binding ${PROJECT_ID} \
        --member="serviceAccount:${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com" \
        --role="roles/pubsub.editor"
    
    gcloud projects add-iam-policy-binding ${PROJECT_ID} \
        --member="serviceAccount:${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com" \
        --role="roles/storage.admin"
else
    echo "Service account already exists"
fi

# Deploy to Cloud Run
echo -e "${YELLOW}☁️  Deploying to Cloud Run${NC}"
gcloud run deploy ${SERVICE_NAME} \
    --image=${IMAGE_NAME}:latest \
    --platform=managed \
    --region=${REGION} \
    --allow-unauthenticated \
    --service-account=${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com \
    --memory=4Gi \
    --cpu=2 \
    --timeout=300 \
    --concurrency=80 \
    --min-instances=1 \
    --max-instances=100 \
    --set-env-vars="SPRING_PROFILES_ACTIVE=production,GCP_PROJECT_ID=${PROJECT_ID},GCP_REGION=${REGION}" \
    --set-secrets="DATABASE_URL=database-url:latest,DATABASE_USERNAME=database-username:latest,DATABASE_PASSWORD=database-password:latest,GEMINI_API_KEY=gemini-api-key:latest,TEAMS_CLIENT_ID=teams-client-id:latest,TEAMS_CLIENT_SECRET=teams-client-secret:latest,TEAMS_TENANT_ID=teams-tenant-id:latest,JWT_SECRET=jwt-secret:latest,ADMIN_API_KEY=admin-api-key:latest"

# Get the service URL
SERVICE_URL=$(gcloud run services describe ${SERVICE_NAME} --platform=managed --region=${REGION} --format='value(status.url)')

echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
echo -e "${GREEN}🌐 Service URL: ${SERVICE_URL}${NC}"
echo -e "${GREEN}📊 Health Check: ${SERVICE_URL}/nebula-control-plane/actuator/health${NC}"
echo -e "${GREEN}🎯 API Endpoint: ${SERVICE_URL}/nebula-control-plane/api/v1${NC}"

# Optional: Deploy admin UI to Firebase Hosting or Cloud Storage
echo -e "${YELLOW}🎨 Would you like to deploy the Admin UI? (y/n)${NC}"
read -r deploy_ui

if [[ $deploy_ui == "y" || $deploy_ui == "Y" ]]; then
    echo -e "${YELLOW}🏗️  Building Admin UI${NC}"
    cd admin-ui
    
    # Set the API base URL
    echo "REACT_APP_API_BASE_URL=${SERVICE_URL}/nebula-control-plane/api/v1" > .env.production
    
    npm install
    npm run build
    
    # Deploy to Firebase Hosting (requires firebase-tools)
    if command -v firebase &> /dev/null; then
        echo -e "${YELLOW}🔥 Deploying to Firebase Hosting${NC}"
        firebase deploy --only hosting
    else
        echo -e "${YELLOW}📦 Firebase CLI not found. You can manually deploy the build/ folder${NC}"
        echo -e "${YELLOW}📁 Build files are in: admin-ui/build/${NC}"
    fi
fi

echo -e "${GREEN}🎉 Nebula Platform deployment completed!${NC}"