# TravelBooking Quarkus - Admin Controller Specifications (SUPER_ADMIN)

Access: **SUPER_ADMIN only**. Every endpoint requires `@Authorized(roles = "SUPER_ADMIN")`. No other roles allowed. No inheritance; each controller and method is standalone with full business rules and audit logging.

## Target Controllers (14)
- AdminUserController
- AdminCurrencyController
- AdminFeeController
- AdminReelController
- AdminPaymentController
- AdminDashboardController
- AdminReportController
- AdminAccountController
- AdminAccommodationController
- AdminBookingController
- AdminReviewController
- AdminEventController
- AdminSupplierToolsController ✅ NEW
- AdminTrustController ✅ NEW

## Controllers and Operations

### AdminUserController (8 methods)
- View all users with filters (role, status, registration date range) + pagination; return ID, email, role, status, registration date, profile info.
- Create user manually: email, password (or temp), role, first/last; optional phone, company (supplier), association name; send welcome email.
- View user details: full profile, status/dates, booking/payment counts, creation date, last login/activity, verification status (suppliers).
- Update user: email, name, phone, profile fields, role change, verification status.
- Disable account: soft disable with reason; pause active bookings/payments; reversible.
- Suspend account: duration (e.g., 7/30 days); auto re-enable after expiration; log reason.
- Ban permanently: block login; require reason; soft delete visibility; reversible with audit trail.
- Restore user account: re-enable/suspend lift/ban lift with audit entry.

### AdminCurrencyController (5 methods)
- Get all currencies: code, name, rate to base, last update, active flag.
- Create currency: code, name, rate to base (USD), decimal places, symbol, active flag.
- Update exchange rate: adjust rate, update timestamp, keep history; support bulk update via external API.
- Disable currency: mark inactive; existing bookings valid; new bookings blocked.
- Set base currency: choose platform default (typically USD) for conversions.

### AdminFeeController (7 methods)
- Get current fee structure: booking fee, supplier commission, association commission, payment processing, cancellation rules, listing fee; descriptions/effective dates.
- Update booking fee: fixed or percentage; description; effective date (schedulable).
- Update supplier commission: split percentages; future bookings; show supplier impact.
- Update association commission: event revenue split for managers vs platform; future events.
- Update payment processing fee: percent or fixed; per payment method differences.
- Get fee history: all changes with who/when and revenue impact.
- Create fee version (snapshot): effective from/to; retain old versions for historical bookings.

### AdminReelController (7 methods)
- View pending reels: status PENDING_APPROVAL; creator info, upload date, duration, thumbnail, title; sorted by submission; paginated.
- Approve reel: move to APPROVED; visible in feeds; notify creator; log admin/date.
- Reject reel: mark REJECTED; notify creator with reason; log reason; allow resubmit/appeal.
- Delete reel: soft or hard delete; remove/hide engagement; notify creator; log reason; can be temporary.
- View reel engagement stats: views, likes, comments, shares, saves, trends, engaged users, geo data (if available).
- Manage reel categories: create/update/delete categories; assign reels; track trending performance.
- Flag/report handling: moderation queue with reports, reasons, counts, action history; auto-hide after threshold.

### AdminPaymentController (8 methods)
- View all payments: booking reference, traveler, supplier, amount, status (PENDING/SUCCESS/FAILED/REFUNDED/DISPUTED), method, timestamp; paginated filters.
- Filter payments by status/date/supplier: support each status, date ranges, supplier earnings with counts/aggregates.
- Process refund: manual full/partial refund for successful payments; require reason; call processor; update status to REFUNDED; notify traveler/supplier; log admin/reason.
- View payment statistics: totals for period, average amount, transaction counts, success/failure/refund rates, revenue by supplier/accommodation type, trends.
- View Stripe webhook logs: events, confirmation status, intent tracking, failed attempts/errors.
- Dispute/chargeback management: view disputes, reason codes, evidence status, resolution status, re-dispute actions.
- Filter payments by supplier: earnings and top-performing accommodations.
- Filter payments by date range: reconciliation and aggregates.

### AdminAccommodationController (8 methods)
- View all accommodations: status filters (PENDING_APPROVAL/APPROVED/REJECTED/INACTIVE), supplier/owner, location, type; show owner info, location, room count, base price, approval status/dates.
- Approve accommodation: move PENDING_APPROVAL → APPROVED; searchable/bookable; notify supplier; optional notes; log admin/date.
- Reject accommodation: mark REJECTED; hide from search/booking; notify supplier with reason; allow resubmit; store reason.
- Inspect accommodation details: full info including description, amenities, rules, photo quality, pricing, cancellation policy, owner background.
- View accommodation reviews: all reviews; ratings/comments; identify complaints/quality issues.
- View accommodation analytics: bookings, cancellations, revenue, occupancy, average rating, guest satisfaction trends, trending/declining.
- Suspend accommodation: mark inactive/suspended; no new bookings; existing honored; require reason; notify supplier; allow re-activate.
- Delete accommodation: soft delete; hide from listings; supplier cannot modify; preserve historical bookings/reviews; log reason.

