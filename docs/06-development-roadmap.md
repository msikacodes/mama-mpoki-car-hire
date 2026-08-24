# MAMA MPOKI CAR HIRE — Development Roadmap

## Phase 1: Foundation (Week 1-2)
> Get the core infrastructure running

### Backend Setup
- [ ] Initialize Spring Boot project with Maven
- [ ] Configure MySQL database connection
- [ ] Set up Spring Security with JWT (access + refresh tokens)
- [ ] Set up rate limiting filter for login endpoint
- [ ] Create base entity classes (`BaseEntity`, `SoftDeletableEntity`)
- [ ] Create enum classes (VehicleType, ModuleType, VehicleStatus, etc.)
- [ ] Set up Flyway for database migrations
- [ ] Configure CORS for frontend (environment-based)
- [ ] Set up Swagger/OpenAPI documentation
- [ ] Create global exception handler
- [ ] Create standard API response format (`ApiResponse<T>`)
- [ ] Configure JPA auditing (`@EnableJpaAuditing`)

### Database (Flyway Migrations)
- [ ] V1: Create `owner` table and insert initial owner record
- [ ] V2: Create `vehicle` table with soft delete fields
- [ ] V3: Create `driver` table with soft delete fields
- [ ] V4: Create `conductor` table with soft delete fields
- [ ] V5: Create `customer` table
- [ ] V6: Create all indexes

### Authentication
- [ ] Implement login endpoint (returns access + refresh tokens)
- [ ] Implement refresh token endpoint
- [ ] Implement JWT token generation (access + refresh)
- [ ] Implement JWT authentication filter (validates access tokens only)
- [ ] Implement change password endpoint
- [ ] Implement rate limiting on login (5 attempts/min per IP)
- [ ] Test full authentication flow

---

## Phase 2: Fleet Management (Week 2-3)
> Core vehicle and driver management

### Vehicles
- [ ] CRUD operations for vehicles (with soft delete)
- [ ] Vehicle list with filtering (by moduleType, status)
- [ ] Vehicle detail view
- [ ] Fleet summary statistics (`/vehicles/fleet-summary`)
- [ ] Vehicle availability check endpoint (`/vehicles/available`)

### Drivers
- [ ] CRUD operations for drivers (with soft delete)
- [ ] Driver list with filtering
- [ ] Driver detail view
- [ ] Driver performance stats (`/drivers/stats`)

### Conductors
- [ ] CRUD operations for conductors (with soft delete)
- [ ] Conductor list with filtering
- [ ] Conductor detail view
- [ ] Conductor performance stats (`/conductors/stats`)

### Customers
- [ ] CRUD operations for customers (with soft delete)
- [ ] Customer search by name/phone
- [ ] Customer booking history

---

## Phase 3: Special Hire Module (Week 3-5)
> The main revenue module

### Bookings
- [ ] Create/edit booking (with vehicle availability check)
- [ ] Booking list with filters (status, date range)
- [ ] Booking detail view with payments
- [ ] Update booking status
- [ ] Booking financial summary (`/bookings/{id}/financials`)

### Trips
- [ ] Create trip from booking
- [ ] Assign driver to trip
- [ ] Update trip status
- [ ] Complete trip (odometer, finalize)

### Trip Expenses
- [ ] Add/remove trip expenses
- [ ] Expense categories (fuel, driver allowance, toll, food, other)
- [ ] Trip expense summary

### Payments
- [ ] Record payments against booking
- [ ] Payment history per booking
- [ ] Outstanding balance calculation
- [ ] Payment status tracking (UNPAID, PARTIALLY_PAID, PAID)

### Profit Calculation
- [ ] Trip profit/loss calculation
- [ ] Booking financial summary
- [ ] Currency display (TZS)

---

## Phase 4: Daladala Module (Week 5-7)
> Daily operations module

### Routes
- [ ] CRUD operations for routes
- [ ] Route list with active status
- [ ] Route performance stats

