# Deployment Guide

## Supabase

1. Create a project.
2. Open SQL Editor.
3. Run `supabase/schema.sql`.
4. Create an Auth user.
5. Insert its ID into `profiles` with role `SUPER_ADMIN`.
6. Create an active attendance session.
7. Add students.

## Local backend

Set environment variables:

Windows PowerShell:
```powershell
$env:SUPABASE_URL="https://YOURPROJECT.supabase.co"
$env:SUPABASE_PUBLISHABLE_KEY="YOUR_PUBLISHABLE_KEY"
cd backend
mvn spring-boot:run
```

## Frontend

Replace placeholders in each `js/config.js`.

Serve each folder with a local HTTP server.

## Vercel

Import each frontend directory as a separate Vercel project.

Set production `API_BASE_URL` to your deployed Spring Boot API.

Important: Vercel does not run a normal Spring Boot server. Deploy the Java backend to a Java-capable service and put its HTTPS URL into the frontend.

## HTTPS camera requirement

Browser camera access generally requires HTTPS in production. Vercel provides HTTPS for deployed sites.

## Production security

- Never expose Supabase secret/service-role keys.
- Use RLS.
- Restrict CORS to your production frontend URLs.
- Add server-side authentication/JWT validation before production use.
- Do not trust a client-supplied `scannedBy` value in the final production API; derive the user identity from a verified access token.
