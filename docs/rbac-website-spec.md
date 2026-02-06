# TravelBooking Quarkus - Website Controller Specifications

Access: role-specific for TRAVELER, SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER. Public access only where noted (auth entrypoints, public searches/views).

## Design Principles
- Rich, detailed responses with related context.
- Full feature set (not mobile-limited).
- Support complex queries and filtering.
- Dashboards and analytics for suppliers/managers.
- Larger pagination (20-50 items).

## Target Controllers
- WebAuthController
- WebAccommodationController
- WebBookingController
- WebReelController
- WebReviewController
- WebUserController
- WebChatController
- WebNotificationController
- WebPaymentController
- WebEventController
- WebSupplierController
- WebAssociationController
- WebAnalyticsController
- WebSearchController
- WebGlobalizationController

## Controller Specifications

### WebAuthController (path base: `/api/v1/website/auth`)
- Register account (public): role selection; email uniqueness/format; password strength (8+ with upper/lower/number); first/last name; optional phone/company/registration (supplier) or association name; terms/privacy required; send verification; create user ACTIVE; audit log; return JWT + profile/completeness%.
- Login (public): email/password; check status (not disabled/suspended/banned); 2FA prompt if enabled; JWT (24h or 30d with rememberMe); update last login; log event; return token, role, permissions, last login.
- Social OAuth (Google/Apple/Microsoft): accept provider token; auto-create (TRAVELER) if missing; link existing; return JWT.
- Two-Factor Authentication: enable/setup after login; verify code during login; manage backup codes.
- Forgot/Reset password: send reset link (24h); reset with token; validate password; invalidate token; send confirmation.
- Verify email: token from email; mark verified.
- Change email: require current password; send verification to new email; confirm then update.
- Session management: logout, clear cookies/tokens; view active sessions; logout other devices; manage session timeout.

### WebAccommodationController (path base: `/api/v1/website/accommodations`)
- Advanced search (public): location (city/country/region/coords), checkIn/checkOut, guests, type, amenities multi-select, price range, rating, verifiedOwnerOnly, instantBookOnly, sortBy (relevance/price_asc/price_desc/rating/newest/most_booked), pagination (20-50); returns rich cards with map location, 3-5 images, base price, avg rating, review count, owner info, availability calendar, instant booking flag.
- Traveler reads: detailed accommodation view with description, rules, times, amenities (detailed), room/bed breakdown, full gallery, cancellation policy, host info (name/avatar/response time/acceptance/verification/reviews), similar accommodations, reviews with filters, pricing breakdown (base/taxes/fees), 6-month availability calendar, Q&A section.
- Supplier CRUD (auth SUPPLIER_SUBSCRIBER, `requireOwner=true`): create listing (rich text, location, type, rooms/beds, capacity, amenities, rules, times, cancellation policy, base + seasonal pricing, photos) with PENDING_APPROVAL; view my accommodations with filters/status; get/edit details; update fields (type change may need admin), auto-approve minor, re-approve major; soft delete; photo management (upload/delete/reorder/set featured); pricing management (base, seasonal ranges, discounts, early/last-minute deals, history); analytics (availability calendar, views, bookings, cancellations, revenue, occupancy, rating distribution, trending, comparisons, export); Q&A manage (answer/edit/delete/toggle visibility).
- Upload verification documents: submit and manage property verification artifacts; track review status; required for approval when flagged.

### WebBookingController (13 methods)
- Traveler: create booking (availability/price breakdown, cancellation policy, PENDING_PAYMENT), view bookings with status/date filters and pagination, detailed booking view with host contact/history/actions, modify booking (dates/guests/add-ons, reprice, host approval, notify), cancel booking (show refund, process refund, notify), write review after completion (rating/title/comment/tags/photos; anonymous option; publish or private).
- Supplier: view bookings per accommodation with filters, supplier detailed view (traveler profile/past bookings, accommodation, payment status, requests, communication history), confirm booking (optional personalized message, send check-in), reject booking with reason (full refund), request modification (suggest dates, traveler accept/decline), pending bookings queue, booking analytics (occupancy, trends, cancellation rate, avg length, peak periods, guest demographics, seasonal patterns).

### WebReelController (10 methods)
- View reel feed (approved, algorithm ordering, infinite scroll; show ID, creator name/avatar, title, description, thumbnail, duration, like/comment/view counts, liked flag).
- Upload reel (larger files, title/description, location/tags, thumbnail selection auto/custom, category, visibility public/private/friends; status PENDING_APPROVAL; notify user).
- Manage my reels: list with status filters, pagination, engagement stats, edit/delete options.
- Get reel details: full metadata, video, creator profile, comments with pagination, like/comment/share, similar recommendations, view count and engagement rate.
- Edit reel: update title/description/tags/thumbnail (not video file).
- Delete reel: soft delete; hide from feed; preserve history.
- Like/unlike reel: toggle like; update count; return confirmation.
- Comment on reel: add comment; return comment with ID/timestamp.
- Delete comment: owner verification; return confirmation.
- Reel analytics: views, likes, comments, shares; sources (feed/search/direct); viewer demographics; engagement rate; traffic over time; charts; comparisons.