### Daily Operations
- [ ] Record daily operation (with driver + conductor assignment)
- [ ] Operation list with filters (date, route, vehicle)
- [ ] Complete operation (enter final figures)
- [ ] Operation detail view with summary
- [ ] Operation profit/loss summary

### Revenue & Expenses
- [ ] Add daily revenue entries
- [ ] Add daily expense entries
- [ ] Delete revenue/expense entries
- [ ] Daily financial breakdown

### Conductor Integration
- [ ] Assign conductor to operation
- [ ] Track conductor fare collection
- [ ] Conductor performance reports

### Daladala Reports
- [ ] Daily summary report
- [ ] Route performance report
- [ ] Vehicle performance report
- [ ] Conductor performance report

---

## Phase 5: Private Cars Module (Week 7-8)
> Simple record-keeping module

### Private Car Management
- [ ] Register private car (extends vehicle)
- [ ] Insurance information tracking
- [ ] Registration expiry tracking
- [ ] Maintenance schedule

### Fuel Records
- [ ] Add fuel records
- [ ] **Validation**: total_cost = liters × cost_per_liter
- [ ] Fuel consumption analysis
- [ ] Fuel cost trends

### Maintenance Records
- [ ] Add maintenance records
- [ ] Service reminders
- [ ] Maintenance cost tracking

### Document Alerts
- [ ] Insurance expiry alerts (30 days)
- [ ] Registration expiry alerts
- [ ] Service due reminders

---

## Phase 6: Shared Services & Reports (Week 8-10)
> Cross-module features

### Dashboard
- [ ] Fleet status overview
- [ ] Revenue summary across modules (TZS)
- [ ] Alerts and notifications
- [ ] Recent activity feed
- [ ] Revenue trend charts

### Fuel Management (Shared)
- [ ] Fuel records across all vehicles
- [ ] Fuel consumption comparison
- [ ] Cost per kilometer analysis

### Maintenance (Shared)
- [ ] Maintenance records across all vehicles
- [ ] Upcoming maintenance alerts
- [ ] Maintenance cost summary

### Reports
- [ ] Special hire summary report (with date params)
- [ ] Daladala performance report (with date params)
- [ ] Vehicle profitability comparison
- [ ] Expense breakdown report
- [ ] Revenue breakdown report
- [ ] Monthly P&L summary (`?year=2026&month=8`)
- [ ] Quarterly P&L summary (`?year=2026&quarter=3`)
- [ ] Export reports to PDF/Excel

---

## Phase 7: Frontend - React SPA (Week 10-14)
> Build the user interface

### Setup
- [ ] Initialize React project with Vite
- [ ] Set up Tailwind CSS
- [ ] Configure Axios with interceptors + auto-refresh
- [ ] Set up React Router
- [ ] Create layout components (Sidebar, Header, Footer)

### Authentication Pages
- [ ] Login page
- [ ] Change password page
- [ ] Auto-refresh token handling

### Dashboard
- [ ] Dashboard page with cards
- [ ] Revenue chart component (Recharts)
- [ ] Alerts component
- [ ] Recent activity component

### Special Hire Pages
- [ ] Bookings list page
- [ ] New booking form (with vehicle availability)
- [ ] Booking detail page (with financials)
- [ ] Trips list page
- [ ] Trip detail page (with expenses)
- [ ] Customer management pages

### Daladala Pages
- [ ] Routes list page
- [ ] New route form
- [ ] Operations list page
- [ ] New operation form (with conductor selection)
- [ ] Operation detail page
- [ ] Conductor management pages

### Private Cars Pages
- [ ] Private cars list page
- [ ] New private car form
- [ ] Car detail page
- [ ] Fuel records page (with validation)
- [ ] Maintenance records page

### Fleet Pages
- [ ] Vehicles list page
- [ ] New vehicle form
- [ ] Vehicle detail page
- [ ] Drivers list page
- [ ] New driver form

### Reports Pages
- [ ] Special hire report page (with date picker)
- [ ] Daladala report page (with date picker)
- [ ] Vehicle report page
- [ ] Monthly summary page
- [ ] Quarterly summary page
- [ ] Report export functionality

---

