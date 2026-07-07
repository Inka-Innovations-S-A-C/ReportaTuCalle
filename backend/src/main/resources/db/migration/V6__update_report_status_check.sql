-- Actualizar la restricción de estados para permitir ASSIGNED
ALTER TABLE report.reports DROP CONSTRAINT reports_status_check;

ALTER TABLE report.reports ADD CONSTRAINT reports_status_check 
CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'REJECTED'));
