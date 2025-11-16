#!/bin/bash

echo "🐳 Testing Docker Containerization"
echo "=================================="
echo ""

# Step 1: Build PostgreSQL image
echo "📦 Step 1: Building PostgreSQL image..."
docker build -f Dockerfile.postgres -t devices-api-postgres:latest .
if [ $? -ne 0 ]; then
    echo "❌ Failed to build PostgreSQL image"
    exit 1
fi
echo "✅ PostgreSQL image built successfully"
echo ""

# Step 2: Build Application image
echo "📦 Step 2: Building Application image..."
docker build -t devices-api:latest .
if [ $? -ne 0 ]; then
    echo "❌ Failed to build Application image"
    exit 1
fi
echo "✅ Application image built successfully"
echo ""

# Step 3: Create network
echo "🌐 Step 3: Creating Docker network..."
docker network create devices-network 2>/dev/null || echo "Network already exists"
echo ""

# Step 4: Start PostgreSQL container
echo "🐘 Step 4: Starting PostgreSQL container..."
docker run -d \
  --name devices-postgres \
  --network devices-network \
  -e POSTGRES_DB=devices_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  devices-api-postgres:latest

if [ $? -ne 0 ]; then
    echo "⚠️  PostgreSQL container might already be running"
else
    echo "✅ PostgreSQL container started"
fi
echo ""

# Step 5: Wait for PostgreSQL to be ready
echo "⏳ Step 5: Waiting for PostgreSQL to be ready..."
sleep 10
echo "✅ PostgreSQL should be ready"
echo ""

# Step 6: Start Application container
echo "🚀 Step 6: Starting Application container..."
docker run -d \
  --name devices-api-app \
  --network devices-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://devices-postgres:5432/devices_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -p 8080:8080 \
  devices-api:latest

if [ $? -ne 0 ]; then
    echo "⚠️  Application container might already be running"
else
    echo "✅ Application container started"
fi
echo ""

# Step 7: Wait for application to start
echo "⏳ Step 7: Waiting for application to start (30 seconds)..."
sleep 30
echo ""

# Step 8: Check container status
echo "📊 Step 8: Checking container status..."
echo ""
docker ps --filter "name=devices-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

# Step 9: Check application logs
echo "📋 Step 9: Checking application logs (last 20 lines)..."
echo ""
docker logs --tail 20 devices-api-app
echo ""

# Step 10: Test API endpoint
echo "🧪 Step 10: Testing API endpoint..."
echo ""
echo "Testing GET /api/v1/devices..."
sleep 5
curl -s http://localhost:8080/api/v1/devices | head -20 || echo "⚠️  API might still be starting up"
echo ""
echo ""

# Step 11: Show health check
echo "💚 Step 11: Testing Swagger UI availability..."
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" http://localhost:8080/swagger-ui.html
echo ""

# Summary
echo "=================================="
echo "🎉 Docker Containerization Test Complete!"
echo ""
echo "📌 Quick Commands:"
echo "   - View logs:         docker logs -f devices-api-app"
echo "   - Stop containers:   docker stop devices-api-app devices-postgres"
echo "   - Remove containers: docker rm -f devices-api-app devices-postgres"
echo "   - Access API:        http://localhost:8080/api/v1/devices"
echo "   - Access Swagger:    http://localhost:8080/swagger-ui.html"
echo ""
echo "✨ Test a complete flow:"
echo '   curl -X POST http://localhost:8080/api/v1/devices \'
echo '     -H "Content-Type: application/json" \'
echo '     -d '"'"'{"name":"Laptop","brand":"Dell"}'"'"
echo ""