## Phase 8: Polish & Deployment (Week 14-16)
> Final touches and going live

### Testing
- [ ] Unit tests for services
- [ ] Integration tests for API endpoints
- [ ] Test all CRUD operations
- [ ] Test authentication flow (login, refresh, rate limit)
- [ ] Test report generation
- [ ] Test soft delete behavior
- [ ] Test fuel record validation

### Performance
- [ ] Database query optimization
- [ ] Add pagination to all list endpoints
- [ ] Verify all database indexes are created
- [ ] Optimize N+1 queries (use `@EntityGraph` or `JOIN FETCH`)

### Deployment
- [ ] Dockerize Spring Boot backend
- [ ] Dockerize React frontend
- [ ] Docker Compose for local development
- [ ] Production database setup (MySQL on cloud)
- [ ] Deploy backend (Railway / Render / AWS)
- [ ] Deploy frontend (Vercel / Netlify)
- [ ] Configure environment variables (JWT_SECRET, FRONTEND_URL, etc.)
- [ ] Set up SSL/HTTPS

### Documentation
- [ ] API documentation (Swagger)
- [ ] Setup instructions
- [ ] User manual

---

## Technology Decisions Log

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Backend Framework | Spring Boot 3.x | Industry standard, Java ecosystem |
| Database | MySQL 8.x | Popular, well-supported, easy hosting |
| Authentication | JWT (access + refresh) | Stateless, works with SPA frontend |
| Rate Limiting | In-memory cache (Caffeine/Guava) | Simple, no external dependencies |
| Frontend | React | Component-based, large ecosystem |
| CSS | Tailwind CSS | Fast development, consistent design |
| API Style | RESTful | Simple, well-understood |
| Build Tool | Maven | Standard for Java projects |
| ORM | Spring Data JPA | Reduces boilerplate, good MySQL support |
| API Docs | Swagger/OpenAPI 3 | Interactive documentation |
| DB Migrations | Flyway | Version-controlled schema changes |
| Soft Delete | Boolean flag + timestamp | Never lose data, easy to restore |

---

## Estimated Timeline

```
Week 1-2:   ████████░░░░░░░░  Foundation (Auth + Rate Limiting + Soft Delete)
Week 2-3:   ░░░░████░░░░░░░░  Fleet Management (Vehicles + Drivers + Conductors)
Week 3-5:   ░░░░░░████░░░░░░  Special Hire Module (Bookings + Trips + Payments)
Week 5-7:   ░░░░░░░░████░░░░  Daladala Module (Routes + Operations + Conductors)
Week 7-8:   ░░░░░░░░░░██░░░░  Private Cars Module (Insurance + Fuel + Maintenance)
Week 8-10:  ░░░░░░░░░░░████░  Shared Services & Reports
Week 10-14: ░░░░░░░░░░░░████  Frontend (React SPA)
Week 14-16: ░░░░░░░░░░░░░░██  Polish & Deploy
```

**Total Estimated Time: 16 weeks (4 months)**

---

## Priority Matrix

### Must Have (MVP)
- Owner authentication (login + refresh token)
- Rate limiting on login
- Vehicle CRUD (with soft delete)
- Driver CRUD
- Conductor CRUD
- Special hire bookings & trips
- Basic financial tracking (TZS)
- Daladala daily operations
- Basic dashboard
- Basic reports (with date parameters)

### Should Have
- Customer management
- Document expiry alerts
- Advanced reports (monthly/quarterly P&L)
- Fuel tracking (with validation)
- Maintenance tracking
- Vehicle availability check

### Nice to Have
- PDF/Excel export
- Charts and visualizations
- Offline support
- Mobile responsive design
- SMS notifications

---

## Next Steps

1. **Review all design documents** with stakeholders
2. **Confirm technology choices** (Spring Boot, MySQL, JWT with refresh tokens, React)
3. **Start Phase 1**: Set up the Spring Boot project
4. **Create initial database schema** with Flyway migrations
5. **Implement authentication** with refresh tokens and rate limiting
6. **Build core CRUD operations** with soft delete support
