
---

# 🏠 BAYTI

## Smart Construction Project Management Platform

Bayti is a digital platform designed to simplify the home-building journey by connecting landowners with contractors, managing construction projects and offers, and providing a marketplace for surplus construction materials. :chatgpt-content-reference{index="1"}

---

# 🎯 Main Features

### 👤 User
- Register and verify account
- Add and manage land
- Create construction projects
- Receive contractor offers
- Select a contractor
- Follow construction phases
- Manage construction materials
- Sell surplus materials
- Buy surplus materials from other users

### 👷 Contractor
- Verify contractor account
- View open construction projects
- Submit offers
- Execute construction phases
- Update construction progress
- Add purchased materials
- Record material usage

### 👨‍💼 Admin
- Manage and monitor users and the platform
- Verify contractors
- Approve contractor license information

### ♻️ Surplus Marketplace
- Create surplus listings
- Close listings
- Reopen listings
- Transfer surplus quantities
- Purchase surplus materials
- Pay for surplus orders
- Email buyer and seller after successful payment

---

# 🔄 System Workflow

```text
User Registration
       ↓
Email Verification
       ↓
Add Land
       ↓
Create Construction Project
       ↓
Project becomes OPEN
       ↓
Contractors view Open Projects
       ↓
Contractor submits Offer
       ↓
Landowner reviews Offers
       ↓
Landowner accepts Offer
       ↓
Contractor is assigned
       ↓
Construction Phases are created
       ↓
Contractor starts Phase
       ↓
Phase → IN_PROGRESS
       ↓
Contractor completes Phase
       ↓
Phase → COMPLETED
       ↓
Materials are purchased and used
       ↓
Surplus Quantity is calculated
       ↓
Surplus can be listed for sale
       ↓
Another User purchases surplus
       ↓
Payment
       ↓
Email confirmation
```

---

# 🔐 1. Authentication & Verification

## Verify User / Contractor

### Endpoint

```http
POST /api/v1/auth/verify
```

### User Request

```text
Verification Code
```

### User Response

```json
{
  "status": "ACCEPTED"
}
```

### Contractor Response

```json
{
  "status": "PENDING_ADMIN_VERIFICATION"
}
```

### Business Logic

#### User

```text
Correct verification code
        ↓
ACCEPTED
        ↓
Login allowed
```

#### Contractor

```text
Correct verification code
        ↓
Verification successful
        ↓
PENDING_ADMIN_VERIFICATION
        ↓
Admin verifies License Number
        ↓
ACCEPTED
        ↓
Login allowed
```


---

# 🏡 2. Land Management

## Add Land

### Endpoint

```http
POST /api/v1/land/add
```

### Request

```text
Land data:
- area
- city
- ...
```

### Response

```json
{
  "message": "Land added successfully"
}
```

### Business Logic

```text
Validate Land Data
        ↓
Get Authenticated User
        ↓
Link Land to User
        ↓
Save Land
```

:chatgpt-content-reference{index="4"}

---

# 🏗️ 3. Create Construction Project

### Endpoint

```http
POST /api/v1/construction-project/add
```

### Request

```text
Land ID
+
Project Data
```

### Response

```json
{
  "message": "Construction project created successfully"
}
```

### Business Logic

```text
Check Land Exists
        ↓
Check Land Ownership
        ↓
Create Construction Project
        ↓
Link Project to User + Land
        ↓
Save Project
```

:chatgpt-content-reference{index="5"}

---

# 🔎 4. View Open Construction Projects

### Endpoint

```http
GET /api/v1/construction-project/open
```

### Request

```text
No body required
```

### Response

```json
[
  {
    "projectId": 1,
    "status": "OPEN"
  }
]
```

### Business Logic

```text
Query Construction Projects
        ↓
Filter status = OPEN
        ↓
Return available projects
```

:chatgpt-content-reference{index="6"}

---

# 💼 5. Submit Contractor Offer

### Endpoint

```http
POST /api/v1/offers/add
```

### Request

```text
Project ID
+
Offer Price
+
Offer Details
```

### Response

```json
{
  "message": "Offer submitted successfully"
}
```

### Business Logic

```text
Check Project Exists
        ↓
Check Project Status = OPEN
        ↓
Link Offer to Contractor
        ↓
Link Offer to Project
        ↓
Save Offer
```

