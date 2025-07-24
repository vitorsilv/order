CREATE TABLE IF NOT EXISTS insurance_policy (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    product_id UUID NOT NULL,
    category VARCHAR(20) NOT NULL,
    sales_channel VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    status VARCHAR(20),
    total_monthly_premium_amount NUMERIC(15, 2),
    insured_amount NUMERIC(15, 2),
    coverages JSONB,
    assistances JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS insurance_policy_history (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(20) NOT NULL, -- Mesmo status da policy
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    insurance_policy_id UUID NOT NULL REFERENCES insurance_policy(id) ON DELETE CASCADE
);
-- Índice para consultas frequentes por cliente
CREATE INDEX IF NOT EXISTS idx_insurance_policy_customer ON insurance_policy(customer_id);

-- Índice para histórico de políticas
CREATE INDEX IF NOT EXISTS idx_policy_history ON insurance_policy_history(insurance_policy_id);

-- Índice GIN para consultas em campos JSONB
CREATE INDEX IF NOT EXISTS idx_coverages_gin ON insurance_policy USING GIN (coverages);
CREATE INDEX IF NOT EXISTS idx_assistances_gin ON insurance_policy USING GIN (assistances);