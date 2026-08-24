# MAMA MPOKI CAR HIRE — Module Breakdown & Business Workflows

## Module Overview

The system has **3 main modules** + **shared services**:

```
┌─────────────────────────────────────────────────────────┐
│                    MAMA MPOKI CAR HIRE                  │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ SPECIAL HIRE │  │  DALADALA   │  │ PRIVATE CAR │    │
│  │  (Coaster/  │  │   (Dodoma   │  │   (Owner's  │    │
│  │   Minibus)  │  │   Region)   │  │    Cars)    │    │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘    │
│         │                │                │             │
│         └────────────────┼────────────────┘             │
│                          ▼                              │
│              ┌──────────────────────┐                   │
│              │   SHARED SERVICES    │                   │
│              │  • Vehicles          │                   │
│              │  • Drivers           │                   │
│              │  • Conductors        │                   │
│              │  • Fuel Records      │                   │
│              │  • Maintenance       │                   │
│              │  • Reports           │                   │
│              └──────────────────────┘                   │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## MODULE 1: SPECIAL HIRE (Coaster / Minibus)

### Purpose
Manage charter/hire bookings for coasters and minibuses. This is the **revenue-focused** module for long-distance and event-based trips.

### Business Workflows

#### Workflow 1.1: New Booking
```
Owner receives hire request (phone/walk-in)
    │
    ▼
Create Customer Record (if new)
    │
    ▼
Check Vehicle Availability
  • GET /vehicles/available?startDate=...&endDate=...
  • Show list of available vehicles
    │
    ▼
Create Booking
  • Select vehicle
  • Set dates
  • Set destination
  • Set agreed price (TZS)
  • Record deposit (TZS)
    │
    ▼
Booking Status: PENDING → CONFIRMED
    │
    ▼
When trip date arrives:
  • Create Trip record
  • Assign driver
  • Start trip
    │
    ▼
During Trip:
  • Add expenses (fuel, tolls, food, driver allowance)
    │
    ▼
Trip Complete:
  • Enter odometer reading
  • Finalize expenses
  • Calculate profit/loss
    │
    ▼
Record Payments (can be multiple)
  • Cash, Mobile Money (M-Pesa), Bank Transfer
    │
    ▼
Booking Status: COMPLETED
  • Check if fully paid
  • Outstanding balance = agreed_price - total_paid
```

#### Workflow 1.2: Trip Profit Calculation
```
Trip Revenue (agreed_price)                    TZS
    │
    ▼
Minus Trip Expenses:
  • Fuel cost                                 - TZS
  • Driver allowance                          - TZS
  • Tolls                                     - TZS
  • Food/accommodation                        - TZS
  • Other expenses                            - TZS
    │
    ▼
= Trip Profit/Loss                             TZS
    │
    ▼
Profit Margin = (Profit / Revenue) × 100       %
```

#### Workflow 1.3: Vehicle Availability Check
```
GET /vehicles/available?moduleType=SPECIAL_HIRE&startDate=2026-09-01&endDate=2026-09-03

For each ACTIVE SPECIAL_HIRE vehicle:
  1. Query hire_booking for date overlaps
     WHERE vehicle_id = :vehicleId
     AND status NOT IN ('CANCELLED', 'COMPLETED')
     AND hire_date <= :endDate AND end_date >= :startDate

  2. Query trip for active trips
     WHERE vehicle_id = :vehicleId
     AND status IN ('SCHEDULED', 'IN_PROGRESS')

  3. Check maintenance status
     WHERE status = 'MAINTENANCE'

Return list of available vehicles.
```

### Data Fields

#### Customer
- Full name, phone, email
- ID type & number (National ID, Passport, etc.)
- Address
- Soft deleted (not hard deleted)

#### Booking
- Vehicle, Customer
- Hire dates (start/end)
- Destination, Trip purpose
- Agreed price (TZS), Deposit (TZS)
- Status (PENDING → CONFIRMED → IN_PROGRESS → COMPLETED → CANCELLED)

#### Trip
- Booking reference
- Driver assignment
- Actual dates
- Odometer start/end
- Status

#### Trip Expense
- Type (Fuel, Driver Allowance, Toll, Food, Other)
- Amount (TZS), Description, Date

#### Payment
- Amount (TZS), Method (Cash, Mobile Money, Bank Transfer)
- Date, Reference number (e.g., M-Pesa code)

### Reports
1. **Trip P&L Report** — Revenue vs expenses per trip
2. **Vehicle Utilization** — How often each vehicle is hired
3. **Customer History** — All bookings for a customer
4. **Monthly Revenue** — Total hire income per month (TZS)
5. **Destination Analysis** — Most popular destinations
6. **Driver Performance** — Trips completed, on-time rate

---

## MODULE 2: DALADALA (Dodoma Region)

### Purpose
Manage **daily route-based operations** for daladala (public minibus) services. This is the **daily operations** module focused on routes, daily revenue, and expenses.

### Key Staff Roles
- **Driver**: Operates the vehicle
- **Conductor**: Collects fares, manages passengers, handles money

### Business Workflows

#### Workflow 2.1: Daily Operation Recording
```
Start of Day:
  • Owner selects vehicle
  • Assigns route
  • Assigns driver (from driver entity)
  • Assigns conductor (from conductor entity)
    │
    ▼
