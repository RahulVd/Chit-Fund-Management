-- Round 1 migration: rename owner_balance columns to chit_group_balance
--
-- The "owner balance" was a misleading name. It is the group's accumulated
-- dividend pool (built from auction discounts, split equally at settlement).
-- This rename aligns the DB column names with their actual meaning.
--
-- RUN ORDER: run this against Supabase BEFORE restarting the backend with the
-- renamed Java entities. Hibernate's ddl-auto=update cannot rename columns --
-- it would create empty new columns instead, silently zeroing out the balance.
--
-- This migration is idempotent-friendly: if you re-run it, the RENAME will
-- error because the column already has the new name. Check first.

-- Rename on chit_group table
ALTER TABLE chit_group
    RENAME COLUMN owner_balance TO chit_group_balance;

-- Rename on auctions table (the snapshot of the balance after each auction)
ALTER TABLE auctions
    RENAME COLUMN owner_balance_after TO chit_group_balance_after;
