
# BAYTI

## 1. Project Overview

BAYTI is a digital platform designed to simplify and organize the home construction process by connecting landowners with verified contractors through a structured construction workflow.

The platform allows users to add and manage their land, create construction projects, receive contractor offers, select a contractor, and follow the progress of construction phases and materials.

Contractors can browse available construction projects, submit offers, manage construction phases, and track purchased and used materials. The platform also manages surplus construction materials by allowing users to list, transfer, and purchase remaining materials.

BAYTI also provides administrative management, email notifications for important events, and AI-supported functionality to enhance the construction management experience.

The system is built using a layered architecture that separates Controllers, Services, Repositories, and Database Entities, making the application organized, maintainable, and scalable.

# 2. Main Features

## 2.1 User & Account Verification

### Description

BAYTI supports account verification for both users and contractors. Users can verify their accounts directly, while contractors require additional admin verification of their license information.

### API

**POST**

```text
/api/v1/auth/verify
```

### Request

```json
{
  "verificationCode": "123456"
}
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

1. The system receives the verification code.
2. The code is validated.
3. If the account belongs to a user:
   - The account becomes `ACCEPTED`.
   - The user can proceed to login.
4. If the account belongs to a contractor:
   - Verification succeeds.
   - The contractor remains pending until the administrator verifies the license.

### Database Relationship

```text
User
  │
  └── Account Verification

Contractor
  │
  └── Account Verification
          │
          └── Admin License Verification
```

---

# 2.2 Land Management

### Description

A verified user can add and manage their land before creating a construction project.

### API

**POST**

```text
/api/v1/land/add
```

### Request

```json
{
  "area": 500,
  "city": "Dammam"
}
```

### Response

```json
{
  "message": "Land added successfully"
}
```

### Validation

- Land data must be valid.
- The authenticated user must exist.
- The land is linked to the authenticated user.

### Business Logic

```text
Authenticated User
        ↓
Validate Land Data
        ↓
Create Land
        ↓
Link Land to User
        ↓
Save Land
```

### Database Relationship

```text
User 1 ──────── * Lands
```

---

# 2.3 Construction Project Management

### Description

After adding a land, the user can create a construction project associated with that land.

### API

**POST**

```text
/api/v1/construction-project/add
```

### Request

```json
{
  "landId": 1,
  "projectData": "..."
}
```

### Response

```json
{
  "message": "Construction project created successfully"
}
```

### Validation

The system verifies:

- Land exists.
- Land belongs to the authenticated user.
- Project data is valid.

### Business Logic

```text
User
 ↓
Select Land
 ↓
Check Land Ownership
 ↓
Create Construction Project
 ↓
Link Project to User
 ↓
Link Project to Land
 ↓
Save Project
```

### Database Relationship

```text
User
 │
 └── Lands
       │
       └── ConstructionProject
```

---

# 2.4 Open Construction Projects

### Description

Contractors can view construction projects that are currently available for offers.

### API

**GET**

```text
/api/v1/construction-project/open
```

### Request

No request body.

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

The system retrieves projects whose status is:

```text
OPEN
```

Only open projects are returned to contractors.

### Database Flow

```text
ConstructionProject
        ↓
Filter status = OPEN
        ↓
Return available projects
```

---

# 2.5 Contractor Offers

### Description

A verified contractor can submit an offer for an open construction project.

### API

**POST**

```text
/api/v1/offers/add
```

### Request

```json
{
  "projectId": 1,
  "price": 250000
}
```

### Response

```json
{
  "message": "Offer submitted successfully"
}
```

### Validation

The system checks:

- Project exists.
- Project status is `OPEN`.
- Contractor is valid.
- Offer is linked to the contractor.
- Offer is linked to the construction project.

### Database Relationship

```text
Contractor
     │
     └── Offer
            │
            └── ConstructionProject
```

---

# 2.6 Accept Contractor Offer

### Description

The land/project owner can select one of the submitted contractor offers.

### API

```text
PUT /api/v1/offers/accept/{offerId}
```

> The exact path variable format should match the implemented controller.

### Request

```text
offerId
```

### Response

```json
{
  "message": "Offer accepted successfully"
}
```

### Business Logic

```text
Project Owner
      ↓
Select Offer
      ↓
Validate Offer
      ↓
Check Project Ownership
      ↓
Accept Offer
      ↓
Assign Contractor
      ↓
Move Project to Execution
```

### Relationship

```text
User
 │
 └── ConstructionProject
          │
          └── Offer
                │
                └── Contractor