During Day:
  • Record departure time
  • Record number of passengers
  • Track fare collection
    │
    ▼
End of Day:
  • Record return time
  • Record total passengers
  • Enter daily revenue
  • Enter daily expenses
    │
    ▼
Operation Complete:
  • Calculate daily profit
  • Update vehicle status
```

#### Workflow 2.2: Daily Financial Summary
```
Daily Revenue Sources:
  • Fare collection (passengers × fare)     TZS
  • Charter income                           TZS
  • Advertising income                       TZS
  • Other income                             TZS
    │
    ▼
Daily Expenses:
  • Fuel                                     TZS
  • Repairs                                  TZS
  • Tolls                                    TZS
  • Conductor allowance                      TZS
  • Other expenses                           TZS
    │
    ▼
Daily Profit = Total Revenue - Total Expenses  TZS
```

#### Workflow 2.3: Conductor Management
```
Add Conductor:
  • Full name, phone, national ID
  • Daily rate (default earning)
  • Status (ACTIVE/INACTIVE)

Track Conductor Performance:
  • Total operations completed
  • Total fare collected
  • Average daily fare
  • Comparison with other conductors

Assign to Operations:
  • Select conductor from active list
  • Record assigned to daily_operation
```

#### Workflow 2.4: Route Management
```
Create/Edit Routes:
  • Route name (e.g., "Dodoma Town - Ihumwa")
  • Start/end points
  • Distance (km)
  • Standard fare (TZS)
    │
    ▼
Route Performance:
  • Total operations per route
  • Average revenue per operation
  • Average passengers per trip
  • Route profitability
```

### Data Fields

#### Conductor
- Full name, phone, national ID
- Address
- Daily rate (default earning in TZS)
- Status (ACTIVE/INACTIVE/ON_LEAVE)

#### Route
- Name (e.g., "Dodoma Town - Ihumwa")
- Start point, End point
- Distance (km)
- Standard fare amount (TZS)
- Status (ACTIVE/INACTIVE)

#### Daily Operation
- Vehicle, Route
- Driver (entity reference), Conductor (entity reference)
- Date
- Departure time, Return time
- Total passengers
- Status (SCHEDULED → IN_PROGRESS → COMPLETED → CANCELLED)

#### Daily Revenue
- Source (Fare, Charter, Advertising, Other)
- Amount (TZS), Description, Date

#### Daily Expense
- Type (Fuel, Repair, Toll, Maintenance, Conductor Allowance, Other)
- Amount (TZS), Description, Date

### Reports
1. **Daily Summary** — Revenue, expenses, profit for each day (TZS)
2. **Route Performance** — Which routes earn the most
3. **Vehicle Performance** — Which daladala vehicles are most profitable
4. **Driver Performance** — Revenue per driver
5. **Conductor Performance** — Fare collected per conductor
6. **Weekly/Monthly Trends** — Revenue and profit over time
7. **Expense Breakdown** — Where money is being spent
8. **Passenger Trends** — Peak days, average passengers

### Key Difference from Special Hire
| Aspect | Special Hire | Daladala |
|--------|-------------|----------|
| **Workflow** | Trip-based (booking → trip → payment) | Daily operations (route → daily revenue/expenses) |
| **Revenue** | Per trip (agreed price in TZS) | Per day (fare collection in TZS) |
| **Expenses** | Per trip | Per day |
| **Scheduling** | Customer-driven bookings | Owner-driven daily schedules |
| **Financial Focus** | Profit per trip | Daily/weekly/monthly performance |
| **Route** | Variable (customer decides) | Fixed routes |
| **Staff** | Driver only | Driver + Conductor |

---

## MODULE 3: PRIVATE CARS (Owner's Cars)

### Purpose
Simple record-keeping for the owner's personal vehicles. Focus on **maintenance tracking** and **document expiry alerts**.

### Business Workflows

#### Workflow 3.1: Vehicle Registration
```
Add Private Car:
  • Vehicle details (make, model, year, reg number)
  • Insurance information
    - Insurance number, provider
    - Expiry date
  • Registration details
    - Registration expiry date
    │
    ▼
System tracks:
  • Insurance expiry date
  • Registration expiry date
  • Last service date
  • Next service date
