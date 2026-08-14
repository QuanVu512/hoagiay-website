# Deploy To Render With Docker

Use the repository Dockerfile when creating a Render Web Service.

## Required Environment Variables

```properties
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://...
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
JWT_BASE64_SECRET=...
JWT_VALIDITY_IN_SECONDS=3600
JWT_REFRESH_VALIDITY_IN_SECONDS=604800
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
COOKIE_SECURE=true
COOKIE_SAME_SITE=Lax
```

Render provides `PORT` automatically. The Docker command passes that value to Spring Boot.

## Free Plan Sleep

On Render free instances, the service may sleep after inactivity. A custom domain does not prevent sleep.

To keep the server always awake, use a paid Render instance or another hosting plan/platform that does not sleep web services.
