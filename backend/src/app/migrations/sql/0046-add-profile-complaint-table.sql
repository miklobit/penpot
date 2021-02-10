CREATE TABLE profile_complaint_report (
  profile_id uuid NOT NULL REFERENCES profile(id) ON DELETE CASCADE,
  created_at timestamptz NOT NULL DEFAULT now(),

  is_expired boolean DEFAULT false,

  type text NOT NULL,
  content jsonb,

  PRIMARY KEY (profile_id, created_at)
);

-- Enables fast index only scans.
CREATE INDEX profile_complaint_report__not_expired__idx
    ON profile_complaint_report (created_at, profile_id)
 WHERE is_expired IS false;

ALTER TABLE profile_complaint_report
  ALTER COLUMN type SET STORAGE external,
  ALTER COLUMN content SET STORAGE external;

ALTER TABLE profile
  ADD COLUMN is_mutted boolean DEFAULT false,
  ADD COLUMN auth_backend text NULL;

ALTER TABLE profile
  ALTER COLUMN auth_backend SET STORAGE external;

UPDATE profile
   SET auth_backend = 'google'
 WHERE password = '!';

UPDATE profile
   SET auth_backend = 'penpot'
 WHERE password != '!';

-- Table storing a permanent complaint table for register all
-- permanent bounces and spam reports (complaints) and avoid sending
-- more emails there.
CREATE TABLE global_complaint_report (
  email text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),

  type text NOT NULL,
  content jsonb,

  PRIMARY KEY (email, created_at)
);

ALTER TABLE global_complaint_report
  ALTER COLUMN type SET STORAGE external,
  ALTER COLUMN content SET STORAGE external;