```

---

# 2.7 Construction Phase Management

### Description

After accepting an offer, construction phases can be created for the project.

### Data

Each phase contains information such as:

```text
Phase
- phase name
- status
- start date
- actual end date
- offer/project relationship
```

### Initial Status

```text
NOT_DONE
```

### Initial Dates

```text
startDate = null
actualEndDate = null
```

### Business Rules

- Offer must exist.
- Offer must have status `ACCEPTED`.
- The same phase name cannot be duplicated for the same offer.

### Flow

```text
Accepted Offer
      ↓
Create Phase
      ↓
NOT_DONE
      ↓
Start Phase
      ↓
IN_PROGRESS
      ↓
Complete Phase
      ↓
COMPLETED
```

---

# 2.8 Start Construction Phase

### Request

```text
contractorId
offerId
phaseId
```

### Business Rules

The system checks:

1. Offer exists.
2. Contractor owns the offer.
3. Phase exists.
4. Phase belongs to the same offer.
5. Phase status is `NOT_DONE`.

### Status Transition

```text
NOT_DONE → IN_PROGRESS
```

### Date

```text
startDate = Today
```

---

# 2.9 Complete Construction Phase

### Request

```text
contractorId
offerId
phaseId
```

### Business Rules

The system checks:

1. Offer exists.
2. Contractor owns the offer.
3. Phase exists.
4. Phase belongs to the offer.
5. Phase status is `IN_PROGRESS`.

### Status Transition

```text
IN_PROGRESS → COMPLETED
```

### Date

```text
actualEndDate = Today
```

---

# 2.10 Material Management

Construction materials are connected to the construction project and are managed by the contractor during execution.

The system tracks:

```text
plannedQuantity
purchasedQuantity
usedQuantity
surplusQuantity
```

### Formula

```text
surplusQuantity =
purchasedQuantity - usedQuantity
```

Example:

```text
Purchased = 130
Used      = 20

Surplus = 130 - 20
        = 110
```

---

# 2.11 Add Purchased Materials

### Request

```text
contractorId
materialId
count
```

### Validation

- `count > 0`
- Phase must not be closed/completed.
- Contractor must be authorized for the project.

### Business Logic

```text
purchasedQuantity += count

surplusQuantity =
purchasedQuantity - usedQuantity
```

If the purchased quantity exceeds the planned quantity, the project owner receives an email notification.

Example:

```text
Previous Purchased = 100
New Purchase       = 30

Purchased = 130
Used      = 20

Surplus = 110
```

---

# 2.12 Use Construction Materials

### Request

```text
contractorId
materialId
count
```

### Validation

```text
count > 0
```

The system also verifies:

```text
count + usedQuantity <= purchasedQuantity
```

### Business Logic

```text
usedQuantity += count

surplusQuantity =
purchasedQuantity - usedQuantity
```

Example:

```text
Purchased = 130
Previous Used = 20
New Used = 40

Used = 60

Surplus = 130 - 60
        = 70
```

---

# 2.13 Surplus Materials Marketplace

When a construction phase is completed, remaining materials can be offered through the surplus marketplace.

### Requirements

- Phase must be `COMPLETED`.
- `surplusQuantity > 0`.
- Only one listing can exist for the same material from the same seller.
- Seller must be the project owner.

### Listing Data

```text
Material
Quantity
Material Type
Seller
Status
```

### Initial Status

```text
AVAILABLE
```

### Flow

```text
Completed Phase
      ↓
Calculate Surplus
      ↓
Create Surplus Listing
      ↓
AVAILABLE
```

---

# 2.14 Close Surplus Listing

### Request

```text
userId
surplusItemId
```

### Business Rules

Cannot close:

```text
SOLD
```

Cannot close a listing that is already:

```text
CLOSED
```

### Status Transition

```text
AVAILABLE → CLOSED
```

---

# 2.15 Reopen Surplus Listing

### Request

```text
userId
surplusItemId
```

### Business Rules

Cannot reopen:

```text
SOLD
```

Cannot reopen:

```text
AVAILABLE
```

A closed listing can be reopened.

### Status Transition

```text
CLOSED → AVAILABLE
```

---

# 2.16 Transfer Surplus Materials

A project owner can transfer surplus quantity to another material owned by the same owner.

### Request

```text
userId
surplusItemId
materialId
quantity
```

### Validation

- Listing cannot be `SOLD`.
- Target material type must match surplus material type.
- Quantity must be greater than zero.
- Quantity cannot exceed available surplus.
- Target material must belong to the same owner.

### Business Logic

```text
Target Material
      ↓