### WebReviewController (8 methods)
- View reviews: filters (rating/date), sort (helpful/recent/rating), pagination 20; return reviewer name/avatar, rating, title, comment, helpful count, date, response status.
- Write review (TRAVELER, completed booking): rating, title, detailed comment, photo upload (up to 5), tags (cleanliness, communication, check-in, accuracy, value), anonymous option; submit; return confirmation.
- Edit review: author verification; update rating/title/comment/tags; return confirmation; show edit history.
- Delete review: soft delete; optional reason; return confirmation.
- Respond to review (SUPPLIER_SUBSCRIBER): response message; public; notify reviewer; return confirmation.
- Mark review helpful: increment helpful; prevent duplicates; return updated count.
- Flag review: report reason (inappropriate/fake/spam/etc.), optional message; admin notified; return confirmation.
- Review analytics: average rating over time, distribution chart, most helpful, trends, common tags/comments, comparison to similar properties.

### WebUserController (12 methods)
- Get profile: full profile info; avatar, name, email, phone, bio, location, member since, verification badges, stats (bookings/reviews/reels), social links.
- Edit profile: update name/email/phone/bio/location/language/currency; update profile picture; privacy settings; return confirmation + updated profile.
- Upload profile picture: image upload/crop; save to storage; update everywhere; return URL.
- Delete account (GDPR): confirm password; acknowledge deletion; soft delete/anonymize per policy; immediate logout; return confirmation.
- Get account settings: security/preferences; 2FA status; active sessions/devices; email/push settings; language; currency.
- Update security settings: password change; enable/disable 2FA; manage backup codes; view login history; manage sessions/devices.
- Manage payment methods (TRAVELER): list/add/set default/delete/edit label/expiry.
- View transaction history: filters (date, status), pagination; return transaction ID, booking reference, date, amount, status; download invoice.
- Download invoice: booking-specific payment invoice PDF with booking/accommodation/amount/date/payment method.
- Manage preferences: notification frequency, communication preferences, marketing opt-in/out, data sharing, language/timezone/currency, accessibility.
- Wishlist management: add/remove accommodations; view paginated; share link; multiple wishlists; export.
- Personal statistics: total bookings/spend (traveler), total reviews, total reels, activity timeline, favorite accommodations/destinations.

### WebSupplierController (10 methods)
- Supplier dashboard: totals (accommodations/active listings), monthly bookings/revenue/cancellations, average rating, pending actions, recent bookings/reviews, quick stats.
- Supplier analytics: revenue (month/year/all-time), revenue by accommodation, booking trend, occupancy, cancellation rate/reasons, guest satisfaction (average rating/distribution), geographic distribution, seasonal patterns, charts/graphs, export.
- Manage accommodations: list with status, create/edit, pricing management, bulk operations (pause/enable), bulk photo uploads, manage amenities library.
- Manage bookings: view all bookings (filters by status/date/accommodation), confirm/reject, request date modifications, messaging, send check-in instructions, track payments, manage cancellations.
- Manage reviews: view/respond/flag, monitor rating trends, export data.
- Payout management: payout schedule, pending/previous payouts, payout breakdown by accommodation, payout methods (bank/PayPal/etc.), tax documentation, set payout schedule/preferences.
- Verification management: check status; upload/update government ID and proof of address; track expirations; resubmit; view history.
- Communication center: inbox for guest messaging, create message, view threads, search, auto-reply templates, bulk messaging.
- Supplier reports: custom date range; report types (revenue, bookings, guests, cancellations); metrics breakdown; comparisons; export PDF/CSV; schedule automated reports.
- Manage house rules: set quiet hours/pets/smoking/etc., cancellation policy, check-in/out policies, guest requirements, auto-confirm or review toggles.

### WebAssociationController (12 methods)
- Association dashboard: totals (events/active events, members/participants), monthly registrations/revenue, pending actions, recent/upcoming events, member statistics.
- Association analytics: revenue (month/year/all-time), event revenue breakdown, participant growth, event popularity, member retention, geographic distribution, charts/graphs, export.
- Create event: name/description (rich text), location (city/coords/address), date/time/duration, capacity, price, agenda, requirements/restrictions, images, category, visibility, status PENDING_APPROVAL; return confirmation.
- Manage events: view all statuses; edit before start; cancel (notify participants); delete (soft); publish/unpublish; archive completed.
- Get event details: full info; participant list/management; registrations; cancellations/refunds; performance metrics; download participant list.
- Manage registrations: list; approve pending (if manual); reject; refund; send reminders; add notes.
- Event communications: announcements to participants; reminders before; thank you after; templates; view sent history.
- Manage programs: create program/series; associate events; program stats; program discounts.
- Payout management: payout schedule, pending/previous, payout by event, set method/schedule, tax documentation.
- Member management: view members/directory/profiles/history; send communications; export member list.
- Association reports: custom range; report types (events/participants/revenue/members); metrics/breakdowns; export PDF/CSV; schedule automated reports.
- Compliance management: upload insurance/safety docs; health/safety guidelines; incident reporting; participant liability waivers.

