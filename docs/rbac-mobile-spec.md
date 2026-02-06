# TravelBooking Quarkus - Mobile Controller Specifications

Access: role-specific for TRAVELER, SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER. Public access only where noted (auth entrypoints, public views).

## Design Principles
- Lightweight responses, minimal nesting.
- Fast responses optimized for mobile bandwidth.
- Paginated results with small page sizes (10-20).
- Clear, concise error messages.

## Target Controllers
- MobileAuthController
- MobileAccommodationController
- MobileBookingController
- MobileReelController
- MobileReviewController
- MobileUserController
- MobileChatController
- MobileNotificationController
- MobilePaymentController
- MobileEventController
- MobileSearchController

## Controller Specifications

### MobileAuthController
- Register: select role (TRAVELER, SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER); email/password/first/last required; optional phone/company (supplier); return JWT + basic info; send verification email.
- Login: email/password; validate status (not disabled/suspended/banned); return JWT with expiration; update last login; return basic profile.
- OAuth Google/Apple/Microsoft: accept provider token; auto-create (TRAVELER) if missing; return JWT; link account.
- Refresh token: validate near-expiry token; issue new token.
- Logout: invalidate token; optionally clear push tokens; update last activity.
- Forgot password: email input; send reset link (24h token); return success message.
- Verify email: token from email; mark verified.

### MobileAccommodationController
- Search accommodations: location, dates, guests, price range, type, amenities, min rating; pagination 10-20; returns ID, name, location, image URL, base price, rating, review count.
- Get accommodation details: name, description, type, location (lat/lon), owner info (name, rating, response time), amenities, rules, cancellation policy, times, pricing, gallery, address/map, review count/avg.
- Get accommodation images: list of image URLs with captions; order by upload or specified order; high/low-res options.
- Check availability: by accommodation + dates; returns available flag, price for dates, discounts, blackout/restrictions.
- Get nearby accommodations: by lat/lon + radius (default 10km); sorted by distance; limited 5-10; include distance.
- Get trending accommodations: most booked, highest rated, most viewed, new listings (last 7 days); limited list (10).

### MobileBookingController
**Traveler flows**
- Create booking: accommodation ID, check-in/out, guests, optional requests; calculate total with fees; create PENDING booking; return confirmation and next step to payment; send email.
- View my bookings: filter by status (PENDING, CONFIRMED, COMPLETED, CANCELLED); pagination 20; sort by check-in; return booking ID, accommodation name, dates, status, amount, thumbnail.
- Get booking details: verify traveler owns; include accommodation details, supplier info, dates, price breakdown, payment status, cancellation policy, rules, supplier contact, check-in instructions.
- Cancel booking: booking ID + reason; enforce cancellation policy; compute refund; initiate refund; set status CANCELLED; notify supplier; return refund amount/timeline.

**Supplier flows**
- View accommodation bookings: filter by accommodation (owned) and status (PENDING_CONFIRMATION, CONFIRMED, COMPLETED, CANCELLED); pagination 20; sort by check-in; return booking ID, traveler name, dates, status, amount.
- Confirm booking (detailed): `PUT /api/v1/mobile/bookings/{bookingId}/confirm` with optional message; verify ownership and status PENDING_CONFIRMATION; set CONFIRMED, timestamp; notify traveler with check-in details/instructions; return confirmation.
- Reject booking (detailed): `PUT /api/v1/mobile/bookings/{bookingId}/reject` with required reason; verify ownership and status PENDING_CONFIRMATION; set REJECTED; process full refund immediately; notify traveler with reason; return confirmation.
- Get accommodation bookings (detailed): `GET /api/v1/mobile/bookings/accommodation/{accommodationId}` with optional status/page/size; verify ownership; fetch bookings sorted by check-in; paginate lightweight list (ID, traveler, check-in, status, amount).