```

#### Workflow 3.2: Expense Tracking
```
Owner records expenses:
  • Fuel purchases (liters × cost per liter = total)
  • Service/repair costs
  • Insurance payments
  • Registration renewals
    │
    ▼
System maintains:
  • Total cost per vehicle (TZS)
  • Cost per period (monthly/yearly)
  • Expense breakdown by category
```

#### Workflow 3.3: Document Expiry Alerts
```
System checks daily:
  • Insurance expiry (alert 30 days before)
  • Registration expiry (alert 30 days before)
  • Service due date (alert based on interval)
    │
    ▼
Dashboard shows alerts:
  ⚠️ T 789 GHI insurance expires in 5 days
  ⚠️ T 456 DEF registration expires in 15 days
  ⚠️ T 123 ABC service overdue by 500km
```

### Data Fields

#### Private Car (extends Vehicle)
- Insurance number, provider, expiry date
- Registration expiry date
- Last inspection date
- Last service date
- Notes

#### Fuel Record
- Date, liters, cost per liter (TZS), total cost (TZS)
- Odometer reading, fueling station
- **Validation**: total_cost must equal liters × cost_per_liter

#### Maintenance Record
- Type (Service, Repair, Inspection, Oil Change, etc.)
- Date, description, cost (TZS)
- Garage name, odometer reading
- Next service date

### Reports
1. **Vehicle Expenses** — Total costs per vehicle (TZS)
2. **Fuel Consumption** — Liters and cost over time
3. **Maintenance History** — All service records
4. **Document Status** — Expiry tracking summary
5. **Annual Costs** — Yearly expense summary (TZS)

---

## SHARED SERVICES

### Vehicles (Shared Across Modules)
- All vehicles stored in one `vehicle` table
- `module_type` field distinguishes: SPECIAL_HIRE, DALADALA, PRIVATE
- Shared services: fuel records, maintenance records
- Vehicle status: ACTIVE, INACTIVE, MAINTENANCE, RETIRED
- Soft deleted (not hard deleted)

### Drivers (Shared Across Modules)
- Drivers can work across special hire and daladala
- Driver performance tracked across both modules
- Daily rate stored for billing (TZS)
- Soft deleted (not hard deleted)

### Conductors (Daladala Only)
- Specific to daladala operations
- Track fare collection performance
- Daily rate stored for earnings (TZS)
- Soft deleted (not hard deleted)

### Fuel Management (Shared)
- All fuel records linked to vehicle
- Cost tracking per vehicle (TZS)
- Consumption analysis
- **Validation**: total_cost = liters × cost_per_liter

### Maintenance (Shared)
- All maintenance records linked to vehicle
- Service reminders
- Cost tracking (TZS)

### Dashboard
- Overall fleet status
- Revenue summaries across all modules (TZS)
- Alerts and notifications
- Recent activity

### Reports
- Cross-module financial reports
- Monthly/quarterly P&L (TZS)
- Vehicle profitability comparison
- Expense analysis

---

## Page Structure (Frontend)

```
/login
/dashboard
/special-hire
  /bookings
  /bookings/new
  /bookings/:id
  /trips
  /trips/:id
  /customers
/daladala
  /routes
  /routes/new
  /operations
  /operations/new
  /operations/:id
  /conductors
  /conductors/new
/private-cars
  /list
  /new
  /:id
  /:id/fuel
  /:id/maintenance
/fleet
  /vehicles
  /vehicles/new
  /vehicles/:id
  /drivers
  /drivers/new
/reports
  /special-hire
  /daladala
  /vehicles
  /monthly
  /quarterly
/settings
/profile
```

---

## UI Component Structure

### Dashboard
```
┌─────────────────────────────────────────────────────┐
│  🏠 MAMA MPOKI CAR HIRE          [Profile] [Logout]│
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │ 🚐 8     │ │ 📋 3     │ │ 💰 12.5M │            │
│  │ Vehicles │ │ Pending  │ │ Monthly  │            │
│  │ Active   │ │ Bookings │ │ Revenue  │            │
│  └──────────┘ └──────────┘ └──────────┘            │
│                                                     │
│  ┌─────────────────┐  ┌─────────────────┐          │
│  │  Revenue Chart  │  │  Alerts         │          │
│  │  (Last 30 days) │  │  • Insurance X  │          │
│  │  📈  TZS        │  │  • Service due  │          │
│  └─────────────────┘  └─────────────────┘          │
│                                                     │
│  ┌─────────────────────────────────────────┐        │
│  │  Recent Activity                        │        │
│  │  • Booking #12 - T 123 ABC - Dar       │        │
│  │  • Operation - Dodoma-Ihumwa - 45 pax  │        │
│  │  • Fuel - T 456 DEF - 35,000 TZS       │        │
│  └─────────────────────────────────────────┘        │
│                                                     │
└─────────────────────────────────────────────────────┘
```
