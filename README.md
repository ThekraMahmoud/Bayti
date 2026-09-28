
## Database Relationships + API Integration

```markdown
# Database Relationships and API Integration

BAYTI uses connected entities to manage the complete construction lifecycle.

The main relationship is:

```text
USER
  |
  | 1 : N
  v
LANDS
  |
  | 1 : N
  v
CONSTRUCTION_PROJECT
  |
  | 1 : N
  v
OFFERS
  |
  | N : 1
  v
CONTRACTORS
```

After an offer is accepted:

```text
OFFERS
  |
  | 1 : N
  v
CONSTRUCTION_PHASES
  |
  | 1 : N
  v
MATERIALS
```

After construction is completed:

```text
MATERIALS
  |
  | surplusQuantity > 0
  v
SURPLUS_ITEMS
  |
  | 1 : N
  v
SURPLUS_ORDERS
  |
  | N : 1
  v
USER
```

---

# Endpoint → Controller → Service → Repository → Database

Each API operation follows the same backend flow:

```text
HTTP Request
      |
      v
Controller
      |
      v
Service
      |
      v
Repository
      |
      v
Database
```

The Service layer is responsible for applying the business rules before changing the database.

---

# 1. Verify Account

```text
POST /api/v1/auth/verify
        |
        v
AuthController
        |
        v
Auth/User Service
        |
        +---- Validate verification code
        |
        +---- Determine user type
        |
        +---- Update verification status
        |
        v
USER / CONTRACTOR
```

### Flow

```text
User
 |
 | Verification Code
 v
POST /api/v1/auth/verify
 |
 v
Validate Code
 |
 +---- Invalid
 |       ↓
 |     Reject
 |
 +---- Valid
         ↓
      ACCEPTED
         ↓
       Login
```

For contractors:

```text
Valid Code
    ↓
PENDING_ADMIN_VERIFICATION
    ↓
Admin verifies License Number
    ↓
ACCEPTED
    ↓
Login
```

---

# 2. Add Land

```text
POST /api/v1/land/add
        |
        v
LandsController
        |
        v
LandsService
        |
        +---- Validate Land Data
        |
        +---- Get Authenticated User
        |
        +---- Link Land to User
        |
        v
LandsRepository
        |
        v
LANDS TABLE
```

### Database Relationship

```text
USER
  |
  | user_id
  v
LANDS
```

### Flow

```text
Authenticated User
        ↓
Add Land
        ↓
Validate Data
        ↓
Link Land → User
        ↓
Save Land
```

After the land is successfully created, it can be used to create a construction project.

---

# 3. Create Construction Project

```text
POST /api/v1/construction-project/add
        |
        v
ConstructionProjectController
        |
        v
ConstructionProjectService
        |
        +---- Check Land Exists
        |
        +---- Check Land Ownership
        |
        +---- Create Project
        |
        +---- Link User
        |
        +---- Link Land
        |
        v
ConstructionProjectRepository
        |
        v
CONSTRUCTION_PROJECT TABLE
```

### Database Relationships

```text
USER
  |
  | user_id
  v
CONSTRUCTION_PROJECT
  |
  | land_id
  v
LANDS
```

### Business Flow

```text
Land ID + Project Data
        ↓
Check Land Exists
        ↓
Check User Owns Land
        ↓
Create Construction Project
        ↓
Link:
    User
    +
    Land
        ↓
Save Project
```

The project can now become available to contractors.

---

# 4. View Open Projects

```text
GET /api/v1/construction-project/open
        |
        v
ConstructionProjectController
        |
        v
ConstructionProjectService
        |
        v
ConstructionProjectRepository
        |
        v
CONSTRUCTION_PROJECT TABLE
```

The repository retrieves projects where:

```text
status = OPEN
```

### Flow

```text
Contractor
    ↓
GET Open Projects
    ↓
Query CONSTRUCTION_PROJECT
    ↓
Filter status = OPEN
    ↓
Return Available Projects
```

This endpoint is the connection between the project owner workflow and the contractor workflow.

```text
USER
 ↓
LAND
 ↓
CONSTRUCTION_PROJECT
 ↓