:chatgpt-content-reference{index="7"}

---

# 🤝 6. Accept Contractor Offer

### Endpoint

```http
PUT /api/v1/offers/accept/{...}
```

### Request

```text
Offer ID
```

### Response

```json
{
  "message": "Offer accepted successfully"
}
```

### Business Logic

```text
Check Project Ownership
        ↓
Check Offer is Valid
        ↓
Accept Offer
        ↓
Assign Contractor
        ↓
Move Project to Execution
```

:chatgpt-content-reference{index="8"}

---

# 🏗️ 7. Create Construction Phases


### Request

```text
Offer ID
```

### Validation

```text
✓ Offer exists
✓ Offer status = ACCEPTED
✓ No duplicate phase with the same name
```

### Initial Phase State

```text
status = NOT_DONE
startDate = null
actualEndDate = null
```


---

# ▶️ 8. Start Construction Phase

### Request

```text
contractorId
offerId
phaseId
```

### Validation

```text
✓ Offer exists
✓ Contractor owns the Offer
✓ Phase exists
✓ Phase belongs to the same Offer
✓ Phase status = NOT_DONE
```

### Update

```text
NOT_DONE
    ↓
IN_PROGRESS
```


```text
startDate = Today
```

:chatgpt-content-reference{index="10"}

---

# ✅ 9. Complete Construction Phase

### Request

```text
contractorId
offerId
phaseId
```

### Validation

```text
✓ Offer exists
✓ Contractor owns the Offer
✓ Phase exists
✓ Phase belongs to the Offer
✓ Phase status = IN_PROGRESS
```

### Update

```text
IN_PROGRESS
      ↓
COMPLETED
```


```text
actualEndDate = Today
```

:chatgpt-content-reference{index="11"}

---

# 🧱 10. Purchased Materials


### Request

```text
contractorId
materialsId
count
```

### Validation

```text
✓ count > 0
✓ Construction Phase must be IN_PROGRESS
✓ Cannot add purchased quantity to a closed/completed phase
```

### Business Logic

```text
purchasedQuantity
        +
new quantity
        ↓
new purchasedQuantity
        ↓
calculate surplusQuantity
```

### Formula

```text
surplusQuantity =
purchasedQuantity - usedQuantity
```


### Example

```text
Purchased = 100 + 30
Purchased = 130

Used = 20

Surplus = 130 - 20
Surplus = 110
```

---

# 🔨 11. Use Construction Materials

### Request

```text
contractorId
materialsId
count
```

### Validation

```text
✓ count > 0
✓ Phase must not be closed/completed
✓ usedQuantity + count <= purchasedQuantity
```

### Business Logic

```text
usedQuantity
      +
new usage
      ↓
new usedQuantity
      ↓
recalculate surplusQuantity
```

### Formula

```text
surplusQuantity =
purchasedQuantity - usedQuantity
```

:chatgpt-content-reference{index="13"}

### Example

```text
Purchased = 130
Used = 20 + 40

Used = 60

Surplus = 130 - 60
Surplus = 70
```

---

# ♻️ 12. Create Surplus Listing

### Request

```text
surplus-items
```

### Business Logic

The system validates:

```text
✓ Construction Phase = COMPLETED
✓ surplusQuantity > 0
✓ Seller is the project owner
✓ No duplicate listing for the same Material
  from the same Seller
```

The system automatically determines:

```text
quantity = surplusQuantity
materialType = Material type
status = AVAILABLE
```

:chatgpt-content-reference{index="14"}

---

# 🔒 13. Close Surplus Listing

### Request

```text
userId
surplusItemsId
```

### Validation

```text
✗ Cannot close SOLD listing
✗ Cannot close already CLOSED listing
```

### Status Transition

```text
AVAILABLE
     ↓
CLOSED
```

:chatgpt-content-reference{index="15"}

---

# 🔓 14. Reopen Surplus Listing

### Request

```text
userId
surplusItemsId
```

### Validation

```text
✗ Cannot reopen SOLD listing
✗ Cannot reopen AVAILABLE listing
✓ CLOSED listing can be reopened
```

### Status Transition

```text
CLOSED
   ↓
AVAILABLE
```

:chatgpt-content-reference{index="16"}

---

# 🔄 15. Transfer Surplus

### Request

```text
userId
surplusItemsId
materialsId
quantity
```

### Validation

