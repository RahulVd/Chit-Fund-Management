-- Clean slate: wipe all transactional rows to start fresh with correct money math.
-- RUN THIS ON SUPABASE SQL EDITOR.
--
-- This clears the per-group data but keeps the chit_group definitions themselves
-- intact (so you can reuse them). It resets chitGroupBalance to 0 and status
-- back to ACTIVE so a group behaves like a brand-new one.
--
-- Order matters: child tables first (auctions, owner_months, payments,
-- owner_payments, settlements), then reset the group's balance/status.
--
-- This is a one-time cleanup script for stale test data that was recorded
-- against the buggy owner-month logic (which wrongly added the pot to the
-- dividend pool). After this runs, re-record auctions/owner-months/payments
-- from scratch -- they will now compute correctly.

BEGIN;

DELETE FROM settlements;
DELETE FROM auctions;
DELETE FROM owner_months;
DELETE FROM owner_payments;
DELETE FROM payments;

-- Reset every group's dividend pool to zero and reopen completed groups,
-- since their recorded history is now gone.
UPDATE chit_group
   SET chit_group_balance = 0,
       status = 'ACTIVE';

-- Optional: also wipe members if you want to recreate them.
-- Uncomment the next line ONLY if you want members gone too.
-- DELETE FROM members;

COMMIT;

-- Verify it worked:
-- SELECT id, chit_name, chit_group_balance, status FROM chit_group;
