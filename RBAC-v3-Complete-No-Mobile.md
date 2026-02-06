# TravelBooking-Quarkus: RBAC Architecture Specifications v3.0
## Admin & Website Controllers Only - With Design Pattern Explanations

---

## 📋 DOCUMENT OVERVIEW

This is the **UPGRADED RBAC Architecture Specifications v3.0** containing:
- **ADMIN Controllers Only** (14 controllers, 80+ methods) - SUPER_ADMIN exclusive
- **WEBSITE Controllers Only** (18 controllers, 120+ methods) - Full-featured web interface
- **NO MOBILE Controllers** (Removed for faster implementation)
- **Complete Design Pattern Documentation** - Explained for AI agent implementation
- **Total: 32 controllers, 200+ methods**

---

## 🏗️ ARCHITECTURAL DESIGN PATTERNS EXPLAINED

### Pattern 1: COMPLETE METHOD SEPARATION (No Inheritance)

**What This Means:**
- Each controller is a standalone class with NO parent class
- Each method is independent with its own business logic
- NO code sharing between controllers or methods
- NO inheritance hierarchy (NO extends keyword)

**Why This Design:**
- Easy to test each method independently
- No dependency hell or circular dependencies
- Easy to modify one method without affecting others
- Clear separation of concerns
- Better for AI code generation (each method is self-contained)

**Implementation Rule:**
```
✅ DO: Create controller without extends
    public class AdminUserController {
        Method 1: Complete standalone logic
        Method 2: Complete standalone logic
        Method 3: Complete standalone logic
    }

❌ DON'T: Create inheritance chains
    public class AdminUserController extends BaseController {
        // Shared code from base
    }

❌ DON'T: Share code between methods
    private commonLogic() { ... } // Used by multiple methods
```

**Benefit for AI Agent:**
- Each method can be implemented independently
- No need to understand parent classes
- No need to trace inheritance chains
- Faster code generation and testing

---

### Pattern 2: ROLE-BASED ACCESS CONTROL (RBAC)

**What This Means:**
- Every endpoint explicitly defines WHO can access it
- Four roles: SUPER_ADMIN, TRAVELER, SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER
- Authorization checked before business logic
- Resource ownership verified where applicable

**Why This Design:**
- Security-first approach
- Clear permission model
- Easy to audit and review
- Prevents unauthorized access

**Authorization Levels:**

**Level 1: Role-Only Authorization**
```
@Authorized(roles = "SUPER_ADMIN")
→ Only users with SUPER_ADMIN role can call
→ No other checks needed
→ Example: Admin dashboard

@Authorized(roles = {"TRAVELER", "SUPPLIER_SUBSCRIBER"})
→ Multiple roles allowed
→ Example: View own profile
```

**Level 2: Role + Ownership Authorization**
```
@Authorized(roles = "TRAVELER", requireOwner = true)
→ TRAVELER role + must own the resource
→ Example: Cancel own booking (travelerId == currentUserId)

@Authorized(roles = "SUPPLIER_SUBSCRIBER", requireOwner = true)
→ SUPPLIER role + must own the accommodation
→ Example: Update own accommodation details
```

**Level 3: Admin Authority**
```
@Authorized(roles = "SUPER_ADMIN")
→ Full override authority
→ Can perform any action
→ Can bypass normal restrictions
→ Example: Refund any payment, ban any user
```

**Implementation Rule for AI:**
```
EVERY endpoint MUST have @Authorized annotation

Examples:

AdminUserController:
  ✅ @Authorized(roles = "SUPER_ADMIN")
  public void disableUser(@PathParam("userId") Long userId) { ... }

WebUserController (TRAVELER):
  ✅ @Authorized(roles = "TRAVELER", requireOwner = true)
  public void updateMyProfile(...) { ... }

WebBookingController (both roles different actions):
  ✅ @Authorized(roles = "TRAVELER", requireOwner = true)
  public void cancelBooking(...) { ... }
  
  ✅ @Authorized(roles = "SUPPLIER_SUBSCRIBER")
  public void confirmBooking(...) { ... }
```

---

### Pattern 3: SOFT DELETE (Data Preservation)