```text
✓ Surplus must not be SOLD
✓ Target Material type must match surplus Material type
✓ quantity > 0
✓ quantity <= available surplus quantity
✓ Target Material belongs to the same surplus owner
```

### Business Logic

```text
Target Material:
purchasedQuantity += transferredQuantity

Target Material:
surplusQuantity = recalculated

Surplus Listing:
surplusQuantity -= transferredQuantity
```

If the remaining surplus becomes zero:

```text
AVAILABLE
     ↓
SOLD
```

:chatgpt-content-reference{index="17"}

---

# 🛒 16. Purchase Surplus Material

### Request

```text
surplus-orders
```

### Validation

```text
✗ User cannot buy their own surplus
✓ Surplus listing must be AVAILABLE
✓ Requested quantity <= available quantity
```

### Business Logic

A new order is created with:

```text
paid = false
```

:chatgpt-content-reference{index="18"}

---

# 💳 17. Pay for Surplus Order

### Request

```text
buyerId
surplusOrderId
```

### Validation

```text
✓ Order belongs to the buyer
✗ Cannot pay an already-paid order
✓ System verifies available quantity again
```

### Business Logic

After successful payment:

```text
Surplus Quantity
        ↓
subtract purchased quantity
        ↓
if quantity = 0
        ↓
status = SOLD
```

Then:

```text
paid = true
```

Finally, the system sends an Email to:

```text
Seller
+
Buyer
```

to confirm the purchase. :chatgpt-content-reference{index="19"}

---

# 🔌 API Summary

| # | Feature | Method | Endpoint |
|---|---|---|---|
| 1 | Verify account | POST | `/api/v1/auth/verify` |
| 2 | Add land | POST | `/api/v1/land/add` |
| 3 | Create construction project | POST | `/api/v1/construction-project/add` |
| 4 | View open projects | GET | `/api/v1/construction-project/open` |
| 5 | Submit offer | POST | `/api/v1/offers/add` |
| 6 | Accept offer | PUT | `/api/v1/offers/accept/{...}` |
| 7 | Create construction phases | — | Phase endpoint |
| 8 | Start phase | — | Phase endpoint |
| 9 | Complete phase | — | Phase endpoint |
| 10 | Add purchased materials | — | Materials endpoint |
| 11 | Use materials | — | Materials endpoint |
| 12 | Create surplus listing | — | `/surplus-items` |
| 13 | Close surplus listing | — | Surplus endpoint |
| 14 | Reopen surplus listing | — | Surplus endpoint |
| 15 | Transfer surplus | — | Surplus endpoint |
| 16 | Purchase surplus | — | `/surplus-orders` |
| 17 | Pay for order | — | Surplus order endpoint |


---

# 🧠 Business Rules

## Construction

```text
Land must exist
        ↓
User must own Land
        ↓
Create Project
        ↓
Project becomes OPEN
        ↓
Contractors submit Offers
        ↓
Owner accepts Offer
        ↓
Contractor assigned
```

## Construction Phases

```text
NOT_DONE
   ↓
IN_PROGRESS
   ↓
COMPLETED
```


## Materials

```text
Purchased Quantity
        -
Used Quantity
        =
Surplus Quantity
```


## Surplus

```text
COMPLETED Phase
       ↓
Surplus > 0
       ↓
AVAILABLE
       ↓
CLOSED ↔ AVAILABLE
       ↓
SOLD
```

---

# 🛠️ Technologies

```text
Java
Spring Boot
Spring Web
Spring Data JPA
MySQL
Jakarta Validation
Spring AI
Java Mail
Maven
Postman
Git
GitHub
```

---

# 🏛️ Architecture

```text
Controller
     ↓
Service
     ↓
Repository
     ↓
MySQL Database
```

---

# 📁 Main Modules

```text
Controller
├── UserController
├── LandsController
├── ConstructionProjectController
├── ContractorsController
├── OffersController
├── ConstructionPhasesController
├── MaterialsController
├── SurplusItemsController
├── SurplusOrdersController
├── AIController
├── EmailController
└── AdminController

Service
├── UserService
├── LandsService
├── ConstructionProjectService
├── ContractorsService
├── OffersService
├── ConstructionPhasesService
├── MaterialsService
├── SurplusItemsService
├── SurplusOrdersService
├── AIService
├── EmailService
└── AdminService
```

---