### WebChatController (10 methods)
- Get conversations: inbox with pagination 20; sort by most recent; filter unread; search; return ID, other party name/avatar, last message, timestamp, unread count.
- Get conversation messages: conversation ID; pagination 50; return message ID, sender name/avatar, text, timestamp, read status, attachments.
- Send message: recipient or conversation ID; message text; optional attachments; create conversation if needed; return ID/timestamp; notify recipient (email/push).
- Delete message: sender verification; soft delete; return confirmation.
- Mark as read: message ID; update read timestamp; return confirmation.
- Mark conversation as read: mark all messages as read; return confirmation.
- Block user: block communications from user ID; return confirmation.
- Search messages: keyword search; specific conversation or all; date range; pagination; return matches with context.
- Archive conversation: move to archive; still searchable; return confirmation.
- Typing indicator (optional real time): send/receive typing signals; clear when done.

### WebNotificationController (8 methods)
- Get notifications: list all with pagination 20; filter read/unread or type; sort newest; return ID, type, title, message, timestamp, read status, action URL.
- Get unread count: count unread for badge.
- Mark as read: notification ID; update status/timestamp; return confirmation.
- Mark all as read: bulk; return count.
- Delete notification: soft delete; return confirmation.
- Delete all: clear all notifications with confirmation; return confirmation.
- Get notification preferences: retrieve email/push/SMS settings per type, frequency, do-not-disturb window.
- Update notification preferences: enable/disable types, channels, frequency, quiet hours; return confirmation.

### WebPaymentController (9 methods)
- Create payment (traveler): booking ID, calculated amount, choose payment method (saved/new Stripe card); process payment; create record; update booking status; return confirmation or error; send receipt.
- Get payment methods: list saved cards with last4/brand/expiry/default; add new card option; delete per card.
- Save payment method: Stripe token plus card label; optional set default; save securely; return confirmation.
- Delete payment method: ID; unset default if needed; return confirmation.
- Edit payment method: update card label and expiry (if supported); return confirmation.
- Get transaction history: all transactions with filters (date, status: paid/pending/failed/refunded); sort by date; pagination 20; return transaction ID, booking reference, amount, date, status, receipt button.
- Download receipt: transaction ID; generate PDF with booking, accommodation, price breakdown, payment method, receipt number.
- Request refund: transaction ID; reason; full/partial amount; submit request; return confirmation for review.
- View failed payment: show failure reason; retry; change-card options.

### WebEventController (7 methods)
- Event listing/details (public read); create/manage events (association roles) with approvals; manage registrations/approvals/refunds; event communications; payouts; reporting; compliance assets.

### WebAnalyticsController (5 methods)
- Personal dashboards: role-specific overview (traveler bookings/spending/reviews; supplier accommodations/bookings/revenue; association events/members/revenue) with relevant metrics.
- Export data: select data type (bookings, payments, events, etc.), date range, format (CSV/PDF/JSON); download file; include comparison/aggregation as required.

### WebSearchController (6 methods)
- Global search: single box across accommodations, reels, events, users; mixed results with type indicators; autocomplete; recent searches.
- Search accommodations: advanced filters and text search in name/description; return detailed results.
- Search reels: text search in title/description; filter by creator; category filter; return previews.
- Search events: text search with location/date filters; return event info.
- Search users: text search in names; return public profiles.
- Search messages: keyword in message content; search within conversations; return results with context.

### WebGlobalizationController (path base: `/api/v1/website/globalization`)
- Get supported languages (public): `GET /languages`; return code, name, native name, flag.
- Get supported currencies (public): `GET /currencies`; return code, name, symbol, decimal places, exchange rate to base, active status.
- Set user language preference: auth roles; `PUT /language` with languageCode; return updated preference.
- Set user currency preference: auth roles; `PUT /currency` with currencyCode; return updated preference.
- Get translation keys (public): `GET /translations/{languageCode}`; return key/value object.
- Get exchange rates (public): `GET /exchange-rates` with optional baseCurrency (default USD); return rates with timestamp.
- Get country information (public): `GET /countries`; return code, name, flag, timezone, phone prefix, default language/currency.
- Convert currency (public): `GET /convert` with amount/from/to; return original/converted amount, rate used, rate timestamp.