**What This Means:**
- Never permanently delete data (hard delete)
- Mark records as deleted with timestamp
- Preserve historical data for audit/compliance
- Keep relationships intact

**Why This Design:**
- GDPR compliance (right to be forgotten = soft delete)
- Audit trail preservation
- Can restore if needed
- Legal protection (data retention)
- Historical analysis possible

**Implementation Rule:**
```
Database Fields Added to All Entities:
├─ deleted (Boolean, default=false)
├─ deletedAt (LocalDateTime, null if not deleted)
├─ deletedBy (Long, admin who deleted)
└─ deletionReason (String, why was it deleted)

ALL Queries MUST Filter:
✅ WHERE deleted = false  (exclude soft-deleted)
❌ Don't query deleted records in normal listings

Restore Logic:
✅ Set deleted = false
✅ Clear deletedAt, deletedBy, deletionReason
✅ User can resume activity
```

**Example Soft Deletes:**
- Delete user account → User can no longer login
- Delete accommodation → Listing hidden, existing bookings honored
- Delete reel → Content hidden from feed, data preserved
- Delete message → Shows "deleted" in chat thread

---

### Pattern 4: PERMISSION-BASED OPERATIONS

**What This Means:**
- Not just checking role, but checking specific permission
- Same role can have different permission levels
- Granular control over who can do what

**Why This Design:**
- More flexible than role-based alone
- Can grant specific permissions to users
- Easy to add new permissions without changing roles
- Fine-grained access control

**Example:**
```
Role: SUPER_ADMIN (has ALL permissions)
├─ Can create users
├─ Can delete accommodations
├─ Can process refunds
├─ Can view all payments
└─ Can ban users

Role: TRAVELER (has limited permissions)
├─ Can view accommodations (public list)
├─ Can create bookings (with own funds)
├─ Can write reviews (verified bookings only)
└─ Can delete own messages

Role: SUPPLIER_SUBSCRIBER (business permissions)
├─ Can create own accommodations
├─ Can view own bookings
├─ Can manage own pricing
└─ Can view own analytics
```

---

### Pattern 5: LAYERED ARCHITECTURE (Controller → Service → Repository → Database)

**What This Means:**
- Clear separation between layers
- Each layer has specific responsibility
- Data flows through layers consistently

**Why This Design:**
- Easy to test each layer independently
- Easy to modify database without changing controllers
- Business logic centralized in service layer
- Repository handles database operations

**Layer Responsibilities:**

**Controller Layer** (HTTP Entry Point)
```
Responsibility:
✓ Accept HTTP requests
✓ Validate authorization (@Authorized)
✓ Parse request parameters
✓ Call service layer
✓ Return HTTP responses

Rules:
✓ NO business logic here
✓ NO database queries
✓ NO complex calculations
✓ Minimal error handling (delegates to service)

Example:
@Authorized(roles = "SUPER_ADMIN")
public Response disableUser(@PathParam("userId") Long userId) {
    userManagementService.disableUserAccount(userId);  // ← Call service
    return Response.ok("User disabled").build();        // ← Return response
}
```

**Service Layer** (Business Logic)
```
Responsibility:
✓ Implement business logic
✓ Validate data
✓ Coordinate database operations
✓ Handle errors and exceptions
✓ Perform calculations
✓ Create audit logs

Rules:
✓ NO HTTP operations
✓ Call repositories for data
✓ Transaction management here
✓ Complex logic goes here

Example:
public void disableUserAccount(Long userId) {
    User user = userRepository.findById(userId);           // ← Query DB
    validateUser(user);                                     // ← Validate
    user.setStatus(UserStatus.DISABLED);                   // ← Business logic
    user.setDisabledAt(LocalDateTime.now());
    userRepository.persist(user);                          // ← Update DB
    auditService.log("User disabled", userId);             // ← Audit log
}
```

**Repository Layer** (Database Access)
```
Responsibility:
✓ Database queries
✓ Database updates/deletes
✓ Complex queries (filtering, searching)
✓ Transaction commits

Rules:
✓ NO business logic
✓ NO HTTP operations
✓ Simple CRUD operations
✓ Query methods only

Example:
public User findById(Long userId) { ... }
public void persist(User user) { ... }
public void update(User user) { ... }
public List<User> getActiveUsers() { ... }
```

