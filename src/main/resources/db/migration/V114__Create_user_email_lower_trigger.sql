-- Создаем функцию для триггера (PostgreSQL)
CREATE OR REPLACE FUNCTION lowercase_email()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.email := LOWER(NEW.email);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS trigger_lowercase_email ON users;

-- Создаем триггер
CREATE TRIGGER trigger_lowercase_email
    BEFORE INSERT OR UPDATE ON users
    FOR EACH ROW
EXECUTE FUNCTION lowercase_email();