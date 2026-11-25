-- hermes-credit · queries to inspect the credit database

-- 1. The table exists (Flyway V1)
SHOW TABLES;

-- 2. All analyses, newest first
SELECT * FROM analyses ORDER BY id DESC;

-- 3. One analysis with its decision (change the id)
SELECT id, farmer, amount, score, credit_limit, decision, reason
FROM analyses
WHERE id = 1;

-- 4. Analyses still waiting for a decision
SELECT id, farmer, score FROM analyses WHERE decision IS NULL;

-- 5. How many analyses per decision
SELECT decision, COUNT(*) FROM analyses GROUP BY decision;