### MobileReelController
- Path base: `/api/v1/mobile/reels`
- Get reel feed: public; `GET /feed` with page/size; fetch approved reels ordered by engagement/recent; paginate; include creator name/avatar, title, thumbnail, duration, like/comment counts, `is_liked_by_current_user` when authenticated.
- Get trending reels: public; `GET /trending` with page/size; highest engagement score (last 7 days); limited results.
- Upload reel: auth roles TRAVELER/SUPPLIER_SUBSCRIBER/ASSOCIATION_MANAGER; multipart video (<100MB mp4/mov), title, optional description/location/tags; upload to storage; create reel PENDING_APPROVAL with creator ID/timestamp; auto-thumbnail; notify admin and user; return reel ID/status and approval ETA.
- Like reel: auth roles above; `POST /{reelId}/like`; toggle like; ensure APPROVED/not deleted; create/delete like record; adjust like count; return updated count.
- Delete reel: auth roles above with `requireOwner=true`; `DELETE /{reelId}`; verify creator; soft delete; remove from feeds/search; preserve history; return confirmation.
- Get reel comments: public; `GET /{reelId}/comments` with page/size (default 20); newest first; exclude deleted; return ID, commenter name/avatar, text, timestamp, like count.
- Add comment: auth roles above; `POST /{reelId}/comments` with `content` (max 500); verify reel exists/not deleted; create comment with commenter ID/timestamp; increment reel comment count; notify creator; return comment with ID/timestamp.
- Get reel stats: public; `GET /{reelId}/stats`; return views, likes, comments, shares, engagement rate.

### MobileReviewController
- Path base: `/api/v1/mobile/reviews`
- Get accommodation reviews: public; `GET /accommodation/{accommodationId}` with page/size (default 10) and sortBy (RECENT/HELPFUL/RATING_HIGH/RATING_LOW); return reviewer name, rating, title, comment preview, date, helpful count, verified booking indicator.
- Get reviews by rating: public; `GET /by-rating/{rating}` (1-5) with optional accommodationId and page/size; return filtered paginated list.
- Submit review: auth TRAVELER with `requireOwner=true`; `POST /` with bookingId, accommodationId, rating, title, comment (min 20 chars), optional photos (max 3 URLs); booking must be COMPLETED and owned; ensure not already reviewed; create verified review; store photos; recalc accommodation rating; notify owner; return review ID/timestamp/status.
- Delete review: auth TRAVELER owner; `DELETE /{reviewId}`; soft delete; recalc rating; return confirmation.
- Mark review helpful: public (optional auth); `PUT /{reviewId}/helpful` with `helpful` boolean; prevent duplicate votes by IP/user; increment/decrement helpful count; return updated count.

### MobileUserController
- Path base: `/api/v1/mobile/user`
- Get profile: auth roles; `GET /profile`; return name, email, phone, avatar URL, member since, role badge, stats (bookings/reviews/reels).
- Update profile: auth roles; `PUT /profile` with optional firstName, lastName, phoneNumber, bio, location; apply updates and log changes; return updated profile.
- Upload profile picture: auth roles; `POST /profile/picture` multipart image (jpg/png, max 5MB); validate/ upload to storage; delete old picture; update profile; return new URL.
- Change password: auth roles; `POST /change-password` with oldPassword/newPassword (min 8); verify old (BCrypt); validate strength; hash/save; logout other sessions; send security email; return confirmation.
- Get preferences: auth roles; `GET /preferences`; return notification/language/currency/privacy settings.
- Update preferences: auth roles; `PUT /preferences` with notificationsEnabled, language, currencyPreference, privacyPublicProfile; update and return preferences.

### MobileChatController
- Path base: `/api/v1/mobile/chat`
- Get conversations: auth roles; `GET /conversations` with page/size (default 0/10); fetch user conversations sorted by last message; paginate; include other user name/avatar, last message preview, timestamp, unread count.
- Get conversation messages: auth roles; `GET /conversations/{conversationId}/messages` with page/size (default 0/20); verify participation; fetch non-deleted messages oldest-first; return ID, sender name/avatar, text, timestamp, read status, attachments.
- Send message: auth roles; `POST /messages` with conversationId, content, optional attachmentUrl; verify participation; create message with sender/time/content; store attachment URL; update conversation last timestamp; notify recipient; return ID/timestamp/delivery status.
- Delete message: auth roles with `requireOwner=true`; `DELETE /messages/{messageId}`; verify sender; soft delete; show as deleted; return confirmation.
- Mark message as read: auth roles; `PUT /messages/{messageId}/read`; update read status/timestamp; return confirmation.
- Get unread count: auth roles; `GET /unread-count`; return total unread and per-conversation counts.
- WebSocket real-time chat (optional): auth roles; `GET /ws` upgrade; maintain live messaging with typing/presence indicators and reconnect handling.