### AdminReportController (7 methods)
- Platform overview dashboard: total users, accommodations (approved/pending/rejected), bookings (current month/all-time), revenue (current month/all-time), active listings/travelers, new registrations.
- Revenue report: platform revenue for period; by accommodation type/location; trend over time; average transaction; revenue per accommodation; commission vs supplier earnings; refund impact.
- User growth report: new users by role; retention; active regions; demographics (if available); MoM growth; churn rate.
- Supplier performance report: top earners; booking count; cancellation rate; average rating; reputation; active vs inactive suppliers.
- Booking analysis report: totals; success rate; average value; popular accommodations; cancellation reasons/rates; peak periods; seasonal trends.
- Accommodation performance report: most booked; lowest performing; occupancy; average rating; new accommodations this period.
- Custom report generator: date range; metrics; filter by region/location/supplier; export PDF/CSV; schedule recurring.

### AdminBookingController (6 methods)
- View all bookings: filters by status/date/accommodation/supplier; pagination and sorting.
- Get booking details: full booking info, traveler/supplier info, payment status, history.
- Modify booking (admin override): change dates/guests/status; recalc pricing; log change.
- Cancel booking (admin override): enforce or override policy; compute refund; process refund; notify parties; log reason/admin.
- Force confirm booking: set status to CONFIRMED; send confirmations/check-in instructions.
- View booking analytics: occupancy, cancellation rate, revenue, trends, peak periods.

### AdminReviewController (6 methods)
- View all reviews: filters (status/rating/date/accommodation/user); pagination.
- Get review details: full content, attachments, author, target accommodation, rating, flags.
- Flag/remove review: soft delete or hide; log reason; notify author if needed.
- Restore review: undo removal; log admin.
- View review reports/flags: list reports with reasons/actions.
- Update review verification status: mark verified/unverified; adjust accommodation rating if needed.

### AdminEventController (5 methods)
- View all events: filters by status/date/location/organizer; pagination.
- Approve event: move to APPROVED; notify organizer; log admin/date.
- Reject event: mark REJECTED; notify with reason; allow resubmit; log reason.
- Cancel event (admin): notify participants; process refunds per policy; log reason.
- View event analytics: registrations, revenue, attendance, cancellations.

### AdminSupplierToolsController ✅ NEW (8 methods)
- View supplier verification queue: pending/verifying suppliers; documents/status.
- Approve supplier verification: mark verified; log admin; notify supplier.
- Reject supplier verification: record reason; notify supplier; allow resubmit.
- Manage supplier payout accounts: view/update payout destinations; verify linkage.
- Process manual payout: initiate payout to supplier; record reason/reference; log admin.
- Suspend supplier account: temporary suspension with reason/duration; notify supplier.
- Remove supplier: soft remove from platform; preserve history; log reason.
- View supplier performance issues: highlight poor metrics/complaints for action.

### AdminTrustController ✅ NEW (10 methods)
- View fraud detection dashboard: summary of alerts, risk scores, recent flags.
- View suspicious user activity: failed payments, rapid changes, location anomalies.
- View suspicious bookings: high-risk patterns; flag for review.
- Block user account (fraud): immediate block; log reason; notify security channel.
- Flag suspicious payment: mark payment for review; freeze related booking if needed.
- Manage identity verification requests: queue of IDs/POA documents.
- Approve identity verification: mark verified; log admin; notify user.
- Reject identity verification: record reason; notify user; allow resubmit.
- Monitor payment disputes: list disputes/chargebacks; status and next actions.
- Create trust alert: log and broadcast security alert for follow-up.

### AdminAccountController (8 methods)
- View suspended accounts: reason, suspension end date, original date/admin; auto-restore when expired.
- View banned accounts: reason, ban date/admin; allow manual unban with reason.
- Monitor suspicious activity: unusual booking patterns, failed payments, chargebacks, rapid changes, location anomalies; flag for review.
- Manual account unlock: unlock locked accounts; reset failed login counter; send reset email.
- Manage two-factor authentication: view users with 2FA; force 2FA; disable if compromised; view backup code status.
- Force 2FA for user: enforce enrollment for high-risk users; notify.
- Email verification management: view unverified emails; resend; mark verified manually; block operations for unverified.
- Phone verification management: view unverified phones; resend SMS/call; mark verified manually.

### AdminDashboardController (4 methods)
- Get dashboard summary: key metrics, pending actions, recent payments/transactions, latest registrations, pending approvals, system health.
- Get pending approvals queue: counts for accommodation approvals, reel approvals, supplier verification, user reports.
- Get quick stats: today's bookings, today's revenue, this month's revenue, user count by role.
- Get system notifications: system errors/warnings, security alerts, payment processing issues, high-value bookings, dispute alerts.
