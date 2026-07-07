ALTER TABLE "user".user_profiles
ADD COLUMN civic_score integer NOT NULL DEFAULT 0;

ALTER TABLE report.reports
ADD COLUMN assigned_to_user_id bigint,
ADD COLUMN resolution_image_url character varying(500);