OPEN
 ↓
CONTRACTOR
```

---

# 5. Submit Offer

```text
POST /api/v1/offers/add
        |
        v
OffersController
        |
        v
OffersService
        |
        +---- Check Project Exists
        |
        +---- Check Project Status = OPEN
        |
        +---- Get Contractor
        |
        +---- Create Offer
        |
        +---- Link Offer → Project
        |
        +---- Link Offer → Contractor
        |
        v
OffersRepository
        |
        v
OFFERS TABLE
```

### Database Relationships

```text
CONSTRUCTION_PROJECT
        |
        | project_id
        v
      OFFERS
        ^
        |
        | contractor_id
        |
  CONTRACTORS
```

### Flow

```text
Contractor
    ↓
View OPEN Projects
    ↓
Select Project
    ↓
Submit Offer
    ↓
Validate Project
    ↓
Create Offer
    ↓
Link:
    Offer → Project
    Offer → Contractor
    ↓
Save Offer
```

The offer cannot be created if the project does not exist or is not `OPEN`.

---

# 6. Accept Offer

```text
PUT /api/v1/offers/accept/{offerId}
        |
        v
OffersController
        |
        v
OffersService
        |
        +---- Check Project Ownership
        |
        +---- Check Offer
        |
        +---- Accept Offer
        |
        +---- Assign Contractor
        |
        v
OffersRepository
        |
        v
OFFERS
        |
        v
CONSTRUCTION_PROJECT
```

### Flow

```text
Land Owner
    ↓
Select Offer
    ↓
Check Project Ownership
    ↓
Check Offer
    ↓
Accept Offer
    ↓
Assign Contractor
    ↓
Project moves to execution
```

### Relationship

```text
USER
  |
  | owns
  v
CONSTRUCTION_PROJECT
  |
  | receives
  v
OFFERS
  |
  | submitted by
  v
CONTRACTORS
```

After acceptance:

```text
ACCEPTED OFFER
      ↓
ASSIGNED CONTRACTOR
      ↓
CONSTRUCTION PHASES
```

---

# 7. Create Construction Phase

```text
Offer ID
   |
   v
ConstructionPhasesController
   |
   v
ConstructionPhasesService
   |
   +---- Check Offer Exists
   |
   +---- Check Offer = ACCEPTED
   |
   +---- Check Duplicate Phase
   |
   v
ConstructionPhasesRepository
   |
   v
CONSTRUCTION_PHASES TABLE
```

### Relationship

```text
OFFERS
  |
  | offer_id
  v
CONSTRUCTION_PHASES
```

### Initial State

```text
status = NOT_DONE
startDate = null
actualEndDate = null
```

---

# 8. Start Construction Phase

```text
contractorId
offerId
phaseId
        |
        v
ConstructionPhasesController
        |
        v
ConstructionPhasesService
        |
        +---- Offer Exists
        |
        +---- Contractor Owns Offer
        |
        +---- Phase Exists
        |
        +---- Phase Belongs to Offer
        |
        +---- Phase = NOT_DONE
        |
        +---- Change Status
        |
        +---- Set startDate
        |
        v
CONSTRUCTION_PHASES
```

### Status Transition

```text
NOT_DONE
    |
    | Start
    v
IN_PROGRESS
```

### Database Update

```text
status = IN_PROGRESS
startDate = Today
```

---

# 9. Material Purchase

```text
contractorId
materialsId
count
        |
        v
MaterialsController
        |
        v
MaterialsService
        |
        +---- Check count > 0
        |
        +---- Check Phase is active
        |
        +---- Increase purchasedQuantity
        |
        +---- Calculate surplusQuantity
        |
        +---- Check plannedQuantity
        |
        +---- Send Email if exceeded
        |
        v
MaterialsRepository
        |
        v
MATERIALS TABLE
```

### Calculation

```text
purchasedQuantity
        -
usedQuantity
        =
surplusQuantity
```

Example:

```text
Purchased = 130
Used      = 20

Surplus = 110
```

---

# 10. Material Usage

```text
contractorId
materialsId
count
        |
        v
MaterialsController
        |
        v
