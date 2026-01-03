#!/bin/bash

echo "Waiting for Keycloak to start..."
sleep 15

echo "Configuring Keycloak..."

# Obtenir le token d'administration
ACCESS_TOKEN=$(curl -s -X POST \
  http://localhost:8080/realms/master/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

echo "Admin token obtained"

# Créer le realm Skillverse
curl -s -X POST \
  http://localhost:8080/admin/realms \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "realm": "skillverse",
    "enabled": true,
    "displayName": "Skillverse",
    "displayNameHtml": "<div>Skillverse</div>",
    "registrationAllowed": true,
    "registrationEmailAsUsername": true,
    "rememberMe": true,
    "verifyEmail": true,
    "loginWithEmailAllowed": true,
    "duplicateEmailsAllowed": false,
    "resetPasswordAllowed": true,
    "editUsernameAllowed": false,
    "bruteForceProtected": true
  }'

echo "Realm skillverse created"

# Créer le client auth-service
curl -s -X POST \
  "http://localhost:8080/admin/realms/skillverse/clients" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "auth-service",
    "name": "Auth Service",
    "description": "Authentication Service for Skillverse",
    "enabled": true,
    "publicClient": false,
    "secret": "your-client-secret",
    "redirectUris": [
      "http://localhost:8080/*",
      "http://localhost:3000/*"
    ],
    "webOrigins": [
      "http://localhost:8080",
      "http://localhost:3000"
    ],
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "authorizationServicesEnabled": false,
    "fullScopeAllowed": true,
    "attributes": {
      "oauth2.device.authorization.grant.enabled": "false",
      "backchannel.logout.session.required": "true",
      "backchannel.logout.revoke.offline.tokens": "false"
    }
  }'

echo "Client auth-service created"

# Créer des rôles
curl -s -X POST \
  "http://localhost:8080/admin/realms/skillverse/roles" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "ROLE_USER", "description": "Regular user"}'

curl -s -X POST \
  "http://localhost:8080/admin/realms/skillverse/roles" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "ROLE_ADMIN", "description": "Administrator"}'

curl -s -X POST \
  "http://localhost:8080/admin/realms/skillverse/roles" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "ROLE_TEACHER", "description": "Teacher role"}'

curl -s -X POST \
  "http://localhost:8080/admin/realms/skillverse/roles" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "ROLE_LEARNER", "description": "Learner role"}'

echo "Roles created"

# Créer un utilisateur de test
curl -s -X POST \
  "http://localhost:8080/admin/realms/skillverse/users" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@skillverse.com",
    "email": "test@skillverse.com",
    "firstName": "Test",
    "lastName": "User",
    "emailVerified": true,
    "enabled": true,
    "credentials": [
        {
        "type": "password",
        "value": "Test123!",
        "temporary": false
        }
    ]
    }'

echo "Test user created"

echo "Keycloak configuration completed!"