Increase purchasedQuantity
      ↓
Recalculate surplusQuantity

Surplus Listing
      ↓
Decrease available quantity
      ↓
If quantity = 0
      ↓
SOLD
```

---

# 2.17 Purchase Surplus Materials

A user can purchase available surplus materials from another user.

### Request

```text
surplusItemId
quantity
```

### Business Rules

- User cannot purchase their own surplus.
- Listing must be `AVAILABLE`.
- Requested quantity must not exceed available quantity.

### Order

A new surplus order is created with:

```text
paid = false
```

### Flow

```text
Buyer
 ↓
Select AVAILABLE Surplus
 ↓
Check Quantity
 ↓
Create Order
 ↓
paid = false
```

---

# 2.18 Pay for Surplus Order

### Request

```text
buyerId
surplusOrderId
```

### Validation

The system checks:

- Order belongs to the buyer.
- Order has not already been paid.
- Surplus quantity is still available.

### Payment Logic

After successful payment:

```text
Available Quantity -= Order Quantity
```

If quantity becomes zero:

```text
AVAILABLE → SOLD
```

And:

```text
paid = true
```

### Notifications

An email is sent to:

```text
Seller
Buyer
```

### Complete Flow

```text
Buyer
 ↓
Create Order
 ↓
paid = false
 ↓
Payment
 ↓
Re-check Availability
 ↓
Subtract Quantity
 ↓
Update paid = true
 ↓
If quantity = 0 → SOLD
 ↓
Send Email
```

---

# 2.19 Admin Management

The administrator is responsible for platform-level management and contractor verification.

### Main Responsibilities

- Manage users.
- Monitor the platform.
- Verify contractors.
- Verify contractor license information.
- Approve contractor accounts.

### Contractor Verification Flow

```text
Contractor Registration
        ↓
Verification Code
        ↓
Code Accepted
        ↓
PENDING_ADMIN_VERIFICATION
        ↓
Admin Checks License Number
        ↓
ACCEPTED
        ↓
Contractor Can Login
```

---

# 2.20 Email Notifications

BAYTI uses email notifications for important business events.

Examples include:

### Purchased Quantity Exceeds Planned Quantity

```text
Contractor purchases more material
        ↓
Purchased > Planned
        ↓
Send Email to Project Owner
```

### Surplus Payment

```text
Payment Completed
        ↓
Send Email to Buyer
        ↓
Send Email to Seller
```

---

# 2.21 AI Features

The project also contains an AI service/controller layer integrated with the construction-project and land-related modules.

The AI functionality is implemented through the project's AI service and related repositories.

The exact AI request/response format should be documented directly from the implemented `AIController` and `AIService` methods rather than inventing an API contract.

---

# 3. Complete BAYTI Workflow

The entire platform works as one connected workflow:

```text
USER
 │
 ├── Verify Account
 │
 ├── Add Land
 │
 └── Create Construction Project
             │
             ↓
          OPEN
             │
             ↓
       Contractors
             │
             └── Submit Offers
                     │
                     ↓
              Project Owner
                     │
                     └── Accept Offer
                             │
                             ↓
                       Assign Contractor
                             │
                             ↓
                    Create Construction Phases
                             │
                             ↓
                         NOT_DONE
                             │
                             ↓
                        IN_PROGRESS
                             │
                             ├── Purchase Materials
                             │
                             ├── Use Materials
                             │
                             └── Calculate Surplus
                             │
                             ↓
                         COMPLETED
                             │
                             ↓
                    Create Surplus Listing
                             │
                    ┌────────┴─────────┐
                    ↓                  ↓
                Sell/Buy            Transfer
                    │
                    ↓
                 Order
                    │
                    ↓
                Payment
                    │
                    ↓
             Email Notifications
```

---

# 4. Database Relationship Flow

```text
User
 │
 ├─────────────── Lands
 │                    │
 │                    └──── ConstructionProject
 │                                  │
 │                                  └──── Offers
 │                                          │
 │                                          └──── Contractor
 │
 └─────────────── SurplusItems
                         │
                         └──── SurplusOrders
```

And for construction execution:

```text
ConstructionProject
        │
        └── Offer
              │
              ├── Contractor
              │
              └── ConstructionPhases
                        │
                        └── Materials
                              │
                              └── SurplusItems
                                    │
                                    └── SurplusOrders
```

---