MaterialsService
        |
        +---- Check count > 0
        |
        +---- Check Phase is active
        |
        +---- Check:
        |     usedQuantity + count
        |     <= purchasedQuantity
        |
        +---- Increase usedQuantity
        |
        +---- Recalculate surplus
        |
        v
MaterialsRepository
        |
        v
MATERIALS TABLE
```

Example:

```text
Purchased = 130
Used      = 60

Surplus = 70
```

---

# 11. Complete Construction Phase

```text
contractorId
offerId
phaseId
        |
        v
ConstructionPhasesController
        |
        v
ConstructionPhasesService
        |
        +---- Check Offer
        |
        +---- Check Contractor Ownership
        |
        +---- Check Phase
        |
        +---- Check Phase → Offer
        |
        +---- Check Phase = IN_PROGRESS
        |
        +---- Set COMPLETED
        |
        +---- Set actualEndDate
        |
        v
CONSTRUCTION_PHASES
```

### Status Transition

```text
NOT_DONE
    ↓
IN_PROGRESS
    ↓
COMPLETED
```

Materials are managed while the phase is active, then the completed phase allows the remaining material surplus to move into the surplus workflow.

---

# 12. Create Surplus Listing

```text
surplus-items
        |
        v
SurplusItemsController
        |
        v
SurplusItemsService
        |
        +---- Check Phase = COMPLETED
        |
        +---- Check surplusQuantity > 0
        |
        +---- Check Seller owns Project
        |
        +---- Check duplicate listing
        |
        +---- Set quantity
        |
        +---- Set material type
        |
        +---- Set AVAILABLE
        |
        v
SurplusItemsRepository
        |
        v
SURPLUS_ITEMS TABLE
```

### Relationship

```text
MATERIALS
    |
    | material_id
    v
SURPLUS_ITEMS
    |
    | seller_id
    v
USER
```

### Flow

```text
COMPLETED PHASE
       ↓
surplusQuantity > 0
       ↓
Create Listing
       ↓
AVAILABLE
```

---

# 13. Close Surplus Listing

```text
userId
surplusItemsId
        |
        v
SurplusItemsController
        |
        v
SurplusItemsService
        |
        +---- Check Listing
        |
        +---- Cannot be SOLD
        |
        +---- Cannot already be CLOSED
        |
        +---- AVAILABLE → CLOSED
        |
        v
SURPLUS_ITEMS
```

---

# 14. Reopen Surplus Listing

```text
userId
surplusItemsId
        |
        v
SurplusItemsController
        |
        v
SurplusItemsService
        |
        +---- Check Listing
        |
        +---- Cannot be SOLD
        |
        +---- Cannot already be AVAILABLE
        |
        +---- CLOSED → AVAILABLE
        |
        v
SURPLUS_ITEMS
```

---

# 15. Transfer Surplus

```text
userId
surplusItemsId
materialsId
quantity
        |
        v
SurplusItemsController
        |
        v
SurplusItemsService
        |
        +---- Check Listing != SOLD
        |
        +---- Check Material Types Match
        |
        +---- Check quantity > 0
        |
        +---- Check quantity <= surplus
        |
        +---- Check Target Material Owner
        |
        +---- Update Target Material
        |
        +---- Update Surplus
        |
        v
+----------------------+
|                      |
v                      v
MATERIALS          SURPLUS_ITEMS
```

### Transfer Effect

```text
Target Material
    |
    +--> purchasedQuantity increases
    |
    +--> surplusQuantity recalculated


Surplus Listing
    |
    +--> quantity decreases
```

If quantity becomes zero:

```text
AVAILABLE
    ↓
SOLD
```

---

# 16. Create Surplus Order

```text
surplus-orders
        |
        v
SurplusOrdersController
        |
        v
SurplusOrdersService
        |
        +---- Buyer != Seller
        |
        +---- Listing = AVAILABLE
        |
        +---- Requested Quantity <= Available
        |
        +---- Create Order
        |
        +---- paid = false
        |
        v
SurplusOrdersRepository
        |
        v
SURPLUS_ORDERS TABLE
```

### Relationship

```text
SURPLUS_ITEMS
      |
      | surplus_item_id
      v