### MobilePaymentController
- Path base: `/api/v1/mobile/payments`
- Process payment: auth TRAVELER; `POST /` with bookingId, stripeTokenId, paymentMethod; verify traveler owns booking; get amount/currency; call Stripe; on success create payment record and set booking to PENDING_CONFIRMATION; send receipt; on failure return error; return payment status and receipt URL if success.
- Get payment methods: auth TRAVELER; `GET /methods`; return saved methods (ID, last4, expiry, brand, is_default).
- Save payment method: auth TRAVELER; `POST /methods` with stripeTokenId, cardName, optional setAsDefault; save with Stripe; create local record; update default if flagged; return saved details without sensitive data.
- Delete payment method: auth TRAVELER; `DELETE /methods/{methodId}`; verify ownership; delete; unset default if needed; return confirmation.
- Get transaction history: auth TRAVELER; `GET /history` with page/size (default 0/10); fetch payments for traveler sorted by date desc; paginate; return ID, booking reference, amount, date, status.

### MobileNotificationController
- Path base: `/api/v1/mobile/notifications`
- Get notifications: auth roles; `GET /` with page/size (default 0/20); fetch user notifications sorted newest; paginate; return ID, type, title, message, timestamp, read status, action URL.
- Get unread notifications: auth roles; `GET /unread` with page/size (default 0/20); fetch unread only; sort newest; paginate; return list.
- Mark as read: auth roles; `PUT /{notificationId}/read`; verify recipient; update read status/timestamp; return confirmation.
- Mark all as read: auth roles; `PUT /mark-all-read`; mark all unread to read; return count.
- Delete notification: auth roles; `DELETE /{notificationId}`; verify recipient; soft delete; return confirmation.
- Register device token: auth roles; `POST /device-token` with token and platform (ANDROID/IOS); associate token with user; replace old token for same device if present; return confirmation.
- Get unread count: auth roles; `GET /count/unread`; return unread count for badge.

### MobileEventController
- Path base: `/api/v1/mobile/events`
- Get events list: public; `GET /` with optional location, startDate, endDate, page/size (default 0/10); fetch approved events; filter; sort by date; paginate; return ID, name, image, location, date, organizer, participant count.
- Get event details: public; `GET /{eventId}`; return full event info (name, description, location, lat/lon, date/time, duration, organizer info, participant list, capacity, price, schedule, requirements).
- Register for event: auth TRAVELER; `POST /{eventId}/register` with participantCount; check capacity; handle payment if paid; add to participants; send confirmation email; return registration confirmation.
- Cancel event registration: auth TRAVELER with ownership; `DELETE /{eventId}/register`; fetch registration; compute refund per policy; process refund; remove participant; return confirmation.
- Create event (ASSOCIATION_MANAGER): event details (name, description, location, date/time/duration, capacity, price, requirements, images, category, visibility); create with PENDING_APPROVAL; return confirmation.
- Get my events (ASSOCIATION_MANAGER): list events created by current user; pagination; return ID, name, date, participant count, status.

### MobileSearchController
- Path base: `/api/v1/mobile/search`
- Global search: public; `GET /` with query and page/size (default 0/10); search accommodations, reels, events, users; return mixed results with type indicators and counts.
- Search accommodations: public; `GET /accommodations` with query, optional filters object, page/size; return filtered results.
- Search reels: public; `GET /reels` with query, page/size; return reel results.
- Search events: public; `GET /events` with query, page/size; return event results.
- Search users: public; `GET /users` with query, page/size; return public profiles only (name, avatar, role, rating if applicable).