**Database Layer** (Persistence)
```
Responsibility:
✓ Store data
✓ Execute SQL
✓ Maintain relationships

Rules:
✓ Entities only
✓ JPA annotations
✓ No business logic
```

---

### Pattern 6: STATELESS DESIGN WITH JWT TOKENS

**What This Means:**
- Server doesn't store session data
- Client includes token with each request
- Token validated on every request
- Scalable across multiple servers

**Why This Design:**
- Stateless = easily scalable
- No session state to manage
- Token contains all info needed
- Mobile-friendly (no cookies needed)
- Multiple servers can validate same token

**Implementation Rule:**
```
Client Flow:
1. Login endpoint → Generate JWT token
2. Store token on client
3. Include token in Authorization header for every request
4. Server validates token on every request
5. Extract userId from token
6. Check user role and permissions
7. Execute endpoint
8. Return response

Token Contains:
├─ userId (WHO is this?)
├─ role (WHAT role?)
├─ expirationTime (WHEN does it expire?)
└─ Signature (cryptographically signed)

No session data stored on server for this user.
Token is self-contained and verifiable.
```

---

### Pattern 7: BUSINESS OPERATION AUDITING

**What This Means:**
- Log all important operations
- Track WHO did WHAT and WHEN
- Preserve complete audit trail
- Compliance and legal protection

**Why This Design:**
- Legal compliance (many regulations require audit trails)
- Security monitoring (detect suspicious activity)
- Troubleshooting (trace what happened)
- Accountability (who changed what)

**Audit Log Requirements:**

**Operations That MUST Be Logged:**
```
Admin Actions:
├─ User creation, updates, deletion, banning, suspending
├─ Payment refunds
├─ Content approval/rejection (reels, accommodations)
├─ Currency/fee changes
├─ Account disabling/suspension/banning
└─ Any system-wide changes

Sensitive User Actions:
├─ Large payments
├─ Account deletions
├─ Multiple failed login attempts
├─ Permission changes
└─ Booking cancellations with refunds

What to Log:
├─ WHO (user ID, admin ID)
├─ WHAT (operation description, affected resource ID)
├─ WHEN (timestamp)
├─ RESULT (success/failure, error if failed)
└─ REASON (why was it done, if applicable)

Example Log Entry:
{
  timestamp: "2024-02-06T10:30:00Z",
  adminId: 1,
  operation: "USER_BANNED",
  resourceId: 456,
  reason: "Fraudulent activity detected",
  status: "SUCCESS"
}
```

---

### Pattern 8: VALIDATION AT MULTIPLE LEVELS

**What This Means:**
- Validate at multiple points in request lifecycle
- Early validation prevents errors
- Multiple validation layers catch issues

