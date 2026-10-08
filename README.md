#  SmartQR Attendance System

A secure, high-performance, mobile-ready QR code attendance tracking system built for university event check-ins. It utilizes a lightweight vanilla JavaScript frontend paired with **Supabase** (PostgreSQL with Row Level Security)[cite: 4, 11] and supports optional backend integration via **Java Spring Boot**.

---

## Key Features

* **Real-Time Camera Scanning:** Built with `html5-qrcode` to enable gate staff to use mobile rear cameras smoothly inside any modern browser (`scanner.html`)[cite: 4].
* **Blind-Scan Auto-Registration:** Automatically detects and registers new student IDs on-the-fly if they do not yet exist in the database[cite: 4].
* **Smart Check-In / Check-Out Toggle:** Automatically determines whether a scanned student is arriving (Checking In) or leaving (Checking Out) based on their active attendance status[cite: 4].
* **Role-Based Access Control (RBAC):** Protects administrative routes (`SUPER_ADMIN` and `ADMIN`) using session validation and Supabase profiles (`auth.js`)[cite: 12, 14].
* **Live Analytics & CSV Export:** Filter records by date, view live duration metrics, and export full attendance logs directly to CSV (`attendance.html`, `reports.html`)[cite: 5, 8].
* **Production Security Hardened:** Configured with strict HTTP security headers (`vercel.json`) and database-level Row Level Security (RLS).

---

## Tech Stack

* **Frontend:** HTML5, CSS3, Vanilla JavaScript, Tailwind/Custom CSS
* **Database & Auth:** Supabase (PostgreSQL, Supabase Auth, RLS)[cite: 11, 12]
* **Backend API (Optional):** Java Spring Boot (`/api/attendance/scan`)[cite: 14]
* **Hosting & Deployment:** Vercel (Static hosting with automated HTTPS)

---

## Project Structure

```text
qr-attendance-system/
├── index.html            # Super Admin Login portal[cite: 9]
├── dashboard.html        # Main navigation dashboard[cite: 6]
├── scanner.html          # Mobile camera QR code scanner[cite: 4]
├── attendance.html       # Live attendance records and date filters[cite: 5]
├── manual-entry.html     # Manual student number entry form[cite: 7]
├── reports.html          # Analytics metrics and CSV export[cite: 8]
├── vercel.json           # Production HTTP security headers[cite: 10]
├── css/
│   └── style.css         # Global stylesheet
└── js/
    ├── config.js         # Supabase and API client initialization
    ├── auth.js           # RBAC, login handlers, and secure fetch headers[cite: 12, 13, 14]
    └── scanner.js        # Core scanning logic utilities[cite: 13]