SURPLUS_ORDERS
      |
      | buyer_id
      v
USER
```

---

# 17. Pay Surplus Order

```text
buyerId
surplusOrderId
        |
        v
SurplusOrdersController
        |
        v
SurplusOrdersService
        |
        +---- Check Order belongs to Buyer
        |
        +---- Check paid = false
        |
        +---- Re-check available quantity
        |
        +---- Deduct surplus quantity
        |
        +---- Update Order
        |
        +---- paid = true
        |
        +---- Send Email
        |
        v
+-----------------------+
|                       |
v                       v
SURPLUS_ORDERS      SURPLUS_ITEMS
```

### Final Flow

```text
Order
  ↓
Payment
  ↓
Validate Buyer
  ↓
Validate Quantity
  ↓
Deduct Surplus
  ↓
paid = true
  ↓
Quantity = 0 ?
  |
  +---- YES → SOLD
  |
  +---- NO  → AVAILABLE
  ↓
Send Email
  ↓
Buyer + Seller
```

---

# Complete Integrated Flow

```text
                    USER
                     |
                     | Verify
                     v
                  AUTH
                     |
                     v
                  LANDS
                     |
                     | Add Land
                     v
          CONSTRUCTION_PROJECT
                     |
                     | status = OPEN
                     v
              OPEN PROJECTS
                     |
                     | Contractor views
                     v
                CONTRACTOR
                     |
                     | Submit Offer
                     v
                  OFFERS
                     |
                     | Owner accepts
                     v
             ACCEPTED OFFER
                     |
                     | Contractor assigned
                     v
          CONSTRUCTION_PHASES
                     |
                     | Start
                     v
                IN_PROGRESS
                     |
             +-------+-------+
             |               |
             v               v
        PURCHASE          USE
        MATERIAL          MATERIAL
             |               |
             +-------+-------+
                     |
                     v
                 MATERIALS
                     |
                     | purchased - used
                     v
              SURPLUS QUANTITY
                     |
                     | Complete Phase
                     v
                COMPLETED
                     |
                     | Create Listing
                     v
              SURPLUS_ITEMS
                     |
                     | AVAILABLE
             +-------+-------+
             |               |
             v               v
           CLOSED          ORDER
             |               |
             | Reopen        | Pay
             |               v
             +----------> AVAILABLE
                             |
                             v
                            SOLD
```

# Controller → Service → Repository → Database

```text
UserController
      ↓
UserService
      ↓
UserRepository
      ↓
USER


LandsController
      ↓
LandsService
      ↓
LandsRepository
      ↓
LANDS


ConstructionProjectController
      ↓
ConstructionProjectService
      ↓
ConstructionProjectRepository
      ↓
CONSTRUCTION_PROJECT


OffersController
      ↓
OffersService
      ↓
OffersRepository
      ↓
OFFERS


ConstructionPhasesController
      ↓
ConstructionPhasesService
      ↓
ConstructionPhasesRepository
      ↓
CONSTRUCTION_PHASES


MaterialsController
      ↓
MaterialsService
      ↓
MaterialsRepository
      ↓
MATERIALS


SurplusItemsController
      ↓
SurplusItemsService
      ↓
SurplusItemsRepository
      ↓
SURPLUS_ITEMS


SurplusOrdersController
      ↓
SurplusOrdersService
      ↓
SurplusOrdersRepository
      ↓
SURPLUS_ORDERS
```

# Final Entity Relationship Flow

```text
USER
 |
 +----> LANDS
 |          |
 |          +----> CONSTRUCTION_PROJECT
 |                       |
 |                       +----> OFFERS
 |                                  |
 |                    +-------------+-------------+
 |                    |                           |
 |                    v                           v
 |               CONTRACTORS             CONSTRUCTION_PHASES
 |                                                |
 |                                                v
 |                                            MATERIALS
 |                                                |
 |                                                v
 |                                          SURPLUS_ITEMS
 |                                                |
 |                                                v
 +---------------------------------------- SURPLUS_ORDERS
```

This means every major API endpoint is connected to the next step in the business process rather than being documented as an isolated endpoint.
```