**Why This Design:**
- Fail fast (don't do work if data invalid)
- Better error messages
- Security (prevent bad data)
- Consistency

**Validation Levels:**

**Level 1: Input Validation (Controller)**
```
✓ Check required fields present
✓ Check field formats valid
✓ Check length constraints
✓ Check value ranges

Example:
email must be valid email format
password must be minimum 8 characters
userId must be positive integer
```

**Level 2: Business Logic Validation (Service)**
```
✓ Check business rules
✓ Check relationships valid
✓ Check state transitions allowed
✓ Check authorization

Example:
User cannot cancel booking if already started
Supplier cannot create accommodation if not verified
Traveler cannot write review if booking not completed
```

**Level 3: Database Constraints (Database)**
```
✓ NOT NULL constraints
✓ UNIQUE constraints
✓ FOREIGN KEY constraints
✓ Data type checks

Example:
Email must be unique (UNIQUE constraint)
User ID cannot be null (NOT NULL)
Booking must reference valid accommodation (FOREIGN KEY)
```

---

### Pattern 9: ERROR HANDLING STRATEGY

**What This Means:**
- Consistent error responses
- Clear error messages
- Proper HTTP status codes

**Why This Design:**
- Client knows what went wrong
- Easy to debug
- Professional API behavior
- Consistent across endpoints

**Error Response Pattern:**
```
Success Response:
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2024-02-06T10:30:00Z"
}

Error Response:
{
  "success": false,
  "message": "User not found",
  "errorCode": "NOT_FOUND",
  "timestamp": "2024-02-06T10:30:00Z"
}

HTTP Status Codes:
├─ 200 OK → Successful operation
├─ 201 Created → Resource created
├─ 400 Bad Request → Invalid input
├─ 401 Unauthorized → Not authenticated
├─ 403 Forbidden → Not authorized (no permission)
├─ 404 Not Found → Resource doesn't exist
├─ 409 Conflict → State conflict (e.g., booking already exists)
└─ 500 Internal Server Error → Server error

Error Types to Handle:
├─ ValidationException → 400 Bad Request
├─ UnauthorizedException → 403 Forbidden
├─ NotFoundException → 404 Not Found
├─ ConflictException → 409 Conflict
└─ SystemException → 500 Internal Server Error
```

---

### Pattern 10: PAGINATION FOR LARGE DATASETS

**What This Means:**
- Don't return all results at once
- Return results in pages (chunks)
- Client can request specific page

**Why This Design:**
- Performance (small responses)
- Memory efficiency (don't load everything)
- Better user experience (faster load times)
- Mobile-friendly (less data transfer)

**Implementation Rule:**
```
Default Pagination:
├─ Page size: 20 items
├─ Start page: 0
├─ Sorted by: relevant field

Request Parameters:
├─ page (query): 0, 1, 2, ... (page number)
├─ size (query): 10-50 (items per page)
└─ sortBy (query): field to sort by

Response Format:
{
  "data": [ ... 20 items ... ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 20,
    "totalItems": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrevious": false
  }
}

Example URLs:
GET /api/v1/admin/users?page=0&size=20
→ First 20 users
GET /api/v1/admin/users?page=1&size=20
→ Next 20 users (21-40)
GET /api/v1/admin/users?page=7&size=20
→ Last users
```

---

## 🔐 AUTHORIZATION SYSTEM ARCHITECTURE

### Authorization Annotation (`@Authorized`)
```
Location: src/main/java/com/travel/security/annotation/Authorized.java

Purpose: Decorator for methods to specify access control

Properties:
├─ roles: String[] = Array of allowed roles
├─ requireOwner: Boolean = User must own resource
└─ message: String = Custom error message

Usage Examples:

@Authorized(roles = "SUPER_ADMIN")
→ Only SUPER_ADMIN can call

@Authorized(roles = {"TRAVELER", "SUPPLIER_SUBSCRIBER"})
→ Either TRAVELER or SUPPLIER can call

@Authorized(roles = "TRAVELER", requireOwner = true)
→ TRAVELER + must own the resource

@Authorized(roles = "SUPER_ADMIN")
public Response banUser(@PathParam("userId") Long userId) {
    // Only admin can execute
}
```

### AuthorizationInterceptor
```
Location: src/main/java/com/travel/security/interceptor/AuthorizationInterceptor.java

Purpose: Intercepts every request with @Authorized annotation

Execution Flow:
1. Extract JWT token from Authorization header
2. Parse token to get userId
3. Load user from database
4. Check if user role is in allowed roles
5. If requireOwner = true:
   - Extract resource ID from request
   - Verify user owns the resource
6. If all checks pass: Allow request to proceed
7. If any check fails: Throw UnauthorizedException

Benefits:
├─ Centralized authorization (not repeated in each method)
├─ Consistent authorization across all endpoints
├─ Easy to audit who can access what
└─ Easy to modify authorization rules
```

### SecurityContext Helper
```
Location: src/main/java/com/travel/security/SecurityContext.java

Purpose: Helper methods for authorization checks

Key Methods:
├─ getCurrentUser() → Get authenticated user
├─ getCurrentUserId() → Get user ID from JWT
├─ isCurrentUserAdmin() → Check if SUPER_ADMIN
├─ isResourceOwner(userId, resourceId) → Check ownership
└─ isMessageOwner(messageId, userId) → Check message ownership

Benefits:
├─ Reusable authorization logic
├─ DRY principle (Don't Repeat Yourself)
├─ Easy to change authorization rules
└─ Centralized user context
```

---

## 👨‍💼 ADMIN CONTROLLERS (SUPER_ADMIN ONLY)

### Design Pattern for Admin Controllers

**Core Principle:**
- SUPER_ADMIN has FULL PLATFORM ACCESS
- Can manage any user, any resource, any setting
- Can override normal restrictions
- All actions are audited

**Authorization Pattern:**
```
@Authorized(roles = "SUPER_ADMIN")
public class AdminUserController {
    // Every method here requires SUPER_ADMIN role
    // Authorization annotation on each public method
}

@Authorized(roles = "SUPER_ADMIN")
public Response disableUser(@PathParam("userId") Long userId) { ... }

@Authorized(roles = "SUPER_ADMIN")
public Response processRefund(@PathParam("paymentId") Long paymentId) { ... }

@Authorized(roles = "SUPER_ADMIN")
public Response approveAccommodation(@PathParam("accommodationId") Long id) { ... }
```

**Responsibility:**
- Manage all users (create, update, delete, ban, suspend)
- Manage system settings (currency, fees)
- Content moderation (approve/reject reels, accommodations, events)
- Payment management (view all, process refunds)
- Analytics and reporting
- Fraud detection and trust management
- Account security management
- Supplier tools and verification

---

### Complete Admin Controllers List

#### 1. AdminUserController
**Purpose:** Manage all users across platform
**Methods:** 8
- View all users (with filters)
- Create user manually
- View user details
- Update user information
- Disable user account
- Suspend user temporarily
- Ban user permanently
- Restore user account

#### 2. AdminCurrencyController
**Purpose:** Manage platform currencies and exchange rates
**Methods:** 5
- Get all currencies
- Create new currency
- Update exchange rate
- Disable currency
- Set base currency

#### 3. AdminFeeController
**Purpose:** Manage platform fees and commissions
**Methods:** 7
- Get current fee structure
- Update booking fee
- Update supplier commission
- Update association commission
- Update payment processing fee
- Get fee history
- Create fee version (snapshot)

#### 4. AdminReelController
**Purpose:** Moderate travel reels content
**Methods:** 7
- View pending reels
- Approve reel
- Reject reel
- Delete reel
- View engagement stats
- Manage categories
- Handle reports/flags

#### 5. AdminPaymentController
**Purpose:** Monitor and manage all payments
**Methods:** 8
- View all payments
- Filter by status
- Filter by date range
- Filter by supplier
- Process refund
- View statistics
- View Stripe webhooks
- Manage disputes/chargebacks

#### 6. AdminAccommodationController
**Purpose:** Review and manage all accommodations
**Methods:** 8
- View all accommodations
- Approve accommodation
- Reject accommodation
- Inspect details
- View reviews
- View analytics
- Suspend accommodation
- Delete accommodation

#### 7. AdminReportController
**Purpose:** Generate platform analytics and reports
**Methods:** 7
- Dashboard summary
- Revenue report
- User growth report
- Supplier performance report
- Booking analysis report
- Accommodation performance report
- Custom report generator

#### 8. AdminBookingController
**Purpose:** Admin-level booking management
**Methods:** 6
- View all bookings
- Get booking details
- Modify booking (override)
- Cancel booking (override)
- Force confirm booking
- View analytics

#### 9. AdminReviewController
**Purpose:** Manage review quality and authenticity
**Methods:** 6
- View all reviews
- Get review details
- Flag/remove review
- Restore review
- View reports/flags
- Update verification status

#### 10. AdminEventController
**Purpose:** Approve and manage events
**Methods:** 5
- View all events
- Approve event
- Reject event
- Cancel event
- View analytics

#### 11. AdminSupplierToolsController ✅ NEW
**Purpose:** Manage supplier accounts and verification
**Methods:** 8
- View verification queue
- Approve supplier verification
- Reject supplier verification
- Manage payout accounts
- Process manual payout
- Suspend supplier account
- Remove supplier
- View performance issues

#### 12. AdminTrustController ✅ NEW
**Purpose:** Fraud detection and trust management
**Methods:** 10
- View fraud dashboard
- View suspicious users
- View suspicious bookings
- Block user account (fraud)
- Flag suspicious payment
- Manage identity verification requests
- Approve identity verification
- Reject identity verification
- Monitor payment disputes
- Create trust alert

#### 13. AdminAccountController
**Purpose:** Account security and verification
**Methods:** 8
- View suspended accounts
- View banned accounts
- Monitor suspicious activity
- Manual account unlock
- Manage 2FA
- Force 2FA for user
- Email verification management
- Phone verification management

#### 14. AdminDashboardController
**Purpose:** Admin dashboard and quick access
**Methods:** 4
- Get dashboard summary
- Get pending approvals queue
- Get quick stats
- Get system notifications

---

## 🌐 WEBSITE CONTROLLERS (Full-Featured)

### Design Pattern for Website Controllers

**Core Principle:**
- Multiple roles with different permissions
- TRAVELER, SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER
- Each role has specific endpoints and limitations
- Resource ownership enforced

**Authorization Pattern:**
```
For Public Endpoints (no auth needed):
// No @Authorized annotation
public Response searchAccommodations(...) { ... }

For Role-Specific Endpoints:
@Authorized(roles = "TRAVELER")
public Response createBooking(...) { ... }

@Authorized(roles = "SUPPLIER_SUBSCRIBER")
public Response createAccommodation(...) { ... }

For Ownership-Required Endpoints:
@Authorized(roles = "TRAVELER", requireOwner = true)
public Response cancelMyBooking(...) { ... }

@Authorized(roles = "SUPPLIER_SUBSCRIBER", requireOwner = true)
public Response updateMyAccommodation(...) { ... }

For Multiple Roles (Different Actions):
@Authorized(roles = "TRAVELER")
public Response cancelBooking(...) { ... }

@Authorized(roles = "SUPPLIER_SUBSCRIBER")
public Response confirmBooking(...) { ... }
```

**Responsibility per Role:**

**TRAVELER (Travelers/Guests)**
- Search and view accommodations
- Create and manage bookings
- Process payments
- Write reviews (verified bookings only)
- Upload travel reels
- Send messages to suppliers
- View own profile and preferences
- View own notifications
- Wishlist management

**SUPPLIER_SUBSCRIBER (Accommodation Owners/Suppliers)**
- Create and manage accommodations
- View and confirm/reject bookings
- Manage pricing and seasonal rates
- View own analytics and earnings
- Manage photos and Q&A
- Upload reels (for promotion)
- Message guests
- Supplier dashboard and tools

**ASSOCIATION_MANAGER (Event Organizers)**
- Create and manage events
- Manage event registrations and programs
- View member analytics
- Message members
- Upload event reels
- Manage association dashboard
- Compliance and safety management

---

### Complete Website Controllers List

#### 1. WebAuthController
**Purpose:** User authentication for web
**Methods:** 9
- Register account
- Login
- OAuth Google
- OAuth Apple
- OAuth Microsoft
- 2FA setup
- Forgot password
- Reset password
- Verify email

#### 2. WebAccommodationController
**Purpose:** Full accommodation lifecycle (SUPPLIER)
**Methods:** 12
- Advanced search (for TRAVELER)
- Get full details (for TRAVELER)
- Create accommodation (SUPPLIER)
- View my accommodations (SUPPLIER)
- Update accommodation (SUPPLIER)
- Delete accommodation (SUPPLIER)
- Upload photos (SUPPLIER)
- Manage pricing (SUPPLIER)
- Get analytics (SUPPLIER)
- Manage Q&A (SUPPLIER)
- View verification docs (SUPPLIER)

#### 3. WebBookingController
**Purpose:** Complete booking lifecycle
**Methods:** 13
- Create booking (TRAVELER)
- View my bookings (TRAVELER)
- Get details (both)
- Modify booking (TRAVELER)
- Cancel booking (TRAVELER)
- Write review (TRAVELER)
- View supplier bookings (SUPPLIER)
- Confirm booking (SUPPLIER)
- Reject booking (SUPPLIER)
- Request modification (SUPPLIER)
- Get pending (SUPPLIER)
- View analytics (SUPPLIER)

#### 4. WebReelController
**Purpose:** Travel reels content management
**Methods:** 10
- View feed (all)
- Upload reel (all)
- Manage my reels (creator)
- Get details (all)
- Edit reel (creator)
- Delete reel (creator)
- Like/unlike (all)
- Comment (all)
- Delete comment (creator)
- Get analytics (creator)

#### 5. WebReviewController
**Purpose:** Review management and moderation
**Methods:** 8
- View reviews (all)
- Write review (TRAVELER)
- Edit review (reviewer)
- Delete review (reviewer)
- Respond to review (SUPPLIER)
- Mark helpful (all)
- Flag review (all)
- Get analytics (SUPPLIER)

#### 6. WebUserController
**Purpose:** User profile and account management
**Methods:** 12
- Get profile (authenticated)
- Edit profile (owner)
- Upload picture (owner)
- Delete account (owner)
- Account settings (owner)
- Security settings (owner)
- Payment methods (TRAVELER)
- Transaction history (TRAVELER)
- Download invoice (TRAVELER)
- Preferences (owner)
- Wishlist (TRAVELER)
- Statistics (owner)

#### 7. WebSupplierController
**Purpose:** Supplier-specific dashboard and tools
**Methods:** 10
- Get dashboard (SUPPLIER)
- Get analytics (SUPPLIER)
- Manage accommodations (SUPPLIER)
- Manage bookings (SUPPLIER)
- Manage reviews (SUPPLIER)
- Payout management (SUPPLIER)
- Verification management (SUPPLIER)
- Communication center (SUPPLIER)
- Get reports (SUPPLIER)
- Manage house rules (SUPPLIER)

#### 8. WebAssociationController
**Purpose:** Association manager tools
**Methods:** 12
- Get dashboard (ASSOCIATION_MANAGER)
- Get analytics (ASSOCIATION_MANAGER)
- Create event (ASSOCIATION_MANAGER)
- Manage events (ASSOCIATION_MANAGER)
- Get event details (ASSOCIATION_MANAGER)
- Manage registrations (ASSOCIATION_MANAGER)
- Event communications (ASSOCIATION_MANAGER)
- Manage programs (ASSOCIATION_MANAGER)
- Payout management (ASSOCIATION_MANAGER)
- Member management (ASSOCIATION_MANAGER)
- Get reports (ASSOCIATION_MANAGER)
- Compliance management (ASSOCIATION_MANAGER)

#### 9. WebChatController
**Purpose:** Direct messaging between users
**Methods:** 8
- Get conversations (authenticated)
- Get messages (authenticated)
- Send message (authenticated)
- Delete message (message owner)
- Mark read (authenticated)
- Mark conversation read (authenticated)
- Block user (authenticated)
- Search messages (authenticated)

#### 10. WebNotificationController
**Purpose:** User notification management
**Methods:** 8
- Get notifications (authenticated)
- Get unread count (authenticated)
- Mark read (authenticated)
- Mark all read (authenticated)
- Delete notification (authenticated)
- Delete all (authenticated)
- Get preferences (authenticated)
- Update preferences (authenticated)

#### 11. WebPaymentController
**Purpose:** Payment processing for TRAVELER
**Methods:** 9
- Create payment (TRAVELER)
- Get payment methods (TRAVELER)
- Save method (TRAVELER)
- Delete method (TRAVELER)
- Edit method (TRAVELER)
- Get history (TRAVELER)
- Download receipt (TRAVELER)
- Request refund (TRAVELER)
- View failed (TRAVELER)

#### 12. WebEventController
**Purpose:** Event management and registration
**Methods:** 7
- Browse events (all)
- Get details (all)
- Register event (TRAVELER)
- View my events (TRAVELER)
- Event analytics (TRAVELER)
- Event cancellation (ASSOCIATION_MANAGER)
- Share event (all)

#### 13. WebAnalyticsController
**Purpose:** Role-specific analytics
**Methods:** 5
- Get personal dashboard (role-specific)
- Export data (authenticated)
- View metrics (authenticated)
- Generate custom reports (authenticated)
- Scheduled reports (authenticated)

#### 14. WebSearchController
**Purpose:** Cross-platform search
**Methods:** 6
- Global search (all)
- Search accommodations (all)
- Search reels (all)
- Search events (all)
- Search users (all)
- Advanced filters (all)

#### 15. WebGlobalizationController ✅ NEW
**Purpose:** Multi-language and multi-currency support
**Methods:** 8
- Get supported languages (all)
- Get supported currencies (all)
- Set language preference (authenticated)
- Set currency preference (authenticated)
- Get translation keys (all)
- Get exchange rates (all)
- Get country information (all)
- Convert currency (all)

---

## 📊 QUICK STATISTICS

| Metric | Count |
|--------|-------|
| **Total Controllers** | 32 |
| **Admin Controllers** | 14 |
| **Website Controllers** | 18 |
| **Total Methods** | 200+ |
| **Admin Methods** | 80+ |
| **Website Methods** | 120+ |
| **Authorization Coverage** | 100% |
| **New Controllers** | 3 |
| **Design Patterns** | 10 |

---

## 🎯 IMPLEMENTATION GUIDE FOR AI AGENT

### What the AI Agent Needs to Know

**1. No Inheritance Pattern**
- Each controller stands alone
- No parent classes, no base controllers
- Each method is self-contained
- Copy-paste specific patterns to multiple places if needed
- This is INTENTIONAL for clarity

**2. Authorization is Explicit**
- Every public method MUST have @Authorized
- No authorization = security hole
- Check: Do all public methods have @Authorized?

**3. Business Logic is in Service Layer**
- Controllers: Just validate and call service
- Services: Implement the actual logic
- Repositories: Just database operations

**4. Soft Delete is Standard**
- Never hard delete
- Always soft delete (mark deleted, keep data)
- Add deleted, deletedAt, deletedBy, deletionReason fields
- Filter deleted = false in all queries

**5. Error Handling is Consistent**
- Always return standardized response format
- Always use correct HTTP status codes
- Always log errors for audit trail

**6. Pagination is Required**
- Don't return all results
- Always paginate (except single resource fetches)
- Default page size: 20
- Include pagination metadata in response

**7. Audit Logging is Critical**
- Log all admin actions
- Log all sensitive operations
- Log WHO, WHAT, WHEN, WHY
- Use timestamps
- Store in audit table

---

## ✅ IMPLEMENTATION CHECKLIST FOR AI AGENT

Before submitting code, verify:

```
CONTROLLER SETUP:
☐ Class created without extends keyword
☐ Class decorated with @Path and @RestController
☐ All public methods have @Authorized annotation
☐ Proper HTTP methods (@GET, @POST, @PUT, @DELETE)
☐ All endpoints documented in method comments

AUTHORIZATION:
☐ @Authorized(roles = "...") on every endpoint
☐ requireOwner = true where needed
☐ Admin endpoints check for SUPER_ADMIN only
☐ User endpoints check for specific role + ownership

BUSINESS LOGIC:
☐ Controllers call services, not repositories
☐ Services implement business logic
☐ Repositories only do CRUD operations
☐ No direct database queries in controllers

ERROR HANDLING:
☐ Try-catch blocks where needed
☐ Proper HTTP status codes returned
☐ Error messages are clear
☐ Exceptions logged for audit

PAGINATION:
☐ List endpoints have page, size parameters
☐ Pagination metadata in response
☐ Default page size = 20

SOFT DELETE:
☐ All entities have deleted, deletedAt, deletedBy, deletionReason
☐ All queries filter deleted = false
☐ Delete operations mark deleted, not hard delete

LOGGING:
☐ Admin actions logged
☐ Sensitive operations logged
☐ WHO, WHAT, WHEN logged
☐ Audit trail preserved

TESTING:
☐ Each method tested independently
☐ Authorization rules tested
☐ Error cases tested
☐ Happy path tested
```

---

## 🚀 FINAL NOTES FOR AI AGENT

**This is your complete specification. No ambiguity. No guesswork needed.**

Each controller is **completely independent** - implement one without worrying about others.

Each method is **self-contained** - read the method spec, implement exactly as specified.

Authorization is **explicit** - every method clearly states who can call it.

Business logic is **clear** - step-by-step explanation in each method spec.

Design patterns are **explained** - understand WHY things are designed this way.

You have **everything needed** for production-ready implementation.

---

**Your complete RBAC Architecture Specifications v3.0 are ready for AI agent implementation! 🚀**
